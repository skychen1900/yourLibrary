package com.mycompany.expense;

/**
 * This class holds the user preferences that can be set
 * for the sample expense report application.
 *
 * @author Hans Bergsten, Gefion Software <hans@gefionsoftware.com>
 * @version 1.0
 */
public class UserProfile {
    private String firstName;
    private String lastName;
    private String street;
    private String city;
    private String state;
    private String zip;
    private String locale;
    private String fontStyle;
    private String fontSize;

    /**
     * Returns the first name.
     */
    public String getFirstName() {
	return firstName;
    }

    /**
     * Sets the first name.
     */
    public void setFirstName(String firstName) {
	this.firstName = firstName;
    }

    /**
     * Returns the last name.
     */
    public String getLastName() {
	return lastName;
    }

    /**
     * Sets the last name.
     */
    public void setLastName(String lastName) {
	this.lastName = lastName;
    }

    /**
     * Returns the street address.
     */
    public String getStreet() {
	return street;
    }

    /**
     * Sets the street address.
     */
    public void setStreet(String street) {
	this.street = street;
    }

    /**
     * Returns the city name.
     */
    public String getCity() {
	return city;
    }

    /**
     * Sets the city name.
     */
    public void setCity(String city) {
	this.city = city;
    }

    /**
     * Returns the state name.
     */
    public String getState() {
	return state;
    }

    /**
     * Sets the state name.
     */
    public void setState(String state) {
	this.state = state;
    }

    /**
     * Returns the postal code (e.g., ZIP code).
     */
    public String getZip() {
	return zip;
    }

    /**
     * Sets the postal code (e.g., ZIP code).
     */
    public void setZip(String zip) {
	this.zip = zip;
    }

    /**
     * Returns the preferred locale, or "en" if none is set.
     */
    public String getLocale() {
	if (locale == null) {
	    locale = "en";
	}
	return locale;
    }

    /**
     * Sets the preferred locale.
     */
    public void setLocale(String locale) {
	this.locale = locale;
    }

    /**
     * Returns the preferred font style.
     */
    public String getFontStyle() {
	return fontStyle;
    }

    /**
     * Sets the preferred font style.
     */
    public void setFontStyle(String fontStyle) {
	this.fontStyle = fontStyle;
    }

    /**
     * Returns the preferred font size.
     */
    public String getFontSize() {
	return fontSize;
    }

    /**
     * Sets the preferred font size.
     */
    public void setFontSize(String fontSize) {
	this.fontSize = fontSize;
    }
}
