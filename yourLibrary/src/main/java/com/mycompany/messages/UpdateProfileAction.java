package com.mycompany.messages;

/**
 * This class updates a user profile in the Project Billboard
 * application.
 *
 * @author Hans Bergsten, Gefion Software <hans@gefionsoftware.com>
 * @version 1.0
 */
public class UpdateProfileAction {

    private EmployeeRegistryBean empReg;
    private EmployeeBean validUser;
    private String[] projects;
    private String requestMethod;

    public EmployeeRegistryBean getRegistry() {
	return empReg;
    }

    public void setRegistry(EmployeeRegistryBean empReg) {
	this.empReg = empReg;
    }

    public EmployeeBean getValidUser() {
	return validUser;
    }

    public void setValidUser(EmployeeBean validUser) {
	this.validUser = validUser;
    }

    public String[] getProjects() {
	return validUser.getProjects();
    }

    public void setProjects(String[] projects) {
	this.projects = projects;
    }

    public String getRequestMethod() {
	return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
	this.requestMethod = requestMethod;
    }

    /**
     * Updates the projects property of an authenticated user,
     * represented by the "validUser" session attribute, using
     * the EmployeeRegistryBean. This action is only performed
     * for POST requests.
     */
    public String updateProfile() {
        if ("POST".equals(requestMethod)) {
            if (projects == null) {
                projects = new String[0];
            }
            validUser.setProjects(projects);
	    empReg.saveEmployee(validUser);
        }
        return "success";
    }
}
