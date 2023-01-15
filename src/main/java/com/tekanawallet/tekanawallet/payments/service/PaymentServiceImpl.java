package com.tekanawallet.tekanawallet.payments.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekanawallet.tekanawallet.payments.repository.BalanceRepository;
import com.tekanawallet.tekanawallet.payments.repository.TransactionRepository;
import com.tekanawallet.tekanawallet.registration.model.Balance;
import com.tekanawallet.tekanawallet.registration.model.Transaction;
import com.tekanawallet.tekanawallet.registration.model.User;
import com.tekanawallet.tekanawallet.registration.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class PaymentServiceImpl implements PaymentService{
	
	@Autowired
	BalanceRepository balanceRepository;
	
	@Autowired 
	UserRepository userRepository;
	
	@Autowired
	TransactionRepository transactionRepository;
	
	
    

	

	@Override
	@Transactional
	public Transaction cashIn(Transaction transaction) {
		Transaction tansact=null;
		try {
			
			Balance userCurrentBalance=this.userBalance(transaction.getAccountOwner());
			Long amountAfterCashIn=this.increaseBalance(userCurrentBalance,transaction.getCashIn());
			updateBalance(userCurrentBalance, amountAfterCashIn);
			tansact=transactionRepository.save(transaction);
		} catch (Exception e) {
			e.getStackTrace();
		}
	    
		return tansact;
	}

	@Override
	@Transactional
	public Transaction cashOut(Transaction transaction) {
		Transaction transact=null;
		long transactionCharges=0;
		try {
		Balance userCurrentBalance=this.userBalance(transaction.getAccountOwner());
		 if(isEnoughBalance(userCurrentBalance, transaction.getCashOut(), transactionCharges)) {
		  Long amountAfterCashOut=decreaseBalance(userCurrentBalance,transaction.getCashOut(), transactionCharges);	
		  updateBalance(userCurrentBalance, amountAfterCashOut);
		  transact=transactionRepository.save(transaction);
		 }
		 
		} catch (Exception e) {
		 e.getStackTrace();
		}
		return transact;
	}

	@Override
	public Balance userBalance(User user) {
	  return balanceRepository.findByUser(user);
		
	}
	
	public Long increaseBalance(Balance balance,Long amount) {
		Long totalAmount=balance.getBalance()+amount;
		return totalAmount;
	}
	
	public Long decreaseBalance(Balance balance,Long amount,Long charges) {
		Long totalAmount=balance.getBalance()-amount-charges;
		return totalAmount;
	}

	public boolean isEnoughBalance(Balance balance,Long amount,Long charges) {
		boolean valid=false;
		if(balance.getBalance()>=(amount+charges)) {
			valid=true;
		}
		return valid;
	}
	public Balance updateBalance(Balance balance,Long amount) {
		balance.setBalance(amount);
		balance.setUpdatedAt(LocalDateTime.now());
	 return balanceRepository.save(balance);
	}

	@Override
	public List<Transaction> findTransactionsByUser(User user) {
	  return transactionRepository.findByUser(user);
	}
}
