
int step = 20; // kokeile muuttaa tata.
int framerate = 60; // taman asettaminen pienemmaksi saa taman nayttamaan lagaavalta. kokeile muuttaa esim 1, 10, 50, 100
float gravity = 9.81f / framerate; /// fysiikkaa: painovoiman takia nopeus kasvaa 9.81 pikselia per sekunti joka sekunti
                                   /// joten tarvitaan tietaa montako pikselia pitaa liikkua per frame

//// alustetaan ohjelmaan oikea koko ja nopeus.
void setup() {
    size(800, 600);
    frameRate(framerate);
    stroke(0, 0, 0); // black
    font = createFont("Courier New", 16);
    textFont(font);
}

//// tata kutsutaan automaattisesti kokoajan.
//// framerate kertoo kuinka monta kertaa sekunnissa
//// elokuvat ovat yleensa 30 fps
//// pelit toivottavasti yli 50 fps.
//// tuossa ylla asetettu etta tama pyrkii olemaan 60 fps.

void draw() {
    stroke(0, 0, 0); // 0 black
    background(255, 255, 255); // white

    drawGrid();
    drawCoordinates();
   
    //// kaydaan jokainen ruudulla oleva pallo lapi ja katsotaan onko se oikeasti viela ruudulla
    //// jos se on mennyt jo yli laidan niin voidaan poistaa se listalta, ettei turhaan vie muistia
    for (int i = balls.size()-1; i >= 0; i--) { 
	    Ball ball = (Ball) balls.get(i);

		if (ball.withinBounds())
            ball.update();
        else
            balls.remove(i);

  	}  
    drawBallCount();
}


//// koordinaatisto
void drawGrid() {
    drawMajorLines();
    drawMinorLines();
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
    fill(0, 0, 0); // black
    text(mouseX + "," +mouseY, 5, 15);

    // origo coordinates
    fill(225, 225, 225); // grey
    rect(width - 80, 0, 80, 20);
    fill(0);
    text(round((mouseX - (width / 2)) / step) + "," + round(((height / 2) - mouseY) / step), width - 75, 15);

}

void drawBallCount() {
    stroke(255, 0, 0);
    fill(225, 225, 225); // grey
    rect(0, height - 20, 80, 20);
    fill(0);
    text(balls.size() + " balls", 5, height - 5);
}


void mouseClicked() {
    balls.add(new Ball(mouseX, mouseY));
}

void mouseDragged() {
    mouseClicked();
}

//////////////////////////////////////////////////////////////
//////// 
//////// Pallojen toiminnallisuus
//////// sis. varityksen ja liikkeen (putoamisnopeus)
////////
////////////////////////////////////////////////////////

ArrayList balls = new ArrayList();

class Ball {
    PVector pos;
    PVector motion;

    Ball(float x, float y) {
        pos = new PVector(x, y);
        motion = new PVector(0, 0);
    }

    void update() {
        if (withinBounds()) {
        	// esimerkkeja:
        	
        	// putoaa alas:
            motion.y += gravity; 
            
            // putoaa ylos
			// motion.y -= gravity; 
			
			//putoaa sivulle:
            //motion.x -= gravity;

            // putoaa kuin kovassa tuulessa:
            // motion.y += gravity * 0.5; 
            // motion.x += gravity * 2; 
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
        float alpha = (pos.y / height);
        float red	= 255;
        float green = 255 * alpha;
        float blue  = 255 * alpha;
        fill(red, green, blue);
        ellipse(pos.x, pos.y, step, step);
    }
}


