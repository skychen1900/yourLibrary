package com.mycompany.expense;

/**
 * This exception signals an error reading or writing a
 * report in the ReportRegistry.
 *
 * @author Hans Bergsten, Gefion Software <hans@gefionsoftware.com>
 * @version 1.0
 */
public class RegistryException extends Exception {
    public RegistryException() {
	super();
    }

    public RegistryException(String message) {
	super(message);
    }

    public RegistryException(String message, Throwable cause) {
	super(message, cause);
    }

    public RegistryException(Throwable cause) {
	super(cause);
    }
}
