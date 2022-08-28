package gra;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_NEAREST;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import manageassets.AssetsManager;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.newdawn.slick.Color;

public class Config {
	public static int width = 800;//szerokosc okna z grą (jesli nie wlaczony pelny ekran)
	public static int height = 600;//jak wyzej tylko ze dotyczy wysokosci
	public static boolean fullscreen = false;//pelny ekran lub nie
	public static int fps = 60;//maksymalna czestotliwosc odswiezania okna z gra (w klatkach na sekunde)
	public static boolean vsync = true;//synchronizacja pionowa dla okna z gra
	public static int maxMemory = 64;//ilosc pamieci po przekroczeniu ktorej zostaje wywolana funkcja czyszczaca pamiec
	
	public static boolean enableMusic = true;//wlaczona lub wylaczona muzyka
	public static boolean enableSounds = true;//wlaczone lub wylaczone efekty dzwiekowe
	public static boolean shadows = true;//wlaczanie/wylaczanie cieni
	public static boolean particles = true;//wlaczanie/wylaczanie czasteczek
	
	public static float effectsVolume = 1.0f;//od 0 do 1, glosnosc efektow dzwiekowych
	public static float musicVolume = 1.0f;//od 0 do 1, glosnosc muzyki
	public static int difficult = 1;//poziom trudnosci, 0 - latwy, 1 - sredni, 2 - trudny
	public static String[] difficultNames = new String[]{"Łatwy", "Średni", "Trudny"};//nazwy poziomow trudnosci
	
	public static float backgroundsSwitchTime = 10.0f;//co ile sekund nastepuje przejscie tla w menu
	public static float backgroundSwitchSpeed = 10.0f;//dlugosc trwania ww. przejscia
	
	public static int gameType = 0;//tryb gry: 0 - samouczek, 1 - survival, 2 - kampania
	public static int survivalLvl = 1;//numer mapy wybranej przez gracza
	public static int campainglLvl = 1;//numer mapy wybranej przez gracza dla kampani
	public static float enemyFrequency = 10.0f;//(10.0)co ile sekund pojawia sie nowy przeciwnik
	public static float firstEnemyTime = 3.0f;//(3.0)czas po jakim pojawi sie pierwszy przeciwnik
	
	public static int resolution = 1;//przechowuje index aktualnie ustawionej rozdzielczosci z tablicy ponizej
	public static String[] resolutions = new String[]{"640\nx\n480", "800\nx\n600", "1024\nx\n768", "1280\nx\n720", "1366\nx\n768", "1280\nx\n1024", "1920\nx\n1080"};//rozdzielczosci dostepne w ustawieniach
	
	public static boolean pause = false;//pauza podczas gry
	
	public static float globalMenuOffset = 0.0f;//dodatkowa odleglosc od ekranu w ktorej kontrolki sa renderowane (w ulamku wysokosci lub szerokosci)
	
	public static float czasRozjasniania = 1.5f;//(2.0) czas rozjasniania lub przyciemniania ekranu przy niektorych przejsciach (w sekundach)
	public static float czasPrezentacji = 4.0f;//(6.0) czas pokazywania animacji loga przy ladowaniu (w sekundach)
	public static float logoImageSize = 0.4f;//rozmiar obrazka z logiem (w ulamku wysokosci okna)

	public static float czasOdliczania = 3.0f;//odliczanie do startu gry
	
	public static float viewPosToleration = 0.001f;//wartosc przy ktorej animacja przejscia sie konczy
	public static float speedTransition = 4.0f;//szybkosc przesuwania obrazu w animacji menu

	public static float gameWidthRatio = 1.5f;//stosunek szerokosci do wysokosci wyswietlanego obszaru gry w czasie gry

	public static float bossRadius = 0.15f;//promien bossa
	public static float bossHp = 500;//zycie bossa
	
	public static int lastPosCount = 10;//ilosc ostatnich zapamietywanych pozycji obiektu
	public static float playerRadius = 0.04f;//promien kola jakim jest gracz (w ulamku wysokosci obszaru gry)
	public static float playerSpeed = 0.3f;//predkosc obiektu gracza (ulamek wysokosci planszy gry jaki pokona gracz w przeciagu sekundy)
	public static float turnPower = 360.0f;//ilosc stopni o jaka obroci sie obiekt gracza w ciagu sekundy
	
