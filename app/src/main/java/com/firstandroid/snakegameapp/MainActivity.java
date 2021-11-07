/*
-make movement smoother (Save next direction as a String and add a counter in handler)
-Ranking
-Walk eaten apple through snake body
 */

package com.firstandroid.snakegameapp;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GestureDetectorCompat;
import android.view.GestureDetector;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

import android.provider.Settings.Secure;

import java.util.ArrayList;
import java.util.Random;

import static android.view.Gravity.CENTER;

public class MainActivity extends Activity implements View.OnClickListener {

    private String Machine_ID ;
    public static String username;
    //private DatabaseReference myRef;
    private DatabaseReference mDatabase;
    private static final String AD_UNIT_ID = "ca-app-pub-5961795793039298/3263663177";
    public static String SnakeColor = "Green";
    private RelativeLayout RL;
    private ConstraintLayout RL2;
    private FrameLayout FL;
    private ArrayList<RelativeLayout.LayoutParams> LayoutsParamsArray = new ArrayList<RelativeLayout.LayoutParams>();
    private TextView ScoreTV;
    private TextView HighScoreTV;
    private TextView YouLostTV;
    private ImageView btn_snake;
    private Button RestartBtn;
    private Button PauseBtn;
    private boolean Lost = false;
    private boolean Pause = false;
    private boolean finished;
    private ImageView imageView;
    private ArrayList<ImageView> SnakeArray = new ArrayList<ImageView>();
    private int MLeft ;
    private int MTop ;
    private int MRight = 0 ;
    private int MBottom = 0;
    private int MLeftApple;
    private int MTopApple;
    private int HighScore = 0;
    private int PlayGroundWidth,PlayGroundHeight;
    public static int sizepx ;
    private double ScreenHeight,ScreenWidth;
    private static final String Left = "Left";
    private static final String Right = "Right";
    private static final String Up = "Up";
    private static final String Down = "Down";
    private String LastDirection = "";
    private ArrayList<Integer> MLeftArray = new ArrayList<Integer>();
    private ArrayList<Integer> MTopArray = new ArrayList<Integer>();
    private int aaa = 1;
    public static int delay = 250;
    private double changeindelay = 0.95;
    private GestureDetectorCompat detector;
    private Drawable snake;
    SharedPreferences sharedPref;
    SharedPreferences usernamePref;
    MediaPlayer eatingsoundeffect;
    MediaPlayer losingsoundeffect;
    Bitmap bitmapOriginal;
//    private Point getPointOfView(View view) {
//        int[] location = new int[2];
//        view.getLocationOnScreen(location);
//        return new Point(location[0] , location[1] );
//    }
    private int step_left, step_right, step_up, step_down,x,y, Score;
    private int NumberInQueue = Score / 10;
    private static final String TAG = "MainActivity";
    private InterstitialAd interstitialAd;
    private String NextDirection = "";
    private boolean ThisIsANextDirection = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        loadAd();
        //sizepx = (int)convertDpToPx(MainActivity.this,size);
        sharedPref = getSharedPreferences("myPref", MODE_PRIVATE);
        AdjustSnakeColor();
        delay= sharedPref.getInt("Speed", 250);
         Machine_ID = Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID);

        btn_snake = findViewById(R.id.btn_snake);
        btn_snake.setOnClickListener(this);
        btn_snake.setBackground(snake);
        RestartBtn =  findViewById(R.id.RestartBtn);
        RestartBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                StartingActivity.buttonclicksoundeffect.start();
                Restart();
            }
        });
        PauseBtn =  findViewById(R.id.btn_pause);
        PauseBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                StartingActivity.buttonclicksoundeffect.start();
                if (Lost){
                    Intent z = new Intent(MainActivity.this,LeaderboardActivity.class);
                    startActivity(z);
                }
                else if (!Pause) {
                    Pause();
                } else {
                    Resume();
                }
            }
        });
        imageView =  findViewById(R.id.imageView);
        ScoreTV =  findViewById(R.id.ScoreTV);
        HighScoreTV =  findViewById(R.id.HighScoreTV);
        YouLostTV = findViewById(R.id.YouLostTV);
        eatingsoundeffect= MediaPlayer.create(MainActivity.this,R.raw.eatingsoundeffect);
        losingsoundeffect= MediaPlayer.create(MainActivity.this,R.raw.retroarcadelose);
        RL =  findViewById(R.id.RelativeLayout);
        RL2 =  findViewById(R.id.RelativeLayout2);
        FL =  findViewById(R.id.FL);
        AdjustPlayGround();
        RL2.bringToFront();
        btn_snake.bringToFront();
        RL.bringToFront();
        AdjustHighScore();
        GetUsername();

        detector = new GestureDetectorCompat(this, new MyGestureListener());
        FirebaseAnalytics.getInstance(this);
        //analytics.logEvent("button_clicked",null);
         mDatabase = FirebaseDatabase.getInstance().getReference();
         writeNewUser(Machine_ID,username,HighScore);
        //ReadData();
    }

    private void GetUsername() {
        usernamePref = getSharedPreferences("username", MODE_PRIVATE);
        username = usernamePref.getString("usernamekey", ""); //"" is the default value
    }

    public void writeNewUser(String Machine_ID, String username, int HighScore) {
        User user = new User(username, HighScore);
        mDatabase.child("users").child(Machine_ID).setValue(user);
    }
    @IgnoreExtraProperties
    public class User {

        public String username;
        public int HighScore;

        public User() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }
        public User (int HighScore){
            this.HighScore = HighScore;
        }
        public User(String username, int HighScore) {
            this.username = username;
            this.HighScore = HighScore;
        }


    }

    private void AdjustSnakeColor (){

        SnakeColor = sharedPref.getString("Color", "");
        if (SnakeColor.equals("")){
            SnakeColor="Green";
        }
        if (SnakeColor.equals("Green")){
//            snake = R.drawable.snake;
            snake = getDrawable(R.drawable.snake);
            bitmapOriginal = BitmapFactory.decodeResource(getResources(), R.drawable.snake);
        }
        if (SnakeColor .equals("Pink")){
            snake = getDrawable(R.drawable.snakepink);
            bitmapOriginal = BitmapFactory.decodeResource(getResources(), R.drawable.snakepink);
        }
        if (SnakeColor.equals("Blue")){
            snake = getDrawable(R.drawable.snakeblue);
            bitmapOriginal = BitmapFactory.decodeResource(getResources(), R.drawable.snakeblue);
        }

    }
    private void AdjustHighScore (){
        if (Score>HighScore) {
            HighScore=Score;
            SharedPreferences prefs = this.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("key", HighScore);
            editor.commit();
        }
        else{
            SharedPreferences prefs = this.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
            HighScore = prefs.getInt("key", 0); //0 is the default value
        }

        HighScoreTV.setText(Integer.toString(HighScore));
    }
