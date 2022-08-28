package gra;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import gui.Bounds;

import org.newdawn.slick.opengl.Texture;

public class Weather {
	private Texture texture;//textura czasteczek
	private int type = 0;//typ pogody: 0 - snieg, 1 - deszcz
	private Bounds bounds;//obszar gry
	private List<SimpleParticle> particles;//lista czasteczek
	private Random random;//obiekt potrzebny do generowania losowych wartosci czasteczek
	private float timeToNewParticle = 0.0f;//odlcizanie czasu do mozliwego stworzenia nowej czasteczki
	private float particleFrequency;//czestotliwosc generowania nowych czasteczek

	public Weather(int type, Texture texture, Bounds bounds) {
		this.texture = texture;
		this.bounds = bounds;
		this.particles = new ArrayList<SimpleParticle>();
		this.random = new Random();
		this.type = type;
		this.particleFrequency = 0.2f - 0.18f*type;
	}
	//obliczenia czasteczek pogody
	private void update(float delta) {
		if(delta != 0 && particles.size() < Config.maxWeatherParticles && timeToNewParticle >= particleFrequency) {
			timeToNewParticle = 0.0f;
			switch(type) {
			case 0://snieg
				particles.add(new SimpleParticle(bounds.x + random.nextInt(10000)/10000f * bounds.width, bounds.y + 0.0f*bounds.height, 0.01f*bounds.height*(1.0f+random.nextInt(1000)/1000f), -15.0f + random.nextInt(3000)/100f, 0.5f+random.nextInt(1000)/2000f, true));
				particles.get(particles.size()-1).setColor(1, 1, 1, 1);
				break;
			case 1://deszcz
				particles.add(new SimpleParticle(bounds.x + random.nextInt(10000)/10000f * bounds.width, bounds.y + 0.0f*bounds.height, 0.01f*bounds.height*(1.0f+random.nextInt(1000)/1000f), -15.0f + random.nextInt(3000)/100f, (0.5f+random.nextInt(1000)/2000f)*5.0f, true));
				particles.get(particles.size()-1).setColor(1, 1, 1, 1);
				break;
			}
		}
		else
			timeToNewParticle+=delta;//liczenie czasu do mozliwosci generowania nowej czasteczki
	}
	
	public void render(float delta) {
		update(delta);
		if(texture != null)
			glBindTexture(GL_TEXTURE_2D, texture.getTextureID());//ustawienie textury
		else
			glBindTexture(GL_TEXTURE_2D, 0);//czyszczenie textury z opengl
		for(int i=0; i<particles.size(); i++) {
			if(particles.get(i).getPos().y > bounds.height)
				particles.remove(i);
			else {
				particles.get(i).move(90.0f*delta);
				particles.get(i).render();
			}
		}
	}
}
