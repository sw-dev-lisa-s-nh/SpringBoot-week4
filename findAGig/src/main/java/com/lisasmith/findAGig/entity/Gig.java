package com.lisasmith.findAGig.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.lisasmith.findAGig.util.EventType;
import com.lisasmith.findAGig.util.GenreType;

@Entity
public class Gig {

	private Long gigId;
	private String gigDate;
	private String gigStartTime;
	private String gigDuration;
	private String phone;
	private EventType event;
	private GenreType genre;
	private String description;
	private double salary;
	private Long plannerId;
	private Address address;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getGigId() {
		return gigId;
	}
	
	public void setGigId(Long gigId) {
		this.gigId = gigId;
	}
	
	public String getGigDate() {
		return gigDate;
	}

	public void setGigDate(String gigDate) {
		this.gigDate = gigDate;
	}
	
	public String getGigStartTime() {
		return gigStartTime;
	}

	public void setGigStartTime(String gigStartTime) {
		this.gigStartTime = gigStartTime;
	}
	
	public String getGigDuration() {
		return gigDuration;
	}

	public void setGigDuration(String gigDuration) {
		this.gigDuration = gigDuration;
	}
	
	@ManyToOne
	@JoinColumn(name = "addressId")
	public Address getAddress() {
		return address;
	}
	
	public void setAddress(Address address) {
		this.address = address;
	}
	
	public String getPhone() {
		return phone;
	}
	
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	public EventType getEvent() {
		return event;
	}
	
	public void setEvent(EventType event) {
		this.event = event;
	}
	
	public GenreType getGenre() {
		return genre;
	}
	
	public void setGenre(GenreType genre) {
		this.genre = genre;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	public Long getPlannerId() {
		return plannerId;
	}
	
	public void setPlannerId(Long id) {
		this.plannerId = id;
	}
	
	public double getSalary() {
		return salary;
	}

	public void setSalary(double salary) {
		this.salary = salary;
	}
	
}
