package tech.orbfin.api.gateway.services;

public interface IServiceToken {
    boolean isAccessTokenValid(String accessToken);
    String getUsernameFromAccessToken(String accessToken);
}
