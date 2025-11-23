package com.example.mislugares.presentacion;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.mislugares.R;
import com.example.mislugares.datos.RepositorioLugares;
import com.example.mislugares.modelo.GeoPunto;
import com.example.mislugares.modelo.Lugar;

public class AdaptadorLugares extends RecyclerView.Adapter<AdaptadorLugares.ViewHolder> {

    protected RepositorioLugares lugares; //lista de lugares
    protected View.OnClickListener onClickListener; //listener de clics

    public AdaptadorLugares(RepositorioLugares lugares) {
        this.lugares = lugares;
    }

    public void setOnItemClickListener(View.OnClickListener listener){
        this.onClickListener = listener;
    }

    public void actualiza() {
        notifyDataSetChanged();
    }

    //vistas de cada elemento
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nombre, direccion;
        public ImageView foto;
        public RatingBar valoracion;
        public TextView distancia;


        public ViewHolder(View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.nombre);
            direccion = itemView.findViewById(R.id.direccion);
            foto = itemView.findViewById(R.id.foto);
            valoracion = itemView.findViewById(R.id.valoracion);
            distancia = itemView.findViewById(R.id.distancia);
        }

        public void personaliza(Lugar lugar) {
            nombre.setText(lugar.getNombre());
            direccion.setText(lugar.getDireccion());

            int idDrawable = android.R.drawable.ic_menu_help; //iconos por defecto -> pendiente cambiarlo si me toca como PIA
            switch (lugar.getTipo()) {
                case RESTAURANTE: idDrawable = android.R.drawable.ic_menu_compass; break;
                case BAR: idDrawable = android.R.drawable.ic_menu_share; break;
                case COPAS: idDrawable = android.R.drawable.ic_menu_slideshow; break;
                case ESPECTACULO: idDrawable = android.R.drawable.ic_menu_view; break;
                case HOTEL: idDrawable = android.R.drawable.ic_menu_mylocation; break;
                case COMPRAS: idDrawable = android.R.drawable.ic_menu_crop; break;
                case EDUCACION: idDrawable = android.R.drawable.ic_menu_manage; break;
                case DEPORTE: idDrawable = android.R.drawable.ic_menu_directions; break;
                case NATURALEZA: idDrawable = android.R.drawable.ic_menu_gallery; break;
                case GASOLINERA: idDrawable = android.R.drawable.ic_menu_mylocation; break;
            }

            foto.setImageResource(idDrawable);
            foto.setScaleType(ImageView.ScaleType.FIT_END);
            valoracion.setRating(lugar.getValoracion());

            GeoPunto pos = ((Aplicacion)itemView.getContext().getApplicationContext()).posicionActual;

            if (lugar.getPosicion() == null || lugar.getPosicion().equals(GeoPunto.SIN_POSICION)) {
                distancia.setText(""); //si es pag web no muestra nada
            } else if (pos.equals(GeoPunto.SIN_POSICION)) {
                distancia.setText("..."); //si no hay permiso de ubicacion
            } else {
                int d = (int) pos.distancia(lugar.getPosicion());
                if (d < 2000) distancia.setText(d + " m");
                else distancia.setText(d / 1000 + " Km");
            }

        }
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.elemento_lista, parent, false);
        v.setOnClickListener(onClickListener);
        return new ViewHolder(v);
    }

    @Override public void onBindViewHolder(ViewHolder holder, int position) {
        holder.personaliza(lugares.elemento(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener != null) {
                    v.setTag(holder.getAdapterPosition());
                    onClickListener.onClick(v);
                }
            }
        });
    }

    @Override public int getItemCount() {
        return lugares.tamano();
    }
}
