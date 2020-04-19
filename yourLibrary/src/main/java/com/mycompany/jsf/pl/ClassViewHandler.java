package com.mycompany.jsf.pl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.faces.FactoryFinder;
import javax.faces.application.StateManager;
import javax.faces.application.StateManager.SerializedView;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.mycompany.newsservice.views.SubscribeView;

/**
 * This class is a JSF ViewHandler that works with views created
 * by classes implementing the View interface.
 *
 * @author Hans Bergsten, Gefion Software <hans@gefionsoftware.com>
 * @version 1.0
 */
public class ClassViewHandler extends ViewHandler {
    private static final String STATE_VAR = "com.mycompany.viewState";
    protected ViewHandler origViewHandler;
    private Map views = new HashMap();

    /**
     * Creates an instance and saves a reference to the
     * previously registered ViewHandler.
     */
    public ClassViewHandler(ViewHandler origViewHandler) {
        this.origViewHandler = origViewHandler;
    }

    /**
     * Delegates the call to the previously registered ViewHandler.
     */
    public Locale calculateLocale(FacesContext context) {
        return origViewHandler.calculateLocale(context);
    }

    /**
     * Delegates the call to the previously registered ViewHandler.
     */
    public String calculateRenderKitId(FacesContext context) {
        return origViewHandler.calculateRenderKitId(context);
    }

    /**
     * Delegates the call to the previously registered ViewHandler.
     */
    public String getActionURL(FacesContext context, String viewId) {
        return origViewHandler.getActionURL(context, viewId);
    }

    /**
     * Delegates the call to the previously registered ViewHandler.
     */
    public String getResourceURL(FacesContext context, String path) {
        return origViewHandler.getResourceURL(context, path);
    }

    /**
     * Returns the UIViewRoot for the specified view ID, created
     * by the createViewRoot() method, with the "locale" and 
     * "renderKitId" properties initialized.
     */
    public UIViewRoot createView(FacesContext context, String viewId) {
        String realViewId = viewId;
        if (viewId.indexOf(".") != -1) {
            realViewId = viewId.substring(0, viewId.indexOf("."));
        }

        UIViewRoot viewRoot = createViewRoot(context, realViewId);
        if (viewRoot != null) {
            if (context.getViewRoot() != null) {
                UIViewRoot oldRoot = context.getViewRoot();
                viewRoot.setLocale(oldRoot.getLocale());
                viewRoot.setRenderKitId(oldRoot.getRenderKitId());
            }
            else {
		ViewHandler activeVH = 
		    context.getApplication().getViewHandler();
                viewRoot.setLocale(activeVH.calculateLocale(context));
                viewRoot.setRenderKitId(activeVH.calculateRenderKitId(context));
            }
        }
        return viewRoot;
    }

    /**
     * Gets the view state to save, if any, and puts a reference to
     * the state in a request scope variable where it can be picked
     * up by the writeState() method, and renders the view represented
     * by the provided UIViewRoot instance by calling renderResponse().
     */
    public void renderView(FacesContext context, UIViewRoot viewToRender) 
        throws IOException {

        setupResponseWriter(context);

        StateManager sm = context.getApplication().getStateManager();
        SerializedView state = sm.saveSerializedView(context);
	context.getExternalContext().getRequestMap().put(STATE_VAR, state);

        context.getResponseWriter().startDocument();
        renderResponse(context, viewToRender);
        context.getResponseWriter().endDocument();
    }

    /**
     * Returns the UIViewRoot for the restored view identified by
     * the provided view ID, or null if no state is available.
     */
    public UIViewRoot restoreView(FacesContext context, String viewId) {
        String realViewId = viewId;
        if (viewId.indexOf(".") != -1) {
            realViewId = viewId.substring(0, viewId.indexOf("."));
        }

        String renderKitId = 
	    context.getApplication().getViewHandler().
	    calculateRenderKitId(context);

        StateManager sm = context.getApplication().getStateManager();
        return sm.restoreView(context, realViewId, renderKitId);
    }

    /**
     * Writes the state captured and saved by the renderView() method
     * to the response with the help of the current StateManager.
     */
    public void writeState(FacesContext context) throws IOException {
	SerializedView state = (SerializedView)
	    context.getExternalContext().getRequestMap().get(STATE_VAR);
	if (state != null) {
	    StateManager sm = context.getApplication().getStateManager();
	    sm.writeState(context, state);
	}
    }

    /**
     * Returns the UIViewRoot for the view created by the View instance
     * matching the view ID.
     */
    protected UIViewRoot createViewRoot(FacesContext context, String viewId) {
        UIViewRoot viewRoot = null;
        View view = (View) views.get(viewId);
        if (view == null) {
            if ("/subscribe".equals(viewId)) {
                view = new SubscribeView();
                views.put(viewId, view);
            }
        }
        if (view != null) {
            viewRoot = view.createView(context);
            viewRoot.setViewId(viewId);
        }
        return viewRoot;
    }

    /**
     * Recursively renders the provided component and all its children
     * by calling encodeBegin(), encodeChildren() (if the component
     * renders its children) and encodeEnd().
     */
    protected void renderResponse(FacesContext context, UIComponent component)
        throws IOException {

        component.encodeBegin(context);
        if (component.getRendersChildren()) {
            component.encodeChildren(context);
        }
        else {
            Iterator i = component.getChildren().iterator();
            while (i.hasNext()) {
                renderResponse(context, (UIComponent) i.next());
            }
        }
        component.encodeEnd(context);
    }

    /**
     * Asks the current RenderKit to create a ResponseWriter around
     * the OutputStream for the response body and sets the
     * Content-Type response header to the MIME type selected by
     * the ResponseWriter from the alternatives listed in the
     * Accept request header.
     */
    private void setupResponseWriter(FacesContext context) 
        throws IOException {

        ServletResponse response = (ServletResponse)
            context.getExternalContext().getResponse();
        OutputStream os = response.getOutputStream();
	Map headers = context.getExternalContext().getRequestHeaderMap();
	String acceptHeader = (String) headers.get("Accept");

	// Work-around for JSF 1.0 RI bug: failing to accept "*/*" and
	// and "text/*" as valid replacements for "text/html"
	if (acceptHeader != null && 
	    (acceptHeader.indexOf("*/*") != -1 ||
	     acceptHeader.indexOf("text/*") != -1)) {
	    acceptHeader += ",text/html";
	}

        RenderKitFactory renderFactory = (RenderKitFactory)
            FactoryFinder.getFactory(FactoryFinder.RENDER_KIT_FACTORY);
        RenderKit renderKit = 
            renderFactory.getRenderKit(context,
                 context.getViewRoot().getRenderKitId());
        ResponseWriter writer = 
            renderKit.createResponseWriter(new OutputStreamWriter(os),
                acceptHeader, response.getCharacterEncoding());
        context.setResponseWriter(writer);
        response.setContentType(writer.getContentType());
    }
}
