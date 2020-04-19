package com.mycompany.messages;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * This class contains information about an employee.
 *
 * @author Hans Bergsten, Gefion Software <hans@gefionsoftware.com>
 * @version 1.0
 */
public class EmployeeBean implements Serializable {
    // Properties
    private String dept;
    private Date empDate;
    private String emailAddr;
    private String firstName;
    private String lastName;
    private List projects;
    private String password;
    private String username;

    /**
     * Returns the dept property value.
     */
    public String getDept() {
        return dept;
    }

    /**
     * Sets the dept property value.
     */
    public void setDept(String dept) {
        this.dept = dept;
    }

    /**
     * Returns the empDate property value.
     */
    public Date getEmpDate() {
        return empDate;
    }

    /**
     * Sets the empDate property value.
     */
    public void setEmpDate(Date empDate) {
        this.empDate = empDate;
    }

    /**
     * Returns the emailAddr property value.
     */
    public String getEmailAddr() {
        return emailAddr;
    }

    /**
     * Sets the emailAddr property value.
     */
    public void setEmailAddr(String emailAddr) {
        this.emailAddr = emailAddr;
    }

    /**
     * Returns the firstName property value.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the firstName property value.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Returns the lastName property value.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the lastName property value.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Returns the projects property value.
     */
    public String[] getProjects() {
        if (projects == null) {
            return new String[0];
        }
        return (String[]) projects.toArray(new String[projects.size()]);
    }

    /**
     * Sets the projects property value.
     */
    public void setProjects(String[] projects) {
	if (projects == null) {
	    this.projects = null;
	}
	else {
	    this.projects = Arrays.asList(projects);
	}
    }
    
    /*
     * Adds one project to the projects property
     */
    public void setProject(String project) {
	if (projects == null) {
	    projects = new ArrayList();
	}
	projects.add(project);
    }

    /**
     * Returns the password property value.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password property value.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns the username property value.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username property value.
     */
    public void setUsername(String username) {
        this.username = username;
    }
}
