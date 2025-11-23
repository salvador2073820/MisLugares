package com.example.mislugares.presentacion;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mislugares.R;
import com.example.mislugares.casos_uso.CasosUsoLugar;
import com.example.mislugares.datos.LugaresBDAdapter;
import com.example.mislugares.modelo.Lugar;
import com.example.mislugares.modelo.TipoLugar;

import android.widget.AdapterView;

public class EdicionLugarActivity extends AppCompatActivity {

    private EditText nombre;
    private Spinner tipo;
    private TextView tipoSeleccionado;
    private EditText direccion;
    private EditText telefono;
    private EditText url;
    private EditText comentario;
    private LugaresBDAdapter lugares;
    private CasosUsoLugar usoLugar;
    private int pos = -1;
    private int id = -1;
    private Lugar lugar;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edicion_lugar);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            pos = extras.getInt("pos", -1);
            id = extras.getInt("id", -1);
        }

        lugares = ((Aplicacion) getApplication()).lugares;
        usoLugar = new CasosUsoLugar(this, lugares);

        if (id != -1) {
            lugar = lugares.elemento(id);
        } else {
            lugar = lugares.elementoPos(pos);
        }

        //campos de edicion
        nombre = findViewById(R.id.nombre);
        tipo = findViewById(R.id.tipo);
        tipoSeleccionado = findViewById(R.id.tipo_seleccionado);
        direccion = findViewById(R.id.direccion);
        telefono = findViewById(R.id.telefono);
        url = findViewById(R.id.url);
        comentario = findViewById(R.id.comentario);

        actualizaVistas();
        configurarSpinner();
    }

    private void configurarSpinner() {
        ArrayAdapter<String> adaptador = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                TipoLugar.getNombres()
        );
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tipo.setAdapter(adaptador);

        tipo.setSelection(lugar.getTipo().ordinal());
        tipoSeleccionado.setText(TipoLugar.getNombres()[lugar.getTipo().ordinal()]);

        tipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                tipoSeleccionado.setText(TipoLugar.getNombres()[position]);
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {
                tipoSeleccionado.setText("ninguno");
            }
        });
    }

    public void actualizaVistas() {
        nombre.setText(lugar.getNombre());
        direccion.setText(lugar.getDireccion());
        if (lugar.getTelefono() != 0) {
            telefono.setText(Integer.toString(lugar.getTelefono()));
        }
        url.setText(lugar.getUrl());
        comentario.setText(lugar.getComentario());
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edicion_lugar, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.accion_guardar:
                lugar.setNombre(nombre.getText().toString());
                lugar.setTipo(TipoLugar.values()[tipo.getSelectedItemPosition()]);
                lugar.setDireccion(direccion.getText().toString());
                try {
                    lugar.setTelefono(Integer.parseInt(telefono.getText().toString()));
                } catch (NumberFormatException e) {
                    lugar.setTelefono(0);
                }
                lugar.setUrl(url.getText().toString());
                lugar.setComentario(comentario.getText().toString());

                int _id = id;
                if (_id == -1) {
                    _id = lugares.getAdaptador().idPosicion(pos);
                }
                usoLugar.guardar(_id, lugar);
                finish();
                return true;

            case R.id.accion_cancelar:
                if (id != -1) usoLugar.borrar(id);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}