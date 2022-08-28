package gra;

import static org.lwjgl.opengl.GL11.*;//importowanie calego OpenGL 11
import gui.Bounds;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class Explosion {
	private int type = 0;//typ explozji (0 - zwykly wybuch, 1 - iskry po pocisku, 2 - wybuch bomby)
	private Vector2f explosionPos;//srodek eksplozji
	private List<SimpleParticle> particles;//lista czasteczek
	private Texture texture;//textura dla czasteczek
	private Bounds bounds;//obszar mapy
	public float explosionTime = 0;

	public Explosion(int type, Vector2f center, Texture texture, Bounds bounds) {
		this.particles = new ArrayList<SimpleParticle>();//nowa pusta lista czasteczek
		this.explosionPos = center;
		this.texture = texture;
		this.bounds = bounds;
		this.type = type;
		generateParticles();
	}
	//tworzy poczatkowe czasteczki w punkcie wybuchu z odpowiednimi parametrami zaleznie od typu wybuchu
	private void generateParticles() {
		Random random = new Random();//obiekt generujacy losowe liczby
		switch(type) {
		case 0://zwykly wybuch
			for(int i=0; i<Config.hitParticles; i++) {//tworzenie nowych czasteczek
				float randomSize = ((random.nextInt(500)+500)/100000f)*1.5f;
				float particleDirection = random.nextInt(36000)/100f;
				float randomSpeed = random.nextInt(1000)/1000f;
				particles.add(new SimpleParticle(bounds.x + explosionPos.x*bounds.width, bounds.y+explosionPos.y*bounds.height, bounds.height*randomSize, particleDirection, randomSpeed, false));
				particles.get(particles.size()-1).setColor(1, 1f-(float)Math.pow(randomSpeed,2) -0.1f + random.nextInt(100)/1000f, 0, 1);//losowy kolor pomiedzy zoltym a czerwonym
			}
		break;
		case 1://efekt uderzenia pocisku
			for(int i=0; i<Config.shootParticles; i++) {//tworzenie nowych czasteczek
				float randomSize = ((random.nextInt(500)+500)/100000f)*1.2f;
				float particleDirection = random.nextInt(36000)/100f;
				float randomSpeed = random.nextInt(1000)/1000f * 0.5f;
				particles.add(new SimpleParticle(bounds.x + explosionPos.x*bounds.width, bounds.y+explosionPos.y*bounds.height, bounds.height*randomSize, particleDirection, randomSpeed, false));
				particles.get(particles.size()-1).setColor(1, random.nextInt(100)/200f+0.5f, 0, 1);//losowy kolor pomiedzy zoltym a czerwonym
			}
			break;
		case 2:
			for(int i=0; i<Config.bombParticles; i++) {//tworzenie nowych czasteczek
				float randomSize = ((random.nextInt(500)+500)/100000f)*2.0f;
				float particleDirection = random.nextInt(36000)/100f;
				float randomSpeed = random.nextInt(1000)/1000f * 4.0f;
				particles.add(new SimpleParticle(bounds.x + explosionPos.x*bounds.width, bounds.y+explosionPos.y*bounds.height, bounds.height*randomSize, particleDirection, randomSpeed, false));
				particles.get(particles.size()-1).setColor(1, random.nextInt(100)/200f+0.5f, 0, 1);//losowy kolor pomiedzy zoltym a czerwonym
			}
			break;
		}
	}
	//aktualizuje wybuch
	private void update(float delta) {
		explosionTime += delta;//licznik trwania eksplozji
	}
	//renderuje wybuch
	public void render(float delta) {
		update(delta);//aktualizacje fizyki
		if(texture != null)//jesli textura nie jest pusta
			texture.bind();//ustawia texture dla wszystkich czasteczek
		else
			glBindTexture(GL_TEXTURE_2D, 0);//usuwanie textury z opengl dla nastepnych polygonow
		float lifePercent = (Config.hitExplosionTime-explosionTime)/Config.hitExplosionTime;//obliczanie w procentach czasu trwania explozji (0 - poczatek, 1 - czas po ktorym explozja sie konczy)
		for(int i=0; i<particles.size(); i++) {//dla kazdej czateczki na liscie
			particles.get(i).move(bounds.height*Config.hitExplosionParticleSpeed*delta);
			particles.get(i).setAlfa(lifePercent);//zmienia przezroczystosc zaleznie od czasu zycia
			particles.get(i).render();//renderuje czasteczke
		}
	}
}
