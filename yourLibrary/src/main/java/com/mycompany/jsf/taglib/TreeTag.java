package com.mycompany.jsf.taglib;

import javax.faces.webapp.UIComponentTag;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import com.mycompany.jsf.component.UITree;

/**
 * This class is a tag handler that creates and configures a
 * "com.mycompany.Tree" component with a "com.mycompany.Tree"
 * renderer.
 *
 * @author Hans Bergsten, Gefion Software <hans@gefionsoftware.com>
 * @version 1.0
 */
public class TreeTag extends UIComponentTag {
    private String value;
    private String var;
    private String varNodeToggler;

    /**
     * Sets the component's value, which must be a JSF value binding
     * expression for a "com.mycompany.jsf.model.TreeModel" or a
     * "com.mycompany.jsf.model.TreeNode" instance.
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Sets the value of the "var" property, i.e., the name of
     * the request scope variable that holds the current node
     * being processed.
     */
    public void setVar(String var) {
        this.var = var;
    }

    /**
     * Sets the value of the "varNodeToggler" property, i.e., the name of
     * the request scope variable that holds an instance of a class
     * with a toggleExpanded() action method that toggles the value
     * of the "expanded" property for the node it's bound to.
     */
    public void setVarNodeToggler(String varNodeToggler) {
        this.varNodeToggler = varNodeToggler;
    }

    /**
     * Returns "com.mycompany.Tree".
     */
    public String getComponentType() {
	return "com.mycompany.Tree";
    }

    /**
     * Returns "com.mycompany.Tree".
     */
    public String getRendererType() {
	return "com.mycompany.Tree";
    }

    /**
     * Configures the component based on the tag handler property
     * values.
     */
    protected void setProperties(UIComponent component) {
        super.setProperties(component);

	FacesContext context = getFacesContext();
        if (value != null) {
	    ValueBinding vb = 
		context.getApplication().createValueBinding(value);
	    component.setValueBinding("value", vb);
        }
                
        if (var != null) {
	    ((UITree) component).setVar(var);
        }
                
        if (varNodeToggler != null) {
	    ((UITree) component).setVarNodeToggler(varNodeToggler);
        }
    }
}


