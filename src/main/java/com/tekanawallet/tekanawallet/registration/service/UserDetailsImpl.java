package com.tekanawallet.tekanawallet.registration.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.tekanawallet.tekanawallet.registration.model.Role;
import com.tekanawallet.tekanawallet.registration.model.User;
import com.tekanawallet.tekanawallet.registration.repository.UserRepository;

@Service
public class UserDetailsImpl implements UserDetailsService{
    @Autowired
    private UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user=userRepository.findByEmail(username);
		
		if (user == null) {
			throw new UsernameNotFoundException("User not found with username: " + username);
		}
		List<GrantedAuthority> authorities =user.getRoles().stream()
		        .map(role -> new SimpleGrantedAuthority(role.getName().name()))
		        .collect(Collectors.toList());
		
		//boolean accountNonExpired = true;
	    boolean credentialsNonExpired = true;
	    boolean accountNonLocked = true;
		//return new UserDetails(user.getEmail(), user.getPassword(), true, true, true, true, authorities);
	   
		return 	new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
				user.getIsEnabled(),user.getAccountStatus(),credentialsNonExpired, accountNonLocked, authorities);
		
		
                
	}
	
	
  public List<GrantedAuthority> UserRoles(String username){
	  List <GrantedAuthority> authorities=null;
	  User user=userRepository.findByEmail(username); 
	  if (user == null) {
			throw new UsernameNotFoundException("User not found with username: " + username);
		}
	  authorities=user.getRoles().stream()
		        .map(role -> new SimpleGrantedAuthority(role.getName().name()))
		        .collect(Collectors.toList());
	  return authorities;
  }



}
