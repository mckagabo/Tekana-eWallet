package com.tekanawallet.tekanawallet.registration.model;

import java.time.LocalDateTime;
import java.util.UUID;




import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
@Entity
@Table(name="transactions")
public class Transaction {
	@Id
	@GeneratedValue(strategy=GenerationType.UUID)
	private UUID id;
	
	@Column
	private String description;
	
	@Column 
	private String currency;
	
	@Column
	private LocalDateTime transactionTime;
	
	@Column
	private String userAccount;
	
	@ManyToOne
    @JoinColumn(name = "account_owner")
	private User accountOwner;
	
	@Column(name="cash_in")
	private Long cashIn;
	
	@Column(name="cash_out")
	private Long cashOut;
	
	@Column(name="external_reference")
    private String externalReference;
	
	@Column(name="internal_reference")
	private String internalReference;
	
	
	

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public LocalDateTime getTransactionTime() {
		return transactionTime;
	}

	public void setTransactionTime(LocalDateTime transactionTime) {
		this.transactionTime = transactionTime;
	}

	public String getUserAccount() {
		return userAccount;
	}

	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}

	public User getAccountOwner() {
		return accountOwner;
	}

	public void setAccountOwner(User accountOwner) {
		this.accountOwner = accountOwner;
	}

	public Long getCashIn() {
		return cashIn;
	}

	public void setCashIn(Long cashIn) {
		this.cashIn = cashIn;
	}

	public Long getCashOut() {
		return cashOut;
	}

	public void setCashOut(Long cashOut) {
		this.cashOut = cashOut;
	}

	public String getExternalReference() {
		return externalReference;
	}

	public void setExternalReference(String externalReference) {
		this.externalReference = externalReference;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getInternalReference() {
		return internalReference;
	}

	public void setInternalReference(String internalReference) {
		this.internalReference = internalReference;
	}
	
	

}
