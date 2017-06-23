package us.julesandremi.seismic;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String defaultAddress = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/4.5_week.geojson";

    private List listSeism;
    private boolean magAscendSorted = false;
    private ListView mListView;
    private CustomAdapter seismAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton fab;
    private boolean timeAscendSorted = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Toujours en premier
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);

        mListView = (ListView) findViewById(R.id.seism_list);

        listSeism = new ArrayList<>();
        seismAdapter = new CustomAdapter(this, listSeism);
        mListView.setAdapter(seismAdapter);
        mListView.setOnItemClickListener(seismAdapter);

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

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.this.sortSeisms();
                Log.d("Tri", "Tri fait");
            }
        });





        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.sort_time) {
            this.sortSeisms("timeAscend");
        } else if (id == R.id.source_all) {
            this.defaultAddress = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_week.geojson";
            // Changer les titres intitulés ou symboles pour la lisibilité de la source utilisée
            this.asyncJson();

        } else if (id == R.id.source_medium) {
            this.defaultAddress = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.geojson";
            // Changer les titres intitulés ou symboles pour la lisibilité de la source utilisée
            this.asyncJson();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /*public void clickSeism(View view) {
        Intent afficherCarte = new Intent(MainActivity.this, FullscreenMapActivity.class);
        afficherCarte.putExtra("url", "https://earthquake.usgs.gov/earthquakes/eventpage/usd0008367#map");
        startActivity(afficherCarte);
    }*/

    /**
     * @// TODO: 20/06/17 Remplacer le symbole du bouton flottant par une flèche qui change de sens suivant le mode de tri
     */
    public void sortSeisms(){
        if (this.magAscendSorted) {
            this.sortSeisms("magDescend");
        } else {
            this.sortSeisms("magAscend");
        }
    }
    public void sortSeisms(String key){
        key = (key == null ? "croissant" : key);
        ArrayList <Seism> seismList = (ArrayList) this.seismAdapter.getListSeism();
        Collections.sort(seismList);

        switch (key){
            case "magAscend":
                if (!this.magAscendSorted) Collections.reverse(listSeism);
                this.magAscendSorted = true;
                break;
            case "magDescend":
                Collections.reverse(seismList);
                this.magAscendSorted = false;
                break;
            case "timeAscend":
                Collections.sort(listSeism, new Comparator<Seism>() {
                    @Override
                    public int compare(Seism o1, Seism o2) {
                        int result = o1.getTime().before(o2.getTime()) ? 1 : o1.getTime().after(o2.getTime()) ? -1 : 0 ;
                        return result;
                        }
                    });
                this.timeAscendSorted = true;
                this.magAscendSorted = false;
                break;
            case "timeDescend":
                if (!this.magAscendSorted)
                    Collections.sort(listSeism, new Comparator<Seism>() {
                        @Override
                        public int compare(Seism o1, Seism o2) {
                            int result = o1.getTime().before(o2.getTime()) ? 1 : o1.getTime().after(o2.getTime()) ? -1 : 0 ;
                            return result;
                        }
                    });
                Collections.reverse(listSeism);

                this.timeAscendSorted = false;
                this.magAscendSorted = false;
                break;

        }
        this.seismAdapter.setListSeism(seismList);
        this.seismAdapter.notifyDataSetChanged();
    }
}
