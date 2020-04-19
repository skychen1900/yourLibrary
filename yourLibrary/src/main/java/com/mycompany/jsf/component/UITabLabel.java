package com.mycompany.jsf.component;

import java.util.Iterator;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.el.MethodBinding;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.faces.event.FacesEvent;

/**
 * This class is a JSF component that represents a clickable
 * tab control label. It must be used as a facet named "label"
 * of a component representing a tab within a UIPanel with a
 * "com.mycompany.jsf.TabbedRenderer".
 *
 * The component is rendered by the "javax.faces.Link" renderer
 * type by default, using its child components as the link element
 * body. It handles the ActionEvent it fires itself by setting
 * the "rendered" property for its parent "true" and to "false"
 * for the parents siblings.
 *
 * @author Hans Bergsten, Gefion Software <hans@gefionsoftware.com>
 * @version 1.0
 */
public class UITabLabel extends UICommand {

    public static final String COMPONENT_TYPE = "com.mycompany.TabLabel";
    public static final String COMPONENT_FAMILY = "javax.faces.Command";

    /**
     * Creates an instance and sets the renderer type to
     * "javax.faces.Link".
     */
    public UITabLabel() {
        super();
        setRendererType("javax.faces.Link");
    }

    /**
     * Returns the COMPONENT_TYPE value.
     */
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    /**
     * Disables all standard UICommand event handling and calls
     * processAction() to process the event if it's an ActionEvent.
     */
    public void broadcast(FacesEvent event) {
        if (event instanceof ActionEvent) {
            processAction((ActionEvent) event);
        }
    }

    /**
     * Returns "null", because this component can't be configured
     * with an action method.
     */
    public MethodBinding getAction() {
        return null;
    }

    /**
     * Throws "UnsupportedOperationException" to disable the use
     * of an action method.
     */
    public void setAction(MethodBinding action) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns "null", because this component can't be configured
     * with an action listener method.
     */
    public MethodBinding getActionListener() {
        return null;
    }

    /**
     * Throws "UnsupportedOperationException" to disable the use
     * of an action listener method.
     */
    public void setActionListener(MethodBinding actionListener) {
        throw new UnsupportedOperationException();
    }

    /**
     * Throws "UnsupportedOperationException" to disable the use
     * of ActionListener instances.
     */
    public void addActionListener(ActionListener listener) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns an empty array, because this component can't be
     * configured with ActionListener instances.
     */
    public ActionListener[] getActionListeners() {
        return new ActionListener[0];
    }

    /**
     * Throws "UnsupportedOperationException" to disable the use
     * of ActionListener instances.
     */
    public void removeActionListener(ActionListener listener) {
        throw new UnsupportedOperationException();
    }

    /**
     * Locates the component's grandparent (holding all components
     * acting as tabs), gets all its children (the tab components),
     * and sets the "rendered" property to "true" for this component's
     * parenent and to "false" for all others.
     */
    private void processAction(ActionEvent event) {
        UIComponent parent = getParent();
        UIComponent panelTabbed = parent.getParent();
        Iterator i = panelTabbed.getChildren().iterator();
        while (i.hasNext()) {
            UIComponent tab = (UIComponent) i.next();
            if (tab.equals(parent)) {
                tab.setRendered(true);
            }
            else {
                tab.setRendered(false);
            }
        }
    }
}
