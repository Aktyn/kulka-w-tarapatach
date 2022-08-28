package gui;

import static org.lwjgl.opengl.GL11.*;//importowanie calego OpenGL 11
import gra.Config;

import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.opengl.Texture;

public class Image implements GuiPart {
	private Bounds bounds;//prostokatny obszar na ktorym wyswietlany jest obrazek
	private Texture texture;//textura obrazka
	private Vector3f color;//kolor obrazka
	private float alfa;//przezroczystosc textury
	private float rotation = 0;//rotacja obiektu
	
	public Image() {
		this.bounds = new Bounds(0, 0, 100, 100);//domyslne wartosci
		this.color = new Vector3f(1f, 1f, 1f);//domyslnie bialy
		this.alfa = 1;//nieprzezroczysty
	}
	//przypisanie textury
	public void setTexture(Texture texture) {
		this.texture = texture;
	}
	//aktualizacja obszaru
	public void setBounds(float x, float y, float width, float height) {
		this.bounds.x = x;
		this.bounds.y = y;
		this.bounds.width = width;
		this.bounds.height = height;
	}
	//ustawia rotacje obiektu
	public void setRotation(float rotation) {
		this.rotation = rotation;
	}
	//ustawia kolor
	public void setColor(float r, float g, float b) {
		this.color.x = r;
		this.color.y = g;
		this.color.z = b;
	}
	//ustawia kanal alfa
	public void setAlfa(float alfa) {
		this.alfa = alfa;
	}
	//zwraca wartosc kanalu alfa
	public float getAlfa() {
		return this.alfa;
	}
	//zwraca obszar
	public Bounds getBounds() {
		return bounds;
	}
	//sprawdzanie czy obiekt jest nasluchiwany (obrazek nigdy)
	public boolean getListener() {
		return false;
	}
	public void render() {
		if(rotation != 0) {//obracanie obiektu
			glPushMatrix();//blokowanie zmian matrixowych dla poprzednio wyrenderowanych obiektow
		    glTranslatef(bounds.x+bounds.width*0.5f, Config.height-bounds.y-bounds.height*0.5f, 0); //przesuwanie obrazu tak by obrocic obiekt wzgledem jego srodka
		    glRotatef(rotation, 0, 0, 1);//obracanie obiektu
		    glTranslatef(-(bounds.x+bounds.width*0.5f), -(Config.height-bounds.y-bounds.height*0.5f), 0);//ponowne przesuniecie obrazu na wczesniejsza pozycje
		}
		
		if(texture != null) {//jesli textura tla nie jest pusta
			texture.bind();
			//renderowanie obrazka
			glBegin(GL_QUADS);
		      glColor4f(color.x, color.y, color.z, alfa);//domyslnie bialy kolor
		      glTexCoord2f(0, 0);
		      glVertex2f(bounds.x, Config.height-bounds.y-bounds.height);
		      glTexCoord2f(1, 0);
		      glVertex2f(bounds.x+bounds.width, Config.height-bounds.y-bounds.height);
		      glTexCoord2f(1, 1);
		      glVertex2f(bounds.x+bounds.width, Config.height-bounds.y);
		      glTexCoord2f(0, 1);
		      glVertex2f(bounds.x, Config.height-bounds.y);
		   glEnd();
		}
		else
			glBindTexture(GL_TEXTURE_2D, 0);//usuwanie textury z opengl dla nastepnych polygonow
		
		if(rotation != 0)
			 glPopMatrix();//puszczenie matrixa (zamkniecie kodu od wywolania fukncji glPushMatrix())
	}

	//czyszczenie przed zniszczeniem kontrolki
	public void clear() {
		
	}
}
