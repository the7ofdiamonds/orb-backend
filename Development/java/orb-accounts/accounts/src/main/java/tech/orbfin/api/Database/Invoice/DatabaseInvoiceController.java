//package tech.orbfin.api.Database.Invoice;
//
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//
//public class DatabaseInvoiceController {
//
//    @Autowired
//    private DatabaseInvoiceRepository invoiceRepository;
//
//    public List<DatabaseInvoice> getAllInvoices() {
//        return invoiceRepository.findAll();
//    }
//
//    public DatabaseInvoice getInvoiceById(Long id) {
//        return invoiceRepository.findById(id).get();
//    }
//
//    public DatabaseInvoice createInvoice(DatabaseInvoice databaseInvoice) {
//
//        return invoiceRepository.save(databaseInvoice);
//    }
//
//    public DatabaseInvoice updateInvoice(Long id, DatabaseInvoice invoice) {
//        DatabaseInvoice existingInvoice = invoiceRepository.findById(id).get();
//        existingInvoice.setDescription(invoice.getDescription());
//        return invoiceRepository.save(existingInvoice);
//    }
//
//    public String deleteInvoice(Long id) {
//        try {
//            invoiceRepository.findById(id).get();
//            invoiceRepository.deleteById(id);
//            return "Invoice deleted successfully";
//        } catch (Exception e) {
//            return "Invoice not found in the database: " + e.getMessage();
//        }
//    }
//}
