package com.jps.apiexample.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jps.apiexample.config.MoneySerializer;

/**
 * Representa la información de balance de un usuario
 */
public class BalanceInfo {
    
    @JsonSerialize(using = MoneySerializer.class)
    private Double balance;
    
    @JsonProperty("total_debits")
    @JsonSerialize(using = MoneySerializer.class)
    private Double totalDebits;
    
    @JsonProperty("total_credits")
    @JsonSerialize(using = MoneySerializer.class)
    private Double totalCredits;
    
    // Constructors
    public BalanceInfo() {}
    
    public BalanceInfo(Double balance, Double totalDebits, Double totalCredits) {
        this.balance = balance;
        this.totalDebits = totalDebits;
        this.totalCredits = totalCredits;
    }
    
    // Getters and Setters
    public Double getBalance() {
        return balance;
    }
    
    public void setBalance(Double balance) {
        this.balance = balance;
    }
    
    public Double getTotalDebits() {
        return totalDebits;
    }
    
    public void setTotalDebits(Double totalDebits) {
        this.totalDebits = totalDebits;
    }
    
    public Double getTotalCredits() {
        return totalCredits;
    }
    
    public void setTotalCredits(Double totalCredits) {
        this.totalCredits = totalCredits;
    }
    
    @Override
    public String toString() {
        return "BalanceInfo{" +
                "balance=" + balance +
                ", totalDebits=" + totalDebits +
                ", totalCredits=" + totalCredits +
                '}';
    }
}
