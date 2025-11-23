package com.example.mislugares.presentacion;

import android.app.Application;
import com.example.mislugares.datos.LugaresBDAdapter;
import com.example.mislugares.modelo.GeoPunto;

public class Aplicacion extends Application {
    public LugaresBDAdapter lugares;
    public AdaptadorLugaresBD adaptador;
    public GeoPunto posicionActual = new GeoPunto(0.0, 0.0);

    @Override public void onCreate() {
        super.onCreate();
        lugares = new LugaresBDAdapter(this);
        adaptador = new AdaptadorLugaresBD(lugares, lugares.extraeCursor());
        lugares.setAdaptador(adaptador);
    }
}