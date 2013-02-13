package fi.jw.processing;

import processing.core.*;

import java.util.Date;

public class PongSketch extends PApplet {

    /**
     *
     */
    private static final long serialVersionUID = 4489382234265685537L;

    PVector[] paddles = new PVector[2];
    PVector[] movements = new PVector[2];
    float[] recentMovements = new float[10];
    PVector ballPosition, ballDirection;
    int[] scores = new int[2];
    PFont font;
    PFont timerFont;

    final PVector xAxis = new PVector(10,0,0);
    final int paddleWidth = 16;
    final int paddleHeight = 64;
    final int padding = 16;
    final int ballWidth = 16;
    int tick = 0;

    int maxPaddle;
    int minPaddle = padding;
    boolean gameOn = false;
    long lastReset = System.currentTimeMillis();

    public void setup() {
        smooth();
        size(1024, 768);
        maxPaddle = (height - padding - paddleHeight);
        background(0, 0, 0);
        noStroke();
        paddles[0] = new PVector(padding, padding, 0);
        paddles[1] = new PVector(width - paddleWidth - padding, padding, 0);

        scores[0] = 0;
        scores[1] = 0;


        font = createFont("Press Start 2P", 32);
        timerFont = createFont("Press Start 2P", 64);
        textFont(font);
        reset();
    }

    public void reset() {
        gameOn = false;
        lastReset = System.currentTimeMillis();
        movements[0] = new PVector(0, 0, 0);
        movements[1] = new PVector(0, 0, 0);
        recentMovements = new float[] { 0,0,0,0,0,0,0,0,0,0 };
        ballPosition = new PVector(width / 2 - ballWidth / 2, height / 2 - ballWidth / 2);
        boolean goingLeft = (random(-1,1) > 0);
        float angle = radians(random(-70,70));

        ballDirection = new PVector(cos(angle), sin(angle), 0);
        ballDirection.mult(6);

    }

    public void draw() {
        frameRate(60);
        pushMatrix();
        noStroke();
        strokeWeight(0);
        background(0);
        drawPaddles();
        drawBorders();
        drawBall();
        drawScores();

        if (!gameOn) {
            long secondsSinceReset = (long)Math.floor((System.currentTimeMillis() - lastReset) / 1000);
            long secondsToWait = 1;
            if (secondsSinceReset > secondsToWait) {
                color(102,255,51);
                gameOn = true;
            } else {
                textFont(timerFont);
                fill(255,0,0);
                text("" + (secondsToWait - secondsSinceReset), width/2 - 50, height/2 - 50);
            }
        }
        popMatrix();
    }

    private void drawBall() {
        noStroke();
        if (gameOn)
            moveBall();

        fill(102, 255, 51);
        rect(ballPosition.x, ballPosition.y, ballWidth, ballWidth);
        stroke(102,255,51);
        strokeWeight(1);
        line(ballPosition.x + ballWidth / 2, ballPosition.y + ballWidth / 2, ballPosition.x + ballDirection.x * 10, ballPosition.y + ballDirection.y * 10);
    }

    HIT ballHitsPaddle(int paddle) {
        float deltaY = ballPosition.y - paddles[paddle].y;
        /*
         MISS           b.y < p.y ==> b.y - p.y < 0
         |   TOP
         |   CENTER
         |   CENTER
         |   BOTTOM  _____ == paddleheight
         MISS



         */

        if (deltaY < 0 || deltaY > paddleHeight) {
            return HIT.MISS;
        } else {
            float fraction = (deltaY / paddleHeight);
            log("fraction %f", fraction);
            if (fraction <= (1f/4f)) {
                return HIT.TOP;
            } else if (fraction < (3f/4f)) {
                return HIT.CENTER;
            } else {
                return HIT.BOTTOM;
            }
        }
    }

