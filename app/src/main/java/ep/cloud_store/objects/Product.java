package ep.cloud_store.objects;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Klavdij on 19/01/2017.
 */

public class Product implements Serializable {

    private int id;
    private String mainImage;
    private ArrayList<String> images;
    private double price;
    private String title;
    private String description;

    //constructor

    public Product(int id, String mainImage, ArrayList<String> images, double price, String title, String description) {
        this.id = id;
        this.mainImage = mainImage;
        this.images = images;
        this.price = price;
        this.title = title;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getMainImage() {
        return mainImage;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public double getPrice() {
        return price;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
