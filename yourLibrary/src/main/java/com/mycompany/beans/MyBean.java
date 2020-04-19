package com.mycompany.beans;

import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

public class MyBean {
    private HtmlOutputText errorComp;

    public void setErrorComp(HtmlOutputText errorComp) {
	this.errorComp = errorComp;
    }

    public HtmlOutputText getErrorComp() {
	return errorComp;
    }

    public void validateText(FacesContext context, UIComponent comp,
			     Object value) {
	if ("the kitchen sink".equals(value)) {
	    errorComp.setValue("I said anything but!");
	    errorComp.setStyle("color: red");
	    comp.getAttributes().put("style", "color: red");
	}
	else {
	    errorComp.setValue(null);
	    errorComp.setStyle(null);
	    comp.getAttributes().put("style", null);
	}
    }
}
