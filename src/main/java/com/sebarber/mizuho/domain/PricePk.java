package com.sebarber.mizuho.domain;

import java.io.Serializable;

public class PricePk implements Serializable {
	private static final long serialVersionUID = 9008837697521105466L;
	
	private String instrumentId;
	private String vendorId;
	
	public PricePk(){}
	
	public PricePk(String instrumentId, String vendorId){
		this.instrumentId = instrumentId;
		this.vendorId = vendorId;
	}

	public String getInstrumentId() {
		return instrumentId;
	}

	public String getVendorId() {
		return vendorId;
	}	

	public void setInstrumentId(String instrumentId) {
		this.instrumentId = instrumentId;
	}

	public void setVendorId(String vendorId) {
		this.vendorId = vendorId;
	}

	@Override
	public String toString() {
		return "PricePk [instrumentId=" + instrumentId + ", vendorId=" + vendorId + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((instrumentId == null) ? 0 : instrumentId.hashCode());
		result = prime * result + ((vendorId == null) ? 0 : vendorId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PricePk other = (PricePk) obj;
		if (instrumentId == null) {
			if (other.instrumentId != null)
				return false;
		} else if (!instrumentId.equals(other.instrumentId))
			return false;
		if (vendorId == null) {
			if (other.vendorId != null)
				return false;
		} else if (!vendorId.equals(other.vendorId))
			return false;
		return true;
	}
	
	
	
}