//    private void ReadData (){
//        // Read from the database
//        myRef = FirebaseDatabase.getReference("message");
//        myRef.setValue("Hello, World!");
//        myRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//                String value = dataSnapshot.getValue(String.class);
//                Log.d(TAG, "Value is: " + value);
//                value = value;
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                // Failed to read value
//                Log.w(TAG, "Failed to read value.", error.toException());
//            }
//        });
//    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent event) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {

            float diffY = event2.getY() - event1.getY();
            float diffX = event2.getX() - event1.getX();
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        onSwipeRight();
                    } else {
                        onSwipeLeft();
                    }
                }
            } else {
                if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        onSwipeBottom();
                    } else {
                        onSwipeTop();
                    }
                }
            }
            return true;
        }
    }
    private void onSwipeLeft() {
        YouLostTV.setText("");
        if (ThisIsANextDirection){NextDirection="Left";}
        else if(!LastDirection.equals(Right)){

            Left();
        }

    }
    private void onSwipeRight() {
        YouLostTV.setText("");
        if (ThisIsANextDirection){NextDirection="Right";}
        else if(!LastDirection.equals(Left)) {
            Right();
        }
    }
    private void onSwipeTop() {
        YouLostTV.setText("");
        if (ThisIsANextDirection){NextDirection="Up";}
        else if(!LastDirection.equals(Down)){
            Up();
        }
        }
    private void onSwipeBottom() {
        YouLostTV.setText("");
        if (ThisIsANextDirection){NextDirection="Down";}
        else if(!LastDirection.equals(Up)) {
            Down();
        }
    }
    protected void Rotate(float degrees){
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees, bitmapOriginal.getWidth()/2, bitmapOriginal.getHeight()/2);
        Bitmap bitmapRot = Bitmap.createBitmap(bitmapOriginal, 0, 0, bitmapOriginal.getWidth(), bitmapOriginal.getHeight(), matrix, true);
        btn_snake.setImageBitmap(bitmapRot);

    }
    protected void Right() {

        btn_snake.setBackgroundColor(Color.TRANSPARENT);
        Rotate(-90);
        step_right = sizepx;
        step_up = 0;
        step_down = 0;
        step_left = 0;
        x = -sizepx;
        y = 0;
        aaa++;
        LastDirection = Right;
        ThisIsANextDirection = true;
    }
    protected void Left() {
        btn_snake.setBackgroundColor(Color.TRANSPARENT);
        Rotate(90);
        step_left = sizepx;
        step_up = 0;
        step_down = 0;
        step_right = 0;
        x = sizepx;
        y = 0;
        aaa++;
        LastDirection = Left;
        ThisIsANextDirection = true;
    }
    protected void Up() {
        btn_snake.setBackgroundColor(Color.TRANSPARENT);
        Rotate(180);
        step_right = 0;
        step_up = sizepx;
        step_down = 0;
        step_left = 0;
        y = sizepx;
        x = 0;
        aaa++;
        LastDirection = Up;
        ThisIsANextDirection = true;
    }
    protected void Down() {
        btn_snake.setBackgroundColor(Color.TRANSPARENT);
        Rotate(0);
        step_right = 0;
        step_up = 0;
        step_down = sizepx;
        step_left = 0;
        y = -sizepx;
        x = 0;
        aaa++;
        LastDirection = Down;
        ThisIsANextDirection = true;
    }
    protected void Restart() {

        finish();
        finished = true;
        startActivity(getIntent());

        //showInterstitial();
    }
    protected void Pause() {
//        step_left = 0;
//        step_right = 0;
//        step_down = 0;
//        step_up = 0;

        PauseBtn.setBackgroundResource(R.drawable.play);

        Pause= true;
    }
    protected void Resume() {

        PauseBtn.setBackgroundResource(R.drawable.pause);
        Pause = false;
        //super.onStart();
    }
    protected void CreateBorder(int width) {
        //RelativeLayout layout = (RelativeLayout) findViewById(R.id.RelativeLayout); // id fetch from xml
        ShapeDrawable rectShapeDrawable = new ShapeDrawable(); // pre defined class
        Paint paint = rectShapeDrawable.getPaint();
        paint.setColor(Color.parseColor("#A16036"/*brown*/));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(width);
        FL.setBackgroundDrawable(rectShapeDrawable);
    }
    public static float convertDpToPx(Context context, float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }
    protected void AddImageView() {
        ImageView NewImg = new ImageView(MainActivity.this);
        SnakeArray.add(0, NewImg);
        if (SnakeColor.equals("Green")||SnakeColor.equals("") ){
            NewImg.setBackgroundResource(R.drawable.circle);
        }
        if (SnakeColor.equals("Pink")){
            NewImg.setBackgroundResource(R.drawable.circlepink);
        }
        if (SnakeColor.equals("Blue")){
            NewImg.setBackgroundResource(R.drawable.circleblue);
        }
        RelativeLayout.LayoutParams NewLay = new RelativeLayout.LayoutParams(sizepx, sizepx);
        NewLay.setMargins(MLeftArray.get(0), MTopArray.get(0), 0, 0);
        LayoutsParamsArray.add(0, NewLay);
        NewImg.setLayoutParams(LayoutsParamsArray.get(0));
        RL.addView(SnakeArray.get(0));
    }
    protected boolean ConflictWithSnake(RelativeLayout.LayoutParams Lay){
        NumberInQueue = Score / 10;
        for (int i = NumberInQueue; i >= 0; i--) {
            if (Lay.leftMargin == LayoutsParamsArray.get(i).leftMargin && Lay.topMargin==LayoutsParamsArray.get(i).topMargin) {
                return true;
            }
        }
        return false;
    }
    protected RelativeLayout.LayoutParams GetNewApplePos(){

         Random r1 = new Random();
         Random r2 = new Random();
         //int random = random.nextInt(maximum-minimum) + minimum;
         int i1 = r1.nextInt(PlayGroundWidth/sizepx);
         int i2 = r2.nextInt(PlayGroundHeight/sizepx);
         RelativeLayout.LayoutParams pos = new RelativeLayout.LayoutParams(sizepx, sizepx);
         MLeftApple = i1* sizepx;
         MTopApple = i2 * sizepx ;
         pos.setMargins(MLeftApple,MTopApple,0,0);
         return pos;

    }
    protected void ChangeApplePosToRandomPos() {
        RelativeLayout.LayoutParams Lay = GetNewApplePos();
        while(ConflictWithSnake(Lay)){
            Lay = GetNewApplePos();
        }
        imageView.setLayoutParams(Lay);
        imageView.setBackgroundResource(R.drawable.apple);

    }
    protected void AppleStartingPos() {
        RelativeLayout.LayoutParams pos2 = new RelativeLayout.LayoutParams(sizepx, sizepx);
        MLeftApple = 5 * sizepx;
        MTopApple = 8 * sizepx;
        pos2.setMargins(MLeftApple,MTopApple, 0, 0);
        imageView.setLayoutParams(pos2);
    }
    protected void MoveSnakeHeadInDirectionSelected() {
        MLeft = MLeft - step_left;
        MRight = MRight + step_left;
        MLeft = MLeft + step_right;
        MRight = MRight - step_right;
        MTop = MTop - step_up;
        MBottom = MBottom + step_up;
        MTop = MTop + step_down;
        MBottom = MBottom - step_down;
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(sizepx, sizepx);
        lp.setMargins(MLeft, MTop, MRight, MBottom);
        btn_snake.setLayoutParams(lp);

    }
    protected void WhenAppleEatenDo() {
        if (!finished){eatingsoundeffect.start();}
        imageView.setBackgroundColor(Color.TRANSPARENT);
        Score = Score + 10;
        ScoreTV.setText(String.valueOf(Score));
        ChangeApplePosToRandomPos();
        AddImageView();
        AdjustHighScore();
    }
    protected void Lose(){
        if (!finished){losingsoundeffect.start(); }
        //PauseBtn.setEnabled(false);
        Pause= true;
        Lost = true;
        mDatabase.child("users").child(Machine_ID).child("HighScore").setValue(HighScore);
        YouLostTV.setText("YOU LOST :(");
        PauseBtn.setBackground(getDrawable(R.drawable.leaderboardicon));
        //RecordScore();
        showInterstitial();
        }
    protected void LoseIfOutOfBounds() {
        if (aaa != 1) {
            if (MLeft < 0 || MLeft >= (RL.getWidth()) || MTop < 0 || MTop >= RL.getHeight() ){
                Lose();
            }
        }
    }
    protected void MoveSnakeTailAndLoseIfSnakeHitsItsBody(){
        NumberInQueue = Score / 10;
        for (int i = NumberInQueue; i > 0; i--) {
            if (LayoutsParamsArray.get(i).leftMargin == MLeft && LayoutsParamsArray.get(i).topMargin == MTop&&i<NumberInQueue){
                Lose();//LoseIfSnakeHitsItsBody
            }
            SnakeArray.get(i-1).setLayoutParams(LayoutsParamsArray.get(i-1));//MoveSnakeTail
        }

    }
    private void CleanArray(ArrayList liste){
        int k = liste.size();
        if ( k > NumberInQueue+10 )
            liste.subList(NumberInQueue+10, k).clear();
    }
    protected void FollowParent(){

        MLeftArray.add(0, MLeft);
        MTopArray.add(0, MTop);
        RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(sizepx, sizepx);
        layout.setMargins(MLeftArray.get(0), MTopArray.get(0), 0, 0);
        LayoutsParamsArray.add(0,layout);
        CleanArray(MLeftArray);
        CleanArray(MTopArray);
        CleanArray(LayoutsParamsArray);

    }
    protected void RecordScore(){
        //mDatabase.child("users").child(userId).child("username").setValue(name);
//        private FirebaseDatabase db = FirebaseDatabase.getInstance();
//        private DatabaseReference root = db.getReference().child("Score");
        String S = String.valueOf(Score);
        //Users.setValue(S);
    }
    protected void AdjustPlayGround(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        ScreenHeight = displayMetrics.heightPixels;
        ScreenWidth = displayMetrics.widthPixels;
        sizepx = (int) ScreenWidth/15;
        MLeft = sizepx*2 ;
        MTop = sizepx*2;
        //double AspectRatio =  (ScreenHeight/ScreenWidth);
        PlayGroundWidth = sizepx*13;
//        PlayGroundHeight = (int) (PlayGroundWidth * (AspectRatio*3/4));
        PlayGroundHeight = sizepx * 16;
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(PlayGroundWidth, PlayGroundHeight);
        params.gravity = CENTER;
        RL.setLayoutParams(params);
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(PlayGroundWidth+(2*sizepx), PlayGroundHeight+(2*sizepx));
        FL.setLayoutParams(params2);
        AppleStartingPos();
    }
    @Override
    protected void onStart() {
        super.onStart();
         Handler handler1 = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (!Pause) {
                    FollowParent();
                    MoveSnakeHeadInDirectionSelected();
                    MoveSnakeTailAndLoseIfSnakeHitsItsBody();
                    if (!Lost) {
                        if (MLeftApple == MLeft && MTopApple == MTop) {
                            WhenAppleEatenDo();
                        }
                        LoseIfOutOfBounds();
                    }
                    NextDirection();
                }
                handler1.postDelayed(this,delay);


            }
        };

        handler1.post(runnable);
    }

    private void NextDirection() {
        if (NextDirection.equals("Right")){Right();}
        if (NextDirection .equals("Left")){Left();}
        if (NextDirection.equals("Up")){Up();}
        if (NextDirection.equals("Down")){Down();}
        NextDirection = "";
        ThisIsANextDirection = false;
    }

    private void showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and restart the game.
        if (interstitialAd != null) {
            interstitialAd.show(this);
        } else {
            //Toast.makeText(this, "Ad did not load", Toast.LENGTH_SHORT).show();

        }
    }
    public void loadAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(
                this,
                AD_UNIT_ID,
                adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        MainActivity.this.interstitialAd = interstitialAd;
                        Log.i(TAG, "onAdLoaded");
                        //Toast.makeText(MainActivity.this, "onAdLoaded()", Toast.LENGTH_SHORT).show();
                        interstitialAd.setFullScreenContentCallback(
                                new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        // Called when fullscreen content is dismissed.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        MainActivity.this.interstitialAd = null;
                                        Log.d("TAG", "The ad was dismissed.");
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                                        // Called when fullscreen content failed to show.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        MainActivity.this.interstitialAd = null;
                                        Log.d("TAG", "The ad failed to show.");
                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                        // Called when fullscreen content is shown.
                                        Log.d("TAG", "The ad was shown.");
                                    }
                                });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i(TAG, loadAdError.getMessage());
                        interstitialAd = null;

                        String error = String.format("domain: %s, code: %d, message: %s", loadAdError.getDomain(), loadAdError.getCode(), loadAdError.getMessage());
                        //Toast.makeText(MainActivity.this, "onAdFailedToLoad() with error: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
    }
    @Override
    public void onClick(View v) {

    }
}