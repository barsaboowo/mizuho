package com.sebarber.mizuho.data;

import com.sebarber.mizuho.domain.PricePk;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.sebarber.mizuho.domain.Price;

public class PriceDao implements Dao<PricePk, Price> {
	private static final Logger LOG = Logger.getLogger(PriceDao.class);
	private final Map<PricePk, Price> priceMap = new HashMap<>();

	@Override
	public Price get(PricePk key) {
		if (key == null) {
			LOG.warn("Null key passed as argument to get");
			return null;
		}
		LOG.info("Getting price with key " + key);
		return priceMap.get(key);
	}

	@Override
	public Set<Price> getAll() {
		LOG.info("Getting all prices");
		return new HashSet<Price>(priceMap.values());
	}

	@Override
	public void put(PricePk key, Price entity) {
		if (key == null || entity == null) {
			throw new IllegalArgumentException("Cannot insert null values");
		}
		LOG.info("Adding price with key " + key);
		if(priceMap.get(key) != null){
			LOG.warn("Replacing existing value for key " + key);
		}
		priceMap.put(key, entity);
	}

	@Override
	public void delete(PricePk key) {
		if (key == null) {
			LOG.warn("Null key passed as argument to delete");
		} else {
			LOG.info("Deleting record with key " + key);
			Price value = priceMap.remove(key);
			if (value == null) {
				LOG.warn("no record found with key " + key);
			}
		}
	}

}
