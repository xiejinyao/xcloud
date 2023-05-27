package com.xjinyao.xcloud.interactive.captcha.core.service.impl;

import com.xjinyao.xcloud.interactive.captcha.core.enums.RepCodeEnum;
import com.xjinyao.xcloud.interactive.captcha.core.service.CaptchaService;
import com.xjinyao.xcloud.interactive.captcha.core.util.ResponseModel;
import com.xjinyao.xcloud.interactive.captcha.core.vo.CaptchaVO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class DefaultCaptchaServiceImpl extends AbstractCaptchaService {

    private static Logger logger = LoggerFactory.getLogger(DefaultCaptchaServiceImpl.class);

    @Override
    public String captchaType() {
        return "default";
    }

    @Override
    public void init(Properties config) {
        super.init(config);
    }

    private CaptchaService getService(String captchaType) {
        return CaptchaServiceFactory.instances.get(captchaType);
    }

    @Override
    public ResponseModel get(CaptchaVO captchaVO) {
        if (captchaVO == null) {
            return RepCodeEnum.NULL_ERROR.parseError("captchaVO");
        }
        if (StringUtils.isEmpty(captchaVO.getCaptchaType())) {
            return RepCodeEnum.NULL_ERROR.parseError("类型");
        }
        return getService(captchaVO.getCaptchaType()).get(captchaVO);
    }

    @Override
    public ResponseModel check(CaptchaVO captchaVO) {
        if (captchaVO == null) {
            return RepCodeEnum.NULL_ERROR.parseError("captchaVO");
        }
        if (StringUtils.isEmpty(captchaVO.getCaptchaType())) {
            return RepCodeEnum.NULL_ERROR.parseError("类型");
        }
        if (StringUtils.isEmpty(captchaVO.getToken())) {
            return RepCodeEnum.NULL_ERROR.parseError("token");
        }
        return getService(captchaVO.getCaptchaType()).check(captchaVO);
    }

    @Override
    public ResponseModel verification(CaptchaVO captchaVO) {
        if (captchaVO == null) {
            return RepCodeEnum.NULL_ERROR.parseError("captchaVO");
        }
        if (StringUtils.isEmpty(captchaVO.getCaptchaVerification())) {
            return RepCodeEnum.NULL_ERROR.parseError("二次校验参数");
        }
        try {
            String codeKey = String.format(REDIS_SECOND_CAPTCHA_KEY, captchaVO.getCaptchaVerification());
            if (!CaptchaServiceFactory.getCache(cacheType).exists(codeKey)) {
                return ResponseModel.errorMsg(RepCodeEnum.API_CAPTCHA_INVALID);
            }
            //二次校验取值后，即刻失效
            CaptchaServiceFactory.getCache(cacheType).delete(codeKey);
        } catch (Exception e) {
            logger.error("验证码坐标解析失败", e);
            return ResponseModel.errorMsg(e.getMessage());
        }
        return ResponseModel.success();
    }

}
