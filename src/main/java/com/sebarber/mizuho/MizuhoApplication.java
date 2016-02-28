package com.sebarber.mizuho;

import java.util.Collections;
import java.util.Set;

import org.apache.activemq.camel.component.ActiveMQComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.google.common.collect.Sets;
import com.sebarber.mizuho.data.Dao;
import com.sebarber.mizuho.data.PriceDao;
import com.sebarber.mizuho.domain.Price;
import com.sebarber.mizuho.domain.PricePk;
import com.sebarber.mizuho.endpoint.PriceServiceConsumer;
import com.sebarber.mizuho.service.MockPriceFeed;
import com.sebarber.mizuho.service.PriceService;
import com.sebarber.mizuho.service.PriceServiceImpl;
import com.sebarber.mizuho.service.TimerService;
import com.sebarber.mizuho.service.TimerTask;
import com.sebarber.mizuho.utils.Constants;
import com.sebarber.mizuho.validator.PriceValidator;
import com.sebarber.mizuho.validator.Validator;

@SpringBootApplication
public class MizuhoApplication {

	private final Dao<PricePk, Price> priceStore = new PriceDao();
	private final PriceService priceService = new PriceServiceImpl(30, 60, 60, priceStore);
	private final Validator<Price> priceValidator = new PriceValidator();
	private final TimerTask mockPriceFeed = new MockPriceFeed("bloomberg", 10, 2);

	@Autowired
	private TimerService timerService;

	@Autowired
	private PriceServiceConsumer priceServiceConsumer;

	@Autowired
	private ActiveMQComponent activeMQComponent;	

	public static void main(String[] args) {
		SpringApplication.run(MizuhoApplication.class, args);
	}

	@Bean
	public TimerTask mockPriceFeed() {
		return mockPriceFeed; 
	}

	@Bean
	public ActiveMQComponent activeMQComponent() {
		ActiveMQComponent activeMqComponent = new ActiveMQComponent();
		activeMqComponent.setBrokerURL("vm://localhost?broker.persistent=false");
		return activeMqComponent;
	}

	@Bean
	public PriceServiceConsumer priceServiceConsumer() {
		return new PriceServiceConsumer(jmsEndpoints(), Constants.DATE_FORMAT);
	}

	@Bean
	public TimerService timerService() {
		return new TimerService(3, timerTasks());
	}

	@Bean
	public Set<String> jmsEndpoints() {
		return Sets.newHashSet("activemq:topic:com.pricefeed.bloomberg", "activemq:topic:com.pricefeed.reuters");
	}

	@Bean
	public Dao<PricePk, Price> priceStore() {
		return priceStore;
	}

	@Bean
	public PriceService priceService() {
		return priceService;
	}

	@Bean
	public Set<TimerTask> timerTasks() {
		return Sets.newHashSet((TimerTask) priceService(), mockPriceFeed());
	}

	@Bean
	public Validator<Price> priceValidator() {
		return priceValidator;
	}

}
