package com.sebarber.mizuho.mock;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.sebarber.mizuho.domain.PriceImpl;
import com.sebarber.mizuho.utils.Constants;

@Component
public class MockInternalJMSConsumer extends RouteBuilder{
	
	private static final Logger LOG = Logger.getLogger(MockInternalJMSConsumer.class);
	

	@Override
	public void configure() throws Exception {
		from(Constants.TOPIC_PRICE_POJO_INTERNAL).process(new Processor() {
			
			@Override
			public void process(Exchange exchange) throws Exception {
				
				PriceImpl price = exchange.getIn().getBody(PriceImpl.class);
				LOG.info("Received price on internal JMS feed: " + price);
			}
		});
		
	}
	
	

}
