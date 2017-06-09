package us.julesandremi.seismic;

import java.io.Serializable;
import java.net.URL;
import java.sql.Timestamp;

/**
 * Created by remicmacs on 07/06/17.
 * 
 * @// TODO: 08/06/17 Ajouter toString 
 * @// TODO: 08/06/17 Ajouter package model avec CoordinatesPoint, Seism, SeismsStream 
 */

public class Seism implements Serializable {
    private String title;
    private float mag; //[-1.0; 10.0]
    private String place;
    private Timestamp time;
    private String id;
    private String [] ids;
    private boolean tsunami;
    private CoordinatesPoint coordinates;
    private URL url;

    public Seism(String title, float mag, String place, Timestamp time, String id, CoordinatesPoint coordinates) {
        this.setTitle(title);
        this.setMag(mag);
        this.setPlace(place);
        this.setTime(time);
        this.setId(id);
        this.setCoordinates(coordinates);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        if ("".equals(place)) throw new IllegalArgumentException("Place must be informed");
        this.place = place;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if ("".equals(id)) throw new IllegalArgumentException("Id must be informed");
        this.id = id;
    }

    public String[] getIds() {
        return ids;
    }

    public void setIds(String[] ids) {
        this.ids = ids;
    }

    public boolean isTsunami() {
        return tsunami;
    }

    public void setTsunami(boolean tsunami) {
        this.tsunami = tsunami;
    }

    public CoordinatesPoint getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(CoordinatesPoint coordinates) {
        this.coordinates = coordinates;
    }

    public float getMag() {
        return mag;
    }

    public void setMag(float mag) {
        if (mag < -1 || mag > 10) throw new IllegalArgumentException("Magnitude value out of bonds");
        this.mag = mag;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }
}
