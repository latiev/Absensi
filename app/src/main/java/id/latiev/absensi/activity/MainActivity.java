package id.latiev.absensi.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.latiev.absensi.R;
import id.latiev.absensi.helper.KegiatanTimelineAdapter;
import id.latiev.absensi.model.Kegiatan;
import id.latiev.absensi.network.AppController;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // set preferences
    private SharedPreferences preferencesUser;
    private SharedPreferences preferencesURLServer;
    public static SharedPreferences preferencesAbsensi;
    public static String PREF_ABSENSI = "absensiPreference";
    public static String KEY_ID = "id";

    // set variabel
    private List<Kegiatan> kegiatanList;
    private String urlServer;

    // set display
    private RecyclerView recyclerView;
    private FloatingActionsMenu fabMenu;
    private FloatingActionButton fabMasuk, fabPulang, fabAktifitas, fabPermintaan;
    private View viewDialog;
    private EditText editTextActions, editTextInformations;
    private TextInputLayout tilActions, tilInformations;

    // set adapter
    private KegiatanTimelineAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        preferencesUser = getSharedPreferences(SplashScreen.PREF_USER, MODE_PRIVATE);
        preferencesURLServer = getSharedPreferences(LoginActivity.PREF_URL_SERVER, MODE_PRIVATE);
        preferencesAbsensi = getSharedPreferences(PREF_ABSENSI, MODE_PRIVATE);

        urlServer = preferencesURLServer.getString(LoginActivity.KEY_URL, "");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initCollapsingToolbar();

        fabMenu = (FloatingActionsMenu) findViewById(R.id.fab_abm_menu);
        fabMasuk = (FloatingActionButton) findViewById(R.id.fab_abm_masuk);
        fabPulang = (FloatingActionButton) findViewById(R.id.fab_abm_pulang);
        fabAktifitas = (FloatingActionButton) findViewById(R.id.fab_abm_kegiatan);
        fabPermintaan = (FloatingActionButton) findViewById(R.id.fab_abm_permintaan);

        recyclerView = (RecyclerView) findViewById(R.id.rv_cm);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        kegiatanList = new ArrayList<>();

        adapter = new KegiatanTimelineAdapter(MainActivity.this, kegiatanList);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (fabMenu.isExpanded()) {
                    fabMenu.collapse();
                }
            }
        });

        fabMasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (preferencesAbsensi.contains(KEY_ID)) {
                    if (preferencesAbsensi.getString(KEY_ID, "").equalsIgnoreCase("")) {
                        postAbsenMasuk(preferencesUser.getString(SplashScreen.KEY_ID, ""));
                    } else {
                        Toast.makeText(MainActivity.this, "Tadi sudah absen masuk gan, enggak boleh lagi", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    postAbsenMasuk(preferencesUser.getString(SplashScreen.KEY_ID, ""));
                }
                fabMenu.collapse();
            }
        });

        fabPulang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (preferencesAbsensi.contains(KEY_ID)) {
                    if (preferencesAbsensi.getString(KEY_ID, "").equalsIgnoreCase("")) {
                        Toast.makeText(MainActivity.this, "Ente belum absen masuk bos", Toast.LENGTH_SHORT).show();
                    } else {
                        postAbsenPulang(preferencesAbsensi.getString(KEY_ID, ""), preferencesUser.getString(SplashScreen.KEY_ID, ""));
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Ente belum absen masuk bos", Toast.LENGTH_SHORT).show();
                }
                fabMenu.collapse();
            }
        });

        fabAktifitas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (preferencesAbsensi.getString(KEY_ID, "").equalsIgnoreCase("")) {
                    Toast.makeText(MainActivity.this, "Absen masuk dulu baru bisa input kegiatan", Toast.LENGTH_SHORT).show();
                    fabMenu.collapse();
                } else {
                    callDialog(fabAktifitas);
                }
            }
        });

        fabPermintaan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (preferencesAbsensi.getString(KEY_ID, "").equalsIgnoreCase("")) {
                    Toast.makeText(MainActivity.this, "Absen masuk dulu baru bisa input permintaan", Toast.LENGTH_SHORT).show();
                    fabMenu.collapse();
                } else {
                    callDialog(fabPermintaan);
                }
            }
        });
    }

    private void postAbsenMasuk(final String idUser) {
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        String url = "http://" + urlServer + "/api/datasources/presensi";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject datas = new JSONObject(response);
                    Toast.makeText(MainActivity.this, datas.getString("pesan"), Toast.LENGTH_SHORT).show();
                    JSONObject data = new JSONObject(datas.getString("data"));
                    SharedPreferences.Editor editor = preferencesAbsensi.edit();
                    editor.putString(KEY_ID, data.getString("id"));
                    editor.commit();

                    kegiatanList.add(new Kegiatan(splitTime(data.getString("masuk")), "Masuk kerja", "Di RSUD Temanggung", android.R.color.transparent, R.color.colorAbu));
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("Error catch ", e.toString());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error response ", error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", "");
                params.put("id_user", idUser);
                return params;
            }
        };

        queue.add(request);
    }

    private void postAbsenPulang(final String id, final String idUser) {
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        String url = "http://" + urlServer + "/api/datasources/presensi";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject datas = new JSONObject(response);
                    Toast.makeText(MainActivity.this, datas.getString("pesan"), Toast.LENGTH_SHORT).show();
                    JSONObject data = new JSONObject(datas.getString("data"));
                    SharedPreferences.Editor editor = preferencesAbsensi.edit();
                    editor.remove(KEY_ID);
                    editor.commit();

                    kegiatanList.add(new Kegiatan(splitTime(data.getString("pulang")), "Pulang Kerja", "Di RSUD Temanggung", R.color.colorAbu, android.R.color.transparent));
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("Error catch ", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error response ", error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", id);
                params.put("id_user", idUser);
                return params;
            }
        };

        queue.add(request);
    }

    private void postAktifitas(final String id, final String idUser, final String idPresensi, final String kegiatan, final String keterangan) {
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        String url = "http://" + urlServer + "/api/datasources/aktifitas";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject datas = new JSONObject(response);
                    Toast.makeText(MainActivity.this, datas.getString("pesan"), Toast.LENGTH_SHORT).show();
                    JSONObject data = datas.getJSONObject("data");
                    kegiatanList.add(new Kegiatan(splitTime(data.getString("waktu")), data.getString("kegiatan"), data.getString("keterangan"), R.color.colorAbu, R.color.colorAbu));
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("Error catch ", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error response ", error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", id);
                params.put("id_user", idUser);
                params.put("id_presensi", idPresensi);
                params.put("kegiatan", kegiatan);
                params.put("keterangan", keterangan);

                return params;
            }
        };
        queue.add(request);
    }

    private void postPermintaan(final String id, final String idUser, final String idPresensi, final String permintaan, final String keterangan) {
        String url = "http://" + urlServer + "/api/datasources/permintaan";
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject datas = new JSONObject(response);
                    Toast.makeText(MainActivity.this, datas.getString("pesan"), Toast.LENGTH_SHORT).show();
                    JSONObject data = datas.getJSONObject("data");
                    kegiatanList.add(new Kegiatan(splitTime(data.getString("waktu")), data.getString("permintaan"), data.getString("keterangan"), R.color.colorAbu, R.color.colorAbu));
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("Error catch ", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error response ", error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", id);
                params.put("id_user", idUser);
                params.put("id_presensi", idPresensi);
                params.put("permintaan", permintaan);
                params.put("keterangan", keterangan);

                return params;
            }
        };
        queue.add(request);
    }

    private void getAbsensi(final String id) {
        String url = "http://" + urlServer + "/api/datasources/presensi?id=" + id;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String datas = response.getString("data");
                    if (datas.equalsIgnoreCase("null")) {
                        Toast.makeText(MainActivity.this, "Anda belum absen", Toast.LENGTH_SHORT).show();
                    } else {
                        JSONObject data = new JSONObject(datas);
                        String masuk = data.getString("masuk");
                        String pulang = data.getString("pulang");

                        kegiatanList.add(new Kegiatan(splitTime(masuk), "Masuk kerja", "di RSUD Temanggung", android.R.color.transparent, R.color.colorAbu));
                        adapter.notifyDataSetChanged();

                        getAktifitas(id);
                        getPermintaan(id);
                        if (!pulang.equalsIgnoreCase("00:00:00")) {
                            kegiatanList.add(new Kegiatan(splitTime(pulang), "Pulang kerja", "di RSUD Temanggung", R.color.colorAbu, android.R.color.transparent));
                            adapter.notifyDataSetChanged();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("Log catch ", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Log error response ", error.toString());
            }
        });
        AppController.getInstance().addToRequestQueue(request, "Batal");
    }

    private void getAktifitas(String idPresensi) {
        String url = "http://" + urlServer + "/api/datasources/aktifitas_list_by_id_presensi?id_presensi=" + idPresensi;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray datas = response.getJSONArray("data");
                    if (datas.length() > 0) {
                        for (int i = 0; i < datas.length(); i++) {
                            JSONObject data = datas.getJSONObject(i);
                            String waktu = data.getString("waktu");
                            String kegiatan = data.getString("kegiatan");

                            kegiatanList.add(new Kegiatan(splitTime(waktu), kegiatan, "", R.color.colorAbu, R.color.colorAbu));
                        }
                        adapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("Log catch ", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Log error response ", error.toString());
            }
        });
        AppController.getInstance().addToRequestQueue(request, "Batal");
    }

    private void getPermintaan(String idPresensi) {
        String url = "http://" + urlServer + "/api/datasources/permintaan_list_by_id_presensi?id_presensi=" + idPresensi;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray datas = response.getJSONArray("data");
                    if (datas.length() > 0) {
                        for (int i = 0; i < datas.length(); i++) {
                            JSONObject data = datas.getJSONObject(i);
                            String waktu = data.getString("waktu");
                            String permintaan = data.getString("permintaan");

                            kegiatanList.add(new Kegiatan(splitTime(waktu), permintaan, "", R.color.colorAbu, R.color.colorAbu));
                        }
                        adapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("Log catch ", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error response ", error.toString());
            }
        });
        AppController.getInstance().addToRequestQueue(request, "Batal");
    }

    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.ctl_abm_collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.abl_abm_appbar);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle("Absensi");
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    private String splitTime(String time) {
        String[] parts = time.split(":");
        return parts[0] + ":" + parts[1];
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void callDialog(final FloatingActionButton fab) {
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        viewDialog = inflater.inflate(R.layout.dialog_action, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(viewDialog);

        editTextActions = (EditText) viewDialog.findViewById(R.id.et_da_actions);
        editTextInformations = (EditText) viewDialog.findViewById(R.id.et_da_informations);
        tilActions = (TextInputLayout) viewDialog.findViewById(R.id.til_da_actions);
        tilInformations = (TextInputLayout) viewDialog.findViewById(R.id.til_da_informations);

        if (fab == fabAktifitas) {
            tilActions.setHintEnabled(true);
            tilActions.setHintAnimationEnabled(true);
            tilActions.setHint("Kegiatan");

            tilInformations.setHintEnabled(true);
            tilInformations.setHintAnimationEnabled(true);
            tilInformations.setHint("Keterangan");
        } else if (fab == fabPermintaan) {
            tilActions.setHintEnabled(true);
            tilActions.setHintAnimationEnabled(true);
            tilActions.setHint("Permintaan");

            tilInformations.setHintEnabled(true);
            tilInformations.setHintAnimationEnabled(true);
            tilInformations.setHint("Keterangan");
        }

        builder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (fab == fabAktifitas) {
                    Toast.makeText(MainActivity.this, "Post kegiatan", Toast.LENGTH_SHORT).show();
                } else if (fab == fabPermintaan) {
                    Toast.makeText(MainActivity.this, "Post permintaan", Toast.LENGTH_SHORT).show();
                }
            }
        }).setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        Button tombol = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        tombol.setOnClickListener(new CustomListener(alertDialog, fab));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
            View viewDialogSetting = inflater.inflate(R.layout.dialog_setting, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setView(viewDialogSetting);

            final TextInputLayout tilSetting = (TextInputLayout) viewDialogSetting.findViewById(R.id.til_ds_setting);
            final EditText editTextSetting = (EditText) viewDialogSetting.findViewById(R.id.et_ds_setting);

            editTextSetting.setText(urlServer);

            builder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (editTextSetting.getText().toString().equalsIgnoreCase("")){
                        tilSetting.setError("URL Server harus diisi");
                        requestFocus(tilSetting);
                    } else {
                        tilSetting.setErrorEnabled(false);
                        SharedPreferences.Editor editor = preferencesURLServer.edit();
                        editor.putString(LoginActivity.KEY_URL, editTextSetting.getText().toString());
                        editor.commit();

                        dialog.dismiss();
                    }
                    Toast.makeText(MainActivity.this, "Berhasil mengatur URL Server", Toast.LENGTH_SHORT).show();
                }
            }).setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_kegiatan) {
            startActivity(new Intent(MainActivity.this, KegiatanActivity.class));
        } else if (id == R.id.nav_permintaan) {
            startActivity(new Intent(MainActivity.this, PermintaanActivity.class));
        } else if (id == R.id.nav_logout) {
            SharedPreferences.Editor editor = preferencesUser.edit();
            editor.remove(SplashScreen.KEY_ID);
            editor.commit();

            finish();

            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (preferencesAbsensi.contains(KEY_ID)) {
            if (!preferencesAbsensi.getString(KEY_ID, "").equalsIgnoreCase("")) {
                kegiatanList.clear();
                adapter.notifyDataSetChanged();
                getAbsensi(preferencesAbsensi.getString(KEY_ID, ""));
            }
        }
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    class CustomListener implements View.OnClickListener {

        private final Dialog dialog;
        private final FloatingActionButton floatingActionButton;

        public CustomListener(Dialog dialog, FloatingActionButton floatingActionButton) {
            this.dialog = dialog;
            this.floatingActionButton = floatingActionButton;
        }

        @Override
        public void onClick(View v) {
            if (editTextActions.getText().toString().equalsIgnoreCase("")) {
                if (floatingActionButton == fabAktifitas) {
                    tilActions.setError("Kegiatan harus diisi");
                    requestFocus(editTextActions);
                } else if (floatingActionButton == fabPermintaan) {
                    tilActions.setError("Permintaan harus diisi");
                    requestFocus(editTextActions);
                }
            } else {
                tilActions.setErrorEnabled(false);
                if (floatingActionButton == fabAktifitas) {
                    postAktifitas("", preferencesUser.getString(SplashScreen.KEY_ID, ""), preferencesAbsensi.getString(KEY_ID, ""), editTextActions.getText().toString(), editTextInformations.getText().toString());
                } else if (floatingActionButton == fabPermintaan) {
                    postPermintaan("", preferencesUser.getString(SplashScreen.KEY_ID, ""), preferencesAbsensi.getString(KEY_ID, ""), editTextActions.getText().toString(), editTextInformations.getText().toString());
                }
                if (fabMenu.isExpanded()) {
                    fabMenu.collapse();
                }
                dialog.dismiss();
            }
        }
    }
}
