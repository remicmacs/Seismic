package us.julesandremi.seismic;

import android.support.design.widget.CoordinatorLayout;

import java.io.Serializable;

/**
 * Created by remicmacs on 08/06/17.
 */

public class CoordinatesPoint implements Serializable {
    private float longitude; // [-180 ; 180] degrés E/W (méridien) Positif Est, Négatif Ouest (de Greenwich)

    private float latitude; // [-90; 90] degrés N/S (parallèle) Positif Nord, Négatif Sud (de l'équateur)
    private float depth; // en km [0; 1000]
    // Format google maps : <latitude>, <longitude>. Pas de virgule autre que séparatices de longitude latitude

    public CoordinatesPoint(float latitude, float longitude){
        this(latitude, longitude, 0.0f);
    }


    public CoordinatesPoint(float latitude, float longitude, float depth){
        this.setLatitude(latitude);
        this.setLongitude(longitude);
        this.setDepth(depth);
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        if (Math.abs(longitude) > 180) throw new IllegalArgumentException("Longitude cannot exceed 180 ° northward or southward");
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        if (Math.abs(latitude) > 90 ) throw new IllegalArgumentException("Latitude cannot exceed 90 ° northward or southward");
        this.latitude = latitude;
    }

    public float getDepth() {
        return depth;
    }

    public void setDepth(float depth) {
        if (depth < -1000 ||depth > 1000) throw new IllegalArgumentException("Depth of point exceded");
        this.depth = depth;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return (String.format("[ %f , %f , %f ]", this.getLatitude(), this.getLongitude(), this.getDepth() ));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;

        CoordinatesPoint autre = (CoordinatesPoint) obj;

        return (
                    ((Float) this.getLatitude() ).equals((Float) autre.getLatitude()) &&
                    ((Float) this.getLongitude()).equals((Float) autre.getLongitude()) &&
                    ((Float) this.getDepth() ).equals((Float) autre.getDepth())

                );
    }
}
