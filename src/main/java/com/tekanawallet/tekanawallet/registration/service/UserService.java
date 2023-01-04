package com.tekanawallet.tekanawallet.registration.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.core.userdetails.UserDetails;

import com.tekanawallet.tekanawallet.dto.UserDto;
import com.tekanawallet.tekanawallet.registration.model.Balance;
import com.tekanawallet.tekanawallet.registration.model.User;

import tekanawallet.tekanawallet.enums.ERoles;

public interface UserService{
	  
	   

	    User findByEmail(String email);

	    List<UserDto> findAllUsers();

		User saveUser(UserDto userDto, ERoles userRole);
		
		User updateUser(User user);
	    
	    Balance saveBalance(User user,long balance);
	    
	    User findByUsername(String username);
	    
	    Optional<User> findById(UUID id);
	    
	    UserDto convertUserToDto(User user);

}
