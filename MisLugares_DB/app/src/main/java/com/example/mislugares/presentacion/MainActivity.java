package com.example.mislugares.presentacion;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mislugares.R;
import com.example.mislugares.casos_uso.CasosUsoActividades;
import com.example.mislugares.casos_uso.CasosUsoLocalizacion;
import com.example.mislugares.casos_uso.CasosUsoLugar;
import com.example.mislugares.datos.LugaresBD;
import com.example.mislugares.datos.LugaresBDAdapter;
import com.example.mislugares.datos.RepositorioLugares;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    //btn comentados VIEJOS
    /*private Button bAcercaDe;
    private Button bPreferencias;
    private Button bSalir;
     */

    private LugaresBDAdapter lugares;
    private CasosUsoLugar usoLugar;
    private CasosUsoActividades usoActividades;
    private RecyclerView recyclerView;
    public AdaptadorLugaresBD adaptador;
    private static final int SOLICITUD_PERMISO_LOCALIZACION = 1;
    private CasosUsoLocalizacion usoLocalizacion;
    static final int RESULTADO_PREFERENCIAS = 0;
    MediaPlayer mp;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }

        lugares = ((Aplicacion) getApplication()).lugares;
        usoLugar = new CasosUsoLugar(this, lugares);
        usoActividades = new CasosUsoActividades(this);
        recyclerView = findViewById(R.id.recyclerview);
        adaptador = ((Aplicacion) getApplication()).adaptador;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adaptador);

        adaptador.setOnItemClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                int pos = recyclerView.getChildAdapterPosition(v);
                usoLugar.mostrar(pos);
            }
        });

        //musica CREO QUE ES EJERCICIO TEMPORAL
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean musicaActiva = pref.getBoolean("musica", true);
        mp = MediaPlayer.create(this, R.raw.menu);
        mp.setLooping(true);
        if (musicaActiva) {
            mp.start();
        }

        usoLocalizacion = new CasosUsoLocalizacion(this, SOLICITUD_PERMISO_LOCALIZACION);

        //Codigo de botones comentado VIEJO
        /*
        bPreferencias = findViewById(R.id.btnPreferencias);
        bPreferencias.setOnClickListener(view -> mostrarPreferencias(view));

        bAcercaDe = findViewById(R.id.btnAcerca);
        bAcercaDe.setOnClickListener(view -> usoActividades.lanzarAcercaDe());

        bSalir = findViewById(R.id.btnSalir);
        bSalir.setOnClickListener(view -> finish());
        */

        adaptador.setOnItemClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                int pos = (Integer) v.getTag();
                usoLugar.mostrar(pos);
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                usoLugar.nuevo();
            }
        });
    }

    @Override protected void onStop() {
        super.onStop();
        usoLocalizacion.desactivar();
        if (mp != null && mp.isPlaying()) {
            mp.pause();
        }
    }

    @Override protected void onResume() {
        super.onResume();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean musicaActiva = pref.getBoolean("musica", true);
        usoLocalizacion.activar();

        if (mp == null) {
            mp = MediaPlayer.create(this, R.raw.menu);
            mp.setLooping(true);
        }
        if (musicaActiva) {
            if (!mp.isPlaying()) {
                mp.start();
            }
        } else {
            if (mp.isPlaying()) {
                mp.pause();
            }
        }
        adaptador.actualiza();
    }


    @Override protected void onDestroy() {
        super.onDestroy();
        if (mp != null) {
            mp.release();
            mp = null;
        }
    }

    @Override protected void onSaveInstanceState(Bundle estadoGuardado) {
        super.onSaveInstanceState(estadoGuardado);
        if (mp != null) {
            int pos = mp.getCurrentPosition();
            estadoGuardado.putInt("posicion", pos);
        }
    }

    @Override protected void onRestoreInstanceState(Bundle estadoGuardado) {
        super.onRestoreInstanceState(estadoGuardado);
        if (estadoGuardado != null && mp != null) {
            int pos = estadoGuardado.getInt("posicion", 0);
            mp.seekTo(pos);
            mp.start();
        }
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.settings) {
            lanzarPreferencias(null);
            return true;
        }
        if (id == R.id.acercaDe) {
            usoActividades.lanzarAcercaDe();
            return true;
        }
        if (id == R.id.buscar) {
            lanzarVistaLugar(null);
            return true;
        }
        if (id == R.id.menu_mapa) {
            Intent intent = new Intent(this, MapaActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void lanzarVistaLugar(View view) {
        final EditText entrada = new EditText(this);
        entrada.setText("0"); //Valor por defecto

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, //ancho
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(50, -10, 50, 20);
        entrada.setLayoutParams(params);

        //ctn
        LinearLayout contenedor = new LinearLayout(this);
        contenedor.setOrientation(LinearLayout.VERTICAL);
        contenedor.addView(entrada);

        //tema actual PENDIENTE ARREGLAR
        boolean modoOscuro = (getResources().getConfiguration().uiMode &
                android.content.res.Configuration.UI_MODE_NIGHT_MASK)
                == android.content.res.Configuration.UI_MODE_NIGHT_YES;

        //color del título según el modo
        SpannableString titulo = new SpannableString("Selección de lugar");
        titulo.setSpan(new ForegroundColorSpan(
                modoOscuro ? Color.WHITE : Color.BLACK
        ), 0, titulo.length(), 0);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(titulo)
                .setMessage("Indica el id del lugar:")
                .setView(contenedor)
                .setPositiveButton("OK", (dialogInterface, whichButton) -> {
                    try {
                        int id = Integer.parseInt(entrada.getText().toString());
                        usoLugar.mostrar(id);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .create();
        dialog.show();
    }


    //PreferenciasActivity
    public void lanzarPreferencias(View view) {
        Intent i = new Intent(this, PreferenciasActivity.class);
        startActivityForResult(i, RESULTADO_PREFERENCIAS);
    }

    //toast para ver las preferencias CREO QUE ES VIEJO
    public void mostrarPreferencias(View view) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String s = "Notificaciones: " + pref.getBoolean("notificaciones", false)
                + ", Orden: " + pref.getString("orden", "?")
                + ", Maximo: " + pref.getString("maximo", "3");
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULTADO_PREFERENCIAS) {
            adaptador.setCursor(lugares.extraeCursor());
            adaptador.notifyDataSetChanged();
        }
    }
}
