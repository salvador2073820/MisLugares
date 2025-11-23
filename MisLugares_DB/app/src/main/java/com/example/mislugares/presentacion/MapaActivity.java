package com.example.mislugares.presentacion;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.mislugares.R;
import com.example.mislugares.presentacion.AdaptadorLugaresBD;
import com.example.mislugares.modelo.GeoPunto;
import com.example.mislugares.modelo.Lugar;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapaActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {
    private GoogleMap mapa;
    private AdaptadorLugaresBD adaptador;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapa);

        adaptador = ((Aplicacion) getApplication()).adaptador;

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.mapa);
        mapFragment.getMapAsync(this);
    }

    @Override public void onInfoWindowClick(Marker marker) {
        for (int pos = 0; pos < adaptador.getItemCount(); pos++) {
            if (adaptador.lugarPosicion(pos).getNombre().equals(marker.getTitle())) {
                Intent intent = new Intent(this, VistaLugarActivity.class);
                intent.putExtra("pos", pos);
                startActivity(intent);
                break;
            }
        }
    }

    private Bitmap oscurecerBitmap(Bitmap original) {
        Bitmap bmp = Bitmap.createBitmap(original.getWidth(), original.getHeight(), original.getConfig());
        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint();

        ColorMatrix cm = new ColorMatrix();
        cm.setScale(0f, 0f, 0f, 1f);

        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(original, 0, 0, paint);
        return bmp;
    }

    @Override public void onMapReady(GoogleMap googleMap) {
        mapa = googleMap;
        mapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mapa.setMyLocationEnabled(true);
        }

        mapa.getUiSettings().setZoomControlsEnabled(true);
        mapa.getUiSettings().setCompassEnabled(true);

        if (adaptador.getItemCount() > 0) {
            //lugar donde se inicia en el mapa
            GeoPunto p = adaptador.lugarPosicion(0).getPosicion(); //primer elemento para abrir el mapa ahí
            mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(p.getLatitud(), p.getLongitud()), 12)); //zoom inicial

            for (int n = 0; n < adaptador.getItemCount(); n++) {
                Lugar lugar = adaptador.lugarPosicion(n);
                GeoPunto pos = lugar.getPosicion();
                if (pos != null && pos.getLatitud() != 0) {
                    Bitmap iGrande = BitmapFactory.decodeResource(
                            getResources(), lugar.getTipo().getRecurso());
                    Bitmap icono = Bitmap.createScaledBitmap(iGrande, 80, 80, false);
                    icono = oscurecerBitmap(icono);

                    mapa.addMarker(new MarkerOptions()
                            .position(new LatLng(pos.getLatitud(), pos.getLongitud()))
                            .title(lugar.getNombre())
                            .snippet(lugar.getDireccion())
                            .icon(BitmapDescriptorFactory.fromBitmap(icono)));
                }
            }
        }
        mapa.setOnInfoWindowClickListener(this);
    }
}