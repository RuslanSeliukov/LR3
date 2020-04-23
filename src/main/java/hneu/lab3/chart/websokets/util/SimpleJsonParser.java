package hneu.lab3.chart.websokets.util;

import hneu.lab3.chart.websokets.models.BasicMassage;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import static hneu.lab3.chart.websokets.util.Constants.*;

@Service
public class SimpleJsonParser<T> implements Parser<T> {

    Logger LOG = LoggerFactory.getLogger(SimpleJsonParser.class);

    public T parseToObject(String json, Class<T> clazz) {
        try {
            if (clazz.equals(BasicMassage.class)) {
                Object object = JSONValue.parse(json);
                JSONObject jsonObject = (JSONObject) object;
                T basicMassage = null;

                BasicMassage temp = new BasicMassage();
                temp.setUser((String) jsonObject.get(USER));
                temp.setMessage((String) jsonObject.get(MESSAGE));

                basicMassage = (T) temp;
                return basicMassage;
            }
        } catch (Exception e) {
            LOG.error("Exception occurred while parsing json with SimpleJson parser");
        }
        return null;
    }

    public String parseToJson(T object, Class<T> clazz) {
        try {
            if (clazz.equals(BasicMassage.class)) {
                BasicMassage basicMassage = (BasicMassage) object;
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(USER, basicMassage.getUser());
                jsonObject.put(MESSAGE, basicMassage.getMessage());
                return jsonObject.toJSONString();
            }
        } catch (Exception e) {
            LOG.error("Exception occurred while parsing object with SimpleJson parser");
        }
        return null;
    }

    public T parseJsonFileToObject(Class<T> clazz) {
        if (clazz.equals(BasicMassage.class)) {

            JSONParser jsonParser = new JSONParser();
            T basicMassage = null;

            try (Reader reader = new FileReader(new ClassPathResource(DUMMY_JSON).getFile())) {

                JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);

                BasicMassage temp = new BasicMassage();

                temp.setUser((String) jsonObject.get(USER));
                temp.setMessage((String) jsonObject.get(MESSAGE));

                basicMassage = (T) temp;

            } catch (IOException e) {
                LOG.error("Exception occurred when reading file: " + new ClassPathResource(DUMMY_JSON).getFilename());
            } catch (ParseException e) {
                LOG.error("Exception occurred when parsing file: " + new ClassPathResource(DUMMY_JSON).getFilename());
            }

            return basicMassage;
        }
        return null;
    }
}
