//package tech.orbfin.api.gateway.configurations;
//
//import org.springframework.context.ApplicationListener;
//import org.springframework.context.event.ContextRefreshedEvent;
//import org.springframework.stereotype.Component;
//import redis.clients.jedis.Jedis;
//
//@Component
//public class ConfigJedis implements ApplicationListener<ContextRefreshedEvent> {
//
//    @Override
//    public void onApplicationEvent(ContextRefreshedEvent event) {
//        // Connect to Redis server
//        try (Jedis jedis = new Jedis("localhost", 6379)) {
//            // Replace "oldkey" with your current key and "newkey" with the desired new key
//            String oldKey = "id";
//            String newKey = "token";
//
//            // Rename the key
//            jedis.rename(oldKey, newKey);
//
//            System.out.println("Key renamed successfully.");
//        }
//    }
//}
