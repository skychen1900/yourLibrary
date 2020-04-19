package com.mycompany.jsf.pl;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

/**
 * This interface must be implemented by classes representing a
 * view for the ClassViewHandler.
 *
 * @author Hans Bergsten, Gefion Software <hans@gefionsoftware.com>
 * @version 1.0
 */
public interface View {
    public UIViewRoot createView(FacesContext context);
}
