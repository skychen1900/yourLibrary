package com.mycompany.newsservice.models;

/**
 * This class is an example of a JSF-independent business logic class.
 * It represents a Subscriber of a fictitious newsletter subscription
 * service.
 *
 * @author Hans Bergsten, Gefion Software <hans@gefionsoftware.com>
 * @version 1.0
 */
public class Subscriber {
    private String emailAddr;
    private String[] subscriptionIds;

    public String getEmailAddr() {
        return emailAddr;
    }

    public void setEmailAddr(String emailAddr) {
        this.emailAddr = emailAddr;
    }

    public String[] getSubscriptionIds() {
        return subscriptionIds;
    }

    public void setSubscriptionIds(String[] subscriptionIds) {
        this.subscriptionIds = subscriptionIds;
    }

    public void save() {
        StringBuffer subscriptions = new StringBuffer();
        if (subscriptionIds != null) {
            for (int i = 0; i < subscriptionIds.length; i++) {
                subscriptions.append(subscriptionIds[i]).append(" ");
            }
        }
        System.out.println("Subscriber Email Address: " + emailAddr +
                           "\nSubscriptions: " + subscriptions);
    }
}
