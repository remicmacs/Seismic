package us.julesandremi.seismic;

import android.util.JsonReader;
import android.util.Log;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * Created by remicmacs on 07/06/17.
 * @// TODO: 08/06/17 Ajouter Package Métier avec SeismAsyncTask 
 */

public class JsonParser  {

    private Reader in;

    private JsonReader reader;

    public JsonParser(InputStream in){
        try{
            this.reader = new JsonReader(new InputStreamReader(in, "UTF-8"));

        } catch (UnsupportedEncodingException err){
            Log.d("Erreur", "parsing JSON error : constructor : "+ Arrays.toString(err.getStackTrace()));
        }
    }


    public SeismsStream readJsonStream() throws JSONException {
        try {
            int count = 0;
            Timestamp generated = new Timestamp(System.currentTimeMillis());
            ArrayList<Seism> seisms = null;

            reader.beginObject();
            while (reader.hasNext()){
                String propertyName = reader.nextName();
                switch (propertyName) {
                    case "metadata":
                        reader.beginObject();
                        while (reader.hasNext()) {
                            propertyName = reader.nextName();
                            switch (propertyName) {
                                case "generated":
                                    long generatedSeconds = reader.nextLong();
                                    generated = new Timestamp(generatedSeconds);
                                    break;
                                case "count":
                                    count = reader.nextInt();
                                    break;
                                default:
                                    reader.skipValue();
                                    break;
                            }
                        }
                        reader.endObject();
                        break;
                    case "features":
                        seisms = readSeismArray();
                        break;
                    default:
                        reader.skipValue();
                        break;
                }
            }
            reader.endObject();
            reader.close();

            return new SeismsStream(seisms, generated, count);

        } catch (Exception err){
            Log.d("Erreur", "parsing JSON error : entry point : " + Arrays.toString(err.getStackTrace()));
            throw new JSONException("Erreur de parsing");
        } finally {
            try {

                this.reader.close();
            } catch (Exception err){
                Log.d("Erreur", "parsing JSON error : closing point : " + Arrays.toString(err.getStackTrace()));
            }
        }

    }

    private ArrayList<Seism> readSeismArray() throws IOException {
        ArrayList<Seism> listSeisms = new ArrayList<>(50);
        reader.beginArray();
        while (reader.hasNext()){
            listSeisms.add(this.readSeism());
        }
        reader.endArray();
        return listSeisms;
    }

    private Seism readSeism() throws IOException {
        Seism newSeism;

        float mag = -1.0f;
        String place= "";
        Timestamp time = new Timestamp(System.currentTimeMillis()); // UTC ?
        //Timestamp updated;
        //int tz;//timezone [-1200;+1200];
        String url = "";
        //String type; // earthquake par exemple
        String title = "";
        //String alert; // Niveau d'alerte ["green", "yellow", "orange", "red"] => Gérer avec une Enum ?
        boolean tsunami = false; // Boolean en fait 1 si oui, 0 si non
        CoordinatesPoint point = null;
        //String ids; // a split dans un tableau de str dans le Séisme == ensemble d'ids d'évenements associés à celui-ci
        String id = ""; // Unique identifier


        //int felt; // Nombre total de signalements par des personnes ayant ressenti le séisme
        //float cdi; // [0.0, 10.0] intensité maximale rapportée pour l'événement
        //double mmi; // [0.0, 10.0] intensité maximale rapportée pour l'événement Sauf que c'est un autre organisme
        //String status;
        //int sig; // [0, 1000] indice de "significance". Est fonction des autres facteurs.
        //String net;
        //String code; // Source d'origine qui a renseigné l'événement.
        //String sources;  // pareil, il faut split
        //String types;
        //int nst; // Nombre de stations sismiques utilisées pour déterminer l'emplacement du seism
        //float dmin; // [0.4, 7.1] distance horizontale en degrees (1 degree ~= 111.2 km) jusqu'à la station la plus proche.
        //float rms;  // Franchement on s'en servira pas

        reader.beginObject();

        while (reader.hasNext()){
            String propertyName = reader.nextName();


            switch (propertyName){
                case "properties":
                    reader.beginObject();
                    while (reader.hasNext()){
                        String internalPropertiesName = reader.nextName();
                        switch (internalPropertiesName){
                            case "mag" :
                                mag = (float) reader.nextDouble();
                                break;
                            case "place" :
                                place = reader.nextString();
                                break;
                            case "time" :
                                long milliseconds = reader.nextLong();
                                time = new Timestamp(milliseconds);
                                break;
                            case "title" :
                                title = reader.nextString();
                                break;
                            case "tsunami" :
                                tsunami = (reader.nextInt() == 1 ? true : false);
                                break;
                            case "url" :
                                url = reader.nextString();
                                break;
                            default:
                                reader.skipValue();
                        }
                    }
                    reader.endObject();
                    break;
                case "id":
                    id = reader.nextString();
                    break;
                case "geometry":
                    point = readCoordinates();
                    break;
                default:
                    reader.skipValue();
            }
        }

        reader.endObject();

        newSeism = new Seism(title, mag, place, time, id, point);
        newSeism.setTsunami(tsunami);

        if (!"".equals(url)) {
            try {
                URL uri = new URL(url);
                newSeism.setUrl(uri);
            } catch (MalformedURLException err){
                Log.d("Erreur : ", err.getMessage()+"\n"+Arrays.toString(err.getStackTrace()));
            }
        }

        return newSeism;
    }

    private CoordinatesPoint readCoordinates() throws IOException {
        float coordinates[] = new float[3];
        int i = 0;
        float longitude = 0.f;
        float latitude = 0.f;
        float depth = 0.f;

        reader.beginObject();
        while (reader.hasNext()){
            String key = reader.nextName();
            if (key.equals("coordinates")) {
                reader.beginArray();
                while (reader.hasNext() && i < 3){
                    coordinates[i++] = (float) reader.nextDouble();
                }

                longitude = coordinates[0];
                latitude = coordinates[1];
                depth = coordinates[2];

                reader.endArray();

            } else {
                reader.skipValue();
            }

        }
        reader.endObject();

        return new CoordinatesPoint(latitude, longitude, depth);

    }
}
