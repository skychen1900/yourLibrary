package com.mycompany.jsf.taglib;

import javax.faces.webapp.UIComponentTag;

/**
 * This class is a tag handler that creates and configures a
 * "com.mycompany.TabLabel" component with a "javax.faces.Link"
 * renderer.
 *
 * @author Hans Bergsten, Gefion Software <hans@gefionsoftware.com>
 * @version 1.0
 */
public class TabLabelTag extends UIComponentTag {

    /**
     * Returns "com.mycompany.TabLabel".
     */
    public String getComponentType() {
	return "com.mycompany.TabLabel";
    }

    /**
     * Returns "javax.faces.Link".
     */
    public String getRendererType() {
	return "javax.faces.Link";
    }
}
