package com.rabobank.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.rabobank.dto.Record;
import com.rabobank.dto.ErrorDetails;
import com.rabobank.dto.ErrorRecords;
import com.rabobank.utils.Constants;

@Service
public class RecordService {

	public ErrorDetails processStatement(List<Record> record) throws Exception {

		Boolean checkForDuplicates = false;
		Boolean checkForIncorrectEndBalance = false;

		List<ErrorRecords> errorRecords = new ArrayList<ErrorRecords>();

		for (int i = 0; i < record.size(); i++) {
			for (int j = i + 1; j < record.size(); j++) {

				checkForDuplicates = checkForDuplicateRecords(record, checkForDuplicates, errorRecords, i, j);

			}

			BigDecimal endBalance = record.get(i).getStartBalance().add(record.get(i).getMutation());

			checkForIncorrectEndBalance = checkForEndBalance(record, checkForIncorrectEndBalance, errorRecords, i,
					endBalance);
		}

		ErrorDetails errorDetails = processingResponse(checkForDuplicates, checkForIncorrectEndBalance, errorRecords);

		return errorDetails;
	}

	private ErrorDetails processingResponse(Boolean checkForDuplicates, Boolean checkForIncorrectEndBalance,
			List<ErrorRecords> errorRecords) {
		ErrorDetails errorDetails = new ErrorDetails();

		errorDetails.setErrorRecords(errorRecords);

		if (checkForDuplicates && checkForIncorrectEndBalance)
			errorDetails.setResult(Constants.DUPLICATE_REFERENCE_INCORRECT_END_BALANCE);
		else if (checkForDuplicates)
			errorDetails.setResult(Constants.DUPLICATE_REFERENCE);
		else if (checkForIncorrectEndBalance)
			errorDetails.setResult(Constants.INCORRECT_END_BALANCE);
		else
			errorDetails.setResult(Constants.SUCCESSFUL);
		return errorDetails;
	}

	private Boolean checkForEndBalance(List<Record> record, Boolean checkForIncorrectEndBalance,
			List<ErrorRecords> errorRecords, int i, BigDecimal endBalance) {
		if (endBalance.compareTo(record.get(i).getEndBalance()) != 0) {
			checkForIncorrectEndBalance = true;

			ErrorRecords errorRecord = new ErrorRecords();
			errorRecord.setReference(record.get(i).getReference());
			errorRecord.setAccountNumber(record.get(i).getAccountNumber());
			errorRecords.add(errorRecord);
		}
		return checkForIncorrectEndBalance;
	}

	private Boolean checkForDuplicateRecords(List<Record> record, Boolean checkForDuplicates,
			List<ErrorRecords> errorRecords, int i, int j) {
		if (record.get(i).getReference().equals(record.get(j).getReference())) {
			checkForDuplicates = true;
			ErrorRecords errorRecord = new ErrorRecords();
			errorRecord.setReference(record.get(i).getReference());
			errorRecord.setAccountNumber(record.get(j).getAccountNumber());
			errorRecords.add(errorRecord);

		}
		return checkForDuplicates;
	}

}
