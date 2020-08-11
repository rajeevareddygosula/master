package com.rabobank.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.rabobank.dto.ErrorDetails;
import com.rabobank.dto.Record;
import com.rabobank.service.RecordService;
import com.rabobank.utils.Constants;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@ControllerAdvice
@RequestMapping(value = "/api")
@Api(value = "Rest APIs")
public class RecordProcessController extends ResponseEntityExceptionHandler {
	@Autowired
	private RecordService recordService;
	private ErrorDetails errorDetails;

	@RequestMapping(value = "/record", method = RequestMethod.POST)
	@ApiOperation(value = "processing customer statment", response = Iterable.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "SUCCESSFUL"),
			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
			@ApiResponse(code = 500, message = "Something went wrong with server. Please try after some times") })
	public ResponseEntity<?> processStatement(@RequestBody List<@Valid Record> record) {
		try {
			errorDetails = recordService.processStatement(record);
			return ResponseEntity.ok(errorDetails);
		} catch (Exception e) {
			errorDetails = new ErrorDetails();
			errorDetails.setResult(Constants.INTERNAL_SERVER_ERROR);
			return new ResponseEntity<Object>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		errorDetails = new ErrorDetails();
		errorDetails.setResult(Constants.BAD_REQUEST);
		return new ResponseEntity<Object>(errorDetails, HttpStatus.BAD_REQUEST);
	}
}
