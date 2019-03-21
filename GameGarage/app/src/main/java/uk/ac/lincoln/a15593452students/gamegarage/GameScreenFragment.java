package uk.ac.lincoln.a15593452students.gamegarage;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

public class GameScreenFragment extends Fragment implements OnMapReadyCallback {

    // Handles
    private FavouritesDBHandler favouritesDBHandler;
    private Game game;
    private GoogleMap mMap;
    private MapView mapView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        favouritesDBHandler = new FavouritesDBHandler(getContext(), null, null, 1);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_game__screen_, container, false);

        // Prevent underneath stacked fragment/activity from being clicked
        view.setClickable(true);

        // Grab passed game object
        Bundle receivedBundle = getArguments();
        game = (Game) receivedBundle.getSerializable("GAME_OBJECT");

        // Handles
        ImageView imageView_gameProfileBanner = (ImageView) view.findViewById(R.id.game_banner_image);
        ImageView imageView_gameProfileImage = (ImageView) view.findViewById(R.id.game_profile_image);
        TextView tv_gameTitle = (TextView) view.findViewById(R.id.text_game_title);
        TextView tv_gameDeck = (TextView) view.findViewById(R.id.text_deck_descrip);

        // Apply to Profile Views
        Picasso.with(getContext())
                .load(game.getImage())
                .into(imageView_gameProfileImage);

        Picasso.with(getContext())
                .load(game.getImageScreen())
                .fit()
                .centerCrop()
                .into(imageView_gameProfileBanner);

        tv_gameTitle.setText(game.getName());
        tv_gameDeck.setText(game.getDeck());

        // MapView
        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        // Get Map
        mapView.getMapAsync(this);

        return view;
    }


    // Adds game to favourites
    public void onFavouritesPress(View view) {

        favouritesDBHandler.addGame(game);
        Toast.makeText(getContext(), game.getName() + " has been added to your favourites!", Toast.LENGTH_SHORT).show();

    }

    // Camera Permissions Method
    public void checkPermissions(View view) {

        // Checks for Camera Permissions
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 1);

    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Initialise Map
        mMap = googleMap;

        // Check if App has permissions
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Ask for permission
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);

            return;
        }

        SetupMap();
    }

    // Sets up the Map upon granted permission
    public void SetupMap()
    {
        mMap.setMyLocationEnabled(true);

        // GAME LatLng
        LatLng latLng = new LatLng(53.227668, -0.540038);

        // Create instantiate Marker
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.draggable(false);
        markerOptions.title("GAME");

        MapsInitializer.initialize(getActivity());
        // Create Marker at pos
        mMap.addMarker(markerOptions);
        // Position Map Camera to pos
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, 100, 0, 0)));
    }
}
