package com.sebarber.mizuho;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

import javax.validation.ValidationException;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;
import com.sebarber.mizuho.consumer.Consumer;
import com.sebarber.mizuho.data.PriceDao;
import com.sebarber.mizuho.domain.PriceImpl;
import com.sebarber.mizuho.service.PriceServiceImpl;
import com.sebarber.mizuho.utils.Constants;
import com.sebarber.mizuho.validator.PriceValidator;

import org.junit.Assert;

public class PriceServiceTest {

	private static final Date DATE_31_DAYS_AGO = new Date(
			System.currentTimeMillis() - (Constants.ONE_DAY_IN_MILLIS * 31));

	private static final PriceImpl testPrice = new PriceImpl("instrumentId", "vendorId", "idType", "instrumentType",
			"priceType", DATE_31_DAYS_AGO, BigDecimal.ONE, BigDecimal.TEN, true);

	private static final PriceImpl testPrice2 = new PriceImpl(null, "vendorId", "idType", "instrumentType", "priceType",
			DATE_31_DAYS_AGO, BigDecimal.ONE, BigDecimal.TEN, true);

	private static final PriceImpl testPrice3 = new PriceImpl("instrumentId", null, "idType", "instrumentType",
			"priceType", DATE_31_DAYS_AGO, BigDecimal.ONE, BigDecimal.TEN, true);

	private static final PriceImpl testPrice4 = new PriceImpl("instrumentId", "vendorId", "idType", "instrumentType",
			"priceType", null, BigDecimal.ONE, BigDecimal.TEN, true);

	private PriceServiceImpl priceService;
	private final PriceValidator priceValidator = new PriceValidator();
	private final Set<PriceImpl> pricesConsumed = Sets.newHashSet();

	@Before
	public void setup() {
		PriceDao priceDao = new PriceDao();
		priceDao.put(testPrice.getPricePk(), testPrice);
		priceService = new PriceServiceImpl(30, 60, 60, priceDao, Collections.singleton(new Consumer<PriceImpl>() {
			public void consume(PriceImpl p) {
				pricesConsumed.add(p);
			}

			@Override
			public String getConsumerName() {
				return "Test Consumer";
			}
		}));
		priceService.setPriceValidator(priceValidator);
	}

	@Test
	public void testPriceService() {
		// Test that priming has happened
		Set<PriceImpl> prices = priceService.getPricesForInstrumentId(testPrice.getInstrumentId());
		Assert.assertTrue("Cache was primed", prices.size() == 1);
		Assert.assertTrue("Cache was primed", prices.contains(testPrice));

		prices = priceService.getPricesForVendor(testPrice.getVendorId());
		Assert.assertTrue("Cache was primed", prices.size() == 1);
		Assert.assertTrue("Cache was primed", prices.contains(testPrice));

		prices = priceService.getAllPrices();
		Assert.assertTrue("Cache was primed", prices.size() == 1);
		Assert.assertTrue("Cache was primed", prices.contains(testPrice));
	
		Assert.assertTrue("Price was published", pricesConsumed.contains(testPrice));
		Assert.assertTrue("Price is active as published", pricesConsumed.iterator().next().isActive());

		// Test deletion
		pricesConsumed.clear();
		priceService.delete(testPrice);
		Assert.assertTrue("Price was deleted", priceService.getAllPrices().isEmpty());
		Assert.assertTrue("Price was deleted", priceService.getPricesForVendor(testPrice.getVendorId()).isEmpty());
		Assert.assertTrue("Price was deleted", priceService.getPricesForInstrumentId(testPrice.getInstrumentId()).isEmpty());
		Assert.assertTrue("Price was published", pricesConsumed.contains(testPrice));
		Assert.assertFalse("Price is inactive as published", pricesConsumed.iterator().next().isActive());
		
		// Test addOrUpdate method
		pricesConsumed.clear();
		testPrice.setActive(true);
		priceService.addOrUpdate(testPrice);
		prices = priceService.getPricesForInstrumentId(testPrice.getInstrumentId());
		Assert.assertTrue("Price was added", prices.size() == 1);
		Assert.assertTrue("Price was added", prices.contains(testPrice));
		Assert.assertTrue("Price was published", pricesConsumed.contains(testPrice));
		Assert.assertTrue("Price is active as published", pricesConsumed.iterator().next().isActive());

		prices = priceService.getPricesForVendor(testPrice.getVendorId());
		Assert.assertTrue("Price was cached by vendor", prices.size() == 1);
		Assert.assertTrue("Price was cached by vendor", prices.contains(testPrice));

		prices = priceService.getAllPrices();
		Assert.assertTrue("Price was added", prices.size() == 1);
		Assert.assertTrue("Price was added", prices.contains(testPrice));

		// Test the timer cleanup task
		pricesConsumed.clear();
		priceService.runTask();
		Assert.assertTrue("Price was deleted", priceService.getAllPrices().isEmpty());
		Assert.assertTrue("Price was deleted from vendor cache", priceService.getPricesForVendor(testPrice.getVendorId()).isEmpty());
		Assert.assertTrue("Price was deleted from instrument cache", priceService.getPricesForInstrumentId(testPrice.getInstrumentId()).isEmpty());
		Assert.assertTrue("Price was published", pricesConsumed.contains(testPrice));
		Assert.assertFalse("Price is inactive as published", pricesConsumed.iterator().next().isActive());
		
	}

	@Test(expected = ValidationException.class)
	public void testValidationFailsWithNullVendorId() {
		priceService.addOrUpdate(testPrice2);
	}

	@Test(expected = ValidationException.class)
	public void testValidationFailsWithNullInstrumentId() {
		priceService.addOrUpdate(testPrice3);
	}

	@Test(expected = ValidationException.class)
	public void testValidationFailsWithNullCreatedDate() {
		priceService.addOrUpdate(testPrice4);
	}
}
