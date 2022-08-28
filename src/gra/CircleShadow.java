package gra;

import static org.lwjgl.opengl.GL11.*;//importowanie calego OpenGL 11

import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class CircleShadow {
	private float alfa;//przezroczystosc cienia
	private Texture shadowTexture;//textura cienia
	
	public CircleShadow(float alfa, Texture shadowTexture) {
		this.alfa = alfa;
		this.shadowTexture = shadowTexture;
	}
	//zmienia przezroczystosc cienia
	public void setAlfa(float alfa) {
		this.alfa = alfa;
	}
	//ustawia texture okraglego cienia
	public void setTexture(Texture texture) {
		this.shadowTexture = texture;
	}
	//renderuje okragly cien pod obiektami
	public void render(Vector2f pos, float radius) {
		//przesuwanie pozycji cienia w prawo i dol
		pos.x += radius*0.3f;
		pos.y += radius*0.3f;
		if(shadowTexture != null)//jesli textura cienia nie jest pusta
			glBindTexture(GL_TEXTURE_2D, shadowTexture.getTextureID());//renderowanie textury cienia
		else
			glBindTexture(GL_TEXTURE_2D, 0);//renderowanie bez textury
		glColor4f(0, 0, 0, alfa);//kolor czarny z kanalem alfa
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
	}
}
