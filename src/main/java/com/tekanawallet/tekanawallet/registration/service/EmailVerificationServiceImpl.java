package com.tekanawallet.tekanawallet.registration.service;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.tekanawallet.tekanawallet.registration.model.User;
import com.tekanawallet.tekanawallet.registration.model.VerificationToken;
import com.tekanawallet.tekanawallet.registration.repository.VerificationTokenRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service

public class EmailVerificationServiceImpl implements EmailVerificationService{
	 private final static Logger LOGGER = LoggerFactory
	            .getLogger(EmailVerificationService.class);
	@Autowired
	VerificationTokenRepository verificationTokenRepo;
	
	@Autowired
	private  JavaMailSender mailSender;
	
	@Value("${email.tokenValidationHours}")
	private int expirationMinute;
	
	@Value("${email.sender}")
	private String emailSender;

	@Override
	public VerificationToken createVerificationToken(User user, String token) {
		VerificationToken userToken=new VerificationToken();
		userToken.setUser(user);
		userToken.setToken(token);
		userToken.setCreatedAt(LocalDateTime.now());
		userToken.setExpiryDate(LocalDateTime.now().plusHours(expirationMinute));
	 return  verificationTokenRepo.save(userToken);
	}

	@Override
	public void sendHtmlEmail(String to,String subject,String message) {
		try {
			 MimeMessage mimeMessage = mailSender.createMimeMessage();
			    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);	     
			    helper.setFrom(emailSender);
			    helper.setTo(to);
			    helper.setSubject(subject);
			    helper.setText(message, true);
			    mailSender.send(mimeMessage);
		} catch (Exception e) {
			 LOGGER.error("failed to send email", e);
	            System.out.println("OOOOOH"+e.getMessage());
	            throw new IllegalStateException("failed to send email");
		}
	}
	@Override
	public void sendSimpleMail(String to,String subject, String message) {
        try {
        	SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        	  simpleMailMessage.setTo(to);
        	  simpleMailMessage.setSubject(subject);
        	  simpleMailMessage.setText(message);
        	  simpleMailMessage.setFrom(emailSender);
            mailSender.send(simpleMailMessage);
        } catch (Exception e) {
            LOGGER.error("failed to send email", e);
            System.out.println("OOOOOH"+e.getMessage());
            throw new IllegalStateException("failed to send email");
        }
    }
	
	@Override
	public VerificationToken findByUser(User user) {
		return verificationTokenRepo.findByUser(user);
	}

	@Override
	public VerificationToken findByToken(String token) {
	return	verificationTokenRepo.findByToken(token);
	}

	@Override
	public VerificationToken updateToken(VerificationToken token) {
		return verificationTokenRepo.save(token);
	}

}
