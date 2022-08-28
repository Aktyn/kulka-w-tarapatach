package gra;

import static org.lwjgl.opengl.GL11.*;
import gui.Bounds;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.opengl.Texture;

public class Ball {
	private Texture texture;//textura obiektu
	public Bounds gridBounds;//obszar po jakim moze poruszac sie obiekt
	public float radius;//promien kola jakim jest obiekt (dla kolizji)
	public int lastEnemyCollison = -2;//przechowuje index ostatniego udezonego przeciwnika (-1 czyli player, -2 czyli brak)
	
	public float objectSpeed = 0;//predkosc obiektu
	public Vector2f pos;//pozycja obiektu
	public Vector2f[] lastPos;//ostatnia niekolizyjna pozycja obiektu
	private Vector3f color;//kolor obiektu
	private float alfa = 1;//przezroczystosc obiektu
	public Vector2f visiblePos = new Vector2f(0f, 0f);//przechowuje pozycje obiektu do wyrenderowania na ekranie
	public float rotation = 0;//rotacja obiektu w degradianach
	public float visibleRotation = 0;//widoczna rotacja obiektu dla plynnosci
	public boolean collided = false;//czy zaszla kolizja ze sciana
	private float collidedTime = 0.0f;//odliczanie czasu po kolizji ze sciana
	public float lastCollideTime = 0.0f;//odliczanie czasu po zwyklej kolizji
	public float hp = 100;//zycie obiektu
	
	public CircleShadow shadow;//obiekt cienia
	
	public Vector2f[][] collisionLines;//vektory takie same jak w scianach (obliczana kolizja na ich podstawie)
	
	private int type = 0;//definiuje kim jest rodzic obiektu (0 - player, 1 - enemy, 2 - pocisk dzialka, 3 - pocisk portalguna)
	public boolean explode = false;//jesli true - obiekt do wybuchu
	
