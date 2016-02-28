package com.sebarber.mizuho.service;

public interface TimerTask {
	void runTask();
	String getServicename();
	long getDelayInSeconds();
	long getPeriodInSeconds();
}
