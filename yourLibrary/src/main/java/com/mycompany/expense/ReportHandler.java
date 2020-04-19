package com.mycompany.expense;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.servlet.http.HttpServletRequest;

/**
 * This class contains properties and methods for the JSF components
 * in the report area of the sample expense report application.
 *
 * @author Hans Bergsten, Gefion Software <hans@gefionsoftware.com>
 * @version 1.0
 */
public class ReportHandler {
    private static final int SORT_BY_TITLE = 0;
    private static final int SORT_BY_OWNER = 1;
    private static final int SORT_BY_DATE = 2;
    private static final int SORT_BY_TOTAL = 3;
    private static final int SORT_BY_STATUS = 4;

    private ReportRegistry registry;
    private Rules rules;
    private Report currentReport;
    private String currentUser;
    private boolean isManager;
    private DataModel reportsModel;
    private DataModel entriesModel;

    private Date from;
    private Date to;
    private int[] status;

    private boolean ascending = false;
    private int sortBy = SORT_BY_DATE;

    private int noOfRows = 5;
    private int firstRowIndex = 0;
    private int noOfPageLinks = 5;

    /**
     * Creates a new instance, initialized with a new Report and
     * information about the current user.
     */
    public ReportHandler() {
        rules = new Rules();
        currentReport = getCurrentReport();
        currentUser = getCurrentUser();
        isManager = isManager();
    }

    /**
     * Sets the ReportRegistry instance used by this application.
     */
    public void setReportRegistry(ReportRegistry registry) {
        this.registry = registry;
    }

    /**
     * Returns the current Report instance, or a new instance
     * if there's no current instance.
     */
    public Report getCurrentReport() {
        if (currentReport == null) {
            currentReport = createNewReport();
        }
        return currentReport; 
    }

    /**
     * Returns a List with Report instances matching the filtering
     * criteria.
     */
    public List getReports() {
        String user = null;
        if (!isManager) {
            user = getCurrentUser();
        }
        List l = null;
        try {
            l = registry.getReports(user, from, to, status);
        }
        catch (RegistryException e) {
            addMessage("registry_error", e.getMessage());
        }
        return l;
    }

    /**
     * Returns a DataModel with Report instances matching the
     * filtering criteria.
     */
    public DataModel getReportsModel() {
        if (reportsModel == null) {
            reportsModel = new ListDataModel();
        }
        reportsModel.setWrappedData(getReports());
        return reportsModel;
    }

    /**
     * Returns a DataModel with Report instances matching the
     * filtering criteria, sorted according to the current
     * sort column and order.
     */
    public DataModel getSortedReportsModel() {
        if (reportsModel == null) {
            reportsModel = new ListDataModel();
        }
        List reports = getReports();
        sortReports(reports);
        reportsModel.setWrappedData(reports);
        return reportsModel;
    }

    /*
     * Methods related to the report filtering.
     */

    /**
     * Returns the from date, or a Date representing the previous
     * month if no from date is set.
     */
    public Date getFrom() {
        if (from == null) {
            from = getPreviousMonth(new Date());
        }
        return from;
    }

    /**
     * Sets the from date.
     */
    public void setFrom(Date from) {
        this.from = from;
    }

    /**
     * Returns the to date, or a Date representing today if no
     * to date is set.
     */
    public Date getTo() {
        if (to == null) {
            to = new Date();
        }
        return to;
    }

    /**
     * Sets the to date.
     */
    public void setTo(Date to) {
        this.to = to;
    }

    /**
     * Returns the status codes for displayed reports, or the
     * code for "Submitted" if no status code is set and the
     * current user is a manager, or all status codes if no
     * status code is set and the current user isn't a manager.
     */
    public String[] getStatus() {
        if (status == null) {
            if (isManager) {
                status = new int[1];
                status[0] = Report.STATUS_SUBMITTED;
            }
            else {
                status = new int[4];
                status[0] = Report.STATUS_OPEN;
                status[1] = Report.STATUS_SUBMITTED;
                status[2] = Report.STATUS_ACCEPTED;
                status[3] = Report.STATUS_REJECTED;
            }
        }

        // Convert the int[] to a String[] to match the SelectItem type
        String[] stringStatus = new String[status.length];
        for (int i = 0; i < status.length; i++) {
            stringStatus[i] = String.valueOf(status[i]);
        }
        return stringStatus;
    }

