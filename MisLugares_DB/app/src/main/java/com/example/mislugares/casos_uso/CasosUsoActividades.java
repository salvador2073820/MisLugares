package com.example.mislugares.casos_uso;

import android.app.Activity;
import android.content.Intent;

import com.example.mislugares.presentacion.AcercaDeActivity;

public class CasosUsoActividades {
    private Activity actividad;

    public CasosUsoActividades(Activity actividad) {
        this.actividad = actividad;
    }

    //lanzar AcercaDe
    public void lanzarAcercaDe() {
        Intent i = new Intent(actividad, AcercaDeActivity.class);
        actividad.startActivity(i);
    }

    //funcions obsoletas, solo eran ejmplos temp
    /*public void lanzarPreferencias() {
        Intent i = new Intent(actividad, PreferenciasActivity.class);
        actividad.startActivity(i);
    }

    public void lanzarMapa() {
        Intent i = new Intent(actividad, MapaActivity.class);
        actividad.startActivity(i);
    }*/
}
