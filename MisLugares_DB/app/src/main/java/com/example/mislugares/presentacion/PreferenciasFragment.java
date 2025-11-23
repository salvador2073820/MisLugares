package com.example.mislugares.presentacion;

import android.os.Bundle;
import android.widget.Toast;

import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.mislugares.R;

public class PreferenciasFragment extends PreferenceFragmentCompat {

    @Override public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferencias, rootKey);

        //preferencia maximo elementos
        EditTextPreference lugares = findPreference("maximo");
        if (lugares != null) {

            inicializarResumen(lugares);
            lugares.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override public boolean onPreferenceChange(Preference preference, Object newValue) {
                    String valorNuevo = (String) newValue;
                    int valor;
                    try {
                        valor = Integer.parseInt(valorNuevo);
                    } catch (NumberFormatException e) {
                        Toast.makeText(getActivity(), "Debe de ser un número", Toast.LENGTH_SHORT).show();
                        return false; //no actualizar el valor si esta vacio
                    }
                    //limitar rango elementos
                    if (valor < 0 || valor > 99) {
                        Toast.makeText(getActivity(), "Valor Máximo 99", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    //actualizar resumen
                    preference.setSummary("Cantidad de valores que se muestran (" + valor + ")");
                    return true;
                }
            });
        }
    }

    private void inicializarResumen(EditTextPreference pref) {
        String valor = pref.getText(); //valor guardado
        if (valor != null) {
            pref.setSummary("Cantidad de valores que se muestran (" + valor + ")");
        }
    }
}
