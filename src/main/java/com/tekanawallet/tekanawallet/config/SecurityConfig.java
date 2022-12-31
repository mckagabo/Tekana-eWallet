package com.tekanawallet.tekanawallet.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.tekanawallet.tekanawallet.registration.service.UserDetailsImpl;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	 @Autowired
	 private  UserDetailsImpl userDetailsService;
	 
	 @Autowired
	 private AuthEntryPointJwt unauthorizedHandler;
	    @Bean
	    public AuthenticationManager authenticationManager(
	            final AuthenticationConfiguration authenticationConfiguration) throws Exception {
	        return authenticationConfiguration.getAuthenticationManager();
	    }
	 
	 
	 @Autowired
	private JwtRequestFilter jwtRequestFilter;
	 
	  @Bean
	  public DaoAuthenticationProvider authenticationProvider() {
	      DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
	       
	      authProvider.setUserDetailsService(userDetailsService);
	      authProvider.setPasswordEncoder(passwordEncoder());
	   
	      return authProvider;
	  }
	  
	@Bean
    public static PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
	// @Bean
	//  public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
	 //   return authConfig.getAuthenticationManager();
	 // }
	
	  @Bean
	  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		 return http
				 .csrf(csrf->csrf.disable())
				 .authorizeHttpRequests(auth->{
					 auth.requestMatchers("/","/api/auth/signup").permitAll();
					 auth.requestMatchers("/","/api/auth/signin").permitAll();
					// auth.requestMatchers("/","/api/auth/users").permitAll();
					// auth.requestMatchers("/","/api/payments/externalpay").permitAll();
					 auth.anyRequest().authenticated();
				 }) 
		         .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
		        .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
			     .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
				 .build();
				 
		  
	  }
	  
	   
	
	
}
