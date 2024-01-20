package com.intuit.ubercraftdemo.advice;

import java.util.Collection;
import org.springframework.http.MediaType;

public class InvalidFileTypeException extends IllegalArgumentException {
	public InvalidFileTypeException(Collection<String> acceptedFileTypes, String suppliedFileType) {
		super(String.format("Supplied a %s, expected file should be %s", suppliedFileType, acceptedFileTypes));
	}
}