    /**
     * Sets the status codes to display.
     */
    public void setStatus(String[] stringStatus) {
        // Convert the String[], matching the SelectItem type, to 
        // the int[] used internally
        status = null;
        if (stringStatus != null) {
            status = new int[stringStatus.length];
            for (int i = 0; i < stringStatus.length; i++) {
                status[i] = Integer.valueOf(stringStatus[i]).intValue();
            }
        }
    }

    /*
     * Methods related to the report entries for the current report.
     */

    /**
     * Returns a List with the ReportEntry instances for the current
     * Report, sorted on the entry dates.
     */
    public List getCurrentReportEntries() {
        List currentList = currentReport.getEntries();
        Collections.sort(currentList, new Comparator() {
                public int compare(Object o1, Object o2) {
                    Date d1 = ((ReportEntry) o1).getDate();
                    Date d2 = ((ReportEntry) o2).getDate();
                    return d1.compareTo(d2);
                }
            });
        return currentList;
    }

    /**
     * Returns a DataModel with the ReportEntry instances for the current
     * Report, sorted on the entry dates.
     */
    public DataModel getReportEntriesModel() {
        if (entriesModel == null) {
            entriesModel = new ListDataModel();
	    entriesModel.setWrappedData(getCurrentReportEntries());
        }
        return entriesModel;
    }

    /**
     * Creates a new Report and makes it the current report.
     */
    public String create() {
        currentReport = createNewReport();
        return "success";
    }

    /**
     * Deletes the current report, or queues an error message if
     * the current user isn't allowed to do that or the registry
     * throws an exception.
     */
    public String delete() {
        try {
            refreshCache();
        }
        catch (RegistryException e) {
            addMessage("registry_error", e.getMessage());
            return "error";
        }

        if (!rules.canDelete(currentUser, isManager, currentReport)) {
            addMessage("report_no_delete_access", null);
            return "error";
        }

        String outcome = "success";
        try {
            registry.removeReport(currentReport);
            currentReport = createNewReport();
        }
        catch (RegistryException e) {
            addMessage("registry_error", e.getMessage());
            outcome = "error";
        }
        return outcome;
    }

    /**
     * Submits the current report, or queues an error message if
     * the current user isn't allowed to do that or the registry
     * throws an exception.
     */
    public String submit() {
        try {
            refreshCache();
        }
        catch (RegistryException e) {
            addMessage("registry_error", e.getMessage());
            return "error";
        }

        if (!rules.canSubmit(currentUser, isManager, currentReport)) {
            addMessage("report_no_submit_access", null);
            return "error";
        }

        String outcome = "success";
        int currentStatus = currentReport.getStatus();
        currentReport.setStatus(Report.STATUS_SUBMITTED);
        try {
            saveReport();
        }
        catch (RegistryException e) {
            addMessage("registry_error", e.getMessage());
            currentReport.setStatus(currentStatus);
            outcome = "error";
        }
        return outcome;
    }

    /**
     * Accepts the current report, or queues an error message if
     * the current user isn't allowed to do that or the registry
     * throws an exception.
     */
    public String accept() {
        try {
            refreshCache();
        }
        catch (RegistryException e) {
            addMessage("registry_error", e.getMessage());
            return "error";
        }

        if (!rules.canAccept(currentUser, isManager, currentReport)) {
            addMessage("report_no_accept_access", null);
            return "error";
        }

        String outcome = "success";
        int currentStatus = currentReport.getStatus();
        currentReport.setStatus(Report.STATUS_ACCEPTED);
        try {
            saveReport();
        }
        catch (RegistryException e) {
            addMessage("registry_error", e.getMessage());
            currentReport.setStatus(currentStatus);
            outcome = "error";
        }
        return outcome;
    }

