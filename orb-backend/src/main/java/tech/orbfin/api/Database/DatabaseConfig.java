package tech.orbfin.api.Database;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import tech.orbfin.api.Database.Item.DatabaseItemController;
import tech.orbfin.api.Database.Quote.DatabaseQuoteController;
import tech.orbfin.api.Database.Invoice.DatabaseInvoiceController;
import tech.orbfin.api.Database.Receipt.DatabaseReceiptController;

@Configuration
public class DatabaseConfig {

    @Bean
    public DatabaseItemController itemController() {
        return new DatabaseItemController();
    }

    @Bean
    public DatabaseQuoteController quoteController() {
        return new DatabaseQuoteController();
    }

    @Bean
    public DatabaseInvoiceController invoiceController() {
        return new DatabaseInvoiceController();
    }

    @Bean
    public DatabaseReceiptController receiptController() {
        return new DatabaseReceiptController();
    }
}
