package com.sebarber.mizuho.endpoint;

import java.util.Set;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.gson.GsonDataFormat;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.sebarber.mizuho.domain.Price;
import com.sebarber.mizuho.validator.Validator;

public class PriceJMSPublisher extends RouteBuilder {
	private static final Logger LOG = Logger.getLogger(PriceJMSPublisher.class);
	
	@Autowired
	private Validator<Price> priceValidator;

	private final GsonDataFormat format = new GsonDataFormat(Price.class);

	private final Set<String> jmsEndpoints;

	private final String jmsTopic;

	public PriceJMSPublisher(Set<String> jmsEndpoints, String jmsTopic, String dateFormat) {
		super();
		this.jmsTopic = jmsTopic;
		this.jmsEndpoints = jmsEndpoints;
		format.setDateFormatPattern(dateFormat);
	}

	@Override
	public void configure() throws Exception {
		for (String jms : jmsEndpoints) {
			LOG.info("Setting publish route for " + jms);
			from(jms).unmarshal(format).process(new Processor() {
				
				@Override
				public void process(Exchange exchange) throws Exception {
					Price p = exchange.getIn().getBody(Price.class);
					LOG.info("Validating price " + p );
					priceValidator.validate(p);
					LOG.info("Price validated successfully, will be published to " + jmsTopic);
				}
			}).to(jmsTopic);
		}

	}

}
