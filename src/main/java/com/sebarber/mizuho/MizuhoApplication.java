package com.sebarber.mizuho;

import java.util.Set;

import org.apache.activemq.camel.component.ActiveMQComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.google.common.collect.Sets;
import com.sebarber.mizuho.consumer.Consumer;
import com.sebarber.mizuho.consumer.PriceServiceJMSConsumer;
import com.sebarber.mizuho.data.Dao;
import com.sebarber.mizuho.data.PriceDao;
import com.sebarber.mizuho.domain.PriceImpl;
import com.sebarber.mizuho.domain.PricePk;
import com.sebarber.mizuho.endpoint.PriceJMSEndpoint;
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

	private final Dao<PricePk, PriceImpl> priceStore = new PriceDao();
	
	private final Validator<PriceImpl> priceValidator = new PriceValidator();
	private final TimerTask mockPriceFeed = new MockPriceFeed("bloomberg", 10, 2);
	private final PriceServiceJMSConsumer<PriceImpl> priceServiceConsumer = new PriceServiceJMSConsumer<>();

	@Autowired
	private TimerService timerService;
	
	@Autowired
	private PriceJMSEndpoint<PriceImpl> priceJMSEndpoint;
	
	@Autowired
	private ActiveMQComponent activeMQComponent;	

	public static void main(String[] args) {
		SpringApplication.run(MizuhoApplication.class, args);
	}
	
	@Bean
	public PriceJMSEndpoint<PriceImpl> priceJMSEndpoint(){
		return new PriceJMSEndpoint<>(PriceImpl.class, jmsEndpoints(), Constants.DATE_FORMAT);
	}
	
	@Bean
	public TimerTask mockPriceFeed() {
		return mockPriceFeed; 
	}
	
	@Bean
	public PriceServiceJMSConsumer<PriceImpl> priceConsumer(){
		return priceServiceConsumer;
	}

	@Bean
	public ActiveMQComponent activeMQComponent() {
		ActiveMQComponent activeMqComponent = new ActiveMQComponent();
		activeMqComponent.setBrokerURL("vm://localhost?broker.persistent=false");
		return activeMqComponent;
	}

	@Bean
	public PriceServiceJMSConsumer<PriceImpl> priceServiceConsumer() {
		return new PriceServiceJMSConsumer<PriceImpl>();
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
	public Dao<PricePk, PriceImpl> priceStore() {
		return priceStore;
	}

	@Bean
	public PriceService<PriceImpl> priceService(Set<Consumer<PriceImpl>> priceConsumers, Dao<PricePk,PriceImpl> priceStore) {
		return new PriceServiceImpl(30, 60, 60, priceStore, priceConsumers);
	}

	@Bean
	public Set<TimerTask> timerTasks() {
		return Sets.newHashSet((TimerTask) priceService(priceConsumers(), priceStore), mockPriceFeed());
	}

	@Bean
	public Set<Consumer<PriceImpl>> priceConsumers() {
		return Sets.<Consumer<PriceImpl>>newHashSet(priceServiceConsumer());
	}

	@Bean
	public Validator<PriceImpl> priceValidator() {
		return priceValidator;
	}

}
