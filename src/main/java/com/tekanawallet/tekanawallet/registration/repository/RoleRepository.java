package com.tekanawallet.tekanawallet.registration.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tekanawallet.tekanawallet.registration.model.Role;

import tekanawallet.tekanawallet.enums.ERoles;

public interface RoleRepository extends JpaRepository<Role,UUID> {
	 Role findByName(ERoles name);
}
