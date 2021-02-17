package com.lisasmith.findAGig.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lisasmith.findAGig.entity.Gig;
import com.lisasmith.findAGig.entity.GigStatus;
import com.lisasmith.findAGig.entity.Instrument;
import com.lisasmith.findAGig.repository.GigRepository;
import com.lisasmith.findAGig.repository.GigStatusRepository;
import com.lisasmith.findAGig.repository.InstrumentRepository;
import com.lisasmith.findAGig.util.StatusType;

@Service
public class GigService {

	private static final Logger logger = LogManager.getLogger(UserService.class);
	
	@Autowired
	private GigRepository repo;
	
	@Autowired
	private GigStatusRepository statusRepo;
	
	@Autowired
	private InstrumentRepository instrumentRepo;
	
	
	public Iterable<Gig> getGigs() {
		logger.info("Finding all Gigs");
		return repo.findAll();
	}
	
	public Iterable<GigStatus> getGigStatuses(Long gigId) {
		logger.info("Finding all Instruments for a particularGig!");
		List<GigStatus> matchingGigStatuses = new ArrayList<GigStatus>();
		Iterable<GigStatus> allGigStatuses = new ArrayList<GigStatus>();
		allGigStatuses = statusRepo.findAll();		
		logger.info("After findall & before finding matching Gig Statuses");
		int counter = 1;
		for (GigStatus gigStatus : allGigStatuses) {
			if (gigStatus.getGigId().equals(gigId)) {
				matchingGigStatuses.add(gigStatus);
				logger.info("Matching Status #: " + counter++);
			}
		}
		logger.info("After finding matching Gig Statuses");
		return matchingGigStatuses;		 
		 
	}

	public Iterable<GigStatus> getGigStatusesByUserId(Long userId) {
		logger.info("Finding all Gigs for a particular UserId!");
		List<GigStatus> matchingGigStatuses = new ArrayList<GigStatus>();
		Iterable<GigStatus> allGigStatuses = new ArrayList<GigStatus>();
		allGigStatuses = statusRepo.findAll();		
		logger.info("After findall & before finding matching Gig Statuses");
		int counter = 1;
		for (GigStatus gigStatus : allGigStatuses) {
			logger.info("In allGigStatuses loop");
			if (gigStatus.getMusicianId() != null) {
				if (gigStatus.getMusicianId().equals(userId)) {
					matchingGigStatuses.add(gigStatus);
					logger.info("Matching Status #: " + counter++);
				}
			}
		}
		logger.info("After finding matching Gig Statuses");
		return matchingGigStatuses;		 
		 
	}

	public Gig createGig(Gig newGig) throws Exception {
		try {
			Gig gig = new Gig();
			Gig savedGig = new Gig();
			gig.setGigDate(newGig.getGigDate());
			gig.setGigStartTime(newGig.getGigStartTime());
			gig.setGigDuration(newGig.getGigDuration());
			gig.setAddress(newGig.getAddress());
			gig.setPhone(newGig.getPhone());
			gig.setEvent(newGig.getEvent());
			gig.setGenre(newGig.getGenre());
			gig.setDescription(newGig.getDescription());
			gig.setSalary(newGig.getSalary());
			gig.setPlannerId(newGig.getPlannerId());
			logger.info("Create a Gig");
			savedGig = repo.save(gig);
			return savedGig;

		} catch (Exception e) {
			logger.error("Exception occurred while trying to create a gig.");
			throw new Exception ("Unable to create a gig.");
		}
	}

	
	private GigStatus addGigStatustoInstruments(GigStatus gigStatus) {
		logger.info("In addGigStatustoInstruments instrumentId: " + gigStatus.getInstrumentId());
		Instrument instrument = new Instrument();
		instrument = instrumentRepo.findOne(gigStatus.getInstrumentId());

		logger.info("after getting newInstrumentsaddGigStatustoInstruments");		
		logger.info("Instrument name: " + instrument.getName() + " id: " + instrument.getInstrumentId());
		instrument.getGigStatuses().add(gigStatus);
		logger.info("After add(gigStatus)");
		return gigStatus;
	}
	
