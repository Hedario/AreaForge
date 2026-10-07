package com.hedario.areaforge.exceptions;

public class AreaNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AreaNotFoundException(String name) {
        super("The area name for " + name + " couldn't be resolved, does it exist?");
    }
}
