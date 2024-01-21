package com.intuit.ubercraftdemo.exception.advice;

import com.intuit.ubercraftdemo.exception.InvalidDriverStatusTransitionException;
import com.intuit.ubercraftdemo.exception.InvalidFileTypeException;
import com.intuit.ubercraftdemo.exception.InvalidStepModificationException;
import com.intuit.ubercraftdemo.exception.NoCaseAssignedException;
import com.intuit.ubercraftdemo.exception.NoSuchRecordException;
import java.nio.file.NoSuchFileException;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.web.servlet.MultipartProperties;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@AllArgsConstructor
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

	private final MultipartProperties multipartProperties;

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(
		HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status,
		WebRequest request) {
		String error = "Malformed JSON request";
		return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, error, ex));
	}

	@Override
	protected ResponseEntity<Object> handleMaxUploadSizeExceededException(
		MaxUploadSizeExceededException ex, HttpHeaders headers, HttpStatusCode status,
		WebRequest request) {
		ApiError apiError = new ApiError(HttpStatus.PAYLOAD_TOO_LARGE, ex);
		apiError.setMessage(String.format("File size exceeds maximum allowed limit of %d MB.",
			multipartProperties.getMaxFileSize().toMegabytes()));
		return buildResponseEntity(apiError);
	}


	@ExceptionHandler(InvalidStepModificationException.class)
	public ResponseEntity<Object> handleInvalidStepState(InvalidStepModificationException ex) {
		ApiError apiError = new ApiError(HttpStatus.METHOD_NOT_ALLOWED);
		apiError.setMessage(ex.getMessage());
		return buildResponseEntity(apiError);
	}

	@ExceptionHandler(NoCaseAssignedException.class)
	public ResponseEntity<Object> handleCaseNotAssignedException(NoCaseAssignedException ex) {
		ApiError apiError = new ApiError(HttpStatus.NOT_FOUND);
		apiError.setMessage(ex.getMessage());
		return buildResponseEntity(apiError);
	}

	@ExceptionHandler(NoSuchFileException.class)
	public ResponseEntity<Object> handleNoSuchFileException(NoSuchFileException ex) {
		ApiError apiError = new ApiError(HttpStatus.NOT_FOUND);
		apiError.setMessage(ex.getMessage());
		return buildResponseEntity(apiError);
	}
	@ExceptionHandler(InvalidFileTypeException.class)
	public ResponseEntity<Object> handleInvalidFileTypeSpecified(InvalidFileTypeException ex) {
		ApiError apiError = new ApiError(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
		apiError.setMessage(ex.getMessage());
		return buildResponseEntity(apiError);
	}

	@ExceptionHandler(InvalidDriverStatusTransitionException.class)
	public ResponseEntity<Object> handleInvalidDriverStatusTransition(InvalidFileTypeException ex) {
		ApiError apiError = new ApiError(HttpStatus.METHOD_NOT_ALLOWED);
		apiError.setMessage(ex.getMessage());
		return buildResponseEntity(apiError);
	}

	@ExceptionHandler(NoSuchRecordException.class)
	public ResponseEntity<Object> handleNoSuchRecordFoundException(NoSuchRecordException ex) {
		ApiError apiError = new ApiError(HttpStatus.NOT_FOUND);
		apiError.setMessage(ex.getMessage());
		return buildResponseEntity(apiError);
	}


	private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
		return new ResponseEntity<>(apiError, apiError.getStatus());
	}
}