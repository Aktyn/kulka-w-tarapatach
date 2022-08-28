package gra;

import static org.lwjgl.opengl.GL11.*;//importowanie calego OpenGL 11
import gui.Bounds;

import java.util.Random;

import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class Effect {
	private Texture texture;//textura obiektu
	public int index;//WAZNE!!! precyzuje typ efektu legenda ponizej
	public Vector2f pos;//pozycja renderowanego obiektu
	public float radius;//promien obiektu
	private Bounds bounds;//obszar mapy gry
	private float alfa = 1;//poziom przezroczystosci
	public float lifeTime = 0.0f;//czas zycia obiektu
	private CircleShadow shadow;//obiekt cienia
	/** LEGENDA dla efektow rozpoznawanych po indexie**/
	/** 0 - spowolnienie wszystkich przeciwnikow
	/** 1 - przyspieszenie jednego obiektu
	/** 2 - apteczka dla gracza
	/** 3 - bron dla gracza
	/** 4 - portal gun
	/** 5 - bomba
	 */
	public Effect(Vector2f pos, Texture[] textures, Bounds bounds, Texture shadowTexture) {
		initialize(pos, bounds, shadowTexture);
		//losowanie efektu (ilosc efektow rowna ilosci textur w przesylanej zmiennej)
		Random random = new Random();//obiekt potrzebny do generowania losowych liczb
		int randomIndex = random.nextInt(textures.length);
		if(randomIndex != 2) {//dla apteczek
			randomIndex = random.nextInt(textures.length);
		}
		if(randomIndex == 4 || randomIndex == 5) {//dla portalguna lub bombki
			randomIndex = random.nextInt(textures.length);
		}
		this.index = randomIndex;
		this.texture = textures[index];
	}
	public Effect(Vector2f pos, Texture[] textures, Bounds bounds, int index, Texture shadowTexture) {
		initialize(pos, bounds, shadowTexture);
		this.index = index;
		this.texture = textures[index];
	}
	//wspolna funkcja do obu kontruktorow (dla przejzystosci kodu)
	private void initialize(Vector2f pos, Bounds bounds, Texture shadowTexture) {
		this.bounds = bounds;
		this.pos = pos;
		this.radius = 0;
		shadow = new CircleShadow(0.5f, shadowTexture);
	}
	
	//renderowanie obiektu
	public void render(float delta) {
		lifeTime += delta;//odliczanie do konca istnienia obiektu
		//animacja powiekszania sie obiektu
		if(radius < Config.effectRadius) {
			float deltaSize = Config.effectRadius - radius;
			radius += Math.pow(deltaSize, 0.8f) * delta;
			alfa = radius / Config.effectRadius;
		}
		else if (radius > Config.effectRadius){
			radius = Config.effectRadius;
			alfa = 1.0f;
		}
		//////////////////////////////////////////////
		if(lifeTime >= Config.effectsLifeTime-1.0f) {
			radius = Config.effectRadius*(Config.effectsLifeTime-lifeTime);
			alfa = Config.effectsLifeTime-lifeTime;
		}
		if (lifeTime >= Config.effectsLifeTime) {
			radius = 0.0f;
			alfa = 0.0f;
			shadow.setAlfa(0);
		}
		if(radius > Config.effectRadius)
			radius = Config.effectRadius;
		///////////////////////////////////////////////
		if(Config.shadows)
			shadow.render(new Vector2f(bounds.x + pos.x*bounds.width, bounds.y + (1-pos.y)*bounds.height), radius*bounds.height);
		//renderowanie obiektu
		if(texture != null)
			glBindTexture(GL_TEXTURE_2D, texture.getTextureID());//ustawienie textury
	    else
	    	glBindTexture(GL_TEXTURE_2D, 0);//usuwanie textury z opengl dla nastepnych polygonow
		glColor4f(1, 1, 1, alfa);//kolor bialy z kanalem alfa
		//miganie ikonki
		if(lifeTime >= Config.effectsLifeTime-5.0f) {//jeseli pozostalo 5 sekund zycia
			if((int)((Config.effectsLifeTime-lifeTime)*2f) % 2 == 0)//co sekunde przez sekunde
				glColor4f(1.0f, 0.75f, 0.75f, alfa);
		}
		glBegin(GL_QUADS);
	      glTexCoord2f(0, 0);
	      glVertex2f(bounds.x + (pos.x - radius)*bounds.width, bounds.y + (1-pos.y - radius)*bounds.height);
	      glTexCoord2f(1, 0);
	      glVertex2f(bounds.x + (pos.x + radius)*bounds.width, bounds.y + (1-pos.y - radius)*bounds.height);
	      glTexCoord2f(1, 1);
	      glVertex2f(bounds.x + (pos.x + radius)*bounds.width, bounds.y + (1-pos.y + radius)*bounds.height);
	      glTexCoord2f(0, 1);
	      glVertex2f(bounds.x + (pos.x - radius)*bounds.width, bounds.y + (1-pos.y + radius)*bounds.height);
	   glEnd();
	}
}
