package com.mycompany.jsf.component;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;

import javax.faces.el.ValueBinding;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesEvent;
import javax.faces.event.FacesListener;
import javax.faces.event.PhaseId;

import com.mycompany.jsf.model.TreeNode;
import com.mycompany.jsf.model.TreeModel;

/**
 * This class is a JSF component that represents a tree control.
 * Facets named "openNode", "closedNode" and "leafNode" represent
 * the components resposible for processing nodes of a TreeModel
 * in all request processing lifecycle phases, and may be either
 * input or output components. The class implements the NamingContainer
 * interface to adjust the client IDs for the facets, so that a
 * unique client ID is used for each node even though all nodes
 * are in fact processed by one set of components.
 * <p>
 * The value of this component must be either a TreeModel or a
 * TreeNode representing the root node of a tree.
 * <p>
 * The component is rendered by the "com.mycompany.Tree" renderer
 * type by default.
 *
 * @author Hans Bergsten, Gefion Software <hans@gefionsoftware.com>
 * @version 1.0
 */
public class UITree extends UIComponentBase implements NamingContainer {


    public static final String COMPONENT_TYPE = "com.mycompany.Tree";
    public static final String COMPONENT_FAMILY = "com.mycompany.Tree";

    /**
     * The component's value, either a TreeModel or a TreeNode instance.
     */
    private Object value = null;

    /**
     * The TreeModel used by this component, either set explicitly as
     * the value of created as a wrapper around a TreeNode value.
     */
    private TreeModel model = null;

    /**
     * The current node ID.
     */
    private String nodeId;

    /**
     * This map contains SavedState instances for each node in the
     * tree, keyed by the client identifier of the component representing
     * node, which contains the nodeId value for uniqueness.
     */
    private Map saved = new HashMap();

    /**
     * The name of the request scope variable through which the data
     * object for the current node is exposed.
     */
    private String var = null;

    /**
     * The name of the request scope variable through which the node
     * toggler object is exposed.
     */
    private String varNodeToggler = null;

    /**
     * The NodeToggler instance.
     */
    private NodeToggler nodeToggler;

    /**
     * Creates an instance and sets the renderer type to
     * "com.mycompany.Tree".
     */
    public UITree() {
        super();
        setRendererType("com.mycompany.Tree");
    }

    /**
     * Returns the COMPONENT_TYPE value.
     */
    public String getFamily() {
        return (COMPONENT_FAMILY);
    }

    /**
     * Returns the name of the request scope through which the data object
     * for the current node is exposed.
     */
    public String getVar() {
        return var;
    }

    /**
     * Sets the name of the request-scope variable through which the
     * data object for the current node is exposed.
     */
    public void setVar(String var) {
        this.var = var;
    }

    /**
     * Returns the name of the request scope through which the NodeToggler
     * is exposed.
     */
    public String getVarNodeToggler() {
        return varNodeToggler;
    }

    /**
     * Sets the name of the request scope through which the NodeToggler
     * is exposed.
     */
    public void setVarNodeToggler(String varNodeToggler) {
        this.varNodeToggler = varNodeToggler;
    }

    /**
     * Returns the single instance of the NodeToggler, creating it
     * if needed.
     */
    private NodeToggler getNodeToggler() {
	if (nodeToggler == null) {
	    nodeToggler = new NodeToggler(this);
	}
	return nodeToggler;
    }

    /**
     * Returns the "openNode" facet.
     */
    public UIComponent getOpenNode() {
        return getFacet("openNode");
    }

    /**
     * Sets the "openNode" facet.
     */
    public void setOpenNode(UIComponent openNode) {
        getFacets().put("openNode", openNode);
    }

    /**
     * Returns the "closedNode" facet.
     */
    public UIComponent getClosedNode() {
        return getFacet("closedNode");
    }

    /**
     * Sets the "openNode" facet.
     */
    public void setClosedNode(UIComponent closedNode) {
        getFacets().put("closedNode", closedNode);
    }

    /**
     * Returns the "leafNode" facet.
     */
    public UIComponent getLeafNode() {
        return getFacet("leafNode");
    }

    /**
     * Sets the "leafNode" facet.
     */
    public void setLeafNode(UIComponent closedNode) {
        getFacets().put("leafNode", closedNode);
    }

    /**
     * Returns the current node from the TreeModel, or "null" if
     * no node is currently processed.
     */
    public TreeNode getNode() {
	if (getDataModel() == null) {
	    return null;
	}
        return (getDataModel().getNode());
    }

