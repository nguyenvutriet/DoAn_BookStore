package com.example.project_bookstore.Entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "flashsaledetail")
@IdClass(FlashSaleDetailId.class)
public class FlashSaleDetail {

    @Id
    @ManyToOne
    @JoinColumn(name = "flashSaleId")
    private FlashSale flashSale;

    @Id
    @ManyToOne
    @JoinColumn(name = "bookId")
    private Books book;

    @Column(nullable = false)
    private BigDecimal originalPrice;

    @Column(nullable = false)
    private BigDecimal salePrice;

    @Column(nullable = false)
    private Integer quantityLimit;

    private Integer quantitySold = 0;

    @Version
    private Long version;

    public FlashSale getFlashSale() { return flashSale; }
    public void setFlashSale(FlashSale flashSale) { this.flashSale = flashSale; }
    public Books getBook() { return book; }
    public void setBook(Books book) { this.book = book; }
    public BigDecimal getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(BigDecimal originalPrice) { this.originalPrice = originalPrice; }
    public BigDecimal getSalePrice() { return salePrice; }
    public void setSalePrice(BigDecimal salePrice) { this.salePrice = salePrice; }
    public Integer getQuantityLimit() { return quantityLimit; }
    public void setQuantityLimit(Integer quantityLimit) { this.quantityLimit = quantityLimit; }
    public Integer getQuantitySold() { return quantitySold; }
    public void setQuantitySold(Integer quantitySold) { this.quantitySold = quantitySold; }
    public Integer getRemaining() { return quantityLimit - quantitySold; }
}