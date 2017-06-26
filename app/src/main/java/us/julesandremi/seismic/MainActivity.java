package us.julesandremi.seismic;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String defaultAddress = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/4.5_week.geojson";

    private List listSeism = null;
    private boolean magAscendSorted = false;
    private boolean timeAscendSorted = false;
    private boolean magSorted = false;
    private ListView mListView;
    private CustomAdapter seismAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton fab;
    private MenuItem magSort;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Toujours en premier
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);

        try{
            Intent intent =  getIntent();
            listSeism = (ArrayList<Seism>) intent.getSerializableExtra("list");
        } catch (Exception err){
            Log.d("Erreur", "Err");
        }

        mListView = (ListView) findViewById(R.id.seism_list);

        if (listSeism == null) listSeism = new ArrayList<>();
        seismAdapter = new CustomAdapter(this, listSeism);
        mListView.setAdapter(seismAdapter);
        mListView.setOnItemClickListener(seismAdapter);
        mListView.setOnItemLongClickListener(seismAdapter);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                MainActivity.this.asyncJson();
            }
        });

        // Configure the refreshing colors
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        this.asyncJson();

        ArrayList <Seism> seismList = (ArrayList) this.seismAdapter.getListSeism();
        Collections.sort(seismList);
        this.magSorted = true;


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent afficherCarteComplete = new Intent(MainActivity.this, CompleteMapActivity.class);
                afficherCarteComplete.putExtra("list", (Serializable) MainActivity.this.seismAdapter.getListSeism());
                MainActivity.this.startActivity(afficherCarteComplete);
            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void asyncJson() {
        this.asyncJson(this.defaultAddress);
    }

    private void asyncJson(String url){
        Snackbar.make(fab, R.string.Loading, Snackbar.LENGTH_LONG).show();
        new SeismAsyncTask().execute(this, this.listSeism, this.seismAdapter, this.swipeRefreshLayout, url);
        this.magAscendSorted = true;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        this.magSort = menu.getItem(0);


        magSort.setTitle(getResources().getString(R.string.action_sort_mag_descend));
        magSort.setTitleCondensed(getResources().getString(R.string.action_sort_mag_descend_min));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
         if (id == R.id.action_sort_mag) {
            if (this.magAscendSorted) {
                this.sortSeisms("magDescend", item);
            } else {
                this.sortSeisms("magAscend", item);
            }
        } else if (id == R.id.action_sort_time) {
            if (this.timeAscendSorted) {
                this.sortSeisms("timeDescend", item);
            } else {
                this.sortSeisms("timeAscend", item);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

         if (id == R.id.source_medium) {
            this.defaultAddress = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.geojson";
            // Changer les titres intitulés ou symboles pour la lisibilité de la source utilisée
            this.asyncJson();
        } else if (id == R.id.source_big) {
            this.defaultAddress = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/4.5_week.geojson";
            this.asyncJson();
        } else if (id == R.id.source_significant){
            this.defaultAddress = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/significant_week.geojson";
            this.asyncJson();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void sortSeisms(String key, MenuItem item){
        ArrayList <Seism> seismList = (ArrayList) this.seismAdapter.getListSeism();


        switch (key){
            case "magAscend":
                if (!(this.magSorted && this.magAscendSorted)) {
                    Collections.sort(seismList);
                    item.setTitle(getResources().getString(R.string.action_sort_mag_descend));
                    item.setTitleCondensed(getResources().getString(R.string.action_sort_mag_descend_min));
                    this.magAscendSorted = true;
                    this.magSorted = true;
                }
                break;
            case "magDescend":
                if (! this.magAscendSorted) Collections.sort(seismList);
                Collections.reverse(seismList);
                item.setTitle(getResources().getString(R.string.action_sort_mag));
                item.setTitleCondensed(getResources().getString(R.string.action_sort_mag_min));
                this.magAscendSorted = false;
                this.magSorted = true;
                break;
            case "timeAscend":
                if (!(!this.magSorted && this.timeAscendSorted)) {
                    Collections.sort(seismList, new Comparator<Seism>() {
                        @Override
                        public int compare(Seism o1, Seism o2) {
                            int result = o1.getTime().before(o2.getTime()) ? 1 : o1.getTime().after(o2.getTime()) ? -1 : 0;
                            return result;
                        }
                    });
                    this.timeAscendSorted = true;
                    this.magSorted = false;
                    item.setTitle(getResources().getString(R.string.action_sort_time_desc));
                    item.setTitleCondensed(getResources().getString(R.string.action_sort_time_desc_min));
                }
                break;
            case "timeDescend":
                if (!this.timeAscendSorted) {
                    Collections.sort(seismList, new Comparator<Seism>() {
                        @Override
                        public int compare(Seism o1, Seism o2) {
                            int result = o1.getTime().before(o2.getTime()) ? 1 : o1.getTime().after(o2.getTime()) ? -1 : 0 ;
                            return result;
                        }
                    });
                }
                Collections.reverse(seismList);

                this.timeAscendSorted = false;
                this.magSorted = false;
                item.setTitle(getResources().getString(R.string.action_sort_time));
                item.setTitleCondensed(getResources().getString(R.string.action_sort_time_min));
                break;

        }
        this.seismAdapter.setListSeism(seismList);
        this.seismAdapter.notifyDataSetChanged();
    }
}