	public static float particleFrequency = 0.01f;//co jaki czas ma zostac utworzona nowa czasteczka w obiekcie Player ( w sekundach)
	public static float particleTime = 0.75f;//czas istnienia czasteczki (w sekundach)
	public static float visibleParticleSize = 1.5f;//widoczny rozmiar startowy czasteczki (w ulamku promienia gracza)
	public static Color particleColor = new Color(1f, 1f, 1f);//domyslny kolor czasteczek
	public static int maxWeatherParticles = 100;//maksymalna ilosc czasteczek pogody

	public static float spawnBorder = 0.05f;//(0.05)odleglosc od krawedzi mapy na ktorej nie sa spawnione spawnery ani efekty
	
	public static float enemySpawnerRadius = 0.05f;//(0.05)promien portalu z ktorego wylania sie przeciwnik (w ulamku wysokosci obszaru gry)
	public static float enemySpawnerParticleSpeed = 0.07f;//(0.07)predkosc poruszania czastek w spawnerze
	public static float enemyRadius = 0.03f;//(0.03)promien kola jakim jest okragly przeciwnik (w ulamku wysokosci obszaru gry)
	public static float enemySpeed = 0.4f;//(0.04)szybkosc poruszania sie obiektow przeciwnikow (ulamek wysokosci planszy gry jaki pokona gracz w przeciagu sekundy)
	public static Color enemyColor = new Color(1f, 0f, 0f);
	public static int damageLayers = 3;//ile warstw pokazuje sie na przeciwniku przy maksymalnym stopniu uszkodzenia
	
	public static float mapTextureTiles = 4;//ile razy textura ma zostac powtorzona w pionie i poziomie
	public static String mapFolder = "Maps";//nazwa folderu z mapami
	public static String mapPrefix = "map_";//poczatek nazwy pliku z mapa
	public static String mapConfigFile = "mapConfig";//plik z informacjami o mapach
	public static String mapExtension = ".obj";//rozszerzenie pliku z obiektem mapy
	public static String configFileName = "config";//nazwa pliku z ustawieniami
	
	public static String[] mapNames;//przechowuje nazwy mapek
	public static int[] mapRecords;//przechowuje rekordy dla danych map w trybie survival
	public static int[][] campaingRecords;//ilosc gwiazdek osiagnietych na kazdym poziomie, na kazdym z poziomow trudnosci
	
	public static float calculationPrecisionFix = 10000;//ustawia precyzje dla obliczen kolizyjnych (nie ma wplywu na wydajnosc) 
	public static float collisionPositionFix = 0.1f;//procent przesuniecia o roznice wektorow pomiedzy poprzednim polozeniem gracza a obecnym w trakcie kolizji
	public static float distanceToCollisionLine = 0.8f;//(0.75f)dodatkowa odleglosc w ulamku promienia obiektu do kolizji lini

	public static float hitStrength = 15.0f;//(15)ilosc hp zabierana graczowi podczas zderzenia z przeciwnikiem
	public static int hitParticles = 100;//czasteczki pojawiajace sie podczas kolizji gracza z przeciwnikiem
	public static float hitExplosionTime = 1.0f;//(0.3)//czas trwania explozji podczas kolizji gracza z przeciwnikiem w sekundach
	public static float hitExplosionParticleSpeed = 0.1f;//(0.2)//predkosc poruszania sie czasteczek podczas sekundy
	
