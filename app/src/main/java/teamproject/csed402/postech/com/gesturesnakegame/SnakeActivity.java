package teamproject.csed402.postech.com.gesturesnakegame;

import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;

import teamproject.csed402.postech.com.gesturesnakegame.engines.SnakeEngine;
import teamproject.csed402.postech.com.gesturesnakegame.engines.TouchSnake;

public class SnakeActivity extends AppCompatActivity {
    // Declare an instance of SnakeEngine
    TouchSnake snakeEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the pixel dimensions of the screen
        Display display = getWindowManager().getDefaultDisplay();

        // Initialize the result into a Point object
        Point size = new Point();
        display.getSize(size);

        // Create a new instance of the SnakeEngine class
        snakeEngine = new TouchSnake(this, size);
        // Make snakeEngine the view of the Activity
        setContentView(snakeEngine);
    }

    @Override
    protected void onResume() {
        super.onResume();
        snakeEngine.resume();
    }

    // Stop the thread in snakeEngine
    @Override
    protected void onPause() {
        super.onPause();
        snakeEngine.pause();
    }
}
