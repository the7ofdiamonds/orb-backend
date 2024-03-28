package tech.orbfin.api.Stripe;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.stripe.Stripe;

import tech.orbfin.api.Stripe.Invoice.StripeInvoice;
import tech.orbfin.api.Stripe.Quote.StripeQuote;

@Configuration
@PropertySource("classpath:application.yaml")
public class StripeConfig {

    @Value("${stripe.api-key}")
    private String stripeApiKey;

    @Bean
    public StripeQuote stripeQuote() {
        return new StripeQuote();
    }

    @Bean
    public StripeInvoice stripeInvoice() {
        return new StripeInvoice();
    }

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }
}