	public static float randomNewEffectFrequency = 1.0f;//(1)co ile czasu (w sekundach) losowane jest pojawienie sie nowego efektu lub nie
	public static float newEffectChance = 0.3f;//(0.3)szansa od 0 do 1 na pojawienie sie efektu podczas efektu
	public static float effectRadius = 0.04f;//promien renderowanego efektu (najlepiej taki jak promien gracza 
	//efekty kolejno: spowolnienie wszystkich przeciwnikow, przyspieszenie, apteczka, bron dla gracza, portal gun
	public static float effectsLifeTime = 35.0f;//(35)czas zycia efektu
	public static float[] effectsDurations = new float[]{5.0f, 5.0f, 0.0f, 10.0f, 5.0f, 10.0f};//czasy trwania efektow zaleznie od indexu efektu (w sekundach)
	public static float effectsIconRadius = 0.04f;//promien ikonki efektu
	public static float effectsTimeBarLength = 0.1f;//poczatkowa dlugosc paska odliczajacego czas do konca trwania efektu
	public static float effectsTimeBarHeight = 0.015f;//wysokosc paska odliczajacego czas do konca trwania efektu
	public static float kitPower = 7.5f;//hp zwracane po zebraniu apteczki

	public static float wallCollisionTime = 0.1f;//(0.1)czas po ktorym zapisywanie poprzednich pozycji kulki zostaje wznowione
	public static float wallCollisionStunTime = 0.5f;//(0.5)ile czasu (w sekundach) gracz jest zamroczony po uderzeniu w sciane
	public static float weaponRadiusRatio = 1.5f;//rozmiar broni gracza w stosunku do rozmiaru gracza
	public static float bulletSpeed = 1.0f;//predkosc pocisku
	public static float bulletSensitivity = 0.25f;//czestotliwosc strzelania (w sekundach)
	public static float portalGunSensitivity = 0.5f;//czestotliwosc strzelania z portalguna (w sekundach)
	public static float enemyShootPower = 25.01f;//ilosc hp zabierana przeciwnikowi podczas strzalu
	public static int shootParticles = 35;//ilosc czasteczek pojawiajacych sie w przypadku kolizji pocisku z czyms
	public static int bombParticles = 200;//ilosc czasteczek pojawiajacych sie po wybuchu bomby
	public static float bombDmgRadius = 0.5f;//promien razenia bomby (w ulamku wysokosci planszy gry)
	public static float bulletLifeTime = 3.0f;//czas zycia wystrzelonego pocisku
	public static float portalLifeTime = 15.0f;//czas istnienia portalu
	public static float bombLifeTime = 3.0f;//czas do wybuchu bomby
	