	public Ball() {
		lastPos = new Vector2f[Config.lastPosCount];
		for(int i=0; i<Config.lastPosCount; i++)
			lastPos[i] = new Vector2f(0.5f, 0.5f);
		this.color = new Vector3f(1.0f, 1.0f, 1.0f);
		shadow = new CircleShadow(0.5f, null);
	}
	//ustawia cien kolowy dla obiektu
	public void setShadow(Texture shadowTexture) {
		shadow.setTexture(shadowTexture);
	}
	//zmienia typ obiektu
	public void setType(int type) {
		if(type == 2 || type == 0)//pocisk dzialka i gracz nie rzucaja cienia
			shadow.setAlfa(0.0f);
		this.type = type;
	}
	//ustawia texture obiektu
	public void setTexture(Texture texture) {
		this.texture = texture;
	}
	//ustawia kolor obiektu
	public void setColor(float r, float g, float b) {
		this.color.x = r;
		this.color.y = g;
		this.color.z = b;
	}
	//ustawia przezroczystosc obiektu
	public void setAlfa(float alfa) {
		this.alfa = alfa;
	}
	//ustawia obszar po ktorym obiekt moze sie poruszac
	public void setBounds(float x, float y, float width, float height) {
		this.gridBounds = new Bounds(x,y,width,height);
	}
	//przypisuje vektory kolizji
	public void setCollisionLines(Vector2f[][] vectors) {
		this.collisionLines = vectors;
	}
	//utrzymuje rotacje obiektu w zakresie od 0 do 360 stopni
	private void resolveRotation() {
		if(rotation > 360) {
			rotation -= 360;
			visibleRotation -= 360;
		}
		else if(rotation < 0) {
			rotation += 360;
			visibleRotation += 360;
		}
	}
	//funkcja sprawdza czy obiekt jest w odpowiednim obszarze
	private void isOnMap() {
		if(pos.x < 0) {
			pos.x = 1;
		}
		if(pos.x > 1) {
			pos.x = 0;
		}
		if(pos.y < 0) {
			pos.y = 1;
		}
		if(pos.y > 1) {
			pos.y = 0;
		}
	}
	//sprawdzanie czy wartosc jest liczbowa
	private boolean isNumber(float a) {
		//zwraca true jesli kolejno: a nie jest NaN czyli a jest rowne a i a nie jest dodatnia nieskonczonoscia ani ujemna
		return a==a && a != Float.POSITIVE_INFINITY && a!= Float.NEGATIVE_INFINITY;
	}
	//funkcja sprawdza czy obiekt nie wyszedl swoim obszarem poza okreslony obszar
	private boolean checkWallCollision() {
		for(int j=0; j<collisionLines.length; j++) {
			for(int i=0; i<collisionLines[j].length; i++) {
				Vector2f pA = collisionLines[j][i];
				Vector2f pB = collisionLines[j][i+1 < collisionLines[j].length ? i+1 : 0];
				if(getDistance(pA, pos) <= radius) {//jesli obiekt koliduje z wierzcholkiem pA
					vertexCollision(i, j);//oblicza to (vertex i part w tej kolejnosci indexy)
					collided = true;
					collidedTime = Config.wallCollisionTime;
					return true;
				}
				//parametry funkcji liniowej:
				float A = (pA.y - pB.y) / (pA.x - pB.x);//parametr wspolczynnika a
				if(!isNumber(A))//korygowanie wartosci dla wynikow z dzielenia przez 0
					A = Config.calculationPrecisionFix-1;
				float B = -1;//parametr przy wspolczynniku y
				float C = pA.y - (A * pA.x);//wyraz wolny
				//obliczanie odleglosci do prostej ze wzoru matematycznego
				float distance = (float)(Math.abs(A * pos.x + B * pos.y + C) / Math.sqrt(A*A + B*B));
				if(distance <= radius || getDistance(pA, pos) <= radius || getDistance(pB, pos) <= radius) {//jesli kolo lezy na prostej
					//obliczanie punktu kolizji z ukladu rownan prostej kolizji i prostopadlej do niej prostej przechodzacej przez srodek kola
					float a2 = -(1.0f/A);//przeciwny i odwrotny wspolczynnik kierunkowy
					if(!isNumber(a2))
						a2 = Config.calculationPrecisionFix/2f;
					//y = a2*x + b2 	===>>	b2 = y - (a2*x);
					float b2 = pos.y - (a2 * pos.x);//podstawianie wspolzednych srodka kola by obliczyc drugi parametr funkcji
					//y = a*x + b;		y = a2*x + b2
					//y - y = a*x + b - a2*x - b2;	a*x - a2*x + b - b2 = 0;	(a-a2)*x + b - b2 = 0;
					//x = (-b + b2) / (a - a2);
					//obliczanie wspolzednych punktu kolizji
					float kx = (b2 - C) / (A - a2);
					float ky = A * kx + C;// == a2 * kx + b2
					//sprawdzanie czy pkt znajduje sie na odcinku jakim jest linia kolizji
					if(Math.min(pA.y, pB.y) < ky+radius*Config.distanceToCollisionLine && 
							Math.max(pA.y, pB.y) > ky-radius*Config.distanceToCollisionLine && 
							Math.min(pA.x, pB.x) < kx+radius*Config.distanceToCollisionLine && 
							Math.max(pA.x, pB.x) > kx-radius*Config.distanceToCollisionLine) {
						//obliczanie nowej rotacji gracza
						if(type == 2) {//pocisk nie odbija sie, wybucha
							//pozycjonowanie pocisku w miejscu uderzenia
							pos.x = kx;
							pos.y = ky;
						}
						updateRotation(A);//obracanie obiektu zaleznie od wartosci tangensu z A
						collided = true;
						collidedTime = Config.wallCollisionTime;
						return true;
					}
				}
			}
		}
		return false;
	}
	//obliczanie kolizji z danym wierzcholkiem
	private void vertexCollision(int vertex, int part) {
		Vector2f pA = collisionLines[part][vertex];
		Vector2f pB = collisionLines[part][vertex-1 >= 0 ? vertex-1 : collisionLines[part].length-1];
		Vector2f pC = collisionLines[part][vertex+1 < collisionLines[part].length ? vertex+1 : 0];
		Vector2f[] collisionPoints = new Vector2f[2];
		collisionPoints[0] = getLinePoint(pA, pB);
		collisionPoints[1] = getLinePoint(pA, pC);

		if(collisionPoints[0] != null && collisionPoints[1] != null) {//formalnosc
			float A = (collisionPoints[0].y - collisionPoints[1].y) / (collisionPoints[0].x - collisionPoints[1].x);
			if(!isNumber(A))//korygowanie wartosci dla wynikow z dzielenia przez 0
				A = Config.calculationPrecisionFix-1;
			updateRotation(A);//obracanie obiektu zaleznie od wartosci tangensu z A
		}
	}
	//zwraca punkt na prostej prostopadly od niej do srodka kola
	private Vector2f getLinePoint(Vector2f pA, Vector2f pB) {
		//parametry funkcji liniowej:
		float A = (pA.y - pB.y) / (pA.x - pB.x);//parametr wspolczynnika a
		if(!isNumber(A))//korygowanie wartosci dla wynikow z dzielenia przez 0
			A = Config.calculationPrecisionFix-1;
		//float B = -1;//parametr przy wspolczynniku y
		float C = pA.y - (A * pA.x);//wyraz wolny
		
		//obliczanie punktu kolizji z ukladu rownan prostej kolizji i prostopadlej do niej prostej przechodzacej przez srodek kola
		float a2 = -(1.0f/A);//przeciwny i odwrotny wspolczynnik kierunkowy
		if(!isNumber(a2))
			a2 = Config.calculationPrecisionFix/2f;
		//y = a2*x + b2 	===>>	b2 = y - (a2*x);
		float b2 = pos.y - (a2 * pos.x);//podstawianie wspolzednych srodka kola by obliczyc drugi parametr funkcji
		//obliczanie wspolzednych punktu kolizji
		float kx = (b2 - C) / (A - a2);
		float ky = A * kx + C;// == a2 * kx + b2
		return new Vector2f(kx, ky);
	}
	//odbija obiekt od sciany o podanym kacie nachylenia
	private void updateRotation(float A) {
		if(type == 2) {//pocisk nie odbija sie, wybucha
			this.objectSpeed = 0;//zatrzymywanie pocisku
			this.radius = 0;//zmniejszanie pocisku do niewidzialnosci
			explode = true;
		}
		else if (type == 3) {//dla portalguna
			this.objectSpeed = 0;//zatrzymywanie obiektu na scianie
		}
		else {
			float newRot = -(float)Math.toDegrees(Math.atan(A));//obliczanie kata z funkcji
			newRot += 90;
			while(newRot < 0)
				newRot += 180;
			while (newRot >= 180)
				newRot -= 180;
			//obliczanie nowej rotacji obiektu
			rotation = -(rotation-newRot) + newRot;
			
			lastEnemyCollison = -2;//resetuje ostatnia kolizje przeciwnka
		}
	}
	//zwraca odleglosc miedzy dwoma punktami
	private float getDistance(Vector2f a, Vector2f b) {
		return (float)Math.sqrt((Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2)));
	}
	//przywraca ostatnia niekolizyjna pozycje
	public void resolvePos() {
		pos.x = lastPos[1].x + ((pos.x-lastPos[1].x) < 0 ? Config.collisionPositionFix*radius : -Config.collisionPositionFix*radius);
		pos.y = lastPos[1].y + ((pos.y-lastPos[1].y) < 0 ? Config.collisionPositionFix*radius : -Config.collisionPositionFix*radius);
		for(int i=0; i<Config.lastPosCount-1; i++)
			lastPos[i] = lastPos[i+1];
	}
	//obliczanie pozycji i rotacji obiektu
	private void update(float delta) {
		if(type == 3 && objectSpeed == 0)//dla portalu
			rotation += 180 * delta;//wieczne obracanie sie o 180 stopni na sekunde
		lastCollideTime += delta;
		if(lastCollideTime > 0.75f)
			lastEnemyCollison = -2;//resetuje ostatnia kolizje przeciwnka
		
		//obliczanie zwrotu wektora ruchu zaleznie od rotacji obiektu gracza
		float velocityX = (float)Math.sin(Math.toRadians(rotation));
		float velocityY = (float)Math.cos(Math.toRadians(rotation));
		
		//aktualizacja pozycji
		pos.x += objectSpeed * delta * velocityX;
		pos.y += objectSpeed * delta * velocityY;
		
		//sprawdzanie czy obiekt nie wpadl na sciane
		if(collidedTime > 0)
			collidedTime -= delta;
		else if (collidedTime < 0)
			collidedTime = 0;
		float tmpRot = rotation;
		if(!checkWallCollision()) {
			if(!collided) {
				if(collidedTime == 0) {
					for(int i=Config.lastPosCount-1; i>0; i--)
						lastPos[i] = new Vector2f(lastPos[i-1]);
					lastPos[0].x = pos.x;
					lastPos[0].y = pos.y;
				}
			}
			else 
				collided = false;
		}
		else {
			int max = Config.lastPosCount;//maksymalna ilosc wykonania petli naprawiajacej kolizje
			do {
				if(type != 2) resolvePos();
				if(type == 0 && Config.gameType == 2 && (Config.campainglLvl == 3 || Config.campainglLvl == 7))//jesli jest to poziom elektrowni lub burzy kampani i obiektem jest gracz
					hp -= 34;//traci zycie
				max--;
			} while(checkWallCollision() && max > 0);
		}
		//naprawa blednej kalkulacji rotacji po kolizji
		if(Math.abs(rotation - tmpRot) < 25 && collided)
			rotation = tmpRot > rotation ? tmpRot - 90 : tmpRot + 90;
		
		isOnMap();//sprawdza czy obiekt nie wylecial za mape
		
		resolveRotation();//utrzymuje rotacje w zakresie 1 - 360

		//OBLICZANIE WIDOCZNEJ DLA UZYTKOWNIKA ROTACJI OBIEKTU
		float rotDelta = rotation-visibleRotation;//roznica rotacji prawdziwej a tej widocznej dla uzytkownika
		//korygowanie przeskokow w rotacji (np podczas odbicia sie od sciany)
		if(Math.abs(rotDelta) > Math.abs((rotation+360) - (visibleRotation))) 
				rotDelta = (rotation+360) - (visibleRotation);
		if(Math.abs(rotDelta) > Math.abs((rotation-360) - (visibleRotation))) 
			rotDelta = (rotation-360) - (visibleRotation);
		visibleRotation += (rotDelta / Config.turnPower) * delta * Config.turnPower * 20;//aktualizacja widocznej rotacji
		//utrzymanie widocznej rotacji w zakresie od 1 do 360 stopni
		if(visibleRotation > 360)
			visibleRotation -= 360;
		if(visibleRotation < 0)
			visibleRotation += 360;
		/////////////////////////////////////////////////
	}
	
	//renderowanie obiektu na odpowiedniej pozycji
	public void render(float delta) {
		this.update(delta);
		//kopiowanie pozycji obiektu dla ewentualnie pozniejszego przywrocenia podczas kolizji
		//obliczanie punktu srodka obiektu
		visiblePos.x = gridBounds.x + (pos.x)*gridBounds.width;
	    visiblePos.y = gridBounds.y + (1-pos.y)*gridBounds.height;
		
	    if(type != 0 && Config.shadows)
	    	shadow.render(new Vector2f(visiblePos), radius*gridBounds.height);
	    
	    glPushMatrix();//blokowanie zmian matrixowych dla poprzednio wyrenderowanych obiektow
	    glTranslatef(visiblePos.x, visiblePos.y, 0); //przesuwanie obrazu tak by obrocic obiekt wzgledem jego srodka
	    glRotatef(visibleRotation, 0, 0, 1);//obracanie obiektu
	    glTranslatef(-visiblePos.x, -visiblePos.y, 0);//ponowne przesuniecie obrazu na wczesniejsza pozycje
	    
	    //renderownaie figury z textura obiektu
	    if(texture != null)
	    	glBindTexture(GL_TEXTURE_2D, texture.getTextureID());//ustawienie textury
	    else
	    	glBindTexture(GL_TEXTURE_2D, 0);//usuwanie textury z opengl dla nastepnych polygonow
	    if(alfa == 1)
	    	glColor3f(color.x, color.y, color.z);//kolor obiektu bez przezroczyscosci
	    else 
	    	glColor4f(color.x, color.y, color.z, alfa);//przezroczysty kolor
	    glBegin(GL_QUADS);
	      glTexCoord2f(0, 0);
	      glVertex2f(visiblePos.x - radius*gridBounds.width, visiblePos.y - radius*gridBounds.height);
	      glTexCoord2f(1, 0);
	      glVertex2f(visiblePos.x + radius*gridBounds.width, visiblePos.y - radius*gridBounds.height);
	      glTexCoord2f(1, 1);
	      glVertex2f(visiblePos.x + radius*gridBounds.width, visiblePos.y + radius*gridBounds.height);
	      glTexCoord2f(0, 1);
	      glVertex2f(visiblePos.x - radius*gridBounds.width, visiblePos.y + radius*gridBounds.height);
	   glEnd();
	   
	   glPopMatrix();//puszczenie matrixa (zamkniecie kodu od wywolania fukncji glPushMatrix())
	}
}
