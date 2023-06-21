package com.flashex.socialmedia.view;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;


import com.flashex.socialmedia.databinding.ActivityUploadBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.UUID;

public class UploadActivity extends AppCompatActivity {

    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionResultLauncher;
    Uri imageData;
    private ActivityUploadBinding binding;

    private FirebaseAuth auth;
    private FirebaseStorage firebaseStorage;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUploadBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //calk func
        registerLauncher();//launcher permission

        //initialize
        auth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = firebaseStorage.getReference();
    }

    public void selectedImageClick(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //ask permission -> if click the user permission denny,click the snackbar is a button
                        permissionResultLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                }).show();
            } else {
                //ask permission
                permissionResultLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        } else {
            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intentToGallery);
        }
    }

    public void uploadButtonClick(View view) {
        if (imageData != null) {
            //uuid -> universal unique id
            UUID uuid = UUID.randomUUID();
            String imagePath = "images/" + uuid + ".jpg";

            storageReference.child(imagePath).putFile(imageData)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //Download uuid
                            StorageReference newReference = firebaseStorage.getReference(imagePath);
                            newReference.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            //image ui get
                                            String downloadUri = uri.toString();
                                            //get edit text post comment
                                            String comment = binding.editTextComment.getText().toString();
                                            //sign in user email
                                            FirebaseUser user = auth.getCurrentUser();
                                            String email = user.getEmail();

                                            HashMap<String, Object> postData = new HashMap<>();
                                            postData.put("useremail", email);
                                            postData.put("downloaduri", downloadUri);
                                            postData.put("comment", comment);
                                            postData.put("date", FieldValue.serverTimestamp());
                                            //now date -> FieldValue.serverTimestamp()

                                            //save firestore
                                            firebaseFirestore.collection("Posts").add(postData)
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {
                                                            Intent intent = new Intent(UploadActivity.this, FeedActivity.class);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                            startActivity(intent);
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }else {
            Toast.makeText(getApplicationContext(), "lütfen Bir Dosya Seçiniz", Toast.LENGTH_SHORT).show();
        }
    }

    private void registerLauncher() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    Intent intentFromResult = result.getData();
                    if (intentFromResult != null) {
                        imageData = intentFromResult.getData();
                        binding.imageView.setImageURI(imageData);
                    }
                }
            }
        });

        permissionResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result) {
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallery);
                } else {
                    Toast.makeText(UploadActivity.this, "Permission needed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}