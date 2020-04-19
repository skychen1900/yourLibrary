package com.mycompany.jsf.taglib;

import javax.faces.webapp.UIComponentTag;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

/**
 * This class is a tag handler that creates and configures a
 * "javax.faces.Input" component with a "com.mycompany.DatePicker"
 * renderer.
 *
 * @author Hans Bergsten, Gefion Software <hans@gefionsoftware.com>
 * @version 1.0
 */
public class InputDatePickerTag extends UIComponentTag {

    private String startYear;
    private String years;
    private String value;
    private String styleClass;

    /**
     * Sets the first year to include in the year selection list.
     * The default is two years prior to the current year.
     */
    public void setStartYear(String startYear) {
        this.startYear = startYear;
    }

    /**
     * Set the number of years to include in the year selection list.
     * The default is five years.
     */
    public void setYears(String years) {
        this.years = years;
    }

    /**
     * Sets the component's value. This must be a JSF value binding
     * expression for a "java.util.Date" instance.
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Sets the CSS class for the rendered "select" elements.
     */
    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    /**
     * Returns "javax.faces.Input".
     */
    public String getComponentType() {
	return "javax.faces.Input";
    }

    /**
     * Returns "com.mycompany.DatePicker".
     */
    public String getRendererType() {
	return "com.mycompany.DatePicker";
    }

    /**
     * Configures the component based on the tag handler property
     * values.
     */
    protected void setProperties(UIComponent component) {
        super.setProperties(component);

	FacesContext context = getFacesContext();
        if (startYear != null) {
            if (isValueReference(startYear)) {
                ValueBinding vb = 
		    context.getApplication().createValueBinding(startYear);
                component.setValueBinding("startYear", vb);
            } else {
                component.getAttributes().put("startYear", 
		    new Integer(startYear));
            }
        }

        if (years != null) {
            if (isValueReference(years)) {
                ValueBinding vb = 
		    context.getApplication().createValueBinding(years);
                component.setValueBinding("years", vb);
            } else {
                component.getAttributes().put("years", new Integer(years));
            }
        }

        if (value != null) {
            if (isValueReference(value)) {
		ValueBinding vb = 
		    context.getApplication().createValueBinding(value);
		component.setValueBinding("value", vb);
            } else {
                component.getAttributes().put("value", value);
            }
        }
                
        if (styleClass != null) {
            if (isValueReference(styleClass)) {
                ValueBinding vb = 
		    context.getApplication().createValueBinding(styleClass);
                component.setValueBinding("styleClass", vb);
            } else {
                component.getAttributes().put("styleClass", styleClass);
            }
        }
    }
}
