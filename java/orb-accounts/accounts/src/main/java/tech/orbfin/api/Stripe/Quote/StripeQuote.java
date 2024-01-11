package tech.orbfin.api.Stripe.Quote;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tech.orbfin.api.Database.Quote.DatabaseQuote;

import com.stripe.exception.StripeException;
import com.stripe.model.Quote;
import com.stripe.model.QuoteCollection;

public class StripeQuote {

    public Quote createQuote(DatabaseQuote databaseQuote) throws StripeException {
        try {
            Map<String, Object> params = new HashMap<>();
            List<Object> items = databaseQuote.getItems();

            params.put("customer", databaseQuote.getCustomer());
            params.put("line_items", items);
            params.put("description", databaseQuote.getDescription());

            Quote stripeQuote = Quote.create(params);

            return stripeQuote;
        } catch (StripeException e) {
            e.printStackTrace();
            System.err.println("Stripe Exception: " + e.getMessage());
        }

        return null;
    }

    public QuoteCollection getAllQuotes() throws StripeException {
        try {
            Map<String, Object> params = new HashMap<>();

            QuoteCollection quotes = Quote.list(params);

            return quotes;
        } catch (StripeException e) {
            e.printStackTrace();
            System.err.println("Stripe Exception: " + e.getMessage());
        }

        return null;
    }

    public Quote getQuote(String quoteId) throws StripeException {
        try {
            Quote quote = Quote.retrieve(quoteId);
            return quote;
        } catch (StripeException e) {
            e.printStackTrace();
            System.err.println("Stripe Exception: " + e.getMessage());
        }

        return null;
    }

    public Quote updateQuote(String quoteId, DatabaseQuote databaseQuote) throws StripeException {
        try {
            Map<String, Object> params = new HashMap<>();
            List<Object> items = databaseQuote.getItems();

            Quote quoteToUpdate = Quote.retrieve(quoteId);

            params.put("line_items", items);

            Quote updatedQuote = quoteToUpdate.update(params);

            return updatedQuote;
        } catch (StripeException e) {
            e.printStackTrace();
            System.err.println("Stripe Exception: " + e.getMessage());
        }

        return null;
    }

    public Quote finalizeQuote(String quoteId) throws StripeException {
        try {
            Quote quote = Quote.retrieve(quoteId);
            Quote finalizedQuote = quote.finalizeQuote();

            return finalizedQuote;
        } catch (StripeException e) {
            e.printStackTrace();
            System.err.println("Stripe Exception: " + e.getMessage());
        }

        return null;
    }

    public Quote acceptQuote(String quoteId) throws StripeException {
        try {
            Quote quote = Quote.retrieve(quoteId);
            Quote acceptedQuote = quote.accept();

            return acceptedQuote;
        } catch (StripeException e) {
            e.printStackTrace();
            System.err.println("Stripe Exception: " + e.getMessage());
        }

        return null;
    }

    public Quote cancelQuote(String quoteId) throws StripeException {
        try {
            Quote quote = Quote.retrieve(quoteId);
            Quote cancelledQuote = quote.cancel();

            return cancelledQuote;
        } catch (StripeException e) {
            e.printStackTrace();
            System.err.println("Stripe Exception: " + e.getMessage());
        }

        return null;
    }
}
