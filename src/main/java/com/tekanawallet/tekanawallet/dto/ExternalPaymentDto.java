package com.tekanawallet.tekanawallet.dto;

public class ExternalPaymentDto {
	
	private String reciever;
	
	private String externalReference;
	
	private long amount;
	
	private String description;
	
	

	public String getReciever() {
		return reciever;
	}

	public void setReciever(String receiver) {
		this.reciever = receiver;
	}

	public String getExternalReference() {
		return externalReference;
	}

	public void setExternalReference(String externalReference) {
		this.externalReference = externalReference;
	}

	public long getAmount() {
		return amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
	

}
