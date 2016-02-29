package com.sebarber.mizuho;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.google.common.collect.Sets;
import com.sebarber.mizuho.domain.PriceImpl;


public class PriceControllerTest extends ControllerTest {
	private static final PriceImpl testPrice = new PriceImpl("instrumentId", "vendorId", "idType", "instrumentType",
			"priceType", new Date(), BigDecimal.ONE, BigDecimal.TEN, true);

	@Before
	public void setup() {
		super.setup();
	}

	@Test
	public void testPriceController() throws Exception {
		Set<PriceImpl> prices = Sets.newHashSet(testPrice);
		String json = jsonUtils.mapToJson(prices);

		String uri = "/prices/create";
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON).content(json)).andReturn();

		String message = result.getResponse().getContentAsString();
		int status = result.getResponse().getStatus();
		
		Assert.assertEquals(HttpStatus.OK.value(), status);
		Assert.assertEquals("All prices processed successfully", message);	
		
	}
}
