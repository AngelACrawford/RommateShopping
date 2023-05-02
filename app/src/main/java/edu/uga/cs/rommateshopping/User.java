package edu.uga.cs.rommateshopping;

import android.content.ClipData;

import java.util.ArrayList;

public class User {

    public String firstName;
    public String lastName;
    public String email;
    public String room;
    public ArrayList<ClipData.Item> purchasedList;

    /**
     * Default constructor required for calls to DataSnapshot
     */
    public User() {
    }

    /**
     * Constructor that sets the appropriate fields for our item.
     *
     * @param firstName the first name of our user
     * @param lastName last name of our user
     * @param email email of our user
     * @param room room number of the user
     */
    public User(String firstName, String lastName, String email, String room) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.room = room;
        this.purchasedList = new ArrayList<ClipData.Item>();
    }

    /**
     * Sets the first name of the user
     *
     * @param firstName first name of user
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Sets the last name of the user
     *
     * @param lastName last name of user
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Sets the email of the user
     *
     * @param email email of user
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Sets the room number of the user
     *
     * @param room room number of user
     */
    public void setRoom(String room) {
        this.room = room;
    }

    /**
     *
     * @return the first name of the user
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     *
     * @return the last name of the user
     */
    public String getLastName() {
        return lastName;
    }

    /**
     *
     * @return the email of the user
     */
    public String getEmail() {
        return email;
    }

    /**
     *
     * @return the room number for the user
     */
    public String getRoom() {
        return room;
    }
}
