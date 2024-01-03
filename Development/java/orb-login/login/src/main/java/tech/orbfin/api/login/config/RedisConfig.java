//package tech.orbfin.api.login.config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//@ConfigurationProperties(prefix = "")
//public class RedisConfig {
//    @Value("${spring.data.redis.host}")
//    private String redisHost;
//
//    @Value("${spring.data.redis.port}")
//    private int redisPort;
//
//    @Value("${spring.data.redis.password}")
//    private String redisPassword;
//
//    @Autowired
//    private RedisConfig redisConfig;
//
//    public String getRedisHost(){return this.redisHost;}
//
//    public void setRedisHost(String redisHost){this.redisHost = redisHost;}
//
//    public int getRedisPort(){return this.redisPort;}
//
//    public void setRedisPort(int redisPort){this.redisPort = redisPort;}
//
//    public String getRedisPassword(){return this.redisPassword;}
//
//    public void setRedisPassword(String redisPassword){this.redisPassword = redisPassword;}
//
//}
