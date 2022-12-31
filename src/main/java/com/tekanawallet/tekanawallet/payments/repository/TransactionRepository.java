package com.tekanawallet.tekanawallet.payments.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.tekanawallet.tekanawallet.registration.model.Transaction;
import com.tekanawallet.tekanawallet.registration.model.User;


;

public interface TransactionRepository extends JpaRepository<Transaction,UUID>{
	@Query("SELECT t from Transaction t where t.accountOwner=?1")
	List<Transaction> findByUser(User accountOwner);

}
