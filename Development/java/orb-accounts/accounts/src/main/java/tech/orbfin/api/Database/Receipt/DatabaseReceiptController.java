//package tech.orbfin.api.Database.Receipt;
//
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//
//public class DatabaseReceiptController {
//
//    @Autowired
//    private DatabaseReceiptRepository receiptRepository;
//
//    public List<DatabaseReceipt> getAllReceipts() {
//        return receiptRepository.findAll();
//    }
//
//    public DatabaseReceipt getReceiptById(Long id) {
//        return receiptRepository.findById(id).get();
//    }
//
//    public DatabaseReceipt createReceipt(DatabaseReceipt receipt) {
//
//        return receiptRepository.save(receipt);
//    }
//
//    public String deleteReceipt(Long id) {
//        try {
//            receiptRepository.findById(id).get();
//            receiptRepository.deleteById(id);
//            return "Receipt deleted successfully";
//        } catch (Exception e) {
//            return "Receipt #" + id + " not found in the database: " + e.getMessage();
//        }
//    }
//
//}
