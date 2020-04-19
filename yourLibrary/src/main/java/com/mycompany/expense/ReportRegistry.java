package com.mycompany.expense;

import java.util.Date;
import java.util.List;

/**
 * This abstract class represents the report registry for the
 * sample expense report application. Concrete subclasses can
 * use any type of permanent storage.
 *
 * @author Hans Bergsten, Gefion Software <hans@gefionsoftware.com>
 * @version 1.0
 */
public abstract class ReportRegistry {
    /**
     * Adds a copy of the report to the registry.
     */
    public abstract void addReport(Report report) throws RegistryException;

    /**
     * Replaces an existing report in the registry with a copy of the
     * provided report.
     */
    public abstract void updateReport(Report report) throws RegistryException;

    /**
     * Removes an existing report in the registry with ID of the provided
     * report.
     */
    public abstract void removeReport(Report report) throws RegistryException;

    /**
     * Returns a copy of the report with the specified ID, or null if
     * there's no matching report in the registry.
     */
    public abstract Report getReport(int id) throws RegistryException;

    /**
     * Returns a List with copies of all reports matching the
     * search criteria.
     */
    public abstract List getReports(String owner, Date from, Date to,
        int[] status) throws RegistryException;
}
