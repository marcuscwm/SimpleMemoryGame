package sg.edu.iss.memorygame;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final int[] btnIdList = new int[]{R.id.gameButton11, R.id.gameButton12,
            R.id.gameButton13, R.id.gameButton21, R.id.gameButton22, R.id.gameButton23,
            R.id.gameButton31, R.id.gameButton32, R.id.gameButton33, R.id.gameButton41,
            R.id.gameButton42, R.id.gameButton43};

    private final int[] imgIdList = new int[]{R.drawable.ic_1_soccer, R.drawable.ic_2_navi,
            R.drawable.ic_3_place, R.drawable.ic_4_basketball, R.drawable.ic_5_star,
            R.drawable.ic_6_esports};

    private Boolean lastImgIsFaceUp;
    private Boolean clickable;
    private int lastImgId;
    private ImageButton lastBtn;
    private int matchedSets;
    private List<ImageButton> matchedBtns;
    private int seconds;
    private int minutes;
    private Boolean started;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startSettings();

        matchedBtns = new ArrayList<ImageButton>() {
        };

        List<Integer> images = new ArrayList<Integer>() {
        };

        for (int imgId : imgIdList) {
            images.add(imgId);
            images.add(imgId);
        }

        // add images twice into List for matching sets and use collection to shuffle
        Collections.shuffle(images);

        for (int i = 0; i < btnIdList.length; i++) {
            ImageButton btn = findViewById(btnIdList[i]);
            if (btn != null) {
                int finalI = i;
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (clickable && matchedSets < imgIdList.length
                                && !matchedBtns.contains(btn)) {
                            btn.setImageResource(images.get(finalI));

                            // first flip of a pair
                            if (!lastImgIsFaceUp) {
                                started = true;
                                lastImgIsFaceUp = true;
                                lastImgId = images.get(finalI);
                                lastBtn = btn;
                                clickable = true;

                                // if mismatch
                            } else if (images.get(finalI) != lastImgId) {
                                clickable = false;
                                // timer until user can click again
                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        // overturn mismatched pair
                                        btn.setImageResource(R.drawable.ic_0_police);
                                        lastBtn.setImageResource(R.drawable.ic_0_police);
                                        lastImgIsFaceUp = false;
                                        clickable = true;
                                    }
                                }, 1500);

                                // if matched
                            } else if (images.get(finalI) == lastImgId && lastBtn != btn) {
                                lastImgIsFaceUp = false;
                                matchedSets++;
                                TextView textScore = findViewById(R.id.textMatches);
                                @SuppressLint("DefaultLocale") String text = String.format(
                                        "%d of %d matched", matchedSets, imgIdList.length);
                                textScore.setText(text);
                                clickable = true;
                                matchedBtns.add(btn);
                                matchedBtns.add(lastBtn);
                                if (matchedSets == imgIdList.length)
                                    started = false;
                            }
                        }
                    }
                });
            }
        }
        runTimer();
    }

    private void startSettings() {
        lastImgIsFaceUp = false;
        clickable = true;
        matchedSets = 0;
        seconds = 0;
        minutes = 0;
        started = false;
    }

    private void runTimer() {
        TextView txtTime = findViewById(R.id.textTimer);
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (seconds == 60) {
                    seconds = 0;
                    minutes++;
                }

                @SuppressLint("DefaultLocale") String text = String.format(
                        "%02d:%02d", minutes, seconds);
                txtTime.setText(text);

                if (started)
                    seconds++;
                handler.postDelayed(this, 1000);
            }
        });
    }
}