package com.example.mobilecoursework;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GuessDog extends AppCompatActivity {
    //Basics+ To store scores & missed
    private int questionNo;
    private int score;
    private int missed;

    //Dog breed name as the Question
    public TextView breedQuestion;

    //to Choose option(answer) which are images.
    public ImageView img1;
    public ImageView img2;
    public ImageView img3;

    //to go NEXT , Exit
    public Button btnNext;
    Button endGame;

    //to display user chosen answer CORRECT or WRONG
    public TextView status;

    //display Question number , scores , missed
    public TextView level_lable;
    public TextView score_lable;
    public TextView missed_lable;

    //holds the name of the CORRECT breed.
    static String targetBreed;


    //holds the image resource id of the CORRECT breed.
    Integer targetResID;

    //Timer variables
    public CountDownTimer timer;
    public long timeLeft;
    boolean timerMode;
    private TextView countDownText;

    //To check if submitted for OnSaveInstance(Orientation change).
    boolean submitted;

    //To check if the timer was running when changing the orientation.
    boolean timerRunning;

    //To check if the user has CHOSEN the CORRECT answer when changing the orientation.
    boolean isAnswerCorrect;

    //To pass the slected view when orientation change(ONLY IF SUBMITTED)
    Integer selectedView;

    //For score pop up window - when pressing EXIT BUTTON
    Dialog myDialog;

    //for sound effects
    SoundPlayer soundPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess_dog);

        //getting references...
        endGame = findViewById(R.id.endgame2);
        breedQuestion = findViewById(R.id.breedNameQuest);
        status =  findViewById(R.id.status_text);
        score_lable = findViewById(R.id.score_lable);
        btnNext = findViewById(R.id.submit_btn);
        missed_lable =  findViewById(R.id.missed_lable);
        level_lable = findViewById(R.id.q_number);
        countDownText = findViewById(R.id.timer);
        img1= findViewById(R.id.img1);
        img2= findViewById(R.id.img2);
        img3= findViewById(R.id.img3);
        status= findViewById(R.id.status_text);

        //Initiating popUp window
        myDialog = new Dialog(this);

        //initiating sound player
        soundPlayer = new SoundPlayer(this);

        //Adding listeners on select - NEXT, EXIT
        addListenerOnButton();
        addListenerOnEndGameButton();

        //check for saved instance
        if (savedInstanceState != null) {
            Bundle bundle = savedInstanceState.getBundle("x");
            score = bundle.getInt("score");
            missed = bundle.getInt("missed");
            questionNo = bundle.getInt("question_no");
            submitted=bundle.getBoolean("submitted");
            isAnswerCorrect =bundle.getBoolean("status");
            targetResID =bundle.getInt("targetResID");
            selectedView = bundle.getInt("selectedImage");
            targetBreed=bundle.getString("breedName");
            timeLeft= bundle.getLong("timeLeft");
            timerMode=bundle.getBoolean("timerMode");
            timerRunning=bundle.getBoolean("timerRunning");

            //Set image resources & tag names
            img1.setImageResource(bundle.getInt("img1"));
            img1.setTag(bundle.getInt("img1"));
            img2.setImageResource(bundle.getInt("img2"));
            img2.setTag(bundle.getInt("img2"));
            img3.setImageResource(bundle.getInt("img3"));
            img3.setTag(bundle.getInt("img3"));

            //Set question (breed name)
            breedQuestion.setText(targetBreed);

            //IF TIMER MODE ON - If timer was running, Continue the timer, else , Display the time left.
            if(timerRunning)
                startTimer();
            else if(timerMode)
                updateTimer();

            //IF ANSWER SUBMITTED - update views, else, RESET buttons& text views , image views...
            if(submitted) {
                disableImages();
                btnNext.setEnabled(true);
                updateUI(isAnswerCorrect);
                updateImageBorders();
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

            //Initializing basics to default value.
            questionNo=0;
            score=0;
            missed=0;
            targetBreed="";
            selectedView=0;

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
        bundle.putInt("targetResID",targetResID);
        bundle.putInt("img1", (Integer) img1.getTag());
        bundle.putInt("img2", (Integer) img2.getTag());
        bundle.putInt("img3", (Integer) img3.getTag());
        bundle.putBoolean("submitted",submitted);
        bundle.putBoolean("status",isAnswerCorrect);
        bundle.putInt("selectedImage",selectedView);
        bundle.putString("breedName",targetBreed);
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
                saveToFireBase.save(userId, score, "lev2score");
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
                status.setText("WRONG!");
                status.setTextColor(Color.RED);
                btnNext.setEnabled(true);
                missed++;
                updateMarks();
                markCorrectImage();
                disableImages();
                submitted=true;
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
     * generate 3 random images & set to ImageView.
     */
    public void setRandomImages(){
        //to store USED images, to not display same images again.
        ArrayList<String> usedIDs = new ArrayList<>();

        //generate a random number for TARGET POSITION(img 1 or 2 or 3)
        int targetPosition = TheGame.rand.nextInt(3)+1;

        //invoking method from custom class to generate a random image name.
        String targetimgID = TheGame.generateRandomImageID("random");

        //getting the target breed name by ignoring last 2 characters which are "_" and a number. ex: breedname_6
        targetBreed = targetimgID.substring(0,targetimgID.length()-2);

        //to hold randomly generated Image name.
        String randomID;

        //to loop and generate strings "img1", "img2", "img3" which are imageView references.
        int num=1;

        while(num<=3) {
            //obtain imageView from R.id
            int id = this.getResources().getIdentifier("img" + num, "id", this.getPackageName());

            ImageView img= findViewById(id);

            //If this is the target position then set 'targetimgID' as the random id, else, generate random image.
            if(num == targetPosition){
                randomID = targetimgID;
            }else{
                //Generating random image which should not be the TARGET BREED.
                randomID = TheGame.generateRandomImageID(targetBreed);
                while (usedIDs.contains(randomID)){ //to ignore Duplicate images.
                    randomID = TheGame.generateRandomImageID(targetBreed);
                }
            }

            //obtain image resource id from drawable & set that as the tag name for its image view.
            int resId = getResources().getIdentifier(randomID,"drawable",getPackageName());
            img.setImageResource(resId);
            img.setTag(resId);
            usedIDs.add(randomID);

            //to store the taget image's resource id for later prpose.(To check answer CORRECT or WRONG)
            if(num==targetPosition){
                targetResID=resId;
            }
            num++;
        }
    }


    /**
     * set Question by replacing "_" by "<space>" for the breed names which consists more than one word. ex: Golden_Retriever
     */
    public void setQuestion(){
        breedQuestion.setText(targetBreed.replace("_"," "));
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
        btnNext.setEnabled(false);
        img1.setEnabled(true);
        img2.setEnabled(true);
        img3.setEnabled(true);
        img1.setBackgroundResource(R.drawable.image_border5);
        img2.setBackgroundResource(R.drawable.image_border5);
        img3.setBackgroundResource(R.drawable.image_border5);
    }

    /**
     * for button NEXT
     */
    public void addListenerOnButton() {
        final Animation animTranslate = AnimationUtils.loadAnimation(this,R.anim.anim_translate);
        btnNext.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                v.startAnimation(animTranslate);
                run();
                if(timerMode){
                    timeLeft=11000;
                    startTimer();
                }
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
        setRandomImages();
        setQuestion();
        bounceAnimate();
    }


    /**
     * On image click - processing results.
     * @param view - selected view
     */
    public void onImageClick(View view){
        //for savedInstance purpose
        selectedView=(Integer)view.getTag();
        submitted=true;

        // get the selected view's resource id (as it is already set as the Tag name)
        Integer selectedImgResId =(Integer)view.getTag();

        btnNext.setEnabled(true);

        //as to stop timer on image click
        if(timerMode)
            stopTimer();

        //comparing selected image resource id with target image res id.(to check CORRECT or WRONG)
        if(selectedImgResId.equals(targetResID)){
            soundPlayer.playCorrectSound();
            view.setBackgroundResource(R.drawable.image_border2); //setting GREEN border
            isAnswerCorrect=true; //to update UI & for savedInstance purpose
            score++;
        }else {
            soundPlayer.playWrongSound();
            view.setBackgroundResource(R.drawable.image_border3); // setting RED border
            isAnswerCorrect=false; ////to update UI & for savedInstance purpose
            missed++;
            markCorrectImage(); //invokes method which sets BLUE border for the correct image.
        }
        //displays "CORRECT !" or "WRONG !"
        updateUI(isAnswerCorrect);

        //updates scores, missed ..
        updateMarks();
    }

    /**
     * To update the UI according to user clicked image.
     * @param correct - defines if the clicked image is CORRECT(true) or WRONG(false)
     */
    public void updateUI(boolean correct){
        if(correct){
            status.setText("CORRECT!");
            status.setTextColor(Color.GREEN);
        }else{
            status.setText("WRONG");
            status.setTextColor(Color.RED);
            markCorrectImage();
        }
        disableImages(); // to make images Un-clickable as user has already chosen the answer.
    }

    /**
     * make all 3 images un-clickable.
     */
    public void disableImages(){
        img1.setEnabled(false);
        img2.setEnabled(false);
        img3.setEnabled(false);
    }

    /**
     * marks the correct image with BLUE border. comparing every images res id with 'targetResId'.(which are set as the tag name)
     */
    public void markCorrectImage(){
        //stores imageView references.
        ImageView[] imgViewList={img1,img2,img3};

        //to avoid code duplication,Loop through the list.
        for(ImageView imgView: imgViewList){
            if(imgView.getTag().equals(targetResID)){
                imgView.setBackgroundResource(R.drawable.image_border4);
                break;
            }
        }
    }

    /**
     * invoked only when the user changes orientation.
     */
    public void updateImageBorders(){
        //stores imageView references.
        ImageView[] imgViewList={img1,img2,img3};

        //to avoid code duplication,Loop through the list.
        for(ImageView imgView: imgViewList){
            Integer currentTag = (Integer) imgView.getTag();
            //checks if this is the user clicked image
            if(currentTag.equals(selectedView)){
                //checks if the user clicked image is CORRECT
                if(currentTag.equals(targetResID)){
                    imgView.setBackgroundResource(R.drawable.image_border2); // GREEN border
                    break;
                }else
                    //the user clicked image is WRONG
                    imgView.setBackgroundResource(R.drawable.image_border3); //RED border
            }
            //User has not clicked this view, check if thats the CORRECT image.
            else if(currentTag.equals(targetResID)){
                imgView.setBackgroundResource(R.drawable.image_border4); //BLUE border
            }
        }
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
                String shareBody = "I scored "+score+" in GUESS THE DOG challenge! Can you do that? Download app here -............";
                String shareSub = "HEY! Check this out! ";
                intent.putExtra(Intent.EXTRA_SUBJECT,shareSub);
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
        final Animation animBounce = AnimationUtils.loadAnimation(this, R.anim.anim_bounce);
        MyBounceInterpolater interpolater = new MyBounceInterpolater(0.2,20);
        animBounce.setInterpolator(interpolater);
        breedQuestion.startAnimation(animBounce);
    }
}

