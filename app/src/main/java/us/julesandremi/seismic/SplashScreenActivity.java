package us.julesandremi.seismic;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import javax.net.ssl.HttpsURLConnection;

public class SplashScreenActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new PrefetchData().execute();
    }

    private class PrefetchData extends AsyncTask<Void, Void, SeismsStream> {


        @Override
        protected SeismsStream doInBackground(Void... params) {
            URL url = null;
            try{
                url = new URL("https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/4.5_week.geojson");
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
            return stream;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // before making http calls
        }


        @Override
        protected void onPostExecute(SeismsStream stream) {
            super.onPostExecute(stream);
            // After completing http call
            // will close this activity and lauch main activity
            Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
            i.putExtra("list", stream.getSeisms());
            startActivity(i);

            // close this activity
            finish();
        }

    }

}
