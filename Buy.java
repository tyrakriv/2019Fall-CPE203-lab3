public class Buy {

    private String sessionID;
    private String productID;
    private String price;
    private String quantity;

    public Buy(String sessionID, String productID, String price, String quantity)
    {
        this.sessionID = sessionID;
        this.productID = productID;
        this.price = price;
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public String getProductID() {
        return productID;
    }
}
