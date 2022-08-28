package gra;

import static org.lwjgl.opengl.GL11.*;//importowanie calego OpenGL 11

import org.lwjgl.input.Keyboard;
import org.newdawn.slick.opengl.Texture;

public class Loading {
	private Window callBack;//obiekt nadrzedny
	
	private Texture introTexture;//textura wyswietlana dla prezentacji gry
	private Texture loadingTexture;//ikonka klepsydry
	
	private float czasPrezentacji = Config.czasPrezentacji+Config.czasRozjasniania;//calkowity czas pokazywania intra gry
	private boolean showStarted = false;//zmienna potrzebna do pominiecia obliczen w pierwszej klatce renderowania
	
	public Loading(Window callBack)  {
		this.callBack = callBack;//przypisanie obiektu Window do zmiennej globalnej by zwracac wyniki ladowania
		
		setUpRender();//wywoluje funkcje ktora konfiguruje opengl do wyswietlania polygonow
		//ladowanie jedynych textur potrzebnych w animacji intra
		preLoad();
		introTexture = this.callBack.assetsManager.getTexture("assets/Textures/napisIntro.png");
		loadingTexture = this.callBack.assetsManager.getTexture("assets/Textures/loading.png");
	}
	//laduje textury potrzebne jedynie do pokazania instra
	private void preLoad() {
		this.callBack.assetsManager.parameter.minFilter = GL_LINEAR;//ustawienie parametru filtrowania textur
		this.callBack.assetsManager.parameter.magFilter = GL_LINEAR;
		this.callBack.assetsManager.loadTexture("assets/Textures/napisIntro.png");
		this.callBack.assetsManager.loadTexture("assets/Textures/loading.png");
	}
	//laduje assety potrzebne uruchomeinia do menu glownego
	private void load() {
		//ladowanie textur, czcionek i dzwiekow za pomoca funkcji w klasie AssetsManager
		Config.loadCommonAssets(callBack.assetsManager);//laduje assety potrzebne podczas calej gry
		Config.loadMenuAssets(callBack.assetsManager);//laduje assety potrzebne jedynie w menu glownym
		Config.loadFonts(callBack.assetsManager);//laduje czcionki
	}
	//renderuje intro i przeprowadza procedury ladowania w odpowiednim czasie
	public void render(float delta) {
		glClearColor(0.32f, 0.61f, 1.0f, 1.0f);//czysci tlo okna w kolorze jasnoniebieskim
		glClear(GL_COLOR_BUFFER_BIT);//czysci powierzchnie okna by wyrenderowac nastepna klatke
		
		//odliczanie i pokazywanie textury z nazwa gry
		if(czasPrezentacji > Config.czasPrezentacji)//etap rozjasniania
			drawRozjasnienie((float)Math.pow((czasPrezentacji - Config.czasPrezentacji)/Config.czasRozjasniania, 0.5f));
		else if(czasPrezentacji-Config.czasRozjasniania >= 0)//etap plynnego pojawiania sie logo
			drawIntro((float)Math.pow(1 - (czasPrezentacji-Config.czasRozjasniania)/(Config.czasPrezentacji-Config.czasRozjasniania), 4));//rysowanie intra
		else {//etap pokazywania statycznego obrazu
			drawIntro(1);//rysuje nieprzezroczyste intro
			callBack.assetsManager.unloadTexture("assets/Textures/napisIntro.png");
			callBack.assetsManager.unloadTexture("assets/Textures/loading.png");
			load();//funkcja laduje potrzebne textury, dzwieki i czcionki
			callBack.startMenu();//wywolanie funkcji w obiekcie Window ktora zakonczy procedure ladowania
		}
		
		if(showStarted)//odliczanie  czasu z pominieciem pierwszej klatki
			czasPrezentacji -= delta;
		else
			showStarted = true;
		
		if(checkSkipIntro())//sprawdzanie czy uzytkownik chce pominac animacje
			czasPrezentacji = Config.czasRozjasniania;
	}
	private void drawRozjasnienie(float alfa) {
		//renderowanie plynnie rozjasniajacego sie tla bez textury
		glDisable(GL_TEXTURE_2D);//tymczasowe wylaczenie textur by wyrenderowac tlo
		glBindTexture(GL_TEXTURE_2D, 0);//usuwanie textury z opengl dla nastepnych polygonow
		glColor4f(0,0,0, alfa);//kolor czarny plynnie znikajacy
		glBegin(GL_QUADS);
	      glVertex2f(0,0);
	      glVertex2f(Config.width, 0);
	      glVertex2f(Config.width, Config.height);
	      glVertex2f(0, Config.height);
	    glEnd();
	    glEnable(GL_TEXTURE_2D);//ponowne wlaczenie textur
	}
	private void drawIntro(float alfa) {
		//renderowanie prostokatu z textura napisu
		glBindTexture(GL_TEXTURE_2D, introTexture.getTextureID());//usuwanie textury z opengl dla nastepnych polygonow
		glColor4f(1,1,1, alfa);//kolor bialy z kanalem alfa plynnie zwiekszanym
	    glBegin(GL_QUADS);
	      glTexCoord2f(0, 0);
	      glVertex2f(0, Config.height*0.5f-Config.width/4f);
	      glTexCoord2f(1, 0);
	      glVertex2f(Config.width, Config.height*0.5f-Config.width/4f);
	      glTexCoord2f(1, 1);
	      glVertex2f(Config.width, Config.height*0.5f-Config.width/4f+Config.width/2f);
	      glTexCoord2f(0, 1);
	      glVertex2f(0, Config.height*0.5f-Config.width/4f+Config.width/2f);
	   glEnd();
	   
	   //renderuje ikonke klepsydry
	   glBindTexture(GL_TEXTURE_2D, loadingTexture.getTextureID());//usuwanie textury z opengl dla nastepnych polygonow
		glColor4f(1,1,1, alfa);//kolor bialy bez przezroczystosci
	    glBegin(GL_QUADS);
	      glTexCoord2f(0, 0);
	      glVertex2f(Config.width - Config.height*0.15f, Config.height*0.85f);
	      glTexCoord2f(1, 0);
	      glVertex2f(Config.width, Config.height*0.85f);
	      glTexCoord2f(1, 1);
	      glVertex2f(Config.width, Config.height);
	      glTexCoord2f(0, 1);
	      glVertex2f(Config.width - Config.height*0.15f, Config.height);
	   glEnd();
	}
	//spradza klawisz enter i escape na klawiaturze
	private boolean checkSkipIntro() {
		return Keyboard.isKeyDown(Keyboard.KEY_RETURN) || Keyboard.isKeyDown(Keyboard.KEY_ESCAPE);
	}
	
	private void setUpRender() {
		//konfiguracja renderowania figur opengl
		glMatrixMode(GL_PROJECTION);
		glOrtho(0, Config.width, Config.height, 0, 0, 1);
		glEnable(GL_TEXTURE_2D);//wlaczenie textur
		
		//wlaczenie obslugi przezroczystosci
		glEnable(GL_BLEND);
 		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	}
}
