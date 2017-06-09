package us.julesandremi.seismic;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import org.json.JSONObject;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import javax.net.ssl.HttpsURLConnection;

/**
 * Created by remicmacs on 07/06/17.
 */

public class SeismAsyncTask extends AsyncTask<Object, Void, SeismsStream>  {

    private URL url ;
    private TextView tvTest;
    private Context context;

    @Override
    protected void onPreExecute() {
        try{
            String address = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/4.5_week.geojson";
            this.url = new URL(address);
        } catch (MalformedURLException err){
            Log.d("Erreur ", err.getMessage()+"\n" + Arrays.toString(err.getStackTrace()));
        }
        super.onPreExecute();
    }

    @Override
    protected SeismsStream doInBackground(Object... params) {
        this.tvTest = (TextView) params[0];
        this.context = (Context) params[1];

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
    protected void onPostExecute(SeismsStream s) {
        try{
            this.tvTest.setText("Nombre de seismes trouvés : "+s.getCount());
        } catch (Exception err){
            Log.d("Erreur", Arrays.toString(err.getStackTrace()));
        }
    }
}
