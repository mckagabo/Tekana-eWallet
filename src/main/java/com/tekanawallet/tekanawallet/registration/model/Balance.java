package com.tekanawallet.tekanawallet.registration.model;

import java.time.LocalDateTime;
import java.util.UUID;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;

import jakarta.persistence.OneToOne;

@Entity(name="balances")
public class Balance {
	 @Id
	 @GeneratedValue(strategy=GenerationType.UUID)
	private UUID id;
	
	@OneToOne()
	@JoinColumn(name = "user_id")
	private User user;
	
	@Column(name="balance")
	private Long balance;
	
	@Column(name="updated_at")
	private LocalDateTime updatedAt;
	

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Long getBalance() {
		return balance;
	}

	public void setBalance(Long balance) {
		this.balance = balance;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
	
	

}
