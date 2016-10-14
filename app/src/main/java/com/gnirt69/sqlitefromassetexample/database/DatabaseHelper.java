package com.gnirt69.sqlitefromassetexample.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.gnirt69.sqlitefromassetexample.model.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NgocTri on 11/7/2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DBNAME = "sample.db";
    public static final String DBLOCATION = "/data/data/com.gnirt69.sqlitefromassetexample/databases/";
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public DatabaseHelper(Context context) {
        super(context, DBNAME, null, 1);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void openDatabase() {
        String dbPath = mContext.getDatabasePath(DBNAME).getPath();
        if (mDatabase != null && mDatabase.isOpen()) {
            return;
        }
        mDatabase = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public void closeDatabase() {
        if (mDatabase != null) {
            mDatabase.close();
        }
    }

    public List<Product> getListProduct() {
        Product product = null;
        List<Product> productList = new ArrayList<>();
        openDatabase();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM PRODUCT", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            product = new Product(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getString(3));
            productList.add(product);
            cursor.moveToNext();
        }
        cursor.close();
        closeDatabase();
        return productList;
    }

    public Product getProductById(int id) {
        Product product = null;
        openDatabase();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM PRODUCT WHERE ID = ?", new String[]{String.valueOf(id)});
        cursor.moveToFirst();
        product = new Product(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getString(3));
        //Only 1 resul
        cursor.close();
        closeDatabase();
        return product;
    }
    public long updateProduct(Product product) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", product.getName());
        contentValues.put("PRICE", product.getPrice());
        contentValues.put("DESCRIPTION", product.getDescription());
        String[] whereArgs = {Integer.toString(product.getId())};
        openDatabase();
        long returnValue = mDatabase.update("PRODUCT",contentValues, "ID=?", whereArgs);
        closeDatabase();
        return returnValue;
    }

    public long addProduct(Product product) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID", product.getId());
        contentValues.put("NAME", product.getName());
        contentValues.put("PRICE", product.getPrice());
        contentValues.put("DESCRIPTION", product.getDescription());
        openDatabase();
        long returnValue = mDatabase.insert("PRODUCT", null, contentValues);
        closeDatabase();
        return returnValue;
    }
    public boolean deleteProductById(int id) {
        openDatabase();
        int result = mDatabase.delete("PRODUCT",  "ID =?", new String[]{String.valueOf(id)});
        closeDatabase();
        return result !=0;
    }
}
