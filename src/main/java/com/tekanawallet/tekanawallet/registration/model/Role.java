package com.tekanawallet.tekanawallet.registration.model;

import java.util.List;
import java.util.UUID;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import tekanawallet.tekanawallet.enums.ERoles;

@Entity
@Table(name="roles")
public class Role {
	 @Id
	 @GeneratedValue(strategy=GenerationType.UUID)
    private UUID id;

	@Enumerated(EnumType.STRING)
    @Column(name="name", unique=true)
    private ERoles name;

    @ManyToMany(mappedBy="roles")
    private List<User> users;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	

	public ERoles getName() {
		return name;
	}

	public void setName(ERoles name) {
		this.name = name;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}
    
    

}
