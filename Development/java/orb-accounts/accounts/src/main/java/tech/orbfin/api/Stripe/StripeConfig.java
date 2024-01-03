//package tech.orbfin.api.Stripe;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import com.stripe.Stripe;
//
//import tech.orbfin.api.Stripe.Invoice.StripeInvoice;
//import tech.orbfin.api.Stripe.Quote.StripeQuote;
//
//import tech.orbfin.api.config.EnvConfig;
//
//@Configuration
//public class StripeConfig {
//
//    public StripeConfig() {
//        EnvConfig envConfig = new EnvConfig();
//        Stripe.apiKey = envConfig.getStripeAPIKey();
//    }
//
//    @Bean
//    public StripeQuote stripeQuote() {
//        // You can instantiate and configure StripeQuote here
//        return new StripeQuote();
//    }
//
//    @Bean
//    public StripeInvoice stripeInvoice() {
//        // You can instantiate and configure StripeQuote here
//        return new StripeInvoice();
//    }
//}
