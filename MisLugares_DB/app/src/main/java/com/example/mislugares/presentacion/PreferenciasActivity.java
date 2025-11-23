package com.example.mislugares.presentacion;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.mislugares.R;

public class PreferenciasActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferencias);

        //toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //PRUEBAS boton atras - segun el libro no es necesario
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.contenedor_preferencias, new PreferenciasFragment())
                .commit();
    }

    @Override public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
