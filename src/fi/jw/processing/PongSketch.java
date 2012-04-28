package fi.jw.processing;

import processing.core.*;

public class PongSketch extends PApplet {

    /**
     *
     */
    private static final long serialVersionUID = 4489382234265685537L;

    PVector[] paddles = new PVector[2];
    PVector[] movements = new PVector[2];
    PVector ballPosition, ballDirection;
    int[] scores = new int[2];
    PFont font;

    final int paddleWidth = 16;
    final int paddleHeight = 128;
    final int padding = 16;

    int maxPaddle;
    int minPaddle = padding;

    public void setup() {
        smooth();
        size(1024, 768);
        maxPaddle = (height - padding - paddleHeight);
        background(0, 0, 0);
        noStroke();
        paddles[0] = new PVector(padding, padding, 0);
        paddles[1] = new PVector(width - paddleWidth - padding, padding, 0);
        movements[0] = new PVector(0, 0, 0);
        movements[1] = new PVector(0, 0, 0);
        ballPosition = new PVector(width / 2, height / 2);
        boolean goingLeft = (random(-1,1) > 0);
        float angle = radians(random(0,90) + (goingLeft ? 90 : 0));

        ballDirection = new PVector(cos(angle), sin(angle), 0);
        ballDirection.mult(5);
        scores[0] = 0;
        scores[1] = 1;
        //font = loadFont("Courier-48.vlw");
//	  textFont(font);
    }

    public void draw() {
        frameRate(60);
        pushMatrix();
        noStroke();
        strokeWeight(0);
        background(0);
        drawBall();
        drawPaddles();
        drawBorders();
        drawScores();
        popMatrix();
    }

    private void drawBall() {
        moveBall();
        fill(128);
        rect(ballPosition.x, ballPosition.y, 16, 16);
    }

    private void moveBall() {
        ballPosition.add(ballDirection);
        int ballPadding = padding + paddleWidth;
        if (ballPosition.x < ballPadding || ballPosition.x > (width - ballPadding))
            ballDirection.x *= -1;

        if (ballPosition.y < ballPadding || ballPosition.y > (height - ballPadding))
            ballDirection.y *= -1;

        movements[1].y = (ballPosition.y - paddleMiddle(1)) / 2;
    }

    public void keyPressed() {
        // slow so far.
        if (keyCode == UP) {
            movements[0].y = -10;
        } else if (keyCode == DOWN) {
            movements[0].y = 10;
        }

    }

    int paddleMiddle(int i) {
        return (int)(paddles[i].y + paddleHeight / 2);
    }
    public void mouseMoved() {
        movements[0].y = mouseY - paddleMiddle(0);
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
//		text("" + scores[0], width/2 - 50, 50);
//		text("" + scores[1], width/2 + 50, 50);
    }
}
