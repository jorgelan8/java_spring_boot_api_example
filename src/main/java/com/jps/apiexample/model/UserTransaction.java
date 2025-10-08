package com.jps.apiexample.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

/**
 * Representa una transacción de usuario en el sistema
 */
@Entity
@Table(name = "user_transactions", 
       indexes = {
           @Index(name = "idx_user_id", columnList = "user_id"),
           @Index(name = "idx_date_time", columnList = "date_time"),
           @Index(name = "idx_user_date", columnList = "user_id, date_time")
       })
public class UserTransaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Positive(message = "ID must be positive")
    private Integer id;
    
    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be positive")
    @Column(name = "user_id", nullable = false)
    @JsonProperty("user_id")
    private Integer userId;
    
    @NotNull(message = "Amount is required")
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private Double amount;
    
    @NotNull(message = "DateTime is required")
    @Column(name = "date_time", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateTime;
    
    // Constructors
    public UserTransaction() {}
    
    public UserTransaction(Integer id, Integer userId, Double amount, LocalDateTime dateTime) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.dateTime = dateTime;
    }
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Integer getUserId() {
        return userId;
    }
    
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    
    public Double getAmount() {
        return amount;
    }
    
    public void setAmount(Double amount) {
        this.amount = amount;
    }
    
    public LocalDateTime getDateTime() {
        return dateTime;
    }
    
    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
    
    @Override
    public String toString() {
        return "UserTransaction{" +
                "id=" + id +
                ", userId=" + userId +
                ", amount=" + amount +
                ", dateTime=" + dateTime +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserTransaction that = (UserTransaction) o;
        return id != null ? id.equals(that.id) : that.id == null;
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