    /**
     * Returns the current node ID, or "null" if no node is currently
     * processed.
     */
    public String getNodeId() {
        return nodeId;
    }

    /**
     * Sets the node ID, saving the state of all facet components
     * for the previous node ID and restoring it for the new
     * if it was saved for the new node ID previously, and exposes the
     * node for the new node ID and the NodeToggler through their
     * request scope variables.
     */
    public void setNodeId(String nodeId) {
        // Save current state for the previous node
        saveDescendantState();

        this.nodeId = nodeId;
	TreeModel model = getDataModel();
	if (model == null) {
	    return;
	}
	model.setNodeId(nodeId);

        // Reset current state information for the new row index
        restoreDescendantState();

        // Clear or expose the current row data as a request scope attribute
	Map requestMap =
	    getFacesContext().getExternalContext().getRequestMap();
        if (var != null) {
            if (nodeId == null) {
                requestMap.remove(var);
            } else {
		requestMap.put(var, getNode());
            }
        }
        if (varNodeToggler != null) {
            if (nodeId == null) {
                requestMap.remove(varNodeToggler);
            } else {
		requestMap.put(varNodeToggler, getNodeToggler());
            }
        }
    }

    /**
     * Returns the component value, set explicitly or through a
     * ValueBinding, or null if the value isn't set.
     */
    public Object getValue() {
	if (value != null) {
	    return value;
	}
	ValueBinding vb = getValueBinding("value");
	if (vb != null) {
	    return (vb.getValue(getFacesContext()));
	} else {
	    return null;
	}
    }

    /**
     * Sets the component value, either a TreeModel or a TreeNode,
     * and resets the previously cached model, if any.
     */
    public void setValue(Object value) {
        this.model = null;
        this.value = value;
    }

    /**
     * Throws an IllegalArgumentException if the name is "var",
     * "varToggler" or "nodeId" (these properties must be set
     * to explicit values); otherwise, delegates to the superclass.
     */
    public void setValueBinding(String name, ValueBinding binding) {
        if ("value".equals(name)) {
            model = null;
        } else if ("var".equals(name) || "nodeId".equals(name) ||
		   "varNodeToggler".equals(name)) {
            throw new IllegalArgumentException();
        }
        super.setValueBinding(name, binding);
    }

    /**
     * Returns a client ID composed from the regular client ID
     * (returned by the superclass getClientId() method) and the
     * current node ID (if any) separated by a colon.
     */
    public String getClientId(FacesContext context) {
	String ownClientId = super.getClientId(context);
        if (nodeId != null) {
            return ownClientId + NamingContainer.SEPARATOR_CHAR + nodeId;
        } else {
            return ownClientId;
        }
    }

    /**
     * Wraps the event in a ChildEvent and calls the superclass method
     * add it to the queue with this component as the event source.
     */
    public void queueEvent(FacesEvent event) {
        super.queueEvent(new ChildEvent(this, event, getNodeId()));
    }

    /**
     * If the event is a ChildEvent, unwraps the real event, calls
     * setNodeId() with the node ID in the ChildEvent and delegates
     * the processing to the real event source.
     */
    public void broadcast(FacesEvent event) throws AbortProcessingException {

	if (!(event instanceof ChildEvent)) {
	    super.broadcast(event);
	    return;
	}

	// Set up the correct context and fire our wrapped event
	ChildEvent childEvent = (ChildEvent) event;
        String currNodeId = getNodeId();
	setNodeId(childEvent.getNodeId());
	FacesEvent nodeEvent = childEvent.getFacesEvent();
	nodeEvent.getComponent().broadcast(nodeEvent);
        setNodeId(currNodeId);
	return;
    }

    /**
     * Before delegating to the superclass, resets the saved
     * per-node state for facet input components unless it is 
     * needed to rerender the current page with errors, as
     * determined by the keepSaved() method.
     */
    public void encodeBegin(FacesContext context) throws IOException {
        model = null; // Re-evaluate even with server-side state saving
        if (!keepSaved(context)) {
            saved = new HashMap();
        }
        super.encodeBegin(context);
    }

