package com.sebarber.mizuho.domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by B on 26/02/2016.
 */
public class PriceImpl implements Price{


    private static final long serialVersionUID = 9008837697521105465L;

    private PricePk pricePk;
    private String idType;
    private String instrumentType;
    private String priceType;
    private Date created;
    private BigDecimal bid;
    private BigDecimal ask;   
    private boolean isActive;
    
    public PriceImpl(){
    	this.pricePk = new PricePk(null, null);
    	this.isActive = true;
    }

    public PriceImpl(String instrumentId, String vendorId, String idType, String instrumentType, String priceType, Date created, BigDecimal bid,
			BigDecimal ask, boolean isActive) {
		super();
		this.pricePk = new PricePk(instrumentId, vendorId);
		this.idType = idType;
		this.instrumentType = instrumentType;
		this.priceType = priceType;
		this.created = created;
		this.bid = bid;
		this.ask = ask;
		this.isActive = isActive;
	}
    
    public PricePk getPricePk(){
    	return pricePk;
    }
    
    public String getInstrumentId(){
    	return pricePk.getInstrumentId();
    }
    
    public String getVendorId(){
    	return pricePk.getVendorId();
    }

	public String getIdType() {
        return idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public String getInstrumentType() {
        return instrumentType;
    }

    public void setInstrumentType(String instrumentType) {
        this.instrumentType = instrumentType;
    }

    public String getPriceType() {
        return priceType;
    }

    public void setPriceType(String priceType) {
        this.priceType = priceType;
    }

    public BigDecimal getBid() {
        return bid;
    }

    public void setBid(BigDecimal bid) {
        this.bid = bid;
    }

    public BigDecimal getAsk() {
        return ask;
    }

    public void setAsk(BigDecimal ask) {
        this.ask = ask;
    }

	@Override
	public int hashCode() {
		return pricePk.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PriceImpl other = (PriceImpl) obj;
		return other.getPricePk().equals(this.pricePk);
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	@Override
	public String toString() {
		return "Price [pricePk=" + pricePk + ", idType=" + idType + ", instrumentType=" + instrumentType
				+ ", priceType=" + priceType + ", created=" + created + ", bid=" + bid + ", ask=" + ask + "]";
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	
	

   
}
