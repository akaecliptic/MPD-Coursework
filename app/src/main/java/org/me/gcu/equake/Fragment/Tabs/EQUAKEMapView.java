package org.me.gcu.equake.Fragment.Tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.me.gcu.equake.Architecture.EQUAKEViewModel;
import org.me.gcu.equake.Model.EQUAKE;
import org.me.gcu.equake.R;

import java.util.List;
import java.util.Optional;

import static org.me.gcu.equake.Fragment.EQUAKEFragment.EQUAKE_TAG;
import static org.me.gcu.equake.Utility.EQuakeUtility.getStrengthHue;

/**
 * Developed by: Michael Adebayo Fatoye
 * Student ID: S1718017
 */
public class EQUAKEMapView extends Fragment implements OnMapReadyCallback {

    private GoogleMap equakeMap;
    private EQUAKEViewModel model;
    private List<EQUAKE> working;
    private static final double[] CENTER_OF_UK = { 54f, -2.5f };

    private String lastClick = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        model = new ViewModelProvider(requireActivity()).get(EQUAKEViewModel.class);
        working = model.getWorking();
        return inflater.inflate(R.layout.main_equake_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initViews();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        equakeMap = googleMap;
        setMapSettings();
        setMapListeners();

        working.forEach(this::createMarker);
        equakeMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(CENTER_OF_UK[0], CENTER_OF_UK[1]), 5));
    }

    private void setMapSettings() {
        UiSettings settings = equakeMap.getUiSettings();
        settings.setZoomControlsEnabled(true);
        settings.setTiltGesturesEnabled(false);
    }

    private void setMapListeners() {
        equakeMap.setOnMarkerClickListener(marker -> {
            if (!lastClick.equals(marker.getTitle())){
                Toast.makeText(requireContext(), "Click Again to Open EQuake View.", Toast.LENGTH_SHORT).show();
                lastClick = marker.getTitle();
                return false;
            }

            Optional<EQUAKE> checking = working
                    .stream()
                    .filter(equake -> equake.getDisplayLocation().equals(marker.getTitle()))
                    .findFirst();

            if (!checking.isPresent())
                return false;

            Bundle bundle = new Bundle();
            bundle.putSerializable(EQUAKE_TAG, checking.get());

            Toast.makeText(requireContext(), "Opening ' " + checking.get().getDisplayLocation() + "'", Toast.LENGTH_SHORT).show();

            NavController navController = Navigation.findNavController(requireView());
            navController.navigate(R.id.action_main_to_equake, bundle);
            return true;
        });
    }

    private void initViews() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.main_map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void createMarker(EQUAKE equake) {
        LatLng location = new LatLng(equake.getLatitude(), equake.getLongitude());
        equakeMap.addMarker(new MarkerOptions()
                .position(location)
                .icon(BitmapDescriptorFactory.defaultMarker(getStrengthHue(equake.getMagnitude())))
                .title(equake.getDisplayLocation())
        );
    }
}
