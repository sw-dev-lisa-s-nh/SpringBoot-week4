package com.lisasmith.findAGig.service;


import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lisasmith.findAGig.entity.Instrument;
import com.lisasmith.findAGig.entity.User;
import com.lisasmith.findAGig.repository.GigRepository;
import com.lisasmith.findAGig.repository.InstrumentRepository;
import com.lisasmith.findAGig.repository.UserRepository;


@Service
public class UserService {
	
	private static final Logger logger = LogManager.getLogger(UserService.class);

	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private InstrumentRepository instrumentRepo;
	
	@Autowired 
	private GigRepository gigRepo;
	
	public Iterable<User> getUsers() {
		logger.info("Finding all Users");
		return userRepo.findAll();
	}

	public User createUser(User newUser) throws Exception {
		try {
			User user = new User();
			user.setAddress(newUser.getAddress());
			user.setFirstName(newUser.getFirstName());
			user.setLastName(newUser.getLastName());
			user.setUserType(newUser.getUserType());
			user.setSystemAdmin(newUser.isSystemAdmin());
			user.setInstruments(newUser.getInstruments());
			logger.info("Creating a new User.");
			addUsertoInstruments(user);
			logger.info("Added user to Instruments.");
			return userRepo.save(user);
		} catch (Exception e) {
			logger.error("Exception occurred while trying to create a user.");
			throw new Exception("Unable to create a new user.");
		}
	}
	
	private User addUsertoInstruments(User user) {
		List<Instrument> newInstruments = new ArrayList<Instrument>();
		List<Instrument> instruments = user.getInstruments();
		for (Instrument instrument : instruments) {
			newInstruments.add(instrumentRepo.findOne(instrument.getInstrumentId()));
		}	
		int counter = 1;
		logger.info("In addUsertoInstruments");
		for (Instrument newInstrument : newInstruments) {
			logger.info("Before add(user) -- Time through instruments:" + counter);
			logger.info("Instrument name: " + newInstrument.getName());
			logger.info("instrument id: " +	newInstrument.getInstrumentId());
			newInstrument.getMusicians().add(user);
			logger.info("After add(user) -- Time through instruments:" + counter++);
		}
		return user;
	}
	

	public User updateUser(User user, Long id) throws Exception {
		try {
			User oldUser = userRepo.findOne(id);
			oldUser.setFirstName(user.getFirstName());
			oldUser.setLastName(user.getLastName());
			oldUser.setSystemAdmin(user.isSystemAdmin());
			oldUser.setUserType(user.getUserType());
			oldUser.setInstruments(user.getInstruments());
			
			//attempt to get the PUT to work with new instruments
			addUsertoInstruments(oldUser);
			logger.info("Updating user: " + id);
			return userRepo.save(oldUser);
		} catch (Exception e) {
			logger.error("Exception occurred while trying to update a user.");
			throw new Exception("Unable to update user id:" + id);
		}
	}
	
	public void deleteUser(Long id) throws Exception {
		try {
			userRepo.delete(id);
			logger.info("Deleted user: " + id);
		} catch (Exception e) {
			logger.error("Exception occurred while trying to delete user:" + id);
			throw new Exception("Unable to delete user.");
		}
	}
	
}
