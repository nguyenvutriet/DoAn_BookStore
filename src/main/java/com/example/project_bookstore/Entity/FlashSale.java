package com.example.project_bookstore.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "flashsale")
public class FlashSale {

    @Id
    @Column(name = "flashSaleId", length = 10)
    private String flashSaleId;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(length = 20)
    private String status = "ACTIVE";

    @OneToMany(mappedBy = "flashSale", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FlashSaleDetail> details;

    public String getFlashSaleId() { return flashSaleId; }
    public void setFlashSaleId(String flashSaleId) { this.flashSaleId = flashSaleId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<FlashSaleDetail> getDetails() { return details; }
    public void setDetails(List<FlashSaleDetail> details) { this.details = details; }
}