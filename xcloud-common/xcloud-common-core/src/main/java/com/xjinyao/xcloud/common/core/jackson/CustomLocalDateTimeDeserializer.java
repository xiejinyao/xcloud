package com.xjinyao.xcloud.common.core.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 谢进伟
 * @description 自定义LocalDateTime类型反序列化
 * @createDate 2021/7/7 14:52
 */
public class CustomLocalDateTimeDeserializer extends LocalDateTimeDeserializer {


    private List<DateTimeFormatter> formatterList = new ArrayList<>();

    public CustomLocalDateTimeDeserializer(DateTimeFormatter formatter) {
        super(formatter);
    }

    public void addFormatter(DateTimeFormatter formatter) {
        formatterList.add(formatter);
    }

    protected CustomLocalDateTimeDeserializer(LocalDateTimeDeserializer base, Boolean leniency) {
        super(base, leniency);
    }

    @Override
    public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        try {
            return super.deserialize(parser, context);
        } catch (Exception e) {
            String string = parser.getText().trim();
            for (DateTimeFormatter formatter : formatterList) {
                try {
                    return LocalDateTime.parse(string, formatter);
                } catch (Exception exception) {
                }
            }
            throw new RuntimeException("Text '" + string + "' could not be parsed to LocalDateTime");
        }
    }
}
