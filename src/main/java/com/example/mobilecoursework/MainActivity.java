package com.example.mobilecoursework;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    //GIF IMAGE view is found online. REf - https://github.com/koral--/android-gif-drawable

    MediaPlayer player;

    private static int SPLASH_TIME_OUT = 4000;
    private Switch switch_timer;
    private Intent intent;
    FirebaseAuth fAuth;
    Button login;
    TextView profileText;
    SoundPlayer soundPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        play();


        soundPlayer = new SoundPlayer(this);


        login = findViewById(R.id.btn_loginSignup);

        profileText = findViewById(R.id.game_Topic);
        switch_timer = findViewById(R.id.switch_timer);
        addListnerOnLoginbtn();
        fAuth = FirebaseAuth.getInstance();



    }

    public void play(){
        if(player==null){
            player=MediaPlayer.create(this,R.raw.home);
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlayer();
                }
            });
        }
        player.start();
    }


    public void stopPlayer(){
        if(player!=null){
            player.release();
            player=null;
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        fAuth = FirebaseAuth.getInstance();

        if(fAuth.getCurrentUser() != null){
            login.setText("Logout");
            String nick_name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
            loginSignup.setUserId(nick_name);
            profileText.setText(nick_name);
        }else{
            login.setText("Login");
            profileText.setText("Guest");

        }
    }

    public void logOut(){
        FirebaseAuth.getInstance().signOut();
        loginSignup.setUserId(null);
        onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPlayer();
    }

    public void openGuessTheBreed(View view) {
        intent = new Intent(this,GuessBreed.class);
        if(switch_timer.isChecked()) {
            intent.putExtra("timer",true);
        }else{
            intent.putExtra("timer",false);
        }
        startActivity(intent);

    }

    public void openGuessTheDog(View view) {
        Intent intent = new Intent(this,GuessDog.class);
        if(switch_timer.isChecked()) {
            intent.putExtra("timer",true);
        }else{
            intent.putExtra("timer",false);
        }
        startActivity(intent);
    }

    public void openSearchBreed(View view) {
        Intent intent = new Intent(this,slideShow.class);
        startActivity(intent);
    }

    public void openScoreBoard(View view) {
        Intent intent = new Intent(this,scoreDetails.class);
        startActivity(intent);
    }

    public void openLoginSignup() {
        Intent intent = new Intent(this,loginSignup.class);
        startActivity(intent);
    }


    public void addListnerOnLoginbtn(){
        login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(login.getText().equals("Login")) {
                    openLoginSignup();

                }else{
                    logOut();
                }
            }
        });

    }


    public void playPause(View view) {
        if(player!=null){
            stopPlayer();
        }else{
            play();
        }
    }
}
