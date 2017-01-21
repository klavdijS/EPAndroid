package ep.cloud_store.app_adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import ep.cloud_store.R;
import ep.cloud_store.activities.ProductDetails;
import ep.cloud_store.objects.Product;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.MyViewHolder> {

    List<Product> products;

    public ListAdapter(List<Product> products) {
        this.products = products;
    }

    public void updateProducts(List <Product> products) {
        if (this.products != null) {
            this.products.clear();
            this.products.addAll(products);
            this.notifyDataSetChanged();
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item,
                viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder myViewHolder, final int i) {

        final Product product = products.get(i);
        Glide.with(myViewHolder.mView.getContext()).load(product.getMainImage()).centerCrop().into(myViewHolder.productImage);
        myViewHolder.productTitle.setText(product.getTitle());
        String priceString = Double.toString(product.getPrice())+" $";
        myViewHolder.productPrice.setText(priceString);

        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(v);
                System.out.println("CLicking item");
                Context context = v.getContext();
                Intent intent = new Intent(context, ProductDetails.class);
                intent.putExtra("Product", product);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return products == null ? 0 : products.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        View mView;
        ImageView productImage;
        TextView productTitle;
        TextView productPrice;


        public MyViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            productImage = (ImageView) itemView.findViewById(R.id.thumbnail);
            productTitle = (TextView) itemView.findViewById(R.id.title);
            productPrice = (TextView) itemView.findViewById(R.id.price);
        }

    }

}
