package ep.cloud_store.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import ep.cloud_store.R;
import ep.cloud_store.config.AppConfig;
import ep.cloud_store.fragments.TempFragment;
import ep.cloud_store.objects.DataHolder;
import ep.cloud_store.objects.Product;
import ep.cloud_store.objects.SessionManager;

public class Home extends AppCompatActivity implements TempFragment.OnFragmentInteractionListener {

    Toolbar toolbar;
    List<Product> products;
    SessionManager session;
    public static final int MENU_LOGIN = Menu.FIRST;
    public static final int MENU_REGISTER = Menu.FIRST + 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = (Toolbar) findViewById(R.id.details_toolbar);
        setSupportActionBar(toolbar);
        session = new SessionManager(getApplicationContext());

        final TempFragment tempFragment = new TempFragment();
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(AppConfig.apiUrl, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                List<Product> products = new ArrayList<Product>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject product = response.getJSONObject(i);
                        JSONArray imagesUrl = product.getJSONArray("image");
                        String [] imageUrls = new String[imagesUrl.length()];
                        for (int j = 0; j < imagesUrl.length(); j++) {
                            String imageUrl = AppConfig.baseUrl+imagesUrl.getString(j);
                            imageUrls[j] = imageUrl;
                        }

                        Product temp = new Product(product.getInt("id"),imageUrls[1],imageUrls,product.getDouble("price"),product.getString("name"),product.getString("description"));
                        products.add(i,temp);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                DataHolder.getInstance().setProductList(products);
                tempFragment.refreshProductList(products);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                System.out.println(responseString);
            }
        });

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        if (viewPager != null) {
            Adapter adapter = new Adapter(getSupportFragmentManager());
            adapter.addFragment(tempFragment, "Temp");
            viewPager.setAdapter(adapter);
        }

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (session.isLoggedIn()) {
            getMenuInflater().inflate(R.menu.menu_main,menu);
        } else {
            menu.add(Menu.NONE,MENU_LOGIN, Menu.NONE,"Login");
            menu.add(Menu.NONE,MENU_REGISTER, Menu.NONE,"Register");
        }
        return true;
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement

        switch (item.getItemId()) {
            case R.id.settings:
                Intent s = new Intent(Home.this,SettingsActivity.class);
                startActivity(s);
                return true;
            case R.id.log_out:
                session.setLogin(false);
                session.setUser(null);
                Intent l = new Intent(Home.this,LoginActivity.class);
                startActivity(l);
                finish();
                return true;
            case MENU_LOGIN:
                Intent i = new Intent(Home.this,LoginActivity.class);
                startActivity(i);
                return true;
            case MENU_REGISTER:
                Intent r = new Intent(Home.this,RegisterActivity.class);
                startActivity(r);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        session.setLastActivity(getClass().getName());
    }
}
