package teamproject.csed402.postech.com.gesturesnakegame;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import teamproject.csed402.postech.com.gesturesnakegame.engines.SnakeEngine;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        TextView scoreLabel= (TextView) findViewById(R.id.current_score);
        TextView highscoreLabel= (TextView) findViewById(R.id.high_score);

        int score=getIntent().getIntExtra("SCORE",0);
        scoreLabel.setText(score+ "");
        SharedPreferences settings= getSharedPreferences("GAME_DATA",Context.MODE_PRIVATE);
        int highscore= settings.getInt("HIGH_SCORE",0);
        if(score >highscore)
        {
            highscoreLabel.setText("High Score : " + score);
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("HIGH_SCORE", score);
            editor.commit();
        }
        else
        {
            highscoreLabel.setText("High Score : "+highscore);
        }
        Button isrestart=(Button)findViewById(R.id.queryrestart);
        isrestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

            }
        });
    }
}

