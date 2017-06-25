package us.julesandremi.seismic;

import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.StringTokenizer;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by remicmacs on 07/06/17.
 */

public class SeismAsyncTask extends AsyncTask<Object, Void, SeismsStream>  {

    private URL url ;
    private String address ;
    private Context context;
    private ArrayList<Seism> listSeism;
    private CustomAdapter seismAdapter;
    private SwipeRefreshLayout swipeRefreshLayoutFromMain;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected SeismsStream doInBackground(Object... params) {
        this.context = (Context) params[0];
        listSeism = (ArrayList<Seism>) params[1];
        seismAdapter = (CustomAdapter) params[2];
        this.swipeRefreshLayoutFromMain = (SwipeRefreshLayout) params[3];
        this.address = (String) params[4];

        try{
            this.url = new URL(this.address);
        } catch (MalformedURLException err){
            Log.d("Erreur ", err.getMessage()+"\n" + Arrays.toString(err.getStackTrace()));
        }

        SeismsStream stream = null;
        try {
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            if (urlConnection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                JsonParser js = new JsonParser(urlConnection.getInputStream());
                stream = js.readJsonStream();
            }

        } catch (Exception err) {
            Log.d("Connexion", err.getMessage());

        }
        try{
            listSeism = stream.getSeisms();
        } catch (Exception err) {
            Log.d("Parsing", err.getMessage() + "\n" + err.getStackTrace().toString());
        }
        Collections.sort(listSeism);
        seismAdapter.setListSeism(listSeism);
        return stream;
    }

    @Override
    protected void onPostExecute(SeismsStream s) {
        try{
            Snackbar.make(this.swipeRefreshLayoutFromMain, String.format(this.context.getResources().getString(R.string.founded_seisms), s.getCount()), Snackbar.LENGTH_LONG).show();
            seismAdapter.notifyDataSetChanged();
            swipeRefreshLayoutFromMain.setRefreshing(false);
        } catch (Exception err){
            Log.d("Erreur on Post", Arrays.toString(err.getStackTrace()));
        }
    }
}
