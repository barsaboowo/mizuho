package com.sebarber.mizuho;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;
import com.sebarber.mizuho.consumer.Consumer;
import com.sebarber.mizuho.data.PriceDao;
import com.sebarber.mizuho.domain.Price;
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
	private final Set<PriceImpl> pricesConsumed = Sets.newHashSet();
	private final PriceServiceImpl priceServiceImpl = new PriceServiceImpl(30, 1, 1, priceDao, 
			Collections.singleton(new Consumer<PriceImpl>() {
				public void consume(PriceImpl p){
					pricesConsumed.add(p);
				}

				@Override
				public String getConsumerName() {
					return "Test Consumer";
				}
	}));
	
	private final Validator<PriceImpl> priceValidator = new PriceValidator();
	
	@Before
	public void setup(){
		priceServiceImpl.setPriceValidator(priceValidator);
	}
	
	@Test
	public void testTimerService() throws InterruptedException{
		priceServiceImpl.addOrUpdate(testPrice);
		Assert.assertTrue(priceServiceImpl.getAllPrices().size() == 1 && priceServiceImpl.getAllPrices().contains(testPrice));
		Assert.assertTrue("Price has been published", pricesConsumed.contains(testPrice));
		Assert.assertTrue("Price has been published", pricesConsumed.iterator().next().isActive());
		
		timerService = new TimerService(1, Sets.newHashSet(priceServiceImpl));
	
		//Wait for the timer service to kick in
		Thread.sleep(2000);
		
		Assert.assertTrue("Price has been deleted", priceServiceImpl.getAllPrices().isEmpty());
		Assert.assertTrue("Price has been deleted", priceServiceImpl.getPricesForInstrumentId(testPrice.getInstrumentId()).isEmpty());
		Assert.assertTrue("Price has been deleted", priceServiceImpl.getPricesForVendor(testPrice.getVendorId()).isEmpty());		
		
		Assert.assertTrue(pricesConsumed.contains(testPrice));
		Assert.assertFalse("Price has been published as inactive", pricesConsumed.iterator().next().isActive());
		
	}
	
}
