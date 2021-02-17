package com.lisasmith.findAGig.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.lisasmith.findAGig.entity.Instrument;
import com.lisasmith.findAGig.service.InstrumentService;

@RestController
@RequestMapping("/instruments")
public class InstrumentController {
	
	@Autowired
	private InstrumentService service;
	
	@RequestMapping(method=RequestMethod.GET)
	public ResponseEntity<Object> getInstruments() {
		return new ResponseEntity<Object>(service.getInstruments(), HttpStatus.OK);
	}

	@RequestMapping(method=RequestMethod.POST)
	public ResponseEntity<Object> createInstrument(@RequestBody Instrument instrument) throws Exception {
		return new ResponseEntity<Object>(service.createInstrument(instrument), HttpStatus.CREATED);
	}
	
	@RequestMapping(value="/{id}", method=RequestMethod.PUT)
	public ResponseEntity<Object> updateInstrument(@RequestBody Instrument instrument, @PathVariable Long id) throws Exception {
		try {			
			return new ResponseEntity<Object>(service.updateInstrument(instrument, id), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Object>("Unable to update instrument: " + id, HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value="/{id}", method=RequestMethod.DELETE)
	public ResponseEntity<Object> deleteInstrument( @PathVariable Long id) {
		try {
			service.removeInstrument(id);
			return new ResponseEntity<Object>("Successfully deleted instrument with id: " + id, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Object>("Unable to delete instrument: " + id, HttpStatus.BAD_REQUEST);

		}
	}
	
}
