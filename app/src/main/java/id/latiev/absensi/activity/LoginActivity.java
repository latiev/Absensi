package id.latiev.absensi.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import id.latiev.absensi.R;
import id.latiev.absensi.network.AppController;

public class LoginActivity extends AppCompatActivity {

    // set display
    private EditText editTextUsername, editTextPassword;
    private Button buttonLogin, buttonRegister;

    // sharedpreferences
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextUsername = (EditText) findViewById(R.id.et_al_username);
        editTextPassword = (EditText) findViewById(R.id.et_al_password);
        buttonLogin = (Button) findViewById(R.id.btn_al_login);
        buttonRegister = (Button) findViewById(R.id.btn_al_register);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDataUser(editTextUsername.getText().toString(), computeMD5Hash(editTextPassword.getText().toString()));
            }
        });
    }

    private void getDataUser(final String username, final String password) {
        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        String url = "http://10.0.2.2/absensi/api/datasources/login";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response ", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String datas = jsonObject.getString("data");
                    if (datas.equalsIgnoreCase("null")){
                        Toast.makeText(LoginActivity.this, "username dan password tidak sesuai", Toast.LENGTH_SHORT).show();
                    } else {
                        JSONObject data = new JSONObject(datas);
                        preferences = getSharedPreferences(SplashScreen.PREF_USER, MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(SplashScreen.KEY_ID, data.getString("id"));
                        editor.putString(SplashScreen.KEY_USERNAME, data.getString("username"));
                        editor.putString(SplashScreen.KEY_FULLNAME, data.getString("nama"));
                        editor.commit();

                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("Error catch ", e.toString());
                    Toast.makeText(LoginActivity.this, "Iki kodinganku sik salah gan", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error response ", error.toString());
                Toast.makeText(LoginActivity.this, "koneksine suloyo gan", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);

                Log.d("Params ", params.toString());
                return params;
            }
        };

        queue.add(request);
    }

    private String computeMD5Hash(String password) {
        String hash = "";
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(password.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuffer MD5Hash = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2) {
                    h = "0" + h;
                }
                MD5Hash.append(h);
            }
            hash = String.valueOf(MD5Hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hash;
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
