package com.tekanawallet.tekanawallet.registration.listener;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.tekanawallet.tekanawallet.dto.UserDto;
import com.tekanawallet.tekanawallet.registration.event.OnReactivationRequest;
import com.tekanawallet.tekanawallet.registration.model.VerificationToken;
import com.tekanawallet.tekanawallet.registration.service.EmailVerificationService;
import com.tekanawallet.tekanawallet.registration.service.UserService;

@Component
public class ReactivationRequestListener implements ApplicationListener<OnReactivationRequest>{

	@Autowired
	EmailVerificationService verificationService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	private JavaMailSender javaMailSender;
	
	@Value("${site.base.url}")
	private String baseUrl;
	
	@Value("${email.tokenValidationHours}")
	private int expirationHours;
	
	@Override
	public void onApplicationEvent(OnReactivationRequest event) {	
		this.resendActivationLink(event);
	}
	
	private void resendActivationLink(OnReactivationRequest event) {
		VerificationToken token=event.getToken();
		
		token.setExpiryDate(LocalDateTime.now().plusHours(expirationHours));
		VerificationToken updatedToken=verificationService.updateToken(token);
		 UserDto userDto=userService.convertUserToDto(updatedToken.getUser());
		String subject="Account Reactivation";
		String confirmationUrl=baseUrl+event.getApUrl()+"/activation?token=" + updatedToken.getToken();
		String messageContent = "Dear [[name]],<br>"
                + "Thank you for registering with us please proceed to activate your account by clicking bellow:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">"
                + "<button type=\"button\" style=\"font: bold 14px Arial;color:white;background-color:green\">ACTIVATE ACCOUNT </button></a></h3>"
                + "Thank you,<br>"
                + "Tekana e-Wallet";
		 String message=messageContent.replace("[[name]]", userDto.getFirstName());
	        message=message.replace("[[URL]]", confirmationUrl);
	       verificationService.sendHtmlEmail(userDto.getEmail(),subject,message);
	}

}
