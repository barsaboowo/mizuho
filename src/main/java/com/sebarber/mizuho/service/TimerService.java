package com.sebarber.mizuho.service;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class TimerService {
	private static final Logger LOG = Logger.getLogger(TimerService.class);
	
	private final ScheduledExecutorService scheduler;

	@Autowired
	private Set<TimerTask> timerTasks;

	public TimerService(int numThreads, Set<TimerTask> timerTasks) {
		
		LOG.info("Number of threads = " + numThreads);
		
		this.timerTasks = timerTasks;

		scheduler = Executors.newScheduledThreadPool(numThreads);

		init();
	}

	private void init() {

		for (TimerTask timerTask : timerTasks) {
			LOG.info("Scheduling new timer service for " + timerTask.getServicename());
			scheduler.scheduleAtFixedRate(new Runnable() {

				@Override
				public void run() {
					try {
						timerTask.runTask();
					} catch (Throwable e) {
						LOG.error("Unable to run timer task for " + timerTask.getServicename(), e);
					}
				}
			}, timerTask.getDelayInSeconds(), timerTask.getPeriodInSeconds(), TimeUnit.SECONDS);

		}
	}
}
