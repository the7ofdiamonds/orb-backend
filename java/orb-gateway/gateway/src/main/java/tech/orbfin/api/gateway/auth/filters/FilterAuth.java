package tech.orbfin.api.gateway.auth.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.function.Consumer;

@Component
@AllArgsConstructor
public class FilterAuth implements GatewayFilterFactory<FilterAuth.Config> {

    private final FilterJWT filterJWT;

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            try {
                // Adapt the FilterJWT to work with ServerWebExchange if needed
                // For example, create a wrapper class that implements HttpServletRequest and HttpServletResponse
                // and adapts them to ServerWebExchange
                ServerWebExchange adaptedExchange = adaptExchange(exchange);
                return Mono.fromRunnable(() -> {
                    try {
                        filterJWT.doFilterInternal((HttpServletRequest) adaptedExchange.getRequest(), (HttpServletResponse) adaptedExchange.getResponse(), (FilterChain) chain);
                    } catch (ServletException | IOException e) {
                        // Handle the exception based on your application's requirements
                        e.printStackTrace(); // Log the exception
                    }
                });
            } catch (Exception e) {
                // Handle the exception based on your application's requirements
                e.printStackTrace(); // Log the exception
                return Mono.empty();
            }
        };
    }

    private ServerWebExchange adaptExchange(ServerWebExchange exchange) {
        // Implement the adaptation logic if needed
        // For example, create a wrapper class that implements HttpServletRequest and HttpServletResponse
        // and adapts them to ServerWebExchange
        // This depends on the requirements and structure of your FilterJWT class
        return exchange;
    }

    @Override
    public Config newConfig() {
        return new Config();
    }

    @Override
    public Class<Config> getConfigClass() {
        return Config.class;
    }

    @Override
    public String name() {
        return "FilterAuth";
    }

    public static class Config {
        // Configuration properties can be added here if needed
    }
}
