//package tech.orbfin.api.Database.Quote.QuoteItem;
//
//import java.io.IOException;
//import java.util.List;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import jakarta.persistence.AttributeConverter;
//import jakarta.persistence.Converter;
//
//// ...
//
//@Converter
//public class QuoteItemListConverter implements AttributeConverter<List<QuoteItem>, String> {
//
//    private final static ObjectMapper objectMapper = new ObjectMapper();
//
//    @Override
//    public String convertToDatabaseColumn(List<QuoteItem> attribute) {
//        try {
//            return objectMapper.writeValueAsString(attribute);
//        } catch (Exception e) {
//            throw new RuntimeException("Error converting list to JSON: " + e.getMessage());
//        }
//    }
//
//    @Override
//    public List<QuoteItem> convertToEntityAttribute(String dbData) {
//        try {
//            TypeReference<List<QuoteItem>> typeRef = new TypeReference<List<QuoteItem>>() {
//            };
//            return objectMapper.readValue(dbData, typeRef);
//        } catch (IOException e) {
//            throw new RuntimeException("Error converting JSON to list: " + e.getMessage());
//        }
//    }
//}
