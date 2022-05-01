package com.example.instagram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {
    EditText fullNames, username, user_email, phone;
    AppCompatButton save;
    private CircleImageView dp;
    private FirebaseAuth sAuth;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference documentReference;
    String User;
    private Uri resultUri;

    private static final int gallery_pick =1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        sAuth = FirebaseAuth.getInstance();
        User= sAuth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        documentReference = firebaseFirestore.collection("Users").document(User);

        fullNames = findViewById(R.id.fullnames);
        username = findViewById(R.id.username);
        user_email = findViewById(R.id.user_email);
        phone = findViewById(R.id.phone);

        save = findViewById(R.id.save);
        save.setOnClickListener(view -> saveUserDetails());

        dp = findViewById(R.id.setup_profile);
        dp.setOnClickListener(view -> {
            Intent gallery = new Intent();
            gallery.setType("image/*");
            gallery.setAction(Intent.ACTION_PICK);
            startActivityForResult(gallery, gallery_pick);

        });
        DocumentReference documentReference = firebaseFirestore.collection("Users").document(User);
        documentReference.addSnapshotListener(this, (value, error) -> {
            if (value.exists()){
                Glide.with(getApplicationContext()).load(value.getString("profile_img_url")).placeholder(R.drawable.profile_placeholder).into(dp);
            }

        });
    }

    private void saveUserDetails() {
        String txt_name = fullNames.getText().toString();
        String txt_username = username.getText().toString();
        String txt_user_email = user_email.getText().toString();
        String num_phone = phone.getText().toString();

        if (TextUtils.isEmpty(txt_name)|TextUtils.isEmpty(txt_username)|TextUtils.isEmpty(txt_user_email)|TextUtils.isEmpty(num_phone)) {
            fullNames.setError("Empty field!");
            username.setError("Empty Field");
            user_email.setError("Cannot be Empty");
            phone.setError("Enter a Valid phone number");
        }
        else {
            HashMap users = new HashMap();
            users.put("Fullname", txt_name);
            users.put("Username", txt_username);
            users.put("Email", txt_user_email);
            users.put("Phone", num_phone);
            users.put("Search", txt_name.toLowerCase());
            
            
            documentReference.set(users).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()){
                    Toast.makeText(SetupActivity.this, "Welcome!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SetupActivity.this, MainActivity.class));
                    finish();
                }
                else{
                    Toast.makeText(this, "Failed!"+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        if (resultUri!= null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Profile Images").child(User);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
            } catch (IOException exception){
                exception.printStackTrace();
            }
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,20, byteArrayOutputStream);

            byte [] data = byteArrayOutputStream.toByteArray();
            UploadTask uploadTask = storageReference.putBytes(data);
            uploadTask.addOnFailureListener(this, e -> {
                finish();
                return;
            });
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                if (taskSnapshot.getMetadata()!=null) {
                    if (taskSnapshot.getMetadata().getReference()!=null){
                        Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                        result.addOnSuccessListener(uri -> {
                            String imgUrl = uri.toString();
                            Map newImage =new HashMap();
                            newImage.put("profile_img_url", imgUrl);
                            finish();
                            return;
                        });
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1&& resultCode== Activity.RESULT_OK){
            final Uri imageUri = data.getData();
            resultUri=imageUri;
            dp.setImageURI(resultUri);
        }
    }
}