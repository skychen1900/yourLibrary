package com.mycompany.jsf.pl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.Stack;

import javax.faces.application.Application;
import javax.faces.application.ViewHandler;
import javax.faces.component.ActionSource;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectMany;
import javax.faces.component.UISelectOne;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.el.PropertyNotFoundException;
import javax.faces.el.ValueBinding;
import javax.faces.el.MethodBinding;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;

/**
 * This class is a JSF ViewHandler that works with views defined by
 * a combination of a view specification file and a template file.
 *
 * @author Hans Bergsten, Gefion Software <hans@gefionsoftware.com>
 * @version 1.0
 */
public class XMLViewHandler extends ClassViewHandler {

    /**
     * Creates an instance and saves a reference to the
     * previously registered ViewHandler.
     */
    public XMLViewHandler(ViewHandler origViewHandler) {
        super(origViewHandler);
    }

    /**
     * Returns the UIViewRoot for the specified view, by parsing
     * the view specification file and processing the elements in
     * the specification with an instance of the ViewSpecHandler
     * class.
     */
    protected UIViewRoot createViewRoot(FacesContext context, String viewId) {

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = null;
        try {
            saxParser = factory.newSAXParser();
        }
        catch (SAXException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        catch (ParserConfigurationException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        UIViewRoot viewRoot = new UIViewRoot();
        viewRoot.setViewId(viewId);
        ExternalContext ec = context.getExternalContext();
        InputStream viewSpecIS = null;

        DefaultHandler handler = 
            new ViewSpecHandler(context.getApplication(), viewRoot);
        try {
            viewSpecIS = context.getExternalContext().
                getResourceAsStream(viewId + ".view");
            saxParser.parse(viewSpecIS, handler);

        } catch (SAXParseException e) {
            String msg = "View spec parsing error: " + e.getMessage() +
                " at line=" + e.getLineNumber() +
                " col=" + e.getColumnNumber();
            throw new IllegalArgumentException(msg);
        } catch (Exception e) {
            String msg = "View spec parsing error: " + e.getMessage();
            throw new IllegalArgumentException(msg);
        } finally {
            try {
                if (viewSpecIS != null) {
                    viewSpecIS.close();
                }
            } catch (IOException e) {}
        }
        return viewRoot;
    }

    /**
     * Renders the view represented by the provided root component by
     * parsing the template file for the view and processing the elements in
     * the template with an instance of the TemplateHandler class.
     */
    protected void renderResponse(FacesContext context, UIComponent component)
        throws IOException {

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = null;
        try {
            saxParser = factory.newSAXParser();
        }
        catch (SAXException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        catch (ParserConfigurationException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        UIViewRoot root = (UIViewRoot) component;
        String viewId = root.getViewId();
        ExternalContext ec = context.getExternalContext();
        InputStream templIS = null;

        DefaultHandler handler = new TemplateHandler(context, root);
        try {
            templIS = context.getExternalContext().
                getResourceAsStream(viewId + ".html");
            saxParser.parse(templIS, handler);

        } catch (SAXParseException e) {
            String msg = "Template parsing error: " + e.getMessage() +
                " at line=" + e.getLineNumber() +
                " col=" + e.getColumnNumber();
            throw new IllegalArgumentException(msg);
        } catch (Exception e) {
            String msg = "Template parsing error: " + e.getMessage();
            throw new RuntimeException(msg, e);
        } finally {
            try {
                if (templIS != null) {
                    templIS.close();
                }
            } catch (IOException e) {}
        }
    }

    /**
     * This class is a SAX DefaultHandler for processing the 
     * view specification file.
     */
    private static class ViewSpecHandler extends DefaultHandler {
        private Stack stack;
        private Application application;

	/**
	 * Creates an instance and pushes the root component onto a stack.
	 */
        public ViewSpecHandler(Application application, UIComponent root) {
            this.application = application;
            stack = new Stack();
            stack.push(root);
        }

	/**
	 * If the element is a "component" element, calls createComponent()
	 * to create the corresponding component, adds it as a child of
	 * the component at the top of the stack, and pushes the new
	 * component onto the stack.
	 */
        public void startElement(String namespaceURI, String lName, 
            String qName, Attributes attrs) throws SAXException {

            if ("component".equals(qName)) {
                UIComponent component = createComponent(application, attrs);
		((UIComponent) stack.peek()).getChildren().add(component);
		stack.push(component);
            }
        }

	/**
	 * Pops the top component off the stach.
	 */
        public void endElement(String namespaceURI, String lName,
            String qName) throws SAXException {

            if ("component".equals(qName)) {
                stack.pop();
            }
        }

	/**
	 * Creates and returns a component of the type specified by
	 * the "type" attribute, configured based on all the other
	 * attributes.
	 */
        private UIComponent createComponent(Application application,
            Attributes attrs) {
            if (attrs == null || attrs.getValue("type") == null) {
                String msg = 
                    "'component' element without 'type' attribute found";
                throw new IllegalArgumentException(msg);
            }

            String type = attrs.getValue("type");
            UIComponent component = application.createComponent(type);
            if (component == null) {
                String msg = "No component class registered for 'type' " +
                    type;
                throw new IllegalArgumentException(msg);
            }

            for (int i = 0; i < attrs.getLength(); i++) {
                String name = attrs.getLocalName(i);
                if ("".equals(name)) {
                    name = attrs.getQName(i);
                }
                if ("type".equals(name)) {
                    continue;
                }
                String value = attrs.getValue(i);
                if (value.startsWith("#{")) {
		    if ("action".equals(name)) {
			MethodBinding mb = 
			    application.createMethodBinding(value, null);
			((ActionSource) component).setAction(mb);
		    }
		    else {
			ValueBinding vb = 
                            application.createValueBinding(value);
			component.setValueBinding(name, vb);
		    }
                }
                else {
                    component.getAttributes().put(name, value);
                }
            }
            return component;
        }
    }

    /**
     * This class is a SAX DefaultHandler for processing the 
     * template file.
     */
    private static class TemplateHandler extends DefaultHandler {
        private StringBuffer textBuff = null;
        private FacesContext context;
        private ResponseWriter out;
        private UIViewRoot root;
        private Stack stack;
        private Object suppressTemplate;

	/**
	 * Creates an instance and pushes the root component onto a stack.
	 */
        public TemplateHandler(FacesContext context, UIViewRoot root) {

            this.context = context;
            this.root = root;
            out = context.getResponseWriter();
            stack = new Stack();
            stack.push(root);
        }

	/**
	 * Writes buffered text, if any, and tries to locate a component
	 * with the ID defined by the "id" attribute by calling 
	 * the findComponent() method on the root component. This means
	 * that the "id" attribute value must contain all naming
	 * container parent IDs. If there's a matching component in
	 * the view, it's configured based on the template element
	 * attributes, and its encodeBegin() method is invoked, and
	 * it's pushed onto the stack. Template text suppression is
	 * enabled if the suppressTemplate() method returns "true".
	 * If no component matches the "id" attribute, the element
	 * name is pushed onto the stack and the start tag is written
	 * as-is.
	 */
        public void startElement(String namespaceURI, String lName, 
            String qName, Attributes attrs) throws SAXException {

            handleTextIfNeeded();

            String id = attrs.getValue("id");
            if (id != null && root.findComponent(id) != null) {
                UIComponent comp = findAndConfigure(id, attrs);
                stack.push(comp);
                try {
                    comp.encodeBegin(context);
                }
                catch (IOException ioe) {}
                if (suppressTemplate(comp)) {
		    suppressTemplate = comp;
		}
            }
            else {
                stack.push(qName);
                if (suppressTemplate == null) {
                    try {
                        out.startElement(qName, null);
                        for (int i = 0; i < attrs.getLength(); i++) {
                            out.writeAttribute(attrs.getQName(i), 
                                 attrs.getValue(i), null);
                        }
                    }
                    catch (IOException ioe) {}
                }
            }
        }

	/**
	 * Writes buffered text, if any. If the top object on the stack
	 * is a component, calls its encodeChildren() (if it's
	 * a type that renders its children) and its encodeEnd() methods,
	 * and disables suppressing template text if it was enabled
	 * by this component. If the top object is an element name and
	 * template text isn't suppressed, the end tag is written as-is.
	 */
        public void endElement(String namespaceURI, String lName,
            String qName) throws SAXException {

            handleTextIfNeeded();

            Object o = stack.pop();
            if (o instanceof String) {
		if (suppressTemplate == null) {
		    try {
			out.endElement(qName);
			out.writeText("\n", null);
		    }
		    catch (IOException ioe) {}
		}
            }
            else {
                UIComponent comp = (UIComponent) o;
                try {
                    if (comp.getRendersChildren()) {
                        comp.encodeChildren(context);
                    }
                    comp.encodeEnd(context);
                    out.writeText("\n", null);
                }
                catch (IOException ioe) {}
                if (suppressTemplate == comp) {
                    suppressTemplate = null;
                }
            }
        }

	/**
	 * Returns the component matching the ID, configured based on
	 * the attributes.
	 */
        private UIComponent findAndConfigure(String id, Attributes attrs) {
            UIComponent comp = root.findComponent(id);
            for (int i = 0; i < attrs.getLength(); i++) {
                // Don't overwrite "id"
                if ("id".equals(attrs.getQName(i))) {
                    continue;
                }
                comp.getAttributes().put(attrs.getQName(i), attrs.getValue(i));
            }
            return comp;
        }

         private boolean suppressTemplate(UIComponent comp) {
             return comp.getRendersChildren() || 
                 comp instanceof UISelectMany || comp instanceof UISelectOne;
        } 

        /**
         * Buffer the characters until an end or start element is encountered.
         */
        public void characters(char buf[], int offset, int len)
            throws SAXException {

            if (suppressTemplate != null) {
                return;
            }
            
            if (textBuff == null) {
                textBuff = new StringBuffer(len * 2);
            }
            textBuff.append(buf, offset, len);
        }

        /**
         * Creates a String containing the buffered characters and another
         * String representing the element structure the characters belong to
         * and lets the subclass handle it. Note that this method must be
         * be called in startElement() and endElement() before updating
         * the element list, since the buffered characters belong to the
         * previous element.
         */
        private void handleTextIfNeeded() {
            if (textBuff != null) {
                String value = textBuff.toString().trim();
                textBuff = null;
                if (value.length() == 0) {
                    return;
                }
                try {
                    out.writeText(value, null);
                }
                catch (IOException ioe) {}
            }
        }
    }
}
