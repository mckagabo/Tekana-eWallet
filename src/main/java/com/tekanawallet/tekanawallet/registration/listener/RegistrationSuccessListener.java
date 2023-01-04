package com.tekanawallet.tekanawallet.registration.listener;



import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Component;

import com.tekanawallet.tekanawallet.dto.UserDto;
import com.tekanawallet.tekanawallet.registration.event.OnRegistrationCompleteEvent;
import com.tekanawallet.tekanawallet.registration.model.User;
import com.tekanawallet.tekanawallet.registration.service.EmailVerificationService;
import com.tekanawallet.tekanawallet.registration.service.UserService;


@Component
public class RegistrationSuccessListener implements ApplicationListener<OnRegistrationCompleteEvent>{
	@Autowired
	EmailVerificationService verificationService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	private JavaMailSender javaMailSender;
	
	@Value("${site.base.url}")
	private String baseUrl;
	
	
	
	

	@Override
	public void onApplicationEvent(OnRegistrationCompleteEvent event) {
		this.confirmRegistration(event);
		
	}
	
	private static final BytesKeyGenerator DEFAULT_TOKEN_GENERATOR=KeyGenerators.secureRandom(100);

	private void confirmRegistration(OnRegistrationCompleteEvent event) {
		 User user = event.getUser();
	     String token = new String(Base64.encodeBase64URLSafe(DEFAULT_TOKEN_GENERATOR.generateKey()));
	     UserDto userDto=userService.convertUserToDto(user);
	    	 verificationService.createVerificationToken(user, token);
		     String recipientAddress = user.getEmail();
		        String confirmationUrl= baseUrl+event.getAppUrl() + "/activation?token=" + token;
		        String subject="Account activation";
		        String messageContent = "Dear [[name]],<br>"
		                + "Thank you for registering with us please proceed to activate your account by clicking bellow:<br>"
		                + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
		                + "Thank you,<br>"
		                + "Tekana e-Wallet";
		        String message=messageContent.replace("[[name]]", userDto.getFirstName());
		        message=message.replace("[[URL]]", confirmationUrl);
		       verificationService.sendHtmlEmail(recipientAddress,subject,message);
		        
		      	        
		
		
	}

}