    /**
     * If "rendered" is true, resets the cached model and saved
     * per-node state, calls processNodes() with a PhaseId for
     * this phase, resets the node ID, and calls the decode() method.
     */
    public void processDecodes(FacesContext context) {
        if (!isRendered()) {
            return;
        }

        model = null; // Re-evaluate even with server-side state saving
        saved = new HashMap(); // We don't need saved state here

	processNodes(context, PhaseId.APPLY_REQUEST_VALUES, null, 0);
	setNodeId(null);
	decode(context);
    }

    /**
     * If "rendered" is true, calls processNodes() with a PhaseId for
     * this phase, resets the node ID, and calls the decode() method.
     */
    public void processValidators(FacesContext context) {
        if (!isRendered()) {
            return;
        }

	processNodes(context, PhaseId.PROCESS_VALIDATIONS, null, 0);
	setNodeId(null);
    }

    /**
     * If "rendered" is true, calls processNodes() with a PhaseId for
     * this phase, resets the node ID, and calls the decode() method.
     */
    public void processUpdates(FacesContext context) {
        if (!isRendered()) {
            return;
        }

	processNodes(context, PhaseId.UPDATE_MODEL_VALUES, null, 0);
	setNodeId(null);
    }

    /**
     * Returns the component state to be saved as part of the view
     * state.
     */
    public Object saveState(FacesContext context) {
        Object values[] = new Object[4];
        values[0] = super.saveState(context);
        values[1] = value;
        values[2] = var;
        values[3] = varNodeToggler;
        return (values);
    }

