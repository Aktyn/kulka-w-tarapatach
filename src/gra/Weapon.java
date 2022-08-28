package gra;

import static org.lwjgl.opengl.GL11.*;//importowanie calego OpenGL 11

import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class Weapon {
	private Texture doubleCannon;//textura dzialek
	private Texture portalGun;//textura portalGuna
	private Texture bomb;//textura bomby
	private Vector2f pos;//srodek obiektu
	private float radius;//promien obiektu
	private float rotation;//rotacja obiektu
	public int weaponState = 0;//stan broni (przelaczanie miedzy dzialkami a portal gunem)

	public Weapon() {
		this.pos = new Vector2f(0.0f, 0.0f);
		this.radius = 0.0f;
		this.rotation = 0.0f;
	}
	//ustawia texture
	public void setTexture(Texture[] textures) {
		this.doubleCannon = textures[0];
		this.portalGun = textures[1];
		this.bomb = textures[2];
	}
	//aktualizowanie parametrow
	public void update(Vector2f pos, float radius, float rotation) {
		this.pos = pos;
		this.radius = radius;
		this.rotation = rotation;
	}
	//renderowanie obiektu broni gracza
	public void render() {
		glPushMatrix();//blokowanie zmian matrixowych dla poprzednio wyrenderowanych obiektow
	    glTranslatef(pos.x, pos.y, 0); //przesuwanie obrazu tak by obrocic obiekt wzgledem jego srodka
	    glRotatef(rotation, 0, 0, 1);//obracanie obiektu
	    glTranslatef(-pos.x, -pos.y, 0);//ponowne przesuniecie obrazu na wczesniejsza pozycje
	    
	    //renderownaie figury z textura obiektu
	    switch(weaponState) {
	    case 0://dzialka
		    if(doubleCannon != null)
		    	doubleCannon.bind();//ustawienie textury dzialka
		    else
		    	glBindTexture(GL_TEXTURE_2D, 0);//usuwanie textury z opengl dla nastepnych polygonow
		    break;
	    case 1://portalGun
		    if(portalGun != null)
		    	portalGun.bind();//ustawienie textury portalguna
		    else
		    	glBindTexture(GL_TEXTURE_2D, 0);//usuwanie textury z opengl dla nastepnych polygonow
		    break;
	    case 2://bomba
	    	if(bomb != null)
	    		bomb.bind();//ustawienie txtury bomby
	    	else
	    		glBindTexture(GL_TEXTURE_2D, 0);//usuwanie textury z opengl dla nastepnych polygonow
	    	break;
	    }
	    glColor3f(1,1,1);//kolor bialy bez przezroczystosci
	    glBegin(GL_QUADS);
	      glTexCoord2f(0, 0);
	      glVertex2f(pos.x - (radius*0.75f), pos.y - (radius));
	      glTexCoord2f(1, 0);
	      glVertex2f(pos.x + (radius*1.25f), pos.y - (radius));
	      glTexCoord2f(1, 1);
	      glVertex2f(pos.x + (radius*1.25f), pos.y + (radius));
	      glTexCoord2f(0, 1);
	      glVertex2f(pos.x - (radius*0.75f), pos.y + (radius));
	   glEnd();
	   
	   glPopMatrix();//puszczenie matrixa (zamkniecie kodu od wywolania fukncji glPushMatrix())
	}
}