    /**
     * Rejects the current report, or queues an error message if
     * the current user isn't allowed to do that or the registry
     * throws an exception.
     */
    public String reject() {
        try {
            refreshCache();
        }
        catch (RegistryException e) {
            addMessage("registry_error", e.getMessage());
            return "error";
        }

        if (!rules.canReject(currentUser, isManager, currentReport)) {
            addMessage("report_no_reject_access", null);
            return "error";
        }

        String outcome = "success";
        int currentStatus = currentReport.getStatus();
        currentReport.setStatus(Report.STATUS_REJECTED);
        try {
            saveReport();
        }
        catch (RegistryException e) {
            addMessage("registry_error", e.getMessage());
            currentReport.setStatus(currentStatus);
            outcome = "error";
        }
        return outcome;
    }

    /**
     * Makes the report at the current row in the reports DataModel the
     * current report, or queues an error message if the current user isn't
     * allowed to do that or the registry throws an exception.
     */
    public String select() {
        Report selectedReport = (Report) reportsModel.getRowData();
        if (!rules.canView(currentUser, isManager, selectedReport)) {
            addMessage("report_no_view_access", null);
            return "error";
        }
        setCurrentReport(selectedReport);
        return "success";
    }

    /**
     * Adds an entry to the current report, or queues an error message if
     * the current user isn't allowed to do that or the registry
     * throws an exception.
     */
    public String addEntry(ReportEntry entry) {
        try {
            refreshCache();
        }
        catch (RegistryException e) {
            addMessage("registry_error", e.getMessage());
            return "error";
        }
        
        if (!rules.canEdit(currentUser, isManager, currentReport)) {
            addMessage("report_no_edit_access", null);
            return "error";
        }

        String outcome = "success";
        currentReport.addEntry(entry);
        try {
            saveReport();
        }
        catch (RegistryException e) {
            currentReport.removeEntry(entry.getId());
            addMessage("registry_error", e.getMessage());
            outcome = "error";
        }
        return outcome;
    }

    /**
     * Removes the entry represented by the current row in the entries
     * DataModel from the current report, or queues an error message if
     * the current user isn't allowed to do that or the registry
     * throws an exception.
     */
    public String removeEntry() {
        ReportEntry selectedEntry = 
            (ReportEntry) entriesModel.getRowData();
        int entryId = selectedEntry.getId();
        return removeEntry(entryId);
    }

    /**
     * Removes the specified entry from the current report, or queues
     * an error message if the current user isn't allowed to do that or
     * the registry throws an exception.
     */
    public String removeEntry(int entryId) {
        try {
            refreshCache();
        }
        catch (RegistryException e) {
            addMessage("registry_error", e.getMessage());
            return "error";
        }

        if (!rules.canEdit(currentUser, isManager, currentReport)) {
            addMessage("report_no_edit_access", null);
            return "error";
        }

        String outcome = "success";
        ReportEntry currentEntry = currentReport.getEntry(entryId);
        currentReport.removeEntry(entryId);
        try {
            saveReport();
        }
        catch (RegistryException e) {
            addMessage("registry_error", e.getMessage());
            currentReport.addEntry(currentEntry);
            outcome = "error";
        }
        return outcome;
    }

    /**
     * Uses the entry represented by the current row in the entries
     * DataModel to update the corresponding entry in the current report,
     * or queues an error message if the current user isn't allowed to
     * do that or the registry throws an exception.
     */
    public String updateEntry() {
        ReportEntry selectedEntry = 
            (ReportEntry) entriesModel.getRowData();
        int entryId = selectedEntry.getId();
        return updateEntry(selectedEntry);
    }

