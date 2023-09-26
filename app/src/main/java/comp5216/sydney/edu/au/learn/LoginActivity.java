package comp5216.sydney.edu.au.learn;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import comp5216.sydney.edu.au.learn.util.toastUtil;

public class LoginActivity extends AppCompatActivity {

    private Button loginBtn;
    private Button registerBtn;
    private EditText userName;
    private EditText password;
    private FirebaseAuth auth;
    private LinearLayout login_container;

    private View imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginBtn = findViewById(R.id.btnLogin);
        userName = findViewById(R.id.etUsername);
        password = findViewById(R.id.etPassword);
        login_container = findViewById(R.id.buttonView);
        imageView = findViewById(R.id.imageView);
        registerBtn = findViewById(R.id.register);
        // initial page，hidden form
        login_container.setVisibility(View.GONE);

        // start anime
        fadeInLogo();
        // get firebase
        auth = FirebaseAuth.getInstance();

        loginBtn.setOnClickListener(this::onClick);

        registerBtn.setOnClickListener(this::toRegisterClick);
    }

    private void fadeInLogo() {
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(imageView, "alpha", 0f, 1f);
        fadeIn.setDuration(1000);

        fadeIn.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                login_container.setVisibility(View.VISIBLE);
            }
        });

        fadeIn.start();
    }
    private void toRegisterClick(View view){
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }
    private void onClick(View view){
       String userNameUse = userName.getText().toString();
       String passwordUse = password.getText().toString();

       // set toast
        String ok = "login successed";
        String fail = "wrong password or username";

        if(!userNameUse.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(userNameUse).matches()){
            if(!passwordUse.isEmpty()){
                auth.signInWithEmailAndPassword(userNameUse, passwordUse)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {

                                toastUtil.showToast(LoginActivity.this, ok);

                                // get the user information
                                FirebaseUser user = auth.getCurrentUser();

                                if (user != null) {
                                    String uid = user.getUid(); // get User UID

                                    // get SharedPreferences instance
                                    SharedPreferences sharedPreferences = getSharedPreferences("comp5216", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("userId", uid);

                                    editor.apply();

                                }


                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setTitle("Login failed")
                                        .setMessage("Wrong password or account number")
                                        .setPositiveButton("confirm", (dialogInterface, i) -> {

                                        });

                                builder.create().show();

                            }
                        });
            }else {
                password.setError("password can`t be null");
            }
        } else if (userNameUse.isEmpty()) {
//            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            userName.setError("Email can`t be empty");
        }else {
            userName.setError("Email is not valid");
        }

    }
}