package edu.uga.cs.rommateshopping;

public class Item {

    private String name;
    private Integer quantity;
    private Double price;

    /**
     * Default constructor sets everything to empty or zero.
     */
    public Item() {
        this.name = "";
        this.quantity = 0;
        this.price = 10.0;
    }

    /**
     * Constructor that sets the appropriate fields for our item.
     *
     * @param name the name of the item we are adding
     * @param quantity the number of items
     * @param price the price of the item
     */
    public Item(String name, Integer quantity, Double price) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    /**
     * Returns the item's name
     *
     * @return item name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the item's name
     *
     * @param name the name of the item we are adding
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the item's quantity
     *
     * @return item quantity
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * Sets the item's quantity
     *
     * @param quantity the number of items
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * Returns the item's price
     *
     * @return item price
     */
    public Double getPrice() {
        return price;
    }

    /**
     * Sets the item's price
     *
     * @param price the price of the item
     */
    public void setPrice(Double price) {
        this.price = price;
    }
}
