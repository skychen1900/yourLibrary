package com.mycompany.expense;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.MissingResourceException;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

/**
 * This class contains properties and methods for the JSF components
 * in the report entry area of the sample expense report aplication.
 *
 * @author Hans Bergsten, Gefion Software <hans@gefionsoftware.com>
 * @version 1.0
 */
public class EntryHandler {
    private ReportHandler reportHandler;
    private ReportEntry currentEntry;
    private Map expenseTypes;
    private List expenseTypeChoices;
    private Map specialTypes;
    private List specialChoices;
    private boolean includeSpecial;

    /**
     * Sets a reference to the ReportHandler that this instance
     * interacts with.
     */
    public void setReportHandler(ReportHandler reportHandler) {
	this.reportHandler = reportHandler;
    }

    /**
     * Sets the standard expense type choice values.
     */
    public void setExpenseTypes(Map expenseTypes) {
	this.expenseTypes = expenseTypes;
    }

    /**
     * Sets the special expense type choice values.
     */
    public void setSpecialTypes(Map specialTypes) {
	this.specialTypes = specialTypes;
    }

    /**
     * Returns a List with SelectItem instances for the
     * standard expense type choices.
     */
    public List getExpenseTypeChoices() {
	if (expenseTypeChoices == null) {
	    expenseTypeChoices = new ArrayList();
	    Iterator i = expenseTypes.entrySet().iterator();
	    while (i.hasNext()) {
		Map.Entry me = (Map.Entry) i.next();
		expenseTypeChoices.add(new SelectItem(me.getValue(),
		    (String) me.getKey()));
	    }
	}
	return expenseTypeChoices;
    }

    /**
     * Returns a List with SelectItem instances for the
     * expense type choices, including the special choices if
     * the "includeSpecial" flag is set to "true".
     */
    public List getCurrentChoices() {
	List choices = new ArrayList();
	choices.addAll(getExpenseTypeChoices());
	if (includeSpecial) {
	    choices.addAll(getSpecialChoices());
	}
	Iterator i = choices.iterator();
	while (i.hasNext()) {
	    SelectItem si = (SelectItem) i.next();
	}
	return choices;
    }

    /**
     * Returns a List with SelectItem instances matching the
     * standard expense type choices with labels localized
     * for the view's locale, from a resource bundle with the
     * base name "entryTypes".
     */
    public List getI18nChoices() {
	FacesContext context = FacesContext.getCurrentInstance();
	Locale locale = context.getViewRoot().getLocale();
	ResourceBundle bundle = 
	    ResourceBundle.getBundle("entryTypes", locale);
	List i18nChoices = new ArrayList();
	Iterator i = expenseTypes.entrySet().iterator();
	while (i.hasNext()) {
	    Map.Entry me = (Map.Entry) i.next();
	    i18nChoices.add(new SelectItem(me.getValue(),
		getResource(bundle, (String) me.getKey())));
	}
	return i18nChoices;
    }

    /**
     * Returns the current ReportEntry instance, or a new instance
     * if there's no current instance.
     */
    public ReportEntry getCurrentEntry() {
	if (currentEntry == null) {
	    currentEntry = new ReportEntry();
	}
	return currentEntry;
    }

    /**
     * Adds the current entry to the current report by calling
     * the addEntry() method on the ReportHandler.
     */
    public String add() {
	return reportHandler.addEntry(currentEntry);
    }

    /**
     * Toggles the value of the "includeSpecial" flag. This method
     * is used as an action method for a command component.
     */
    public String toggleTypes() {
	includeSpecial = !includeSpecial;
	return "success";
    }

    /**
     * Toggles the value of the "includeSpecial" flag. This method
     * is used as value change listener method for an input component.
     */
    public void toggleTypes(ValueChangeEvent event) {
	includeSpecial = !includeSpecial;
	FacesContext.getCurrentInstance().renderResponse();
    }

    /**
     * Returns a List with SelectItem instances for the
     * special expense type choices.
     */
    private List getSpecialChoices() {
	if (specialChoices == null) {
	    specialChoices = new ArrayList();
	    if (specialTypes != null) {
		Iterator i = specialTypes.entrySet().iterator();
		while (i.hasNext()) {
		    Map.Entry me = (Map.Entry) i.next();
		    specialChoices.add(new SelectItem(me.getValue(),
			(String) me.getKey()));
		}
	    }
	}
	Iterator i = specialChoices.iterator();
	while (i.hasNext()) {
	    SelectItem si = (SelectItem) i.next();
	}
	return specialChoices;
    }

    /**
     * Returns the resource matching the provided key after
     * replacing all spaces with underscores, or a String
     * containing the key embedded in question marks if no
     * resource matches the key.
     */
    private String getResource(ResourceBundle bundle, String key) {
	// Replace all spaces with underscore
	key = key.replaceAll(" ", "_");
	String resource = null;
	try {
	    resource = bundle.getString(key);
	}
	catch (MissingResourceException e) {
	    resource = "???" + key + "???";
	}
	return resource;
    }
}
