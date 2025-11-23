package com.example.mislugares.datos;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.preference.PreferenceManager;

import com.example.mislugares.modelo.Lugar;
import com.example.mislugares.modelo.TipoLugar;
import com.example.mislugares.datos.RepositorioLugares;
import com.example.mislugares.presentacion.Aplicacion;

public class LugaresBD extends SQLiteOpenHelper implements RepositorioLugares {

    Context contexto;

    public LugaresBD(Context contexto) {
        super(contexto, "lugares", null, 1);
        this.contexto = contexto;
    }

    @Override
    public void onCreate(SQLiteDatabase bd) {

        bd.execSQL("CREATE TABLE lugares (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT, " +
                "direccion TEXT, " +
                "longitud REAL, " +
                "latitud REAL, " +
                "tipo INTEGER, " +
                "foto TEXT, " +
                "telefono INTEGER, " +
                "url TEXT, " +
                "comentario TEXT, " +
                "fecha BIGINT, " +
                "valoracion REAL)");

        //ejemplos insertado por defecto (son de espana)
        bd.execSQL("INSERT INTO lugares VALUES (null, " +
                "'Escuela Politécnica Superior de Gandía', " +
                "'C/ Paranimf, 1 46730 Gandia (SPAIN)', " +
                "-0.166093, 38.995656, " +
                TipoLugar.EDUCACION.ordinal() + ", " +
                "null, 962849300, 'http://www.epsg.upv.es', " +
                "'Uno de los mejores lugares para formarse.', " +
                System.currentTimeMillis() + ", 3.0)");

        bd.execSQL("INSERT INTO lugares VALUES (null, " +
                "'Al de siempre', " +
                "'P. Industrial Junto Molí Nou - 46722, Beniflá (Valencia)', " +
                "-0.199642, 38.925857, " +
                TipoLugar.BAR.ordinal() + ", " +
                "null, 636472495, '', " +
                "'No te pierdas el arroz en calabaza.', " +
                System.currentTimeMillis() + ", 3.0)");

        bd.execSQL("INSERT INTO lugares VALUES (null, " +
                "'androidcurso.com', " +
                "'ciberespacio', 0.0, 0.0, " +
                TipoLugar.EDUCACION.ordinal() + ", " +
                "null, 962849380, 'http://androidcurso.com', " +
                "'Amplía tus conocimientos sobre Android.', " +
                System.currentTimeMillis() + ", 5.0)");

        bd.execSQL("INSERT INTO lugares VALUES (null, " +
                "'Barranco del Infierno', " +
                "'Vía Verde del río Serpis. Villalonga (Valencia)', " +
                "-0.295958, 38.867180, " +
                TipoLugar.NATURALEZA.ordinal() + ", " +
                "null, 0, 'http://sosegaos.blogspot.com.es/2009/02/lorcha-villalonga-via-verde-del-rio.html', " +
                "'Espectacular ruta para bici o andar.', " +
                System.currentTimeMillis() + ", 4.0)");

        bd.execSQL("INSERT INTO lugares VALUES (null, " +
                "'La Vital', " +
                "'Avda. La Vital, 0 46701 Gandia (Valencia)', " +
                "-0.1720992, 38.9795949, " +
                TipoLugar.COMPRAS.ordinal() + ", " +
                "null, 962881070, 'http://www.lavital.es', " +
                "'El típico centro comercial.', " +
                System.currentTimeMillis() + ", 2.0)");
    }

    public static Lugar extraeLugar(Cursor cursor) {
        Lugar lugar = new Lugar();
        lugar.setNombre(cursor.getString(1));
        lugar.setDireccion(cursor.getString(2));
        lugar.setPosicion(new com.example.mislugares.modelo.GeoPunto(cursor.getDouble(3),
                cursor.getDouble(4)));
        lugar.setTipo(com.example.mislugares.modelo.TipoLugar.values()[cursor.getInt(5)]);
        lugar.setFoto(cursor.getString(6));
        lugar.setTelefono(cursor.getInt(7));
        lugar.setUrl(cursor.getString(8));
        lugar.setComentario(cursor.getString(9));
        lugar.setFecha(cursor.getLong(10));
        lugar.setValoracion(cursor.getFloat(11));
        return lugar;
    }

    public Cursor extraeCursor() {
        SharedPreferences pref =
                PreferenceManager.getDefaultSharedPreferences(contexto);
        String orden = pref.getString("orden", "creacion");
        String consulta;

        switch (orden) {
            case "0":
                consulta = "SELECT * FROM lugares ORDER BY fecha DESC";
                break;
            case "1":
                consulta = "SELECT * FROM lugares ORDER BY valoracion DESC";
                break;
            case "2":
                double lon = ((Aplicacion) contexto.getApplicationContext()).posicionActual.getLongitud();
                double lat = ((Aplicacion) contexto.getApplicationContext()).posicionActual.getLatitud();
                consulta = "SELECT * FROM lugares ORDER BY " +
                        "(" + lon + "-longitud)*(" + lon + "-longitud) + " +
                        "(" + lat + "-latitud)*(" + lat + "-latitud)";
                break;
            default:
                consulta = "SELECT * FROM lugares ORDER BY nombre DESC";
                break;
        }

        consulta += " LIMIT " + pref.getString("maximo", "12");
        SQLiteDatabase bd = getReadableDatabase();
        return bd.rawQuery(consulta, null);
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    @Override public Lugar elemento(int id) {
        Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT * FROM lugares WHERE _id = " + id, null);

        try {
            if (cursor.moveToNext()) {
                return extraeLugar(cursor);
            } else {
                throw new SQLException("Error al acceder al elemento _id = " + id);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    @Override public void anade(Lugar lugar) {

    }

    @Override public int nuevo() {
        int _id = -1;
        Lugar lugar = new Lugar();
        getWritableDatabase().execSQL("INSERT INTO lugares (nombre, direccion, longitud, latitud, tipo, foto, telefono, url, comentario, fecha, valoracion) VALUES ('', '', " +
                lugar.getPosicion().getLongitud() + ", " +
                lugar.getPosicion().getLatitud() + ", " +
                lugar.getTipo().ordinal() + ", " +
                "'', 0, '', '', " +
                lugar.getFecha() + ", 0)");
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT _id FROM lugares WHERE fecha = " + lugar.getFecha(), null);
        if (c.moveToNext()) _id = c.getInt(0);
        c.close();
        return _id;
    }
    @Override public void borrar(int id) {
        getWritableDatabase().execSQL("DELETE FROM lugares WHERE _id = " + id);
    }

    @Override
    public int tamano() {
        return 0;
    }

    @Override public void actualiza(int id, Lugar lugar) {
        getWritableDatabase().execSQL("UPDATE lugares SET " +
                "nombre = '" + lugar.getNombre() + "', " +
                "direccion = '" + lugar.getDireccion() + "', " +
                "longitud = " + lugar.getPosicion().getLongitud() + ", " +
                "latitud = " + lugar.getPosicion().getLatitud() + ", " +
                "tipo = " + lugar.getTipo().ordinal() + ", " +
                "foto = '" + lugar.getFoto() + "', " +
                "telefono = " + lugar.getTelefono() + ", " +
                "url = '" + lugar.getUrl() + "', " +
                "comentario = '" + lugar.getComentario() + "', " +
                "fecha = " + lugar.getFecha() + ", " +
                "valoracion = " + lugar.getValoracion() +
                " WHERE _id = " + id);
    }
}
