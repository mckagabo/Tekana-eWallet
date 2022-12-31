package com.tekanawallet.tekanawallet.registration.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.tekanawallet.tekanawallet.registration.model.User;

public interface UserRepository extends JpaRepository<User,UUID>{
	 User findByEmail(String email);
	 
	 @Query("Select u from User u where u.userAccount=?1")
	 User findByUserAccount(String userAccount);
	 
	 Boolean existsByUserAccount(String username);

	 Boolean existsByEmail(String email);
}
