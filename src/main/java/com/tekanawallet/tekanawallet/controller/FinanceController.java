package com.tekanawallet.tekanawallet.controller;


import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tekanawallet.tekanawallet.dto.ExternalPaymentDto;
import com.tekanawallet.tekanawallet.dto.ExternalTransferDto;
import com.tekanawallet.tekanawallet.dto.MtnPayee;
import com.tekanawallet.tekanawallet.dto.MtnTransfer;
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
	  
	  @Autowired
	  private PlatformTransactionManager transactionManager;
	  
	
	  
	  @Value("${momo.apiKey}")
	  
	  private String mtnSecret;
	  
	  @Value("${momo.userId}")
	  private String mtnUsername;
	  
	  @Value("${momo.url}")
	  private String momoUrl;
	  
	  @Value("${momo.subscriptionKey}")
	  private String momoSubKey;
	  
	 
	  
	
	@PostMapping("/send")
	public  ResponseEntity<?> sendMoney(@Valid @RequestBody PaymentDto payment){
		Random random = new Random(); 
		  long refNumber = random.nextLong(9999999); 
		Transaction cashOutransaction=new Transaction();
		Transaction cashInTransaction=new Transaction();
		String transactionRef="TKN"+refNumber;
		User sender=userRepository.findByUserAccount(payment.getSender());
		User reciever=userRepository.findByUserAccount(payment.getReceiver());
		if(sender==null) {
		 return ResponseEntity.status(400).body("Fund sender not found"); 
		}
		cashOutransaction.setAccountOwner(sender);
		cashOutransaction.setDescription(payment.getDescription());
		cashOutransaction.setUserAccount(payment.getSender());
		cashOutransaction.setCashOut(payment.getAmount());
		cashOutransaction.setTransactionTime(LocalDateTime.now());
		cashOutransaction.setCurrency(payment.getCurrency());
		cashOutransaction.setInternalReference(transactionRef);
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
		cashInTransaction.setInternalReference(transactionRef);
		if(paymentService.cashIn(cashInTransaction)==null) {
			return ResponseEntity.status(500).body("Problem with fund transfer");
		}
		return ResponseEntity.ok().body(payment.getAmount()+""
				+ " succesfully transfered to: "+payment.getReceiver());	
		}

	  @PostMapping("/externalPay")
	   public  ResponseEntity<?> cashIn(@Valid @RequestBody ExternalPaymentDto payment){
		  User reciever=userRepository.findByUserAccount(payment.getReciever());
		  Transaction cashInTransaction=new Transaction();
		  if(reciever==null) {
				 return ResponseEntity.status(400).body("Fund receiver doen't exist"); 
			}
		  cashInTransaction.setAccountOwner(reciever);
			cashInTransaction.setCashIn(payment.getAmount());
			cashInTransaction.setCurrency("Frw");
			cashInTransaction.setDescription(payment.getDescription());
			cashInTransaction.setTransactionTime(LocalDateTime.now());
			cashInTransaction.setExternalReference(payment.getExternalReference());
			if(paymentService.cashIn(cashInTransaction)==null) {
			  return ResponseEntity.status(500).body("Problem with fund transfer");
			}
			return ResponseEntity.ok().body(payment.getAmount()+","
					+ " succesfully transfered to: "+payment.getReciever());
			
	   }
	  
	  @PostMapping("/externalTransfer")
	  public ResponseEntity<?> cashOut(@Valid @RequestBody ExternalTransferDto payment){
		  Random random = new Random(); 
		  long refNumber = random.nextLong(9999999); 
		  ResponseEntity<?> response=null;
		  String identificationNumber=payment.getIdentifier();
		  User sender=userRepository.findByUserAccount(payment.getSender());
		  
		  if(sender==null) {
			  return ResponseEntity.status(400).body("Fund sender not found"); 
				}
		 
		  TransactionStatus trans=this.transactionManager.getTransaction(null);
		  try {
			  Transaction cashOutransaction=new Transaction();
			  Transaction transactionResults=null;
			  cashOutransaction.setAccountOwner(sender);
			  cashOutransaction.setDescription(payment.getDescription());
			  cashOutransaction.setUserAccount(payment.getSender());
				cashOutransaction.setCashOut(payment.getAmount());
				cashOutransaction.setTransactionTime(LocalDateTime.now());
				cashOutransaction.setCurrency("Frw"); 
				cashOutransaction.setExternalReference(payment.getDestination()+"-TKN-"+refNumber);
				transactionResults=paymentService.cashOut(cashOutransaction);
				if(transactionResults==null) {
					return ResponseEntity.status(400).body("Insuficient fund");	
					}
				
				 if(payment.getDestination().equalsIgnoreCase("mtn")) {
					 response=sendToMomo(transactionResults,identificationNumber);
					 if(response.getStatusCode().is2xxSuccessful()) {
						 this.transactionManager.commit(trans);	
						
						 return ResponseEntity.ok().body(response);
						
					 }
					
			       return ResponseEntity.status(400).body(response);
					 
					
				 }else if(payment.getDestination().equalsIgnoreCase("airtel")) {
					 
				 }
			   return ResponseEntity.status(200).body("It is well");
		} catch (Exception e) {
			 this.transactionManager.rollback(trans);
			 return ResponseEntity.status(400).body("Transaction failed");
		}
		 
	  }
	  
	  public ResponseEntity<?> sendToAirTel(Transaction transaction,String phoneNumber){
		return null;
		  
	  }
	  
	  public ResponseEntity<?> sendToMomo(Transaction transaction,String phoneNumber) {
		  MtnTransfer momoObject= new  MtnTransfer();
		  MtnPayee payee=new MtnPayee();
		  ResponseEntity<?> resp=null;
		  RestTemplate restTemplate= new RestTemplate();
		 
		  HttpHeaders headers=getHeaders();
		  HttpHeaders httpHeaders=new HttpHeaders();
		  String auth = mtnUsername + ":" + mtnSecret;
		  byte[] encodedAuth = Base64.getEncoder().encode( 
		            auth.getBytes(Charset.forName("US-ASCII")));
		 payee.setPartyId(phoneNumber);
		 payee.setPartyIdType("MSISDN");
		 momoObject.setAmount(transaction.getCashOut().toString());
		 momoObject.setCurrency("EUR");
		 momoObject.setPayerMessage(transaction.getDescription());
		 momoObject.setPayee(payee);
		 momoObject.setExternalId(transaction.getExternalReference());
		 momoObject.setPayeeNote(transaction.getDescription());
		 
		  try {
			         String authHeader = "Basic " + new String( encodedAuth );
			         headers.set("Authorization", authHeader);
			         headers.set("Ocp-Apim-Subscription-Key", momoSubKey);
			         HttpEntity<String> headerEntity = new HttpEntity<String>(headers);
			         ResponseEntity<String> authResponse = restTemplate.exchange(momoUrl+"/remittance/token/", HttpMethod.POST, headerEntity,
							String.class);
			 		
			 		JSONObject json=new JSONObject(authResponse.getBody());
			 		String accessToken=json.getString("access_token");
			 		httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			 		httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			 		httpHeaders.set("Authorization","Bearer "+accessToken);
			 		httpHeaders.set("X-Reference-Id", transaction.getId().toString());
			 		httpHeaders.set("X-Target-Environment", "sandbox");
			 		httpHeaders.set("Content-Type", "application/json");
			 		httpHeaders.set("Ocp-Apim-Subscription-Key", momoSubKey);
			 	
		   		    String momoBody=getMomoBody(momoObject);
		   		 
			 		 HttpEntity<String> momoheader = new HttpEntity<String>(momoBody,httpHeaders);
			 		 RestTemplate momoRequest=new RestTemplate();
			 	     ResponseEntity<String> momoResponse=momoRequest.exchange(momoUrl+"/remittance/v1_0/transfer", 
			 		 HttpMethod.POST, momoheader, String.class); 
			 	   
			 		System.out.println("HAHAHAHA"+momoResponse);
			 	    resp=ResponseEntity.ok().body(momoheader);
		} catch (Exception e) {
			System.out.println("CXOMLSL"+e.getMessage());
			resp=ResponseEntity.status(500).body(""+e.getMessage());
		}
		  
		  
		  return resp;
		  
	  }
	  
	  private String getMomoBody(final MtnTransfer momoDto) throws JsonProcessingException {
			return new ObjectMapper().writeValueAsString(momoDto);
		}

		private HttpHeaders getHeaders() {
			HttpHeaders headers = new HttpHeaders();
			headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
			headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
			return headers;
		}
}
