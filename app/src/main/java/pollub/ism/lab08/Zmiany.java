package pollub.ism.lab08;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "ZMIANY")
public class Zmiany {
    @PrimaryKey(autoGenerate = true)
    public int zmianaId;
    public String data;
    public int staraIlosc;
    public int nowaIlosc;
    public String nazwa;

    public Zmiany(String data, int staraIlosc, int nowaIlosc, String nazwa) {
        this.data = data;
        this.staraIlosc = staraIlosc;
        this.nowaIlosc = nowaIlosc;
        this.nazwa = nazwa;
    }
}