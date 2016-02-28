package com.sebarber.mizuho.service;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.camel.CamelExecutionException;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sebarber.mizuho.domain.Price;
import com.sebarber.mizuho.utils.Constants;
import com.sebarber.mizuho.utils.JSONUtils;

public class MockPriceFeed implements TimerTask {
	private static final Logger LOG = Logger.getLogger(MockPriceFeed.class);
 
	private final String vendorId;
	private final long delayInSeconds;
	private final long periodInSeconds;	
	
	@Produce(uri="activemq:topic:com.pricefeed.bloomberg")
	private ProducerTemplate producerTemplate;
	
	private final JSONUtils jsonUtils = new JSONUtils();
	

	public MockPriceFeed(String vendorId, long delayInSeconds, long periodInSeconds) {
		super();
		this.vendorId = vendorId;
		this.delayInSeconds = delayInSeconds;
		this.periodInSeconds = periodInSeconds;
	}

	@Override
	public void runTask() {
		try {
			Price price = createMockPrice();
			producerTemplate.sendBodyAndHeader(jsonUtils.mapToJson(price), "vendorId", vendorId);
		} catch (CamelExecutionException | JsonProcessingException e) {
			LOG.error("Unable to parse price", e);
		}
		
	}

	private Price createMockPrice() {
		int position = (int) (Math.random() * 5);
		String isin = Constants.TEST_ISINS.get(position);
		return new Price(isin, vendorId, "ISIN", "Government Bond", "Clean Price", new Date(), generatePrice(), generatePrice());
	}

	private BigDecimal generatePrice() {
		return new BigDecimal(100 * Math.random()).setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	@Override
	public String getServicename() {
		return "Mock price feed: " + vendorId;
	}

	@Override
	public long getDelayInSeconds() {
		return delayInSeconds;
	}

	@Override
	public long getPeriodInSeconds() {
		return periodInSeconds;
	}
}
