package tech.orbfin.api.Stripe;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.stripe.Stripe;

import tech.orbfin.api.Stripe.Invoice.StripeInvoice;
import tech.orbfin.api.Stripe.Quote.StripeQuote;

@Configuration
public class StripeConfig {

    public StripeConfig() {
        // Set your Stripe API key here
        Stripe.apiKey = "sk_test_51NKFzqKNsWPbtVUM5dY6xwIZN9eRW8cHN1d94YJz24KMjSGlLDKI6dA5Rq7JLsyvLlQKksl70Mp4gyR88NtAAh9800eky4lkdb";
    }

    @Bean
    public StripeQuote stripeQuote() {
        // You can instantiate and configure StripeQuote here
        return new StripeQuote();
    }

    @Bean
    public StripeInvoice stripeInvoice() {
        // You can instantiate and configure StripeQuote here
        return new StripeInvoice();
    }
}
