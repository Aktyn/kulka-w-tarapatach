package gra;

import static org.lwjgl.opengl.GL11.*;//importowanie calego OpenGL 11

import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class Consequent {
	public float durationTime;//czas trwania efektu
	public int index;//rodzaj efektu
	public float posY;//pozycja efektu na liscie
	private float radius;//promien ikonki
	public Consequent(int index, float posY) {
		this.posY = posY;
		this.index = index;
		this.durationTime = Config.effectsDurations[index];
		this.radius = Config.effectsIconRadius;
	}
	//renderowanie ikonki z paskiem odliczajacym do konca trwania efektu
	public void render(Texture texture, float x, float y, float width, float height) {
		//obliczanie widocznej pozycji obiektu
		Vector2f visiblePos = new Vector2f(x + width - radius*width, Config.height - (y + height - radius * 2 * (1+posY) * height));
		
		//renderowanie ikonki
		if(texture != null)
			glBindTexture(GL_TEXTURE_2D, texture.getTextureID());//texture.bind();//ustawienie textury
	    else
	    	glBindTexture(GL_TEXTURE_2D, 0);//usuwanie textury z opengl dla nastepnych polygonow
		glColor4f(1.0f, 1.0f, 1.0f, 0.7f);//polprzezroczysty bialy kolor
		glBegin(GL_QUADS);
	      glTexCoord2f(0, 0);
	      glVertex2f(visiblePos.x - radius*width, visiblePos.y - radius*height);
	      glTexCoord2f(1, 0);
	      glVertex2f(visiblePos.x + radius*width, visiblePos.y - radius*height);
	      glTexCoord2f(1, 1);
	      glVertex2f(visiblePos.x + radius*width, visiblePos.y + radius*height);
	      glTexCoord2f(0, 1);
	      glVertex2f(visiblePos.x - radius*width, visiblePos.y + radius*height);
	   glEnd();
	   
	   //renderowanie paska odliczania czasu
	   float percent = (durationTime / Config.effectsDurations[index]);
	   float length = Config.effectsTimeBarLength * width * percent;
	   visiblePos.x = x + width - radius*2.2f*width - length;
	   glBindTexture(GL_TEXTURE_2D, 0);//usuwanie textury z opengl dla nastepnych polygonow
	   glColor4f(1.0f-(durationTime / Config.effectsDurations[index]), percent, 0.0f, 0.7f);//polprzezroczysty bialy kolor
		glBegin(GL_QUADS);
	      glTexCoord2f(0, 0);
	      glVertex2f(visiblePos.x, visiblePos.y - Config.effectsTimeBarHeight*height*0.5f);
	      glTexCoord2f(1, 0);
	      glVertex2f(visiblePos.x + length, visiblePos.y - Config.effectsTimeBarHeight*height*0.5f);
	      glTexCoord2f(1, 1);
	      glVertex2f(visiblePos.x + length, visiblePos.y + Config.effectsTimeBarHeight*height*0.5f);
	      glTexCoord2f(0, 1);
	      glVertex2f(visiblePos.x, visiblePos.y + Config.effectsTimeBarHeight*height*0.5f);
	   glEnd();
	}
}
