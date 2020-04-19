package com.mycompany.expense;

import java.io.IOException;
import java.util.Map;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * This class contains the single method for the user preferences
 * screens for the sample expense report aplication.
 *
 * @author Hans Bergsten, Gefion Software <hans@gefionsoftware.com>
 * @version 1.0
 */
public class UserHandler {

    /**
     * This method should save the updated user profile information
     * in a real applciation. For the sample application, it just
     * returns "success".
     */
    public String updateProfile() {
	return "success";
    }
}
