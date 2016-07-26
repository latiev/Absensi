package id.latiev.absensi.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.net.IDN;
import java.util.ArrayList;
import java.util.List;

import id.latiev.absensi.model.Presensi;

/**
 * Created by Latiev on 7/22/2016.
 */
public class AbsensiDB extends SQLiteOpenHelper {

    // Logcat TAG
    private static final String LOG = "AbsensiDB";

    // Database version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "absensi.sqlite";

    // Table Name
    private static final String TABLE_PRESENSI = "presensi";

    // TABLE_PRESENSI column name
    private static final String KEY_ID_PRESENSI = "id_presensi";
    private static final String KEY_TANGGAL_PRESENSI = "tanggal";
    private static final String KEY_ID_USER_PRESENSI = "id_user";
    private static final String KEY_MASUK_PRESENSI = "masuk";
    private static final String KEY_PULANG_PRESENSI = "pulang";

    // TABLE_PRESENSI create statements
    private static final String CREATE_TABLE_PRESENSI = "CREATE TABLE " + TABLE_PRESENSI
            + "(" + KEY_ID_PRESENSI + " TEXT PRIMARY KEY, " + KEY_TANGGAL_PRESENSI + " TEXT, "
            + KEY_ID_USER_PRESENSI + " TEXT, " + KEY_MASUK_PRESENSI + " TEXT, " + KEY_PULANG_PRESENSI + " TEXT)";

    public AbsensiDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PRESENSI);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on uppgrade drop older tables
        db.execSQL("DROP TABLE IF EXIST " + TABLE_PRESENSI);

        // create new table
        onCreate(db);
    }

    /**
     * TABLE_PRESENSI Method
     */

    // create presensi
    public void createPresensi(Presensi presensi){
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID_PRESENSI, presensi.getId());
        values.put(KEY_TANGGAL_PRESENSI, presensi.getTanggal());
        values.put(KEY_ID_USER_PRESENSI, presensi.getIdUser());
        values.put(KEY_MASUK_PRESENSI, presensi.getMasuk());
        values.put(KEY_PULANG_PRESENSI, presensi.getPulang());

        database.insert(TABLE_PRESENSI, null, values);
    }

    // get presensi by id
    public Presensi getPresensiByID(String id){
        SQLiteDatabase database = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_PRESENSI + " WHERE " + KEY_ID_PRESENSI + " = " + "'" + id + "'";
        Log.e(LOG + " getPresensi ", query);
        Cursor cursor = database.rawQuery(query, null);
        if (cursor != null){
            cursor.moveToFirst();
        }

        Presensi presensi = new Presensi();
        presensi.setId(cursor.getString(cursor.getColumnIndex(KEY_ID_PRESENSI)));
        presensi.setTanggal(cursor.getString(cursor.getColumnIndex(KEY_TANGGAL_PRESENSI)));
        presensi.setIdUser(cursor.getString(cursor.getColumnIndex(KEY_ID_USER_PRESENSI)));
        presensi.setMasuk(cursor.getString(cursor.getColumnIndex(KEY_MASUK_PRESENSI)));
        presensi.setPulang(cursor.getString(cursor.getColumnIndex(KEY_PULANG_PRESENSI)));

        return presensi;
    }

    public List<Presensi> getAllPresensi(){
        List<Presensi> presensiList = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_PRESENSI;
        Log.e(LOG + " getAllPrsens ", query);

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(query, null);
        if (cursor.moveToFirst()){
            do {
                Presensi presensi = new Presensi();
                presensi.setId(cursor.getString(cursor.getColumnIndex(KEY_ID_PRESENSI)));
                presensi.setTanggal(cursor.getString(cursor.getColumnIndex(KEY_TANGGAL_PRESENSI)));
                presensi.setIdUser(cursor.getString(cursor.getColumnIndex(KEY_ID_USER_PRESENSI)));
                presensi.setMasuk(cursor.getString(cursor.getColumnIndex(KEY_MASUK_PRESENSI)));
                presensi.setPulang(cursor.getString(cursor.getColumnIndex(KEY_PULANG_PRESENSI)));

                presensiList.add(presensi);
            } while (cursor.moveToNext());
        }

        return presensiList;
    }

    public int updatePresensi(Presensi presensi){
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TANGGAL_PRESENSI, presensi.getTanggal());
        values.put(KEY_ID_USER_PRESENSI, presensi.getIdUser());
        values.put(KEY_MASUK_PRESENSI, presensi.getMasuk());
        values.put(KEY_PULANG_PRESENSI, presensi.getPulang());

        return database.update(TABLE_PRESENSI, values, KEY_ID_PRESENSI + "=?", new String[]{presensi.getId()});
    }

    public void deletePresensi(String id){
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_PRESENSI, KEY_ID_PRESENSI + "=?", new String[]{id});
    }
}
