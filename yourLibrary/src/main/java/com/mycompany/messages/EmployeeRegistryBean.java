package com.mycompany.messages;

import java.io.Serializable;
import java.util.Map;

import java.util.Map.Entry;
import java.util.Iterator;

/**
 * This class is an example of an application specific interface
 * to a data source (faked here, but it could be a database). It 
 * contains methods for authenticating a user, and retrieving and
 * updating user information.
 *
 * @author Hans Bergsten, Gefion software <hans@gefionsoftware.com>
 * @version 1.0
 */
public class EmployeeRegistryBean implements Serializable {
    private Map dataSource;

    /**
     * Sets the dataSource property value.
     */
    public void setDataSource(Map dataSource) {
	if (dataSource != null) {
	    Iterator i = dataSource.entrySet().iterator();
	    while (i.hasNext()) {
		Map.Entry me = (Map.Entry) i.next();
	    }
	}
	
        this.dataSource = dataSource;
    }
    
    /**
     * Returns an EmployeeBean if the specified user name and password
     * match an employee in the database, otherwise null.
     */
    public EmployeeBean authenticate(String username, String password) {
        EmployeeBean empInfo = getEmployee(username);
        if (empInfo != null && empInfo.getPassword().equals(password)) {
            return empInfo;
        }
        return null;
    }

    /**
     * Returns an EmployeeBean initialized with the information
     * found in the database for the specified employee, or null if
     * not found.
     */
    public EmployeeBean getEmployee(String username) {

	return (EmployeeBean) dataSource.get(username);        
    }

    /**
     * Inserts the information about the specified employee, or 
     * updates the information if it's already defined.
     */
    public void saveEmployee(EmployeeBean empInfo) {
	dataSource.put(empInfo.getUsername(), empInfo);
    }    
}
