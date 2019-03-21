package uk.ac.lincoln.a15593452students.gamegarage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity  {

    // Firebase Handle
    private Firebase firebase;

    // Video Player Handle
    private VideoView mVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        // Background Video
        mVideoView = (VideoView)findViewById(R.id.videoView);
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.video;
        Uri uri = Uri.parse(videoPath);
        mVideoView.setVideoURI(uri);
        mVideoView.start();

        // Video ViewMediaPlayer Listener
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setVolume(0f, 0f);
                mediaPlayer.setLooping(true);
            }
        });

        FillPastSearches();
        LoadSavedImage();

        // Initialise Firebase handle
        firebase = new Firebase();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        // Start Video again
        mVideoView.start();

        FillPastSearches();

        LoadSavedImage();
    }

    // Upon pressing the Search Button open next activity
    public void onSearchButtonPress(View view)
    {

        // Edit Text
        EditText editText = (EditText)findViewById(R.id.search_box_input);

        // Get SharedPreferences
        SharedPreferences pastSearches = getSharedPreferences("pastSearches", Context.MODE_PRIVATE);
        // Edit selected SharedPreferences
        SharedPreferences.Editor editor = pastSearches.edit();
        editor.putString("pastSearch", editText.getText().toString());
        editor.commit();

        // Start Intent
        Intent intent = new Intent(this, SearchResultsActivity.class);
        String searchTerm = editText.getText().toString();
        intent.putExtra("searchTerm", searchTerm);
        startActivity(intent);
    }

    // Opens the Favourites Fragment
    public void onFavouritesButtonPress(View view)
    {

        // Fragment Setup
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Fragment Creation
        FavouritesFragment fragment = new FavouritesFragment();
        fragmentTransaction.add(R.id.favourites_fragment_frame, fragment);

        // Fragment Applying
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    // Fills Past Search TextView with past search
    public void FillPastSearches()
    {
        // Fill Past Searches
        TextView tv = (TextView)findViewById(R.id.tv_past_searches);
        SharedPreferences sharedPreferences = getSharedPreferences("pastSearches", Context.MODE_PRIVATE);

        // Get string from SharedPreferences
        String temp = sharedPreferences.getString("pastSearch", "");

        // Set past search TextView
        if(temp == "")
            tv.setText("No past searches...");
        else
            tv.setText(temp);

    }

    // Load Saved Image from GameScreenFragment
    public void LoadSavedImage()
    {
        // Decodes Bitmap from bytes
        Bitmap bitmap = BitmapFactory.decodeFile("/data/data/uk.ac.lincoln.a15593452students.gamegarage/files/ggimage.jpg");

        // Sets ImageView Image to Bitmap
        ImageView imageView = (ImageView)findViewById(R.id.test_image);
        imageView.setImageBitmap(bitmap);

    }

    // Called on Upload Button Press
    public void onFirebaseDownload (View view) throws IOException
    {
        // Called Firebase Download Method
        firebase.DownloadImage(this);

        // Decode from bytes
        Bitmap bitmap = BitmapFactory.decodeFile("/data/data/uk.ac.lincoln.a15593452students.gamegarage/files/firebaseDownload.jpg");

        // Set ImageView Image to Bitmap
        ImageView imageView = (ImageView)findViewById(R.id.firebase_image);
        imageView.setImageBitmap(bitmap);

        Toast.makeText(this, "Firebase Download Complete", Toast.LENGTH_SHORT).show();
    }

    // Called on Download Button Press
    public void onFirebaseUpload(View view)
    {
        // Calls Firebase Upload Method
        firebase.UploadImage(this);
    }
}