package fi.jw.processing;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

public class CoordinatesSketch extends PApplet {

    int step = 20;
    PFont font;
    int framerate = 60;
    float gravity = 9.81f / framerate; // 9.81 / s^2 ; framerate frames per second => increment by 1/framerate every frame;

    List<Ball> balls = new ArrayList<Ball>();

    class Ball {
        PVector pos;
        PVector motion;

        Ball(float x, float y) {
            pos = new PVector(x, y);
            motion = new PVector(0, 0);
        }

        void update() {
            if (withinBounds()) {
                motion.y += gravity;
                pos.add(motion);
                draw();
            }
        }

        boolean withinBounds() {
            return pos.x > 0 && pos.x < width && pos.y > 0 && pos.y < height;
        }

        void draw() {
            ellipseMode(CENTER);
            noStroke();
            fill(255, 255 * (pos.y / height), 255 * (pos.y / height));
            ellipse(pos.x, pos.y, step, step);
        }
    }

    public void setup() {
        size(800, 600);
        frameRate(framerate);
        stroke(0, 0, 0); // black
        font = createFont("Courier New", 16);
        textFont(font);
    }

    public void draw() {
        stroke(0);
        background(255, 255, 255); // white
        drawGrid();
        drawCoordinates();
        List<Ball> toBeRemoved = new ArrayList<Ball>();
        for (Ball b : balls) {
            if (b.withinBounds())
                b.update();
            else
                toBeRemoved.add(b);
        }

        balls.removeAll(toBeRemoved);
        drawBallCount();
    }

    void drawGrid() {
        drawMajorLines();
        drawMinorLines();
    }

    public void mouseClicked() {
        balls.add(new Ball(mouseX, mouseY));
    }

    public void mouseDragged() {
        mouseClicked();
    }


    void drawMajorLines() {
        strokeWeight(5f);
        line(0, height / 2, width, height / 2); // X
        line(width / 2, 0, width / 2, height); // Y
    }

    void drawMinorLines() {
        strokeWeight(1f);
        for (int x = step; x < width; x += step) {
            line(x, 0, x, height);
        }
        for (int y = step; y < height; y += step) {
            line(0, y, width, y);
        }
    }

    void drawCoordinates() {
        strokeWeight(1f);
        // processing coordinates
        fill(225, 225, 225); // grey
        rect(0, 0, 80, 20);
        fill(0);
        text(String.format("%s,%s", mouseX, mouseY), 5, 15);

        // origo coordinates
        fill(225, 225, 225); // grey
        rect(width - 80, 0, 80, 20);
        fill(0);
        text(String.format("%s,%s", round((mouseX - (width / 2)) / step), round(((height / 2) - mouseY) / step)), width - 75, 15);

    }

    void drawBallCount() {
        stroke(255, 0, 0);
        fill(225, 225, 225); // grey
        rect(0, height - 20, 80, 20);
        fill(0);
        text(String.format("%s balls", balls.size()), 5, height - 5);
    }
}
