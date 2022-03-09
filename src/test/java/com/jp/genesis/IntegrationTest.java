package com.jp.genesis;


import static org.assertj.core.api.Assertions.assertThat;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.AtomicBooleanAssert;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jp.genesis.model.Employee;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource("/devtest.properties")
public abstract class IntegrationTest {
	public static final Logger LOG = LoggerFactory.getLogger(IntegrationTest.class);
	
	@LocalServerPort
	private int port;
	
	@Value( "${employeeApiUrl}" )
	private String employeeApiUrl;

	@Autowired
	private TestRestTemplate restTemplate;
	

	@Autowired
	private ObjectMapper objectMapper;
	
	public TestRestTemplate getRestTemplate() {		
		return this.restTemplate;
	}

	public HttpHeaders getHttpHeadersForGetMethod() {
		HttpHeaders headers = new HttpHeaders();
		
		List headerList = new ArrayList<MediaType>();
		headerList.add(MediaType.APPLICATION_JSON);
		headerList.add(MediaType.TEXT_PLAIN);		
		headers.setAccept(headerList);

		return headers;
	}
	
	public String constructEmployeeApiUrl(String pathString) {
		LOG.debug(".............. URL " + employeeApiUrl + pathString);
//		return "http://localhost:8090" + pathString;
		return employeeApiUrl + pathString;
	}
	
	public boolean validateStatusCode(ResponseEntity<?> response, int expectedStatusCode) {
			return (response.getStatusCodeValue() == expectedStatusCode);			
	}
	
	public Object jsonStringToObject(String jsonString, Class<?> objectClass) throws JsonProcessingException{		
		Object responseObject = objectMapper.readValue(jsonString, objectClass);
		return responseObject;
	}
	
}
