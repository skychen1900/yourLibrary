package com.mycompany.expense;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This class represents a report in the ReportRegistry.
 *
 * @author Hans Bergsten, Gefion Software <hans@gefionsoftware.com>
 * @version 1.0
 */
public class Report implements Serializable {
    public static final int STATUS_NEW = 0;
    public static final int STATUS_OPEN = 1;
    public static final int STATUS_SUBMITTED = 2;
    public static final int STATUS_ACCEPTED = 3;
    public static final int STATUS_REJECTED = 4;

    private int currentEntryId;
    private int id = -1;
    private String title;
    private String owner;
    private int status = STATUS_NEW;
    private Map entries;

    /**
     * Creates a new, empty instance.
     */
    public Report() {
        entries = new HashMap();
    }

    /**
     * Creates an instance that is a copy of the provided instance.
     */
    public Report(Report src) {
        setId(src.getId());
        setTitle(src.getTitle());
        setOwner(src.getOwner());
        setStatus(src.getStatus());
        setEntries(src.copyEntries());
        setCurrentEntryId(src.getCurrentEntryId());
    }

    /**
     * Returns the report ID.
     */
    public synchronized int getId() {
        return id;
    }

    /**
     * Sets the report ID.
     */
    public synchronized void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the report title.
     */
    public synchronized String getTitle() {
        return title;
    }

    /**
     * Sets the report title.
     */
    public synchronized void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the report owner.
     */
    public synchronized String getOwner() {
        return owner;
    }

    /**
     * Sets the report owner.
     */
    public synchronized void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * Returns the report status.
     */
    public synchronized int getStatus() {
        return status;
    }

    /**
     * Sets the report status.
     */
    public synchronized void setStatus(int status) {
        this.status = status;
    }

    /**
     * Sets the "id" property of the provided ReportEntry and saves a
     * copy of the ReportEntry (so that changes to the provided instance
     * doesn't change the content of the report).
     */
    public synchronized void addEntry(ReportEntry entry) {
        entry.setId(currentEntryId++);
        entries.put(new Integer(entry.getId()), new ReportEntry(entry));
    }

    /**
     * Removes the entry with the specified ID.
     */
    public synchronized void removeEntry(int id) {
        entries.remove(new Integer(id));
    }

    /**
     * Returns a copy of the entry with the specified ID.
     */
    public synchronized ReportEntry getEntry(int id) {
        return new ReportEntry((ReportEntry) entries.get(new Integer(id)));
    }

    /**
     * Returns a List with copies of all entries.
     */
    public synchronized List getEntries() {
        return new ArrayList(copyEntries().values());
    }

    /**
     * Returns the report start date.
     */
    public synchronized Date getStartDate() {
        Date date = null;
        if (!entries.isEmpty()) {
            List l = getEntriesSortedByDate();
            date = ((ReportEntry) l.get(0)).getDate();
        }
        return date;
    }

    /**
     * Returns the report end date.
     */
    public synchronized Date getEndDate() {
        Date date = null;
        if (!entries.isEmpty()) {
            List l = getEntriesSortedByDate();
            date = ((ReportEntry) l.get(entries.size() - 1)).getDate();
        }
        return date;
    }

    /**
     * Returns the total of all entry amounts.
     */
    public synchronized double getTotal() {
        double total = 0;
        Iterator i = entries.values().iterator();
        while (i.hasNext()) {
            ReportEntry e = (ReportEntry) i.next();
            total += e.getAmount();
        }
        return total;
    }

    /**
     * Returns a String with all report properties.
     */
    public String toString() {
        return "id: " + id + " title: " + getTitle() + 
            " owner: " + getOwner() +
            " startDate: " + getStartDate() + " endDate: " + getEndDate() +
            " status: " + getStatus();
    }

    /**
     * Returns all entries sorted by date.
     */
    private List getEntriesSortedByDate() {
        List l = getEntries();
        Collections.sort(l, new Comparator() {
                public int compare(Object o1, Object o2) {
                    Date d1 = ((ReportEntry) o1).getDate();
                    Date d2 = ((ReportEntry) o2).getDate();
                    return d1.compareTo(d2);
                }
        });
        return l;
    }

    /**
     * Returns the current ID value.
     */
    private int getCurrentEntryId() {
        return currentEntryId;
    }

    /**
     * Sets the current ID value.
     */
    private void setCurrentEntryId(int currentEntryId) {
        this.currentEntryId = currentEntryId;
    }

    /**
     * Returns a Map with copies of all entries.
     */
    private Map copyEntries() {
        Map copy = new HashMap();
        Iterator i = entries.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry e = (Map.Entry) i.next();
            copy.put(e.getKey(), new ReportEntry((ReportEntry) e.getValue()));
        }
        return copy;
    }

    /**
     * Sets the entries.
     */
    private void setEntries(Map entries) {
        this.entries = entries;
    }
}
