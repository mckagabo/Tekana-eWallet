package com.tekanawallet.tekanawallet.controller;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import com.tekanawallet.tekanawallet.config.JwtTokenUtil;
import com.tekanawallet.tekanawallet.dto.JwtResponse;
import com.tekanawallet.tekanawallet.dto.LoginDto;
import com.tekanawallet.tekanawallet.dto.ReactivationDto;
import com.tekanawallet.tekanawallet.dto.UserDto;
import com.tekanawallet.tekanawallet.registration.event.OnReactivationRequest;
import com.tekanawallet.tekanawallet.registration.event.OnRegistrationCompleteEvent;
import com.tekanawallet.tekanawallet.registration.model.User;
import com.tekanawallet.tekanawallet.registration.model.VerificationToken;
import com.tekanawallet.tekanawallet.registration.repository.RoleRepository;
import com.tekanawallet.tekanawallet.registration.repository.UserRepository;
import com.tekanawallet.tekanawallet.registration.service.EmailVerificationService;
import com.tekanawallet.tekanawallet.registration.service.UserDetailsImpl;
import com.tekanawallet.tekanawallet.registration.service.UserService;

import io.jsonwebtoken.impl.DefaultClaims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import tekanawallet.tekanawallet.enums.ERoles;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
	
	 @Autowired
	  AuthenticationManager authenticationManager;

	  @Autowired
	  UserRepository userRepository;
	  
	  @Autowired 
	  UserService userService;
	  
	  @Autowired
	  UserDetailsImpl userDetailsService;

	  @Autowired
	  RoleRepository roleRepository;
	  
	  @Autowired
	  EmailVerificationService verificationService;

	  @Autowired
	  PasswordEncoder encoder;

	  @Autowired
	  JwtTokenUtil jwtUtils;
	  
	  @Autowired
	  ApplicationEventPublisher eventPublisher;
	  
	  @GetMapping(value = "/refreshtoken")
		public ResponseEntity<?> refreshtoken(HttpServletRequest request) throws Exception {
			// From the HttpRequest get the claims
			DefaultClaims claims = (io.jsonwebtoken.impl.DefaultClaims) request.getAttribute("claims");

			Map<String, Object> expectedMap = getMapFromIoJsonwebtokenClaims(claims);
			String token = jwtUtils.generateRefreshToken(expectedMap, expectedMap.get("sub").toString());
			return ResponseEntity.ok(token);
		}
	  
	  public Map<String, Object> getMapFromIoJsonwebtokenClaims(DefaultClaims claims) {
			Map<String, Object> expectedMap = new HashMap<String, Object>();
			for (Entry<String, Object> entry : claims.entrySet()) {
				expectedMap.put(entry.getKey(), entry.getValue());
			}
			return expectedMap;
		}
	  @PostMapping("/signin")
	  public ResponseEntity<?> createAuthenticationToken(@Valid @RequestBody LoginDto loginRequest) {
		    try {
	            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
	                    loginRequest.getUsername(), loginRequest.getPassword()));
	          
	        } catch (final BadCredentialsException ex) {
	        return ResponseEntity.status(400).body("Invalid email or password"); 	
	           // throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
	        }
	        	
		 
		   final UserDetails userDetails = userDetailsService
					.loadUserByUsername(loginRequest.getUsername());
		  
		    if(userDetails!=null) {
		   final String token  = jwtUtils.generateTokenFromUsername(userDetails);
		  
		   return ResponseEntity.ok(new JwtResponse(token));
		    }
		 
		 return ResponseEntity.status(400).body("Invalid credentials"); 
		     
	  }
	  
	  @PostMapping("/signup")
	  public ResponseEntity<?> registerUser(@Valid @RequestBody UserDto userDto){
		
	        String contextPath = "/api/auth";		 
		  try {
			  if (userRepository.existsByEmail(userDto.getEmail())) {
			      return ResponseEntity.badRequest().body("Error: Email already exist!");
			    }
			  if(userRepository.existsByUserAccount(userDto.getUserName())) {
				 return ResponseEntity.badRequest().body("ERRROR: Username taken");
			  }
			  
			  User user=userService.saveUser(userDto,ERoles.CUSTOMER);
			
			  eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user,contextPath));
			  UserDto userDtoResponse=userService.convertUserToDto(user);
			  return ResponseEntity.ok().body(userDtoResponse);
		
		} catch (Exception e) {
			System.out.println("OOOOOOOH YAEAR"+e.getCause());
			return ResponseEntity.status(500).body("Ikibazo:"+e.getMessage());
		}
	  }
	  
	  @RequestMapping(value="/user", method = RequestMethod.GET)
	  // @GetMapping("/user")
	    @ResponseBody
	    public  ResponseEntity<?>  findUser(@RequestParam("id") String id) {
	    	
	    	UUID userId=UUID.fromString(id);
	    	User u=userService.findById(userId).orElse(null);
	    	
	    	if(u==null) {
	    		return ResponseEntity.status(400).body("User not found");	
	    	}
	    	
	    
	    	UserDto userDto=userService.convertUserToDto(u);
	    	return ResponseEntity.ok().body(userDto);
	    	
	    }
	   @GetMapping("/activation")
	   public ResponseEntity<?>  activateAccount(WebRequest request, Model model, @RequestParam("token") String token) {
		 VerificationToken verificationToken= verificationService.findByToken(token);
		 if(verificationToken==null) {
			 model.addAttribute("message", "Invalid token");
			return ResponseEntity.status(400).body("Invalid token");
		 }
		 User user=verificationToken.getUser();
		
		 if(verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
			 model.addAttribute("message", "Token expired");
			 return ResponseEntity.status(400).body("Token expired");
		 }
		 user.setIsEnabled(true); 
		  userService.updateUser(user);
		  verificationToken.setConfirmedAt(LocalDateTime.now());
		  verificationToken.setToken(null);
		 VerificationToken verified= verificationService.updateToken(verificationToken);
		  return ResponseEntity.ok().body("Account succesfuly activated at:"+verified.getConfirmedAt());
		  
	   }
	   
	   @PostMapping("/resendActivation")
	   public ResponseEntity<?> resendActivationEmail(@Valid @RequestBody ReactivationDto emailDto){
		   String email=emailDto.getEmail();
		   User user=userService.findByEmail(email);
		   String contextPath = "/api/auth";
		   if(user==null) {
			 return ResponseEntity.status(400).body("Account not registered");   
		   }
		   VerificationToken token=verificationService.findByUser(user);
		   if(token.getToken()==null) {
			   return ResponseEntity.status(400).body("The account is already activated");   
		   }
		   eventPublisher.publishEvent(new OnReactivationRequest(token,contextPath));
		   return ResponseEntity.ok().body("Activation link resent");
	   }
	 
       @GetMapping("/users")
       @ResponseBody
      public List<UserDto> allUsers(){
	   return userService.findAllUsers();
      }
	  
   
	  
}
