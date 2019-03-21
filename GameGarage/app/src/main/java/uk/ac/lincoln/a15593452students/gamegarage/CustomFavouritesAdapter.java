package uk.ac.lincoln.a15593452students.gamegarage;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

// Custom Adapter for Favourites List View
public class CustomFavouritesAdapter extends ArrayAdapter<Game> {

    // Hold the passed games List
    private List<Game> gameList;

    // Constructor
    public CustomFavouritesAdapter(@NonNull Context context, ArrayList<Game> list) {
        super(context, R.layout.custom_layout, list);

        // Grab Packed Games List
        gameList = list;
    }

    // Custom ListView Behaviour
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // Inflate with custom XML
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View customView = layoutInflater.inflate(R.layout.custom_favourites_layout, parent, false);

        // Get packed Game Object by element[i]
        Game currentGame = gameList.get(position);

        // Grab handles
        final TextView gameText = (TextView) customView.findViewById(R.id.favourites_custom_game_title);
        final ImageView gameBanner = (ImageView) customView.findViewById(R.id.favourites_custom_game_image);

        // Unpack required information from List
        Picasso
                .with(getContext())
                .load(currentGame.getImageScreen())
                .fit()
                .into(gameBanner);

        gameText.setText(currentGame.getName());

        return customView;
    }

}
