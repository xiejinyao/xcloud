package com.xjinyao.xcloud.auth.interceptor;

import cn.hutool.core.util.BooleanUtil;
import com.xjinyao.xcloud.common.core.redis.constant.RedisTemplateBeanNames;
import com.xjinyao.xcloud.common.core.util.DateUtil;
import com.xjinyao.xcloud.common.security.service.CustomClientDetailsService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @description 登录失败检查
 * @createDate 2020/10/22 14:45
 */
@Slf4j
@Aspect
@Component
public class LoginFailCheckAspect {

    /**
     * 登录用户缓存用户名
     */
    private final static String USERNAME = "username";
    /**
     * 登录失败次检查
     */
    private final static String ENABLE_LOGIN_FAIL_COUNT_CHECK = "enableLoginFailCountCheck";
    /**
     * 登录失败次数
     */
    private final static String LOGIN_FAIL_MAX_COUNT = "loginFailMaxCount";
    /**
     * 登录失败之后，需等待最大毫秒数之后才可继续登录
     */
    private final static String LOGIN_FAIL_MAX_WAIT_MILLISECOND = "loginFailMaxWaitMillisecond";

    /**
     * 登录失败缓存次数的key后缀
     */
    private final static String LOGIN_FAIL_COUNT_CACHE_KEY = "_LOGIN_FAIL_COUNT";
    /**
     * 登录失败首次缓存失败时间的key后缀
     */
    private final static String LOGIN_FAIL_FIRST_DATE_TIME_CACHE_KEY = "_LOGIN_FAIL_FIRST_DATE_TIME";

    /**
     * 登录次数超出最大失败次数后的默认等待时间
     */
    private final static String LOGIN_FAIL_COUNT_EXCEED_DEFAULT_WAIT_VALUE = "3600000";
    /**
     * 登录失败次数默认次数
     */
    private final static String LOGIN_FAIL_MAX_COUNT_VALUE = "6";

    private RedisTemplate redisTemplate;
    private final UserDetailsService userDetailsService;
    private CustomClientDetailsService customClientDetailsService;
    private HttpServletRequest request;

    public LoginFailCheckAspect(@Qualifier(RedisTemplateBeanNames.JSON_REDIS_TEMPLATE) RedisTemplate redisTemplate,
                                UserDetailsService userDetailsService,
                                CustomClientDetailsService customClientDetailsService,
                                HttpServletRequest request) {
        this.redisTemplate = redisTemplate;
        this.userDetailsService = userDetailsService;
        this.customClientDetailsService = customClientDetailsService;
        this.request = request;
    }

    @Pointcut("execution(public * org.springframework.security.oauth2.provider.endpoint.TokenEndpoint.postAccessToken(java.security.Principal,java.util.Map))")
    public void tokenEndpointExecution() {
    }

    @SneakyThrows
    @Around("tokenEndpointExecution()")
    public Object around(ProceedingJoinPoint point) {
        Object proceed;
        String clientId = null;
        Map<String, Object> clientAdditionalInformation = null;
        String username = request.getParameter(USERNAME);
        UserDetails userDetails = null;
        try {
            userDetails = userDetailsService.loadUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            //用户名不存在不需要检查
            return point.proceed();
        }
        if (userDetails != null) {
            Object[] args = point.getArgs();
            if (args != null && args.length > 0) {
                clientId = getClientId((Principal) args[0]);
                ClientDetails authenticatedClient = customClientDetailsService.loadClientByClientId(clientId);
                clientAdditionalInformation = authenticatedClient.getAdditionalInformation();
            }
        }
        try {
            if (clientId != null && clientAdditionalInformation != null) {
                String result = checkUserIsLock(username, clientId, clientAdditionalInformation);
                if (result != null) {
                    throw new InvalidGrantException(result);
                }
            }
            proceed = point.proceed();

            redisTemplate.delete(Arrays.asList(getCacheKey(clientId, username, LOGIN_FAIL_COUNT_CACHE_KEY),
                    getCacheKey(clientId, username, LOGIN_FAIL_FIRST_DATE_TIME_CACHE_KEY)));
        } catch (Throwable throwable) {
            throw exception(throwable, username, clientId, clientAdditionalInformation);
        }
        return proceed;
    }

    public Throwable exception(Throwable ex, String username,
                               String clientId, Map<String, Object> clientAdditionalInformation) {
        if (ex instanceof InvalidGrantException) {
            int residueLoginFailCount = loginFailCountCheck(clientId, clientAdditionalInformation, username);
            if (residueLoginFailCount != -1) {
                return new InvalidGrantException(ex.getMessage() + "(剩余次数：" + residueLoginFailCount + "次)", ex);
            }
        }
        return ex;
    }

