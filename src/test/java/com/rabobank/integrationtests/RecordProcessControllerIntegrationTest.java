package com.rabobank.integrationtests;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.rabobank.dto.ErrorDetails;
import com.rabobank.dto.Record;
import com.rabobank.utils.Constants;

public class RecordProcessControllerIntegrationTest {

	private final static String RECORD_URI = "/api/record";

	@LocalServerPort
	private int port;

	TestRestTemplate restTemplate = new TestRestTemplate();
	HttpHeaders headers = new HttpHeaders();

	@Test
	@DisplayName("ig test: validating successful record")
	public void customerStatementSuccessfulTest() throws URISyntaxException {

		List<Record> record = getPositiveRecord();

		ResponseEntity<ErrorDetails> result = this.restTemplate.postForEntity(getURI(RECORD_URI), record,
				ErrorDetails.class);

		Assertions.assertEquals(200, result.getStatusCodeValue());
		Assertions.assertEquals(Constants.SUCCESSFUL, result.getBody().getResult());
		Assertions.assertEquals(0, result.getBody().getErrorRecords().size());

	}

	@Test
	@DisplayName("ig test: validating duplicate records")
	public void customerStatementDuplicateTest() throws URISyntaxException {

		List<Record> record = getDuplicateRecords();

		ResponseEntity<ErrorDetails> result = this.restTemplate.postForEntity(getURI(RECORD_URI), record,
				ErrorDetails.class);

		Assertions.assertEquals(200, result.getStatusCodeValue());
		Assertions.assertEquals(Constants.DUPLICATE_REFERENCE, result.getBody().getResult());

	}

	@Test
	@DisplayName("ig test: validating bad records")
	public void customerStatementBadRequestTest() throws URISyntaxException {

		ResponseEntity<ErrorDetails> result = this.restTemplate.postForEntity(getURI(RECORD_URI), getEntity(""),
				ErrorDetails.class);

		Assertions.assertEquals(400, result.getStatusCodeValue());
		Assertions.assertEquals(Constants.BAD_REQUEST, result.getBody().getResult());

	}

	private URI getURI(String urlString) throws URISyntaxException {

		final String baseUrl = "http://localhost:" + port + RECORD_URI;
		URI uri = new URI(baseUrl);

		return uri;
	}

	private HttpEntity<String> getEntity(Object body) {

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> entity = new HttpEntity<String>(body.toString(), headers);

		return entity;
	}

	private List<Record> getPositiveRecord() {

		List<Record> record = new ArrayList<>();

		Record r1 = new Record();
		r1.setReference(194261L);
		r1.setAccountNumber("NL91RABO0315273637");
		r1.setDescription("Clothes from Jan Bakker");
		r1.setStartBalance(BigDecimal.valueOf(21.6));
		r1.setMutation(BigDecimal.valueOf(-41.83));
		r1.setEndBalance(BigDecimal.valueOf(-20.23));
		record.add(r1);

		return record;
	}

	private List<Record> getDuplicateRecords() {

		List<Record> records = new ArrayList<>();

		Record r1 = new Record();
		r1.setReference(194261L);
		r1.setAccountNumber("NL91RABO0315273637");
		r1.setDescription("Clothes from Jan Bakker");
		r1.setStartBalance(BigDecimal.valueOf(21.6));
		r1.setMutation(BigDecimal.valueOf(-41.83));
		r1.setEndBalance(BigDecimal.valueOf(-20.23));
		records.add(r1);

		Record r2 = new Record();
		r2.setReference(194261L);
		r2.setAccountNumber("NL91RABO0315273637");
		r2.setDescription("Clothes from Jan Bakker");
		r2.setStartBalance(BigDecimal.valueOf(21.6));
		r2.setMutation(BigDecimal.valueOf(-41.83));
		r2.setEndBalance(BigDecimal.valueOf(-20.23));

		records.add(r2);

		return records;
	}

}
