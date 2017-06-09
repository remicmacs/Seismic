package us.julesandremi.seismic;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * Created by remicmacs on 08/06/17.
 */

public class SeismsStream implements Serializable {
    private ArrayList<Seism> seisms;
    private Timestamp generated;
    private int count;

    public SeismsStream (ArrayList<Seism> seisms, Timestamp generated, int count){
        this.setSeisms(seisms);
        this.setCount(count);
        this.setGenerated(generated);
    }

    public ArrayList<Seism> getSeisms() {
        return seisms;
    }

    public void setSeisms(ArrayList<Seism> seisms) {
        this.seisms = seisms;
    }

    public Timestamp getGenerated() {
        return generated;
    }

    public void setGenerated(Timestamp generated) {
        this.generated = generated;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        if (count < 0 ) throw new IllegalArgumentException("Negative count of events forbidden");
        this.count = count;
    }
}
