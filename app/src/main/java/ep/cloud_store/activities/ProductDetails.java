package ep.cloud_store.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.CirclePageIndicator;
import com.synnapps.carouselview.ImageListener;

import ep.cloud_store.R;
import ep.cloud_store.objects.Product;

public class ProductDetails extends AppCompatActivity {

    final String imageUrlPath = "http://192.168.0.11:8888/uploads/";
    CarouselView carouselView;
    TextView productTitle;
    TextView price;
    TextView description;
    Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        // toolbar

        Intent intent = this.getIntent();
        product = (Product) intent.getSerializableExtra("Product");

        Toolbar toolbar = (Toolbar) findViewById(R.id.details_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        carouselView = (CarouselView) findViewById(R.id.carouselView);
        carouselView.setPageCount(product.getImages().size());
        carouselView.setImageListener(imageListener);

        description = (TextView) findViewById(R.id.description);
        productTitle = (TextView) findViewById(R.id.product_title);
        price = (TextView) findViewById(R.id.price);

        description.setText(product.getDescription());
        productTitle.setText(product.getTitle());
        String priceNum = Double.toString(product.getPrice())+ " $";
        price.setText(priceNum);

        System.out.println(product.getImages());

        CirclePageIndicator indicator = (CirclePageIndicator) carouselView.findViewById(R.id.indicator);

        if (indicator != null) {
            indicator.setVisibility(View.GONE);
        }

    }

    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            Glide.with(getApplicationContext()).load(product.getImages().get(position)).centerCrop().into(imageView);
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }
}
