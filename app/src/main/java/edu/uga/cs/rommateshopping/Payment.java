package edu.uga.cs.rommateshopping;

import java.text.DecimalFormat;

public class Payment {
    String person;
    String payment;

    //formats our decimal numbers into an easier to read format.
    DecimalFormat df = new DecimalFormat("###.##");

    /**
     * Default constructor sets everything to empty or zero.
     */
    public Payment() {
        this.person = "" ;
        this.payment = df.format(0.00);
    }

    /**
     * Constructor for setting the attributes to specific values
     *
     * @param person the name of the person a user owes money to
     * @param payment the amount of the payment owed
     */
    public Payment(String person, Double payment) {
        this.person = person;
        this.payment = df.format(payment);
    }

    /**
     * Returns the name of the person payment owed to
     *
     * @return the name of the person a payment is owed to
     */
    public String getPerson() {
        return person;
    }

    /**
     * Sets the person
     *
     * @param person name of person payment is owed
     */
    public void setPerson(String person) {
        this.person = person;
    }

    /**
     * Returns the payment amount
     *
     * @return the payment amount
     */
    public String getPayment() {
        return payment;
    }

    /**
     * Sets payment amount
     *
     * @param payment price of payment
     */
    public void setPayment(Double payment) {
        this.payment = df.format(payment);
    }
}
