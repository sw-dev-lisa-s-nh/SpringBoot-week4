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
import com.lisasmith.findAGig.entity.User;
import com.lisasmith.findAGig.repository.GigRepository;
import com.lisasmith.findAGig.repository.GigStatusRepository;
import com.lisasmith.findAGig.repository.InstrumentRepository;
import com.lisasmith.findAGig.repository.UserRepository;
import com.lisasmith.findAGig.util.StatusType;
import com.lisasmith.findAGig.util.UserType;

@Service
public class GigService {

	private static final Logger logger = LogManager.getLogger(UserService.class);
	
	@Autowired
	private GigRepository repo;
	
	@Autowired
	private GigStatusRepository statusRepo;
	
	@Autowired
	private InstrumentRepository instrumentRepo;
	
	@Autowired
	private UserRepository  userRepo;
	
	@Autowired
	private AddressService addressService;
	
	@Autowired
	private InstrumentService instrumentService;
	
	
	public Iterable<Gig> getGigs() {
		logger.info("Finding all Gigs");
		return repo.findAll();
	}
	
	public Iterable<GigStatus> getGigStatuses(Long gigId) {
		logger.info("Finding all Instruments for a particularGig!");
		return statusRepo.findByGigId(gigId);			 
	}
	
	public Iterable<User> getGigStatusesWithMusicianInfo(Long gigId) {
		logger.info("Finding all Musicians for a particularGig!");
		Iterable<GigStatus> matchingGigStatuses = statusRepo.findByGigId(gigId);
		List<User> matchingMusicians = new ArrayList<User>();
		for (GigStatus gigStatus : matchingGigStatuses) {
			if (gigStatus.getMusicianId() != null) {
				matchingMusicians.add(userRepo.findOne(gigStatus.getMusicianId()));
				logger.info("Matching Status #: " + gigStatus.getId());
			}
		}
		return matchingMusicians;
	}


	public Iterable<GigStatus> getGigStatusesByUserId(Long userId) {
		logger.info("Finding all Gigs for a particular UserId!");
		return statusRepo.findByMusicianId(userId);
	}
	 

