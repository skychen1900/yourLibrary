package com.mycompany.jsf.renderer;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.UIViewRoot;
import javax.faces.render.Renderer;

/**
 * This class is a JSF Renderer for the "javax.faces.Data"
 * component type.
 *
 * @author Hans Bergsten, Gefion Software <hans@gefionsoftware.com>
 * @version 1.0
 */
public class BarRenderer extends Renderer {

    /**
     * Returns "true".
     */
    public boolean getRendersChildren() {
	return true;
    }

    /**
     * Renders the component by letting its UIColumn children process
     * one row at a time, starting with the row specified by the "first"
     * attribute for the number of rows specified by the "rows" attribute,
     * or until the lasy row is reached. If an explicit ID is set for
     * the component, the component's client ID value is used as the
     * "id" attribute value on a "span" element that wraps all output
     * produced by the children.
     */
    public void encodeChildren(FacesContext context, UIComponent component)
        throws IOException {

        if (!component.isRendered()) {
            return;
        }

	String clientId = null;
	if (component.getId() != null && 
	    !component.getId().startsWith(UIViewRoot.UNIQUE_ID_PREFIX)) {
	    clientId = component.getClientId(context);
	}

        ResponseWriter out = context.getResponseWriter();
	if (clientId != null) {
	    out.startElement("span", component);
	    out.writeAttribute("id", clientId, "id");
	}

        UIData data = (UIData) component;

        int first = data.getFirst();
        int rows = data.getRows();
        for (int i = first, n = 0; rows == 0 || n < rows; i++, n++) {
            data.setRowIndex(i);
            if (!data.isRowAvailable()) {
                break;
            }

	    Iterator j = data.getChildren().iterator();
	    while (j.hasNext()) {
		UIComponent column = (UIComponent) j.next();
		if (!(column instanceof UIColumn)) {
		    continue;
		}
		encodeRecursive(context, column);
	    }
	}

	if (clientId != null) {
            out.endElement("span");
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
