package com.xjinyao.xcloud.common.security.component;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.xjinyao.xcloud.common.core.constant.CommonConstants;
import com.xjinyao.xcloud.common.security.exception.CustomAuth2Exception;
import lombok.SneakyThrows;

/**
 * @date 2019/2/1
 * <p>
 * OAuth2 异常格式化
 */
public class CustomAuth2ExceptionSerializer extends StdSerializer<CustomAuth2Exception> {

    public CustomAuth2ExceptionSerializer() {
        super(CustomAuth2Exception.class);
    }

    @Override
    @SneakyThrows
    public void serialize(CustomAuth2Exception value, JsonGenerator gen, SerializerProvider provider) {
        gen.writeStartObject();
        gen.writeObjectField("code", CommonConstants.FAIL);
        gen.writeStringField("msg", value.getMessage());
        gen.writeStringField("data", value.getErrorCode());
        gen.writeEndObject();
    }

}
