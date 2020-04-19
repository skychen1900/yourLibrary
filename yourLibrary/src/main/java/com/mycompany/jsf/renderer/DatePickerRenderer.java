package com.mycompany.jsf.renderer;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.ValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.model.SelectItem;
import javax.faces.render.Renderer;

/**
 * This class is a JSF Renderer for the "javax.faces.Input"
 * component type. It renders a date value as three selection
 * lists for the year, month and day.
 *
 * @author Hans Bergsten, Gefion Software <hans@gefionsoftware.com>
 * @version 1.0
 */
public class DatePickerRenderer extends Renderer {

    /**
     * Creates a Map to hold the year, month and day values from the request
     * parameters and saves it as the submitted value for the component.
     */
    public void decode(FacesContext context, UIComponent component) {
        

        // If the component is disabled, do not change the value of the
        // component, to avoid resetting a value that can't changed
	// on the client (the client doesn't send it)
        if (isDisabledOrReadOnly(component)) {
            return;
        } 
        
        String clientId = component.getClientId(context);
        Map params = context.getExternalContext().getRequestParameterMap();
	String year = (String) params.get(clientId + "_year");
	if (year != null) {
	    Map dateParts = new HashMap();
	    dateParts.put("year", year);
	    dateParts.put("month", params.get(clientId + "_month"));
	    dateParts.put("day", params.get(clientId + "_day"));
	    ((EditableValueHolder) component).setSubmittedValue(dateParts);
	}
    }

    /**
     * Returns a Date instance created from the year, month and day
     * values held by the Map submitted value.
     */
    public Object getConvertedValue(FacesContext context, 
	UIComponent component, Object submittedValue) {

	Map dateParts = (Map) submittedValue;
	int year = Integer.parseInt((String) dateParts.get("year"));
	int month = Integer.parseInt((String) dateParts.get("month"));
	int day = Integer.parseInt((String) dateParts.get("day"));
	return new Date(year - 1900, month - 1, day);
    }

    /**
     * Renders selection lists for the year, month and day value.
     */
    public void encodeBegin(FacesContext context, UIComponent component) 
            throws IOException {

        if (!component.isRendered()) {
            return;
        }

	Date date = null;
	Map submittedValue = (Map) 
	    ((EditableValueHolder) component).getSubmittedValue();
	if (submittedValue != null) {
	    date = 
		(Date) getConvertedValue(context, component, submittedValue);
	}
	else {
	    Object value = ((ValueHolder) component).getValue();
	    date = value instanceof Date ? (Date) value : new Date();
	}

	String styleClass =
	    (String) component.getAttributes().get("styleClass");
        int startYear = date.getYear() + 1900;
	Object startYearAttr = component.getAttributes().get("startYear");
	if (startYearAttr != null) {
	    startYear = ((Number) startYearAttr).intValue();
	}
        int years = 5;
	Object yearsAttr = component.getAttributes().get("years");
	if (yearsAttr != null) {
	    years = ((Number) yearsAttr).intValue();
	}

	String clientId = component.getClientId(context);
        ResponseWriter out = context.getResponseWriter();
	renderMenu(out, getYears(startYear, years), date.getYear() + 1900, 
	    clientId + "_year", styleClass, component);
	renderMenu(out, getMonths(), date.getMonth() + 1, 
	    clientId + "_month", styleClass, component);
	renderMenu(out, getDays(), date.getDate(),
	    clientId + "_day", styleClass, component);
    }

    /**
     * Returns a List with SelectItem instances representing the
     * specified number of years, starting with the specified start
     * year.
     */
    private List getYears(int startYear, int noOfyears) {
	List years = new ArrayList();
	for (int i = startYear; i < startYear + noOfyears; i++) {
	    Integer year = new Integer(i);
	    years.add(new SelectItem(year, year.toString()));
	}
	return years;
    }

    /**
     * Returns a List with SelectItem instances representing all
     * months.
     */
    private List getMonths() {
	DateFormatSymbols dfs = new DateFormatSymbols();
	String[] names = dfs.getMonths();
	List months = new ArrayList();
	for (int i = 0; i < 12; i++) {
	    Integer key = new Integer(i + 1);
	    String label = names[i];
	    months.add(new SelectItem(key, label));
	}
	return months;
    }

    /**
     * Returns a List with SelectItem instances representing 31
     * days.
     */
    private List getDays() {
	List days = new ArrayList();
	for (int i = 1; i < 32; i++) {
	    Integer day = new Integer(i);
	    days.add(new SelectItem(day, day.toString()));
	}
	return days;
    }

    /**
     * Writes a "select" element with "option" elements based on
     * the List with SelectItem instances, with a "selected"
     * attribute for the SelectItem matching the selected value.
     * If a CSS class is specified, it's used as the "class"
     * attribute of the "select" element.
     */
    private void renderMenu(ResponseWriter out, List items, int selected, 
        String clientId, String styleClass, UIComponent component) 
	throws IOException {

	out.startElement("select", component);
	out.writeAttribute("name", clientId, "id");
	if (styleClass != null) {
	    out.writeAttribute("class", styleClass, "styleClass");
	}
	
	Iterator i = items.iterator();
	while (i.hasNext()) {
	    SelectItem si = (SelectItem) i.next();
	    Integer value = (Integer) si.getValue();
	    String label = si.getLabel();
	    out.startElement("option", component);
	    out.writeAttribute("value", value, null);
	    if (value.intValue() == selected) {
		out.writeAttribute("selected", "selected", null);
	    }
	    out.writeText(label, null);
	}
	out.endElement("select");
    }

    /**
     * Returns true if one or both of the HTML attributes "disabled"
     * or "readonly" are set to true.
     */
    private boolean isDisabledOrReadOnly(UIComponent component) {
	boolean disabled = false;
	boolean readOnly = false;

	Object disabledAttr = component.getAttributes().get("disabled");
	if (disabledAttr != null) {
	    disabled = disabledAttr.equals(Boolean.TRUE);
	}
	Object readOnlyAttr = component.getAttributes().get("readonly");
	if (readOnlyAttr != null) {
	    readOnly = readOnlyAttr.equals(Boolean.TRUE);
	}
	return disabled || readOnly;
    }
}
