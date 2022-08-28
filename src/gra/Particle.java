package gra;

import static org.lwjgl.opengl.GL11.*;//importowanie calego OpenGL 11

import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class Particle {
	private float x, y, radius;//kolejno - pozycja x i y czasteczki i jej promien
	public float size;//rozmiar i jednoczesnie przezroczystosc czasteczki
	private CircleShadow shadow;//cien czasteczki
	
	public Particle(float x, float y, float radius, Texture shadowTexture) {
		this.x = x;
		this.y = y;
		this.radius = radius;
		this.size = 1;
		if(Config.shadows)
			shadow = new CircleShadow(0.1f, shadowTexture);
	}
	public void render(float delta) {
		//plynne zmniejszanie rozmiaru i przezroczystosci czasteczki
	   	if(size > 0) {
	   		size -= delta / Config.particleTime;//liniowo
 			//size -= (float)Math.pow((delta / Config.particleTime) / size, 1.2f);//parabolicznie
	   	} 
	   	else if (size < 0)
 			size = 0;
	   	//renderuje pojedynczy obszar czasteczki
	    glColor4f(Config.particleColor.r,Config.particleColor.g,Config.particleColor.b, size);//kolor bialy bez przezroczystosci
		glBegin(GL_QUADS);
	      glTexCoord2f(0, 0);
	      glVertex2f(x - radius*size*Config.visibleParticleSize, y - radius*size*Config.visibleParticleSize);
	      glTexCoord2f(1, 0);
	      glVertex2f(x + radius*size*Config.visibleParticleSize, y - radius*size*Config.visibleParticleSize);
	      glTexCoord2f(1, 1);
	      glVertex2f(x + radius*size*Config.visibleParticleSize, y + radius*size*Config.visibleParticleSize);
	      glTexCoord2f(0, 1);
	      glVertex2f(x - radius*size*Config.visibleParticleSize, y + radius*size*Config.visibleParticleSize);
	   glEnd();
	}
	//renderuje cien czasteczki
	public void renderShadow() {
		if(Config.shadows)
	   		shadow.render(new Vector2f(x, y), (radius*size*Config.visibleParticleSize));
	}
}
