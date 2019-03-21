package uk.ac.lincoln.a15593452students.gamegarage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SearchResultsActivity extends AppCompatActivity {

    // Volley Request Queue
    private RequestQueue mQueue;
    private GameScreenFragment fragment;

    // SharedPreferences: Cached Key
    String key = "CachedSearch";

    // Image Capture
    int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_search_results);

        Intent intent = getIntent();
        String searchedTerm = intent.getStringExtra("searchTerm");

        EditText searchBox = (EditText)findViewById(R.id.search_box_input);
        searchBox.setText(searchedTerm);

        View view = getLayoutInflater().inflate(R.layout.activity_search_results, null, false);

        jsonParse(view);
    }

    // Third Part Library: Volley
    public void jsonParse(View view) {

        mQueue = Volley.newRequestQueue(getApplicationContext());

        // Grab user's input term
        EditText userInput = (EditText)findViewById(R.id.search_box_input);
        final String searchTerm = userInput.getText().toString();

        // Use API Key
        final String api_key = "952e8d6dd89b8651a6162e99c2cef861219ab652";
        // Combine URL + Search Term + KEY
        // Append extra key words to 'field_list' to request more json information about search
        String url = "https://www.giantbomb.com/api/search/?api_key=" + api_key + "&format=json&field_list=name,image,guid,id,deck&query=\"" + searchTerm + "\"&resources=game";

        // Clear searchBox
        userInput.setHint(searchTerm);
        userInput.setText("");

        // Create/Reuse Search Result list for search results
        final ArrayList<Game> game_List = new ArrayList<>();

        // Get JSON object and parse through
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                    // RESPONSE LISTENER
                    @Override
                    public void onResponse(JSONObject response) {

                        // Flag for incomplete results
                        boolean omittedResults = false;

                        // Volley Connection
                        try {

                            // Get JSONArray from returned JSON
                            JSONArray jsonArray = response.getJSONArray("results");

                            // No results found handler
                            if(jsonArray.length() == 0)
                            {
                                // Toast and exit method
                                Toast.makeText(SearchResultsActivity.this, "No results found!", Toast.LENGTH_LONG).show();
                                return;
                            }

                            // Iterate each 'result' array element
                            for (int i = 0; i < jsonArray.length(); i++)
                            {
                                // Read Arrays into separate Object
                                JSONObject results = jsonArray.getJSONObject(i);
                                JSONObject imageArray = results.getJSONObject("image");

                                // Get required info from JSON
                                final String game_names = results.getString("name");
                                final String imageUrl = imageArray.getString("thumb_url");
                                final String imageScreenUrl = imageArray.getString("screen_url");
                                final String deck = results.getString("deck");
                                final int id = results.getInt("id");

                                // Omit incomplete results
                                if(game_names == "null" || game_names == "")
                                {
                                    // ...skip over them and flag bool
                                    omittedResults = true;
                                    continue;
                                }

                                // Pack each Game into an Object
                                game_List.add(new Game(game_names, imageUrl, imageScreenUrl, deck, id));
                                // Store all objects into List for Custom Adapter

                                // If List is empty, don't cache results
                                if(game_List.size() > 0) {
                                    // Make List Savable
                                    Gson gson = new Gson();
                                    String jsonGames = gson.toJson(game_List);

                                    // Save List for no internet cache
                                    SharedPreferences sharedPreferences = getSharedPreferences("cachedSearch", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();

                                    editor.putString(key, jsonGames);
                                    editor.commit();
                                }

                            }

                            // Display results in ListAdapter
                            ListToListAdapter(game_List);

                            // Notify User of Omitted results
                            if(omittedResults)
                                Toast.makeText(SearchResultsActivity.this, "Some results are omitted due to incomplete information!", Toast.LENGTH_LONG).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            // ERROR LISTENER
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

                // Display Toast
                Toast.makeText(getApplicationContext(), "Unable to retrieve results, displaying cached data!", Toast.LENGTH_LONG).show();

                // Get SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("cachedSearch", Context.MODE_PRIVATE);

                // Get Cached Data
                Gson gson = new Gson();
                String response = sharedPreferences.getString(key, "");

                // Covert Back to ArrayList
                final ArrayList<Game> list = gson.fromJson(response, new TypeToken<List<Game>>(){}.getType());

                ListToListAdapter(list);
            }
        });

        mQueue.add(request);
    }

    // Takes List and displays using List Adapter
    public void ListToListAdapter(final ArrayList<Game> game_List) {
        // Create Custom ListAdapter from CustomAdapter.class using compiled Game List
        final ListAdapter customAdapter =  new CustomAdapter(getApplicationContext(), game_List);

        // Grab handle and apply Custom ListView
        ListView resultsList = (ListView) findViewById(R.id.result_list_view);
        resultsList.setAdapter(customAdapter);

        // On Item Click Listener for Fragment
        resultsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Fragment Setup
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                // Fragment Bundle to pass game object
                Bundle fragmentBundle = new Bundle();
                fragmentBundle.putSerializable("GAME_OBJECT", game_List.get(position));

                // Fragment Creation
                fragment = new GameScreenFragment();
                fragment.setArguments(fragmentBundle);
                fragmentTransaction.add(R.id.fragment_profile_container, fragment);

                // Fragment Applying
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        });

    }

    public void onFavouritesPress(View view) {

        fragment.onFavouritesPress(view);

    }

    // Saves Game Profile Picture to internal storage
    public void onSaveImage(View view){

        // Grab Image and Title
        ImageView imageView = (ImageView)findViewById(R.id.game_profile_image);
        TextView textView = (TextView)findViewById(R.id.text_game_title);

        // Convert held image to Bitmap
        BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap imageToSave = (Bitmap)bitmapDrawable.getBitmap();

        // Set filename to save image file
        String filename = "ggimage.jpg";

        try{
            FileOutputStream fos;
            fos = openFileOutput(filename, Context.MODE_PRIVATE);
            imageToSave.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Toast.makeText(this, "Saved image to internal storage as " + filename + "!", Toast.LENGTH_SHORT).show();
    }

    public void checkPermissions(View view) {
        fragment.checkPermissions(view);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch(requestCode) {
            case 1: {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Starting Camera...", Toast.LENGTH_SHORT).show();

                    // Create Camera Intent
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);

                }
                else {
                    Toast.makeText(this, "Permission Denied! Try again!", Toast.LENGTH_SHORT).show();

                }
                return;
            }
            case 2: {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show();

                }
                else {
                    Toast.makeText(this, "Permission Denied! Try again!", Toast.LENGTH_SHORT).show();
                }

                return;
            }

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                return;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check Result is from Camera
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            // Get camera data
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            // Set image using taken picture
            ImageView imageView = (ImageView)findViewById(R.id.game_profile_image);
            imageView.setRotation(90);
            imageView.setImageBitmap(imageBitmap);
        }
    }
}
