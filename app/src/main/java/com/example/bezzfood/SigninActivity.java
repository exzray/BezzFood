package com.example.bezzfood;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.example.bezzfood.utility.Data.PASSWORD_MIN_LENGTH;
import static com.example.bezzfood.utility.Data.URL_IMAGE_1;

public class SigninActivity extends AppCompatActivity {

    private View mv_root;
    private ImageView mv_logo;
    private EditText mv_email, mv_password;

    private FirebaseAuth fb_auth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        mv_root = findViewById(R.id.root);
        mv_logo = findViewById(R.id.image_logo);
        mv_email = findViewById(R.id.field_email);
        mv_password = findViewById(R.id.field_password);

        initUI();
    }

    @Override
    protected void onStart() {
        super.onStart();

        updateUI(fb_auth.getCurrentUser());
    }

    public void onClickSignup(View view) {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }

    public void onClickSubmit(final View view) {
        String email = mv_email.getText().toString().trim();
        String password = mv_password.getText().toString().trim();

        if (validateLogin()) {
            // disable button submit
            view.setEnabled(false);

            // show progress dialog
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("Please wait...");
            dialog.setCancelable(false);
            dialog.show();

            fb_auth
                    .signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            // enable button submit
                            view.setEnabled(true);

                            // dismiss progress dialog
                            dialog.dismiss();

                            if (task.isSuccessful()) {
                                // remove annoying message
                                assert  task.getResult() != null;
                                updateUI(task.getResult().getUser());
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SigninActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void initUI() {
        Glide.with(this).load(URL_IMAGE_1).into(mv_logo);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private boolean validateLogin() {
        String email = mv_email.getText().toString().trim();
        String password = mv_password.getText().toString().trim();

        String err_msg = "";

        if (email.isEmpty() || password.isEmpty())
            err_msg += "Some field still empty.\n";

        if (password.length() < PASSWORD_MIN_LENGTH)
            err_msg += "Password must atleast " + PASSWORD_MIN_LENGTH + " characters.\n";

        if (!err_msg.isEmpty()) {
            Snackbar.make(mv_root, err_msg, Snackbar.LENGTH_LONG).show();

            return false;
        }

        return true;
    }
}
