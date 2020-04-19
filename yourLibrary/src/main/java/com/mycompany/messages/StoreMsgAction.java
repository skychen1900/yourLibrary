package com.mycompany.messages;

/**
 * This class stores a new message in the Project Billboard
 * application.
 *
 * @author Hans Bergsten, Gefion software <hans@gefionsoftware.com>
 * @version 1.0
 */
public class StoreMsgAction {

    private NewsBean newsBean;
    private EmployeeBean validUser;
    private String category;
    private String msg;
    private String requestMethod;

    public void setNewsBean(NewsBean newsBean) {
	this.newsBean = newsBean;
    }

    public void setValidUser(EmployeeBean validUser) {
	this.validUser = validUser;
    }

    public String getCategory() {
	return category;
    }

    public void setCategory(String category) {
	this.category = category;
    }

    public String getMsg() {
	return msg;
    }

    public void setMsg(String msg) {
	this.msg = msg;
    }

    public void setRequestMethod(String requestMethod) {
	this.requestMethod = requestMethod;
    }

    /**
     * Creates a new NewsItemBean and sets its properties based
     * on the "category" and "msg" request parameters, plus
     * the firstName and lastName properties of the authenticated
     * user (an EmployeeBean accessible as the "validUser" session
     * attribute). The NewItemBean is then added to the NewsBean.
     * This action is only performed for POST request.
     *
     */
    public String store() {
        if ("POST".equals(requestMethod)) {
            NewsItemBean item = new NewsItemBean();
            item.setCategory(category);
            item.setMsg(msg);
            item.setPostedBy(validUser.getFirstName() + " " +
                validUser.getLastName());
            newsBean.setNewsItem(item);
        }
        return "success";
    }
}
