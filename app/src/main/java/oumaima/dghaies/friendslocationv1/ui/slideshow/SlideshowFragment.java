package oumaima.dghaies.friendslocationv1.ui.slideshow;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import oumaima.dghaies.friendslocationv1.Config;
import oumaima.dghaies.friendslocationv1.JSONParser;
import oumaima.dghaies.friendslocationv1.MyRecyclerPositionAdapter;
import oumaima.dghaies.friendslocationv1.Position;
import oumaima.dghaies.friendslocationv1.R;
import oumaima.dghaies.friendslocationv1.databinding.FragmentSlideshowBinding;
import oumaima.dghaies.friendslocationv1.ui.home.HomeFragment;

public class SlideshowFragment extends Fragment {

    private FragmentSlideshowBinding binding;
    ListView lv_position;
    Context con;
    ArrayList<Position>data=new ArrayList<Position>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        RecyclerView recyclerView = root.findViewById(R.id.lvposition);
       Telechargement t = new Telechargement(SlideshowFragment.this.getActivity());
        t.execute();



        return root;
    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    class Telechargement extends AsyncTask {
        AlertDialog alert;
        Context con;

        public Telechargement(Context con){
            this.con=con;
        }

        @Override
        protected void onPreExecute() {
            //afficher la boite de dialog
            AlertDialog.Builder dialog = new AlertDialog.Builder(con);
            dialog.setTitle("telechargement");
            dialog.setMessage("veuillez patientez...");
            alert=dialog.create();
            alert.show();

        }

        @Override
        protected Object doInBackground(Object[] objects) {
            //cnx internet => execution du service
            JSONParser parser = new JSONParser();
            data.clear();
            JSONObject response = parser.makeHttpRequest(Config.URL_GET_ALL,"GET",null);

            try {
                int success=response.getInt("success");
                if (success==0){
                    String msg=response.getString("message");
                }else{
                    JSONArray tableau=response.getJSONArray("positions");
                    for (int i=0;i<tableau.length();i++){
                        JSONObject ligne=tableau.getJSONObject(i);
                        int id= ligne.getInt("idPosition");
                        String longitude= ligne.getString("longitude");
                        String latitude= ligne.getString("latitude");
                        String pseudo= ligne.getString("pseudo");
                        Position p =new Position(id,longitude,latitude,pseudo);
                        data.add(p);


                    }
                }

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return null;

        }

        @Override
        protected void onPostExecute(Object o) {//UIThread
            //ArrayAdapter<Position> adapter = new ArrayAdapter<>(con, android.R.layout.simple_list_item_1, data);
            //binding.lvposition.setAdapter(adapter);
            RecyclerView recyclerView = binding.lvposition;
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            MyRecyclerPositionAdapter adapter = new MyRecyclerPositionAdapter(requireContext(), data);
            recyclerView.setAdapter(adapter);

            // Fermez la boîte de dialogue de chargement



            alert.dismiss();
        }
    }
}

