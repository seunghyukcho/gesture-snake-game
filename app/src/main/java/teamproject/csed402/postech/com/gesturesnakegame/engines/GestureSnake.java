package teamproject.csed402.postech.com.gesturesnakegame.engines;

import android.content.Context;
import android.graphics.Point;
import android.os.SystemClock;

import teamproject.csed402.postech.com.gesturesnakegame.utilities.BTScanner;

public class GestureSnake extends SnakeEngine {
    private BTScanner btScanner;

    public GestureSnake(Context context, Point size) {
        super(context, size);
        btScanner = new BTScanner(context);

        btScanner.request();
    }

    @Override
    public void update() {
        if (eatinfo() == 1) {
            snakeLength++;
            spawnBob();
            score++;
        }

        if(btScanner.getLeftGesture()) {
            changeHeading(Rotate.LEFT);
            btScanner.setLeftGesture(false);
        }
        else if(btScanner.getRightGesture()) {
            changeHeading(Rotate.RIGHT);
            btScanner.setRightGesture(false);
        }

        moveSnake();

        if (detectDeath()) {
            newGame();
        }

        if (detectDeath()) {
            SystemClock.sleep(1500); // wait till next game starts
            newGame();
        }
    }
}
