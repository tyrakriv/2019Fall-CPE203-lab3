import java.util.List;

public class View {

    private String sessionID;
    private String productID;
    private String price;

    public View(String sessionID, String productID, String price)
    {
        this.sessionID = sessionID;
        this.productID = productID;
        this.price = price;
    }

    public String getProduct(){
        return productID;
    }

    public String getPrice(){
        return price;
    }


}
