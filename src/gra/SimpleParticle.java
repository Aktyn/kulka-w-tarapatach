package gra;

import static org.lwjgl.opengl.GL11.*;//importowanie calego OpenGL 11

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

public class SimpleParticle {
	private float x;//pozycja x czasteczki
	private float y;//pozycja y czasteczki
	private float radius;//jej promien
	private Vector4f color;//kolor czasteczki
	private float rotation;//kierunek poruszania sie czasteczki
	private float speed;//predkosc czasteczki
	private boolean visualRotate;//czy textura czasteczki ma byc widocznie obrocona?
	
	public SimpleParticle(float x, float y, float radius, float rotation, float speed, boolean visualRotate) {
		this.x = x;
		this.y = y;
		this.radius = radius;
		this.color = new Vector4f(1f, 1f, 1f, 1f);//domyslnie bialy kolor czasteczki
		this.rotation = rotation;
		this.speed = speed;
		this.visualRotate = visualRotate;
	}
	//ustawia promien czasteczki
	public void setRadius(float newRadius) {
		this.radius = newRadius;
	}
	//porusza czasteczke z dana predkoscia zaleznie od obrotu
	public void move(float speed) {
		x += this.speed*speed*(float)Math.sin(Math.toRadians(rotation));
		y += this.speed*speed*(float)Math.cos(Math.toRadians(rotation));
	}
	//zmienia kolor czasteczki
	public void setColor(float r, float g, float b, float alfa) {
		this.color.x = r;
		this.color.y = g;
		this.color.z = b;
		setAlfa(alfa);
	}
	//zmienia przezroczystossc
	public void setAlfa(float alfa) {
		this.color.w = alfa;
	}
	//zwraca wartosc kanalu alfa
	public float getAlfa() {
		return this.color.w;
	}
	//zwraca vektor pozycji
	public Vector2f getPos() {
		return new Vector2f(x, y);
	}
	
	public void render() {
		if(visualRotate) {
			glPushMatrix();//blokowanie zmian matrixowych dla poprzednio wyrenderowanych obiektow
		    glTranslatef(x, y, 0); //przesuwanie obrazu tak by obrocic obiekt wzgledem jego srodka
		    glRotatef(-rotation, 0, 0, 1);//obracanie obiektu
		    glTranslatef(-x, -y, 0);//ponowne przesuniecie obrazu na wczesniejsza pozycj
		}
		
		glColor4f(color.x, color.y, color.z, color.w);//kolor bialy bez przezroczystosci
		glBegin(GL_QUADS);
	      glTexCoord2f(0, 0);
	      glVertex2f(x - radius, y - radius);
	      glTexCoord2f(1, 0);
	      glVertex2f(x + radius, y - radius);
	      glTexCoord2f(1, 1);
	      glVertex2f(x + radius, y + radius);
	      glTexCoord2f(0, 1);
	      glVertex2f(x - radius, y + radius);
	   glEnd();
	   
	   if(visualRotate) 
		   glPopMatrix();//puszczenie matrixa (zamkniecie kodu od wywolania fukncji glPushMatrix())
	}
}
