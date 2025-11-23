package com.example.mislugares.casos_uso;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.os.Environment;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.IOException;

import com.example.mislugares.R;
import com.example.mislugares.datos.LugaresBDAdapter;
import com.example.mislugares.modelo.GeoPunto;
import com.example.mislugares.modelo.Lugar;
import com.example.mislugares.presentacion.EdicionLugarActivity;
import com.example.mislugares.presentacion.VistaLugarActivity;
import com.example.mislugares.presentacion.Aplicacion;

public class CasosUsoLugar {
    private Activity actividad;
    private LugaresBDAdapter lugares;

    public CasosUsoLugar(Activity actividad, LugaresBDAdapter lugares){
        this.actividad = actividad;
        this.lugares = lugares;
    }

    public void mostrar(int pos){
        Intent i = new Intent(actividad, VistaLugarActivity.class);
        i.putExtra("pos", pos);
        actividad.startActivity(i);
    }

    public void borrar(int id) {
        lugares.borrar(id);
        actividad.finish();
    }

    public void borrarPos(int pos) {
        int id = lugares.getAdaptador().idPosicion(pos);
        borrar(id);
    }

    public void editar(int pos, int codigoSolicitud) {
        Intent i = new Intent(actividad, EdicionLugarActivity.class);
        i.putExtra("pos", pos);
        actividad.startActivityForResult(i, codigoSolicitud);
    }

    public void guardar(int id, Lugar nuevoLugar){
        lugares.actualiza(id, nuevoLugar);
    }

    //alta de un lugar
    public void nuevo() {
        int id = lugares.nuevo();
        GeoPunto posicion = ((Aplicacion) actividad.getApplication()).posicionActual;
        if (!posicion.equals(GeoPunto.SIN_POSICION)) {
            Lugar lugar = lugares.elemento(id);
            lugar.setPosicion(posicion);
            lugares.actualiza(id, lugar);
        }
        Intent i = new Intent(actividad, EdicionLugarActivity.class);
        i.putExtra("id", id);
        actividad.startActivity(i);
    }

    public void compartir(Lugar lugar) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, lugar.getNombre() + " - " + lugar.getUrl());
        actividad.startActivity(Intent.createChooser(intent, "Compartir con:"));
    }

    public void llamarTelefono(Lugar lugar) {
        if (lugar.getTelefono() == 0) return;

        Intent intent = new Intent(Intent.ACTION_CALL,
                Uri.parse("tel:" + lugar.getTelefono()));

        if (ContextCompat.checkSelfPermission(actividad,
                Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED) {
            actividad.startActivity(intent);
        } else {
            ActivityCompat.requestPermissions(
                    actividad,
                    new String[]{Manifest.permission.CALL_PHONE},
                    123);
        }
    }

    public void verPgWeb(Lugar lugar) {
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(lugar.getUrl()));
        actividad.startActivity(intent);
    }

    public void verMapa(Lugar lugar) {
        double lat = lugar.getPosicion().getLatitud();
        double lon = lugar.getPosicion().getLongitud();
        Uri uri;

        if (lat != 0 && lon != 0) {
            uri = Uri.parse("geo:" + lat + "," + lon);
        } else {
            uri = Uri.parse("geo:0,0?q=" + Uri.encode(lugar.getDireccion()));
        }

        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        actividad.startActivity(intent);
    }

    //fotos
    public void ponerDeGaleria(int codigoSolicitud) {
        String action;
        if (Build.VERSION.SDK_INT >= 19) {
            action = Intent.ACTION_OPEN_DOCUMENT;
        } else {
            action = Intent.ACTION_PICK;
        }
        Intent intent = new Intent(action, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        actividad.startActivityForResult(intent, codigoSolicitud);
    }

    public void ponerFoto(int pos, String uri, ImageView imageView) {
        Lugar lugar = lugares.elementoPos(pos);
        lugar.setFoto(uri);
        visualizarFoto(lugar, imageView);
        lugares.actualizaPosLugar(pos, lugar);

        if (uri != null && !uri.isEmpty()) {
            try {
                actividad.getContentResolver().takePersistableUriPermission(
                        Uri.parse(uri),
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void visualizarFoto(Lugar lugar, ImageView imageView) {
        try {
            if (lugar.getFoto() != null && !lugar.getFoto().isEmpty()) {
                imageView.setImageURI(Uri.parse(lugar.getFoto()));
            } else {
                imageView.setImageResource(R.drawable.ic_tipo);
            }
        } catch (Exception e) {
            imageView.setImageResource(R.drawable.ic_tipo);
            e.printStackTrace();
        }
    }

    public Uri tomarFoto(int codigoSolicitud) {
        try {
            File file = File.createTempFile(
                    "img_" + (System.currentTimeMillis() / 1000),
                    ".jpg",
                    actividad.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            );

            Uri uriUltimaFoto;
            if (Build.VERSION.SDK_INT >= 24) {
                uriUltimaFoto = FileProvider.getUriForFile(
                        actividad,
                        "com.example.mislugaresa.fileProvider",
                        file
                );
            } else {
                uriUltimaFoto = Uri.fromFile(file);
            }

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uriUltimaFoto);
            actividad.startActivityForResult(intent, codigoSolicitud);

            return uriUltimaFoto;

        } catch (IOException ex) {
            Toast.makeText(actividad, "Error al crear fichero de imagen", Toast.LENGTH_LONG).show();
            return null;
        }
    }

}