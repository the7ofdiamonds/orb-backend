package tech.orbfin.api.gateway.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
@UtilityClass
public class JSON {

    public static String searchJsonValue(String json, String key) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(json);

            return findJsonValue(rootNode, key);
        } catch (Exception e) {
            e.printStackTrace();
            // Handle the exception according to your needs
            return null;
        }
    }

    private static String findJsonValue(JsonNode node, String key) {
        if (node.isObject()) {
            JsonNode valueNode = node.get(key);
            if (valueNode != null) {
                return valueNode.asText();
            } else {
                for (JsonNode childNode : node) {
                    String result = findJsonValue(childNode, key);
                    if (result != null) {
                        return result;
                    }
                }
            }
        } else if (node.isArray()) {
            for (JsonNode childNode : node) {
                String result = findJsonValue(childNode, key);
                if (result != null) {
                    return result;
                }
            }
        }

        return null;
    }

    public static boolean isJsonFile(String filePath) throws Exception {
        try {
            // Read file content
            String fileContent = readFileContent(filePath);

            // Attempt to parse JSON
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.readTree(fileContent);

            // If parsing succeeds, the file is in JSON format
            return true;
        } catch (Exception e) {
            // Parsing failed or other exceptions occurred
            throw new Exception(e.getMessage(), e.getCause());
//            return false;
        }
    }

    public static String readFileContent(String filePath) {
        try (InputStream inputStream = new FileInputStream(filePath)) {
            byte[] bytes = inputStream.readAllBytes();
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Failed to read file content", e);
            return "";
        }
    }

    public static void closeInputStream(InputStream inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                log.error("Failed to close InputStream", e);
            }
        }
    }
}
