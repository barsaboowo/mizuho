package com.sebarber.mizuho.validator;

import javax.validation.ValidationException;

import com.sebarber.mizuho.domain.Entity;

public interface Validator<E extends Entity> {
	void validate(E entity) throws ValidationException;
}
