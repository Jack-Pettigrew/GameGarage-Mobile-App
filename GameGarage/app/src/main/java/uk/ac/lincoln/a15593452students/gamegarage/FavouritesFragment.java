package uk.ac.lincoln.a15593452students.gamegarage;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

// Favourites List Fragment
public class FavouritesFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favourites_screen, container, false);

        // Instantiate DB
        FavouritesDBHandler favouritesDBHandler = new FavouritesDBHandler(getContext(), null, null, 1);

        // Get DB as List
        ArrayList<Game> gameFavouritesList = favouritesDBHandler.databaseToList();

        // Populate ListView with the GameList via Adapter
        ListAdapter favouritesAdapter = new CustomFavouritesAdapter(getContext(), gameFavouritesList);

        // Display ArrayList in List View via Adapter
        ListView favouritesList = (ListView) view.findViewById(R.id.favourites_list_view);
        favouritesList.setAdapter(favouritesAdapter);

        return view;
    }
}
