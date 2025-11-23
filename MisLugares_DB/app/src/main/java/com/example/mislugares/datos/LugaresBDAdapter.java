package com.example.mislugares.datos;

import android.content.Context;
import com.example.mislugares.modelo.Lugar;
import com.example.mislugares.presentacion.AdaptadorLugaresBD;

public class LugaresBDAdapter extends LugaresBD {
    private AdaptadorLugaresBD adaptador;

    public LugaresBDAdapter(Context contexto) {
        super(contexto);
    }

    public Lugar elementoPos(int pos) {
        return adaptador.lugarPosicion(pos);
    }

    public AdaptadorLugaresBD getAdaptador() {
        return adaptador;
    }

    public void setAdaptador(AdaptadorLugaresBD adaptador) {
        this.adaptador = adaptador;
    }

    @Override public void actualiza(int id, Lugar lugar) {
        super.actualiza(id, lugar);
        adaptador.setCursor(extraeCursor());
        adaptador.notifyDataSetChanged();
    }

    @Override public void borrar(int id) {
        super.borrar(id);
        adaptador.setCursor(extraeCursor());
        adaptador.notifyDataSetChanged();
    }

    public int actualizaPosLugar(int pos, Lugar lugar) {
        int id = adaptador.idPosicion(pos);
        actualiza(id, lugar);
        return adaptador.posicionId(id);
    }
}