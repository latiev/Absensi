package id.latiev.absensi.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
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
    private Button buttonLogin, buttonSetupServer;

    // sharedpreferences
    private SharedPreferences preferences;
    public static SharedPreferences preferencesURLServer;
    public static String PREF_URL_SERVER = "PrefURL";
    public static String KEY_URL = "url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextUsername = (EditText) findViewById(R.id.et_al_username);
        editTextPassword = (EditText) findViewById(R.id.et_al_password);
        buttonLogin = (Button) findViewById(R.id.btn_al_login);
        buttonSetupServer = (Button) findViewById(R.id.btn_al_setup_server);

        preferencesURLServer = getSharedPreferences(PREF_URL_SERVER, MODE_PRIVATE);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (preferencesURLServer.contains(KEY_URL)){
                    if (preferencesURLServer.getString(KEY_URL, "").equalsIgnoreCase("")){
                        Toast.makeText(LoginActivity.this, "URL Server belum di setting", Toast.LENGTH_SHORT).show();
                    } else {
                        getDataUser(editTextUsername.getText().toString(), computeMD5Hash(editTextPassword.getText().toString()), preferencesURLServer.getString(KEY_URL, ""));
                    }
                }
            }
        });

        buttonSetupServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(LoginActivity.this);
                View viewDialogSetting = inflater.inflate(R.layout.dialog_setting, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setView(viewDialogSetting);

                final TextInputLayout tilSetting = (TextInputLayout) viewDialogSetting.findViewById(R.id.til_ds_setting);
                final EditText editTextSetting = (EditText) viewDialogSetting.findViewById(R.id.et_ds_setting);

                if (preferencesURLServer.contains(KEY_URL)) {
                    String isi = preferencesURLServer.getString(KEY_URL, "");
                    if (!isi.equalsIgnoreCase("")) {
                        editTextSetting.setText(isi);
                    }
                } else {
                    SharedPreferences.Editor editor = preferencesURLServer.edit();
                    editor.putString(KEY_URL, "");
                    editor.commit();
                }

                builder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (editTextSetting.getText().toString().equalsIgnoreCase("")) {
                            tilSetting.setError("URL Server harus diisi");
                            requestFocus(tilSetting);
                        } else {
                            tilSetting.setErrorEnabled(false);
                            SharedPreferences.Editor editor = preferencesURLServer.edit();
                            editor.putString(KEY_URL, editTextSetting.getText().toString());
                            editor.commit();

                            dialogInterface.dismiss();
                        }
                        Toast.makeText(LoginActivity.this, "Berhasil mengatur URL Server", Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void getDataUser(final String username, final String password, String server) {
        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        String url = "http://" + server + "/api/datasources/login";
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
