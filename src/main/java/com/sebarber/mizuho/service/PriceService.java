package com.sebarber.mizuho.service;

import java.util.Set;

import com.sebarber.mizuho.domain.Price;

public interface PriceService<P extends Price> {
	Set<P> getPricesForVendor(String vendor);
	Set<P> getPricesForInstrumentId(String instrument);
	Set<P> getAllPrices();
	void addOrUpdate(P price);
	void publish(P price);
	void delete(P price);
	void init();
}
