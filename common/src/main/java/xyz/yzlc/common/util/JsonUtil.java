package xyz.yzlc.common.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;

/**
 * jsonUtil<br>
 * <h3>@JsonProperty("")：单独定义字段名称</h3>
 * <h3>@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)</h3>
 * <ul>
 *     <li>SNAKE_CASE：userName -> user_name</li>
 *     <li>UPPER_CAMEL_CASE：userName -> UserName</li>
 *     <li>LOWER_CAMEL_CASE：默认值。userName -> userName</li>
 *     <li>LOWER_CASE：userName -> username</li>
 *     <li>KEBAB_CASE：userName -> user-name</li>
 *     <li>LOWER_DOT_CASE：userName -> user.name</li>
 * </ul>
 */
public class JsonUtil {
    public static final ObjectMapper mapper = new ObjectMapper();

    public static String obj2json(Object obj) throws JsonProcessingException {
        return mapper.writeValueAsString(obj);
    }

    public static <T> T json2pojo(String jsonStr, Class<T> clazz) throws Exception {
        //忽略JSON字符串中存在而Java对象实际没有的属性
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(jsonStr, clazz);
    }

    public static <T> T json2pojo(String jsonStr, TypeReference<T> typeReference)
            throws Exception {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        //忽略JSON字符串中存在而Java对象实际没有的属性
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(jsonStr, typeReference);
    }
}