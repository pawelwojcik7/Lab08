package pollub.ism.lab08;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ZmianyDAO {

    @Insert  //Automatyczna kwerenda wystarczy
    public void insert(Zmiany historiaTransakcji);

    @Update
        //Automatyczna kwerenda wystarczy
    void update(Zmiany historiaTransakcji);

    @Query("SELECT zmianaId, nazwa, data, staraIlosc, nowaIlosc FROM ZMIANY WHERE nazwa= :wybraneWarzywoNazwa")
    List<Zmiany> selectAllUpdates(String wybraneWarzywoNazwa);

    @Query("SELECT COUNT(*) FROM ZMIANY") //Ile jest rekordów w tabeli
    int size();

}