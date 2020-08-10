package com.rabobank.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.rabobank.dto.ErrorDetails;
import com.rabobank.dto.Record;
import com.rabobank.utils.Constants;


@SpringBootTest
@ActiveProfiles("test")
public class RecordServiceTest {

	@Autowired
	private RecordService recordService;

	@Test
	@DisplayName("junit: validating successful record")
	public void validateCustomerStatementSuccessfulRecord() throws Exception {

		List<Record> record = getPositiverecords();

		ErrorDetails errorDetails = recordService.processStatement(record);

		Assertions.assertEquals(Constants.SUCCESSFUL, errorDetails.getResult());
	}

	@Test
	@DisplayName("junit: validating duplicate records")
	public void validateCustomerStatementDuplicateRecord() throws Exception {

		List<Record> record = getDuplicateRecords();

		ErrorDetails errorDetails = recordService.processStatement(record);

		Assertions.assertEquals(Constants.DUPLICATE_REFERENCE, errorDetails.getResult());
	
	}


	private List<Record> getPositiverecords() {

		List<Record> customerStatement = new ArrayList<>();

		Record statement1 = new Record();
		statement1.setReference(194261L);
		statement1.setAccountNumber("NL91RABO0315273637");
		statement1.setDescription("Clothes from Jan Bakker");
		statement1.setStartBalance(BigDecimal.valueOf(21.6));
		statement1.setMutation(BigDecimal.valueOf(-41.83));
		statement1.setEndBalance(BigDecimal.valueOf(-20.23));
		customerStatement.add(statement1);

		return customerStatement;
	}

	private List<Record> getDuplicateRecords() {

		List<Record> record = new ArrayList<>();

		Record statement1 = new Record();
		statement1.setReference(194261L);
		statement1.setAccountNumber("NL91RABO0315273637");
		statement1.setDescription("Clothes from Jan Bakker");
		statement1.setStartBalance(BigDecimal.valueOf(21.6));
		statement1.setMutation(BigDecimal.valueOf(-41.83));
		statement1.setEndBalance(BigDecimal.valueOf(-20.23));
		record.add(statement1);

		Record statement2 = new Record();
		statement2.setReference(194261L);
		statement2.setAccountNumber("NL91RABO0315273637");
		statement2.setDescription("Clothes from Jan Bakker");
		statement2.setStartBalance(BigDecimal.valueOf(21.6));
		statement2.setMutation(BigDecimal.valueOf(-41.83));
		statement2.setEndBalance(BigDecimal.valueOf(-20.23));

		record.add(statement2);

		return record;
	}

	
}
