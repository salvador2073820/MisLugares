package com.example.mislugares.datos;
import com.example.mislugares.modelo.Lugar;
import com.example.mislugares.modelo.TipoLugar;

import java.util.ArrayList;
import java.util.List;

public class LugaresLista implements RepositorioLugares {
    protected List<Lugar> listaLugares;

    public LugaresLista(){
        listaLugares = new ArrayList<Lugar>();
        anadeEjemplos();
    }

    public Lugar elemento(int id){
        return listaLugares.get(id);
    }

    public void anade(Lugar lugar){
        listaLugares.add(lugar);
    }

    public int nuevo(){
        Lugar lugar = new Lugar();
        listaLugares.add(lugar);
        return listaLugares.size()-1;
    }

    public void borrar(int id){
        listaLugares.remove(id);
    }

    public int tamano(){
        return listaLugares.size();
    }

    public void actualiza(int id, Lugar lugar){
        listaLugares.set(id, lugar);
    }

    public void anadeEjemplos(){
        anade(new  Lugar("Escuela Politecnica Superior de Gandia",
                "C/ Paranimf, 1 46730 Gandia (SPAIN)",-0.166093, 38.995656,
                TipoLugar.EDUCACION,962849300, "http://ww.epsg.upv.es",
                "Uno de los mejores lugares para formarse.", 3));
        anade(new Lugar("Al de siempre",
                "P.Industrial Junto Molu Nou - 46722, Benefla (Valencia)",
                -0.190642, 38.925857, TipoLugar.BAR, 636472405, "",
                "No te pierdas el arroz en calabaza.", 3));
        anade(new Lugar("androidcurso.com",
            "ciberespacio", 0.0, 0.0, TipoLugar.EDUCACION,
                962849300, "http://androidcurso.com",
                "Amplia tus conocimientos sobre Android.", 5));
        anade(new Lugar("Barranco del Infierno",
                "Via Verde del rio Serpis. Villalonga (Valencia)",
                -0.295058, 38.867180, TipoLugar.NATURALEZA, 0,
                "http://sosegaos.blogspot.com.es/2009/02/lorcha.villalonga.via"+
                        "-verde-del-rio.html", "Espectacular ruta para bici o andar", 4));
        anade(new Lugar("La Vital",
                "Avda. de La Vital, 0 46701 Gandía (Valencia)", -0.17200092,
                38.9705949, TipoLugar.COMPRAS, 96881070,
                "http://www.lavital.es/", "El tipico centro comercial", 2));
    }
}
