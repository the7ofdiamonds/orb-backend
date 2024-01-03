//package tech.orbfin.api.Database.Quote;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//import jakarta.persistence.Convert;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.Table;
//
//import tech.orbfin.api.Database.Quote.QuoteItem.QuoteItemListConverter;
//
//import org.hibernate.annotations.CreationTimestamp;
//
//import com.fasterxml.jackson.annotation.JsonFormat;
//
//@Entity
//@Table(name = "quotes")
//public class DatabaseQuote {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    @CreationTimestamp
//    @Column(name = "createdAt")
//    private LocalDateTime createdAt;
//
//    @Column(name = "customer")
//    private String stripeCustomerId;
//
//    @Convert(converter = QuoteItemListConverter.class)
//    @Column(name = "items", columnDefinition = "TEXT")
//    private List<Object> items;
//
//    @Column(name = "description")
//    private String description;
//
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public LocalDateTime getCreatedAt() {
//        return createdAt;
//    }
//
//    public void setCreatedAt(LocalDateTime createdAt) {
//        this.createdAt = createdAt;
//    }
//
//    public String getCustomer() {
//        return stripeCustomerId;
//    }
//
//    public void setCustomer(String stripeCustomerId) {
//        this.stripeCustomerId = stripeCustomerId;
//    }
//
//    public List<Object> getItems() {
//        return items;
//    }
//
//    public void setItems(List<Object> items) {
//        this.items = items;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }
//}
