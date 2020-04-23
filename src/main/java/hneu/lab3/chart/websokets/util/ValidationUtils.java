package hneu.lab3.chart.websokets.util;

import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

import static hneu.lab3.chart.websokets.util.Constants.VALIDATION_SCHEMA;

@Service
public class ValidationUtils {

    Logger LOG = LoggerFactory.getLogger(ValidationUtils.class);

    public boolean validateJsonBySchema(String json) {

        boolean isValid;

        try (InputStream inputStream = new ClassPathResource(VALIDATION_SCHEMA).getInputStream()) {
            JSONObject rawSchema = new JSONObject(new JSONTokener(inputStream));
            Schema schema = SchemaLoader.load(rawSchema);
            schema.validate(new JSONObject(json));
            isValid = true;
        } catch (IOException e) {
            LOG.error("Error occurred when reading json schema file");
            isValid = false;
        } catch (ValidationException e) {
            LOG.info("Json is not valid");
            isValid = false;
        } catch (Exception e) {
            LOG.info("Something went wrong while validate json");
            isValid = false;
        }
        return isValid;
    }
}
