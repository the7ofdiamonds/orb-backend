package tech.orbfin.api.Database.Item;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;

@Entity
@Table(name = "items")
public class DatabaseItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @Column(name = "price_id")
    private String stripePriceId;

    @Column(name = "price_data")
    private String stripePriceData;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "tax_rates")
    private String taxRates;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getStripePriceId() {
        return stripePriceId;
    }

    public void setStripePriceId(String stripePriceId) {
        this.stripePriceId = stripePriceId;
    }

    public String getStripePriceData() {
        return stripePriceData;
    }

    public void setStripePriceData(String stripePriceData) {
        this.stripePriceData = stripePriceData;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getTaxRates() {
        return taxRates;
    }

    public void setTaxRates(String taxRates) {
        this.taxRates = taxRates;
    }
}
