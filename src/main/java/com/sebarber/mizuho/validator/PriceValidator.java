package com.sebarber.mizuho.validator;

import javax.validation.ValidationException;

import com.sebarber.mizuho.domain.Price;

public class PriceValidator implements Validator<Price> {

	@Override
	public void validate(Price entity) throws ValidationException {
		if(entity.getPricePk() == null){
			throw new ValidationException("Price does not have pk");
		}
		
		if(entity.getInstrumentId() == null){
			throw new ValidationException("No instrument id for price");
		}
		
		if(entity.getVendorId() == null){
			throw new ValidationException("No vendor id for price");
		}
		
		if(entity.getCreated() == null){
			throw new ValidationException("No created date");
		}
		
	}

}