    /**
     * Uses the provided entry to update the corresponding entry in the
     * current report, or queues an error message if the current user
     * isn't allowed to do that or the registry throws an exception.
     */
    public String updateEntry(ReportEntry entry) {
        try {
            refreshCache();
        }
        catch (RegistryException e) {
            addMessage("registry_error", e.getMessage());
            return "error";
        }

        if (!rules.canEdit(currentUser, isManager, currentReport)) {
            addMessage("report_no_edit_access", null);
            return "error";
        }

        String outcome = "success";
        ReportEntry currentEntry = currentReport.getEntry(entry.getId());
        currentReport.removeEntry(entry.getId());
        currentReport.addEntry(entry);
        try {
            saveReport();
        }
        catch (RegistryException e) {
            addMessage("registry_error", e.getMessage());
            currentReport.removeEntry(entry.getId());
            currentReport.addEntry(currentEntry);
            outcome = "error";
        }
        return outcome;
    }

    /**
     * Returns "true" if the current report is new (no entries yet).
     */
    public boolean isNewDisabled() {
        return isReportNew();
    }

    /**
     * Returns "true" if the current report is new (no entries yet) or
     * the current user isn't allowed to delete the report.
     */
    public boolean isDeleteDisabled() {
        return isReportNew() || 
            !rules.canDelete(currentUser, isManager, currentReport);
    }

    /**
     * Returns "true" if the current report is new (no entries yet) or
     * the current user isn't allowed to submit the report.
     */
    public boolean isSubmitDisabled() {
        return isReportNew() || 
            !rules.canSubmit(currentUser, isManager, currentReport);
    }

    /**
     * Returns "true" if the current report is new (no entries yet) or
     * the current user isn't allowed to accept the report.
     */
    public boolean isAcceptDisabled() {
        return isReportNew() || 
            !rules.canAccept(currentUser, isManager, currentReport);
    }

    /**
     * Returns "true" if the current user is a manager.
     */
    public boolean isAcceptRendered() {
        return isManager;
    }

    /**
     * Returns "true" if the current report is new (no entries yet) or
     * the current user isn't allowed to reject the report.
     */
    public boolean isRejectDisabled() {
        return isReportNew() || 
            !rules.canReject(currentUser, isManager, currentReport);
    }

    /**
     * Returns "true" if the current user is a manager.
     */
    public boolean isRejectRendered() {
        return isManager;
    }

    /**
     * Returns "true" if the current report isn't new (no entries yet) and
     * the current user isn't allowed to edit the report.
     */
    public boolean isEditDisabled() {
        return !isReportNew() &&
            !rules.canEdit(currentUser, isManager, currentReport);
    }

    /**
     * Returns "true" if the current user is associated with the
     * "manager" role.
     */
    public boolean isManager() {
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext ec = context.getExternalContext();
        return ec.isUserInRole("manager");
    }


    /*
     * Methods releated to sorting the reports list.
     */

    /**
     * Sets the sorting column for the reports list to the "title" column,
     * reversing the order if the table is already sorted by this column.
     */
    public String sortByTitle() {
        if (sortBy == SORT_BY_TITLE) {
            ascending = !ascending;
        }
        else {
            sortBy = SORT_BY_TITLE;
            ascending = true;
        }
        return "success";
    }

    /**
     * Sets the sorting column for the reports list to the "owner" column,
     * reversing the order if the table is already sorted by this column.
     */
    public String sortByOwner() {
        if (sortBy == SORT_BY_OWNER) {
            ascending = !ascending;
        }
        else {
            sortBy = SORT_BY_OWNER;
            ascending = true;
        }
        return "success";
    }

    /**
     * Sets the sorting column for the reports list to the "date" column,
     * reversing the order if the table is already sorted by this column.
     */
    public String sortByDate() {
        if (sortBy == SORT_BY_DATE) {
            ascending = !ascending;
        }
        else {
            sortBy = SORT_BY_DATE;
            ascending = false;
        }
        return "success";
    }

