package com.tekanawallet.tekanawallet.registration.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tekanawallet.tekanawallet.dto.RoleDto;
import com.tekanawallet.tekanawallet.dto.TransactionDto;
import com.tekanawallet.tekanawallet.dto.UserDto;
import com.tekanawallet.tekanawallet.payments.repository.BalanceRepository;
import com.tekanawallet.tekanawallet.registration.model.Balance;
import com.tekanawallet.tekanawallet.registration.model.Role;
import com.tekanawallet.tekanawallet.registration.model.Transaction;
import com.tekanawallet.tekanawallet.registration.model.User;
import com.tekanawallet.tekanawallet.registration.repository.RoleRepository;
import com.tekanawallet.tekanawallet.registration.repository.UserRepository;

import tekanawallet.tekanawallet.enums.EGender;
import tekanawallet.tekanawallet.enums.ERoles;

@Service
public class UserServiceImpl implements UserService{
	 private UserRepository userRepository;
	 private RoleRepository roleRepository; 
	 @Autowired
	 private BalanceRepository balanceRepository;
	 private PasswordEncoder passwordEncoder;
	 
	 
	   public UserServiceImpl(UserRepository userRepository,
               RoleRepository roleRepository,
               PasswordEncoder passwordEncoder) {
this.userRepository = userRepository;
this.roleRepository = roleRepository;
this.passwordEncoder = passwordEncoder;
}
	
	@Override
	public User saveUser(UserDto userDto,ERoles userRole) {
		 DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy");
		 User registeredUser=null;
		try {
			 LocalDate dob = LocalDate.parse(userDto.getDob(), formatter);
				long startingBalance= 0;
				User user = new User();
		        user.setNames(userDto.getFirstName() + " " + userDto.getLastName());
		        user.setEmail(userDto.getEmail());
		        user.setGender(showGender(userDto.getGender()));
		        user.setAccountStatus(true);
		        user.setIsEnabled(false);
		        user.setDob(dob);
		        user.setUserAccount(userDto.getUserName());
		        user.setRegistrationTime(LocalDateTime.now());
		        //encrypt the password once we integrate spring security   
		        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
		        Role role = roleRepository.findByName(userRole);
		        if(role == null){
		            role = registerNewRole(userRole);
		        }
		        user.setRoles(Arrays.asList(role));       
		        registeredUser=userRepository.save(user);
		        user.setBalance(saveBalance(registeredUser,startingBalance));
		        return user;
		} catch (Exception e) {
			
			e.getMessage();
		}
		return registeredUser;
	}
 
	public EGender showGender(String gender) {
		EGender sex=null;
		if(gender.equalsIgnoreCase("male")) {
			sex=EGender.MALE;
		}else if(gender.equalsIgnoreCase("female")) {
			sex=EGender.FEMALE;
		}
		return sex;
	}
	
	@Override
	public User findByEmail(String email) {
	  User user=userRepository.findByEmail(email);
	  if(user!=null) {
		  return user;
	  }
	  return null;
	}

	@Override
	public List<UserDto> findAllUsers() {
	
	 List<UserDto> users=userRepository.findAll().stream().map(user->{
		 UserDto userDto=new UserDto();
		 userDto.setId(user.getUserId());
		 userDto.setGender(user.getGender().toString());
		 userDto.setUserName(user.getUserAccount());
		 userDto.setDob(user.getDob().toString());
		 userDto.setEmail(user.getEmail());
		 userDto.setFirstName(retrieveFirstName(user.getNames()));
		 userDto.setLastName(retrieveLastName(user.getNames()));
		 userDto.setRoles(transforRoles(user.getRoles()));
		 userDto.setTransactions(transformList(user.getTransactions()));
		 userDto.setBalance(user.getBalance().getBalance());
		 return userDto;
	 }).collect(Collectors.toList());
	 
	 return users;
	}
	
	@Override
	public UserDto convertUserToDto(User user) {
		  ModelMapper modelMapper = new ModelMapper();
		    UserDto userDto = modelMapper.map(user, UserDto.class);
		    userDto.setFirstName(retrieveFirstName(user.getNames()));
		    userDto.setLastName(retrieveLastName(user.getNames()));
		    userDto.setDob(user.getDob().toString());
		    userDto.setRoles(transforRoles(user.getRoles()));
		    userDto.setTransactions(transformList(user.getTransactions()));
		    userDto.setUserName(user.getUserAccount());
		    userDto.setBalance(user.getBalance().getBalance());
		    userDto.setPassword(null);
		       
		    return userDto;
		}
	  
	public String retrieveFirstName(String name) {
		String arr[]=name.split(" ", 2);
		return arr[0];
	}
	public String retrieveLastName(String name) {
		String arr[]=name.split(" ", 2);
		return arr[1];
	}
	
	public List<RoleDto>transforRoles(List<Role> roles){
		List<RoleDto> rol=roles.stream().map(role->{
			RoleDto dto=new RoleDto();
			dto.setId(role.getId());
			dto.setName(role.getName().toString());
			return dto;
		}).collect(Collectors.toList());
		if(rol==null) {
			List<RoleDto> jobs=new ArrayList<>();
		 return jobs;	
		}
		return rol;
	}
	public List<TransactionDto> transformList(List<Transaction> transactions){
		List<TransactionDto> trans=new ArrayList<>();
		if(transactions!=null)	{
		 trans=	transactions.stream().map(transaction->{
				   TransactionDto dto=new TransactionDto();
				   dto.setId(transaction.getId());
				   dto.setCashIn(transaction.getCashIn());
				   dto.setCashOut(transaction.getCashOut());
				   dto.setCurrency(transaction.getCurrency());
				   dto.setDescription(transaction.getDescription());
				   dto.setTransactionTime(transaction.getTransactionTime());
				   return dto;
				}).collect(Collectors.toList());
		}
	  
	   return trans;
	}
	
	private Role registerNewRole(ERoles newRole) {
	        Role role = new Role();
	        role.setName(newRole);
	        return roleRepository.save(role);
	   }

	@Override
	public Balance saveBalance(User user,long balance) {
	   Balance balanciaga= new Balance();
	   balanciaga.setUser(user);
	   balanciaga.setBalance(balance);
	   balanciaga.setUpdatedAt(LocalDateTime.now());
	   return balanceRepository.save(balanciaga);
	}

	@Override
	public User findByUsername(String username) {
		return userRepository.findByUserAccount(username);
	}

	@Override
	public Optional<User> findById(UUID id) {
		return userRepository.findById(id);
	}

	@Override
	public User updateUser(User user) {
	  return userRepository.save(user);
	}

	
}
