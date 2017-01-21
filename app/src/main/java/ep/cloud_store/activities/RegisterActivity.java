package ep.cloud_store.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class RegisterActivity extends AppCompatActivity {

    private Button registerBtn;
    private Button linkToShopScreen;
    private Button linkToLoginScreen;

    private EditText username;
    private EditText email;
    private EditText password;
    private EditText name;
    private EditText surname;
    private EditText streetAddress;
    private EditText streetNumber;
    private EditText city;
    private EditText postCode;
    private EditText country;
    private EditText phoneNumber;
    private ProgressDialog pDialog;
    AsyncHttpClient client;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = (EditText) findViewById(R.id.username_register);
        email = (EditText) findViewById(R.id.email_register);
        password = (EditText) findViewById(R.id.password_register);
        name = (EditText) findViewById(R.id.name);
        surname = (EditText) findViewById(R.id.surname);
        streetAddress = (EditText) findViewById(R.id.street);
        streetNumber = (EditText) findViewById(R.id.street_number);
        city = (EditText) findViewById(R.id.city);
        postCode = (EditText) findViewById(R.id.postcode);
        country = (EditText) findViewById(R.id.country);
        phoneNumber = (EditText) findViewById(R.id.phoneNumber);

        linkToLoginScreen = (Button) findViewById(R.id.btnLinkToLoginScreen);
        linkToShopScreen = (Button) findViewById(R.id.btnLinkToShopScreen_register);
        registerBtn = (Button) findViewById(R.id.btnRegister);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        client = new AsyncHttpClient();

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: HANDLE REGISTRATION
                Map<String,EditText> fieldMap = new HashMap<String, EditText>();
                fieldMap.put("username",username);
                fieldMap.put("email",email);
                fieldMap.put("password",password);
                fieldMap.put("first_name",name);
                fieldMap.put("last_name",surname);
                fieldMap.put("street",streetAddress);
                fieldMap.put("street_number",streetNumber);
                fieldMap.put("city",city);
                fieldMap.put("postcode",postCode);
                fieldMap.put("country",country);
                fieldMap.put("phone",phoneNumber);

                if(validateInputFields(fieldMap)) {
                    sendRegistration(fieldMap);
                } else {
                    Toast.makeText(getApplicationContext(),"Please fill all the required fields",Toast.LENGTH_SHORT).show();
                }
            }
        });

        linkToShopScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Home.class);
                startActivity(intent);
                finish();
            }
        });

        linkToLoginScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void sendRegistration(Map<String,EditText> map) {
        RequestParams params = new RequestParams();
        for (Map.Entry<String,EditText> entry:map.entrySet()) {
            String key = entry.getKey();
            EditText value = entry.getValue();
            if (key.equals("first_name") || key.equals("last_name") || key.equals("street") || key.equals("city") || key.equals("country")) {
                String string = value.getText().toString().trim();
                string = Character.toUpperCase(string.charAt(0)) + string.substring(1);
                params.put(key,string);
            } else {
                params.put(key,value.getText().toString().trim());
            }
        }

        client.post(this, AppConfig.registrationUrl, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                System.out.println(response);
                String statusString = null;
                try {
                    statusString = response.getString("status");
                    if (statusString.equals("success")) {
                        Toast.makeText(getApplicationContext(),"Successfully created account! You can now login",Toast.LENGTH_LONG).show();
                        Intent i = new Intent(RegisterActivity.this,LoginActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        int errorNumber = response.getInt("error_number");
                        if (errorNumber == 4) {
                            username.setError("Username already exists! Please choose a different one");
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

    public boolean validateInputFields(Map<String,EditText> map) {
        boolean isValid = true;
        for (Map.Entry<String,EditText> entry:map.entrySet()) {
            String temp = entry.getKey();
            EditText value = entry.getValue();
            if (value.getText().toString().trim().isEmpty()) {
                isValid = false;
                value.setError("This field is required");
            }
        }
        return isValid;
    }
}