    /**
     * Restores the component to the provided state, previously 
     * returned by the saveState() method.
     */
    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
        value = values[1];
        var = (String) values[2];
        varNodeToggler = (String) values[3];
    }

    /**
     * Returns the cached model, if any, or the components value,
     * as-is if it's a TreeModel or wrapped in a new TreeModel if
     * it's a TreeNode, saving a reference in the "model" variable.
     */
    private TreeModel getDataModel() {

        if (model != null) {
            return model;
        }

        Object value = getValue();
        if (value != null) {
	    if (value instanceof TreeModel) {
		model = (TreeModel) value;
	    } else if (value instanceof TreeNode) {
		model = new TreeModel((TreeNode) value);
	    }
	}
	return model;
    }

    /**
     * Recursively process all nodes at the root of the tree and
     * all nodes under an open node for the provided phase, i.e.,
     * by calling processDecodes(), processValidators() or
     * processUpdates() on the facet representing the node type.
     */
    private void processNodes(FacesContext context, PhaseId phaseId, 
			 String parentId, int childLevel) {

	// Iterate over all expanded nodes in the model and process the
	// appropriate facet for each node.
	UIComponent facet = null;
	setNodeId(parentId != null ? 
		  parentId + NamingContainer.SEPARATOR_CHAR + childLevel :
		  "0");
	TreeNode node = getNode();
	if (node.isLeafNode()) {
	    facet = getLeafNode();
	} 
	else if (node.isExpanded()) {
	    facet = getOpenNode();
	}
	else {
	    facet = getClosedNode();
	}
	if (phaseId == PhaseId.APPLY_REQUEST_VALUES) {
	    facet.processDecodes(context);
	} else if (phaseId == PhaseId.PROCESS_VALIDATIONS) {
	    facet.processValidators(context);
	} else {
	    facet.processUpdates(context);
	}
	
	if (node.isExpanded()) {
	    int kidId = 0;
	    String currId = getNodeId();
	    Iterator i = node.getChildren().iterator();
	    while (i.hasNext()) {
		TreeNode kid = (TreeNode) i.next();
		processNodes(context, phaseId, currId, kidId++);
	    }
	}
    }

    /**
     * Returns "true" if there's at least one error message queued
     * for a client ID matching one of the nodes.
     */
    private boolean keepSaved(FacesContext context) {

        Iterator clientIds = saved.keySet().iterator();
        while (clientIds.hasNext()) {
            String clientId = (String) clientIds.next();
            Iterator messages = context.getMessages(clientId);
            while (messages.hasNext()) {
                FacesMessage message = (FacesMessage) messages.next();
                if (message.getSeverity().compareTo(FacesMessage.SEVERITY_ERROR)
                    >= 0) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * Restores state information for all facets by calling the
     * restoreDescendantState(UIComponent, FacesContext) method
     * on each facet.
     */
    private void restoreDescendantState() {
        FacesContext context = getFacesContext();
        Iterator i = getFacets().values().iterator();
        while (i.hasNext()) {
            UIComponent facet = (UIComponent) i.next();
	    restoreDescendantState(facet, context);
        }
    }

    /**
     * Restore state information for the specified component and its
     * children from the previously saved state, if any.
     */
    private void restoreDescendantState(UIComponent component,
                                        FacesContext context) {

        // Reset the client identifier for this component
        String id = component.getId();
        component.setId(id); // Forces client id to be reset

        if (component instanceof EditableValueHolder) {
            EditableValueHolder input = (EditableValueHolder) component;
            String clientId = component.getClientId(context);
            SavedState state = (SavedState) saved.get(clientId);
            if (state == null) {
                state = new SavedState();
            }
            input.setValue(state.getValue());
            input.setValid(state.isValid());
            input.setSubmittedValue(state.getSubmittedValue());
            // This *must* be set after the call to setValue(), since
            // calling setValue() always resets "localValueSet" to true.
            input.setLocalValueSet(state.isLocalValueSet());
        }

        Iterator kids = component.getChildren().iterator();
        while (kids.hasNext()) {
            restoreDescendantState((UIComponent) kids.next(), context);
        }
    }

    /**
     * Saves state information for all facets by calling the
     * saveDescendantState(UIComponent, FacesContext) method
     * on each facet.
     */
    private void saveDescendantState() {
        FacesContext context = getFacesContext();
        Iterator i = getFacets().values().iterator();
        while (i.hasNext()) {
            UIComponent facet = (UIComponent) i.next();
	    saveDescendantState(facet, context);
        }
    }

    /**
     * Saves state information for the specified component, if it
     * implements the EditableValueHolder interface, and its
     * children.
     */
    private void saveDescendantState(UIComponent component,
                                     FacesContext context) {

        if (component instanceof EditableValueHolder) {
            EditableValueHolder input = (EditableValueHolder) component;
            String clientId = component.getClientId(context);
            SavedState state = (SavedState) saved.get(clientId);
            if (state == null) {
                state = new SavedState();
                saved.put(clientId, state);
            }
            state.setValue(input.getLocalValue());
            state.setValid(input.isValid());
            state.setSubmittedValue(input.getSubmittedValue());
            state.setLocalValueSet(input.isLocalValueSet());
        }

        Iterator kids = component.getChildren().iterator();
        while (kids.hasNext()) {
            saveDescendantState((UIComponent) kids.next(), context);
        }
    }

    /**
     * Private class to represent saved state information.
     */
    private static class SavedState implements Serializable {

	private Object submittedValue;
	private boolean valid = true;
	private Object value;
	private boolean localValueSet;

	Object getSubmittedValue() {
	    return submittedValue;
	}

	void setSubmittedValue(Object submittedValue) {
	    this.submittedValue = submittedValue;
	}

	boolean isValid() {
	    return valid;
	}

	void setValid(boolean valid) {
	    this.valid = valid;
	}

	Object getValue() {
	    return value;
	}

	public void setValue(Object value) {
	    this.value = value;
	}

	boolean isLocalValueSet() {
	    return localValueSet;
	}

	public void setLocalValueSet(boolean localValueSet) {
	    this.localValueSet = localValueSet;
	}
    }

    /**
     * Private class to wrap an event with a node ID.
     */
    private static class ChildEvent extends FacesEvent {
	private FacesEvent event;
	private String nodeId;

	public ChildEvent(UIComponent component, FacesEvent event, 
	    String nodeId) {
	    super(component);
	    this.event = event;
	    this.nodeId = nodeId;
	}

	public FacesEvent getFacesEvent() {
	    return event;
	}

	public String getNodeId() {
	    return nodeId;
	}

	public PhaseId getPhaseId() {
	    return event.getPhaseId();
	}

	public void setPhaseId(PhaseId phaseId) {
	    event.setPhaseId(phaseId);
	}

	public boolean isAppropriateListener(FacesListener listener) {
	    return false;
	}

	public void processListener(FacesListener listener) {
	    throw new IllegalStateException();
	}
    }

    /**
     * A class with an action method that toggles the "expanded"
     * property value of the current node. It's intended to be
     * bound to command components used as the "closedNode" and
     * "openNode" facets.
     */
    public static class NodeToggler {
        private UITree tree;

        public NodeToggler(UITree tree) {
            this.tree = tree;
	}

	public String toggleExpanded() {
	    TreeNode node = tree.getDataModel().getNode();
	    node.setExpanded(!node.isExpanded());
	    return "toggledExpanded";
	}
    }
}
