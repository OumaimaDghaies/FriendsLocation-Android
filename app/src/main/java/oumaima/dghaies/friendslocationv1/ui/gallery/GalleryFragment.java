package oumaima.dghaies.friendslocationv1.ui.gallery;

import static androidx.core.location.LocationManagerCompat.getCurrentLocation;
import static androidx.core.location.LocationManagerCompat.isLocationEnabled;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import oumaima.dghaies.friendslocationv1.Config;
import oumaima.dghaies.friendslocationv1.JSONParser;
import oumaima.dghaies.friendslocationv1.MapsActivity;
import oumaima.dghaies.friendslocationv1.Position;
import oumaima.dghaies.friendslocationv1.R;
import oumaima.dghaies.friendslocationv1.databinding.FragmentGalleryBinding;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.btnInit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                binding.edLong.setText("");
                binding.edLatit.setText("");
                binding.edPseudo.setText("");


            }
        });

        binding.btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//1:::identifier cette demande dans la méthode onActivityResult()
                startActivityForResult(new Intent(getContext(), MapsActivity.class),1);


            }
        });

        binding.btnCurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation();

            }
        });

        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Vérifier que les champs ne sont pas vides
                String longitude = binding.edLong.getText().toString();
                String latitude = binding.edLatit.getText().toString();
                String pseudo = binding.edPseudo.getText().toString();

                if (longitude.isEmpty() || latitude.isEmpty() || pseudo.isEmpty()) {
                    Toast.makeText(getContext(), "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                } else {
                    // Démarrer la tâche asynchrone pour insérer les données
                    InsertTask insertTask = new InsertTask(getContext(), longitude, latitude, pseudo);
                    insertTask.execute();
                }
            }
        });
        return root;
    }


    private void getCurrentLocation() {
        // Check if location permission is granted
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permission if not granted
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        // FusedLocationProviderClient :: dernière position connue de l'appareil
        FusedLocationProviderClient mClient = LocationServices.getFusedLocationProviderClient(getContext());

        // Get last known location
        mClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                // Update EditText fields with current location
                binding.edLong.setText(String.valueOf(longitude));
                binding.edLatit.setText(String.valueOf(latitude));
            } else {
                Toast.makeText(getContext(), "Unable to retrieve current location", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Failed to get current location", Toast.LENGTH_SHORT).show();
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 ){

                double latitude = data.getDoubleExtra("latitude", 0.0);
                double longitude = data.getDoubleExtra("longitude", 0.0);

                // Mettre à jour les EditText avec les valeurs de latitude et de longitude
                binding.edLong.setText(String.valueOf(longitude));
                binding.edLatit.setText(String.valueOf(latitude));

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private static class InsertTask extends AsyncTask<Void, Void, Integer> {
        private Context context;
        private String longitude;
        private String latitude;
        private String pseudo;

        public InsertTask(Context context, String longitude, String latitude, String pseudo) {
            this.context = context;
            this.longitude = longitude;
            this.latitude = latitude;
            this.pseudo = pseudo;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            // Créer un nouveau parser JSON
            JSONParser parser = new JSONParser();

            // Créer un HashMap pour les paramètres
            HashMap<String, String> params = new HashMap<>();
            params.put("longitude", longitude);
            params.put("latitude", latitude);
            params.put("pseudo", pseudo);

            // Effectuer la requête HTTP POST pour insérer les données
            JSONObject response = parser.makeHttpRequest(Config.URL_Addposition, "POST", params);

            try {
                // Vérifier la réponse du serveur
                int success = response.getInt("success");
                return success;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return -1;
        }

        @Override
        protected void onPostExecute(Integer success) {
            super.onPostExecute(success);
            if (success == 1) {
                // L'insertion a réussi
                Toast.makeText(context, "Insertion réussie", Toast.LENGTH_SHORT).show();
            } else {
                // L'insertion a échoué
                Toast.makeText(context, "Échec de l'insertion", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
