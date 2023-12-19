package tech.orbfin.api.Endpoints;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stripe.model.Invoice;
import com.stripe.model.InvoiceCollection;

import tech.orbfin.api.Database.Invoice.DatabaseInvoice;
import tech.orbfin.api.Database.Invoice.DatabaseInvoiceController;
import tech.orbfin.api.Stripe.Invoice.StripeInvoice;

@RestController
@RequestMapping("/invoices")
public class Invoices {
    private final StripeInvoice stripeInvoice;
    private final DatabaseInvoiceController databaseInvoiceController;

    public Invoices(StripeInvoice stripeInvoice, DatabaseInvoiceController databaseInvoiceController) {
        this.stripeInvoice = stripeInvoice;
        this.databaseInvoiceController = databaseInvoiceController;
    }

    @PostMapping
    public void createInvoice(@RequestBody DatabaseInvoice databaseInvoice) {
        if (stripeInvoice != null && databaseInvoice != null) {

            stripeInvoice.createInvoice(databaseInvoice);
            databaseInvoiceController.createInvoice(databaseInvoice);
        } else {
            return;
        }
    }

    @GetMapping
    public InvoiceCollection getAllInvoices() {
        return stripeInvoice.getAllInvoices();
    }

    @GetMapping("/{invoiceId}")
    public Invoice getInvoice(@PathVariable String invoiceId) {
        return stripeInvoice.getInvoice(invoiceId);
    }

    @PostMapping("/update/{invoiceId}")
    public void updateInvoice(@PathVariable String invoiceId, @RequestBody DatabaseInvoice databseInvoice) {
        stripeInvoice.updateInvoice(invoiceId, databseInvoice);
    }

    @DeleteMapping("/{invoiceId}")
    public void deleteInvoice(@PathVariable String invoiceId) {
        stripeInvoice.deleteInvoice(invoiceId);
    }

    @PostMapping("/{invoiceId}/finalize")
    public void finalizeInvoice(@PathVariable String invoiceId) {
        stripeInvoice.finalizeInvoice(invoiceId);
    }

    @PostMapping("/{invoiceId}/pay")
    public void payInvoice(@PathVariable String invoiceId) {
        stripeInvoice.payInvoice(invoiceId);
    }

    @PostMapping("/{invoiceId}/send")
    public void sendInvoice(@PathVariable String invoiceId) {
        stripeInvoice.sendInvoice(invoiceId);
    }

    @PostMapping("/{invoiceId}/void")
    public void voidInvoice(@PathVariable String invoiceId) {
        stripeInvoice.voidInvoice(invoiceId);
    }

    @PostMapping("/{invoiceId}/mark_uncollectable")
    public void markInvoiceUncollectable(@PathVariable String invoiceId) {
        stripeInvoice.markInvoiceUncollectable(invoiceId);
    }
}
