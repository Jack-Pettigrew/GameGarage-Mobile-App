package uk.ac.lincoln.a15593452students.gamegarage;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

// Class Dedicated to Firebase related methods
public class Firebase {

    // Firebase Storage Handles
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private StorageReference storageReference = firebaseStorage.getReferenceFromUrl("gs://gamegarage-3d41d.appspot.com/");

    // Uploads saved image to Firebase
    public void UploadImage(final Context context) {
        Uri file = Uri.fromFile(new File("/data/data/uk.ac.lincoln.a15593452students.gamegarage/files/ggimage.jpg"));
        StorageReference imagesRef = storageReference.child("files/firebaseUpload.jpg");

        imagesRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        Toast.makeText(context, "Firebase Upload Successful", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

                        Toast.makeText(context, "Firebase Upload Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Downloads Image from Firebase
    public void DownloadImage(final Context context) throws IOException {

        File localFile = new File("/data/data/uk.ac.lincoln.a15593452students.gamegarage/files/firebaseDownload.jpg");
        StorageReference imageRef = storageReference.child("files/firebaseUpload.jpg");

        imageRef.getFile(localFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(context, "Firebase Download Successful", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(context, "Firebase Download Failed", Toast.LENGTH_SHORT).show();
            }
        });


    }

}
