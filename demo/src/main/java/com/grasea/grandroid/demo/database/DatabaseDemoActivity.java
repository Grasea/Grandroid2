package com.grasea.grandroid.demo.database;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.grasea.grandroid.demo.R;
import com.grasea.database.FaceData;
import com.grasea.database.GenericHelper;

import java.util.ArrayList;

public class DatabaseDemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_demo);
        GenericHelper<Book> helper = new GenericHelper<Book>(new FaceData(this, "Grandroid2DB"), Book.class, true);
        Book book = new Book();
        book.setName("Android");
        book.setPrice(200);
        book.setSaleout(false);
        //Insert
        boolean inserted = helper.insert(book);

        //Select Books
        ArrayList<Book> select = helper.select("Where name ='Android'");

        //Select single Book
        //book1 == book2
        Book book1 = helper.selectSingle("Where name ='Android'");
        Book book2 = helper.selectSingle(book.get_id());

        //Update
        book.setPrice(300);
        boolean updated = helper.update(book);

        //Delete
        helper.delete(book.get_id());

    }
}
