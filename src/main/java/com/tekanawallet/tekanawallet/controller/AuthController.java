package com.tekanawallet.tekanawallet.controller;

import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.tekanawallet.tekanawallet.config.JwtTokenUtil;
import com.tekanawallet.tekanawallet.dto.JwtResponse;
import com.tekanawallet.tekanawallet.dto.LoginDto;
import com.tekanawallet.tekanawallet.dto.UserDto;
import com.tekanawallet.tekanawallet.registration.model.User;
import com.tekanawallet.tekanawallet.registration.repository.RoleRepository;
import com.tekanawallet.tekanawallet.registration.repository.UserRepository;
import com.tekanawallet.tekanawallet.registration.service.UserDetailsImpl;
import com.tekanawallet.tekanawallet.registration.service.UserService;

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
	  PasswordEncoder encoder;

	  @Autowired
	  JwtTokenUtil jwtUtils;
	  
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
		   
		  try {
			  if (userRepository.existsByEmail(userDto.getEmail())) {
			      return ResponseEntity.badRequest().body("Error: Email already exist!");
			    }
			  if(userRepository.existsByUserAccount(userDto.getUserName())) {
				 return ResponseEntity.badRequest().body("ERRROR: Username taken");
			  }
			 
			  User user=userService.saveUser(userDto,ERoles.CUSTOMER);
			  UserDto userDtoResponse=userService.convertUserToDto(user);
			  return ResponseEntity.ok().body(userDtoResponse);
		
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Sorry"+e.getMessage());
		}
	  }
	  
	  @RequestMapping(value="/user", method = RequestMethod.GET)
	  // @GetMapping("/user")
	    @ResponseBody
	    public  ResponseEntity<?>  findUser(@RequestParam("id") String id) {
	    	
	    	UUID userId=UUID.fromString(id);
	    	User u=userService.findById(userId).orElse(null);;
	    	
	    	if(u==null) {
	    		return ResponseEntity.status(400).body("User not found");	
	    	}
	    	
	    
	    	UserDto userDto=userService.convertUserToDto(u);
	    	return ResponseEntity.ok().body(userDto);
	    	
	    }
	  
	 
    @GetMapping("/users")
   @ResponseBody
   public List<UserDto> allUsers(){
	   return userService.findAllUsers();
   }
	  
   
	  
}
