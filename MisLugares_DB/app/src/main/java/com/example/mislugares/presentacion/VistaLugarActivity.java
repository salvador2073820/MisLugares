package com.example.mislugares.presentacion;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import android.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.mislugares.R;
import com.example.mislugares.casos_uso.CasosUsoLugar;
import com.example.mislugares.datos.LugaresBDAdapter;
import com.example.mislugares.modelo.Lugar;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import android.provider.MediaStore;

public class VistaLugarActivity extends AppCompatActivity {

    private LugaresBDAdapter lugares;
    private CasosUsoLugar usoLugar;
    private int pos;
    private int id = -1;
    private Lugar lugar;
    private ImageView foto;
    private Uri uriUltimaFoto;
    final static int RESULTADO_EDITAR = 1;
    final static int RESULTADO_GALERIA = 2;
    final static int RESULTADO_FOTO = 3;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_lugar);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        pos = getIntent().getIntExtra("pos", 0);
        lugares = ((Aplicacion) getApplication()).lugares;
        usoLugar = new CasosUsoLugar(this, lugares);
        id = lugares.getAdaptador().idPosicion(pos);
        lugar = lugares.elemento(id);
        foto = findViewById(R.id.foto);

        actualizaVistas();
    }

    public void actualizaVistas() {
        TextView nombre = findViewById(R.id.nombre);
        nombre.setText(lugar.getNombre().isEmpty() ? "(Sin nombre)" : lugar.getNombre());

        ImageView logo_tipo = findViewById(R.id.logo_tipo);
        logo_tipo.setImageResource(lugar.getTipo().getRecurso());
        TextView tipo = findViewById(R.id.tipo);
        tipo.setText(lugar.getTipo().getTexto());

        //direccion
        LinearLayout layoutDireccion = findViewById(R.id.layout_direccion);
        TextView direccion = findViewById(R.id.direccion);
        if (lugar.getDireccion().isEmpty()) {
            layoutDireccion.setVisibility(View.GONE);
        } else {
            layoutDireccion.setVisibility(View.VISIBLE);
            direccion.setText(lugar.getDireccion());
            layoutDireccion.setOnClickListener(view -> verMapa(view));
        }

        //tel
        LinearLayout layoutTelefono = findViewById(R.id.layout_telefono);
        TextView telefono = findViewById(R.id.telefono);
        if (lugar.getTelefono() == 0) {
            layoutTelefono.setVisibility(View.GONE);
        } else {
            layoutTelefono.setVisibility(View.VISIBLE);
            telefono.setText(Integer.toString(lugar.getTelefono()));
            layoutTelefono.setOnClickListener(view -> llamarTelefono(view));
        }

        //url
        LinearLayout layoutUrl = findViewById(R.id.layout_url);
        TextView url = findViewById(R.id.url);
        if (lugar.getUrl().isEmpty()) {
            layoutUrl.setVisibility(View.GONE);
        } else {
            layoutUrl.setVisibility(View.VISIBLE);
            url.setText(lugar.getUrl());
            layoutUrl.setOnClickListener(view -> verPgWeb(view));
        }

        //comentario
        LinearLayout layoutComentario = findViewById(R.id.layout_comentario);
        TextView comentario = findViewById(R.id.comentario);
        if (lugar.getComentario().isEmpty()) {
            layoutComentario.setVisibility(View.GONE);
        } else {
            layoutComentario.setVisibility(View.VISIBLE);
            comentario.setText(lugar.getComentario());
        }

        //fecha y hora
        TextView fecha = findViewById(R.id.fecha);
        fecha.setText(DateFormat.getDateInstance().format(new Date(lugar.getFecha())));
        TextView hora = findViewById(R.id.hora);
        hora.setText(DateFormat.getTimeInstance().format(new Date(lugar.getFecha())));

        //valoración
        RatingBar valoracion = findViewById(R.id.valoracion);
        valoracion.setOnRatingBarChangeListener(null);
        valoracion.setRating(lugar.getValoracion());
        valoracion.setOnRatingBarChangeListener(
                new RatingBar.OnRatingBarChangeListener() {
                    @Override public void onRatingChanged(RatingBar ratingBar, float valor, boolean fromUser) {
                        if (fromUser) {
                            lugar.setValoracion(valor);
                            pos = lugares.actualizaPosLugar(pos, lugar); //guarda y actualiza (en BD y adaptador)
                            actualizaVistas(); //refresca la vista para que muestre el rating
                        }
                    }
                }
        );
        usoLugar.visualizarFoto(lugar, foto);
    }

    public void verMapa(View view) { usoLugar.verMapa(lugar); }
    public void llamarTelefono(View view) { usoLugar.llamarTelefono(lugar); }
    public void verPgWeb(View view) { usoLugar.verPgWeb(lugar); }
    public void compartir(View view) { usoLugar.compartir(lugar); }
    public void ponerDeGaleria(View view) { usoLugar.ponerDeGaleria(RESULTADO_GALERIA); }
    public void tomarFoto(View view) {
        uriUltimaFoto = usoLugar.tomarFoto(RESULTADO_FOTO);
    }
    public void eliminarFoto(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Foto")
                .setMessage("¿Estás seguro que quieres eliminar esta fotografía?")
                .setPositiveButton("Confirmar", (dialog, which) -> {
                    usoLugar.ponerFoto(pos, "", foto);
                    lugar = lugares.elemento(id);
                    actualizaVistas();
                    Toast.makeText(this, "Foto eliminada", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vista_lugar, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.accion_compartir:
                compartir(null);
                return true;
            case R.id.accion_llegar:
                verMapa(null);
                return true;
            case R.id.accion_editar:
                usoLugar.editar(pos, RESULTADO_EDITAR);
                return true;
            case R.id.accion_borrar:
                usoLugar.borrarPos(pos);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULTADO_EDITAR) {
            lugar = lugares.elemento(id);
            pos = lugares.getAdaptador().posicionId(id);
            actualizaVistas();
        } else if (requestCode == RESULTADO_GALERIA) {
            if (resultCode == Activity.RESULT_OK) {
                usoLugar.ponerFoto(pos, data.getDataString(), foto);
            } else {
                Toast.makeText(this, "Foto no cargada", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == RESULTADO_FOTO) {
            if (resultCode == Activity.RESULT_OK && uriUltimaFoto != null) {
                lugar.setFoto(uriUltimaFoto.toString());
                usoLugar.guardar(pos, lugar);
                usoLugar.visualizarFoto(lugar, foto);
            } else {
                Toast.makeText(this, "Error en captura", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override protected void onResume() {
        super.onResume();
        lugar = lugares.elemento(id);
        pos = lugares.getAdaptador().posicionId(id);
        actualizaVistas();
    }

    @Override public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 123) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                usoLugar.llamarTelefono(lugar);
            } else {
                Toast.makeText(this, "Permiso de llamada denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }
}