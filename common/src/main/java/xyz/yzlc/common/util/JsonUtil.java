package xyz.yzlc.common.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;

public class JsonUtil {
    public static final ObjectMapper mapper = new ObjectMapper();

    public static String obj2json(Object obj) throws JsonProcessingException {
        return mapper.writeValueAsString(obj);
    }

    public static <T> T json2pojo(String jsonStr, Class<T> clazz) throws Exception {

        //驼峰转换
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

        //忽略JSON字符串中存在而Java对象实际没有的属性
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(jsonStr, clazz);
    }

    public static <T> T json2pojo(String jsonStr, TypeReference<T> typeReference)
            throws Exception {

        //驼峰转换
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        //忽略JSON字符串中存在而Java对象实际没有的属性
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(jsonStr, typeReference);
    }
}