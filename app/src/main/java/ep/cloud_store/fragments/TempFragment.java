package ep.cloud_store.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

import cz.msebera.android.httpclient.Header;
import ep.cloud_store.R;
import ep.cloud_store.app_adapters.*;
import ep.cloud_store.objects.DataHolder;
import ep.cloud_store.objects.Product;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TempFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TempFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TempFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    final String apiUrlPath = "http://192.168.0.11:8888/";

    String[] imageUrls = {"http://ia.media-imdb.com/images/M/MV5BNjQ2NDA3MDcxMF5BMl5BanBnXkFtZTgwMjE5NTU0NzE@._V1_.jpg", "http://vignette1.wikia.nocookie.net/cookieclicker/images/b/b9/Chocolate_Chip_Cookies_-_kimberlykv.jpg/revision/latest?cb=20130925213259",
            "https://lh6.ggpht.com/0O-HefMHNwemt4gnJ4YfNq3xFaTBaiFcwIRiUpzDZ1KObI8ptEMgoHOSDiy_tumRWexz=w300", "https://upload.wikimedia.org/wikipedia/commons/7/71/Christmas_Cookies_Plateful.JPG"};

    private OnFragmentInteractionListener mListener;

    public TempFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TempFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TempFragment newInstance(String param1, String param2) {
        TempFragment fragment = new TempFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private ListAdapter mAdapter;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_temp, container, false);

        recyclerView = (RecyclerView) view.findViewById(
                R.id.fragment_list_rv);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    refreshItems();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


            public void refreshItems() throws InterruptedException {
                AsyncHttpClient client = new AsyncHttpClient();
                client.get(apiUrlPath+"/test.json", new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        List<Product> products = new ArrayList<Product>();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject product = response.getJSONObject(i);
                                ArrayList <String> imageUrls = new ArrayList<>();
                                String imageUrl = apiUrlPath+"uploads/"+product.getString("image");
                                imageUrls.add(imageUrl);
                                Product temp = new Product(product.getInt("id"),imageUrl,imageUrls,product.getDouble("price"),product.getString("name"),product.getString("description"));
                                products.add(i,temp);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        refreshProductList(products);
                        onItemsLoadComplete();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        System.out.println(responseString);
                    }
                });
            }

            public void onItemsLoadComplete() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        List<Product> products = DataHolder.getInstance().getProductList();

        mAdapter = new ListAdapter(products);
        recyclerView.setAdapter(mAdapter);

        return view;
    }


    public void refreshProductList(List<Product> products) {
        if (this.mAdapter != null) {
            mAdapter = new ListAdapter(products);
            recyclerView.setAdapter(mAdapter);
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}
