package ep.cloud_store.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import ep.cloud_store.R;
import ep.cloud_store.config.AppConfig;
import ep.cloud_store.objects.SessionManager;

public class SettingsActivity extends AppCompatActivity {

    private Button saveChangesBtn;

    private EditText username;
    private EditText email;
    private EditText password;
    private EditText newPassword;
    private EditText name;
    private EditText surname;
    private EditText streetAddress;
    private EditText streetNumber;
    private EditText city;
    private EditText postCode;
    private EditText country;
    private EditText phoneNumber;
    private ProgressDialog pDialog;
    JSONObject user;
    SessionManager session;
    AsyncHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        session = new SessionManager(getApplicationContext());

        username = (EditText) findViewById(R.id.username_settings);
        email = (EditText) findViewById(R.id.email_settings);
        password = (EditText) findViewById(R.id.current_password_settings);
        newPassword = (EditText) findViewById(R.id.new_password_settings);
        name = (EditText) findViewById(R.id.name_settings);
        surname = (EditText) findViewById(R.id.surname_settings);
        streetAddress = (EditText) findViewById(R.id.street_settings);
        streetNumber = (EditText) findViewById(R.id.street_number_settings);
        city = (EditText) findViewById(R.id.city_settings);
        postCode = (EditText) findViewById(R.id.postcode_settings);
        country = (EditText) findViewById(R.id.country_settings);
        phoneNumber = (EditText) findViewById(R.id.phoneNumber_settings);

        if (session.getUserInfo() != null) {
            try {
                user = new JSONObject(session.getUserInfo());
                System.out.println(user);
                getSupportActionBar().setTitle(user.getString("username"));
                username.setText(user.getString("username"), TextView.BufferType.EDITABLE);
                email.setText(user.getString("email"),TextView.BufferType.EDITABLE);
                name.setText(user.getString("first_name"));
                surname.setText(user.getString("last_name"));
                streetAddress.setText(user.getString("street"));
                streetNumber.setText(user.getString("street_number"));
                city.setText(user.getString("city"));
                postCode.setText(user.getString("postcode"));
                country.setText(user.getString("country"));
                phoneNumber.setText(user.getString("phone"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        saveChangesBtn = (Button) findViewById(R.id.btnSaveChanges);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        client = new AsyncHttpClient();

        saveChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String currentPassword = password.getText().toString().trim();
                Map<String,EditText> fieldMap = new HashMap<String, EditText>();
                fieldMap.put("username",username);
                fieldMap.put("email",email);
                fieldMap.put("password",password);
                fieldMap.put("new_password",newPassword);
                fieldMap.put("first_name",name);
                fieldMap.put("last_name",surname);
                fieldMap.put("street",streetAddress);
                fieldMap.put("street_number",streetNumber);
                fieldMap.put("city",city);
                fieldMap.put("postcode",postCode);
                fieldMap.put("country",country);
                fieldMap.put("phone",phoneNumber);

                if (currentPassword.isEmpty()) {
                    password.setError("Enter a password to change the settings.");
                    Toast.makeText(getApplicationContext(),"Please enter your password to save the changes",Toast.LENGTH_LONG).show();
                } else {
                    saveChangesToProfile(fieldMap);
                }

            }
        });

    }

    private void saveChangesToProfile(Map<String,EditText> map) {
        RequestParams params = new RequestParams();
        for (Map.Entry<String,EditText> entry:map.entrySet()) {
            String key = entry.getKey();
            EditText value = entry.getValue();
            if (key.equals("first_name") || key.equals("last_name") || key.equals("street") || key.equals("city") || key.equals("country")) {
                String string = value.getText().toString().trim();
                string = Character.toUpperCase(string.charAt(0)) + string.substring(1);
                params.put(key,string);
            } else {
                if (key.equals("new_password")) {
                    if (value.getText().toString().trim().isEmpty()) {
                        continue;
                    } else {
                        params.put(key,value.getText().toString().trim());
                    }
                } else {
                    params.put(key,value.getText().toString().trim());
                }
            }
        }
        System.out.println(params);

        client.post(this, AppConfig.changeUserSettings, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                System.out.println(response);
                String statusString = null;
                try {
                    statusString = response.getString("status");
                    if (statusString.equals("success")) {
                        if (response.getBoolean("password_updated")) {
                            Toast.makeText(getApplicationContext(),"Successfully updated password and account settings. Please log in again with the new credentials.",Toast.LENGTH_LONG).show();
                            session.setLogin(false);
                            session.setUser(null);
                            Intent i = new Intent(SettingsActivity.this,LoginActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(),"Successfully updated account settings",Toast.LENGTH_LONG).show();
                            session.setUser(response.getJSONObject("user").toString());
                            finish();
                        }
                    } else {
                        int errorNumber = response.getInt("error_number");
                        if (errorNumber == 4) {
                            password.setError("Wrong password!");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                if (pDialog.isShowing()) {
                    pDialog.hide();
                }
                Toast.makeText(getApplicationContext(),"Connection problems. Please check your internet connection or try again later.",Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }
}
