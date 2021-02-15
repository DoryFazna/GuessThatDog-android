package com.example.mobilecoursework;

import android.app.Dialog;
import android.content.Context;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static com.example.mobilecoursework.R.id.playerInfo;

public class TheGame {

    public static String randBreedLevel1;

    static ArrayList<String> usedImages= new ArrayList<>();



    //To store Breed image names
    static List<String> breedNameList = new ArrayList<>(Arrays.asList("boston_bull","briard","chihuahua","clumber","collie","golden_retriever","irish_terrier","lhasa","pug","vizsla"));
    static Random rand = new Random();




    public static String generateRandomImageID(String breed){
        String randBreed=breed;

        while (randBreed.equals(breed)) {
            randBreed = breedNameList.get(rand.nextInt(10));
        }
        randBreedLevel1=randBreed;
        int randImageNo = rand.nextInt(10);
        String randomID = randBreed+"_"+randImageNo;
        return randomID;

    }


    public static String generateRandomImageID2(String breed){
        String randBreed;
        int randImageNo;
        String randomID="starter";


        while(usedImages.contains(randomID)) {
            randImageNo = rand.nextInt(10);
            randomID = breed+"_"+randImageNo;
        }
        usedImages.add(randomID);

        return randomID;

    }










}
