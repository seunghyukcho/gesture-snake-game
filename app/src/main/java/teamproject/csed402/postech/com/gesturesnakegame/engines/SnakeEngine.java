package teamproject.csed402.postech.com.gesturesnakegame.engines;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


import java.util.Random;

import javax.xml.transform.Result;

import teamproject.csed402.postech.com.gesturesnakegame.R;
import teamproject.csed402.postech.com.gesturesnakegame.ResultActivity;
public class SnakeEngine extends SurfaceView implements Runnable {
    public enum Heading {UP, DOWN, LEFT, RIGHT};
    public enum Rotate {LEFT, RIGHT};

    protected final int NUM_BLOCKS_WIDE = 40;
    protected final long FPS = 10;
    protected final long MILLIS_PER_SECOND = 1000;

    protected Thread thread;
    protected Context context;

    protected int score;

    protected Heading heading = Heading.RIGHT;

    protected int screenX, screenY, snakeLength;
    protected int bobX, bobY, blockSize;

    protected int numBlocksHigh;
    protected long nextFrameTime;

    protected int[] snakeXs, snakeYs;

    protected volatile boolean isPlaying;

    protected Canvas canvas;
    protected SurfaceHolder surfaceHolder;
    protected Paint paint;
    protected Paint fontcolor;
    protected Context mContext;
    public ResultActivity resa;



    public SnakeEngine(Context context, Point size) {
        super(context);
        this.mContext = context;

        screenX = size.x;
        screenY = size.y;

        blockSize = screenX / NUM_BLOCKS_WIDE;
        numBlocksHigh = screenY / blockSize;

        AssetManager assetManager = context.getAssets();

        surfaceHolder = getHolder();
        paint = new Paint();

        snakeXs = new int[200];
        snakeYs = new int[200];

        newGame();
    }

    @Override
    public void run() {
        while (isPlaying) {
            if(updateRequired()) {
                update();
                draw();
            }
        }
    }

    public void pause() {
        isPlaying = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        isPlaying = true;
        thread = new Thread(this);
        thread.start();

    }

    public void newGame() {
        snakeLength = 1;
        snakeXs[0] = NUM_BLOCKS_WIDE / 2;
        snakeYs[0] = numBlocksHigh / 2;

        spawnBob();
        score = 0;

        nextFrameTime = System.currentTimeMillis();
    }

    public void spawnBob() {
        Random random = new Random();
        bobX = random.nextInt(NUM_BLOCKS_WIDE - 1) + 1;
        bobY = random.nextInt(numBlocksHigh - 1) + 1;
    }

    protected void eatBob(){
        snakeLength++;
        spawnBob();
        score++;
    }

    protected void moveSnake() {
        for (int i = snakeLength; i > 0; i--) {
            snakeXs[i] = snakeXs[i - 1];
            snakeYs[i] = snakeYs[i - 1];
        }

        switch (heading) {
            case UP:
                snakeYs[0]--;
                break;
            case RIGHT:
                snakeXs[0]++;
                break;
            case DOWN:
                snakeYs[0]++;
                break;
            case LEFT:
                snakeXs[0]--;
                break;
        }
    }

    protected boolean detectDeath() {
        boolean dead = false;

        if (snakeXs[0] == -1) dead = true;
        if (snakeXs[0] >= NUM_BLOCKS_WIDE) dead = true;
        if (snakeYs[0] == -1) dead = true;
        if (snakeYs[0] == numBlocksHigh) dead = true;

        for (int i = snakeLength - 1; i > 0; i--) {
            if ((i > 4) && (snakeXs[0] == snakeXs[i]) && (snakeYs[0] == snakeYs[i])) {
                dead = true;
            }
        }

        return dead;
    }

    public void update() {
        if (snakeXs[0] == bobX && snakeYs[0] == bobY) {
            eatBob();
        }

        moveSnake();

        if (detectDeath()) {
            newGame();
        }
    }

    public void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();
            SharedPreferences pref = getContext().getSharedPreferences("pref", Context.MODE_PRIVATE);
            int snakecolor=pref.getInt("scolor",0);
            int alpha=(0xFF000000 & snakecolor)>>24;
            int red=(0x00FF0000 & snakecolor)>>16;
            int green=(0x0000FF00 &snakecolor)>>8;
            int blue=(0x000000FF&snakecolor);
            canvas.drawColor(Color.argb(255, 0, 0, 0));//background color
            paint.setColor(Color.argb(alpha,red,green,blue));//snake color
            fontcolor=new Paint();
            fontcolor.setTextSize(90);
            fontcolor.setColor(Color.argb(255,255,255,255));//font color which is white
            canvas.drawText("Score:" + score, 10, 70, fontcolor);
            for (int i = 0; i < snakeLength; i++) {
                canvas.drawRect(snakeXs[i] * blockSize,
                        (snakeYs[i] * blockSize),
                        (snakeXs[i] * blockSize) + blockSize,
                        (snakeYs[i] * blockSize) + blockSize,
                        paint);
            }

            paint.setColor(Color.argb(255, 255, 0, 0));//eating color

            canvas.drawRect(bobX * blockSize,
                    (bobY * blockSize),
                    (bobX * blockSize) + blockSize,
                    (bobY * blockSize) + blockSize,
                    paint);

            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    public boolean updateRequired() {
        if(nextFrameTime <= System.currentTimeMillis()){
            nextFrameTime =System.currentTimeMillis() + MILLIS_PER_SECOND / FPS;
            return true;
        }

        return false;
    }

    public void changeHeading(Rotate dir) {
        if(dir == Rotate.RIGHT) {
            switch(heading){
                case UP:
                    heading = Heading.RIGHT;
                    break;
                case RIGHT:
                    heading = Heading.DOWN;
                    break;
                case DOWN:
                    heading = Heading.LEFT;
                    break;
                case LEFT:
                    heading = Heading.UP;
                    break;
            }
        }
        else {
            switch(heading){
                case UP:
                    heading = Heading.LEFT;
                    break;
                case LEFT:
                    heading = Heading.DOWN;
                    break;
                case DOWN:
                    heading = Heading.RIGHT;
                    break;
                case RIGHT:
                    heading = Heading.UP;
                    break;
            }
        }
    }
}
