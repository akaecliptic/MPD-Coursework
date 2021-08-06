package org.me.gcu.equake.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.me.gcu.equake.Model.EQUAKE;
import org.me.gcu.equake.R;

import static org.me.gcu.equake.Utility.EQuakeUtility.getStrengthHue;

/**
 * Developed by: Michael A. F.
 */
public class EQUAKEFragment extends Fragment implements OnMapReadyCallback {

    public static final String EQUAKE_TAG = "incoming";
    private EQUAKE working;

    private TextView textLocation;
    private TextView textTime;
    private TextView textMagnitude;
    private TextView textDepth;
    private TextView textCoordinates;
    private TextView textLink;
    private ImageView imgExit;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_equake, container, false);
    }

    private void incoming() {
        Bundle bundle = getArguments();
        Object obj = bundle.get(EQUAKE_TAG);

        if(obj instanceof EQUAKE){
            working = (EQUAKE) obj;
        }else {
            Navigation.findNavController(requireView()).popBackStack();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        incoming();

        initViews();
        initListeners();
        assignViews();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        setMapSettings(googleMap);
        LatLng location = new LatLng(working.getLatitude(), working.getLongitude());
        googleMap.addMarker(new MarkerOptions()
                .position(location)
                .icon(BitmapDescriptorFactory.defaultMarker(getStrengthHue(working.getMagnitude())))
                .title(working.getDisplayLocation())
        );
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 6));
    }

    private void setMapSettings(GoogleMap googleMap) {
        UiSettings settings = googleMap.getUiSettings();
        settings.setZoomControlsEnabled(true);
        settings.setZoomGesturesEnabled(false);
        settings.setRotateGesturesEnabled(false);
        settings.setTiltGesturesEnabled(false);
    }

    private void initViews() {
        textLocation = requireView().findViewById(R.id.ef_top);
        textTime = requireView().findViewById(R.id.ef_time);
        textMagnitude = requireView().findViewById(R.id.ef_magnitude);
        textDepth = requireView().findViewById(R.id.ef_depth);
        textCoordinates = requireView().findViewById(R.id.ef_coordinates);
        textLink = requireView().findViewById(R.id.ef_link);

        imgExit = requireView().findViewById(R.id.ef_exit);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.ef_map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void assignViews() {
        String time = "Origin: " + working.getTime();
        String magnitude = "Magnitude: " + working.getMagnitude();
        String depth = "Depth: " + working.getDepth() + "km";

        textLocation.setText(working.getLocation());
        textLocation.getBackground().setLevel((int)Math.ceil(working.getMagnitude()));
        textTime.setText(time);
        textMagnitude.setText(magnitude);
        textDepth.setText(depth);
        textCoordinates.setText(working.getCoordinates());

        textLink.setTextColor(Color.BLUE);
    }

    private void initListeners() {
        imgExit.setOnClickListener(view -> Navigation.findNavController(requireView()).popBackStack());
        textLink.setOnClickListener(view -> Toast.makeText(requireContext(), "Hold Link to Open Browser.", Toast.LENGTH_SHORT).show());

        textLink.setOnLongClickListener(view -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(working.getLink()));
            startActivity(browserIntent);
            return true;
        });
    }
}
