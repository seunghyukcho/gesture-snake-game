package teamproject.csed402.postech.com.gesturesnakegame.engines;

import android.content.Context;
import android.graphics.Point;
import android.view.MotionEvent;

public class TouchSnake extends SnakeEngine {
    public TouchSnake(Context context, Point size) {
        super(context, size);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                if (motionEvent.getX() >= screenX / 2) {
                    changeHeading(Rotate.RIGHT);
                } else {
                    changeHeading(Rotate.LEFT);
                }
        }
        return true;
    }
}
