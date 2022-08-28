package gra;

import static org.lwjgl.opengl.GL11.*;//importowanie calego OpenGL 11
import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class Damage {
	private Texture texture;//textura obrazen

	public void setTexture(Texture texture) {
		this.texture = texture;
	}
	public void render(Vector2f pos, float rotation, float radius) {
		//renderowanie warstwy obrazen na przeciwniku
		glPushMatrix();//blokowanie zmian matrixowych dla poprzednio wyrenderowanych obiektow
	    glTranslatef(pos.x, pos.y, 0); //przesuwanie obrazu tak by obrocic obiekt wzgledem jego srodka
	    glRotatef(rotation, 0, 0, 1);//obracanie obiektu
	    glTranslatef(-pos.x, -pos.y, 0);//ponowne przesuniecie obrazu na wczesniejsza pozycje
	    
	    //renderownaie figury z textura obiektu
	    if(texture != null)
	    	glBindTexture(GL_TEXTURE_2D, texture.getTextureID());//texture.bind();//ustawienie textury
	    else
	    	glBindTexture(GL_TEXTURE_2D, 0);//usuwanie textury z opengl dla nastepnych polygonow
	    glColor3f(1, 1, 1);//kolor bialy bez przezroczystosci
	    glBegin(GL_QUADS);
	      glTexCoord2f(0, 0);
	      glVertex2f(pos.x - radius, pos.y - radius);
	      glTexCoord2f(1, 0);
	      glVertex2f(pos.x + radius, pos.y - radius);
	      glTexCoord2f(1, 1);
	      glVertex2f(pos.x + radius, pos.y + radius);
	      glTexCoord2f(0, 1);
	      glVertex2f(pos.x - radius, pos.y + radius);
	   glEnd();
	   
	   glPopMatrix();//puszczenie matrixa (zamkniecie kodu od wywolania fukncji glPushMatrix())
	}
}
