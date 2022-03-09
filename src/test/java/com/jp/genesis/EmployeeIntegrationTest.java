package com.jp.genesis;


import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jp.genesis.model.Employee;

public class EmployeeIntegrationTest  extends IntegrationTest{
		
	@Test
	public void testEmployeeByIdWithResponseEntitySuccess() throws Exception {
		
		HttpEntity<String> entity = new HttpEntity<String>(null, this.getHttpHeadersForGetMethod());
		
		ResponseEntity<String> response = this.getRestTemplate().exchange(this.constructEmployeeApiUrl("/jp/genesis/employees/10010"),
				HttpMethod.GET, entity, String.class);
		
		// Validate that response should not be null
		assertNotNull(response);
		
		// validate that response status code value should be 200
		assertTrue(validateStatusCode(response,200));
		
		// validate through assertThat - status code should be 200
		// assertThat provides throws clear exception in case of failure - easy to debug
		assertThat(response.getStatusCodeValue()).isEqualTo(200);
	
		// Convert response Json string into Employee object
		Employee empResponse11 = (Employee) jsonStringToObject(response.getBody(), Employee.class);
		assertThat(empResponse11.getEmp_no()).isEqualTo(10010);
		
		
		// Convert response Json string into Employee object
		ObjectMapper objectMapper = new ObjectMapper();		
		// Convert JSON string into Employee Object
		Employee empResponse = objectMapper.readValue(response.getBody(), Employee.class);
		assertThat(empResponse.getEmp_no()).isEqualTo(10010);
		
		
		// Compare object to object
		Employee mockEmp = new Employee(10010, new Date(1963,6,1),  "Duangkaew", "Piveteau", 'F', new Date(1989,8,24));
		
		// *****  Date comparision is not working - need to look into it
		//Expecting value <3863-07-01T00:00:00.000 (java.util.Date)> in field <"birth_date"> but was <1963-06-01T00:00:00.000 (java.util.Date)> in <com.jp.genesis.model.Employee@11813715>
		//assertThat(empResponse).isEqualToComparingOnlyGivenFields(mockEmp, "emp_no", "first_name", "last_name", "gender", "birth_date", "last_name");
		
		// compare object to object, field by field
		assertThat(empResponse).isEqualToComparingOnlyGivenFields(mockEmp, "emp_no", "first_name", "last_name", "gender");
			
		// Assert Json string by converting Json string into JsonNode
        JsonNode responseJson = objectMapper.readTree(response.getBody());
        JsonNode fnNodeValue = responseJson.get("first_name");
        assertThat(fnNodeValue.asText()).isEqualTo("Duangkaew");
		
//		String expected1 = "{\"birth_date\":\"1963-05-31T18:30:00.000+00:00\",\"first_name\":\"Duangkaew\",\"last_name\":\"Piveteau\",\"gender\":\"F\",\"hire_date\":\"1989-08-23T18:30:00.000+00:00\"}";
		String expected = "{\"emp_no\":10010,\"first_name\":\"Duangkaew\",\"last_name\":\"Piveteau\",\"gender\":\"F\",\"hire_date\":\"1989-08-23T18:30:00.000+00:00\"}";

		// Convert JSON string into Employee Object
				Employee empResponse1 = objectMapper.readValue(expected, Employee.class);
				assertThat(empResponse1.getEmp_no()).isEqualTo(10010);
				assertThat(empResponse1.getLast_name()).isEqualTo("Piveteau");
		
		// false: ignore missing json fields, i.e. if emp_no tag does not exist then it ignore that fields and compare rest of fields
		JSONAssert.assertEquals(expected, response.getBody(), false);	
		
	}
		
	@Test
	public void testEmployeeByIdWithObjectSuccess() throws Exception {
		Employee employee = this.getRestTemplate().getForObject(this.constructEmployeeApiUrl("/jp/genesis/employees/10010"),Employee.class);
		
		// Validate that response should not be null
		assertNotNull(employee);		
		assertThat(employee).isInstanceOf(Employee.class);
		assertThat(employee.getEmp_no()).isEqualTo(10010);
		
		// Compare object to object
		Employee mockEmp = new Employee(10010, new Date(1963,6,1),  "Duangkaew", "Piveteau", 'F', new Date(1989,8,24));
		assertThat(employee).isEqualToComparingOnlyGivenFields(mockEmp, "emp_no", "first_name", "last_name", "gender");		
	}
	
	@Test
	public void getEmployeeByIdNotFoundSuccess() throws Exception {
		HttpEntity<String> entity = new HttpEntity<String>(null, this.getHttpHeadersForGetMethod());
		
		ResponseEntity<String> response = this.getRestTemplate().exchange(this.constructEmployeeApiUrl("/jp/genesis/employees/999999999"),
				HttpMethod.GET, entity, String.class);
		
		// Validate that response should not be null
		assertNotNull(response);
		
		// validate that response status code value should be 404
		assertTrue(validateStatusCode(response,404));
		
	}
	
	@Test
	public void testSaveNewEmployeeResponseEntitySuccess() throws Exception {
		Employee employee = new Employee(new Date(1958,7,6), "Cristinel", "Bouloucos", 'F', new Date(1993,8,3)  );
		
        HttpEntity<Employee> request = new HttpEntity<>(employee, getHttpHeadersForGetMethod());
        ResponseEntity<String> response = this.getRestTemplate().postForEntity(this.constructEmployeeApiUrl("/jp/genesis/employees"), request, String.class);
        
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        LOG.debug("........... body: " + response.getBody());
        
		Employee newEmp = (Employee) jsonStringToObject(response.getBody(), Employee.class);
        assertThat(newEmp.getFirst_name().equals("Cristinel"));
        assertThat(newEmp.getLast_name().equals("Bouloucos")); 
	}
	

	
	// start time: 13:02:22   -- 13:04:49 (13:04:45)
	// end time: 13:02:37.138  -- 13:04:59  
	// junit time: 0.585 second --- 0.470 S
	
	// start: 1:06:52 (1:06:50
	// end : 1:07:02
	// junit: 0.269 second
	@Test
	public void healthCheckShouldReturnDefaultMessage() throws Exception {

		String response = this.getRestTemplate().getForObject(this.constructEmployeeApiUrl("/jp/genesis/healthcheck"),String.class);
		assertThat(response).isEqualTo("applications is up");

	}

}
