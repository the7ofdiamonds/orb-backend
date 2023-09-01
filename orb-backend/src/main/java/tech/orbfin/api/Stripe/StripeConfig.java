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
        Stripe.apiKey = "sk_test_51NKFzqKNsWPbtVUMVSabp6kptRHF5VndqqlXliby3CJPRK0w7e5mZobcjcA8vYtWdYJ1FPbtuzf2JcJwHUqrWjd000e0IMNPyo";
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
