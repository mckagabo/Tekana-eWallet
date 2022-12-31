package com.tekanawallet.tekanawallet.controller;


import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tekanawallet.tekanawallet.dto.PaymentDto;
import com.tekanawallet.tekanawallet.dto.UserDto;
import com.tekanawallet.tekanawallet.payments.service.PaymentService;
import com.tekanawallet.tekanawallet.registration.model.Transaction;
import com.tekanawallet.tekanawallet.registration.model.User;
import com.tekanawallet.tekanawallet.registration.repository.UserRepository;
import com.tekanawallet.tekanawallet.registration.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/payments")
public class FinanceController {
	
	 @Autowired
	  UserRepository userRepository;
	  
	  @Autowired 
	  UserService userService;
	  
	  @Autowired
	  PaymentService paymentService;
	
	
	@PostMapping("/send")
	public  ResponseEntity<?> sendMoney(@Valid @RequestBody PaymentDto payment){
		Transaction cashOutransaction=new Transaction();
		Transaction cashInTransaction=new Transaction();
		
		User sender=userRepository.findByEmail(payment.getSender());
		User reciever=userRepository.findByEmail(payment.getReceiver());
		if(sender==null) {
		 return ResponseEntity.status(400).body("Fund sender not found"); 
		}
		cashOutransaction.setAccountOwner(sender);
		cashOutransaction.setDescription(payment.getDescription());
		cashOutransaction.setUserAccount(payment.getSender());
		cashOutransaction.setCashOut(payment.getAmount());
		cashOutransaction.setTransactionTime(LocalDateTime.now());
		cashOutransaction.setCurrency(payment.getCurrency());
		if(paymentService.cashOut(cashOutransaction)==null) {
		 return ResponseEntity.status(400).body("Insuficient fund");	
		}
		if(reciever==null) {
			 return ResponseEntity.status(400).body("Fund receiver doen't exist"); 
		}
		cashInTransaction.setAccountOwner(reciever);
		cashInTransaction.setCashIn(payment.getAmount());
		cashInTransaction.setCurrency(payment.getCurrency());
		cashInTransaction.setDescription(payment.getDescription());
		cashInTransaction.setTransactionTime(LocalDateTime.now());
		if(paymentService.cashIn(cashInTransaction)==null) {
			return ResponseEntity.status(400).body("Problem with fund transfer");
		}
		return ResponseEntity.ok().body(payment.getAmount()+""
				+ "succesfully transfered to"+payment.getReceiver());	
		}

	@GetMapping("/externaPay")
	public  ResponseEntity<?> cashIn(){
		return ResponseEntity.ok().body("Yupiii");
	}
}
