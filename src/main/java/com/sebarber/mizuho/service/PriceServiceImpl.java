package com.sebarber.mizuho.service;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.HashMultimap;
import com.sebarber.mizuho.data.Dao;
import com.sebarber.mizuho.domain.Price;
import com.sebarber.mizuho.domain.PricePk;
import com.sebarber.mizuho.utils.Constants;
import com.sebarber.mizuho.validator.Validator;

public class PriceServiceImpl implements PriceService, TimerTask {
	private static final Logger LOG = Logger.getLogger(PriceServiceImpl.class);

	private Dao<PricePk, Price> priceStore;

	@Autowired
	private Validator<Price> priceValidator;

	private ReadWriteLock pricesLock = new ReentrantReadWriteLock(true);

	private final HashMultimap<String, Price> pricesByVendor = HashMultimap.create();
	private final HashMultimap<String, Price> pricesByInstrument = HashMultimap.create();

	private final long cacheAgeInDays;
	private final long cacheAgeInMillis;
	private final long timerDelaySeconds;
	private final long timerPeriodSeconds;
	private boolean isInit = false;

	public PriceServiceImpl(long cacheAgeInDays, long timerDelaySeconds, long timerPeriodSeconds, Dao<PricePk, Price> priceStore) {

		LOG.info("Cache age in days = " + cacheAgeInDays);
		LOG.info("Timer delay in seconds = " + timerDelaySeconds);
		LOG.info("Timer period in seconds = " + timerPeriodSeconds);
		this.priceStore = priceStore;

		this.cacheAgeInDays = cacheAgeInDays;
		this.cacheAgeInMillis = cacheAgeInDays * Constants.ONE_DAY_IN_MILLIS;
		this.timerDelaySeconds = timerDelaySeconds;
		this.timerPeriodSeconds = timerPeriodSeconds;
		init();
	}

	@Override
	public void init() {
		if (isInit) {
			return;
		}
		LOG.info("Priming caches");
		try {
			pricesLock.writeLock().lock();

			for (Price p : priceStore.getAll()) {
				pricesByVendor.put(p.getVendorId(), p);
				pricesByInstrument.put(p.getInstrumentId(), p);
			}

			LOG.info("Cache priming is complete");
			isInit = true;
		} catch (Exception e) {
			LOG.fatal("Unable to prime caches", e);
			throw e;
		} finally {
			pricesLock.writeLock().unlock();
		}
	}

	@Override
	public Set<Price> getPricesForVendor(String vendor) {
		Set<Price> prices;
		LOG.info("Getting prices for vendor " + vendor);
		try {
			pricesLock.readLock().lock();
			prices = new HashSet<Price>(pricesByVendor.get(vendor));
		} catch (Exception e) {
			LOG.error("Unable to get price for vendor " + vendor);
			throw e;
		} finally {
			pricesLock.readLock().unlock();
		}

		return prices;
	}

	@Override
	public Set<Price> getPricesForInstrumentId(String instrument) {
		Set<Price> prices;
		LOG.info("Getting prices for instrument " + instrument);
		try {
			pricesLock.readLock().lock();
			prices = new HashSet<Price>(pricesByInstrument.get(instrument));
		} catch (Exception e) {
			LOG.error("Unable to get price for instrument id " + instrument);
			throw e;
		} finally {
			pricesLock.readLock().unlock();
		}

		return prices;
	}

	@Override
	public void addOrUpdate(Price price) {
		LOG.info("Adding price with primary key " + price.getPricePk());
		try {
			priceValidator.validate(price);
			pricesLock.writeLock().lock();
			priceStore.put(price.getPricePk(), price);
			pricesByVendor.put(price.getPricePk().getVendorId(), price);
			pricesByInstrument.put(price.getInstrumentId(), price);
			LOG.info("Price added successfully");
		} catch (Exception e) {
			LOG.error("Unable to add price with pk " + price.getPricePk(), e);
			throw e;
		} finally {
			try {
				pricesLock.writeLock().unlock();
			} catch (Exception ignore) {
				//Exception is thrown if lock is not locked
			}
		}

	}

	@Override
	public void runTask() {
		LOG.info("Cleaning prices older than " + cacheAgeInDays + " days");
		final Date dateBefore = new Date(System.currentTimeMillis() - cacheAgeInMillis);
		try {
			pricesLock.readLock().lock();
			Set<Price> prices = pricesByInstrument.values().stream().filter(p -> p.getCreated().before(dateBefore))
					.collect(Collectors.toSet());
			pricesLock.readLock().unlock();
			if (!prices.isEmpty()) {
				LOG.info("Deleting " + prices.size() + " old prices");
				for (Price p : prices) {
					delete(p);
				}

				LOG.info("Prices cleaned successfully");
			} else {
				LOG.info("No prices to clean");
			}
		} catch (Exception e) {
			LOG.error("Unable to delete old prices", e);
			try {
				pricesLock.readLock().unlock();
			} catch (Exception ignore) {
				// will get an exception if the readlock is not locked
			}
		}
	}

	@Override
	public void delete(Price price) {
		LOG.info("Deleting price with primary key " + price.getPricePk());
		try {
			pricesLock.writeLock().lock();
			priceStore.delete(price.getPricePk());
			pricesByVendor.remove(price.getVendorId(), price);
			pricesByInstrument.remove(price.getInstrumentId(), price);
			LOG.info("Price added successfully");
		} catch (Exception e) {
			LOG.error("Unable to remove price with pk " + price.getPricePk(), e);
			throw e;
		} finally {
			pricesLock.writeLock().unlock();
		}

	}

	@Override
	public Set<Price> getAllPrices() {
		LOG.info("Getting all prices");
		Set<Price> prices;
		try {
			pricesLock.readLock().lock();
			prices = new HashSet<Price>(pricesByVendor.values());
			LOG.info("Prices retrieved successfully");
		} catch (Exception e) {
			LOG.error("Unable to retrieve all prices", e);
			throw e;
		} finally {
			pricesLock.readLock().unlock();
		}

		return prices;
	}

	@Override
	public String getServicename() {
		return "Price Service";
	}

	public Validator<Price> getPriceValidator() {
		return priceValidator;
	}

	public void setPriceValidator(Validator<Price> priceValidator) {
		this.priceValidator = priceValidator;
	}

	@Override
	public long getDelayInSeconds() {
		return timerDelaySeconds;
	}

	@Override
	public long getPeriodInSeconds() {
		return timerPeriodSeconds;
	}

}
