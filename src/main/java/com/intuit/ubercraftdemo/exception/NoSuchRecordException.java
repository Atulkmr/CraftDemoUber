package com.intuit.ubercraftdemo.exception;

/**
 * Thrown to denote that a query resulted in no data found.
 */
public class NoSuchRecordException extends RuntimeException {

	public NoSuchRecordException(String queriedObject) {
		super(String.format("The specified %s doesn't exist. Please check the request.",
			queriedObject));
	}
}
