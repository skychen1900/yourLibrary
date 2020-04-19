package com.mycompany.expense;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This class is an implementation of the ReportRegistry for the
 * sample expense report application that uses the file system
 * for permanent storage. It's only intended as an example. For
 * real usage of the sample application, an implementation that
 * uses a database is a better choice.
 *
 * @author Hans Bergsten, Gefion Software <hans@gefionsoftware.com>
 * @version 1.0
 */
public class FileReportRegistry extends ReportRegistry {
    private int currentReportId;
    private Map reports;

    /**
     * Creates an instance and loads the current content, if any, from the
     * file system by calling the load() method.
     */
    public FileReportRegistry() throws RegistryException {
        reports = new HashMap();
        try {
            load();
        }
        catch (IOException e) {
            throw new RegistryException("Can't load ReportRegistry", e);
        }
    }

    /**
     * Adds a copy of the report to the registry.
     */
    public synchronized void addReport(Report report)
        throws RegistryException{
        report.setId(currentReportId++);
        reports.put(new Integer(report.getId()), new Report(report));
        try {
            save();
        }
        catch (IOException e) {
            throw new RegistryException("Can't save ReportRegistry", e);
        }
    }

    /**
     * Replaces an existing report in the registry with a copy of the
     * provided report.
     * Throws an IllegalStateException if there's no report with the ID of
     * the provided report in the registry.
     */
    public synchronized void updateReport(Report report)
        throws RegistryException{
        checkExists(report);
        reports.put(new Integer(report.getId()), new Report(report));
        try {
            save();
        }
        catch (IOException e) {
            throw new RegistryException("Can't save ReportRegistry", e);
        }
    }

    /**
     * Removes an existing report in the registry with ID of the provided
     * report.
     * Throws an IllegalStateException if there's no report with the ID of
     * the provided report in the registry.
     */
    public synchronized void removeReport(Report report)
        throws RegistryException{
        checkExists(report);
        reports.remove(new Integer(report.getId()));
        try {
            save();
        }
        catch (IOException e) {
            throw new RegistryException("Can't save ReportRegistry", e);
        }
    }

    /**
     * Returns a copy of the report with the specified ID, or null if
     * there's no matching report in the registry.
     */
    public synchronized Report getReport(int id) {
        return (Report) reports.get(new Integer(id));
    }

    /**
     * Returns a List with copies of all reports matching the
     * search criteria.
     */
    public synchronized List getReports(String owner, Date fromDate,
                                        Date toDate, int[] status) {
        List matches = new ArrayList();
        Iterator i = reports.values().iterator();
        while (i.hasNext()) {
            Report report = (Report) i.next();
            if (matchesCriteria(report, owner, fromDate, toDate, status)) {
                matches.add(new Report(report));
            }
        }
        return matches;
    }

    /**
     * Returns true if the report matches the non-null parameter
     * values.
     */
    private boolean matchesCriteria(Report report, String owner, 
                                    Date from, Date to, int[] status) {
        boolean matches = false;
        if ((owner == null || owner.equals(report.getOwner())) &&
            (from == null || (report.getStartDate() != null &&
             report.getStartDate().getTime() >= from.getTime())) &&
            (to == null || (report.getStartDate() != null &&
             report.getStartDate().getTime() <= to.getTime()))) {
            if (status == null) {
                matches = true;
            }
            else {
                for (int i = 0; i < status.length; i++) {
                    if (report.getStatus() == status[i]) {
                        matches = true;
                        break;
                    }
                }
            }
        }
        return matches;
    }

    /**
     * Tries to locate a report with the ID of the provided report,
     * and throws an IllegalStateException if there's no such report.
     */
    private void checkExists(Report report) {
        Integer id = new Integer(report.getId());
        if (reports == null || reports.get(id) == null) {
            throw new IllegalStateException("Report " + report.getId() +
                                            " doesn't exist");
        }
    }

    /**
     * Loads the registry from the file returned by the getStore()
     * method.
     */
    private void load() throws IOException {
        File store = getStore();
        try {
            ObjectInputStream is = 
                new ObjectInputStream(new FileInputStream(store));
            currentReportId = is.readInt();
            reports = (Map) is.readObject();
        }
        catch (FileNotFoundException fnfe) {
            // Ignore.
        }
        catch (ClassNotFoundException cnfe) {
            // Shouldn't happen, but log it if it does
            System.err.println("Error loading ReportRegistry: " +
                               cnfe.getMessage());
        }
    }

    /**
     * Saves the registry to the file returned by the getStore()
     * method.
     */
    private void save() throws IOException {
        File store = getStore();
        ObjectOutputStream os = 
            new ObjectOutputStream(new FileOutputStream(store));
        os.writeInt(currentReportId);
        os.writeObject(reports);
    }

    /**
     * Returns a File instance for a file named ".expense/store.ser"
     * in the home directory for the account running the JVM.
     */
    private File getStore() {
        File store = null;
        File homeDir = new File(System.getProperty("user.home"));
        File persistenceDir = new File(homeDir, ".expense");
        if (!persistenceDir.exists()) {
            persistenceDir.mkdir();
        }
        return new File(persistenceDir, "store.ser");
    }
}