	public Gig createGig(Gig newGig) throws Exception {
		try {
			Gig gig = new Gig();
			Gig savedGig = new Gig();
			gig.setGigDate(newGig.getGigDate());
			gig.setGigStartTime(newGig.getGigStartTime());
			gig.setGigDuration(newGig.getGigDuration());
			logger.info("before createAddress");
			gig.setAddress(addressService.createAddress(newGig.getAddress()));
			logger.info("after createAddress");
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
		logger.info("In createGigStatus");
		List<Instrument> gigRequiredInstruments = new ArrayList<Instrument>();
		gigRequiredInstruments = gigStatus.getInstruments();
		logger.info("After gigRequiredInstruments: " + gigRequiredInstruments.size());
		Double splitSalary = (oldGig.getSalary()/gigRequiredInstruments.size());
		logger.info("Salary:  " + splitSalary);
		
		for (Instrument inst : gigRequiredInstruments) {
			logger.info("In createGigStatus instrument inst name:" + inst.getName());
			GigStatus newGigStatus = new GigStatus();
			newGigStatus.setGigId(oldGig.getGigId());
			newGigStatus.setStatus(StatusType.OPEN);
			newGigStatus.setSalary(splitSalary);
			newGigStatus.setInstruments(instrumentService.createInstruments(gigRequiredInstruments));
			
			for (Instrument oneInst : newGigStatus.getInstruments()) {
				if (oneInst.getName().equals(inst.getName())) {
					logger.info("Adding Instrument: " + inst.getName() + " with id: " + inst.getInstrumentId());
					newGigStatus.setInstrumentId(oneInst.getInstrumentId());
				}
			}
				
			logger.info("Before addGigStatustoInstruments");
			addGigStatustoInstruments(newGigStatus);
			logger.info("After addGigStatustoInstruments");

			savedGigStatuses.add(statusRepo.save(newGigStatus));
			logger.info("Adding Instrument: " + inst.getInstrumentId() + " and GigId: " + oldGig.getGigId());
		}
		return savedGigStatuses;
	}
	
	// UPDATE:  Update a GigStatus -- REQUEST a GIG, CONFIRM a MUSICIAN, etc.
	public GigStatus updateGigStatus(GigStatus gigStatus, Long gigId, Long userId) throws Exception {	
		
		GigStatus relatedGigStatus = new GigStatus();

		// Initialize the boolean for USER INSTRUMENT MATCH an instrument for the gig
		boolean match = false;
		logger.info("In updateGigStatus");
		try {		
			Iterable<GigStatus> matchedGigStatuses = new ArrayList<GigStatus>(); 
			matchedGigStatuses = statusRepo.findByGigId(gigId);
			logger.info("In updateGigStatus -- after findByGigId");

			Instrument requestedInstrument = new Instrument();
			requestedInstrument = findMatchingInstrument(gigStatus.getInstruments());
			logger.info("In updateGigStatus -- requestedInstrument is: " + requestedInstrument.getName());
			
		//  Get the requested Instrument from the repository			
		//FIND IF REQUESTED INSTRUMENT matches a GIGSTATUS record?
			for (GigStatus matchGigStatus : matchedGigStatuses) {
				List<Instrument> allInst = matchGigStatus.getInstruments();
				for (Instrument oneInst : allInst) {
					if ((oneInst.getInstrumentId().equals(requestedInstrument.getInstrumentId())) && (matchGigStatus.getStatus().equals(StatusType.OPEN))){
						match = true;
						relatedGigStatus.setGigId(matchGigStatus.getGigId());					
					}
				}
			}
				
		// Find USER record in database, and get their instruments
			User relatedUser = userRepo.findOne(userId);
			Iterable<Instrument>  relatedUserInstruments = relatedUser.getInstruments();		
			
			// For every gigStatus for this gigId, retrieve it, and check to see if it 
			//			matches the instrument requested.
			for (GigStatus matchStatus : matchedGigStatuses) {
				logger.info("matchStatus (in updateGigStatus) is: " + matchStatus.getId());
			relatedGigStatus = statusRepo.findOne(matchStatus.getId());
				logger.info("relatedGigStatus (in updateGigStatus) is: " + relatedGigStatus.getId());
				// Does the instrument match?
				// Get the users instruments, and check to see that there is a match
				for (Instrument instrument : relatedUserInstruments) {
					if (instrument.getInstrumentId().equals(relatedGigStatus.getInstrumentId())) {
						if (instrument.getInstrumentId().equals(gigStatus.getInstrumentId()))
						logger.info("In updateGigStatus, instrument matches!");
						match = true;
						
					}
				}		

				if ((match == true) && (relatedUser.getUserType().equals(UserType.MUSICIAN))){
					// IF Status is OPEN --> change status to REQUESTED 
					if (relatedGigStatus.getStatus().equals(StatusType.OPEN)) {
						logger.info("status is OPEN");
						//If new status is REQUESTED, set it, and set Musician_ID to userId
						if (gigStatus.getStatus().equals(StatusType.REQUESTED)) {
							logger.info("looking to make a request");
							relatedGigStatus.setStatus(StatusType.REQUESTED);
							relatedGigStatus.setMusicianId(userId);
							logger.info("before save(relatedGigStatus)");
							statusRepo.save(relatedGigStatus);
							logger.info("after save(relatedGigStatus)");

							return relatedGigStatus;
						} else {
							logger.info("try next record");
						}
					// IF Status is REQUESTED --> and user is planner, change status to CONFIRMED
					} else if (relatedGigStatus.getStatus().equals(StatusType.REQUESTED)) {
						logger.info("status is REQUESTED");
						//Does user_id == planner_id?

						
					// IF Status is CONFIRMED -->  ???
					} else if (relatedGigStatus.getStatus().equals(StatusType.CONFIRMED)) {					
						logger.info("status is CONFIRMED");
						
					} else {
								
					}
							
				}
			
			} // End of for every gigStatus in this gig (matches gigId)
			
			statusRepo.save(relatedGigStatus);
			return relatedGigStatus;	
			
		} catch  (Exception e) {
			logger.error("Exception occurred while trying to update a Gig's status.");
			throw new Exception("Unable to update gigStatus: " + relatedGigStatus.getId() + " with userId: " + userId);
		}		
	}
	
	public Instrument findMatchingInstrument(List<Instrument> requestedInstruments) {
		Iterable<Instrument> allInstruments = new ArrayList<Instrument>();
		allInstruments = instrumentRepo.findAll();			
		for (Instrument matchedInstrument : allInstruments) {
			for (Instrument requestedInstrument : requestedInstruments) {
				if ((matchedInstrument.getName().equals(requestedInstrument.getName())) ||
						(matchedInstrument.getInstrumentId().equals(requestedInstrument.getInstrumentId()))){
					return matchedInstrument;
				}
			}			
		}
		return null;

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