    /**
     * Sets the sorting column for the reports list to the "total" column,
     * reversing the order if the table is already sorted by this column.
     */
    public String sortByTotal() {
        if (sortBy == SORT_BY_TOTAL) {
            ascending = !ascending;
        }
        else {
            sortBy = SORT_BY_TOTAL;
            ascending = true;
        }
        return "success";
    }

    /**
     * Sets the sorting column for the reports list to the "status" column,
     * reversing the order if the table is already sorted by this column.
     */
    public String sortByStatus() {
        if (sortBy == SORT_BY_STATUS) {
            ascending = !ascending;
        }
        else {
            sortBy = SORT_BY_STATUS;
            ascending = true;
        }
        return "success";
    }

    /**
     * Sorts the reports according to the current sort column and order.
     */
    private void sortReports(List reports) {
        switch (sortBy) {
            case SORT_BY_TITLE:
                Collections.sort(reports, 
                    ascending ? ASC_TITLE_COMPARATOR : DESC_TITLE_COMPARATOR);
                break;
            case SORT_BY_OWNER:
                Collections.sort(reports, 
                    ascending ? ASC_OWNER_COMPARATOR : DESC_OWNER_COMPARATOR);
                break;
            case SORT_BY_DATE:
                Collections.sort(reports, 
                    ascending ? ASC_DATE_COMPARATOR : DESC_DATE_COMPARATOR);
                break;
            case SORT_BY_TOTAL:
                Collections.sort(reports, 
                    ascending ? ASC_TOTAL_COMPARATOR : DESC_TOTAL_COMPARATOR);
                break;
            case SORT_BY_STATUS:
                Collections.sort(reports, 
                    ascending ? ASC_STATUS_COMPARATOR : DESC_STATUS_COMPARATOR);
                break;
        }
    }

    private static final Comparator ASC_TITLE_COMPARATOR = new Comparator() {
            public int compare(Object o1, Object o2) {
                String s1 = ((Report) o1).getTitle();
                String s2 = ((Report) o2).getTitle();
                return s1.compareTo(s2);
            }
        };

    private static final Comparator DESC_TITLE_COMPARATOR = new Comparator() {
            public int compare(Object o1, Object o2) {
                String s1 = ((Report) o1).getTitle();
                String s2 = ((Report) o2).getTitle();
                return s2.compareTo(s1);
            }
        };

    private static final Comparator ASC_OWNER_COMPARATOR = new Comparator() {
            public int compare(Object o1, Object o2) {
                String s1 = ((Report) o1).getOwner();
                String s2 = ((Report) o2).getOwner();
                return s1.compareTo(s2);
            }
        };

    private static final Comparator DESC_OWNER_COMPARATOR = new Comparator() {
            public int compare(Object o1, Object o2) {
                String s1 = ((Report) o1).getOwner();
                String s2 = ((Report) o2).getOwner();
                return s2.compareTo(s1);
            }
        };

    private static final Comparator ASC_DATE_COMPARATOR = new Comparator() {
            public int compare(Object o1, Object o2) {
                Date d1 = ((Report) o1).getStartDate();
                Date d2 = ((Report) o2).getStartDate();
                return d1.compareTo(d2);
            }
        };

    private static final Comparator DESC_DATE_COMPARATOR = new Comparator() {
            public int compare(Object o1, Object o2) {
                Date d1 = ((Report) o1).getStartDate();
                Date d2 = ((Report) o2).getStartDate();
                return d2.compareTo(d1);
            }
        };

    private static final Comparator ASC_TOTAL_COMPARATOR = new Comparator() {
            public int compare(Object o1, Object o2) {
                Double d1 = new Double(((Report) o1).getTotal());
                Double d2 = new Double(((Report) o2).getTotal());

                return d1.compareTo(d2);
            }
        };

    private static final Comparator DESC_TOTAL_COMPARATOR = new Comparator() {
            public int compare(Object o1, Object o2) {
                Double d1 = new Double(((Report) o1).getTotal());
                Double d2 = new Double(((Report) o2).getTotal());
                return d2.compareTo(d1);
            }
        };

