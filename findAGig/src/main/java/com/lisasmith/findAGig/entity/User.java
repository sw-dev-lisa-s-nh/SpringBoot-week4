package com.lisasmith.findAGig.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;

import com.lisasmith.findAGig.util.UserType;

@Entity
public class User {

	private Long id;
	private String firstName;
	private String lastName;
	private Address address;
	private UserType userType; 
	private List<Instrument> instruments;
	private boolean systemAdmin;	

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "addressId")
	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}
	
	public UserType getUserType() {
		return this.userType;
	}
	
	public void setUserType(UserType userType) {
		this.userType = userType;
	}

	public boolean isSystemAdmin() {
		return systemAdmin;
	}

	public void setSystemAdmin(boolean systemAdmin) {
		this.systemAdmin = systemAdmin;
	}
	
	
	@ManyToMany(mappedBy="musicians")
	public List<Instrument> getInstruments() {
		return instruments;
	}

	public void setInstruments(List<Instrument> instruments) {
		this.instruments = instruments;
	}

}
