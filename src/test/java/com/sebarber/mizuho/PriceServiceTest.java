package com.sebarber.mizuho;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

import javax.validation.ValidationException;

import org.junit.Before;
import org.junit.Test;

import com.sebarber.mizuho.data.PriceDao;
import com.sebarber.mizuho.domain.PriceImpl;
import com.sebarber.mizuho.service.PriceServiceImpl;
import com.sebarber.mizuho.utils.Constants;
import com.sebarber.mizuho.validator.PriceValidator;

import org.junit.Assert;

public class PriceServiceTest {
	
	private static final Date DATE_31_DAYS_AGO = new Date(System.currentTimeMillis() - (Constants.ONE_DAY_IN_MILLIS * 31));

	private static final PriceImpl testPrice = new PriceImpl("instrumentId", "vendorId", "idType", "instrumentType",
			"priceType", DATE_31_DAYS_AGO, BigDecimal.ONE, BigDecimal.TEN, true);
	
	private static final PriceImpl testPrice2 = new PriceImpl(null, "vendorId", "idType", "instrumentType",
			"priceType", DATE_31_DAYS_AGO, BigDecimal.ONE, BigDecimal.TEN, true);
	
	private static final PriceImpl testPrice3 = new PriceImpl("instrumentId", null, "idType", "instrumentType",
			"priceType", DATE_31_DAYS_AGO, BigDecimal.ONE, BigDecimal.TEN, true);
	
	private static final PriceImpl testPrice4 = new PriceImpl("instrumentId", "vendorId", "idType", "instrumentType",
			"priceType", null, BigDecimal.ONE, BigDecimal.TEN, true);

	private PriceServiceImpl priceService;
	private final PriceValidator priceValidator = new PriceValidator();

	@Before
	public void setup() {
		PriceDao priceDao = new PriceDao();
		priceDao.put(testPrice.getPricePk(), testPrice);
		priceService = new PriceServiceImpl(30, 60, 60, priceDao, Collections.emptySet());
		priceService.setPriceValidator(priceValidator);
	}

	@Test
	public void testPriceService() {
		//Test that priming has happened
		Set<PriceImpl> prices = priceService.getPricesForInstrumentId(testPrice.getInstrumentId());
		Assert.assertTrue(prices.size() == 1);
		Assert.assertTrue(prices.contains(testPrice));
		
		prices = priceService.getPricesForVendor(testPrice.getVendorId());
		Assert.assertTrue(prices.size() == 1);
		Assert.assertTrue(prices.contains(testPrice));
		
		prices = priceService.getAllPrices();
		Assert.assertTrue(prices.size() == 1);
		Assert.assertTrue(prices.contains(testPrice));
		
		//Test deletion
		priceService.delete(testPrice);
		Assert.assertTrue(priceService.getAllPrices().isEmpty());
		Assert.assertTrue(priceService.getPricesForVendor(testPrice.getVendorId()).isEmpty());
		Assert.assertTrue(priceService.getPricesForInstrumentId(testPrice.getInstrumentId()).isEmpty());
		
		//Test addOrUpdate method
		priceService.addOrUpdate(testPrice);
		prices = priceService.getPricesForInstrumentId(testPrice.getInstrumentId());
		Assert.assertTrue(prices.size() == 1);
		Assert.assertTrue(prices.contains(testPrice));
		
		prices = priceService.getPricesForVendor(testPrice.getVendorId());
		Assert.assertTrue(prices.size() == 1);
		Assert.assertTrue(prices.contains(testPrice));
		
		prices = priceService.getAllPrices();
		Assert.assertTrue(prices.size() == 1);
		Assert.assertTrue(prices.contains(testPrice));
		
		//Test the timer cleanup task
		priceService.runTask();
		Assert.assertTrue(priceService.getAllPrices().isEmpty());
		Assert.assertTrue(priceService.getPricesForVendor(testPrice.getVendorId()).isEmpty());
		Assert.assertTrue(priceService.getPricesForInstrumentId(testPrice.getInstrumentId()).isEmpty());
	}
	
	@Test(expected=ValidationException.class)
	public void testValidationFailsWithNullVendorId(){
		priceService.addOrUpdate(testPrice2);
	}
	
	@Test(expected=ValidationException.class)
	public void testValidationFailsWithNullInstrumentId(){
		priceService.addOrUpdate(testPrice3);
	}
	
	@Test(expected=ValidationException.class)
	public void testValidationFailsWithNullCreatedDate(){
		priceService.addOrUpdate(testPrice4);
	}
}
