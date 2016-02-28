package com.sebarber.mizuho;

import org.junit.runner.RunWith;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.sebarber.mizuho.utils.JSONUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MizuhoApplication.class)
@WebAppConfiguration
public abstract class ControllerTest {

	protected MockMvc mockMvc;

	protected JSONUtils jsonUtils;

	@Autowired
	protected WebApplicationContext webApplicationContext;

	protected void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		jsonUtils = new JSONUtils();
	}

}