    private static final Comparator ASC_STATUS_COMPARATOR = new Comparator() {
            public int compare(Object o1, Object o2) {
                Integer i1 = new Integer(((Report) o1).getStatus());
                Integer i2 = new Integer(((Report) o2).getStatus());
                return i1.compareTo(i2);
            }
        };

    private static final Comparator DESC_STATUS_COMPARATOR = new Comparator() {
            public int compare(Object o1, Object o2) {
                Integer i1 = new Integer(((Report) o1).getStatus());
                Integer i2 = new Integer(((Report) o2).getStatus());
                return i2.compareTo(i1);
            }
        };

    /*
     * Methods related to the display of the reports table.
     */

    /**
     * Returns the number of rows to show in the reports table.
     */
    public int getNoOfRows() {
        return noOfRows;
    }

    /**
     * Sets the number of rows to show in the reports table.
     */
    public void setNoOfRows(int noOfRows) {
        this.noOfRows = noOfRows;
    }

    /**
     * Returns the index for the first row in the reports table.
     */
    public int getFirstRowIndex() {
        return firstRowIndex;
    }

    /**
     * Returns "true" if the first page of reports is displayed.
     */
    public boolean isScrollFirstDisabled() {
        return firstRowIndex == 0;
    }

    /**
     * Returns "true" if the last page of reports is displayed.
     */
    public boolean isScrollLastDisabled() {
        return firstRowIndex >= reportsModel.getRowCount() - noOfRows;
    }

    /**
     * Returns "true" if there aren't enough rows to scroll forward
     * one page.
     */
    public boolean isScrollNextDisabled() {
        return firstRowIndex >= reportsModel.getRowCount() - noOfRows;
    }

    /**
     * Returns "true" if the first page is displayed.
     */
    public boolean isScrollPreviousDisabled() {
        return firstRowIndex == 0;
    }

    /**
     * Sets the index for the first row to display in the reports
     * list to zero.
     */
    public String scrollFirst() {
        firstRowIndex = 0;
        return "success";
    }

    /**
     * Sets the index for the first row to display in the reports
     * list to the index of the top row for the last page.
     */
    public String scrollLast() {
        firstRowIndex = reportsModel.getRowCount() - noOfRows;
        if (firstRowIndex < 0) {
            firstRowIndex = 0;
        }
        return "success";
    }

    /**
     * Sets the index for the first row to display in the reports
     * table to the index of the top row for the next page, or
     * to zero if there is no more page.
     */
    public String scrollNext() {
        firstRowIndex += noOfRows;
        if (firstRowIndex >= reportsModel.getRowCount()) {
            firstRowIndex = reportsModel.getRowCount() - noOfRows;
            if (firstRowIndex < 0) {
                firstRowIndex = 0;
            }
        }
        
        return "success";
    }

    /**
     * Sets the index for the first row to display in the reports
     * table to the index of the top row for the previou page, or
     * to zero if there is no more page.
     */
    public String scrollPrevious() {
        firstRowIndex -= noOfRows;
        if (firstRowIndex < 0) {
            firstRowIndex = 0;
        }
        return "success";
    }

    /*
     * Method releated to the page navigation bar.
     */

    /**
     * Returns a List with Page instances for each page of reports.
     */
    public List getPages() {
	int totalNoOfRows = getSortedReportsModel().getRowCount();
        int noOfPages =  totalNoOfRows / noOfRows;
	if (totalNoOfRows % noOfRows > 0) {
	    noOfPages += 1;
	}
	
        List pages = new ArrayList(noOfPages);
        for (int i = 0; i < noOfPages; i++) {
	    pages.add(new Page(i + 1, this));
        }
	return pages;
    }

    /**
     * Returns the number of links to render in the navigation bar.
     */
    public int getNoOfPageLinks() {
	return noOfPageLinks;
    }

