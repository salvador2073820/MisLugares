package com.example.mislugares.datos;

import android.content.Context;

public class LugaresSingleton {

    private static LugaresSingleton instancia;

    private RepositorioLugares lugares;

    private LugaresSingleton(Context context) {
        lugares = new LugaresLista();
    }

    public static void inicializa(Context context) {
        if (instancia == null) {
            instancia = new LugaresSingleton(context);
        }
    }

    public static LugaresSingleton getInstance() {
        if (instancia == null) {
            throw new IllegalStateException("Debes llamar a inicializa(Context) antes de obtener la instancia");
        }
        return instancia;
    }

    public RepositorioLugares getLugares() {
        return lugares;
    }
}
