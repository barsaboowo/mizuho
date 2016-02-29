package com.sebarber.mizuho;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;
import com.sebarber.mizuho.data.PriceDao;
import com.sebarber.mizuho.domain.PriceImpl;
import com.sebarber.mizuho.service.PriceServiceImpl;
import com.sebarber.mizuho.service.TimerService;
import com.sebarber.mizuho.utils.Constants;
import com.sebarber.mizuho.validator.PriceValidator;
import com.sebarber.mizuho.validator.Validator;

public class TimerServiceTest {
	private static final Date DATE_31_DAYS_AGO = new Date(System.currentTimeMillis() - (Constants.ONE_DAY_IN_MILLIS * 31));

	private static final PriceImpl testPrice = new PriceImpl("instrumentId", "vendorId", "idType", "instrumentType",
			"priceType", DATE_31_DAYS_AGO, BigDecimal.ONE, BigDecimal.TEN, true);

	private TimerService timerService;
	private final PriceDao priceDao = new PriceDao();
	private final PriceServiceImpl priceServiceImpl = new PriceServiceImpl(30, 1, 1, priceDao, Collections.emptySet());
	private final Validator<PriceImpl> priceValidator = new PriceValidator();
	
	@Before
	public void setup(){
		priceServiceImpl.setPriceValidator(priceValidator);
	}
	
	@Test
	public void testTimerService() throws InterruptedException{
		priceServiceImpl.addOrUpdate(testPrice);
		Assert.assertTrue(priceServiceImpl.getAllPrices().size() == 1 && priceServiceImpl.getAllPrices().contains(testPrice));
		timerService = new TimerService(1, Sets.newHashSet(priceServiceImpl));
		
		//Wait for the timer service to kick in
		Thread.sleep(2000);
		
		Assert.assertTrue(priceServiceImpl.getAllPrices().isEmpty());
		Assert.assertTrue(priceServiceImpl.getPricesForInstrumentId(testPrice.getInstrumentId()).isEmpty());
		Assert.assertTrue(priceServiceImpl.getPricesForVendor(testPrice.getVendorId()).isEmpty());
	}
	
}
