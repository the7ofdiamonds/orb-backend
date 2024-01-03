//package tech.orbfin.api.Stripe.Invoice;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import tech.orbfin.api.Database.Invoice.DatabaseInvoice;
//
//import com.stripe.exception.StripeException;
//import com.stripe.model.Invoice;
//import com.stripe.model.InvoiceCollection;
//
//public class StripeInvoice {
//
//    public Invoice createInvoice(DatabaseInvoice databaseInvoice) {
//        try {
//            Map<String, Object> params = new HashMap<>();
//            params.put("customer", databaseInvoice.getCustomer());
//            params.put("collection_method", databaseInvoice.getCollectionMethod());
//            params.put("days_until_due", databaseInvoice.getDaysUntilDue());
//
//            Invoice stripeInvoice = Invoice.create(params);
//
//            return stripeInvoice;
//        } catch (StripeException e) {
//            e.printStackTrace();
//            System.err.println("Stripe Exception: " + e.getMessage());
//        }
//
//        return null;
//    }
//
//    public InvoiceCollection getAllInvoices() {
//        try {
//            Map<String, Object> params = new HashMap<>();
//
//            InvoiceCollection invoices = Invoice.list(params);
//
//            return invoices;
//        } catch (StripeException e) {
//            e.printStackTrace();
//            System.err.println("Stripe Exception: " + e.getMessage());
//        }
//
//        return null;
//    }
//
//    public Invoice getInvoice(String invoiceId) {
//        try {
//            Invoice invoice = Invoice.retrieve(invoiceId);
//            return invoice;
//        } catch (StripeException e) {
//            e.printStackTrace();
//            System.err.println("Stripe Exception: " + e.getMessage());
//        }
//
//        return null;
//    }
//
//    public Invoice updateInvoice(String invoiceId, DatabaseInvoice databaseInvoice) {
//        try {
//            Map<String, Object> params = new HashMap<>();
//            String description = databaseInvoice.getDescription();
//            Invoice invoiceToUpdate = Invoice.retrieve(invoiceId);
//            params.put("description", description);
//            Invoice updatedInvoice = invoiceToUpdate.update(params);
//
//            return updatedInvoice;
//        } catch (StripeException e) {
//            e.printStackTrace();
//            System.err.println("Stripe Exception: " + e.getMessage());
//        }
//
//        return null;
//    }
//
//    public Invoice deleteInvoice(String invoiceId) {
//        try {
//            Invoice invoice = Invoice.retrieve(invoiceId);
//            Invoice deletedInvoice = invoice.delete();
//
//            return deletedInvoice;
//        } catch (StripeException e) {
//            e.printStackTrace();
//            System.err.println("Stripe Exception: " + e.getMessage());
//        }
//
//        return null;
//    }
//
//    public Invoice finalizeInvoice(String invoiceId) {
//        try {
//            Invoice invoice = Invoice.retrieve(invoiceId);
//            Invoice finalizedInvoice = invoice.finalizeInvoice();
//
//            return finalizedInvoice;
//        } catch (StripeException e) {
//            e.printStackTrace();
//            System.err.println("Stripe Exception: " + e.getMessage());
//        }
//
//        return null;
//    }
//
//    public Invoice payInvoice(String invoiceId) {
//        try {
//            Invoice invoice = Invoice.retrieve(invoiceId);
//            Invoice acceptedInvoice = invoice.pay();
//
//            return acceptedInvoice;
//        } catch (StripeException e) {
//            e.printStackTrace();
//            System.err.println("Stripe Exception: " + e.getMessage());
//        }
//
//        return null;
//    }
//
//    public Invoice sendInvoice(String invoiceId) {
//        try {
//            Invoice invoice = Invoice.retrieve(invoiceId);
//            Invoice acceptedInvoice = invoice.sendInvoice();
//
//            return acceptedInvoice;
//        } catch (StripeException e) {
//            e.printStackTrace();
//            System.err.println("Stripe Exception: " + e.getMessage());
//        }
//
//        return null;
//    }
//
//    public Invoice voidInvoice(String invoiceId) {
//        try {
//            Invoice invoice = Invoice.retrieve(invoiceId);
//            Invoice voidedInvoice = invoice.voidInvoice();
//
//            return voidedInvoice;
//        } catch (StripeException e) {
//            e.printStackTrace();
//            System.err.println("Stripe Exception: " + e.getMessage());
//        }
//
//        return null;
//    }
//
//    public Invoice markInvoiceUncollectable(String invoiceId) {
//        try {
//            Invoice invoice = Invoice.retrieve(invoiceId);
//            Invoice uncollectableInvoice = invoice.markUncollectible();
//
//            return uncollectableInvoice;
//        } catch (StripeException e) {
//            e.printStackTrace();
//            System.err.println("Stripe Exception: " + e.getMessage());
//        }
//
//        return null;
//    }
//}
