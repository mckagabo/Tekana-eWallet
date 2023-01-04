package com.tekanawallet.tekanawallet.registration.service;

import com.tekanawallet.tekanawallet.registration.model.User;
import com.tekanawallet.tekanawallet.registration.model.VerificationToken;

public interface EmailVerificationService {
	VerificationToken createVerificationToken(User user,String token);
	
	VerificationToken findByUser(User user);
	
	VerificationToken findByToken(String token);
	
	void sendSimpleMail(String to,String subject, String message);
	
	void sendHtmlEmail(String to,String subject, String message);
	
	VerificationToken updateToken(VerificationToken token);
	

}
