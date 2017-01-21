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

import cz.msebera.android.httpclient.Header;
import ep.cloud_store.R;
import ep.cloud_store.config.AppConfig;
import ep.cloud_store.objects.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private Button loginBtn;
    private Button linkToRegisterBtn;
    private Button linkToShopScreen;
    private EditText inputUsername;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    AsyncHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginBtn = (Button) findViewById(R.id.btnLogin);
        linkToRegisterBtn = (Button) findViewById(R.id.btnLinkToRegisterScreen);
        inputUsername = (EditText) findViewById(R.id.username);
        inputPassword = (EditText) findViewById(R.id.password);
        linkToShopScreen = (Button) findViewById(R.id.btnLinkToShopScreen);

        client = new AsyncHttpClient();
        session = new SessionManager(getApplicationContext());

        if (session.isLoggedIn()) {
            Intent i = new Intent(this,Home.class);
            startActivity(i);
            finish();
        }

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = inputUsername.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                if (username.isEmpty()) {
                    inputUsername.setError("Enter a username");
                }

                if (password.isEmpty()) {
                    inputPassword.setError("Enter a password");
                }

                if (!username.isEmpty() && !password.isEmpty()){
                    sendLogin(username,password);
                } else {
                    Toast.makeText(getApplicationContext(),"Please enter username and password!",Toast.LENGTH_LONG).show();
                }
            }
        });

        linkToRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(intent);
            }
        });

        linkToShopScreen.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Home.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void sendLogin(final String username, final String password) {
        RequestParams params = new RequestParams();
        params.put("username",username);
        params.put("password",password);

        pDialog.setMessage("Logging in");
        if (!pDialog.isShowing()) {
            pDialog.show();
        }
        client.post(this, AppConfig.loginUrl, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    System.out.println(response);
                try {
                    String statusString = response.getString("status");
                    if (pDialog.isShowing()) {
                        pDialog.hide();
                    }
                    if (statusString.equals("success")) {
                        session.setLogin(true);
                        JSONObject user = response.getJSONObject("user");
                        session.setUser(user.toString());
                        Intent intent = new Intent(LoginActivity.this,Home.class);
                        startActivity(intent);
                        finish();
                    } else {
                        int errorNumber = response.getInt("error_number");
                        if (errorNumber == 3) {
                            inputUsername.setError("Username does not exist");
                        } else if (errorNumber == 4) {
                            inputPassword.setError("Password is incorrect");
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
}
