package gra;

import static org.lwjgl.opengl.GL11.*;//importowanie calego OpenGL 11

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import gui.Bounds;

import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.Sound;
import org.newdawn.slick.opengl.Texture;

public class Boss {
	public Vector2f pos;//pozycja bossa
	private float height;//zmienna uzywana do plynnego zmieniania wysokosci na ktorej znajduje sie boss
	public float radius;//promien bossa
	public float hp;//jego zycie
	public float rotation;//rotacja
	private float speed;//predkosc poruszania sie bossa
	private float visibleRotation;//widoczna rotacja
	private Texture texture;//textura
	private Texture[] bulletTexture;//textura pociskow bossa
	private Bounds bounds;//i obszar po ktorym moze sie poruszac
	private Random random;//obiekt losujacy wartosci
	private float refreshTime = 0.5f;//co ile czasu ma byc wprowadzana losowa zmiana parametrow bossa
	private float timeToRefresh = 0.0f;//odliczanie do powyzej opisanego zjawiska
	private int goDir = 1;//1 - lewo, -1 - prawo, definiuje to w  ktora strone przesuwa sie gracz po mapce
	public List<Bullet> bullets;//pociski wystrzeliwane przez bossa
	private Vector2f[][] collisionLines;//vektory takie same jak w scianach (obliczana kolizja na ich podstawie)
	private Sound shootSound;//dzwiek strzalu
	
	public Boss() {
		//nadawanie domyslnych poczatkowych wartosci dla zmiennych
		this.rotation = 0;
		this.visibleRotation = 0;
		this.speed = 0.15f;
		this.hp = Config.gameType != 2 ? Config.bossHp : Config.bossHp - 75.0f*(2-Config.difficult);//ilosc zycia zalezna od poziomu trudnosci
		this.radius = Config.bossRadius;
		this.pos = new Vector2f(0.5f, 5.0f-radius);
		this.height = 1.0f - radius;
		this.random = new Random();
		this.bullets = new ArrayList<Bullet>();//tworzenie nowej pustej listy pociskow
	}
	//ustawia obszar na ktorym gracz moze sie poruszac
	public void setBounds(float x, float y, float width, float height) {
		this.bounds = new Bounds(x, y, width, height);
	}
	//ustawia dzwiek strzelania
	public void setShootSound(Sound sound) {
		this.shootSound = sound;
	}
	//ustawia texture bossa
	public void setTexture(Texture texture) {
		this.texture = texture;
	}
	//ustawia texture pocisku
	public void setBulletTexture(Texture texture) {
		this.bulletTexture = new Texture[]{texture};
	}
	//przypisuje vektory kolizji
	public void setCollisionLines(Vector2f[][] vectors) {
		this.collisionLines = vectors;
	}
	//strzelanie
	private void shoot() {
		//obliczanie pozycji dwoch dzialek z ktorych wylaniaja sie pociski
		Vector2f bullet1 = new Vector2f(pos);
		bullet1.y -= radius*0.8f*Math.cos(Math.toRadians(visibleRotation-45f));
		bullet1.x -= radius*0.8f*Math.sin(Math.toRadians(visibleRotation-45f));
		Vector2f bullet2 = new Vector2f(pos);
		bullet2.y -= radius*0.8f*Math.cos(Math.toRadians(visibleRotation+45f));
		bullet2.x -= radius*0.8f*Math.sin(Math.toRadians(visibleRotation+45f));
		//i dodawanie tych dwoch pociskow
		bullets.add(new Bullet(bullet1, 180+visibleRotation, Config.playerRadius*Config.weaponRadiusRatio*0.35f, collisionLines, bounds, bulletTexture, 0));
		bullets.add(new Bullet(bullet2, 180+visibleRotation, Config.playerRadius*Config.weaponRadiusRatio*0.35f, collisionLines, bounds, bulletTexture, 0));
		if(Config.enableSounds)
			shootSound.play(1.0f, Config.effectsVolume);//dzwiek strzalu
	}
	//obliczenia zwiazane z bossem
	private void update(float delta) {
		if(timeToRefresh < refreshTime)
			timeToRefresh += delta;//liczenie czasu do kolejnych decyzji bossa
		else {//refresh
			timeToRefresh = 0.0f;
			rotation += random.nextInt(720*45)/360.0f-45;
			height += (random.nextInt(2000)/1000f-1)*radius;//random.nextInt((int)(1000*radius))/1000-radius*2f;
			if(rotation > 45)//maksymalna rotacja
				rotation = 45;
			if(rotation < -45)//minimalna rotacja
				rotation = -45;
			if(height > 1.0f-radius)//maksymalna wysokosc
				height = 1.0f-radius;
			if(height < 1.0f-radius*2f)//minimalna wysokosc
				height = 1.0f-radius*2f;
			//losowanie strzalu
			if(random.nextInt(100) > 80) {//20 procent szans na
				shoot();//strzal
			}
		}
		//utrzymywanie bossa w pewnym obszarze
		if(Math.abs(pos.y-height) > 0.01f) {
			pos.y -= (pos.y-height)*delta*2;
		}
		if(Math.abs(visibleRotation-rotation) > 0.1f) {
			visibleRotation -= (visibleRotation-rotation)*delta*4;
		}
		pos.x += goDir*speed*delta;
		if(pos.x >= 1-radius || pos.x <= radius)
			goDir = -goDir;//odwracanie kierunku
	}
	//renderowanie obiektu bossa
	public void render(float delta) {
		update(delta);
	    
	    glPushMatrix();//blokowanie zmian matrixowych dla poprzednio wyrenderowanych obiektow
	    glTranslatef(bounds.x + (pos.x)*bounds.width, bounds.y + (1-pos.y)*bounds.height, 0); //przesuwanie obrazu tak by obrocic obiekt wzgledem jego srodka
	    glRotatef(visibleRotation, 0, 0, 1);//obracanie obiektu
	    glTranslatef(-(bounds.x + (pos.x)*bounds.width), -(bounds.y + (1-pos.y)*bounds.height), 0);//ponowne przesuniecie obrazu na wczesniejsza pozycje
	    
	    //renderownaie figury z textura obiektu
	    if(texture != null)
	    	glBindTexture(GL_TEXTURE_2D, texture.getTextureID());//texture.bind();//ustawienie textury
	    else
	    	glBindTexture(GL_TEXTURE_2D, 0);//usuwanie textury z opengl dla nastepnych polygonow
	    glColor3f(1.0f, 1.0f, 1.0f);//kolor obiektu bez przezroczyscosci
	    glBegin(GL_QUADS);
	      glTexCoord2f(0, 0);
	      glVertex2f((bounds.x + (pos.x)*bounds.width) - radius*bounds.width, (bounds.y + (1-pos.y)*bounds.height) - radius*bounds.height);
	      glTexCoord2f(1, 0);
	      glVertex2f((bounds.x + (pos.x)*bounds.width) + radius*bounds.width, (bounds.y + (1-pos.y)*bounds.height) - radius*bounds.height);
	      glTexCoord2f(1, 1);
	      glVertex2f((bounds.x + (pos.x)*bounds.width) + radius*bounds.width, (bounds.y + (1-pos.y)*bounds.height) + radius*bounds.height);
	      glTexCoord2f(0, 1);
	      glVertex2f((bounds.x + (pos.x)*bounds.width) - radius*bounds.width, (bounds.y + (1-pos.y)*bounds.height) + radius*bounds.height);
	   glEnd();
	   
	   glPopMatrix();//puszczenie matrixa (zamkniecie kodu od wywolania fukncji glPushMatrix())
	}
}
