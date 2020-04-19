package com.mycompany.jsf.event;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import com.mycompany.jsf.model.TreeNode;

/**
 * This class is a JSF PhaseListener that captures view state before
 * and after each request processing lifecycle phase.
 * <p>
 * It saves the state as a tree composed of TreeNode instances in
 * a session scope Map named "com.mycompany.debug", with an entry
 * per view keyed by the view ID.
 *
 * @author Hans Bergsten, Gefion Software <hans@gefionsoftware.com>
 * @version 1.0
 */
public class CaptureStatePhaseListener implements PhaseListener {

	private Map<Class, PropertyDescriptor[]> pdCache = Collections.synchronizedMap(new HashMap());

	/**
	 * Returns PhaseId.ANY_PHASE to announce that this listener
	 * must be invoked in all phases.
	 */
	@Override
	public PhaseId getPhaseId() {
		return PhaseId.ANY_PHASE;
	}

	/**
	 * Saves the view state before the regular processing of the view
	 * in the current phase by calling capturePhaseData(), unless
	 * the current phase is Restore View (there's no view available
	 * in this case).
	 */
	@Override
	public void beforePhase(PhaseEvent event) {
		String phaseName = event.getPhaseId().toString();
		if (event.getPhaseId() != PhaseId.RESTORE_VIEW) {
			capturePhaseData("Before " + phaseName, event.getFacesContext());
		}
	}