    public String checkUserIsLock(String username, String clientId, Map<String, Object> clientAdditionalInformation) {
        Object enableLoginFailCountCheck = clientAdditionalInformation.getOrDefault(ENABLE_LOGIN_FAIL_COUNT_CHECK,
                "false");
        //检查是否启用了登录失败限制次数检查功能
        if (BooleanUtil.toBoolean(enableLoginFailCountCheck.toString())) {
            Integer loginFailMaxCount = Integer.valueOf(clientAdditionalInformation.getOrDefault(LOGIN_FAIL_MAX_COUNT
                    , LOGIN_FAIL_MAX_COUNT_VALUE).toString());
            long loginFailMaxWaitMillisecond = Long.parseLong(clientAdditionalInformation
                    .getOrDefault(LOGIN_FAIL_MAX_WAIT_MILLISECOND, LOGIN_FAIL_COUNT_EXCEED_DEFAULT_WAIT_VALUE).toString());

            String loginFailCountKey = getCacheKey(clientId, username, LOGIN_FAIL_COUNT_CACHE_KEY);
            String loginFailFirstDateTimeKey = getCacheKey(clientId, username, LOGIN_FAIL_FIRST_DATE_TIME_CACHE_KEY);

            ValueOperations opsForValue = redisTemplate.opsForValue();
            Object cacheLoginFailCountObj = opsForValue.get(loginFailCountKey);
            Integer cacheLoginFailCount = cacheLoginFailCountObj != null ?
                    Integer.valueOf(cacheLoginFailCountObj.toString()) : null;

            Object loginFailFirstDateTimeObj = opsForValue.get(loginFailFirstDateTimeKey);
            LocalDateTime loginFailFirstDateTime = loginFailFirstDateTimeObj != null ?
                    LocalDateTime.parse(loginFailFirstDateTimeObj.toString()) : null;

            //失败次数超限判断
            if (cacheLoginFailCount != null && cacheLoginFailCount.compareTo(loginFailMaxCount) >= 0) {
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime waitEndDateTime = loginFailFirstDateTime.plus(loginFailMaxWaitMillisecond, ChronoUnit.MILLIS);
                if (waitEndDateTime.isAfter(now)) {
                    Duration between = Duration.between(now, waitEndDateTime);
                    DateUtil.DiffFormatDuring dif = DateUtil.diffFormatDuring(between.toMillis());
                    if (dif != null) {
                        StringBuffer info = new StringBuffer();
                        if (dif.days > 0) {
                            info.append(dif.days).append("天");
                        }
                        if (dif.hours > 0) {
                            info.append(dif.hours).append("小时");
                        }
                        if (dif.minutes > 0) {
                            info.append(dif.minutes).append("分");
                        }
                        if (dif.seconds > 0) {
                            info.append(dif.seconds).append("秒");
                        }
                        //还未过最大等待时间
                        return "您的账号在该客户端已被暂时锁定，请在" + info + "后再试!";
                    }
                }
            }
        }
        return null;
    }

    /**
     * 登录失败次数检查
     *
     * @param clientId              终端Id
     * @param additionalInformation 终端配置额外星系
     * @param username              登录用户名
     * @return
     */
    private int loginFailCountCheck(String clientId, Map<String, Object> additionalInformation, String username) {
        Object enableLoginFailCountCheck = additionalInformation.getOrDefault(ENABLE_LOGIN_FAIL_COUNT_CHECK, "false");
        //检查是否启用了登录失败限制次数检查功能
        if (BooleanUtil.toBoolean(enableLoginFailCountCheck.toString())) {
            long loginFailMaxWaitMillisecond = Long.parseLong(additionalInformation
                    .getOrDefault(LOGIN_FAIL_MAX_WAIT_MILLISECOND, LOGIN_FAIL_COUNT_EXCEED_DEFAULT_WAIT_VALUE).toString());
            Integer loginFailMaxCount = Integer.valueOf(additionalInformation.getOrDefault(LOGIN_FAIL_MAX_COUNT
                    , LOGIN_FAIL_MAX_COUNT_VALUE).toString());

            String loginFailCountKey = getCacheKey(clientId, username, LOGIN_FAIL_COUNT_CACHE_KEY);
            String loginFailFirstDateTimeKey = getCacheKey(clientId, username, LOGIN_FAIL_FIRST_DATE_TIME_CACHE_KEY);

            ValueOperations opsForValue = redisTemplate.opsForValue();

            if (!redisTemplate.hasKey(loginFailCountKey)) {
                opsForValue.set(loginFailCountKey, 1, loginFailMaxWaitMillisecond, TimeUnit.MILLISECONDS);
            } else {
                opsForValue.increment(loginFailCountKey);
            }
            if (!redisTemplate.hasKey(loginFailFirstDateTimeKey)) {
                opsForValue.set(loginFailFirstDateTimeKey, LocalDateTime.now().toString(), loginFailMaxWaitMillisecond,
                        TimeUnit.MILLISECONDS);
            }
            //计算剩余次数
            int currentLoginFailCount = Integer.parseInt(opsForValue.get(loginFailCountKey).toString());
            int residueLoginFailCount = loginFailMaxCount - currentLoginFailCount;
            return residueLoginFailCount < 0 ? 0 : residueLoginFailCount;
        }
        return -1;
    }

    private String getCacheKey(String clientId, String username, String login_fail_count) {
        return clientId + "_" + username + login_fail_count;
    }

    private String getClientId(Principal principal) {
        Authentication client = (Authentication) principal;
        if (!client.isAuthenticated()) {
            throw new InsufficientAuthenticationException("The client is not authenticated.");
        }
        String clientId = client.getName();
        if (client instanceof OAuth2Authentication) {
            // Might be a client and user combined authentication
            clientId = ((OAuth2Authentication) client).getOAuth2Request().getClientId();
        }
        return clientId;
    }
}
