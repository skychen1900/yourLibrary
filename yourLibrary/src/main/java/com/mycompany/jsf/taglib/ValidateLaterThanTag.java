package com.mycompany.jsf.taglib;

import com.mycompany.jsf.validator.LaterThanValidator;
import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.webapp.ValidatorTag;

/**
 * This class is a tag handler that creates and configures a
 * "com.mycompany.jsf.validator.LATER_THAN" validator.
 *
 * @author Hans Bergsten, Gefion Software <hans@gefionsoftware.com>
 * @version 1.0
 */
public class ValidateLaterThanTag extends ValidatorTag {
    private String peerId;

    /**
     * Sets the client ID for the component holding the value to
     * compare the value of the component with this validator to.
     */
    public void setThan(String peerId) {
	this.peerId = peerId;
    }

    /**
     * Returns a new instance of the validator registered under the name
     * "com.mycompany.jsf.validator.LATER_THAN", configured with the
     * "than" property value.
     */
    protected Validator createValidator() {
	Application application = 
	    FacesContext.getCurrentInstance().getApplication();
	LaterThanValidator validator = (LaterThanValidator)
	    application.createValidator("com.mycompany.jsf.validator.LATER_THAN");
	validator.setPeerId(peerId);
	return validator;
    }
}
