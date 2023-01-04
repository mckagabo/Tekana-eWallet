package com.tekanawallet.tekanawallet.registration.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tekanawallet.tekanawallet.registration.model.User;
import com.tekanawallet.tekanawallet.registration.model.VerificationToken;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken,UUID>{
	VerificationToken findByToken(String token);

    VerificationToken findByUser(User user);
}
