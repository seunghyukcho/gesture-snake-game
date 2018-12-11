package teamproject.csed402.postech.com.gesturesnakegame.engines;

import android.content.Context;
import android.graphics.Point;
import android.content.Intent;
import android.util.Log;

import teamproject.csed402.postech.com.gesturesnakegame.MyApplication;

public class GestureSnake extends SnakeEngine{
    private Intent parentIntent;
    private Context parentContext;
    public GestureSnake(Context context, Point size, Intent intent) {
        super(context, size);
        parentContext = context;
        parentIntent = intent;
    }
    @Override
    public void update() {
        if (eatinfo()==1) {
            snakeLength++;
            spawnBob();
            score++;
        }
        String direction = ((MyApplication) parentContext.getApplicationContext()).getSomeVariable();

        Log.d("direction", direction);

        if(direction.equals("left")){
            changeHeading(Rotate.LEFT);
        }
        else if(direction.equals("right")){
            changeHeading(Rotate.RIGHT);
        }
        moveSnake();

        if (detectDeath()) {
            newGame();
        }
    }
}