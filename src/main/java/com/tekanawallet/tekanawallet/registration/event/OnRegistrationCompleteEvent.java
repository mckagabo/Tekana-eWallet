package com.tekanawallet.tekanawallet.registration.event;

import java.util.Locale;

import org.springframework.context.ApplicationEvent;

import com.tekanawallet.tekanawallet.registration.model.User;

public class OnRegistrationCompleteEvent extends ApplicationEvent{
	private static final long serialVersionUID = -2550185165626007488L;
	    private String appUrl;
	  
	    private User user;
	    
	    public OnRegistrationCompleteEvent(
	    	      User user,  String appUrl) {
	    	        super(user);
	    	        
	    	        this.user = user;
	    	      
	    	        this.appUrl = appUrl;
	    	    }

		public String getAppUrl() {
			return appUrl;
		}

		public void setAppUrl(String appUrl) {
			this.appUrl = appUrl;
		}

		

		public User getUser() {
			return user;
		}

		public void setUser(User user) {
			this.user = user;
		}
	    
	    

}