    /**
     * Returns the index for the first page link to render.
     */
    public int getFirstPageIndex() {
        int noOfPages = getPages().size();
	if (noOfPages <= noOfPageLinks) {
	    return 0;
	}
	
	int firstPageIndex = (firstRowIndex / noOfRows) - 1;
	if (firstPageIndex < 0) {
	    firstPageIndex = 0;
	}
	else if (noOfPages - firstPageIndex < noOfPageLinks) {
	    firstPageIndex = noOfPages - noOfPageLinks;
	}
	return firstPageIndex;
    }

    /**
     * Returns the index for the page matching the currently
     * rendered set of reports in the reports list.
     */
    public int getCurrentPage() {
	return (firstRowIndex / noOfRows) + 1;
    }

    /**
     * This class represents a page in the page navigation bar model.
     */
    public static class Page {
	private int number;
	private ReportHandler handler;

	public Page(int number, ReportHandler handler) {
	    this.number = number;
	    this.handler = handler;
	}

	public int getNumber() {
	    return number;
	}

	public String select() {
	    handler.setCurrentPage(number); 
	    return null;
	}
    }

    /**
     * Sets the index for the first row in the reports table to match
     * the specified page number.
     */
    private void setCurrentPage(int currentPage) {
	firstRowIndex = (currentPage - 1) * noOfRows;
    }

    /*
     * Private methods.
     */

    /**
     * If the current report is new, changes its status to "open"
     * and add it to the registry; otherwise, updates the report
     * in the registry. Resets the cached entries DataModel.
     */
    private void saveReport() throws RegistryException {
        if (isReportNew()) {
            currentReport.setStatus(Report.STATUS_OPEN);
            registry.addReport(currentReport);
        }
        else {
            registry.updateReport(currentReport);
        }
	entriesModel = null;
    }

    /**
     * If the current report isn't new (i.e., not yet stored),
     * refreshes the current report with a copy from the registry
     * to ensure the local copy is the latest version.
     */
    private void refreshCache() throws RegistryException {
        if (!isReportNew()) {
            setCurrentReport(registry.getReport(currentReport.getId()));
        }
    }

    /**
     * Creates a new Report instance initialized with the current
     * user as the owner, and resets the cached entries DataModel.
     */
    private Report createNewReport() {
        Report report = new Report();
        report.setOwner(getCurrentUser());
	entriesModel = null;
        return report;
    }

    /**
     * Makes the provided Report the current report, and resets the
     * cached entries DataModel.
     */
    private void setCurrentReport(Report report) {
	currentReport = report;
	entriesModel = null;
    }

    /**
     * Returns a Date one month prior to the provided Date.
     */
    private Date getPreviousMonth(Date current) {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(current);
        c.add(Calendar.MONTH, -1);
        return c.getTime();
    }

    /**
     * Returns the username of the current, authenticated user.
     */
    private String getCurrentUser() {
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext ec = context.getExternalContext();
        return ec.getRemoteUser();
    }

    /**
     * Adds the message matching the key in the application's
     * resource bundle, formatted with the parameters (if any),
     * the the JSF message queue as a global message.
     */
    private void addMessage(String messageKey, Object param) {
        FacesContext context = FacesContext.getCurrentInstance();
        Application application = context.getApplication();
        String messageBundleName = application.getMessageBundle();
        Locale locale = context.getViewRoot().getLocale();
        ResourceBundle rb = 
            ResourceBundle.getBundle(messageBundleName, locale);
        String msgPattern = rb.getString(messageKey);
        String msg = msgPattern;
        if (param != null) {
            Object[] params = {param};
            msg = MessageFormat.format(msgPattern, params);
        }
        FacesMessage facesMsg = 
            new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg);
        context.addMessage(null, facesMsg);
    }

    /**
     * Returns "true" if the current report has status "new".
     */
    private boolean isReportNew() {
        return currentReport.getStatus() == Report.STATUS_NEW;
    }
}
