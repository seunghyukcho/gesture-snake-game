package teamproject.csed402.postech.com.gesturesnakegame;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    private static MediaPlayer mp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Context parentContext = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mp = MediaPlayer.create(this, R.raw.backgruond_m);
        mp.setLooping(true);
        mp.start();
        ImageButton tou = (ImageButton) findViewById(R.id.touch_mode);
        ImageButton ges = (ImageButton) findViewById(R.id.gesture_mode);
        ImageButton setting = (ImageButton) findViewById(R.id.setit);
        tou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SnakeActivity.class);
                startActivity(intent);
            }
        });
        ges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GestureSnakeActivity.class);
                startActivity(intent);
            }
        });
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                startActivity(intent);
            }
        });
    }
}

