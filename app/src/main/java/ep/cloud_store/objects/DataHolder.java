package ep.cloud_store.objects;

import java.util.List;

/**
 * Created by Klavdij on 19/01/2017.
 */

public class DataHolder {
    private static DataHolder instance = new DataHolder();

    public static DataHolder getInstance() {
        return instance;
    }

    private List<Product> productList;
    private int selectedProduct;

    private DataHolder() {

    }

    public void setProductList(List <Product> productList) {
        this.productList = productList;
    }

    public List <Product> getProductList() {
        return this.productList;
    }

    public void setSelectedProduct(int selectedProduct) {
        this.selectedProduct = selectedProduct;
    }

    public int getSelectedProduct() {
        return this.selectedProduct;
    }
}
