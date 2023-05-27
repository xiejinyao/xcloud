package com.xjinyao.xcloud.interactive.captcha.endpoint;

import com.xjinyao.xcloud.interactive.captcha.core.service.CaptchaService;
import com.xjinyao.xcloud.interactive.captcha.core.util.ResponseModel;
import com.xjinyao.xcloud.interactive.captcha.core.vo.CaptchaVO;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.cors.CorsConfiguration;

@RestController
@RequestMapping("/captcha")
@RequiredArgsConstructor
@CrossOrigin(
        originPatterns = CorsConfiguration.ALL,
        allowCredentials = "true",
        allowedHeaders = CorsConfiguration.ALL,
        methods = {
                RequestMethod.POST,
                RequestMethod.OPTIONS
        }
)
public class CaptchaEndpoint {

    @Lazy
    private final CaptchaService captchaService;

    /**
     * 获取交互验证码数据
     *
     * @param captchaVO
     * @return
     */
    @PostMapping("${captcha.get.path:/get}")
    public ResponseModel get(@RequestBody CaptchaVO captchaVO) {
        return captchaService.get(captchaVO);
    }

    /**
     * 核对验证码(前端)
     *
     * @param captchaVO
     * @return
     */
    @PostMapping("${captcha.check.path:/check}")
    public ResponseModel check(@RequestBody CaptchaVO captchaVO) {
        return captchaService.check(captchaVO);
    }
}
