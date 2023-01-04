package com.tekanawallet.tekanawallet.registration.event;

import org.springframework.context.ApplicationEvent;

import com.tekanawallet.tekanawallet.registration.model.VerificationToken;

public class OnReactivationRequest  extends ApplicationEvent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
    private VerificationToken token;
    private String apUrl;
    

	public OnReactivationRequest(VerificationToken resendToken,String url) {
		 super(resendToken);
		 this.token=resendToken;
		 this.apUrl=url;
	}

	public VerificationToken getToken() {
		return token;
	}

	public void setToken(VerificationToken token) {
		this.token = token;
	}

	public String getApUrl() {
		return apUrl;
	}

	public void setApUrl(String apUrl) {
		this.apUrl = apUrl;
	}
    
    
    
    

}
