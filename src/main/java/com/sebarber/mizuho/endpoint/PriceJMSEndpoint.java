package com.sebarber.mizuho.endpoint;

import java.util.Set;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.gson.GsonDataFormat;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.sebarber.mizuho.domain.Price;
import com.sebarber.mizuho.service.PriceService;

public class PriceJMSEndpoint<P extends Price> extends RouteBuilder {
	private static final Logger LOG = Logger.getLogger(PriceJMSEndpoint.class);

	@Autowired
	private PriceService<P> priceService;

	private final GsonDataFormat format;

	private final Set<String> jmsEndpoints;

	private final Class<P> priceClass;

	public PriceJMSEndpoint(Class<P> priceClass, Set<String> jmsEndpoints, String dateFormat) {
		super();
		this.format = new GsonDataFormat(priceClass);		
		this.priceClass = priceClass;
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
					P price = exchange.getIn().getBody(priceClass);
					LOG.info("Received price via jms: " + price.getVendorId() + ", " + price.getInstrumentId());
				}
			}).bean(priceService, "addOrUpdate");
		}

	}

}
