package com.lisasmith.findAGig.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lisasmith.findAGig.entity.Instrument;
import com.lisasmith.findAGig.repository.InstrumentRepository;

@Service
public class InstrumentService {
	
	private static final Logger logger = LogManager.getLogger(InstrumentService.class);
	
	@Autowired
	private InstrumentRepository repo;
	
	public Iterable<Instrument> getInstruments() {
			logger.info("Finding all Instruments.");
			return repo.findAll();
	}

	public Instrument createInstrument(Instrument newInstrument) throws Exception {
		try {
			logger.info("Creating a new Instrument.");
			return repo.save(newInstrument);
		} catch (Exception e) {
			logger.error("Exception occurred while trying to create an instrument.");
			throw new Exception("Unable to create an instrument.");
		}
	}
	
	public Instrument updateInstrument(Instrument newInstrument, Long id) throws Exception {
		try {
			Instrument oldInstrument = repo.findOne(id);
			logger.info("Updating Instrument: " + id);
			oldInstrument.setName(newInstrument.getName());
			return repo.save(oldInstrument);
		} catch (Exception e) {
			logger.error("Exception occurred while trying to update instrument: " + id, e);
			throw new Exception("Unable to update instrument: " + id);
		}
	}
	
	public void removeInstrument(Long id) throws Exception  {
		try {
			repo.delete(id);
			logger.info("Deleting Instrument: " + id);
		} catch (Exception e) {
			logger.error("Exception occurred while deleting instrument: " + id, e);
			throw new Exception("Unable to delete instrument: " + id);
		}
		
	}
}
