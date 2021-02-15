package com.example.mobilecoursework;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static android.R.*;

public class slideShow extends AppCompatActivity {

    ViewFlipper vFlipper;
    AutoCompleteTextView textView;
    Button btn_submit;
    Button btn_stop;
    String userSearchBreed;
    boolean isRunning;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_show);
        textView = findViewById(R.id.searchBreed);
        // Get the string array
        String[] countries = getResources().getStringArray(R.array.breed_names_array);
        // Create the adapter and set it to the AutoCompleteTextView
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, countries);
        textView.setAdapter(adapter);

        btn_submit = findViewById(R.id.search_button);
        btn_stop = findViewById(R.id.stop_button);

        vFlipper = findViewById(R.id.v_flipper);


        if (savedInstanceState != null) {
            Bundle bundle = savedInstanceState.getBundle("x");
            userSearchBreed=bundle.getString("searchedBreed");
            isRunning=bundle.getBoolean("isRunning");
            if(isRunning) {
                onSearch();
                updateAndRunFlipper();
            }
            else
                reset();
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle bundle = new Bundle();
        bundle.putString("searchedBreed",userSearchBreed);
        bundle.putBoolean("isRunning",isRunning);
        outState.putBundle("x",bundle);
    }





    public void flipperImages(String imageName){
        ImageView imageView = new ImageView(this);
        int id = this.getResources().getIdentifier(imageName, "drawable", this.getPackageName());
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setImageResource(id);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        vFlipper.addView(imageView);
    }

    public void onSearch(){
        isRunning=true;
        btn_submit.setEnabled(false);
        textView.setEnabled(false);
        btn_stop.setEnabled(true);
        TheGame.usedImages.clear();
        TheGame.usedImages.add("starter");

    }



    public void searchBreed(View view) {
        onSearch();

        userSearchBreed = textView.getText().toString().toLowerCase();
        userSearchBreed = userSearchBreed.replace(" ","_");
        vFlipper.removeAllViews();


        if(TheGame.breedNameList.contains(userSearchBreed)){
            updateAndRunFlipper();


        }else{
            textView.setError("breed not available");
            reset();
        }


    }


    public void updateAndRunFlipper(){
        while(TheGame.usedImages.size() < 10){
            flipperImages(TheGame.generateRandomImageID2(userSearchBreed));
        }
        vFlipper.startFlipping();
    }


    public void reset(){
        textView.setEnabled(true);
        btn_submit.setEnabled(true);
        btn_stop.setEnabled(false);

    }

    public void stopSearch(View view) {
        isRunning=false;
        reset();
        vFlipper.stopFlipping();
    }
}
