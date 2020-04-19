package com.mycompany.jsf.renderer;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.render.Renderer;

import com.mycompany.jsf.component.UITree;
import com.mycompany.jsf.model.TreeNode;

/**
 * This class is a JSF Renderer for the "com.mycompany.Tree"
 * component type. It renders a tree structure for the nodes
 * represented by the component's model using the "openNode",
 * "closedNode" and "leafNode" facets.
 *
 * @author Hans Bergsten, Gefion Software <hans@gefionsoftware.com>
 * @version 1.0
 */
public class TreeRenderer extends Renderer {

    /**
     * Returns "true".
     */
    public boolean getRendersChildren() {
	return true;
    }

    /**
     * Calls encodeNodes() to write the HTML elements for all nodes,
     * within a "span" element with an "id" attribute set to the
     * component's client ID if explicitly set.
     */
    public void encodeChildren(FacesContext context, UIComponent component)
        throws IOException {

        if (!component.isRendered()) {
            return;
        }

	if (((UITree) component).getValue() == null) {
	    return;
	}
	
        ResponseWriter out = context.getResponseWriter();
	String clientId = null;
	if (component.getId() != null && 
	    !component.getId().startsWith(UIViewRoot.UNIQUE_ID_PREFIX)) {
	    clientId = component.getClientId(context);
	}

	boolean isOuterSpanUsed = false;
	if (clientId != null) {
	    isOuterSpanUsed = true;
	    out.startElement("span", component);
	    out.writeAttribute("id", clientId, "id");
	}
	encodeNodes(context, out, (UITree) component, null, 0);
	((UITree) component).setNodeId(null);
	if (isOuterSpanUsed) {
            out.endElement("span");
	}
    }

    /**
     * Iterates over all expanded nodes in the model and processes the
     * appropriate facet for each node. Writes a "br" elements after
     * each node and embeds child nodes within a "blockquote" element.
     */
    private void encodeNodes(FacesContext context, ResponseWriter out,
	UITree tree, String parentId, int childLevel) throws IOException {

	UIComponent facet = null;
	tree.setNodeId(parentId != null ? 
            parentId + NamingContainer.SEPARATOR_CHAR + childLevel : "0");
	TreeNode node = tree.getNode();
	if (node.isLeafNode()) {
	    facet = tree.getLeafNode();
	} 
	else if (node.isExpanded()) {
	    facet = tree.getOpenNode();
	}
	else {
	    facet = tree.getClosedNode();
	}

	encodeRecursive(context, facet);
	out.startElement("br", tree);
	out.endElement("br");
	if (node.isExpanded()) {
	    out.startElement("blockquote", tree);
	    int kidId = 0;
	    String currId = tree.getNodeId();
	    Iterator i = node.getChildren().iterator();
	    while (i.hasNext()) {
		TreeNode kid = (TreeNode) i.next();
		encodeNodes(context, out, tree, currId, kidId++);
	    }
	    out.endElement("blockquote");
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
