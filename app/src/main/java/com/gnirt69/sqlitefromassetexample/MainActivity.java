package com.gnirt69.sqlitefromassetexample;/**
 * Created by NgocTri on 11/7/2015.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gnirt69.sqlitefromassetexample.adapter.ListProductAdapter;
import com.gnirt69.sqlitefromassetexample.database.DatabaseHelper;
import com.gnirt69.sqlitefromassetexample.model.Product;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class MainActivity extends Activity {
    private ListView lvProduct;
    private ListProductAdapter adapter;
    private List<Product> mProductList;
    private DatabaseHelper mDBHelper;
    private Button btnAdd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        lvProduct = (ListView)findViewById(R.id.listview_product);
        mDBHelper = new DatabaseHelper(this);
        btnAdd = (Button)findViewById(R.id.btnAdd);
        //Check exists database
        File database = getApplicationContext().getDatabasePath(DatabaseHelper.DBNAME);
        if(false == database.exists()) {
            mDBHelper.getReadableDatabase();
            //Copy db
            if(copyDatabase(this)) {
                Toast.makeText(this, "Copy database succes", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Copy data error", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        //Get product list in db when db exists
        mProductList = mDBHelper.getListProduct();
        //Init adapter
        adapter = new ListProductAdapter(this, mProductList);
        //Set adapter for listview
        lvProduct.setAdapter(adapter);
        lvProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int idProduct = (int) view.getTag();
                Toast.makeText(getApplicationContext(), idProduct + "", Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), ((TextView) view.findViewById(R.id.tv_product_name)).getText().toString(), Toast.LENGTH_SHORT).show();

            }
        });
        registerForContextMenu(lvProduct
        );
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), AddOrEditActivity.class);
                i.putExtra("Action", "Add");
                startActivity(i);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mProductList = mDBHelper.getListProduct();
        //Init adapter
        adapter.updateList(mProductList);
    }

    private boolean copyDatabase(Context context) {
        try {

            InputStream inputStream = context.getAssets().open(DatabaseHelper.DBNAME);
            String outFileName = DatabaseHelper.DBLOCATION + DatabaseHelper.DBNAME;
            OutputStream outputStream = new FileOutputStream(outFileName);
            byte[]buff = new byte[1024];
            int length = 0;
            while ((length = inputStream.read(buff)) > 0) {
                outputStream.write(buff, 0, length);
            }
            outputStream.flush();
            outputStream.close();
            Log.w("MainActivity","DB copied");
            return true;
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();
        //Get id of item clicked
        // Retrieve the item that was clicked on
        View v = (View) lvProduct.getAdapter().getView(
                info.position, null, null);

        switch (item.getItemId()) {
            case R.id.menu_item_edit:

                Intent i = new Intent(getApplicationContext(), AddOrEditActivity.class);
                i.putExtra("Action", "Edit");
                i.putExtra("Id", (int)v.getTag()); //Id product saved to tag
                startActivity(i);
                break;
            case R.id.menu_item_del:
                if( mDBHelper.deleteProductById((int)v.getTag())){
                    Toast.makeText(getApplicationContext(),"Deleted", Toast.LENGTH_SHORT).show();
                    mProductList.remove(info.position);
                    adapter.updateList(mProductList);
                } else {
                    Toast.makeText(getApplicationContext(),"Delete failed", Toast.LENGTH_SHORT).show();
                }

                break;
        }
        return true;
    }
}
