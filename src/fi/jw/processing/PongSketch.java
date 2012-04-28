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
	final int padding = 16;

	public void setup() {
	  smooth();
	  size(1024, 768);
	  background(0, 0,0);
	  noStroke();
	  paddles[0] = new PVector(padding,padding,0);
	  paddles[1] = new PVector(width - paddleWidth - padding, padding,0);
	  movements[0] = new PVector(0,0,0);
	  movements[1] = new PVector(0,0,0);
	  ballPosition = new PVector(width/2, height/2);
	  //int goingLeft = (random(-1,1) > 0) ? -1 : 1;
	  ballDirection =  new PVector(0,0,0);// new PVector(goingLeft * 2, 0);
	  scores[0] = 0;
	  scores[1] = 1;
	  //font = loadFont("Courier-48.vlw");
//	  textFont(font);
	}

	public void draw() {
       noStroke();
       strokeWeight(0);
	   background(0);
	   drawBall();
	   drawPaddles();
	   drawBorders();
	   drawScores();
	}

	private void drawBall() {
		ballPosition.add(ballDirection);
		fill(128);
		rect(ballPosition.x, ballPosition.y, 16, 16);
	}
	
	public void keyPressed() {
		if (keyCode == UP) {
			movements[0].y = -2;
		} else if (keyCode == DOWN) {
			movements[0].y = 2;
		}
		
	}

	private void drawPaddles() {
		fill(51,255,102);
		for (int i = 0; i < 2; i++) {
			paddles[i].add(movements[i]);
			rect(paddles[i].x,paddles[i].y,16,128);
		
			movements[i].y = 0;
		}
	}

	private void drawBorders() {
		noFill();
		strokeWeight(5);
		stroke(64);
		line(width/2,5,width/2,height-10);
		stroke(128);
		rect(5,5,width-10,height-10);
	}

	private void drawScores() {
//		text("" + scores[0], width/2 - 50, 50);
//		text("" + scores[1], width/2 + 50, 50);
	}
}
