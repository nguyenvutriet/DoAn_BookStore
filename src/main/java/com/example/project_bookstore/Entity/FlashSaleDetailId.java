package com.example.project_bookstore.Entity;

import java.io.Serializable;
import java.util.Objects;

public class FlashSaleDetailId implements Serializable {
    private String flashSale; // phải trùng tên field @Id kiểu quan hệ bên dưới
    private String book;

    public FlashSaleDetailId() {}
    public FlashSaleDetailId(String flashSale, String book) {
        this.flashSale = flashSale;
        this.book = book;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FlashSaleDetailId)) return false;
        FlashSaleDetailId that = (FlashSaleDetailId) o;
        return Objects.equals(flashSale, that.flashSale) && Objects.equals(book, that.book);
    }

    @Override
    public int hashCode() { return Objects.hash(flashSale, book); }
}