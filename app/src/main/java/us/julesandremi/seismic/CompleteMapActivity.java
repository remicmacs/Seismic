package us.julesandremi.seismic;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class CompleteMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    //private String defaultAddress = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/4.5_week.geojson";

    private ArrayList<Seism> listSeism = null;
    //private CustomAdapter seismAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_map);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        try{
            Intent intent =  getIntent();
            listSeism = (ArrayList<Seism>) intent.getSerializableExtra("list");
        } catch (Exception err){
            Log.d("Erreur", "Erreur de désérialisation");
        }
        if (listSeism == null) listSeism = new ArrayList<>();

        /* On va pas appeler une tâche asynchrone dans une autre alors qu'on a déjà ramené toute la data nécessaire */
        //seismAdapter = new CustomAdapter(this, listSeism);
        //this.asyncJson();

    }

    /*private void asyncJson() {
        this.asyncJson(this.defaultAddress);
    }

    private void asyncJson(String url){
        new SeismAsyncTask().execute(this, this.listSeism, this.seismAdapter, null, url);
    }*/

    @Override
    public void onMapReady(GoogleMap googleMap) {

        //ArrayList <Seism> seismList = this.listSeism;
        for(Seism seisme : this.listSeism) {
            float mag = seisme.getMag();
            float hue = 59.f;
            if (mag >= 3 && mag < 6) {
                hue = 45.f;
            } else if (mag >= 6 && mag < 9){
                hue = 20.f;
            } else if (mag >=9){
                hue = 0.f;
            }
            googleMap.addMarker(
                    new MarkerOptions()
                    .position(new LatLng(seisme.getCoordinates().getLatitude(), seisme.getCoordinates().getLongitude()))
                    .title(seisme.getTitle())
                            .icon(BitmapDescriptorFactory.defaultMarker(hue)));
        }
    }
}
