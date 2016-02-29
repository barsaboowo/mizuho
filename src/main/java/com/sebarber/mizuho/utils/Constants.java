package com.sebarber.mizuho.utils;

import java.util.List;

import com.google.common.collect.Lists;

public final class Constants {
	private Constants(){}
	
	public static final long ONE_DAY_IN_MILLIS = 24*3600*100L;
	public static final List<String> TEST_ISINS = Lists.newArrayList("DE000DG6CF68","DE000JPM85H5","XS1237672412","XS1289335736","XS1289354877");
	public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
	public static final String TOPIC_PRICE_POJO_INTERNAL = "activemq:topic:com.pricefeed.internal";
	
}
