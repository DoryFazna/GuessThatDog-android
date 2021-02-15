package com.example.mobilecoursework;

import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class saveToFireBase {



    public static void save(String userName, final int score, final String levelID) {
        DatabaseReference dbf = FirebaseDatabase.getInstance().getReference().child("Scores").child(userName);



        dbf.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    dataSnapshot.getRef().child(levelID).setValue(score);
                }else{
                    DatabaseReference dbf=FirebaseDatabase.getInstance().getReference().child("Scores");
                    Player player= new Player();
                    player.setUserName(loginSignup.getUserId());
                    if(levelID.equals("lev1score"))
                        player.setLev1score(score);
                    else
                        player.setLev2score(score);

                    dbf.child(player.getUserName()).setValue(player);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


    }




}
