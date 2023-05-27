package com.xjinyao.xcloud.core.rule.listener;

import com.xjinyao.xcloud.common.core.redis.constant.CacheConstants;
import com.xjinyao.xcloud.common.core.redis.service.RedisService;
import com.xjinyao.xcloud.core.rule.RuleUtil;
import com.xjinyao.xcloud.core.rule.constant.RuleConstant;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import springfox.documentation.annotations.ApiIgnore;

import java.util.*;

/**
 * 请求资源扫描监听器
 *
 * @author 谢进伟
 */
@Slf4j
public class RequestMappingScanListener implements ApplicationListener<ApplicationReadyEvent> {

    private static final AntPathMatcher pathMatch = new AntPathMatcher();
    private final Set<String> ignoreApi = new HashSet<String>();
    private final RedisService redisService;

    /**
     * 构造方法
     *
     * @param redisService 注入redis
     */
    public RequestMappingScanListener(RedisService redisService) {
        this.redisService = redisService;
        this.ignoreApi.add("/error");
        this.ignoreApi.add("/swagger-resources/**");
        this.ignoreApi.add("/v2/api-docs-ext/**");
    }

    /**
     * 默认事件
     *
     * @param event ApplicationReadyEvent
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            ConfigurableApplicationContext applicationContext = event.getApplicationContext();
            Environment env = applicationContext.getEnvironment();
            // 获取微服务模块名称
            String microService = env.getProperty("spring.application.name", "application");
            if (redisService == null || applicationContext.containsBean("resourceServerConfiguration")) {
                log.warn("[{}]忽略接口资源扫描", microService);
                return;
            }

            // 所有接口映射
            RequestMappingHandlerMapping mapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
            // 获取url与类和方法的对应信息
            Map<RequestMappingInfo, HandlerMethod> map = mapping.getHandlerMethods();
            List<Map<String, String>> list = new ArrayList<Map<String, String>>();
            Map<String, String> api = new HashMap();
            for (Map.Entry<RequestMappingInfo, HandlerMethod> m : map.entrySet()) {
                RequestMappingInfo info = m.getKey();
                HandlerMethod method = m.getValue();
                if (method.getMethodAnnotation(ApiIgnore.class) != null) {
                    // 忽略的接口不扫描
                    continue;
                }
                Set<MediaType> mediaTypeSet = info.getProducesCondition().getProducibleMediaTypes();
                for (MethodParameter params : method.getMethodParameters()) {
                    if (params.hasParameterAnnotation(RequestBody.class)) {
                        mediaTypeSet.add(MediaType.APPLICATION_JSON);
                        break;
                    }
                }
                String mediaTypes = StringUtils.join(mediaTypeSet, RuleConstant.COLLECTIONS_DEFAULT_SEPARATOR);
                // 请求类型
                RequestMethodsRequestCondition methodsCondition = info.getMethodsCondition();
                // 类名
                String className = method.getMethod().getDeclaringClass().getName();
                // 方法名
                String methodName = method.getMethod().getName();

                String name = "";
                String notes = "";
                Boolean auth = false;
                String authCode = "";

                ApiOperation apiOperation = method.getMethodAnnotation(ApiOperation.class);
                if (apiOperation != null) {
                    name = apiOperation.value();
                    notes = apiOperation.notes();
                }
                // 判断是否需要权限校验
                PreAuthorize preAuth = method.getMethodAnnotation(PreAuthorize.class);
                if (preAuth != null) {
                    authCode = preAuth.value();
                    auth = true;
                }
                name = (name == null || "".equals(name)) ? methodName : name;
                api.put("name", name);
                api.put("notes", notes);
                api.put("className", className);
                api.put("methodName", methodName);
                api.put("serviceId", microService);
                api.put("contentType", mediaTypes);
                api.put("auth", auth.toString());
                api.put("authCode", authCode);
                // 请求路径,一个接口可能有多个请求路径、多个请求方法，为方便后续定位，这里将每一个请求路径都单独成为一个api
                // TODO 这里会有一种清空：控制器controller上和接口方法上均无映射时，待验证
                PatternsRequestCondition p = info.getPatternsCondition();
                for (String url : p.getPatterns()) {
                    if (isIgnore(url)) {
                        continue;
                    }
                    // 路径映射匹配规则
                    String pattern = RuleUtil.getBasePattern(url);
                    // md5码
                    String md5 = DigestUtils.md5DigestAsHex((microService + url).getBytes());
                    api.put("pattern", pattern);
                    api.put("code", md5);
                    api.put("path", url);
                    // 请求匹配模式
                    methodsCondition.getMethods().forEach(method1 -> {
                        api.put("method", method1.toString());

                        list.add(new HashMap(api));

                        log.debug("api scan: {}", api);
                    });
                }
            }
            // 放入redis缓存
            Map<String, Object> res = new HashMap();
            res.put("serviceId", microService);
            res.put("size", list.size());
            res.put("list", list);
            redisService.hset(CacheConstants.XCLOUD_API_RESOURCE, microService, res, 18000L);
            redisService.sSetAndTime(CacheConstants.XCLOUD_SERVICE_RESOURCE, 18000L, microService);
            log.info("资源扫描结果:serviceId=[{}] size=[{}] redis缓存key=[{}]", microService, list.size(),
                    CacheConstants.XCLOUD_API_RESOURCE);
        } catch (Exception e) {
            log.error("error: {}", e.getMessage());
        }
    }

    /**
     * 是否是忽略的Api
     *
     * @param requestPath 请求地址
     * @return boolean
     */
    private boolean isIgnore(String requestPath) {
        for (String path : ignoreApi) {
            if (pathMatch.match(path, requestPath)) {
                return true;
            }
        }
        return false;
    }
}
