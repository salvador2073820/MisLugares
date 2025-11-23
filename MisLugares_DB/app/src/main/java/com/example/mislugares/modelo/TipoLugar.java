package com.example.mislugares.modelo;

import com.example.mislugares.R;

public enum TipoLugar {
    OTROS("Otros", android.R.drawable.ic_menu_help),
    RESTAURANTE("Restaurante", android.R.drawable.ic_menu_compass),
    BAR("Bar", android.R.drawable.ic_menu_share),
    COPAS("Copas", android.R.drawable.ic_menu_slideshow),
    ESPECTACULO("Espectáculo", android.R.drawable.ic_menu_view),
    HOTEL("Hotel", android.R.drawable.ic_menu_mylocation),
    COMPRAS("Compras", android.R.drawable.ic_menu_crop),
    EDUCACION("Educación", android.R.drawable.ic_menu_manage),
    DEPORTE("Deporte", android.R.drawable.ic_menu_directions),
    NATURALEZA("Naturaleza", android.R.drawable.ic_menu_gallery),
    GASOLINERA("Gasolinera", android.R.drawable.ic_menu_mylocation);

    private final String texto;
    private final int recurso;

    TipoLugar(String texto, int recurso) {
        this.texto = texto;
        this.recurso = recurso;
    }

    public String getTexto() {
        return texto;
    }

    public int getRecurso() {
        return recurso;
    }

    public static String[] getNombres() {
        String[] resultado = new String[TipoLugar.values().length];
        for (TipoLugar tipo : TipoLugar.values()) {
            resultado[tipo.ordinal()] = tipo.texto;
        }
        return resultado;
    }
}
