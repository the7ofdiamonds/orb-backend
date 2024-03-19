package tech.orbfin.api.gateway.configurations;

import org.springframework.cloud.client.serviceregistry.AutoServiceRegistration;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;

@Configuration
public class ConfigDiscovery {
//    @Primary
//    @Bean
//    public ServiceRegistry serviceRegistryEndpoint(@Qualifier("zookeeperServiceRegistry") ServiceRegistry zookeeperServiceRegistry) {
//        return zookeeperServiceRegistry;
//    }

//    @Bean
//    public AutoServiceRegistration autoServiceRegistration(
//            @Qualifier("zookeeperAutoServiceRegistration") AutoServiceRegistration zookeeperAutoServiceRegistration) {
//        return zookeeperAutoServiceRegistration;
//    }
//
//    @Bean
//    @Primary
//    public Registration registration(@Qualifier("serviceInstanceRegistration") Registration serviceInstanceRegistration) {
//        return serviceInstanceRegistration;
//    }
//
//    @Bean
//    public ServiceRegistry serviceRegistryEndpoint(@Qualifier("zookeeperServiceRegistry") ServiceRegistry zookeeperServiceRegistry) {
//        return zookeeperServiceRegistry;
//    }
}