	/**
	 * Saves the view state after the regular processing of the view
	 * in the current phase by calling capturePhaseData(). If the
	 * current phase is Render Response, also saves general request
	 * data by calling captureRequestData().
	 */
	@Override
	public void afterPhase(PhaseEvent event) {
		String phaseName = event.getPhaseId().toString();
		capturePhaseData("After " + phaseName, event.getFacesContext());

		if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			captureRequestData(event.getFacesContext());
		}
	}

	/**
	 * Returns the TreeNode for the tree root, from the Map saved
	 * as a session scope variable named "com.mycompany.debug" or
	 * a new instance if it's not found or the found instance is
	 * for a previous request.
	 */
	private TreeNode getRoot(FacesContext context) {
		Map<String, Object> sessionMap = context.getExternalContext().getSessionMap();
		Map<String, Object> debugMap = (Map<String, Object>) sessionMap.get("com.mycompany.debug");
		if (debugMap == null) {
			debugMap = new HashMap<String, Object>();
			sessionMap.put("com.mycompany.debug", debugMap);
		}

		String viewId = getViewId(context);
		TreeNode root = (TreeNode) debugMap.get(viewId);
		if (root == null || !context.equals(root.getValue())) {
			// First request or old data from previous request
			root = new TreeNode();
			root.setName("root");
			root.setValue(context);
			root.setExpanded(true);
			debugMap.put(viewId, root);
		}

		return root;
	}

	/**
	 * Returns the view ID for the view held by the provided
	 * FacesContext, adjusted if needed.  For the first phases
	 * on a new view, the viewId may have the wrong extension. Just
	 * replace it with ".jsp" (should really look at mapping).
	 */
	private String getViewId(FacesContext context) {
		String viewId = context.getViewRoot().getViewId();
		int extPos = viewId.lastIndexOf('.');
		if (extPos > 0) {
			viewId = viewId.substring(0, extPos) + ".jsp";
		}
		return viewId;
	}

	/**
	 * Creates nodes for the request data. Nodes for request headers,
	 * request parameters, and request locales are created and added
	 * as a branch under the root of the tree.
	 */
	private void captureRequestData(FacesContext context) {
		TreeNode root = getRoot(context);

		TreeNode requestNode = new TreeNode();
		requestNode.setName("ExternalContext");
		root.addChild(requestNode);

		TreeNode headersNode = new TreeNode();
		headersNode.setName("requestHeaderMap");
		Map<String, String> headersMap = context.getExternalContext().getRequestHeaderMap();
		addLeafNodes(headersNode, headersMap);
		requestNode.addChild(headersNode);

		TreeNode paramsNode = new TreeNode();
		paramsNode.setName("requestParameterValuesMap");
		Map<String, String[]> paramsMap = context.getExternalContext().getRequestParameterValuesMap();
		addLeafNodes(paramsNode, paramsMap);
		requestNode.addChild(paramsNode);

		TreeNode localesNode = new TreeNode();
		localesNode.setLeafNode(true);
		localesNode.setName("requestLocales");
		Iterator<Locale> locales = context.getExternalContext().getRequestLocales();
		localesNode.setValue(format(locales));
		requestNode.addChild(localesNode);
	}

	/**
	 * Creates nodes for the view. Nodes for each component in the
	 * view's component tree and all scoped variables are created and
	 * added as a branch under the root of the tree.
	 */
	private void capturePhaseData(String phaseName, FacesContext context) {
		TreeNode root = getRoot(context);

		TreeNode phaseNode = new TreeNode();
		phaseNode.setName(phaseName);
		root.addChild(phaseNode);

		TreeNode compNode = new TreeNode();
		UIComponent viewRoot = context.getViewRoot();
		compNode.setName("viewRoot [" + viewRoot.getClass().getName() + "]");
		addComponentNodes(context, compNode, viewRoot);
		phaseNode.addChild(compNode);

		TreeNode varNode = new TreeNode();
		varNode.setName("Scoped Variables");
		phaseNode.addChild(varNode);

		TreeNode appNode = new TreeNode();
		appNode.setName("applicationMap");
		Map<String, Object> appMap = context.getExternalContext().getApplicationMap();
		addLeafNodes(appNode, appMap);
		varNode.addChild(appNode);

		TreeNode sessionNode = new TreeNode();
		sessionNode.setName("sessionMap");
		Map<String, Object> sessionMap = context.getExternalContext().getSessionMap();
		addLeafNodes(sessionNode, sessionMap);
		varNode.addChild(sessionNode);

		TreeNode requestNode = new TreeNode();
		requestNode.setName("requestMap");
		Map<String, Object> requestMap = context.getExternalContext().getRequestMap();
		addLeafNodes(requestNode, requestMap);
		varNode.addChild(requestNode);
	}

	/**
	 * Creates nodes for properties and attributes of the provided
	 * component and adds them as branches under the provided node,
	 * and then recursively calls itself for each child and facet
	 * of the provided component.
	 */
	private void addComponentNodes(FacesContext context, TreeNode parent,
			UIComponent comp) {

		TreeNode propsNode = new TreeNode();
		propsNode.setName("Properties");

		PropertyDescriptor[] pds = pdCache.get(comp.getClass());
		if (pds == null) {
			try {
				BeanInfo bi = Introspector.getBeanInfo(comp.getClass(), Object.class);
				pds = bi.getPropertyDescriptors();
				pdCache.put(comp.getClass(), pds);
			} catch (Exception e) {
			}
			;
		}
		if (pds != null) {
			for (int i = 0; pds != null && i < pds.length; i++) {
				String name = pds[i].getName();
				if ("attributes".equals(name) ||
						"children".equals(name) ||
						"facets".equals(name) ||
						"facetsAndChildren".equals(name) ||
						"parent".equals(name)) {
					continue;
				}
				TreeNode propNode = new TreeNode();
				propNode.setLeafNode(true);
				propNode.setName(name);
				Object value = null;
				Method m = pds[i].getReadMethod();
				if (m == null) {
					value = "--- No read method ---";
				} else {
					try {
						value = m.invoke(comp, null);
					} catch (Exception e) {
					}
				}
				propNode.setValue(toString(value));
				propsNode.addChild(propNode);
			}
			parent.addChild(propsNode);
		}

		TreeNode attrsNode = new TreeNode();
		attrsNode.setName("Attributes");
		Iterator attrs = comp.getAttributes().entrySet().iterator();
		while (attrs.hasNext()) {
			Map.Entry me = (Map.Entry) attrs.next();
			TreeNode attrNode = new TreeNode();
			attrNode.setLeafNode(true);
			attrNode.setName(me.getKey().toString());
			attrNode.setValue(toString(me.getValue()));
			attrsNode.addChild(attrNode);
		}
		parent.addChild(attrsNode);

		if (comp.getChildCount() > 0) {
			TreeNode kidsNode = new TreeNode();
			kidsNode.setName("Children");
			Iterator kids = comp.getChildren().iterator();
			while (kids.hasNext()) {
				UIComponent child = (UIComponent) kids.next();
				TreeNode childNode = new TreeNode();
				childNode.setName(child.getClientId(context) +
						" [" + child.getClass().getName() + "]");
				kidsNode.addChild(childNode);
				addComponentNodes(context, childNode, child);
			}
			parent.addChild(kidsNode);
		}

		Map facetsMap = comp.getFacets();
		if (facetsMap.size() > 0) {
			TreeNode facetsNode = new TreeNode();
			facetsNode.setName("Facets");
			Iterator facets = facetsMap.entrySet().iterator();
			while (facets.hasNext()) {
				Map.Entry me = (Map.Entry) facets.next();
				UIComponent child = (UIComponent) me.getValue();
				TreeNode childNode = new TreeNode();
				childNode.setName((String) me.getKey());
				facetsNode.addChild(childNode);
				addComponentNodes(context, childNode, child);
			}
			parent.addChild(facetsNode);
		}
	}

	/**
	 * Add a TreeNode instance for each entry in the Map, with the
	 * entry key (converted to a String) as the node name, the entry value
	 * (converted to a String) as the node value, and with the "leafNode"
	 * property set to "true".
	 */
	private void addLeafNodes(TreeNode parent, Map map) {
		Iterator i = map.entrySet().iterator();
		while (i.hasNext()) {
			Map.Entry me = (Map.Entry) i.next();
			TreeNode leaf = new TreeNode();
			leaf.setLeafNode(true);
			leaf.setName(me.getKey().toString());
			leaf.setValue(toString(me.getValue()));
			parent.addChild(leaf);
		}
	}

	/**
	 * Returns the value as a String in an appropriate format
	 * depending on the data type. A null value is returned as
	 * "null", an Object or primitive type array or a Collection
	 * is returned as a comma-separated list of values, a Map
	 * is returned as a comma-separated list of "key=value" entries.
	 */
	private String toString(Object value) {
		if (value == null) {
			return "null";
		}

		String string = null;

		// Use element values for common mutable types
		if (value.getClass().isArray()) {
			if (value.getClass().getComponentType().isPrimitive()) {
				if (value.getClass().getComponentType() == Boolean.TYPE) {
					Object[] arr = toObjects((boolean[]) value);
					string = format(Arrays.asList(arr).iterator());
				}
				if (value.getClass().getComponentType() == Byte.TYPE) {
					Object[] arr = toObjects((byte[]) value);
					string = format(Arrays.asList(arr).iterator());
				}
				if (value.getClass().getComponentType() == Character.TYPE) {
					Object[] arr = toObjects((char[]) value);
					string = format(Arrays.asList(arr).iterator());
				}
				if (value.getClass().getComponentType() == Double.TYPE) {
					Object[] arr = toObjects((double[]) value);
					string = format(Arrays.asList(arr).iterator());
				}
				if (value.getClass().getComponentType() == Float.TYPE) {
					Object[] arr = toObjects((float[]) value);
					string = format(Arrays.asList(arr).iterator());
				}
				if (value.getClass().getComponentType() == Integer.TYPE) {
					Object[] arr = toObjects((int[]) value);
					string = format(Arrays.asList(arr).iterator());
				}
				if (value.getClass().getComponentType() == Long.TYPE) {
					Object[] arr = toObjects((long[]) value);
					string = format(Arrays.asList(arr).iterator());
				}
				if (value.getClass().getComponentType() == Short.TYPE) {
					Object[] arr = toObjects((short[]) value);
					string = format(Arrays.asList(arr).iterator());
				}
			} else {
				string = format(Arrays.asList((Object[]) value).iterator());
			}
		} else if (value instanceof Collection) {
			string = format(((Collection) value).iterator());
		} else if (value instanceof Map) {
			string = format((Map) value);
		} else {
			string = value.toString();
		}
		return string;
	}

	/**
	 * Returns an Object array with the values of the array.
	 */
	private Object[] toObjects(boolean[] arr) {
		Object[] objects = new Object[Array.getLength(arr)];
		for (int i = 0; i < arr.length; i++) {
			objects[i] = Array.get(arr, i);
		}
		return objects;
	}

	/**
	 * Returns an Object array with the values of the array.
	 */
	private Object[] toObjects(byte[] arr) {
		Object[] objects = new Object[Array.getLength(arr)];
		for (int i = 0; i < arr.length; i++) {
			objects[i] = Array.get(arr, i);
		}
		return objects;
	}

	/**
	 * Returns an Object array with the values of the array.
	 */
	private Object[] toObjects(char[] arr) {
		Object[] objects = new Object[Array.getLength(arr)];
		for (int i = 0; i < arr.length; i++) {
			objects[i] = Array.get(arr, i);
		}
		return objects;
	}

	/**
	 * Returns an Object array with the values of the array.
	 */
	private Object[] toObjects(double[] arr) {
		Object[] objects = new Object[Array.getLength(arr)];
		for (int i = 0; i < arr.length; i++) {
			objects[i] = Array.get(arr, i);
		}
		return objects;
	}

	/**
	 * Returns an Object array with the values of the array.
	 */
	private Object[] toObjects(float[] arr) {
		Object[] objects = new Object[Array.getLength(arr)];
		for (int i = 0; i < arr.length; i++) {
			objects[i] = Array.get(arr, i);
		}
		return objects;
	}

	/**
	 * Returns an Object array with the values of the array.
	 */
	private Object[] toObjects(int[] arr) {
		Object[] objects = new Object[Array.getLength(arr)];
		for (int i = 0; i < arr.length; i++) {
			objects[i] = Array.get(arr, i);
		}
		return objects;
	}

	/**
	 * Returns an Object array with the values of the array.
	 */
	private Object[] toObjects(short[] arr) {
		Object[] objects = new Object[Array.getLength(arr)];
		for (int i = 0; i < arr.length; i++) {
			objects[i] = Array.get(arr, i);
		}
		return objects;
	}

	/**
	 * Returns an Object array with the values of the array.
	 */
	private Object[] toObjects(long[] arr) {
		Object[] objects = new Object[Array.getLength(arr)];
		for (int i = 0; i < arr.length; i++) {
			objects[i] = Array.get(arr, i);
		}
		return objects;
	}

	/**
	 * Returns a String with comma-separated list of the values
	 * represented by the Iterator.
	 */
	private String format(Iterator i) {
		StringBuffer sb = new StringBuffer();
		boolean first = true;
		while (i.hasNext()) {
			Object o = i.next();
			if (!first) {
				sb.append(", ");
			}
			first = false;
			sb.append(o);
		}
		return sb.toString();
	}

	/**
	 * Returns a String with comma-separated list of the Map entries,
	 * with each entry as "key = value".
	 */
	private String format(Map map) {
		Iterator i = map.entrySet().iterator();
		StringBuffer sb = new StringBuffer();
		boolean first = true;
		while (i.hasNext()) {
			Map.Entry me = (Map.Entry) i.next();
			if (!first) {
				sb.append(", ");
			}
			first = false;
			sb.append(me.getKey()).append("=").append(me.getValue());
		}
		return sb.toString();
	}
}
