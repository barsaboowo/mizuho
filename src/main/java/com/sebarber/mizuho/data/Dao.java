package com.sebarber.mizuho.data;

import java.util.Set;

import com.sebarber.mizuho.domain.Entity;

public interface Dao<K, E extends Entity> {

	E get(K key);
	Set<E> getAll();
	
	void put(K key, E entity);
	void delete(K key);
	
	
}