    public enum HIT {
        MISS, TOP, CENTER, BOTTOM
    }
    private void moveBall() {
        log("mag(): %f; x = %f ; y = %f", ballDirection.mag(), ballDirection.x, ballDirection.y);

        ballPosition.add(ballDirection);
        int ballPadding = padding + paddleWidth;
        boolean onPlayerCourt = ballPosition.x < (width / 2);
        boolean movingLeft = ballDirection.x < 0;
        boolean atLeftBorder = ballPosition.x < ballPadding;
        boolean atRightBorder = ballPosition.x > (width - ballPadding - ballWidth);

        if ((movingLeft && atLeftBorder) || (!movingLeft && atRightBorder)) {
            float angleAdjustment = 0.2f;
            float velocityAdjustment = 0.2f;
            switch (ballHitsPaddle(onPlayerCourt ? 0 : 1)) {
                case MISS:
                    if (onPlayerCourt) {
                        scores[1] += 1;
                    } else {
                        scores[0] += 1;
                    }
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        // don't care
                    }
                    reset();
                    break;

                //it hit, bounce it and add/sub velocity
                case TOP:
                    System.out.println("TOP HIT!");
                    changeAngleAndAdjustVelocity(-angleAdjustment, velocityAdjustment);
                    ballDirection.x *= -1;
                    break;
                case BOTTOM:
                    System.out.println("BOTTOM HIT!");
                    changeAngleAndAdjustVelocity(angleAdjustment, velocityAdjustment);
                    ballDirection.x *= -1;
                    break;
                case CENTER:
                    System.out.println("CENTER HIT!");
                    changeAngleAndAdjustVelocity(0, velocityAdjustment);
                    ballDirection.x *= -1;
                    break;
            }

        }

        if (ballPosition.y < ballPadding) {
            log("Hit ceiling => going down");
            ballDirection.y *= -1;
        } else if (ballPosition.y > (height - ballPadding)) {
            log("Hit bottom => going up");
            ballDirection.y *= -1;
        }

        movements[1].y = (ballPosition.y - paddleMiddle(1)) / 2;
    }

    private void changeAngleAndAdjustVelocity(float angle, float velocity) {
        float angleDelta = changeAngle(angle);
        log("Angle delta = %f", angleDelta);
        adjustVelocity(velocity, angleDelta);
    }
    private void adjustVelocity(float adjustment, float angleDelta) {
        if (ballDirection.mag() < 10f) {
            ballDirection.mult(1f + adjustment);
            log("speed up %f => x = %f , y = %f", 1 + adjustment, ballDirection.x,ballDirection.y);
        }
    }

    private float changeAngle(float adjustment) {
        float xAngle = degrees(PVector.angleBetween(ballDirection, xAxis));
        if (xAngle < 70 || (xAngle > 110 && xAngle < 250) || xAngle > 290) {
            log("xAngle < 70: %s", xAngle);
            ballDirection.y += adjustment;
        } else {
            log("xAngle > 70: %s", xAngle);
        }
        return (xAngle - abs(degrees(PVector.angleBetween(ballDirection, xAxis))));

    }

    public void keyPressed() {
        // slow so far.
        if (keyCode == UP) {
            movements[0].y = -10;
        } else if (keyCode == DOWN) {
            movements[0].y = 10;
        }

    }

    String previousLogMessage = "";
    void log(String pattern, Object... args) {
        String msg = String.format(pattern, args);
        if (!msg.equals(previousLogMessage))
            System.out.println(msg);

        previousLogMessage = msg;
    }
    int paddleMiddle(int i) {
        return (int)(paddles[i].y + paddleHeight / 2);
    }
    public void mouseMoved() {
        movements[0].y = mouseY - paddleMiddle(0);
        tick = (++tick) % recentMovements.length;
        recentMovements[tick] = movements[0].y;
    }

    private void drawPaddles() {
        fill(51, 255, 102);
        for (int i = 0; i < 2; i++) {
            paddles[i].add(movements[i]);
            ensureInsideBorders(i);
            rect(paddles[i].x, paddles[i].y, paddleWidth, paddleHeight);

            movements[i].y = 0;
        }
    }

    private void ensureInsideBorders(int i) {
        paddles[i].y = min(maxPaddle, max(minPaddle, (int)paddles[i].y));
    }

    private void drawBorders() {

        noFill();
        strokeWeight(5);
        stroke(64);
        line(width / 2, 5, width / 2, height - 10);
        stroke(128);
        rect(5, 5, width - 10, height - 10);
    }

    private void drawScores() {
        textFont(font);
        text("" + scores[0], width/2 - 50, 50);
        text("" + scores[1], width/2 + 50, 50);
    }
}
