package com.example.trevor.project5;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    GameView gv;
    Paint drawPaint = new Paint();
    Bitmap boxGraphic, treeGraphic, spawnGraphic, retryGraphic, easyGraphic, mediumGraphic, hardGraphic;
    int numTacos = 30, nextTaco = 0, boxX = 50, boxY = 1600, treeX = 60, treeY = 200, spawnX = treeX, spawnY = treeY,
            retryX = 1025, retryY = 2000, easyX = 500, easyY = 500, mediumX = 500, mediumY = 700, hardX = 500, hardY = 900;;
    tacoBitmap[] tacoGraphics = new tacoBitmap[numTacos + 1];
    Rect tacoRect, boxRect;
    float x=0, y=0;
    boolean tacoSelected = false, retrySelected = false, easySelected = false, mediumSelected = false, hardSelected = false, gameOver = false, newGame = true;
    final int V_OFFSET = 275;
    String centerText = "Collect 30 tacos before the time is up!",
            timerText = "Time left: 30",
            scoreText = "Score: " + nextTaco;
    MediaPlayer bgm, pick, drop;

    CountDownTimer cdTimer =  new CountDownTimer(30000, 1000) {
        public void onTick(long millisUntilFinished) {
            timerText = "Time left: " + millisUntilFinished / 1000;
        }

        public void onFinish() {
            gameOver(false);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        gv = new GameView(this);
        this.setContentView(gv);

        pick = MediaPlayer.create(MainActivity.this,R.raw.pick);
        drop = MediaPlayer.create(MainActivity.this,R.raw.drop);
        bgm = MediaPlayer.create(MainActivity.this,R.raw.bgm_lacucaracha);

        boxGraphic = BitmapFactory.decodeResource(getResources(), R.drawable.boxsmaller);
        treeGraphic = BitmapFactory.decodeResource(getResources(), R.drawable.treebig);
        spawnGraphic = BitmapFactory.decodeResource(getResources(), R.drawable.spawn);
        retryGraphic = BitmapFactory.decodeResource(getResources(), R.drawable.retry);
        easyGraphic = BitmapFactory.decodeResource(getResources(), R.drawable.easy);
        mediumGraphic = BitmapFactory.decodeResource(getResources(), R.drawable.medium);
        hardGraphic = BitmapFactory.decodeResource(getResources(), R.drawable.hard);

        playBGM();
        generateTacos();
    }

    //ToDo cdTimer does not stop when the app is closed
    @Override
    protected void onPause() {
        super.onPause();
        gv.pause();
        stopBGM();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gv.resume();
        if (!bgm.isPlaying())
            playBGM();
    }

    public void restartGame() {
        nextTaco = 0;
        gameOver = false;
        cdTimer.cancel();

        generateTacos();
    }

    public void generateTacos() {
        Random random = new Random();
        Bitmap taco;

        for (int i = 0; i < numTacos + 1; i++) {
            int rand = random.nextInt(3 - 1 + 1) + 1;
            switch (rand) {
                case 1:
                    taco = BitmapFactory.decodeResource(getResources(), R.drawable.taco1smaller);
                    break;
                case 2:
                    taco = BitmapFactory.decodeResource(getResources(), R.drawable.taco2smaller);
                    break;
                case 3:
                    taco = BitmapFactory.decodeResource(getResources(), R.drawable.taco3smaller);
                    break;
                default:
                    taco = BitmapFactory.decodeResource(getResources(), R.drawable.taco1smaller);
            }

            int x = random.nextInt(((spawnX - taco.getWidth()) + spawnGraphic.getWidth()) - spawnX + 1) + spawnX;
            int y = random.nextInt(((spawnY - taco.getHeight()) + spawnGraphic.getHeight()) - spawnY + 1) + spawnY;

            tacoGraphics[i] = new tacoBitmap(taco, x, y);
        }
    }

    private class tacoBitmap {
        int x;
        int y;
        Bitmap bitmap;

        tacoBitmap (Bitmap bitmap, int x, int y) {
            this.bitmap = bitmap;
            this.x = x;
            this.y = y;
        }
    }

    public class GameView extends SurfaceView implements Runnable {

        Thread ViewThread = null;
        SurfaceHolder holder;
        boolean threadOK = true;

        public GameView(Context context) {
            super(context);
            holder = this.getHolder();
        }

        @Override
        public void run() {
            while (threadOK == true) {
                if (!holder.getSurface().isValid()) {
                    continue;
                }

                Canvas gameCanvas = holder.lockCanvas();
                boxRect = new Rect(boxX, boxY, boxX + boxGraphic.getWidth(), boxY + boxGraphic.getHeight());
                tacoRect = new Rect(tacoGraphics[nextTaco].x, tacoGraphics[nextTaco].y,
                        tacoGraphics[nextTaco].x + tacoGraphics[nextTaco].bitmap.getWidth(), tacoGraphics[nextTaco].y + tacoGraphics[nextTaco].bitmap.getHeight());

                //timerRunnable.run();
                myOnDraw(gameCanvas);
                holder.unlockCanvasAndPost(gameCanvas);
            }
        }

        protected void myOnDraw(final Canvas canvas) {
            drawPaint.setAlpha(255);
            canvas.drawColor(Color.parseColor("#5FC1F1"));

            drawPaint.setColor(Color.WHITE);
            drawPaint.setTextSize(68);

            canvas.drawBitmap(treeGraphic, treeX, treeY, drawPaint);
            canvas.drawBitmap(boxGraphic, boxX, boxY, drawPaint);
            canvas.drawBitmap(spawnGraphic, spawnX, spawnY, drawPaint);

            if(newGame) {
                canvas.drawBitmap(easyGraphic, easyX, easyY, drawPaint);
                canvas.drawBitmap(mediumGraphic, mediumX, mediumY, drawPaint);
                canvas.drawBitmap(hardGraphic, hardX, hardY, drawPaint);
            }
            else {
                scoreText = "Score: " + nextTaco;
                drawPaint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText(timerText, 250, 200, drawPaint);
                canvas.drawText(centerText, canvas.getWidth()/2, 100, drawPaint);
                canvas.drawText(scoreText, 1200, 200, drawPaint);

                canvas.drawBitmap(retryGraphic, retryX, retryY, drawPaint);
                if (nextTaco < numTacos) {
                    if (!gameOver)
                        canvas.drawBitmap(tacoGraphics[nextTaco].bitmap, tacoGraphics[nextTaco].x, tacoGraphics[nextTaco].y, drawPaint);
                }
                else
                    gameOver(true);
            }
        }

        public void pause() {
            threadOK = false;
            while(true) {
                try {
                    ViewThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            }
            ViewThread = null;
        }

        public void resume() {
            threadOK = true;
            ViewThread = new Thread(this);
            ViewThread.start();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            {
                x = event.getRawX();
                y = event.getRawY() - V_OFFSET;

                tacoSelected = graphicSelected(tacoGraphics[nextTaco].bitmap, tacoGraphics[nextTaco].x, tacoGraphics[nextTaco].y, x, y);
                retrySelected = graphicSelected(retryGraphic, retryX, retryY, x, y);
                easySelected = graphicSelected(easyGraphic, easyX, easyY, x, y);
                mediumSelected = graphicSelected(mediumGraphic, mediumX, mediumY, x, y);
                hardSelected = graphicSelected(hardGraphic, hardX, hardY, x, y);

                if (tacoSelected && !gameOver) {
                    playPick();
                }

                if (retrySelected) {
                    newGame = true;
                    restartGame();
                }

                if (easySelected && newGame && !gameOver) {
                    newGame = false;
                    numTacos = 10;
                    centerText = "Collect 10 tacos before the time is up!";
                    cdTimer.start();
                }

                if (mediumSelected && newGame && !gameOver) {
                    newGame = false;
                    numTacos = 20;
                    centerText = "Collect 20 tacos before the time is up!";
                    cdTimer.start();
                }

                if (hardSelected && newGame && !gameOver) {
                    newGame = false;
                    numTacos = 30;
                    centerText = "Collect 30 tacos before the time is up!";
                    cdTimer.start();
                }

            }
            break;

            case MotionEvent.ACTION_MOVE:
            {
                x = event.getRawX() - (tacoGraphics[nextTaco].bitmap.getWidth()/2);
                y = (event.getRawY() - V_OFFSET) - (tacoGraphics[nextTaco].bitmap.getHeight()/2);

                if (tacoSelected && !gameOver) {
                    tacoGraphics[nextTaco].x = (int) x;
                    tacoGraphics[nextTaco].y = (int) y;
                }
            }
            break;

            case MotionEvent.ACTION_UP:
            {
                if(tacoSelected && !gameOver) {
                    if (tacoInBox()) {
                        playDrop();
                        nextTaco++;
                    }
                }

                tacoSelected = false;
            }
        }

        return true;
    }

    public boolean graphicSelected(Bitmap graphic, int graphicX, int graphicY, float x, float y) {
        if (x > graphicX
                && x < (graphicX + graphic.getWidth())
                && y > graphicY
                && y < (graphicY + graphic.getHeight())) {
            return true;
        } else {
            return false;
        }
    }

    public boolean tacoInBox() {
        if (!gameOver && Rect.intersects(tacoRect, boxRect))
            return true;
        else
            return false;
    }

    public void playPick() {

        pick.start();
    }

    public void playDrop() {

        drop.start();
    }

    public void playBGM() {

        bgm.setLooping(true);
        bgm.start();
    }

    public void stopBGM() {
        bgm.stop();
    }

    public void gameOver(boolean win) {
        gameOver = true;
        if (win) {
            cdTimer.cancel();
            centerText = "You win!";
        }
        else {
            centerText = "You lose";
        }
    }
}