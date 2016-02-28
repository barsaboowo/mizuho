package com.sebarber.mizuho.utils;

import java.io.IOException;

import java.text.SimpleDateFormat;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONUtils {
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	public JSONUtils(){
		objectMapper.setDateFormat( new SimpleDateFormat(Constants.DATE_FORMAT));
	}
	
	public String mapToJson(Object obj) throws JsonProcessingException{
		return objectMapper.writeValueAsString(obj);
	}
	
	public <T> T mapFromJson(String json, Class<T> clazz) throws JsonParseException, JsonMappingException, IOException {
		return objectMapper.readValue(json, clazz);
	}
}
