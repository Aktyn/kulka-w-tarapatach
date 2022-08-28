package gra;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class Enemy extends Ball{
	public List<Consequent> consequences;//lista aktywnych efektow
	private Damage damage;//obiekt symulujacy uszkodzenia obiektu
	
	public Enemy(float x, float y) {
		damage = new Damage();
		//obliczanie losowego polozenia przeciwnika na mapce
		Random random = new Random();//obiekt potrzebny do wygenerowania losowych liczb
		super.pos = new Vector2f(x, y);//ustawienie obiektu
		for(int i=0; i<super.lastPos.length; i++)
			super.lastPos[i] = new Vector2f(pos);
		super.rotation = random.nextInt(360);//generowane losowej rotacji obiektu
		super.objectSpeed = getSpeed();
		this.radius = Config.enemyRadius;
		this.consequences = new ArrayList<Consequent>();
		super.setType(1);
	}
	//ustawia texture przeciwnika
	public void setTexture(Texture texture) {
		super.setTexture(texture);
	}
	//ustawia texture dla uszkodzen
	public void setDamageTexture(Texture texture) {
		damage.setTexture(texture);
	}
	//ustawia obszar po ktorym obiekt moze sie poruszac
	public void setBounds(float x, float y, float width, float height) {
		super.setBounds(x, y, width, height);
	}
	//przypisuje vektory kolizji
	public void setCollisionLines(Vector2f[][] vectors) {
		super.setCollisionLines(vectors);
	}
	//zwraca predkosc obiektu zaleznie od poziomu trudnosci i trybu gry
	private float getSpeed() {
		if(Config.gameType == 2)//w przypadku kampani
			return Config.enemySpeed + 0.1f*(Config.difficult-1);
		else
			return Config.enemySpeed;
	}
	//zwraca pozycje obiektu
	public Vector2f getPos() {
		return super.pos;
	}
	//zwraca rotacje obiektu
	public float getRot() {
		return super.rotation;
	}
	//ustawia rotacje przeciwnika
	public void setRot(float rot) {
		super.rotation = rot;
	}
	//przypisuje index ostatniego udezonego przeciwnika
	public void setLastEnemyCollision(int index) {
		super.lastEnemyCollison = index;
	}
	//ustawia efekt dla obiektu
	public void addEffect(int index) {
		consequences.add(new Consequent(index, 0));
	}
	//przywraca ostatnia niekolizyjna pozycje przeciwnika
	public void resolvePos() {
		super.resolvePos();
	}
	//renderowanie obiektu przeciwnika na odpowiedniej pozycji
	public void render(float delta) {
		boolean effected = false;//true jesli jeden z efektow zostal zastosowany
		for(int i=0; i<consequences.size(); i++) {
			if(consequences.get(i).durationTime < 0)//jesli czas trwania efektu sie skonczyl
				consequences.remove(i);//zostaje usuniety
			else {
				//dzialanie efektu
				effected = true;
				switch(consequences.get(i).index) {
				case 0://spowalnianie przeciwnika
					super.objectSpeed /= 2.0f;
					break;
				case 1://przyspieszenie
					super.objectSpeed *= 2.0f;
					break;
				}
				consequences.get(i).durationTime -= delta;//odliczanie do konca czasu trwania efektu
			}	
		}
		//renderowanie kulki
		super.render(delta);
		//renderownaie uszkodzen
		if(hp < 100.0f) {//jesli przeciwnik stracil hp
			float tmphp = 100.0f;
			int layer = 0;
			do {
				Vector2f damagePos = new Vector2f(gridBounds.x + pos.x*gridBounds.width, gridBounds.y + (1-pos.y)*gridBounds.height);
				//renderowanie warstwy uszkodzen
				damage.render(damagePos, rotation + 360.0f / Config.damageLayers * layer, radius*gridBounds.height);
				tmphp -=  100.0f / Config.damageLayers;
				layer++;
			} while(tmphp > hp);//dopoki tymczasowe hp jest wieksze od tego na prawde
		}
		if(effected) {//przywracanie zmian efektow jesli takie zaszly
			super.objectSpeed = getSpeed();
		}
	}
}
