//package tech.orbfin.api.gateway.repositories;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import org.springframework.security.oauth2.core.AuthorizationGrantType;
//import org.springframework.security.oauth2.client.registration.ClientRegistration;
//import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
//import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
//
//@Configuration
//public class RepositoryCustomClientRegistration implements ClientRegistrationRepository {
//    private final String tenantID = System.getenv().getOrDefault("MICROSOFT_TENANT_ID", "MICROSOFT_TENANT_ID");
//
//    @Bean
//    public ClientRegistrationRepository clientRegistrationRepository() {
//        //Use Redis
//        return new InMemoryClientRegistrationRepository(googleClientRegistration(), microsoftClientRegistration());
//    }
//
//    private ClientRegistration googleClientRegistration() {
//        return ClientRegistration
//                .withRegistrationId("google")
//                .clientId(System.getenv().getOrDefault("GOOGLE_CLIENT_ID", "GOOGLE_CLIENT_ID"))
//                .clientSecret(System.getenv().getOrDefault("GOOGLE_CLIENT_SECRET", "GOOGLE_CLIENT_SECRET"))
//                .authorizationUri("https://accounts.google.com/o/oauth2/auth")
//                .tokenUri("https://accounts.google.com/o/oauth2/token")
//                .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
//                .userNameAttributeName("id")
//                .clientName("Google")
//                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
//                .scope("https://www.googleapis.com/auth/drive")
//                .redirectUri("http://localhost:8080")
//                .build();
//    }
//
//    private ClientRegistration microsoftClientRegistration() {
//        return ClientRegistration
//                .withRegistrationId("microsoft")
//                .clientId(System.getenv().getOrDefault("MICROSOFT_CLIENT_ID", "MICROSOFT_CLIENT_ID"))
//                .clientSecret(System.getenv().getOrDefault("MICROSOFT_CLIENT_SECRET", "MICROSOFT_CLIENT_SECRET"))
//                .authorizationUri("https://login.microsoftonline.com/" + tenantID + "/oauth2/v2.0/authorize")
//                .tokenUri("https://login.microsoftonline.com/" + tenantID + "/oauth2/v2.0/token")
//                .userInfoUri("https://graph.microsoft.com/v1.0/me")
//                .userNameAttributeName("id")
//                .clientName("Microsoft")
//                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
//                .scope("https://graph.microsoft.com/User.Read")
//                .redirectUri("http://localhost:8080/login/oauth2/code/microsoft")
//                .build();
//    }
//
//    @Override
//    public ClientRegistration findByRegistrationId(String registrationId) {
//        return null;
//    }
//}