package com.sebarber.mizuho;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.sebarber.mizuho.data.Dao;
import com.sebarber.mizuho.data.PriceDao;
import com.sebarber.mizuho.domain.PriceImpl;
import com.sebarber.mizuho.domain.PricePk;

public class PriceDaoTest {

	private static final PriceImpl testPrice = new PriceImpl("instrumentId", "vendorId", "idType", "instrumentType",
			"priceType", new Date(), BigDecimal.ONE, BigDecimal.TEN, true);

	private final Dao<PricePk, PriceImpl> priceDao = new PriceDao();

	@Test
	public void testPriceDao() {
		priceDao.put(testPrice.getPricePk(), testPrice);
		Assert.assertTrue("price was added successfully", testPrice.equals(priceDao.get(testPrice.getPricePk())));
		Assert.assertTrue("All values returned correctly",
				priceDao.getAll().contains(testPrice) && priceDao.getAll().size() == 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullKeyThrowsException() {
		priceDao.put(null, testPrice);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullValueThrowsException() {
		priceDao.put(testPrice.getPricePk(), null);
	}
}
