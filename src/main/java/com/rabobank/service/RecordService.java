package com.rabobank.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.rabobank.dto.ErrorDetails;
import com.rabobank.dto.ErrorRecords;
import com.rabobank.dto.Record;
import com.rabobank.utils.Constants;

@Service
public class RecordService {
	/**
	 * Service method to process records 1. by checking duplicate reference numbers
	 * 2. by checking end balance
	 */
	public ErrorDetails processStatement(List<Record> record) throws Exception {
		Boolean checkForDuplicates = false;
		Boolean checkForIncorrectEndBalance = false;
		List<ErrorRecords> errorRecords = new ArrayList<ErrorRecords>();
		// iterating the input records
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

	/**
	 * Method to check end balance for all records end balance = start balance +/-
	 * mutation
	 */
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

	/**
	 * Method to check duplicate records records by reference number
	 */
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
