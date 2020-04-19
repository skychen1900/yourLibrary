package com.mycompany.jsf.renderer;

import com.mycompany.jsf.component.UITabLabel;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.render.Renderer;

/**
 * This class is a JSF Renderer for the "javax.faces.Panel"
 * component type. It renders a table with one row of cells
 * made up from the "label" facets on its children, assumed
 * to be "com.mycompany.TabLabel" components.
 *
 * @author Hans Bergsten, Gefion Software <hans@gefionsoftware.com>
 * @version 1.0
 */
public class TabbedRenderer extends Renderer {

    /**
     * Returns "true".
     */
    public boolean getRendersChildren() {
	return true;
    }

    /**
     * Loops over all children and calls processDecodes() on the
     * child's "label" facet for all children with the "rendered"
     * property set to "false" (the one with "rendered" set to
     * "true" is decoded by the default processing).
     */
    public void decode(FacesContext context, UIComponent component) {
	// "rendered" false are not decoded by default, but the label
	// facets must be
	Iterator i = component.getChildren().iterator();
	while (i.hasNext()) {
	    UIComponent child = (UIComponent) i.next();
	    if (!child.isRendered()) {
		UITabLabel tabLabel = (UITabLabel) child.getFacet("label");
		if (tabLabel != null) {
		    tabLabel.processDecodes(context);
		}
	    }
	}
    }

    /**
     * Ensures that only one child has "rendered" set to "true",
     * renders an HTML table with one row and one cell per child
     * "label" facet, with a "class" attribute for the "table"
     * element set to the "labelAreaClass" component attribute
     * value, and one of "selectedLabelClass" or "unselectedLabelClass"
     * as the "class" attribute value for the "td" elements.
     */
    public void encodeBegin(FacesContext context, UIComponent component)
        throws IOException {

        if (!component.isRendered()) {
            return;
        }

	/*
	 * Figure out which tab is selected (the first one with
	 * "rendered" set to "true"), and ensure all others are
	 * unselected ("rendered" set to "false").
	 */
	int selected = 0;
	List children = component.getChildren();
	boolean pickedSelected = false;
	for (int i = 0; i < children.size(); i++) {
	    UIComponent child = (UIComponent) children.get(i);
	    if (child.isRendered() && !pickedSelected) {
		selected = i;
		pickedSelected = true;
	    }
	    else {
		child.setRendered(false);
	    }
	}

	// Render the tab labels as an HTML table with the appropriate
	// CSS class
        String labelAreaClass =
	    (String) component.getAttributes().get("labelAreaClass");
        String selectedLabelClass =
	    (String) component.getAttributes().get("selectedLabelClass");
        String unselectedLabelClass =
	    (String) component.getAttributes().get("unselectedLabelClass");

        ResponseWriter out = context.getResponseWriter();
	out.startElement("table", component);
	if (component.getId() != null && 
	    !component.getId().startsWith(UIViewRoot.UNIQUE_ID_PREFIX)) {
		out.writeAttribute("id", component.getClientId(context),
		    "id");
	}
	if (labelAreaClass != null) {
	    out.writeAttribute("class", labelAreaClass, "labelAreaClass");
	}

	out.startElement("tr", component);	
	for (int i = 0; i < children.size(); i++) {
	    UIComponent child = (UIComponent) children.get(i);
	    UITabLabel tabLabel = (UITabLabel) child.getFacet("label");
	    if (tabLabel != null) {
		String styleClass = i == selected ? 
		    selectedLabelClass : unselectedLabelClass;
		out.startElement("td", component);
		if (styleClass != null) {
		    out.writeAttribute("class", styleClass, 
			 i == selected ? 
		             "selectedLabelClass" : "unselectedLabelClass");
		}
		encodeRecursive(context, tabLabel);
		out.endElement("td");
	    }
	}
	out.endElement("tr");
	out.endElement("table");
    }

    /**
     * Calls the appropriate encode methods for the child with
     * the "rendered" property set to "true".
     */
    public void encodeChildren(FacesContext context, UIComponent component)
        throws IOException {

	Iterator i = component.getChildren().iterator();
	while (i.hasNext()) {
	    UIComponent child = (UIComponent) i.next();
	    if (child.isRendered()) {
		child.encodeBegin(context);
		if (child.getRendersChildren()) {
		    child.encodeChildren(context);
		}
		child.encodeEnd(context);
	    }
	}
    }

    /**
     * Calls the appropriate encoding methods on the component and
     * calls itself recursively for all component children.
     */
    private void encodeRecursive(FacesContext context, UIComponent component)
        throws IOException {

        if (!component.isRendered()) {
            return;
        }

        component.encodeBegin(context);
        if (component.getRendersChildren()) {
            component.encodeChildren(context);
        } else {
            Iterator i = component.getChildren().iterator();
            while (i.hasNext()) {
                UIComponent child = (UIComponent) i.next();
                encodeRecursive(context, child);
            }
        }
        component.encodeEnd(context);
    }
}
