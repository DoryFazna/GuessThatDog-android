package com.example.mobilecoursework;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class loginSignup extends AppCompatActivity {

    EditText userName,passWord,nickName;
    Button login_btn, register_btn;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    TextView login_question;

    static String userId;

    DatabaseReference dbf;

    public static void setUserId(String userId) {
        loginSignup.userId = userId;
    }

    public static String getUserId() {
        return userId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_signup);


        userName = findViewById(R.id.userName);
        passWord = findViewById(R.id.password);
        nickName = findViewById(R.id.nickName);
        login_btn = findViewById(R.id.login_btn);
        register_btn = findViewById(R.id.register_btn);
        fAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);
        register_btn.setVisibility(View.INVISIBLE);

        login_question = findViewById(R.id.login_question);
        nickName.setVisibility(View.INVISIBLE);




        ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) { //register mode
                    login_btn.setEnabled(false);
                    register_btn.setVisibility(View.VISIBLE);
                    nickName.setVisibility(View.VISIBLE);
                    login_question.setText("Already have an accout?");

                } else { // login mode

                    login_btn.setEnabled(true);
                    register_btn.setVisibility(View.INVISIBLE);
                    login_question.setText("Don't have an accout?");
                    nickName.setVisibility(View.INVISIBLE);

                }
            }
        });


        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_name = userName.getText().toString();
                String pass_word = passWord.getText().toString();

                if(user_name.isEmpty()){
                    userName.setError("Please enter username");
                }else if(pass_word.isEmpty()){
                    passWord.setError("please enter password");

                }else{

                    progressBar.setVisibility(View.VISIBLE);

                    fAuth.signInWithEmailAndPassword(user_name,pass_word).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                String nick_name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                                System.out.println(nick_name);
                                userId= nick_name;
                                Toast.makeText(loginSignup.this,"Logged in Successfully", Toast.LENGTH_LONG).show();
                                finish();


                            }else{
                                Toast.makeText(loginSignup.this,"Some error has occured"+task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.INVISIBLE);
                            }

                        }
                    });

                }

            }
        });





        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_name = userName.getText().toString();
                String pass_word = passWord.getText().toString();

                if(user_name.isEmpty()){
                    userName.setError("Please enter username");
                }else if(pass_word.isEmpty()){
                    passWord.setError("please enter password");

                }else{

                    progressBar.setVisibility(View.VISIBLE);

                    fAuth.createUserWithEmailAndPassword(user_name,pass_word).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                String nick_name = nickName.getText().toString();
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(nick_name).build();
                                user.updateProfile(profileUpdates);
                                userId= nick_name;
                                Toast.makeText(loginSignup.this,"Logged in Successfully", Toast.LENGTH_LONG).show();
                                finish();

                            }else{
                                Toast.makeText(loginSignup.this,"Some error has occured"+task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.INVISIBLE);
                            }

                        }
                    });

                    }
            }
        });

    }



}