	//zapisuje wszystkie zmienne do pliku tekstowego
	public static void save() {
		String text = "";//tworzenie zmiennej ktora bedzie przechowywac tekst do wpisania do pliku
		//dodawanie do zmiennej nazw zmiennych do zapisania i ich wartosci
		text += "width" + "=" + width + "\n";
		text += "height" + "=" + height + "\n";
		text += "fullscreen" + "=" + fullscreen + "\n";
		text += "fps" + "=" + fps + "\n";
		text += "vsync" + "=" + vsync + "\n";
		text += "resolutionn" + "=" + resolution + "\n";
		text += "speedTransition" + "=" + speedTransition + "\n";
		text += "gameWidthRatio" + "=" + gameWidthRatio + "\n";
		text += "difficult" + "=" + difficult + "\n";
		text += "survivalLvl" + "=" + survivalLvl + "\n";
		text += "enableMusic" + "=" + enableMusic + "\n";
		text += "enableSounds" + "=" + enableSounds + "\n";
		text += "shadows" + "=" + shadows + "\n";
		text += "particles" + "=" + particles + "\n";
		//System.out.println(text);
		//zapisywanie do pliku (tworzenie pliku jesli nie istnieje)///////
		File plik = new File("assets" + "/" + configFileName);//uchwyt do pliku
		if (!plik.exists()) {//jesli takowy jeszcze nie istnieje
			try {
				plik.createNewFile();//zostaje stworzony
		    } catch (IOException e) {
		        e.printStackTrace();//ewentualnie zostaje wypisany kod bledu
		    }
		}
		if(plik.isFile() && plik.canWrite()) {//jesli plik ma prawa do zapisu i jest plikiem
			try {
				FileWriter writerFile = new FileWriter(plik.getAbsoluteFile(), false);//obiekt potrzebny do stworzenia zmiennej ponizej (drugi argument deprecjonuje to czy plik ma byc uprzednio wyczyszczony)
				BufferedWriter writer = new BufferedWriter(writerFile);//tworzenie obiektu ktory pozwala na zapis do pliku
				writer.write(text);//zapisywanie tekstu do pliku
				writer.close();//zamykanie obiektu
			} catch (IOException e) {
				e.printStackTrace();//ewentualnie wypisanie kodu bledu
			}
		}
	}
	//odczytuje wszystkie zmienne z pliku tekstowego i przydziela je
	public static void load() {
		String text = "";//przechowuje zawartosc tekstowa pliku z ustawieniami
		File plik = new File("assets" + "/" + configFileName);//uchwyt do pliku z ustawieniami
		if(plik.exists() && plik.isFile() && plik.canRead()) {//jesli plik ma prawa do odczytu, istnieje i jest plikiem
			try {
				text = readFile(plik.getPath());
	        } catch (IOException e) {
	            e.printStackTrace();//lub wyswietlenie bledu...
	        }
			String[] lines = text.split("\n");//dzielenie pliku na poszczegolne linie
			//konwertowanie i przypisywanie wartosci do odpowiednich zmiennych (w kolejnosci takiej jak w funkcji save()
			try {
				width = Integer.valueOf(lines[0].split("=")[1]);
				height = Integer.valueOf(lines[1].split("=")[1]);
				fullscreen = Boolean.valueOf(lines[2].split("=")[1]);
				fps = Integer.valueOf(lines[3].split("=")[1]);
				vsync = Boolean.valueOf(lines[4].split("=")[1]);
				resolution = Integer.valueOf(lines[5].split("=")[1]);
				speedTransition = Float.valueOf(lines[6].split("=")[1]);
				gameWidthRatio = Float.valueOf(lines[7].split("=")[1]);
				difficult = Integer.valueOf(lines[8].split("=")[1]);
				survivalLvl = Integer.valueOf(lines[9].split("=")[1]);
				enableMusic = Boolean.valueOf(lines[10].split("=")[1]);
				enableSounds = Boolean.valueOf(lines[11].split("=")[1]);
				shadows = Boolean.valueOf(lines[12].split("=")[1]);
				particles = Boolean.valueOf(lines[13].split("=")[1]);
			}
			catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
	}
	//laduje czcionki do assets manager
	public static void loadFonts(AssetsManager manager) {
		manager.loadFont("assets/Fonts/Poetsen.ttf", Config.height*0.3f, "bigFont", true);
		manager.loadFont("assets/Fonts/Poetsen.ttf", Config.height*0.13f, "Poetsen", true);
		manager.loadFont("assets/Fonts/Poetsen.ttf", Config.height*0.075f, "PoetsenMedium", true);
		manager.loadFont("assets/Fonts/Poetsen.ttf", Config.height*0.1f, "PoetsenSmall", true);
		manager.loadFont("assets/Fonts/Poetsen.ttf", Config.height*0.05f, "PoetsenSmallest", true);
	}
	//laduje assety dla menu
	public static void loadMenuAssets(AssetsManager manager) {
		manager.parameter.minFilter = GL_LINEAR;//ustawienie parametru filtrowania textur
		manager.parameter.magFilter = GL_LINEAR;
		
		manager.loadTexture("assets/Textures/GUI/background1.png");
		manager.loadTexture("assets/Textures/GUI/background2.png");
		manager.loadTexture("assets/Textures/GUI/background3.png");
		manager.loadTexture("assets/Textures/GUI/logoAuthor.png");
		manager.loadTexture("assets/Textures/logo.png");
		manager.loadTexture("assets/Textures/GUI/switchTexture.png");
		manager.loadTexture("assets/Textures/GUI/music.png");
		manager.loadTexture("assets/Textures/GUI/sound.png");
		manager.loadTexture("assets/Textures/GUI/disable.png");
		
		manager.parameter.minFilter = GL_NEAREST;//ustawienie parametru filtrowania textur
		manager.parameter.magFilter = GL_NEAREST;
		
		manager.loadTexture("assets/Textures/GUI/znacznik.png");
	}
	//czysci assety dla menu
	public static void unloadMenuAssets(AssetsManager manager) {
		manager.unloadTexture("assets/Textures/GUI/background1.png");
		manager.unloadTexture("assets/Textures/GUI/background2.png");
		manager.unloadTexture("assets/Textures/GUI/background3.png");
		manager.unloadTexture("assets/Textures/GUI/logoAuthor.png");
		manager.unloadTexture("assets/Textures/logo.png");
		manager.unloadTexture("assets/Textures/GUI/switchTexture.png");
		manager.unloadTexture("assets/Textures/GUI/znacznik.png");
		manager.unloadTexture("assets/Textures/GUI/music.png");
		manager.unloadTexture("assets/Textures/GUI/sound.png");
		manager.unloadTexture("assets/Textures/GUI/disable.png");
	}
	//laduje assety dla gry
	public static void loadGameAssets(AssetsManager manager) {
		manager.parameter.minFilter = GL_LINEAR;//ustawienie parametru filtrowania textur
		manager.parameter.magFilter = GL_LINEAR;
		
		manager.loadTexture("assets/Textures/wallStun.png");
		manager.loadTexture("assets/Textures/pauseIcon.png");
		manager.loadTexture("assets/Textures/pauseIconHover.png");
		manager.loadTexture("assets/Textures/player.png");
		manager.loadTexture("assets/Textures/enemy.png");
		manager.loadTexture("assets/Textures/ogien.png");
		manager.loadTexture("assets/Textures/snow.png");
		manager.loadTexture("assets/Textures/rain.png");
		manager.loadTexture("assets/Textures/portal.png");
		manager.loadTexture("assets/Textures/playerHP.png");
		manager.loadTexture("assets/Textures/GUI/arrow.png");
		manager.loadTexture("assets/Textures/simpleParticle.png");
		manager.loadTexture("assets/Textures/laser.png");
		manager.loadTexture("assets/Textures/damage.png");
		manager.loadTexture("assets/Textures/shadow.png");
		manager.loadTexture("assets/Textures/boss.png");
		manager.loadTexture("assets/Textures/bossBullet.png");
		for(int i=1; i <= 8; i++)//ladowanie wszystkich mapek
			manager.loadTexture("assets/Textures/Lvl/" + i + "Texture.png");
		//efekty
		manager.loadTexture("assets/Textures/Effects/weapon.png");
		manager.loadTexture("assets/Textures/Effects/slowAllEnemy.png");
		manager.loadTexture("assets/Textures/Effects/speedUp.png");
		manager.loadTexture("assets/Textures/Effects/kit.png");
		manager.loadTexture("assets/Textures/Effects/pistol.png");
		manager.loadTexture("assets/Textures/Effects/bomb.png");
		manager.loadTexture("assets/Textures/Effects/portalgun.png");
		manager.loadTexture("assets/Textures/Effects/bomba.png");
		manager.loadTexture("assets/Textures/Effects/bombaBullet.png");
		manager.loadTexture("assets/Textures/Effects/portal.png");
		manager.loadTexture("assets/Textures/Effects/portalGunEffect.png");
		
		manager.loadTexture("assets/Textures/tloPlanszy.png");
		
		manager.parameter.minFilter = GL_NEAREST;//ustawienie parametru filtrowania textur
		manager.parameter.magFilter = GL_NEAREST;
		
		manager.loadTexture("assets/Textures/tloPanelu.png");
		manager.loadTexture("assets/Textures/tloGry.png");
		manager.loadTexture("assets/Textures/pixel.png");
	}
	//czysci assety dla gry
	public static void unloadGameAssets(AssetsManager manager) {
		manager.unloadTexture("assets/Textures/wallStun.png");
		manager.unloadTexture("assets/Textures/pauseIcon.png");
		manager.unloadTexture("assets/Textures/pauseIconHover.png");
		manager.unloadTexture("assets/Textures/player.png");
		manager.unloadTexture("assets/Textures/enemy.png");
		manager.unloadTexture("assets/Textures/ogien.png");
		manager.unloadTexture("assets/Textures/snow.png");
		manager.unloadTexture("assets/Textures/rain.png");
		manager.unloadTexture("assets/Textures/portal.png");
		manager.unloadTexture("assets/Textures/playerHP.png");
		manager.unloadTexture("assets/Textures/GUI/arrow.png");
		manager.unloadTexture("assets/Textures/simpleParticle.png");
		manager.unloadTexture("assets/Textures/laser.png");
		manager.unloadTexture("assets/Textures/damage.png");
		manager.unloadTexture("assets/Textures/shadow.png");
		manager.unloadTexture("assets/Textures/boss.png");
		manager.unloadTexture("assets/Textures/bossBullet.png");
		for(int i=1; i <= 8; i++)//czyszczenie wszystkich mapek
			manager.unloadTexture("assets/Textures/Lvl/" + i + "Texture.png");
		//efekty
		manager.unloadTexture("assets/Textures/Effects/weapon.png");
		manager.unloadTexture("assets/Textures/Effects/slowAllEnemy.png");
		manager.unloadTexture("assets/Textures/Effects/speedUp.png");
		manager.unloadTexture("assets/Textures/Effects/kit.png");
		manager.unloadTexture("assets/Textures/Effects/pistol.png");
		manager.unloadTexture("assets/Textures/Effects/bomb.png");
		manager.unloadTexture("assets/Textures/Effects/portalgun.png");
		manager.unloadTexture("assets/Textures/Effects/bomba.png");
		manager.unloadTexture("assets/Textures/Effects/bombaBullet.png");
		manager.unloadTexture("assets/Textures/Effects/portal.png");
		manager.unloadTexture("assets/Textures/Effects/portalGunEffect.png");
		manager.unloadTexture("assets/Textures/tloPlanszy.png");
		manager.unloadTexture("assets/Textures/tloPanelu.png");
		manager.unloadTexture("assets/Textures/tloGry.png");
		manager.unloadTexture("assets/Textures/pixel.png");
	}
	//laduje assety potrzebne caly czas
	public static void loadCommonAssets(AssetsManager manager) {
		//TEXTURY
		manager.parameter.minFilter = GL_NEAREST;//ustawienie parametru filtrowania textur
		manager.parameter.magFilter = GL_NEAREST;
		
		manager.loadTexture("assets/Textures/GUI/buttonHover.png");
		manager.loadTexture("assets/Textures/GUI/buttonNorm.png");
		manager.loadTexture("assets/Textures/GUI/buttonClicked.png");
		
		//DZWIEKI
		manager.loadSound("assets/Sounds/click.ogg", false);
		manager.loadSound("assets/Sounds/shootPlayer.ogg", false);
		manager.loadSound("assets/Sounds/explode.ogg", false);
		manager.loadSound("assets/Sounds/collect.ogg", false);
		manager.loadSound("assets/Sounds/hit.ogg", false);
		manager.loadSound("assets/Sounds/wallHit.ogg", false);
		manager.loadSound("assets/Sounds/win.ogg", false);
		manager.loadSound("assets/Sounds/lose.ogg", false);
	}
	//czysci wszystkie czcionki z pamieci
	public static void unloadAllFonts(AssetsManager manager) {
		manager.unloadFonts();
	}
	//czysci zapisane informacje o postepach w grze (plik mapConfig)
	public static void clearData() {
		//CZYTANIE PLIKU Z NAZWAMI I REKORDAMI MAP
		//uchwyt do pliku
		File fileConf = new File("assets" + "/" + Config.mapFolder + "/" + Config.mapConfigFile);
		String mapsConfigText = "";//przechowuje zawartosc pliku tekstowego
		if(fileConf.exists()) {//jesli takowy istnieje
			try {
				mapsConfigText = readFile(fileConf.getPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
		}
		String[] mapsConfig = mapsConfigText.split("\n");//podzial pliku tekstowego na linie
		String outputText = "";//wyjsciowa zawartosc pliku tekstowego
		for(int i=0; i<mapsConfig.length; i++) {//dla kazdej lini odpowiadajacej jednej mapie w grze
			String[] data = mapsConfig[i].split(" ");//dzielenie lini na poszczegolne dane
			outputText += data[0] + " 0 0 0 0\n";
		}
		//zapisywanie do pliku
		if(fileConf.canWrite()) {
			try {
				FileWriter writerFile = new FileWriter(fileConf.getAbsoluteFile(), false);//obiekt potrzebny do stworzenia zmiennej ponizej (drugi argument deprecjonuje to czy plik ma byc uprzednio wyczyszczony)
				BufferedWriter writer = new BufferedWriter(writerFile);//tworzenie obiektu ktory pozwala na zapis do pliku
				String codedText = new String((outputText).getBytes("UTF-8"));//ustawienie kodowania textu na utf-8
				writer.write(codedText);//zapisywanie tekstu do pliku
				writer.close();//zamykanie obiektu
			} catch (IOException e) {
				e.printStackTrace();//ewentualnie wypisanie kodu bledu
			}
		}
	}
	//aktualizuje tryb wyswietlania okna (rozdzielczosc i pelny ekran lub w oknie)
	public static void updateDisplay() {
		try {
            DisplayMode displayToSet = null;//deklaruje zmienna ktora na koncu bedzie definiowac ustawienia okna
            if (fullscreen) {//jesli zostalo wybrane przelaczanie na fullscreen
                DisplayMode[] modes = Display.getAvailableDisplayModes();//tworzenie tablicy z wszystkimi wspieranymi trybami pelnego ekranu
                int frequency = 0;//deklaracja zmiennej ktora bedzie przechowywac czestotliwosc odswiezania okna
                 
                for (int i=0;i<modes.length;i++) {//dla kazdego z oblugiwanych trybow
                    DisplayMode aktualny = modes[i];//przypisanie aktualnego trybu do  tymczasowej zmiennej
                     
                    if ((aktualny.getWidth() == Config.width) && (aktualny.getHeight() == Config.height)) {//jesli rozdzielczosc w aktualnie sprawdzanym trybie sie zgadza
                        if ((displayToSet == null) || (aktualny.getFrequency() >= frequency)) {//jesli jeszcze nie zostal zadeklarowany tryb wyswietlania lub czestotliwosc wspieranego trybu jest wieksza lub rowna aktualnej
                            if ((displayToSet == null) || (aktualny.getBitsPerPixel() > displayToSet.getBitsPerPixel())) {//a takze jesli bity na pixel aktualnie sprawdzanego trybu sa wieksze od aktualnie przypisanego
                                //zostaje przypisany tryb do wyswietlenia
                            	displayToSet = aktualny;
                                frequency = displayToSet.getFrequency();
                            }
                        }
                        //jesli wlasnie zostal znaleziony zgodny tryb i dodatkowo czestotliwosc odswiezania zgadza sie z ustawieniami
                        if ((aktualny.getBitsPerPixel() == Display.getDesktopDisplayMode().getBitsPerPixel()) &&
                            (aktualny.getFrequency() == Display.getDesktopDisplayMode().getFrequency())) {
                        	//zostaje przypisany, a petla przerwana
                            displayToSet = aktualny;
                            break;
                        }
                    }
                }
            } else {//jesli zostal wybrany tryb w oknie
                displayToSet = new DisplayMode(Config.width, Config.height);//prosta konfiguracja okna podajac jedynie jego wysokosc i szerokosc
            }
             
            if (displayToSet == null)//jesli nie udalo sie znalezc wspierana rozdzielczosc pelnego ekranu
                return;//funkcja zostaje przerwana
 
            Display.setDisplayMode(displayToSet);//zmiana ustawien okna gry
            Display.setFullscreen(fullscreen);//wlaczanie lub wylaczanie trybu pelnego okna
             
        } catch (LWJGLException e) {
            e.printStackTrace();//wyrzucenie bledu
        }
	}
	//czyta plik funckja stworzona dla kompatybilnosci z java 6
	public static String readFile(String file) throws IOException {
	    File filex = new File(file);
	    @SuppressWarnings("resource")//dla eclipse
	    BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filex), "UTF-8"));
		String line = null;
	    StringBuilder stringBuilder = new StringBuilder();
	    String ls = "\n";

	    while((line = reader.readLine()) != null) {
	        stringBuilder.append(line);
	        stringBuilder.append(ls);
	    }
	    return stringBuilder.toString();
	}
}
