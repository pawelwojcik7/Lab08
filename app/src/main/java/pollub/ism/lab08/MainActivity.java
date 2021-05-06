package pollub.ism.lab08;


import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;


import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import pollub.ism.lab08.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ArrayAdapter<CharSequence> adapter;

    private String wybraneWarzywoNazwa = "";
    private Integer wybraneWarzywoIlosc = 0;

    public enum OperacjaMagazynowa {SKLADUJ, WYDAJ}

    private BazaMagazynowa bazaDanych;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.activity_main);

        bazaDanych = Room.databaseBuilder(getApplicationContext(), BazaMagazynowa.class, BazaMagazynowa.NAZWA_BAZY)
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();

        if(bazaDanych.pozycjaMagazynowaDAO().size() == 0){
            String[] asortyment = getResources().getStringArray(R.array.Asortyment);
            for(String nazwa : asortyment){
                PozycjaMagazynowa pozycjaMagazynowa = new PozycjaMagazynowa();
                pozycjaMagazynowa.NAME = nazwa; pozycjaMagazynowa.QUANTITY = 0;
                bazaDanych.pozycjaMagazynowaDAO().insert(pozycjaMagazynowa);
            }
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        adapter = ArrayAdapter.createFromResource(this, R.array.Asortyment, android.R.layout.simple_dropdown_item_1line);
        binding.spinner.setAdapter(adapter);

        binding.przyciskSkladuj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zmienStan(OperacjaMagazynowa.SKLADUJ);
            }
        });

        binding.przyciskWydaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zmienStan(OperacjaMagazynowa.WYDAJ);
            }
        });

        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                wybraneWarzywoNazwa = adapter.getItem(i).toString();
                aktualizuj();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //Nie będziemy implementować, ale musi być
            }
        });

    }

    private void aktualizuj(){
        StringBuilder napis = new StringBuilder();
        wybraneWarzywoIlosc = bazaDanych.pozycjaMagazynowaDAO().findQuantityByName(wybraneWarzywoNazwa);
        binding.tekstStanMagazynu.setText(wybraneWarzywoNazwa+" : "+wybraneWarzywoIlosc);

        for (Zmiany info : bazaDanych.zmianyDAO().selectAllUpdates(wybraneWarzywoNazwa)) {
            napis.append(info.nazwa).append(" ").append(info.data).append(" ")
                    .append(info.staraIlosc).append(" ").append(info.nowaIlosc).append("\n");
        }
        binding.historia.setText(napis.toString());
    }


    private void zmienStan(OperacjaMagazynowa operacja){

        Integer zmianaIlosci = null, zmienionaIlosc = null;

        try {
            zmianaIlosci = Integer.parseInt(binding.edycjaIlosc.getText().toString());
        }catch(NumberFormatException ex){
            return;
        }finally {
            binding.edycjaIlosc.setText("");
        }


        ZoneId zone = ZoneId.of( "UTC+2" );
        ZonedDateTime zoneTime = ZonedDateTime.now(zone);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");

        switch (operacja){
            case SKLADUJ: zmienionaIlosc = wybraneWarzywoIlosc + zmianaIlosci; break;
            case WYDAJ: zmienionaIlosc = wybraneWarzywoIlosc - zmianaIlosci; break;
        }

        Zmiany historiaZmian = new Zmiany(zoneTime.format(formatter),wybraneWarzywoIlosc, zmienionaIlosc, wybraneWarzywoNazwa);
        bazaDanych.zmianyDAO().insert(historiaZmian);
        bazaDanych.pozycjaMagazynowaDAO().updateQuantityByName(wybraneWarzywoNazwa,zmienionaIlosc);

        aktualizuj();
    }
}