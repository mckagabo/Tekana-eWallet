package com.tekanawallet.tekanawallet.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.tekanawallet.tekanawallet.registration.model.User;

import jakarta.persistence.Column;


public class TransactionDto {
	
	private UUID id;
	
	@Column
	private String description;
	
	
	private String currency;
	
	
	private LocalDateTime transactionTime;
	

	private String userAccount;
	
	
	private String  internalReference;
	
	

	private Long cashIn;
	
	
	private Long cashOut;
	
	
    private String externalReference;


	public UUID getId() {
		return id;
	}


	public void setId(UUID id) {
		this.id = id;
	}


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


	public String getInternalReference() {
		return internalReference;
	}


	public void setInternalReference(String internalReference) {
		this.internalReference = internalReference;
	}
	
    
	
}
