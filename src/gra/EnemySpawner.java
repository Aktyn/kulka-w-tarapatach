package gra;

import static org.lwjgl.opengl.GL11.*;//importowanie calego OpenGL 11
import gui.Bounds;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class EnemySpawner {
	private Game callBack;//obiekt Gra potrzebny do wywolania funkcji tworzenia przeciwnika
	private Texture texture;//textura spawnera
	private Texture particlesText;//textura czasteczek
	private Bounds bounds;//obszar planszy gry
	private float radius = 0;//promien spawnera
	private float rotation = 0;//obrot obiektu
	private Vector2f pos;//pozycja obiektu
	private Boolean ending = false;//false - pojawianie sie spawnera i spawnienie przeciwnika, true - znikanie
	public float lifeTime = 2f;//odliczanie do znikniecia portalu
	private float timeToSpawn = 1f;//odliczanie do stworzenia przeciwnika po pojawieniu sie portalu
	private float particleFrequency = 0.02f;//co jaki czas ma sie pojawic nowa czasteczka
	private float newParticleTime = 0;//czas do stworzenia nowej czasteczki
	private List<SimpleParticle> particles;//lista czasteczek
	private Random random = new Random();//obiekt potrzebny do generowania losowych wartosci dla czasteczek
	
	public EnemySpawner(Game callBack, float x, float y, Texture texture, Texture particlesTexture, Bounds bounds) {
		if(Config.particles)
			this.particles = new ArrayList<SimpleParticle>();//nowa pusta lista czasteczek
		this.callBack = callBack;
		this.texture = texture;
		this.particlesText = particlesTexture;
		this.bounds = bounds;
		this.pos = new Vector2f(x, y);
	}
	//zwraca pozycje spawneru
	public Vector2f getPos() {
		return pos;
	}
	//aktualizowanie spawnera
	private void update(float delta) {
		//obliczanie efektu czasteczek emitujacych z portala
		if(Config.particles) {
			if(newParticleTime < particleFrequency) {
				newParticleTime += delta;
			}
			else {
				newParticleTime = 0;
				//dodawanie nowej czasteczki
				float randomSize = ((random.nextInt(500)+500)/100000f)*1.0f;
				float particleDirection = random.nextInt(36000)/100f;
				float randomSpeed = random.nextInt(1000)/1000f;
				particles.add(new SimpleParticle(bounds.x + pos.x*bounds.width, bounds.y+(1-pos.y)*bounds.height, bounds.height*randomSize, particleDirection, randomSpeed, false));
				particles.get(particles.size()-1).setColor(0, 1, 1f-(float)Math.pow(randomSpeed,2) -0.1f + random.nextInt(100)/1000f, 1);//losowy kolor pomiedzy blekitnym a zielonym
			}
		}
		//znikanie obiektu
		if(!ending) {
			if(radius < Config.enemySpawnerRadius) {
				radius += (delta*Config.enemySpawnerRadius)/0.5f;//przez pol sekundy powieksza spawner
			}
			else if (radius >= Config.enemySpawnerRadius) {
				radius = Config.enemySpawnerRadius;
				if(timeToSpawn > 0)
					timeToSpawn -= delta;
				else {
					callBack.addEnemy(pos.x, pos.y);
					ending = true;
				}
			}
		}
		else {
			if(lifeTime > 0)
				lifeTime -= delta;
			else
				lifeTime = 0;
			if(lifeTime <= 1) {
				radius = Config.enemySpawnerRadius*lifeTime;
			}
		}
		rotation += delta*180f;//obracanie obiektu
	}
	//renderowanie spawneru
	public void render(float delta) {
		update(delta);
		
		//obliczanie punktu srodka obiektu
		float visiblePosX = bounds.x + (pos.x)*bounds.width;
	    float visiblePosY = bounds.y + (1-pos.y)*bounds.height;
	    
	    //renderowanie czasteczek
	    if(Config.particles) {
		    if(particlesText != null)
		    	particlesText.bind();//ustawienie textury
		    else 
		    	glBindTexture(GL_TEXTURE_2D, 0);//usuwanie textury z opengl dla nastepnych polygonow
		    for(int i=0; i<particles.size(); i++) {
		    	if(particles.get(i).getAlfa() <= 0)
		    		particles.remove(i);
		    	else {
		    		particles.get(i).move(bounds.height*Config.enemySpawnerParticleSpeed*delta);
		    		particles.get(i).setAlfa((particles.get(i).getAlfa()-delta*0.5f) * (lifeTime <= 1 ? lifeTime : 1));
		    		particles.get(i).render();
		    	}
		    }
	    }
		
	    glPushMatrix();//blokowanie zmian matrixowych dla poprzednio wyrenderowanych obiektow
	    glTranslatef(visiblePosX, visiblePosY, 0); //przesuwanie obrazu tak by obrocic obiekt wzgledem jego srodka
	    glRotatef(rotation, 0, 0, 1);//obracanie obiektu
	    glTranslatef(-visiblePosX, -visiblePosY, 0);//ponowne przesuniecie obrazu na wczesniejsza pozycje
	    
	    //renderownaie figury z textura obiektu
	    if(texture != null)
			texture.bind();//ustawienie textury
	    else 
	    	glBindTexture(GL_TEXTURE_2D, 0);//usuwanie textury z opengl dla nastepnych polygonow
	    glColor3f(0, 1, 1);//kolor bialy bez przezroczystosci
	    glBegin(GL_QUADS);
	      glTexCoord2f(0, 0);
	      glVertex2f(visiblePosX - radius*bounds.width, visiblePosY - radius*bounds.height);
	      glTexCoord2f(1, 0);
	      glVertex2f(visiblePosX + radius*bounds.width, visiblePosY - radius*bounds.height);
	      glTexCoord2f(1, 1);
	      glVertex2f(visiblePosX + radius*bounds.width, visiblePosY + radius*bounds.height);
	      glTexCoord2f(0, 1);
	      glVertex2f(visiblePosX - radius*bounds.width, visiblePosY + radius*bounds.height);
	   glEnd();
	   
	   glPopMatrix();//puszczenie matrixa (zamkniecie kodu od wywolania fukncji glPushMatrix())
	}
}
