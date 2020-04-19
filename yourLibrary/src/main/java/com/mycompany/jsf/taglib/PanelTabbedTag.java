package com.mycompany.jsf.taglib;

import javax.faces.webapp.UIComponentTag;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

/**
 * This class is a tag handler that creates and configures a
 * "javax.faces.Panel" component with a "com.mycompany.Tabbed"
 * renderer.
 *
 * @author Hans Bergsten, Gefion Software <hans@gefionsoftware.com>
 * @version 1.0
 */
public class PanelTabbedTag extends UIComponentTag {
    private String labelAreaClass;
    private String selectedLabelClass;
    private String unselectedLabelClass;

    /**
     * Sets the CSS class used for the rendered "table" element
     * holding the cells with labels.
     */
    public void setLabelAreaClass(String labelAreaClass) {
        this.labelAreaClass = labelAreaClass;
    }

    /**
     * Sets the CSS class used for the "td" element holding the
     * currently selected "label" facet.
     */
    public void setSelectedLabelClass(String selectedLabelClass) {
        this.selectedLabelClass = selectedLabelClass;
    }

    /**
     * Sets the CSS class used for the "td" element holding the
     * "label" facets other than the selected facet.
     */
    public void setUnselectedLabelClass(String unselectedLabelClass) {
        this.unselectedLabelClass = unselectedLabelClass;
    }

    /**
     * Returns "javax.facet.Panel".
     */
    public String getComponentType() {
	return "javax.faces.Panel";
    }

    /**
     * Returns "com.mycompany.Tabbed".
     */
    public String getRendererType() {
	return "com.mycompany.Tabbed";
    }

    /**
     * Configures the component based on the tag handler property
     * values.
     */
    protected void setProperties(UIComponent component) {
        super.setProperties(component);

	FacesContext context = getFacesContext();
        if (labelAreaClass != null) {
            if (isValueReference(labelAreaClass)) {
                ValueBinding vb = 
		    context.getApplication().createValueBinding(labelAreaClass);
                component.setValueBinding("labelAreaClass", vb);
            } else {
                component.getAttributes().put("labelAreaClass", 
                    labelAreaClass);
            }
        }
        if (selectedLabelClass != null) {
            if (isValueReference(selectedLabelClass)) {
                ValueBinding vb = 
		    context.getApplication().createValueBinding(selectedLabelClass);
                component.setValueBinding("selectedLabelClass", vb);
            } else {
                component.getAttributes().put("selectedLabelClass", 
                    selectedLabelClass);
            }
        }
        if (unselectedLabelClass != null) {
            if (isValueReference(unselectedLabelClass)) {
                ValueBinding vb = 
		    context.getApplication().createValueBinding(unselectedLabelClass);
                component.setValueBinding("unselectedLabelClass", vb);
            } else {
                component.getAttributes().put("unselectedLabelClass", 
                    unselectedLabelClass);
            }
        }
    }
}
