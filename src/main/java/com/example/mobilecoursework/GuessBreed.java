package com.example.mobilecoursework;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GuessBreed extends AppCompatActivity {
    //Basics+ To store scores & missed
    private int questionNo;
    private int score;
    private int missed;

    //Dog image as the Question
    private ImageView img;

    //to Choose option(answer)
    private Spinner spinner;

    //to Submit , Exit
    private Button btnSubmit;
    private Button endGame;

    //to display user chosen answer CORRECT or WRONG, to display correct answer in BLUE
    private TextView status;
    private TextView correctAnswer;

    //display Question number , scores , missed
    private TextView level_lable;
    private TextView score_lable;
    private TextView missed_lable;

    //holds the resource ID of the CORRECT image.
    private int resId;

    //Timer variables
    private long timeLeft;
    private CountDownTimer timer;
    private TextView countDownText;
    private boolean timerMode;

    //To check if submitted for OnSaveInstance(Orientation change).
    private boolean submitted;

    //To check if the timer was running when changing the orientation.
    private boolean timerRunning;

    //To check if the user has CHOSEN the CORRECT answer when changing the orientation.
    private boolean isAnswerCorrect;

    //For score pop up window - when pressing EXIT BUTTON
    Dialog myDialog;

    //for sound effects
    SoundPlayer soundPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess_breed);

        //getting references...
        spinner = findViewById(R.id.breed_options);
        btnSubmit = findViewById(R.id.submit_btn);
        correctAnswer = findViewById(R.id.results_text);
        status = findViewById(R.id.status_text);
        endGame = findViewById(R.id.endGame);
        img=findViewById(R.id.dog_img);
        level_lable =  findViewById(R.id.q_number);
        score_lable = findViewById(R.id.score_lable);
        missed_lable = findViewById(R.id.missed_lable);
        countDownText = findViewById(R.id.timer);

        //Initiating popUp window
        myDialog = new Dialog(this);

        //initiating sound player
        soundPlayer = new SoundPlayer(this);

        //Adding listeners on select - SUBMIT, SELECT, EXIT
        addListenerOnButton();
        addListenerOnSelect();
        addListenerOnEndGameButton();


        //check for saved instance
        if (savedInstanceState != null) {
            //TRUE - Get all values and store them.
            Bundle bundle = savedInstanceState.getBundle("x");
            score = bundle.getInt("score");
            missed = bundle.getInt("missed");
            questionNo = bundle.getInt("question_no");
            resId =bundle.getInt("imageId");
            submitted=bundle.getBoolean("submitted");
            isAnswerCorrect =bundle.getBoolean("status");
            timeLeft= bundle.getLong("timeLeft");
            timerMode=bundle.getBoolean("timerMode");
            timerRunning=bundle.getBoolean("timerRunning");

            //Set image to the imageView.
            img.setImageResource(resId);

            //IF TIMER MODE ON - If timer was running, Continue the timer, else , Display the time left.
            if(timerRunning)
                startTimer();
            else if(timerMode)
                updateTimer();


            //IF ANSWER SUBMITTED - update views, else, RESET buttons& textviews...
            if(submitted){
                spinner.setEnabled(false);
                btnSubmit.setText("Next");
                updateUI(isAnswerCorrect);
            }else{
                resetDetails();
            }

            //FINALLY - update all view elements.
            updateMarks();

        //IF SAVED INSTANCE == NULL
        }else {
            //fetching if the timer switch ON / OFF from MAIN ACTIVITY.
            Intent intent = getIntent();
            if(intent.getBooleanExtra("timer",false)) {
                timerMode = true;
                timeLeft=11000;
                startTimer();
            }

            //Initializing questionNo, Score, missed to 0.
            questionNo=0;
            score=0;
            missed=0;

            //BEGIN THE GAME
            run();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle bundle = new Bundle();
        bundle.putInt("score",score);
        bundle.putInt("missed",missed);
        bundle.putInt("question_no",questionNo);
        bundle.putInt("imageId",resId);
        bundle.putBoolean("submitted",submitted);
        bundle.putBoolean("status",isAnswerCorrect);
        bundle.putLong("timeLeft",timeLeft);
        bundle.putBoolean("timerMode",timerMode);
        bundle.putBoolean("timerRunning",timerRunning);
        outState.putBundle("x",bundle);
    }

    /**
     * Save score to firebase Database when exit game. (ONLY IF TIMER ON)
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(timerMode) {
            String userId = loginSignup.getUserId();
            if (userId != null) {
                saveToFireBase.save(userId, score, "lev1score"); //function defined in custom class.
            }
        }
    }

    /**
     * to start the Timer
     */
    public void startTimer(){
        timerRunning=true;
        timer= new CountDownTimer(timeLeft,1000){
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                updateTimer();
            }

            @Override
            public void onFinish() {
                soundPlayer.playExitSound();
                timerRunning=false;
                submitted=true;
                btnSubmit.setEnabled(true);
                onSubmit();
            }
        }.start();
    }

    /**
     * update timer , which is invoked from StartTimer - 'onTick' .
     */
    public void updateTimer(){
        int seconds = (int)timeLeft/1000;
        String timeLeftString = Integer.toString(seconds);
        countDownText.setText(timeLeftString);
    }

    /**
     * stop timer , which is invoked from StartTimer - 'onFinish'.
     */
    public void stopTimer(){
        timerRunning=false;
        timer.cancel();
    }

    /**
     * generate a random image & set to ImageView.
     */
    public void setRandomImage(){
        String randomID = TheGame.generateRandomImageID("random"); //method defined in custom class.
        resId = getResources().getIdentifier(randomID,"drawable",getPackageName());
        img.setImageResource(resId);
    }

    /**
     * update marks after every submit
     **/
    public void updateMarks(){
        level_lable.setText(("Question #"+questionNo));
        score_lable.setText(("Score: "+score));
        missed_lable.setText(("Missed: "+missed));

    }

    /**
     * reset after every submit
     **/
    public void resetDetails(){
        level_lable.setText(("Question #"+questionNo));
        status.setText("");
        spinner.setEnabled(true);
        spinner.setSelection(0);
        btnSubmit.setEnabled(false);
        correctAnswer.setText("");
    }

    /**
     * as the button has 2 functions, Submit & next, it is specified below
     */
    public void addListenerOnButton() {
        final Animation animTranslate = AnimationUtils.loadAnimation(this,R.anim.anim_translate);

        btnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //submit -calls onSubmit method, stops the timer(IF TIMER MODE ON)
                v.startAnimation(animTranslate);
                if(btnSubmit.getText().equals("Submit")) {
                    if(timerMode)
                        stopTimer();
                    onSubmit();

                //next - calls RUN method, starts the timer(IF TIMER MODE ON)
                }else{
                    if(timerMode) {
                        timeLeft=11000;
                        startTimer();}
                    run();
                    btnSubmit.setText("Submit");}
            }
        });
    }

    /**
     * when clicking on submit, processing results
     */
    public void onSubmit(){
        spinner.setEnabled(false);
        String selected = spinner.getSelectedItem().toString().toLowerCase();

        //as images are named with '_' in between,
        selected = selected.replace(" ","_");

        //to save state when switching orientation.
        submitted=true;

        //fetching correct answer from custom class static variable.
        if (TheGame.randBreedLevel1.equals(selected)) {
            soundPlayer.playCorrectSound();
            isAnswerCorrect=true;
            score++;
        }
        else {
            soundPlayer.playWrongSound();
            isAnswerCorrect=false;
            missed++;
        }

        //to update views.
        updateUI(isAnswerCorrect);
        updateMarks();
        btnSubmit.setText("Next");
    }

    /**
     * To update the UI according to user chosen option.
     * @param correct - defines if the chosen option is CORRECT(true) or WRONG(false)
     */
    public void updateUI(boolean correct){
        if(correct){
            status.setText("CORRECT!");
            status.setTextColor(Color.GREEN);
        }else{
            status.setText("WRONG");
            correctAnswer.setText(TheGame.randBreedLevel1.replace("_"," "));
            status.setTextColor(Color.RED);
        }
    }

    /**
     * invoked when selecting item from the SPINNER.
     */
    public void addListenerOnSelect(){
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //when the option was "none", to not let user Submit.
                if (!("none".equals(String.valueOf(spinner.getSelectedItem()).toLowerCase())))
                    btnSubmit.setEnabled(true);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }



    /**
     * to invoke starter methods - GAME BEGINS
     */
    public void run(){
        questionNo ++;
        submitted=false;
        resetDetails();
        setRandomImage();
        bounceAnimate();
    }

    /**
     * create and Show POPUP.
     */
    public void openPopUp(){
        myDialog.setContentView(R.layout.score_popup);
        TextView txtClose=myDialog.findViewById(R.id.txtClose);
        Button btnShare = myDialog.findViewById(R.id.btnShareScores);
        TextView scoreLable = myDialog.findViewById(R.id.score_popup_lable);
        TextView usernameLable = myDialog.findViewById(R.id.username_popup_lable);
        TextView missedLable = myDialog.findViewById(R.id.missed_popup_lable);

        //Setting scores , missed , Username.
        scoreLable.setText(Integer.toString(score));
        missedLable.setText(Integer.toString(missed));

        //fetching username from firebase if LOGGED IN.
        if(loginSignup.getUserId()!=null)
            usernameLable.setText(loginSignup.getUserId());

        //set click listener for 'X' button.
        txtClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
                finish();
            }
        });

        //click listener for "SHARE SCORES" - implicit intent
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String shareBody = "HEY! Check this out! I scored "+score+" in GUESS THE DOG challenge! Can you do that? Download app here -............";
                intent.putExtra(Intent.EXTRA_TEXT,shareBody);
                startActivity(Intent.createChooser(intent,"Share using"));
            }
        });
        //Setting background to transparent & SHOW THE POPUP.
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    /**
     * TO OPEN POPUP - by clicking "EXIT GAME" button
     */
    public void addListenerOnEndGameButton(){
        endGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPopUp();

            }
        });
    }

    /**
     * a simple bounce animation for image entry, using MyBounceInterpolater class
     */
    public void bounceAnimate(){
        ImageView img= findViewById(R.id.dog_img);
        final Animation animBounce = AnimationUtils.loadAnimation(this, R.anim.anim_bounce);
        MyBounceInterpolater interpolater = new MyBounceInterpolater(0.2,20);
        animBounce.setInterpolator(interpolater);
        img.startAnimation(animBounce);
    }
}
