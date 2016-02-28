package com.sebarber.mizuho.endpoint;

import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.sebarber.mizuho.domain.Price;
import com.sebarber.mizuho.service.PriceService;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.gson.GsonDataFormat;

public class PriceServiceConsumer extends RouteBuilder {
	private static final Logger LOG = Logger.getLogger(PriceServiceConsumer.class);
	
	private final GsonDataFormat format = new GsonDataFormat(Price.class);
	
	private final Set<String> jmsEndpoints;

	public PriceServiceConsumer(Set<String> jmsEndpoints, String dateFormat){
		this.jmsEndpoints = jmsEndpoints;
		format.setDateFormatPattern(dateFormat);
	}

	@Autowired
	private PriceService priceService;

	@Override
	public void configure() throws Exception {
		for (String jms : jmsEndpoints) {
			LOG.info("Setting endpoint for " + jms);
			from(jms).unmarshal(format).bean(priceService, "addOrUpdate");
		}
	}

}
