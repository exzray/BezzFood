package com.example.bezzfood;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.bezzfood.model.ModelProfile;
import com.example.bezzfood.utility.Data;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private String md_title, md_message, md_uid;

    private Toolbar mv_toolbar;
    private ImageView mv_avatar;
    private EditText mv_name, mv_mobile, mv_address, mv_description;

    private FirebaseFirestore fb_firestore = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mv_toolbar = findViewById(R.id.toolbar);
        mv_avatar = findViewById(R.id.image_avatar);
        mv_name = findViewById(R.id.field_name);
        mv_mobile = findViewById(R.id.field_mobile);
        mv_address = findViewById(R.id.field_address);
        mv_description = findViewById(R.id.field_description);

        initUI();
    }

    public void onClickProfileSubmit(final View view){
        String name = mv_name.getText().toString().trim();
        String mobile = mv_mobile.getText().toString().trim();
        String address = mv_address.getText().toString().trim();
        String description = mv_description.getText().toString().trim();

        // disable button submit
        view.setEnabled(false);

        // show progress dialog
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.show();

        if (validateProfile()){
            ModelProfile profile = new ModelProfile();
            profile.setName(name);
            profile.setMobile(mobile);
            profile.setAddress(address);
            profile.setDescription(description);

            // upload profile into firestore
            fb_firestore
                    .collection(Data.FIRESTORE_KEY_USERS)
                    .document(md_uid)
                    .set(profile)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            // enable button submit
                            view.setEnabled(true);

                            // dismiss progress dialog
                            dialog.dismiss();

                            Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    private void initUI(){
        setSupportActionBar(mv_toolbar);

        // get extra data
        md_title = getIntent().getStringExtra(Data.EXTRA_PROFILE_TITLE);
        md_message = getIntent().getStringExtra(Data.EXTRA_PROFILE_MESSAGE);
        md_uid = getIntent().getStringExtra(Data.EXTRA_PROFILE_USER);

        updateUI();
    }

    private void updateUI(){
        if (!md_title.isEmpty()) setTitle(md_title);

        if (!md_message.isEmpty()) showMessageDialog(md_message);

        Toast.makeText(this, md_uid, Toast.LENGTH_LONG).show();
    }

    private boolean validateProfile(){
        String err_msg = "";

        String name = mv_name.getText().toString().trim();
        String mobile = mv_mobile.getText().toString().trim();
        String address = mv_address.getText().toString().trim();

        if (name.isEmpty() || mobile.isEmpty() || address.isEmpty())
            err_msg += "Some field still empty.\n";

        if (name.length() < Data.PASSWORD_MIN_LENGTH)
            err_msg += "Name character must more than " + Data.PASSWORD_MIN_LENGTH + "\n";

        if (!err_msg.isEmpty()){
            Toast.makeText(this, err_msg, Toast.LENGTH_LONG).show();

            return false;
        }

        return true;
    }

    private void showMessageDialog(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setPositiveButton("OK", null);
        builder.create().show();
    }
}
