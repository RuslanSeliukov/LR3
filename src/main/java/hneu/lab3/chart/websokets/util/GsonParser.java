package hneu.lab3.chart.websokets.util;

import com.google.gson.Gson;
import hneu.lab3.chart.websokets.models.BasicMassage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import static hneu.lab3.chart.websokets.util.Constants.DUMMY_JSON;

@Service
public class GsonParser<T> implements Parser<T> {

    Logger LOG = LoggerFactory.getLogger(GsonParser.class);

    public T parseToObject(String json, Class<T> clazz) {
        Gson gson = new Gson();
        return gson.fromJson(json, clazz);
    }

    public String parseToJson(T object, Class<T> clazz) {
        return parseToJson(object);
    }

    public String parseToJson(T object) {
        Gson gson = new Gson();
        return gson.toJson(object);
    }

    public T parseJsonFileToObject(Class<T> clazz) {
        if (clazz.equals(BasicMassage.class)) {

            Gson gson = new Gson();
            T parsedObject = null;

            try (Reader reader = new FileReader(new ClassPathResource(DUMMY_JSON).getFile())) {

                parsedObject = gson.fromJson(reader, clazz);

            } catch (IOException e) {
                LOG.error("Exception occurred when reading file: " + new ClassPathResource(DUMMY_JSON).getFilename());
            }
            return parsedObject;
        }
        return null;
    }
}
