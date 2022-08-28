package gra;

import gui.Bounds;

import java.util.Random;

import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class Bullet extends Ball {
	private float lifeTime = 0;
	public int state;//rodzaj pocisku, 0 - dzialkowy, 1 - portalgunowy, 2 - bomba
	
	public Bullet(Vector2f pos, float rotation, float radius, Vector2f[][] collisionLines, Bounds bounds, Texture[] texture, int state) {
		super.pos = pos;
		for(int i=0; i<super.lastPos.length; i++)
			super.lastPos[i] = new Vector2f(pos);
		super.rotation = rotation;
		super.visibleRotation = rotation;
		super.radius = radius;
		if(state == 2)
			super.objectSpeed = 0;
		else
			super.objectSpeed = Config.bulletSpeed;
		super.setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
		this.state = state;
		super.setTexture(texture[state]);
		super.setCollisionLines(collisionLines);
		super.setType(2+state);
		if(state == 0) {//tylko dla pocisku dzialka
			Random random = new Random();//obiekt potrzebny do wygenerowania losowego koloru
			super.setColor(1, random.nextInt(500)/1000f+0.5f, 0);//losowanie odcienia pomiedzy czerwonym a zoltym kolorem
		}
	}
	//renderowanie pocisku
	public void render(float delta) {
		lifeTime += delta;
		switch(state) {
		case 0://pocisk dzialka gracza lub bossa
			if(lifeTime > Config.bulletLifeTime - 1.0f) {
				super.setAlfa(Config.bulletLifeTime - lifeTime);
				//super.shadow.setAlfa(Config.bulletLifeTime - lifeTime);
				if(lifeTime >= Config.bulletLifeTime)
					super.explode = true;
			}
		
		break;
		case 1://portal
			if(lifeTime > Config.portalLifeTime - 1.0f) {
				super.setAlfa(Config.portalLifeTime - lifeTime);
				super.shadow.setAlfa(Config.bulletLifeTime - lifeTime);
				if(lifeTime >= Config.portalLifeTime)
					super.explode = true;
			}
		break;
		case 2://bomba
			super.shadow.setAlfa(0.0f);
			super.setColor(1.0f, (Config.bombLifeTime-lifeTime)/Config.bombLifeTime, (Config.bombLifeTime-lifeTime)/Config.bombLifeTime);
			if(lifeTime >= Config.bombLifeTime) {
				super.explode = true;
			}
			break;
		}
		super.render(delta);
	}
}
