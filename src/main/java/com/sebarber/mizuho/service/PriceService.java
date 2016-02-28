package com.sebarber.mizuho.service;

import java.util.Set;

import com.sebarber.mizuho.domain.Price;

public interface PriceService {
	Set<Price> getPricesForVendor(String vendor);
	Set<Price> getPricesForInstrumentId(String instrument);
	Set<Price> getAllPrices();
	void addOrUpdate(Price price);
	void delete(Price price);
	void init();
}
