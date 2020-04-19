package com.mycompany.newsservice.handlers;

import com.mycompany.newsservice.models.Subscriber;

/**
 * This class is an example of the type of class that acts as an adaptor
 * for JSF-independent business logic classes (such as Subscriber)
 * to make them accessible to JSF components. It contains a single
 * JSF action method for calling the save() method on a Subscriber instance.
 * A real class of this type typically has a lot more methods.
 *
 * @author Hans Bergsten, Gefion Software <hans@gefionsoftware.com>
 * @version 1.0
 */
public class SubscriberHandler {
    private Subscriber subscriber;

    public void setSubscriber(Subscriber subscriber) {
        this.subscriber = subscriber;
    }

    public String saveSubscriber() {
        subscriber.save();
        return "success";
    }
}
