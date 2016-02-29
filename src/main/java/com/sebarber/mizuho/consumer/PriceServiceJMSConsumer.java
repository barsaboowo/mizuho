package com.sebarber.mizuho.consumer;

import org.apache.log4j.Logger;

import com.google.common.collect.ImmutableMap;
import com.sebarber.mizuho.domain.Price;
import com.sebarber.mizuho.utils.Constants;

import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;

public class PriceServiceJMSConsumer<P extends Price> implements Consumer<P> {
	
	@Produce(uri = Constants.TOPIC_PRICE_POJO_INTERNAL)
	private ProducerTemplate producerTemplate;

	private static final Logger LOG = Logger.getLogger(PriceServiceJMSConsumer.class);

	@Override
	public void consume(P price) {
		LOG.info("Publishing price to " + Constants.TOPIC_PRICE_POJO_INTERNAL);
		producerTemplate.sendBodyAndHeaders(price, 
				ImmutableMap.of("vendorId", price.getVendorId(), "instrumentId", price.getInstrumentId()));
	}

	@Override
	public String getConsumerName() {
		return "Price Service JMS Consumer";
	}

}
