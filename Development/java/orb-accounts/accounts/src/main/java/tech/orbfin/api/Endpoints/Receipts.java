//package tech.orbfin.api.Endpoints;
//
//import java.util.List;
//
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import tech.orbfin.api.Database.Receipt.DatabaseReceipt;
//import tech.orbfin.api.Database.Receipt.DatabaseReceiptController;
//
//@RestController
//@RequestMapping("/receipts")
//public class Receipts {
//    private final DatabaseReceiptController databaseReceiptController;
//
//    public Receipts(DatabaseReceiptController databaseReceiptController) {
//        this.databaseReceiptController = databaseReceiptController;
//    }
//
//    @PostMapping
//    public void createReceipt(@RequestBody DatabaseReceipt databaseReceipt) {
//        if (databaseReceipt != null) {
//            databaseReceiptController.createReceipt(databaseReceipt);
//        } else {
//            return;
//        }
//    }
//
//    @GetMapping
//    public List<DatabaseReceipt> getAllReceipts() {
//        return databaseReceiptController.getAllReceipts();
//    }
//
//    @GetMapping("/{receiptId}")
//    public DatabaseReceipt getReceipt(@PathVariable Long receiptId) {
//        return databaseReceiptController.getReceiptById(receiptId);
//    }
//}
