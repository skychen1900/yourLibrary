package com.mycompany.expense;

import java.io.Serializable;
import java.util.Date;

/**
 * This class represents an entry in a Report.
 *
 * @author Hans Bergsten, Gefion Software <hans@gefionsoftware.com>
 * @version 1.0
 */
public class ReportEntry implements Serializable {
    private int id = -1;
    private Date date;
    private int type;
    private double amount;

    /**
     * Creates a new, empty instance.
     */
    public ReportEntry() {
    }

    /**
     * Creates an instance that is a copy of the provided instance.
     */
    public ReportEntry(ReportEntry src) {
        this.setId(src.getId());
        this.setDate(src.getDate());
        this.setType(src.getType());
        this.setAmount(src.getAmount());
    }

    /**
     * Returns the entry ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the entry ID.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the entry date.
     */
    public Date getDate() {
        if (date == null) {
            date = new Date();
        }
        return date;
    }

    /**
     * Sets the entry date.
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Returns the entry type.
     */
    public int getType() {
        return type;
    }

    /**
     * Sets the entry type.
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * Returns the entry amount.
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Sets the entry amount.
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }

    /**
     * Returns a String with all entry properties.
     */
    public String toString() {
        return "id: " + id + " date: " + date + " type: " + type + 
	    " amount: " + amount;
    }
}
