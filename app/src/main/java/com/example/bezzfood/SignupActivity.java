package com.example.bezzfood;

import android.app.ProgressDialog;
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

import static com.example.bezzfood.utility.Data.PASSWORD_MIN_LENGTH;
import static com.example.bezzfood.utility.Data.URL_IMAGE_1;

public class SignupActivity extends AppCompatActivity {

    private View mv_root;
    private ImageView mv_logo;
    private EditText mv_email, mv_password, mv_confirm;

    private FirebaseAuth fb_auth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mv_root = findViewById(R.id.root);
        mv_logo = findViewById(R.id.image_logo);
        mv_email = findViewById(R.id.field_email);
        mv_password = findViewById(R.id.field_password);
        mv_confirm = findViewById(R.id.field_confirm);

        initUI();
    }

    public void onClickSubmit(final View view) {
        String email = mv_email.getText().toString().trim();
        String password = mv_password.getText().toString().trim();

        if (validateRegister()) {

            // disable button submit
            view.setEnabled(false);

            // show progress dialog
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("Please wait...");
            dialog.setCancelable(false);
            dialog.show();

            fb_auth
                    .createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            // enable button submit
                            view.setEnabled(true);

                            // dismiss progress dialog
                            dialog.dismiss();

                            if (task.isSuccessful()) {
                                onBackPressed();
                                finish();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    private void initUI() {
        Glide.with(this).load(URL_IMAGE_1).into(mv_logo);
    }

    private boolean validateRegister() {
        String email = mv_email.getText().toString().trim();
        String password = mv_password.getText().toString().trim();
        String confirm = mv_confirm.getText().toString().trim();
        String err_msg = "";

        if (email.isEmpty() || password.isEmpty() || confirm.isEmpty())
            err_msg += "Some field still empty.\n";

        if (password.length() < PASSWORD_MIN_LENGTH)
            err_msg += "Password must atleast " + PASSWORD_MIN_LENGTH + " characters.\n";

        if (!password.equals(confirm))
            err_msg += "Confirmation password is not same.\n";

        if (!err_msg.isEmpty()) {
            Snackbar.make(mv_root, err_msg, Snackbar.LENGTH_LONG).show();

            return false;
        }

        return true;
    }
}
