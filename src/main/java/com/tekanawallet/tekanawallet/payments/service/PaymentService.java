package com.tekanawallet.tekanawallet.payments.service;

import java.util.List;
import java.util.UUID;

import com.tekanawallet.tekanawallet.registration.model.Balance;
import com.tekanawallet.tekanawallet.registration.model.Transaction;
import com.tekanawallet.tekanawallet.registration.model.User;

public interface PaymentService {
     List<Transaction> findTransactionsByUser(User user);
     
     Transaction cashIn(Transaction transaction);
     
     Transaction cashOut(Transaction transaction);
     
     Balance userBalance(User user);
     
	
}
