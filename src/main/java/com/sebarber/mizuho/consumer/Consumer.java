package com.sebarber.mizuho.consumer;

import com.sebarber.mizuho.domain.Price;

public interface Consumer<P extends Price> {
	void consume(P price);
	String getConsumerName();
}
