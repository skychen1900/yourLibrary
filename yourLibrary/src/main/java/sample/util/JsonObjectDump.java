package sample.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonObjectDump {

    public static String getJsonString(Object parameter) {

        //参照型のみが出力対象とする
        if (!parameter.getClass().isPrimitive() && !(parameter instanceof String)) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            try {
                return mapper.writeValueAsString(parameter);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
