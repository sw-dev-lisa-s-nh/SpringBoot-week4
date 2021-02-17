package com.lisasmith.findAGig.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.lisasmith.findAGig.entity.Gig;
import com.lisasmith.findAGig.entity.GigStatus;
import com.lisasmith.findAGig.service.GigService;

@RestController
@RequestMapping("/gigs")
public class GigController {
	
	@Autowired
	private GigService service;
	
	
	//Read a Gig
	@RequestMapping(method=RequestMethod.GET)
	public ResponseEntity<Object> getGigs() {
		return new ResponseEntity<Object>(service.getGigs(), HttpStatus.OK);
	}

	//Create a Gig
	@RequestMapping(method=RequestMethod.POST)
	public ResponseEntity<Object> createGig(@RequestBody Gig gig) throws Exception {
		return new ResponseEntity<Object>(service.createGig(gig), HttpStatus.CREATED);
	}
	
	// Update a Gig
	@RequestMapping(value="/{id}", method=RequestMethod.PUT)
	public ResponseEntity<Object> updateGig(@RequestBody Gig gig, @PathVariable Long id) throws Exception {
		try {
			return new ResponseEntity<Object>(service.updateGig(gig, id), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.NOT_FOUND);
		}
	}
	
	// Create instruments into GigStatus for an existing Gig
	@RequestMapping(value="/{id}",method=RequestMethod.POST)
	public ResponseEntity<Object> createGigStatus(@RequestBody GigStatus gigStatus, @PathVariable Long id) throws Exception {
			return new ResponseEntity<Object>(service.createGigStatus(gigStatus,id), HttpStatus.CREATED);
	}
	
	// Read instruments into GigStatus for an existing Gig
	@RequestMapping(value="/{id}",method=RequestMethod.GET)
	public ResponseEntity<Object> getGigStatuses(@PathVariable Long id) throws Exception {
			return new ResponseEntity<Object>(service.getGigStatuses(id), HttpStatus.OK);
	}

	
	// Read users that match a Gig
		@RequestMapping(value="/{id}/users",method=RequestMethod.GET)
		public ResponseEntity<Object> getGigStatusesByUserId(@PathVariable Long id) throws Exception {
				return new ResponseEntity<Object>(service.getGigStatusesByUserId(id), HttpStatus.OK);
		}

	// Delete an existing Gig
	@RequestMapping(value="/{id}", method=RequestMethod.DELETE)
	public ResponseEntity<Object> deleteGig(@PathVariable Long id) throws Exception {
		try {
			service.deleteGig(id);
			return new ResponseEntity<Object>("Successfully deleted gig with id: " + id, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.NOT_FOUND);
		}
	}
}

