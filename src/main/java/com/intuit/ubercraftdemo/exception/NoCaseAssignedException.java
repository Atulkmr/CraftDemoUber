package com.intuit.ubercraftdemo.exception;

public class NoCaseAssignedException extends IllegalStateException{
	public NoCaseAssignedException() {
		super("You don't have an assigned case. Please use the /assign API to get a new case.");
	}
}