	public List<GigStatus> createGigStatus(GigStatus gigStatus, Long gigId) throws Exception {
		List<GigStatus> savedGigStatuses = new ArrayList<GigStatus>();
		Gig oldGig = repo.findOne(gigId);
		List<Instrument> gigRequiredInstruments = new ArrayList<Instrument>();
		gigRequiredInstruments = gigStatus.getInstruments();
		Double splitSalary = (oldGig.getSalary()/gigRequiredInstruments.size());
		for (Instrument inst : gigRequiredInstruments) {
			GigStatus newGigStatus = new GigStatus();
			newGigStatus.setGigId(oldGig.getGigId());
			newGigStatus.setStatus(StatusType.OPEN);
			newGigStatus.setSalary(splitSalary);
			newGigStatus.setInstruments(gigStatus.getInstruments());
			logger.info("Adding Instrument: " + inst.getInstrumentId());
			
			newGigStatus.setInstrumentId(inst.getInstrumentId());	
			
			logger.info("Before addGigStatustoInstruments");
			addGigStatustoInstruments(newGigStatus);
			logger.info("After addGigStatustoInstruments");

			savedGigStatuses.add(statusRepo.save(newGigStatus));
			logger.info("Adding Instrument: " + inst.getInstrumentId() + " and GigId: " + oldGig.getGigId());
		}
		return savedGigStatuses;
	}
	
	public GigStatus updateGigStatus(GigStatus gigStatus, Long gigId) throws Exception {		
		try {
			//Gig oldGig = repo.findOne(gigId);
			GigStatus relatedGigStatus = statusRepo.findOne(gigStatus.getId());
			
			// IF Status is OPEN --> and user is matching musician, change status to REQUESTED 
			if (relatedGigStatus.getStatus().equals("OPEN")) {
				logger.info("status is OPEN");
				
			// IF Status is REQUESTED --> and user is planner, change status to CONFIRMED
			} else if (relatedGigStatus.getStatus().equals("REQUESTED")) {
			
				logger.info("status is REQUESTED");

				
			// IF Status is CONFIRMED -->  ???
			} else if (relatedGigStatus.getStatus().equals("CONFIRMED")) {
				
				logger.info("status is CONFIRMED");
				
			} else {
				
				
			}

			return statusRepo.save(relatedGigStatus);
			
		} catch  (Exception e) {
			logger.error("Exception occurred while trying to update a gigStatus.");
			throw new Exception("Unable to update gig id: " + gigId + " with gigStatus: " + gigStatus.getId());
		}		
	}
	
	
	public Gig updateGig(Gig newGig, Long gigId) throws Exception {
		try {
			Gig oldGig = repo.findOne(gigId);
			oldGig.setGigDate(newGig.getGigDate());
			oldGig.setGigStartTime(newGig.getGigStartTime());
			oldGig.setGigDuration(newGig.getGigDuration());
			oldGig.setPhone(newGig.getPhone());
			oldGig.setEvent(newGig.getEvent());
			oldGig.setGenre(newGig.getGenre());
			oldGig.setPlannerId(newGig.getPlannerId());
			oldGig.setSalary(newGig.getSalary());
			oldGig.setDescription(newGig.getDescription());
			logger.info("Updating gig: " + gigId);
			Gig savedGig = new Gig();
			savedGig = repo.save(oldGig);
						
			// Populate GigStatus Table
			// Maybe a @JsonIgnore in Gig??
			
//			Set<Instrument> gigRequiredInstruments = new HashSet<Instrument>();
//			gigRequiredInstruments = savedGig.getInstruments();
//			for (Instrument inst : gigRequiredInstruments) {
//				GigStatus gigStatus = new GigStatus();
//				gigStatus.setGigId(savedGig.getGigId());
//				gigStatus.setStatus(StatusType.OPEN);
//				gigStatus.setSalary(savedGig.getSalary());
//				logger.info("Adding Instrument: " + inst.getInstrumentId() + " to GigId: " + savedGig.getGigId());
//				gigStatus.setInstrumentId(inst.getInstrumentId());
//				statusRepo.save(gigStatus);
//			}			
			return savedGig;
		} catch (Exception e) {
			logger.error("Exception occurred while trying to update a gig.");
			throw new Exception("Unable to update gig id:" + gigId);
		}
	}	
		
	
	public void deleteGig(Long gigId) throws Exception {	
		try {
			// delete gigStatus for gigId first records 1st
			// I have CONSTRAINT ERRORS here... so I can not delete these GigStatuses, 
			// 			and therefore, I cannot delete the gig!
			Iterable<GigStatus> gigStatusByGigId = getGigStatuses(gigId);
			for (GigStatus status : gigStatusByGigId){
				statusRepo.delete(status);
			}
			repo.delete(gigId);
			logger.info("Deleted gig: " + gigId);
		} catch (Exception e) {
			logger.error("Exception occurred while trying to delete gig:" + gigId);
			throw new Exception("Unable to delete gig.");
		}
	}
}

