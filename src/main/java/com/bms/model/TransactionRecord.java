package com.bms.model;

import java.math.BigDecimal;

public class TransactionRecord {
    private long accountId;
    private String transactionType;
    private BigDecimal amount;
    private String referenceAccount;
    private String description;

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getReferenceAccount() {
        return referenceAccount;
    }

    public void setReferenceAccount(String referenceAccount) {
        this.referenceAccount = referenceAccount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
