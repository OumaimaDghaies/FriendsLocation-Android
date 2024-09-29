package oumaima.dghaies.friendslocationv1;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MyRecyclerPositionAdapter extends RecyclerView.Adapter<MyRecyclerPositionAdapter.MyViewHolder>{
    Context con;
    ArrayList<Position> data;
    private LatLng addedPosition;
    public MyRecyclerPositionAdapter( Context con, ArrayList<Position> data){
        this.con=con;
        this.data=data;
    }


    //view profill ili fihha supp mod call
    @NonNull
    @Override
    public MyRecyclerPositionAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //creation de la view
        //convertir code xml
        // v :: lineare layaout
        LayoutInflater inf=LayoutInflater.from(con);
        View v=inf.inflate(R.layout.view_position, null);
        return new MyViewHolder(v);
    }



    @Override
    public void onBindViewHolder(@NonNull MyRecyclerPositionAdapter.MyViewHolder holder, int position) {
        //modification des holders/view
        //recuperartion de la donnee
        Position p =data.get(position);
        //affecter le view/holder
        holder.tvname.setText(p.latitude);
        holder.tvlastname.setText(p.longitude);
        holder.tvnumber.setText(p.pseudo);


    }

    @Override
    public int getItemCount() {
        //nombre totale des views
        return data.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvname,tvlastname,tvnumber;
        ImageView imgdelete,imgcall,imgedite;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            //recupereation  des sous holhers / view
             tvname=itemView.findViewById(R.id.tvname_profil);
             tvlastname=itemView.findViewById(R.id.tvlastname_profil);
             tvnumber=itemView.findViewById(R.id.tvnumber_profil);

             imgdelete=itemView.findViewById(R.id.imageView_delete_profil);
             imgcall=itemView.findViewById(R.id.imageView_call_profil);


            //action sur les holdres
            imgdelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(con);
                    alert.setTitle("Suppression");
                    alert.setMessage("Confirmer la suppression");
                    alert.setPositiveButton("Confirmer", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int position = getAdapterPosition();
                            data.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, data.size());
                        }
                    });
                    alert.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alert.show();
                }
            });
            imgcall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Position currentPosition = data.get(position);
                        double latitude = Double.parseDouble(currentPosition.latitude);
                        double longitude = Double.parseDouble(currentPosition.longitude);

                        // Create an intent to open MapsActivity and pass the coordinates
                        Intent intent = new Intent(con, MapsActivity.class);
                        intent.putExtra("latitude", latitude);
                        intent.putExtra("longitude", longitude);
                        con.startActivity(intent);
                    }
                }
            });

        }


    }
        }
