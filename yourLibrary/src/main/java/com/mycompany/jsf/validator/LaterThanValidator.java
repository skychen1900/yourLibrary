package com.mycompany.jsf.validator;

import java.text.MessageFormat;
import java.io.Serializable;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 * This class is a JSF Validator that validates that the java.util.Date
 * value of the component it's attached to is later than the 
 * java.util.Date value of another component.
 *
 * @author Hans Bergsten, Gefion Software <hans@gefionsoftware.com>
 * @version 1.0
 */
public class LaterThanValidator implements Validator, Serializable {
    private String peerId;

    /**
     * Sets the ID for the peer component the main component's
     * value is compared to. It must be a format that can be
     * used with the javax.faces.component.UIComponent findComponent()
     * method.
     */
    public void setPeerId(String peerId) {
        this.peerId = peerId;
    }

    /**
     * Compares the provided value to the value of the peer component,
     * and throws a ValidatorException with an appropriate FacesMessage
     * if the provided value doesn't represent a later date or if
     * there are problems accessing the peer component value.
     */
    public void validate(FacesContext context, UIComponent component,
			 Object value) throws ValidatorException {

        Application application = context.getApplication();

	/* Note! This validator requires an application bundle
	 * that holds the messages. For validators, or other
	 * components, that should be usable in any application,
	 * bundling a default bundle and use it if the message
	 * isn't available in the application bundle is a nicer
	 * approach.
	 */
	String messageBundleName = application.getMessageBundle();
	Locale locale = context.getViewRoot().getLocale();
	ResourceBundle rb = 
	    ResourceBundle.getBundle(messageBundleName, locale);

        UIComponent peerComponent = component.findComponent(peerId);
        if (peerComponent == null) {
	    String msg = rb.getString("peer_not_found");
	    FacesMessage facesMsg = 
		new FacesMessage(FacesMessage.SEVERITY_FATAL, msg, msg);
	    throw new ValidatorException(facesMsg);
        }
        
        ValueHolder peer = null;
        try {
            peer = (ValueHolder) peerComponent;
        }
        catch (ClassCastException e) {
	    String msg = rb.getString("invalid_component");
	    FacesMessage facesMsg = 
		new FacesMessage(FacesMessage.SEVERITY_FATAL, msg, msg);
	    throw new ValidatorException(facesMsg);
        }
	if (peer instanceof EditableValueHolder &&
	    !((EditableValueHolder) peer).isValid()) {
	    // No point in validating against an invalid value
	    return;
	}
	
        Object peerValue = peer.getValue();
        if (!(peerValue instanceof Date)) {
	    String msg = rb.getString("invalid_peer");
	    FacesMessage facesMsg = 
		new FacesMessage(FacesMessage.SEVERITY_FATAL, msg, msg);
	    throw new ValidatorException(facesMsg);
        }
        
        if (!(value instanceof Date) || 
            !((Date) value).after((Date) peerValue)) {
	    String msg = rb.getString("not_later");
	    String detailMsgPattern = rb.getString("not_later_detail");
	    Object[] params = 
	        {formatDate((Date) peerValue, context, component)};
	    String detailMsg = 
		MessageFormat.format(detailMsgPattern, params);
	    FacesMessage facesMsg = 
		new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, detailMsg);
	    throw new ValidatorException(facesMsg);
        }
    }

    /**
     * Returns the provided Date formatted with the converter attached
     * to the component, if any, or with toString() otherwise.
     */
    private String formatDate(Date date, FacesContext context, 
        UIComponent component) {
        Converter converter = ((ValueHolder) component).getConverter();
        if (converter != null) {
            return converter.getAsString(context, component, date);
        }
        return date.toString();
    }
}
