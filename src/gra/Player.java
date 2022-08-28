package gra;

import static org.lwjgl.opengl.GL11.*;//importowanie calego OpenGL 11

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.Sound;
import org.newdawn.slick.opengl.Texture;

public class Player extends Ball {
	private Texture particleTexture;//wspolna textura dla czastek jakie gracz zostawia za soba
	public List<Consequent> consequences;
	private List<Particle> particles;//lista obiektow czasteczek
	private float particleTime = 0;//liczy czas do stworzenia nowej czasteczki
	public boolean canShoot = false;//czy gracz moze strzelac
	private Weapon weapon;//bron gracza
	public List<Bullet> bullets;//pociski wystrzeliwane przez gracza
	private Texture[] bulletTextures;//textury dla pociskow dzialka i portalguna
	private float bulletTime = 0;//odliczanie do kolejnego strzalu
	public boolean armed = false;//czy jest uzbrojony w zwykla bron
	public float wallHitStunTime = 0;//czas trwania stunu
	private Texture shadowTexture;
	private Sound shootSound;//dzwiek strzalu z dzialka
	private Sound wallHit;//dzwiek zderzenia ze sciana
	
	public Player() {
		super.pos = new Vector2f(0.5f, 0.5f);//ustawienie obiektu gracza na srodku planszy
		particles = new ArrayList<Particle>();//tworzenie pustej listy czasteczek
		bullets = new ArrayList<Bullet>();//i pociskow
		super.radius = Config.playerRadius;
		super.objectSpeed = Config.playerSpeed;
		this.consequences = new ArrayList<Consequent>();
		super.rotation = new Random().nextInt(180)-90;
		super.visibleRotation = super.rotation;
		weapon = new Weapon();
		super.setType(0);
	}
	//ustawia texture gracza
	public void setTexture(Texture texture) {
		super.setTexture(texture);
	}
	//ustawia textura broni
	public void setWeaponTexture(Texture[] textures) {
		weapon.setTexture(textures);
	}
	//ustawia texture pociskow
	public void setBulletTexture(Texture[] textures) {
		this.bulletTextures = textures;
	}
	//ustawia texture cieni kolowy
	public void setShadowTexture(Texture shadowTexture) {
		this.shadowTexture = shadowTexture;
		super.setShadow(shadowTexture);
	}
	//przypisuje texture czasteczek do zmiennej
	public void setParticlesTexture(Texture texture) {
		this.particleTexture = texture;
	}
	//ustawia dzwieki gracza
	public void setSounds(Sound shootSound, Sound hit) {
		this.shootSound = shootSound;
		this.wallHit = hit;
	}
	//ustawia obszar na ktorym gracz moze sie poruszac
	public void setBounds(float x, float y, float width, float height) {
		super.setBounds(x, y, width, height);
	}
	//przypisuje vektory kolizji
	public void setCollisionLines(Vector2f[][] vectors) {
		super.setCollisionLines(vectors);
	}
	//zwraca pozycje obiektu
	public Vector2f getPos() {
		return super.pos;
	}
	//zwraca rotacje obiektu
	public float getRot() {
		return super.rotation;
	}
	//ustawia rotacje gracza
	public void setRot(float rot) {
		super.rotation = rot;
	}
	//ustawia efekt dla obiektu
	public void addEffect(int index) {
		consequences.add(new Consequent(index, consequences.size()));
	}
	//funkcja pobiera dane z klawiatury na podstawie czego steruje graczem
	private void getInput(float delta) {
		boolean keyLeft = Keyboard.isKeyDown(Keyboard.KEY_LEFT) || Keyboard.isKeyDown(Keyboard.KEY_A);//lewa strzalka lub litera A
		boolean keyRight = Keyboard.isKeyDown(Keyboard.KEY_RIGHT) || Keyboard.isKeyDown(Keyboard.KEY_D);//prawa strzalka lub litera D
		
		if(keyLeft && !keyRight)//skrecanie w lewo
			super.rotation -= Config.turnPower*delta;
		if(keyRight && !keyLeft)//skrecanie w prawo
			super.rotation += Config.turnPower*delta;
	}
	//obliczanie pozycji i rotacji gracza 
	public void update(float delta) {
		if(super.collided)
			wallHitStunTime = Config.wallCollisionStunTime;
		if(wallHitStunTime > 0) {
			wallHitStunTime -= delta;//odliczanie do konca trwania stunu
		}
		else if (wallHitStunTime < 0) {
			wallHitStunTime = 0;//zerowanie efektu i czasu trwania
		}
		else
			getInput(delta);//sterowanie
		
		//obliczanie czasteczek
		if(particleTime < Config.particleFrequency)
			particleTime += delta;
		else {
			particleTime = 0;
			float radiusParticle = radius*super.gridBounds.height;
			particles.add(new Particle(super.visiblePos.x+radiusParticle*0.25f*Config.visibleParticleSize, super.visiblePos.y+radiusParticle*0.25f*Config.visibleParticleSize, radiusParticle, shadowTexture));//dodanie nowej czasteczki na aktualnej pozycji gracza
		}
	}
	//renderowanie obiektu gracza na odpowiedniej pozycji
	public void render(float delta) {
		this.update(delta);//obliczenia zwiazanie z graczem
		if(super.collided && Config.enableSounds)
			wallHit.play(1.0f, Config.effectsVolume);//dzwiek walniecia lbem o sciane

		//renderowanie sznuru czasteczek za graczem
		for(int i=0; i<particles.size(); i++) {
			if(particles.get(i).size > 0)
				particles.get(i).renderShadow();
		}
		//ustawianie textury dla renderowania wszystkich czastek
		glBindTexture(GL_TEXTURE_2D, particleTexture.getTextureID());
		for(int i=0; i<particles.size(); i++) {
			if(particles.get(i).size == 0)//jesli czas twania czasteczki dobiegl konca
				particles.remove(i);//zostaje usuuwana z listy
			else//w przeciwnym razie renderowana
				particles.get(i).render(delta);
		}
		////////////////////////////////////////////
		//zarzadzanie efektami
		boolean effected = false;//true jesli jeden z efektow zostal zastosowany
		armed = false;
		for(int i=0; i<consequences.size(); i++) {
			if(consequences.get(i).durationTime < 0)//jesli czas trwania efektu sie skonczyl
				consequences.remove(i);//zostaje usuniety
			else {
				//dzialanie efektu
				effected = true;
				switch(consequences.get(i).index) {
				case 1://przyspieszenie
					super.objectSpeed *= 2.0f;
					break;
				case 3://bron dzialka
					canShoot = true;
					armed = true;
					weapon.weaponState = 0;
					break;
				case 4://portal gun
					canShoot = true;
					armed = true;
					weapon.weaponState = 1;
					break;
				case 5://bombka
					canShoot = true;
					armed = true;
					weapon.weaponState = 2;
					break;
				}
				consequences.get(i).durationTime -= delta;//odliczanie do konca czasu trwania efektu
			}
		}
		
		//renderowanie kulki
		super.render(delta);
		//strzelanie i renderowanie broni
		if(canShoot) {//jesli moze strzelac - renderowanie broni i sprawdzanie klawisza strzalu, jesli wcisniety - strzelanie
			weapon.update(new Vector2f(gridBounds.x + (pos.x)*gridBounds.width, gridBounds.y + (1-pos.y)*gridBounds.height), radius*gridBounds.height*Config.weaponRadiusRatio, rotation);
			weapon.render();
			
			//strzelanie zaleznie od rodzaju broni
			switch(weapon.weaponState) {
			case 0://kanon
				if(bulletTime < Config.bulletSensitivity)
					bulletTime += delta;
				else if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {//strzal z dzialka
					if(Config.enableSounds)
						shootSound.play(1.0f, Config.effectsVolume);//dzwiek strzalu
					bulletTime = 0;
					bullets.add(new Bullet(new Vector2f(pos.x, pos.y), rotation, radius*Config.weaponRadiusRatio, super.collisionLines, gridBounds, bulletTextures, 0));//dodawanie nowego pocisku dzialka
					bullets.get(bullets.size()-1).setShadow(shadowTexture);
				}
				break;
			case 1://portal gun
				if(bulletTime < Config.portalGunSensitivity)
					bulletTime += delta;
				else if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
					//spradzanie czy maksymalna ilosc portali nie zostala przekroczona
					int portals = 0;
					for(int i=0; i<bullets.size(); i++) {
						if(bullets.get(i).state == 1)
							portals++;
					}
					bulletTime = 0;
					if(portals < 2) {
						if(Config.enableSounds)
							shootSound.play(1.0f, Config.effectsVolume);
						bullets.add(new Bullet(new Vector2f(pos.x, pos.y), rotation, radius*Config.weaponRadiusRatio, super.collisionLines, gridBounds, bulletTextures, 1));//dodawanie nowego pocisku portalguna
						bullets.get(bullets.size()-1).setShadow(shadowTexture);
						lastCollideTime = 0.0f;
					}
				}
				break;
			case 2://bomba
				if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
					if(Config.enableSounds)
						shootSound.play(1.0f, Config.effectsVolume);
					bullets.add(new Bullet(new Vector2f(pos.x, pos.y), rotation, radius*Config.weaponRadiusRatio, super.collisionLines, gridBounds, bulletTextures, 2));//dodawanie bomby
					bullets.get(bullets.size()-1).setShadow(shadowTexture);
					//usuwanie efektu
					for(int i=0; i<consequences.size(); i++) {
						if(consequences.get(i).index == 5)//jesli efekt bomby
							consequences.remove(i);//zostaje usuniety
					}
				}
				break;
			}
		}
		
		if(effected) {//przywracanie zmian efektow jesli takie zaszly
			super.objectSpeed = Config.playerSpeed;
			canShoot = false;
		}
	}
}
