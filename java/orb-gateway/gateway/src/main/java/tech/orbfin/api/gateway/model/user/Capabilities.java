package tech.orbfin.api.gateway.model.user;

import tech.orbfin.api.gateway.repositories.IRepositoryUserDetails;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonGetter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import com.fasterxml.jackson.databind.ObjectMapper;

@Getter
@Setter
@RequiredArgsConstructor
@Data
public class Capabilities {
    private final IRepositoryUserDetails iRepositoryUserDetails;

    private Map<String, Map<String, Boolean>> roles;

    @JsonGetter
    public Map<String, Map<String, Boolean>> getRoles() {
        try {
            Map<String, Map<String, Boolean>> roles = new HashMap<>();
            ObjectMapper objectMapper = new ObjectMapper();

            String jsonCapabilitiesData = "{\n" +
                    "    \"administrator\": {\n" +
                    "        \"name\": \"Administrator\",\n" +
                    "        \"capabilities\": {\n" +
                    "            \"switch_themes\": true,\n" +
                    "            \"edit_themes\": true,\n" +
                    "            \"activate_plugins\": true,\n" +
                    "            \"edit_plugins\": true,\n" +
                    "            \"edit_users\": true,\n" +
                    "            \"edit_files\": true,\n" +
                    "            \"manage_options\": true,\n" +
                    "            \"moderate_comments\": true,\n" +
                    "            \"manage_categories\": true,\n" +
                    "            \"manage_links\": true,\n" +
                    "            \"upload_files\": true,\n" +
                    "            \"import\": true,\n" +
                    "            \"unfiltered_html\": true,\n" +
                    "            \"edit_posts\": true,\n" +
                    "            \"edit_others_posts\": true,\n" +
                    "            \"edit_published_posts\": true,\n" +
                    "            \"publish_posts\": true,\n" +
                    "            \"edit_pages\": true,\n" +
                    "            \"read\": true,\n" +
                    "            \"level_10\": true,\n" +
                    "            \"level_9\": true,\n" +
                    "            \"level_8\": true,\n" +
                    "            \"level_7\": true,\n" +
                    "            \"level_6\": true,\n" +
                    "            \"level_5\": true,\n" +
                    "            \"level_4\": true,\n" +
                    "            \"level_3\": true,\n" +
                    "            \"level_2\": true,\n" +
                    "            \"level_1\": true,\n" +
                    "            \"level_0\": true,\n" +
                    "            \"edit_others_pages\": true,\n" +
                    "            \"edit_published_pages\": true,\n" +
                    "            \"publish_pages\": true,\n" +
                    "            \"delete_pages\": true,\n" +
                    "            \"delete_others_pages\": true,\n" +
                    "            \"delete_published_pages\": true,\n" +
                    "            \"delete_posts\": true,\n" +
                    "            \"delete_others_posts\": true,\n" +
                    "            \"delete_published_posts\": true,\n" +
                    "            \"delete_private_posts\": true,\n" +
                    "            \"edit_private_posts\": true,\n" +
                    "            \"read_private_posts\": true,\n" +
                    "            \"delete_private_pages\": true,\n" +
                    "            \"edit_private_pages\": true,\n" +
                    "            \"read_private_pages\": true,\n" +
                    "            \"delete_users\": true,\n" +
                    "            \"create_users\": true,\n" +
                    "            \"unfiltered_upload\": true,\n" +
                    "            \"edit_dashboard\": true,\n" +
                    "            \"update_plugins\": true,\n" +
                    "            \"delete_plugins\": true,\n" +
                    "            \"install_plugins\": true,\n" +
                    "            \"update_themes\": true,\n" +
                    "            \"install_themes\": true,\n" +
                    "            \"update_core\": true,\n" +
                    "            \"list_users\": true,\n" +
                    "            \"remove_users\": true,\n" +
                    "            \"promote_users\": true,\n" +
                    "            \"edit_theme_options\": true,\n" +
                    "            \"delete_themes\": true,\n" +
                    "            \"export\": true\n" +
                    "        }\n" +
                    "    },\n" +
                    "    \"editor\": {\n" +
                    "        \"name\": \"Editor\",\n" +
                    "        \"capabilities\": {\n" +
                    "            \"moderate_comments\": true,\n" +
                    "            \"manage_categories\": true,\n" +
                    "            \"manage_links\": true,\n" +
                    "            \"upload_files\": true,\n" +
                    "            \"unfiltered_html\": true,\n" +
                    "            \"edit_posts\": true,\n" +
                    "            \"edit_others_posts\": true,\n" +
                    "            \"edit_published_posts\": true,\n" +
                    "            \"publish_posts\": true,\n" +
                    "            \"edit_pages\": true,\n" +
                    "            \"read\": true,\n" +
                    "            \"level_7\": true,\n" +
                    "            \"level_6\": true,\n" +
                    "            \"level_5\": true,\n" +
                    "            \"level_4\": true,\n" +
                    "            \"level_3\": true,\n" +
                    "            \"level_2\": true,\n" +
                    "            \"level_1\": true,\n" +
                    "            \"level_0\": true,\n" +
                    "            \"edit_others_pages\": true,\n" +
                    "            \"edit_published_pages\": true,\n" +
                    "            \"publish_pages\": true,\n" +
                    "            \"delete_pages\": true,\n" +
                    "            \"delete_others_pages\": true,\n" +
                    "            \"delete_published_pages\": true,\n" +
                    "            \"delete_posts\": true,\n" +
                    "            \"delete_others_posts\": true,\n" +
                    "            \"delete_published_posts\": true,\n" +
                    "            \"delete_private_posts\": true,\n" +
                    "            \"edit_private_posts\": true,\n" +
                    "            \"read_private_posts\": true,\n" +
                    "            \"delete_private_pages\": true,\n" +
                    "            \"edit_private_pages\": true,\n" +
                    "            \"read_private_pages\": true\n" +
                    "        }\n" +
                    "    },\n" +
                    "    \"author\": {\n" +
                    "        \"name\": \"Author\",\n" +
                    "        \"capabilities\": {\n" +
                    "            \"upload_files\": true,\n" +
                    "            \"edit_posts\": true,\n" +
                    "            \"edit_published_posts\": true,\n" +
                    "            \"publish_posts\": true,\n" +
                    "            \"read\": true,\n" +
                    "            \"level_2\": true,\n" +
                    "            \"level_1\": true,\n" +
                    "            \"level_0\": true,\n" +
                    "            \"delete_posts\": true,\n" +
                    "            \"delete_published_posts\": true\n" +
                    "        }\n" +
                    "    },\n" +
                    "    \"contributor\": {\n" +
                    "        \"name\": \"Contributor\",\n" +
                    "        \"capabilities\": {\n" +
                    "            \"edit_posts\": true,\n" +
                    "            \"read\": true,\n" +
                    "            \"level_1\": true,\n" +
                    "            \"level_0\": true,\n" +
                    "            \"delete_posts\": true\n" +
                    "        }\n" +
                    "    },\n" +
                    "    \"subscriber\": {\n" +
                    "        \"name\": \"Subscriber\",\n" +
                    "        \"capabilities\": {\n" +
                    "            \"read\": true,\n" +
                    "            \"level_0\": true\n" +
                    "        }\n" +
                    "    },\n" +
                    "    \"founder\": {\n" +
                    "        \"name\": \"Founder\",\n" +
                    "        \"capabilities\": {\n" +
                    "            \"switch_themes\": true,\n" +
                    "            \"edit_themes\": true,\n" +
                    "            \"activate_plugins\": true,\n" +
                    "            \"edit_plugins\": true,\n" +
                    "            \"edit_users\": true,\n" +
                    "            \"edit_files\": true,\n" +
                    "            \"manage_options\": true,\n" +
                    "            \"moderate_comments\": true,\n" +
                    "            \"manage_categories\": true,\n" +
                    "            \"manage_links\": true,\n" +
                    "            \"upload_files\": true,\n" +
                    "            \"import\": true,\n" +
                    "            \"unfiltered_html\": true,\n" +
                    "            \"edit_posts\": true,\n" +
                    "            \"edit_others_posts\": true,\n" +
                    "            \"edit_published_posts\": true,\n" +
                    "            \"publish_posts\": true,\n" +
                    "            \"edit_pages\": true,\n" +
                    "            \"read\": true,\n" +
                    "            \"level_10\": true,\n" +
                    "            \"level_9\": true,\n" +
                    "            \"level_8\": true,\n" +
                    "            \"level_7\": true,\n" +
                    "            \"level_6\": true,\n" +
                    "            \"level_5\": true,\n" +
                    "            \"level_4\": true,\n" +
                    "            \"level_3\": true,\n" +
                    "            \"level_2\": true,\n" +
                    "            \"level_1\": true,\n" +
                    "            \"level_0\": true,\n" +
                    "            \"edit_others_pages\": true,\n" +
                    "            \"edit_published_pages\": true,\n" +
                    "            \"publish_pages\": true,\n" +
                    "            \"delete_pages\": true,\n" +
                    "            \"delete_others_pages\": true,\n" +
                    "            \"delete_published_pages\": true,\n" +
                    "            \"delete_posts\": true,\n" +
                    "            \"delete_others_posts\": true,\n" +
                    "            \"delete_published_posts\": true,\n" +
                    "            \"delete_private_posts\": true,\n" +
                    "            \"edit_private_posts\": true,\n" +
                    "            \"read_private_posts\": true,\n" +
                    "            \"delete_private_pages\": true,\n" +
                    "            \"edit_private_pages\": true,\n" +
                    "            \"read_private_pages\": true,\n" +
                    "            \"delete_users\": true,\n" +
                    "            \"create_users\": true,\n" +
                    "            \"unfiltered_upload\": true,\n" +
                    "            \"edit_dashboard\": true,\n" +
                    "            \"update_plugins\": true,\n" +
                    "            \"delete_plugins\": true,\n" +
                    "            \"install_plugins\": true,\n" +
                    "            \"update_themes\": true,\n" +
                    "            \"install_themes\": true,\n" +
                    "            \"update_core\": true,\n" +
                    "            \"list_users\": true,\n" +
                    "            \"remove_users\": true,\n" +
                    "            \"promote_users\": true,\n" +
                    "            \"edit_theme_options\": true,\n" +
                    "            \"delete_themes\": true,\n" +
                    "            \"export\": true\n" +
                    "        }\n" +
                    "    },\n" +
                    "    \"team member\": {\n" +
                    "        \"name\": \"Team Member\",\n" +
                    "        \"capabilities\": {\n" +
                    "            \"edit_posts\": true,\n" +
                    "            \"read\": true,\n" +
                    "            \"level_1\": true,\n" +
                    "            \"level_0\": true,\n" +
                    "            \"delete_posts\": true\n" +
                    "        }\n" +
                    "    }\n" +
                    "}";

            // Deserialize JSON to Map
            roles = objectMapper.readValue(jsonCapabilitiesData, new TypeReference<Map<String, Map<String, Boolean>>>() {
            });

            return roles;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return roles;
    }


    public Map<String, Map<String, Boolean>> deserializeCapabilities(String serializedData) {
        Map<String, Map<String, Boolean>> result = null;
        try {
            result = new HashMap<>();

            // Assuming serializedData is a valid serialized string
            // Replace specific patterns to convert it into a JSON format
            String jsonFormattedData = serializedData
                    .replace("a:", "\"a\":")
                    .replace("s:", "\"s\":")
                    .replace("b:1", "\"b\":true")
                    .replace("b:0", "\"b\":false")
                    .replace(";", ",");


            // Use an ObjectMapper to deserialize the formatted string
            ObjectMapper objectMapper = new ObjectMapper();
            result = objectMapper.readValue(jsonFormattedData, new TypeReference<Map<String, Map<String, Boolean>>>() {
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace(); // Handle the exception appropriately
        }

        return result;
    }

    public Map<String, Boolean> getCapabilitiesForRole(String role) {
        return getRoles().getOrDefault(role, Map.of());
    }
}
