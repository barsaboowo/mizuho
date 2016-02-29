package com.sebarber.mizuho.mock;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.camel.CamelExecutionException;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sebarber.mizuho.domain.PriceImpl;
import com.sebarber.mizuho.service.TimerTask;
import com.sebarber.mizuho.utils.Constants;
import com.sebarber.mizuho.utils.JSONUtils;

public class MockPriceFeed implements TimerTask {
	private static final Logger LOG = Logger.getLogger(MockPriceFeed.class);
 	
	private final long delayInSeconds;
	private final long periodInSeconds;	
	
	@Produce(uri="activemq:topic:com.pricefeed.bloomberg")
	private ProducerTemplate producerTemplateBloomberg;
	
	@Produce(uri="activemq:topic:com.pricefeed.reuters")
	private ProducerTemplate producerTemplateReuters;
	
	private final JSONUtils jsonUtils = new JSONUtils();
	

	public MockPriceFeed(String vendorId, long delayInSeconds, long periodInSeconds) {
		super();
		this.delayInSeconds = delayInSeconds;
		this.periodInSeconds = periodInSeconds;
	}

	@Override
	public void runTask() {
		try {
			PriceImpl price = createMockPrice("Bloomberg");
			producerTemplateBloomberg.sendBodyAndHeader(jsonUtils.mapToJson(price), "vendorId", "Bloomberg");
			price = createMockPrice("Reuters");
			producerTemplateReuters.sendBodyAndHeader(jsonUtils.mapToJson(price), "vendorId", "Reuters");
		} catch (CamelExecutionException | JsonProcessingException e) {
			LOG.error("Unable to parse price", e);
		}
		
	}

	private PriceImpl createMockPrice(String vendorId) {
		int position = (int) (Math.random() * 5);
		String isin = Constants.TEST_ISINS.get(position);
		return new PriceImpl(isin, vendorId, "ISIN", "Government Bond", "Clean Price", new Date(), generatePrice(), generatePrice(), true);
	}

	private BigDecimal generatePrice() {
		return new BigDecimal(100 * Math.random()).setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	@Override
	public String getServicename() {
		return "Mock price feed";
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
