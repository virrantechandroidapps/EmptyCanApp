package com.meshyog.emptycan.references;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.meshyog.emptycan.R;

import java.util.ArrayList;

/**
 * Created by Viswanathan on 12/10/16.
 */
public class FavouritesList extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favouriteslist);
        ListView listView=(ListView)findViewById(R.id.favouriteslist_list);
        FavouritesListAdapter adapter=new FavouritesListAdapter(this,getDatas());
        listView.setAdapter(adapter);
    }

    public ArrayList<String> getDatas(){
        ArrayList<String> list=new ArrayList<>();
        list.add("Favourites 1");
        list.add("Favourites 2");
        list.add("Favourites 3");
        list.add("Favourites 4");
        list.add("Favourites 5");
        list.add("Favourites 6");
        list.add("Favourites 7");
        return list;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_myProfile_list) {
            Intent intent = new Intent(this, Profile.class);
            startActivity(intent);
        } else if (id == R.id.action_orders_list) {
            Intent intent=new Intent(this,MyOrdersList.class);
            startActivity(intent);
        }else if (id == R.id.action_notification_list) {
            Intent intent=new Intent(this,NotificationList.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
