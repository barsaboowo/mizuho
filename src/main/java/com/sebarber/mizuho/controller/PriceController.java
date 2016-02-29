package com.sebarber.mizuho.controller;

import java.util.Collections;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sebarber.mizuho.service.PriceService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.sebarber.mizuho.domain.PriceImpl;

@RestController
@RequestMapping("/prices")
public class PriceController {
	private static final Logger LOG = Logger.getLogger(PriceController.class);

	@Autowired
	private PriceService<PriceImpl> priceService;

	@RequestMapping(value = "/vendor/{vendorId}/list", method = RequestMethod.GET)
	public Set<PriceImpl> getPricesForVendor(@PathVariable("vendorId") String vendorId) {
		LOG.info("Fetching prices for vendor " + vendorId);
		Set<PriceImpl> prices;
		try {
			prices = priceService.getPricesForVendor(vendorId);
			LOG.info("Prices retrieved successfully");
		} catch (Exception e) {
			LOG.error("Unable to get prices for vendor " + vendorId, e);
			prices = Collections.emptySet();
		}
		return prices;
	}

	@RequestMapping(value = "/instrument/{instrumentId}/list", method = RequestMethod.GET)
	public Set<PriceImpl> getPricesForInstrumentId(@PathVariable("instrumentId") String instrumentId) {
		LOG.info("Fetching prices for instrument id " + instrumentId);
		Set<PriceImpl> prices;
		try {
			prices = priceService.getPricesForInstrumentId(instrumentId);
			LOG.info("Prices retrieved successfully");
		} catch (Exception e) {
			LOG.error("Unable to get prices for instrument id " + instrumentId, e);
			prices = Collections.emptySet();
		}
		return prices;
	}

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public ResponseEntity<String> create(@RequestBody Set<PriceImpl> prices) {
		LOG.info("Creating " + prices.size() + " prices.");
		int created = 0;
		ResponseEntity<String> response = null;
		for (PriceImpl p : prices) {
			try {
				priceService.addOrUpdate(p);
				++created;
			} catch (Exception e) {
				LOG.error("Unable to create price", e);
			}
		}
		if(created == prices.size()){
			String message = "All prices processed successfully";
			LOG.info(message);
			response = new ResponseEntity<String>(message, HttpStatus.OK);			
		}else{
			String message = created + " out of " + prices.size() + " prices created.";
			LOG.warn(message);
			response = new ResponseEntity<String>(message, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return response;
	}

}
