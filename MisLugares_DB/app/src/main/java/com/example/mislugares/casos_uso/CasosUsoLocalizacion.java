package com.example.mislugares.casos_uso;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.example.mislugares.modelo.GeoPunto;
import com.example.mislugares.presentacion.AdaptadorLugares;

public class CasosUsoLocalizacion implements LocationListener {

    private static final String TAG = "MisLugares";
    private static final long DOS_MINUTOS = 2 * 60 * 1000;

    private Activity actividad;
    private int codigoPermiso;
    private LocationManager manejadorLoc;
    private Location mejorLoc;
    private GeoPunto posicionActual;
    private AdaptadorLugares adaptador;

    public CasosUsoLocalizacion(Activity actividad, int codigoPermiso) {
        this.actividad = actividad;
        this.codigoPermiso = codigoPermiso;
        this.manejadorLoc = (LocationManager) actividad.getSystemService(Activity.LOCATION_SERVICE);
        this.posicionActual = ((com.example.mislugares.presentacion.Aplicacion) actividad.getApplication()).posicionActual;
        this.adaptador = ((com.example.mislugares.presentacion.Aplicacion) actividad.getApplication()).adaptador;
        ultimaLocalizazion();
    }

    //veri permiso de localización
    public boolean hayPermisoLocalizacion() {
        return ActivityCompat.checkSelfPermission(
                actividad, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    //ultima localizacion conocida
    @SuppressLint("MissingPermission")
    void ultimaLocalizazion() {
        if (hayPermisoLocalizacion()) {
            if (manejadorLoc.isProviderEnabled(LocationManager.GPS_PROVIDER))
                actualizaMejorLocaliz(manejadorLoc.getLastKnownLocation(LocationManager.GPS_PROVIDER));
            if (manejadorLoc.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
                actualizaMejorLocaliz(manejadorLoc.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
        } else {
            solicitarPermiso(Manifest.permission.ACCESS_FINE_LOCATION,
                    "Sin el permiso localización no se puede mostrar la distancia a los lugares.",
                    codigoPermiso, actividad);
        }
    }

    public void permisoConcedido() {
        ultimaLocalizazion();
        activarProveedores();
        adaptador.notifyDataSetChanged();
    }

    //actualizar localizacion
    @SuppressLint("MissingPermission")
    private void activarProveedores() {
        if (hayPermisoLocalizacion()) {
            if (manejadorLoc.isProviderEnabled(LocationManager.GPS_PROVIDER))
                manejadorLoc.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20 * 1000, 5, this);
            if (manejadorLoc.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
                manejadorLoc.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 18 * 1000, 10, this);
        } else {
            solicitarPermiso(Manifest.permission.ACCESS_FINE_LOCATION,
                    "Sin el permiso localización no se puede mostrar la distancia a los lugares.",
                    codigoPermiso, actividad);
        }
    }

    private void actualizaMejorLocaliz(Location localiz) {
        if (localiz != null && (mejorLoc == null
                || localiz.getAccuracy() < 2 * mejorLoc.getAccuracy()
                || localiz.getTime() - mejorLoc.getTime() > DOS_MINUTOS)) {
            Log.d(TAG, "Nueva mejor localización");
            mejorLoc = localiz;
            posicionActual.setLatitud(localiz.getLatitude());
            posicionActual.setLongitud(localiz.getLongitude());
        }
    }

    public void activar() {
        if (hayPermisoLocalizacion()) activarProveedores();
    }

    public void desactivar() {
        if (hayPermisoLocalizacion()) manejadorLoc.removeUpdates(this);
    }

    //LocationListener
    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Nueva localización: " + location);
        actualizaMejorLocaliz(location);
        adaptador.notifyDataSetChanged();
    }

    @Override
    public void onProviderDisabled(String proveedor) {
        Log.d(TAG, "Se deshabilita: " + proveedor);
        activarProveedores();
    }

    @Override
    public void onProviderEnabled(String proveedor) {
        Log.d(TAG, "Se habilita: " + proveedor);
        activarProveedores();
    }

    @Override
    public void onStatusChanged(String proveedor, int estado, Bundle extras) {
        Log.d(TAG, "Cambia estado: " + proveedor);
        activarProveedores();
    }

    public static void solicitarPermiso(final String permiso, String justificacion, final int requestCode, final Activity actividad) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(actividad, permiso)) {
            new androidx.appcompat.app.AlertDialog.Builder(actividad)
                    .setTitle("Solicitud de permiso")
                    .setMessage(justificacion)
                    .setPositiveButton("OK", (dialog, which) -> ActivityCompat.requestPermissions(
                            actividad, new String[]{permiso}, requestCode))
                    .show();
        } else {
            ActivityCompat.requestPermissions(actividad, new String[]{permiso}, requestCode);
        }
    }
}
