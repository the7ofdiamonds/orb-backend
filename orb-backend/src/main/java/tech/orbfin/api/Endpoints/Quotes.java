package tech.orbfin.api.Endpoints;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stripe.exception.StripeException;
import com.stripe.model.Quote;
import com.stripe.model.QuoteCollection;

import tech.orbfin.api.Database.Quote.DatabaseQuote;
import tech.orbfin.api.Database.Quote.DatabaseQuoteController;
import tech.orbfin.api.Stripe.Quote.StripeQuote;

@RestController
@RequestMapping("/quotes")
public class Quotes {
    private final StripeQuote stripeQuote;
    private final DatabaseQuoteController databaseQuote;

    @Autowired
    public Quotes(StripeQuote stripeQuote, DatabaseQuoteController databaseQuote) {
        this.stripeQuote = stripeQuote;
        this.databaseQuote = databaseQuote;
    }

    @PostMapping
    public void createQuote(@RequestBody DatabaseQuote quote) throws StripeException {
        if (stripeQuote != null && databaseQuote != null) {
            stripeQuote.createQuote(quote);
            databaseQuote.createQuote(quote);
        } else {
            return;
        }
    }

    @GetMapping
    public QuoteCollection getAllQuotes() throws StripeException {
        return stripeQuote.getAllQuotes();
    }

    @GetMapping("/{quoteId}")
    public Quote getQuote(@PathVariable String quoteId) throws StripeException {
        return stripeQuote.getQuote(quoteId);
    }

    @PostMapping("/update/{quoteId}")
    public void updateQuote(@PathVariable String quoteId, @RequestBody DatabaseQuote databseQuote)
            throws StripeException {
        stripeQuote.updateQuote(quoteId, databseQuote);
    }

    @PostMapping("/{quoteId}/finalize")
    public void finalizeQuote(@PathVariable String quoteId) throws StripeException {
        stripeQuote.finalizeQuote(quoteId);
    }

    @PostMapping("/{quoteId}/accept")
    public void acceptQuote(@PathVariable String quoteId) throws StripeException {
        stripeQuote.acceptQuote(quoteId);
    }

    @PostMapping("/{quoteId}/cancel")
    public void cancelQuote(@PathVariable String quoteId) throws StripeException {
        stripeQuote.cancelQuote(quoteId);
    }
}
