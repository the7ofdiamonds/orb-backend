//package tech.orbfin.api.gateway.configurations;
//
//import jakarta.persistence.EntityManagerFactory;
//import lombok.Getter;
//import lombok.Setter;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.jdbc.DataSourceBuilder;
//import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//import org.springframework.orm.jpa.JpaTransactionManager;
//import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
//import org.springframework.transaction.PlatformTransactionManager;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//
//import javax.sql.DataSource;
//
//@Setter
//@Getter
//@Configuration
//@EnableTransactionManagement
//@EnableJpaRepositories(
//        basePackages = "tech.orbfin.api.gateway.model.orb",
//        entityManagerFactoryRef = "orbEntityManagerFactory",
//        transactionManagerRef = "orbTransactionManager"
//)
//public class ConfigORB {
//
//    @Value("${spring.orb.datasource.url}")
//    private String url;
//
//    @Value("${spring.orb.datasource.username}")
//    private String username;
//
//    @Value("${spring.orb.datasource.password}")
//    private String password;
//
//    @Value("${spring.orb.datasource.driver-class-name}")
//    private String driverClassName;
//
//    @Bean(name = "orbDataSource")
//    public DataSource dataSource() {
//        return DataSourceBuilder.create()
//                .url(url)
//                .username(username)
//                .password(password)
//                .driverClassName(driverClassName)
//                .build();
//    }
//
//    @Bean(name = "orbEntityManagerFactory")
//    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
//            EntityManagerFactoryBuilder builder,
//            DataSource dataSource) {
//        return builder
//                .dataSource(dataSource)
//                .packages("tech.orbfin.api.gateway.model.orb")
//                .persistenceUnit("orb")
//                .build();
//    }
//
//    @Bean
//    public PlatformTransactionManager orbTransactionManager(EntityManagerFactory entityManagerFactory) {
//        return new JpaTransactionManager(entityManagerFactory);
//    }
//}
