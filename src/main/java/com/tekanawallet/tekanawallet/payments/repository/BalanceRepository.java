package com.tekanawallet.tekanawallet.payments.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;

import com.tekanawallet.tekanawallet.registration.model.Balance;
import com.tekanawallet.tekanawallet.registration.model.User;




public interface BalanceRepository extends JpaRepository<Balance,UUID>{
	
	@Query("Select b from balances b where b.user=?1")
	Balance findByUser(User user);

	

}
