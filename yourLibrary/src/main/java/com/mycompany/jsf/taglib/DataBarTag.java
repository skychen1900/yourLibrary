package com.mycompany.jsf.taglib;

import javax.faces.webapp.UIComponentTag;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

/**
 * This class is a tag handler that creates and configures a
 * "javax.faces.Data" component with a "com.mycompany.Bar"
 * renderer.
 *
 * @author Hans Bergsten, Gefion Software <hans@gefionsoftware.com>
 * @version 1.0
 */
public class DataBarTag extends UIComponentTag {

    private String first;
    private String rows;
    private String value;
    private String var;

    /**
     * Sets the value of the "first" property, i.e., the index for
     * the first row in the component's model to process.
     */
    public void setFirst(String first) {
        this.first = first;
    }

    /**
     * Sets the value of the "rows" property, i.e., the number of
     * rows in the component's model to process.
     */
    public void setRows(String rows) {
        this.rows = rows;
    }

    /**
     * Sets the component's model value, which must be a JSF value
     * binding expression for a "javax.faces.model.DataModel" instance
     * or a data type for which there is a wrapper model. See the
     * "javax.faces.component.UIData" class for details.
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Sets the value of the "var" property, i.e., the name of
     * the request scope variable that holds the current row
     * being processed.
     */
    public void setVar(String var) {
        this.var = var;
    }

    /**
     * Returns "javax.faces.Data".
     */
    public String getComponentType() {
	return "javax.faces.Data";
    }

    /**
     * Returns "com.mycompany.Bar".
     */
    public String getRendererType() {
	return "com.mycompany.Bar";
    }

    /**
     * Configures the component based on the tag handler property
     * values.
     */
    protected void setProperties(UIComponent component) {
        super.setProperties(component);

	FacesContext context = getFacesContext();
        if (first != null) {
            if (isValueReference(first)) {
                ValueBinding vb = 
		    context.getApplication().createValueBinding(first);
                component.setValueBinding("first", vb);
            } else {
                ((UIData) component).setFirst(Integer.parseInt(first));
            }
        }

        if (rows != null) {
            if (isValueReference(rows)) {
                ValueBinding vb = 
		    context.getApplication().createValueBinding(rows);
                component.setValueBinding("rows", vb);
            } else {
                ((UIData) component).setRows(Integer.parseInt(rows));
            }
        }

        if (value != null) {
	    ValueBinding vb = 
		context.getApplication().createValueBinding(value);
	    component.setValueBinding("value", vb);
        }
                
        if (var != null) {
	    ((UIData) component).setVar(var);
        }
    }
}
