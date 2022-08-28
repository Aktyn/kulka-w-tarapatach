package gra;

import static org.lwjgl.opengl.GL11.*;//importowanie calego OpenGL 11
import gui.Bounds;//pobranie wszystkich klas GUI
import gui.Button;
import gui.ButtonListener;
import gui.GUI;
import gui.Image;
import gui.Label;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.Sound;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.opengl.Texture;

public class Game {
	private Window callBack;
	private Bounds gameFieldBounds;//kresla prostokatny obszar na ktorym pokazywana jest gra (proporcje sa niezalezne od rozdzielczosci)
	
	private Player player;//obiekt gracza
	private Boss boss;//obiekt bossa (tylko dla ostatniego poziomu kampani)
	private List<Enemy> enemies;//lista przeciwnikow
	private Texture enemyTexture;//textura potrzebna dla dodawania przeciwnikow
	private Texture damage;//textura uszkodzen kulek jakimi sa przeciwnicy
	private Map map;//obiekt po ktorym porusza sie gracz i przeciwnicy
	private Texture simpleParticle;//textura czasteczki uzywanej np przy wybuchach
	private Texture spawnerTexture;//textura portalu pojawiajacego sie przy tworzeniu nowego przeciwnika
	private Texture[] effectsTextures;//textury efektow pojawiajacych sie w czasie gry
	private List<Explosion> explosions;//lista obiektow renderujacych explozje
	private List<EnemySpawner> spawners;//spawnery przeciwnikow
	private List<Effect> effects;//zbieralne obiekty dajace jakis efekt dla gracza i/lub przeciwnika
	private Vector2f[][] collisionLines;//przechowuje w pamieci punkty krawedzi potrzebnych dla obliczania kolizji
	private Weather weather;//obiekt symulujacy efekty pogodowe typu snieg czy deszcz
	private Texture circleShadowTexture;//textura dla kolowych cieni
	
	private GUI gui;//obiekt zawierajacy wszystkie elementy intefejsu
	
	private int lvl = 1;//przechowuje numer danego poziomu (mapy na ktorej gra uzytkownik)
	private float time;//czas od poczatku gry
	private float timeToNewEnemy = Config.firstEnemyTime;//czas do pojawienia sie nowego przeciwnika
	private int ocena = 0;//dla kampani (jesli gracz przejdzie poziom - to jest liczba gwiazdek)
	private boolean tutorialComplete = false;//true jestli samouczek dobiegl konca
	
	private float timeToNewEffect = Config.randomNewEffectFrequency;//czas do losowania nowego efektu
	
	private float countingTime;
	private boolean counting = true;//jesti true - trwa odliczanie do rozpoczecia gry
	private boolean pauseAnim = false;//deprecyzuje to czy animowane sa przyciski przy przelaczaniu pauzy
	private boolean gameOver = false;//false dopoki gracz zyje
	private boolean tutorialHint = false;//true jesli pokazywana jest informacja samouczka
	private boolean campaingWin = false;//true jesli gracz przejdzie poziom kampani
	private float pauseBtnsPos = 0;//0 - przyciski poza ekranem, 1 - widoczne podczas pauzy
	private boolean record = false;//true jesli zostal pobity rekord
	
	//statystyki
	private float allHpLost = 0;//calkowite stracone zycie od poczatku poziomu
	private int enemiesDead = 0;//calkowita ilosc pokonanych przeciwnikow
	
	////// KONTROLKI GUI
	private Image panelBackground;//tlo panelu z gui
	private Image gameBackground;//tlo ekranu z gra
	private Image wallStun;//textura efektu zamroczenia po uderzeniu w sciane
	private Label lvlLabel;//pokazuje na ktorym poziomie gry jestes
	private Label timeLabel;//pokazuje czas gry
	private Button pauseBtn;//pauzuje gre
	private Button wznowBtn;//wznawia zapauzowana gre
	private Button restartBtn;//restartuje gre z tymi samymi ustawieniami
	private Button nextLvlBtn;//rozpoczyna nastepny poziom
	private Button exitBtn;//pokazuje przyciski potwierdzenia
	private Image playerHp;//pasek zycia gracza
	private Label playerHpInfo;//belka z procentem zycia gracza
	private Label labelHP;//napis nad paskiem zycia
	private Image loseScreen;//pokazuje polprzezroczysty prostokat na calym obszarze ekranu
	private Label deadLabel;//napis zginales wyswietlany po smierci
	private Label resultLabel;//porownuje aktualny czas gry do rekordu (widoczne po smierci gracza)
	private Label newEnemyTime;//odliczanie do pojawienia sie nowego przeciwnika
	private Label countingLabel;//pokazywanie sekund do startu gry
	private Label rekordLabel;//pokazywanie rekordu aktualnego poziomu (jesli taki jest)
	//specyficznie dla samouczka
	private Label samouczekHints;//informacje pojawiajace sie podczas samouczka
	private Button confirmHint;//potwierdzenie przeczytania wskazowki
	private Image arrow;//strzalka wskazujaca na niektore elementy
	
	//dzwieki
	private Sound explode;//dzwiek explozji
	private Sound hit;//dzwiek zderzenia gracza z przeciwnikiem
	private Sound levelComplete;//dzwiek pomyslnego ukonczenia poziomu
	private Sound levelFailed;//dzwiek niepomyslnego ukonczenia poziomu
	private Sound collect;//dzwiek zbierania bonusu przez gracza
	
	public Game(Window callBack) {
		this.callBack = callBack;
		if(Config.particles)
			explosions = new ArrayList<Explosion>();//tworzenie pustej listy obiektow Explosion
		spawners = new ArrayList<EnemySpawner>();//i EnemySpawner
		effects = new ArrayList<Effect>();//i Effect
		setUpRender();//konfiguruje opengl
		lvl = Config.survivalLvl;
		countingTime = Config.czasOdliczania;
		
		time = 0;
		if(Config.gameType == 2)//jesli kampania
			lvl = Config.campainglLvl;
		else if(Config.gameType == 1)//survival
			lvl = Config.survivalLvl;
		else if (Config.gameType == 0)//samouczek
			lvl = 1;
		
		gameFieldBounds = calculateBounds();//obliczanie obszaru gry wedle proporcji
		
		//pobieranie textur, czcionek i dzwiekow potrzebnych do zmiennych
		Texture tlo = callBack.assetsManager.getTexture("assets/Textures/tloGry.png");
		Texture pixel = callBack.assetsManager.getTexture("assets/Textures/pixel.png");
		Texture panelTlo = callBack.assetsManager.getTexture("assets/Textures/tloPanelu.png");
		Texture wallStunTexture = callBack.assetsManager.getTexture("assets/Textures/wallStun.png");
		Texture gridTexture = callBack.assetsManager.getTexture("assets/Textures/tloPlanszy.png");
		Texture playerTexture = callBack.assetsManager.getTexture("assets/Textures/player.png");
		Texture playerHpTexture = callBack.assetsManager.getTexture("assets/Textures/playerHP.png");
		spawnerTexture = callBack.assetsManager.getTexture("assets/Textures/portal.png");
		enemyTexture = callBack.assetsManager.getTexture("assets/Textures/enemy.png");
		Texture particleTexture = callBack.assetsManager.getTexture("assets/Textures/ogien.png");
		Texture snowTexture = callBack.assetsManager.getTexture("assets/Textures/snow.png");
		Texture rainTexture = callBack.assetsManager.getTexture("assets/Textures/rain.png");
		Texture mapTexture = callBack.assetsManager.getTexture("assets/Textures/Lvl/" + lvl + "Texture.png");//pobieranie textury zaleznie od poziomu
		Texture pauseTextures[] = new Texture[] {
				callBack.assetsManager.getTexture("assets/Textures/pauseIcon.png"),
				callBack.assetsManager.getTexture("assets/Textures/pauseIconHover.png"),
		};
		Texture[] buttonTextures = new Texture[]{
				callBack.assetsManager.getTexture("assets/Textures/GUI/buttonNorm.png"),
				callBack.assetsManager.getTexture("assets/Textures/GUI/buttonHover.png"),
				callBack.assetsManager.getTexture("assets/Textures/GUI/buttonClicked.png")
		};
		effectsTextures = new Texture[]{
				callBack.assetsManager.getTexture("assets/Textures/Effects/slowAllEnemy.png"),
				callBack.assetsManager.getTexture("assets/Textures/Effects/speedUp.png"),
				callBack.assetsManager.getTexture("assets/Textures/Effects/kit.png"),
				callBack.assetsManager.getTexture("assets/Textures/Effects/pistol.png"),
				callBack.assetsManager.getTexture("assets/Textures/Effects/portalGunEffect.png"),
				callBack.assetsManager.getTexture("assets/Textures/Effects/bomb.png"),
		};
		Texture[] playerWeapon = new Texture[]{
				callBack.assetsManager.getTexture("assets/Textures/Effects/weapon.png"),
				callBack.assetsManager.getTexture("assets/Textures/Effects/portalgun.png"),
				callBack.assetsManager.getTexture("assets/Textures/Effects/bomba.png"),
		};
		Texture bossBulletTexture = callBack.assetsManager.getTexture("assets/Textures/bossBullet.png");
		Texture laser = callBack.assetsManager.getTexture("assets/Textures/laser.png");
		Texture portal = callBack.assetsManager.getTexture("assets/Textures/Effects/portal.png");
		Texture bomba = callBack.assetsManager.getTexture("assets/Textures/Effects/bombaBullet.png");
		circleShadowTexture = callBack.assetsManager.getTexture("assets/Textures/shadow.png");
		damage = callBack.assetsManager.getTexture("assets/Textures/damage.png");
		simpleParticle = callBack.assetsManager.getTexture("assets/Textures/simpleParticle.png");
		//czcionki
		UnicodeFont menuFont = callBack.assetsManager.getFont("Poetsen");
		UnicodeFont smallFont = callBack.assetsManager.getFont("PoetsenSmall");
		UnicodeFont mediumFont = callBack.assetsManager.getFont("PoetsenMedium");
		UnicodeFont smallestFont = callBack.assetsManager.getFont("PoetsenSmallest");
		UnicodeFont bigFont = callBack.assetsManager.getFont("bigFont");
		//dzwieki
		explode = callBack.assetsManager.getSound("assets/Sounds/explode.ogg");
		hit = callBack.assetsManager.getSound("assets/Sounds/hit.ogg");
		levelComplete = callBack.assetsManager.getSound("assets/Sounds/win.ogg");
		levelFailed = callBack.assetsManager.getSound("assets/Sounds/lose.ogg");
		collect = callBack.assetsManager.getSound("assets/Sounds/collect.ogg");

		Triangle[] mapObject = MapLoader.loadMap(lvl);//laduje wszystkie wielokaty z jakich sklada sie mapa w grze
		collisionLines = MapLoader.loadCollisionLines(lvl);//laduje tylko punkty tworzace krawedzie kolizji
		
		//ODDZIELONY FRAGMENT KODY DLA BOSSA
		if(Config.gameType == 2 && lvl==8) {//tylko na kampani na ostatnim poziomie
			Texture bossTexture = callBack.assetsManager.getTexture("assets/Textures/boss.png");
			boss = new Boss();
			boss.setTexture(bossTexture);
			boss.setBounds(gameFieldBounds.x + (gameFieldBounds.width - gameFieldBounds.height), gameFieldBounds.y, gameFieldBounds.height, gameFieldBounds.height);
			boss.setCollisionLines(collisionLines);
			boss.setBulletTexture(bossBulletTexture);
			boss.setShootSound(callBack.assetsManager.getSound("assets/Sounds/shootPlayer.ogg"));
		}
		
		//tworzenie mapy
		map = new Map();
		map.setBounds(gameFieldBounds.x + (gameFieldBounds.width - gameFieldBounds.height), gameFieldBounds.y, gameFieldBounds.height, gameFieldBounds.height);
		map.setTexture(mapTexture, gridTexture);
		map.setWalls(mapObject);
		
		//tworzenie obiektu gracza
		player = new Player();
		player.setTexture(playerTexture);
		player.setWeaponTexture(playerWeapon);
		player.setBulletTexture(new Texture[]{laser, portal, bomba});
		player.setParticlesTexture(particleTexture);
		player.setShadowTexture(circleShadowTexture);
		player.setSounds(
				callBack.assetsManager.getSound("assets/Sounds/shootPlayer.ogg"),
				callBack.assetsManager.getSound("assets/Sounds/wallHit.ogg"));
		player.setBounds(gameFieldBounds.x + (gameFieldBounds.width - gameFieldBounds.height), gameFieldBounds.y, gameFieldBounds.height, gameFieldBounds.height);
		player.setCollisionLines(collisionLines);
		
		if(Config.particles) {
			if(lvl == 5)
				weather = new Weather(0, snowTexture, new Bounds(gameFieldBounds.x + (gameFieldBounds.width - gameFieldBounds.height), gameFieldBounds.y, gameFieldBounds.height, gameFieldBounds.height));//tworzenie obiektu symulujacego snieg
			if(lvl == 7)
				weather = new Weather(1, rainTexture, new Bounds(gameFieldBounds.x + (gameFieldBounds.width - gameFieldBounds.height), gameFieldBounds.y, gameFieldBounds.height, gameFieldBounds.height));//tworzenie obiektu symulujacego deszcz
		}
		//tworzenie startowych przeciwnikow
		enemies = new ArrayList<Enemy>();
		
		///////// TWORZENIE GUI //////////
		gui = new GUI();
		
		gameBackground = new Image();
		gameBackground.setTexture(tlo);
		gameBackground.setBounds(0, 0, Config.width, Config.height);
		
		loseScreen = new Image();
		loseScreen.setTexture(pixel);
		loseScreen.setColor(1, 0, 0);
		loseScreen.setAlfa(0.0f);
		loseScreen.setBounds(0, -Config.height, Config.width*2f, Config.height*2);
		
		wallStun = new Image();
		wallStun.setTexture(wallStunTexture);
		wallStun.setAlfa(0.0f);
		wallStun.setBounds(gameFieldBounds.x + (gameFieldBounds.width - gameFieldBounds.height), gameFieldBounds.y, gameFieldBounds.height, gameFieldBounds.height);
		
		panelBackground = new Image();
		panelBackground.setTexture(panelTlo);
		panelBackground.setBounds(gameFieldBounds.x, gameFieldBounds.y, gameFieldBounds.width-gameFieldBounds.height, gameFieldBounds.height);
		
		lvlLabel = new Label();
		lvlLabel.setFont(mediumFont);
		lvlLabel.setTextAlign(0, 1);
		lvlLabel.setBounds(gameFieldBounds.x, gameFieldBounds.y + gameFieldBounds.height*0.9f, gameFieldBounds.width-gameFieldBounds.height, gameFieldBounds.height*0.1f);
		lvlLabel.setText(Config.mapNames[lvl-1].replaceAll(" ", "\n"));
		if(Config.gameType == 0)
			lvlLabel.setText("Samouczek");
		
		timeLabel = new Label();
		timeLabel.setFont(mediumFont);
		timeLabel.setTextAlign(0, 2);
		timeLabel.setBounds(gameFieldBounds.x, gameFieldBounds.y + gameFieldBounds.height*0.5f, gameFieldBounds.width-gameFieldBounds.height, gameFieldBounds.height*0.2f);
		
		if(Config.gameType != 0) {//jesli nie jest to samouczek
			rekordLabel = new Label();
			rekordLabel.setFont(smallestFont);
			rekordLabel.setTextAlign(0, 1);
			rekordLabel.setBounds(gameFieldBounds.x, gameFieldBounds.y + gameFieldBounds.height*0.7f, gameFieldBounds.width-gameFieldBounds.height, gameFieldBounds.height*0.1f);
			if(Config.gameType == 1) {
				int seconds = Config.mapRecords[lvl-1];//pobieranie rekordu dla danego lvl
				int minutes = 0;
				while(seconds >= 60) {
					seconds -= 60;
					minutes++;
				}
				if(minutes != 0 || seconds != 0)//jesli rekord istnieje
					rekordLabel.setText("Rekord: " + (minutes>9?minutes:"0"+minutes) + ":" + (seconds>9?seconds:"0"+seconds));
			}
			else if (Config.gameType == 2) {
				int vote = Config.campaingRecords[lvl-1][Config.difficult];
				if(vote != 0)//jesli poziom zostal grany
					rekordLabel.setText(Config.difficultNames[Config.difficult] + "\n" + "Rekord: " + vote + " / 3");
				else
					rekordLabel.setText(Config.difficultNames[Config.difficult]);
			}
		}
		
		labelHP = new Label();
		labelHP.setFont(smallestFont);
		labelHP.setBounds(gameFieldBounds.x, gameFieldBounds.y + gameFieldBounds.height*0.4f, gameFieldBounds.width-gameFieldBounds.height, gameFieldBounds.height*0.1f);
		labelHP.setText("Poziom zdrowia");
		
		newEnemyTime = new Label();
		newEnemyTime.setFont(smallestFont);
		newEnemyTime.setBounds(gameFieldBounds.x, gameFieldBounds.y + gameFieldBounds.height*0.0f, gameFieldBounds.width-gameFieldBounds.height, gameFieldBounds.height*0.2f);
		
		deadLabel = new Label();
		deadLabel.setFont(menuFont);
		deadLabel.setBounds(Config.width*0.1f, Config.height*0.8f+(1-pauseBtnsPos)*Config.height*3.5f, Config.width*0.8f, Config.height*0.2f);
		deadLabel.setText("Zginąłeś");
		
		resultLabel = new Label();
		resultLabel.setFont(smallFont);
		resultLabel.setBounds(Config.width*0.1f, Config.height*0.6f+(1-pauseBtnsPos)*Config.height*3.0f, Config.width*0.8f, Config.height*0.2f);
		resultLabel.setText("");
		
		countingLabel = new Label();
		countingLabel.setFont(bigFont);
		countingLabel.setBounds(gameFieldBounds.x + (gameFieldBounds.width - gameFieldBounds.height), gameFieldBounds.y, gameFieldBounds.height, gameFieldBounds.height);
		countingLabel.setText("Odliczanie");
		
		playerHp = new Label();
		playerHp.setTexture(playerHpTexture);
		playerHp.setBounds(gameFieldBounds.x, gameFieldBounds.y + gameFieldBounds.height*0.35f, (gameFieldBounds.width-gameFieldBounds.height), gameFieldBounds.height*0.1f);
		
		playerHpInfo = new Label();
		playerHpInfo.setFont(smallestFont);
		playerHpInfo.setBounds(gameFieldBounds.x, gameFieldBounds.y + gameFieldBounds.height*0.325f, (gameFieldBounds.width-gameFieldBounds.height), gameFieldBounds.height*0.1f);
		
		pauseBtn = new Button();
		pauseBtn.setBounds(Config.width - Config.height*0.1f, 0.0f, Config.height*0.05f, Config.height*0.1f);
		pauseBtn.setAlfa(0.75f);
		pauseBtn.setColor(0.25f, 1.0f, 0.66f);
		pauseBtn.setNormalTexture(pauseTextures[0]);
		pauseBtn.setHoverTexture(pauseTextures[1]);
		
		wznowBtn = new Button();
		wznowBtn.setBounds(Config.width*0.1f, Config.height*0.5f+(1-pauseBtnsPos)*Config.height*2.5f, Config.width*0.8f, Config.height*0.2f);
		wznowBtn.setFont(menuFont);
		wznowBtn.setText("Wznów");
		wznowBtn.setTextures(buttonTextures);
		
		restartBtn = new Button();
		restartBtn.setBounds(Config.width*0.1f, Config.height*0.2f+(1-pauseBtnsPos)*Config.height*2, Config.width*0.8f, Config.height*0.2f);
		restartBtn.setFont(menuFont);
		restartBtn.setText("Zagraj ponownie");
		restartBtn.setTextures(buttonTextures);
		
		nextLvlBtn = new Button();
		nextLvlBtn.setBounds(Config.width*0.1f, Config.height*0.4f+(1-pauseBtnsPos)*Config.height*2.5f, Config.width*0.8f, Config.height*0.2f);
		nextLvlBtn.setFont(menuFont);
		nextLvlBtn.setText("Następny poziom");
		nextLvlBtn.setTextures(buttonTextures);
		
		exitBtn = new Button();
		exitBtn.setBounds(Config.width*0.1f, Config.height*0.0f+(1-pauseBtnsPos)*Config.height*1.5f, Config.width*0.8f, Config.height*0.2f);
		exitBtn.setFont(menuFont);
		exitBtn.setText("Zakończ");
		exitBtn.setTextures(buttonTextures);
		
		if(Config.gameType == 0) {//dla samouczka
			samouczekHints = new Label();
			samouczekHints.setBounds(gameFieldBounds.x + (gameFieldBounds.width - gameFieldBounds.height), gameFieldBounds.y+gameFieldBounds.height*0.3f, gameFieldBounds.height, gameFieldBounds.height*0.7f);
			samouczekHints.setFont(smallFont);
			samouczekHints.setFontColor(1.0f, 1.0f, 1.0f, 0.0f);
			
			confirmHint = new Button();
			confirmHint.setTextures(buttonTextures);
			confirmHint.setBounds(gameFieldBounds.x + (gameFieldBounds.width - gameFieldBounds.height)+gameFieldBounds.height*0.1f, gameFieldBounds.y+gameFieldBounds.height*0.1f - Config.height*2f, gameFieldBounds.height*0.8f, gameFieldBounds.height*0.2f);
			confirmHint.setText("Rozumiem");
			confirmHint.setFont(smallFont);
			
			arrow = new Image();
			arrow.setTexture(callBack.assetsManager.getTexture("assets/Textures/GUI/arrow.png"));
			arrow.setAlfa(0.0f);
			arrow.setBounds(-100, -100, 100, 100);
		}
		
		gui.addPart(panelBackground);
		gui.addPart(lvlLabel);
		gui.addPart(timeLabel);
		gui.addPart(labelHP);
		gui.addPart(playerHp);
		gui.addPart(playerHpInfo);
		gui.addPart(newEnemyTime);
		gui.addPart(rekordLabel);
		gui.addPart(wallStun);
		if(Config.gameType == 0) {//dla samouczka
			gui.addPart(arrow);
			gui.addPart(samouczekHints);
			gui.addPart(confirmHint);
		}
		gui.addPart(loseScreen);
		//przyciski pauzy dalej w tej kolejnosci
		gui.addPart(countingLabel);
		gui.addPart(deadLabel);
		gui.addPart(resultLabel);
		gui.addPart(wznowBtn);
		gui.addPart(restartBtn);
		gui.addPart(nextLvlBtn);
		gui.addPart(exitBtn);
		gui.addPart(pauseBtn);
		
		setListeners();//dodawanie obslugi zdarzen dla niektorych kontrolek
		//nadawanie dzwieku klikniecia dla przyciskow ktore mozna kliknac
		gui.setClickEffect(callBack.assetsManager.getSound("assets/Sounds/click.ogg"));
	}
	//funkcja dodaje intefejsy nasluchiwania do kontrolek ktore na to pozwalaja
	private void setListeners() {
		wznowBtn.setButtonListener(new ButtonListener(){
            public void clicked(){
            	switchPause();//wznawia gre
            }
        });
		restartBtn.setButtonListener(new ButtonListener(){
            public void clicked(){
            	//restartuje gre
            	Config.pause = false;
            	gameOver = false;
            	pauseAnim = true;
            	callBack.restartGame();
            }
        });
		nextLvlBtn.setButtonListener(new ButtonListener(){
            public void clicked(){
            	//restartuje gre na kolejnym poziomie
            	if(Config.gameType == 2) {
	            	if(Config.campainglLvl < Config.mapNames.length)
	            		Config.campainglLvl++;
            	}
            	if(Config.gameType == 1) {
            		if(Config.survivalLvl < Config.mapNames.length)
	            		Config.survivalLvl++;
            	}
            	Config.pause = false;
            	gameOver = false;
            	pauseAnim = true;
            	callBack.restartGame();
            }
        });
		exitBtn.setButtonListener(new ButtonListener(){
            public void clicked(){
            	//tymczasowo przechodzi do menu, pozniej zrobic by pokazywalo przyciski potwierdzajace
            	Config.pause = false;
            	callBack.exitToMenu();//wylacza gre i przechodzi do menu
            }
        });
		pauseBtn.setButtonListener(new ButtonListener(){
            public void clicked(){
            	//przycisk pauzujacy gre
            	switchPause();
            }
        });
		if(Config.gameType == 0) {//dla samouczka
			confirmHint.setButtonListener(new ButtonListener(){
	            public void clicked(){
	            	//potwierdza wskazowke i kontynuuje samouczek
	            	if(!Config.pause) {
	            		confirmHint();
	            	}
	            }
	        });
		}
	}
	//potwierdza wskazowke w turorialu
	private void confirmHint() {
		if(tutorialComplete) {
			callBack.exitToMenu();//wylacza gre i przechodzi do menu
		}
    	tutorialHint = false;
    	pauseAnim = true;
	}
	//przelacza pauze
	private void switchPause() {
		if(!gameOver)
			Config.pause = !Config.pause;
		pauseAnim = true;
	}
	//inne kalkulacje
	private void update(float delta) {
		//przelaczanie pauzy gdy wcisniety klawisz
		if(Keyboard.next()) {
			if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {//jesli zostal wcisniety klawisz escape
				//pauzowanie gry
				switchPause();
			}
		}
		//obliczenia wspolne dla kazdego trybu gry
		if(Config.gameType != 0) {//procz samouczka
			//odliczanie do nowego efektu
			if(timeToNewEffect > 0) {
				timeToNewEffect -= delta;
			}
			else {
				timeToNewEffect = Config.randomNewEffectFrequency;//resetowanie czasu do nastepnego losowania efektow
				Random random = new Random();//obiekt do generowania losowych liczb
				if(random.nextInt(1000)/1000f < Config.newEffectChance) {//losowanie zaleznie od ktorego efekt pojawi sie lub nie
					int maxTries = 1000;//na wypadek gdyby na mapie nie bylo miejsca
					float x;
			    	float y;
			    	do {
			    		x = Config.spawnBorder + (1f-Config.spawnBorder*2f)*(random.nextInt(10000)/10000f);
			        	y = Config.spawnBorder + (1f-Config.spawnBorder*2f)*(random.nextInt(10000)/10000f);
			        	maxTries--;
			    	}
			    	while(checkEffectsCollision(x, y) && maxTries > 0);
			    	if(maxTries > 0)
			    		effects.add(new Effect(new Vector2f(x, y), effectsTextures, new Bounds(gameFieldBounds.x + (gameFieldBounds.width - gameFieldBounds.height), gameFieldBounds.y, gameFieldBounds.height, gameFieldBounds.height), circleShadowTexture));
				}
			}
			
			//spawnienie przeciwnikow
			if(timeToNewEnemy > 0) {
				timeToNewEnemy -= delta;
				newEnemyTime.setText("Nowy przeciwnik\nza " + (int)timeToNewEnemy + (timeToNewEnemy != 2 && timeToNewEnemy != 3 && timeToNewEnemy != 4 ? " sekund" : " sekundy"));
			}
			else if (timeToNewEnemy < 0) {
				//zaleznie od tego ile minut uplynelo tyle przeciwnikow zostanie jednoczesnie spawnione
				int minutes = (int)(time/60f);
				for(int i=0; i<minutes+1; i++)
					spawnEnemy();
				
				timeToNewEnemy = 0;
			}
			else {
				if(spawners.size() > 0)
					newEnemyTime.setText("Uwaga!!!\nNowi przeciwnicy");
				else
					timeToNewEnemy = Config.enemyFrequency;
			}
		}
		///////////////////////
		if(player.wallHitStunTime > 0) {
			wallStun.setAlfa(1.0f);
		}
		else if (player.wallHitStunTime <= 0) {
			if(wallStun.getAlfa() > 0)
				wallStun.setAlfa(wallStun.getAlfa() - delta);
			else if (wallStun.getAlfa() < 0)
				wallStun.setAlfa(0.0f);
		}
		
		//ZARZADZANIE ROZGRYWKĄ ZALEZNIE OD TRYBU GRY
		switch(Config.gameType) {
		case 0://samouczek
			updateTutorial(delta);
			break;
		case 1://survival
			updateSurvival(delta);//specyficzne obliczenia dla tego trybu rozgrywki
			break;
		case 2://kampania
			updateCampaing(delta);//specyficzne obliczenia dla tego trybu rozgrywki
			break;
		}
		
		float length = (gameFieldBounds.width-gameFieldBounds.height)*player.hp/100f*0.5f;//dlugosc renderowanego paska zycia gracza
		playerHp.setBounds(gameFieldBounds.x + 0.5f*(gameFieldBounds.width-gameFieldBounds.height) - length*1.0f, gameFieldBounds.y + gameFieldBounds.height*0.35f, length, gameFieldBounds.height*0.04f);
		playerHpInfo.setText((int)player.hp + "%");
		float hp = player.hp/100f;//hp gracza od 0 do 1
		playerHp.setColor(hp > 0.5f ? (1f - hp)*2f : 1, hp > 0.5f ? 1f : (hp*2f), 0.4f);//koloruje pasek zdrowia
	
		//SoundStore.get().poll(0);//niezbedne by kontynuowac odtwarzanie streamowanego dzwieku
	}
	//obliczenia dla samouczka
	private void updateTutorial(float delta) {
		if(tutorialHint && Keyboard.isKeyDown(Keyboard.KEY_RETURN))
			confirmHint();
		time += delta;
		if(player.hp > 100.0f)
			player.hp = 100.0f;
		else if (player.hp < 100.0f) {
			player.hp += delta*50f;
		}
		//pokazywanie kolejno informacji dla samouczka
		if(time > 1 && time < 2)
			nextHint("Skręcaj w\nlewo i prawo\nza pomocą\nklawiszy strzałek", 2);
		else if (time > 4 && time < 5)
			nextHint("Możesz włączyć\npauzę w trakcie\ngry naciskając\nklawisz Escape", 5);
		else if (time > 6 && time < 7) {
			arrow.setBounds(gameFieldBounds.x + gameFieldBounds.width-gameFieldBounds.height*1.05f, gameFieldBounds.y + gameFieldBounds.height*0.34f, gameFieldBounds.height*0.2f, gameFieldBounds.height*0.2f);
			arrow.setRotation(40.0f);
			nextHint("Obserwuj pasek\nzdrowia\nNie pozwól by\njego poziom\nspadł do zera", 7);
		}
		else if (time > 9 && time < 10) {
			arrow.setBounds(-100,-100,100,100);
			nextHint("Nie obijaj się\no ściany\nPowoduje to\nchwilowe\nzamroczenie", 10);
		}
		else if (time > 10 && time < 11) {
			spawnEnemy(0.75f, 0.75f);
			this.time = 11;
		}
		else if (time > 12.5f && time < 14) {
			arrow.setBounds(gameFieldBounds.x + gameFieldBounds.width-gameFieldBounds.height*0.45f, gameFieldBounds.y + gameFieldBounds.height*0.75f, gameFieldBounds.height*0.2f, gameFieldBounds.height*0.2f);
			arrow.setRotation(-45.0f);
			nextHint("Pojawił się\nprzeciwnik!\nNie daj mu\nsię dotknąć", 14);
		}
		else if (time > 16 && time < 17) {
			arrow.setBounds(-100,-100,100,100);
			nextHint("Podczas gry,\nco jakiś czas,\npokażą się bonusy\nIch działanie\nzależy od\nikony bonusu", 17);
		}
		else if (time > 19 && time < 20) {
			effects.add(new Effect(new Vector2f(0.25f, 0.75f), effectsTextures, new Bounds(gameFieldBounds.x + (gameFieldBounds.width - gameFieldBounds.height), gameFieldBounds.y, gameFieldBounds.height, gameFieldBounds.height), 3, circleShadowTexture));
			time = 20;
		}
		else if (time > 21 && time < 22) {
			arrow.setBounds(gameFieldBounds.x + gameFieldBounds.width-gameFieldBounds.height*0.95f, gameFieldBounds.y + gameFieldBounds.height*0.75f, gameFieldBounds.height*0.2f, gameFieldBounds.height*0.2f);
			arrow.setRotation(-45.0f);
			nextHint("Jeden z bonusów\npozwala ci strzelać\nklawiszem spacji\nWykorzystaj to i\npokonaj\nprzeciwnika", 22);
		}
		else if(time > 30 && !tutorialComplete) {//oczekiwanie na zabicie przeciwnika
			if(effects.size() < 1) {
				Random random = new Random();//dla generowania losowej pozycji
				int maxTries = 1000;//na wypadek gdyby na mapie nie bylo miejsca
				float x;
		    	float y;
		    	do {
		    		x = Config.spawnBorder + (1f-Config.spawnBorder*2f)*(random.nextInt(10000)/10000f);
		        	y = Config.spawnBorder + (1f-Config.spawnBorder*2f)*(random.nextInt(10000)/10000f);
		        	maxTries--;
		    	}
		    	while(checkEffectsCollision(x, y) && maxTries > 0);
		    	if(maxTries > 0)
		    		effects.add(new Effect(new Vector2f(x, y), effectsTextures, new Bounds(gameFieldBounds.x + (gameFieldBounds.width - gameFieldBounds.height), gameFieldBounds.y, gameFieldBounds.height, gameFieldBounds.height), 3, circleShadowTexture));
			}
		}
		else if (time > 35 && tutorialComplete) {//po przejsciu samouczka
			arrow.setBounds(-100,-100,100,100);
			if(Config.enableSounds)
				levelComplete.play(1.0f, Config.effectsVolume);
			nextHint("Gratulacje!\nUkończyłeś\nwłaśnie samouczek\nJesteś gotowy\nna prawdziwe\nwyzwania", 30);
		}
	}
	//uruchamia pokazywanie informacji samouczka
	private void nextHint(String tekst, float time) {
		samouczekHints.setText(tekst);
		tutorialHint = true;
		pauseAnim = true;
		this.time = time;
	}
	//obliczenia dla kampani
	private void updateCampaing(float delta) {
		time += delta;//odliczanie do konca poziomu
		int sec;
		int min;
		switch(lvl) {//zaleznie od poziomu kampani
		case 1://poczatek gry
		case 4://spacer kosmiczny
		case 5://lodowa jaskinia
			if(time > 120) {
				time = 120;
				if(!campaingWin && Config.enableSounds)
					levelComplete.play(1.0f, Config.effectsVolume);
				campaingWin = true;
				//ocenianie na podstawie straconego zycia
				if(allHpLost == 0)
					ocena = 3;
				else if (allHpLost < 100)
					ocena = 2;
				else
					ocena = 1;
			}
			//dzielenie czasu w sekundach na minuty i sekundy
			sec = 120 - (int)time;
			min = 0;
			while(sec >= 60) {//dopoki ilosc sekund jest wieksza lub rowna 60
				min++;
				sec -= 60;
			}
			timeLabel.setText("Przetrwaj\n" + (min < 10 ? "0"+min : min) + ":" + (sec < 10 ? "0"+sec : sec));
			break;
		case 2://strzelnica
		case 6://pustka
			int enemiesToKill = lvl==2? 10 : 20;//przeciwnicy do zabicia zaleznie od poziomu
			if(enemiesDead >= enemiesToKill) {
				if(!campaingWin && Config.enableSounds)
					levelComplete.play(1.0f, Config.effectsVolume);
				campaingWin = true;
				if(allHpLost == 0)
					ocena = 3;
				else if (allHpLost < 100)
					ocena = 2;
				else
					ocena = 1;
			}
			timeLabel.setText("Zestrzel\n" + (enemiesToKill - enemiesDead) + " wrogów");
			break;
		case 3://elektrownia
		case 7://burza niedurza
			if(time > 120) {
				time = 120;
				if(!campaingWin && Config.enableSounds)
					levelComplete.play(1.0f, Config.effectsVolume);
				campaingWin = true;
				//ocenianie na podstawie straconego zycia
				if(allHpLost == 0)
					ocena = 3;
				else if (allHpLost < 100)
					ocena = 2;
				else
					ocena = 1;
			}
			//dzielenie czasu w sekundach na minuty i sekundy
			sec = 120 - (int)time;
			min = 0;
			while(sec >= 60) {//dopoki ilosc sekund jest wieksza lub rowna 60
				min++;
				sec -= 60;
			}
			timeLabel.setText("Unikaj\n"+(lvl==3?"ścian: ":"chmur: ") + (min < 10 ? "0"+min : min) + ":" + (sec < 10 ? "0"+sec : sec));
			break;
		case 8://boss
			float startBossHp = Config.gameType != 2 ? Config.bossHp : Config.bossHp - 75.0f*(2-Config.difficult);
			if((int)(boss.hp/startBossHp*100f) <= 0) {
				boss.hp = 0;
				if(!campaingWin && Config.enableSounds)
					levelComplete.play(1.0f, Config.effectsVolume);
				campaingWin = true;
				//ocenianie na podstawie straconego zycia
				if(allHpLost == 0)
					ocena = 3;
				else if (allHpLost < 100)
					ocena = 2;
				else
					ocena = 1;
			}
			timeLabel.setText("Boss: " + (int)(boss.hp/startBossHp*100f) + "%");
			break;
		}
		
		if((int)player.hp <= 0) {//jesli zycie gracza spadlo ponizej 1%
			if(!gameOver && Config.enableSounds)
				levelFailed.play(1.0f, Config.effectsVolume);
			gameOver = true;//koniec gry
			Config.pause = true;//pokazywanie tez przyciskow widocznych podczas pauzy
			player.hp = 0;
		}
		if(campaingWin && !gameOver) {
			gameOver = true;
			Config.pause = true;
			time = 0;
			//oblicza ocene na podstawie ilosci straconego hp
			deadLabel.setText("Koniec poziomu");
			resultLabel.setText("Ocena poziomu: " + ocena + " / 3\nGratulacje!");
			//sprawdzanie rekordu
			if(ocena > Config.campaingRecords[lvl-1][Config.difficult]) {
				record = true;
				resultLabel.setText("Ocena poziomu: " + ocena + " / 3\nNowy rekord!");
				saveRecord();
			}
			else {
				if(ocena != Config.campaingRecords[lvl-1][Config.difficult])
					resultLabel.setText("Ocena poziomu: " + ocena + " / 3\nNajlepsza ocena: " + Config.campaingRecords[lvl-1][Config.difficult] + " / 3");
			}
		}
	}
	//obliczenia dla trybu przetrwania
	private void updateSurvival(float delta) {
		time += delta;//licznik
		//dzielenie czasu w sekundach na minuty i sekundy
		int sec = (int)time;
		int min = 0;
		while(sec >= 60) {//dopoki ilosc sekund jest wieksza lub rowna 60
			min++;
			sec -= 60;
		}
		timeLabel.setText("Czas gry\n" + (min < 10 ? "0"+min : min) + ":" + (sec < 10 ? "0"+sec : sec));
		
		if((int)player.hp <= 0) {//jesli zycie gracza spadlo ponizej 1%
			gameOver = true;//koniec gry
			Config.pause = true;//pokazywanie tez przyciskow widocznych podczas pauzy
			player.hp = 0;
			
			//ustalanie tekstu rezultatu
			if((int)time > Config.mapRecords[lvl-1] && !record) {//jesli aktualny czas jest lepszy od rekordu dla danej mapy
				resultLabel.setText("Nowy rekord!\n" + min + " minut i " + sec + " sekund");
				if(Config.enableSounds)
					levelComplete.play(1.0f, Config.effectsVolume);
				if(!record)
					saveRecord();
				record = true;
			}
			else if(!record) {
				//dzielenie najlepszego czasu na minuty i sekundy
				int bestsec = Config.mapRecords[lvl-1];
				int bestmin = 0;
				while(bestsec >= 60) {//dopoki ilosc sekund jest wieksza lub rowna 60
					bestmin++;
					bestsec -= 60;
				}
				resultLabel.setText("Czas: " + min + " minut " + sec + " sekund" + "\n" + "Rekord: " + bestmin + " minut " + bestsec + " sekund");
			}
		}
	}
	//animowanie przyciskow gui
	private void animGui(float delta) {
		if(Config.gameType == 0) {//dla samouczka
			if(tutorialHint) {
				float value = samouczekHints.getFontColor().a;
				if(value < 1) {
					value += (1.0f-value)*delta*Config.speedTransition;
					samouczekHints.setFontColor(1.0f, 1.0f, 1.0f, value);
					confirmHint.setBounds(gameFieldBounds.x + (gameFieldBounds.width - gameFieldBounds.height)+gameFieldBounds.height*0.1f, gameFieldBounds.y+gameFieldBounds.height*0.1f - Config.height*(2.0f-value*2f), gameFieldBounds.height*0.8f, gameFieldBounds.height*0.2f);
					arrow.setAlfa(value);
				}
				else if(value > 1) {
					value = 1.0f;
					samouczekHints.setFontColor(1.0f, 1.0f, 1.0f, value);
					confirmHint.setBounds(gameFieldBounds.x + (gameFieldBounds.width - gameFieldBounds.height)+gameFieldBounds.height*0.1f, gameFieldBounds.y+gameFieldBounds.height*0.1f - Config.height*(2.0f-value*2f), gameFieldBounds.height*0.8f, gameFieldBounds.height*0.2f);
					arrow.setAlfa(value);
				}
			}
			else {
				float value = samouczekHints.getFontColor().a;
				if(value > 0) {
					value -= (value)*delta*Config.speedTransition;
					samouczekHints.setFontColor(1.0f, 1.0f, 1.0f, value);
					confirmHint.setBounds(gameFieldBounds.x + (gameFieldBounds.width - gameFieldBounds.height)+gameFieldBounds.height*0.1f, gameFieldBounds.y+gameFieldBounds.height*0.1f - Config.height*(2.0f-value*2f), gameFieldBounds.height*0.8f, gameFieldBounds.height*0.2f);
					arrow.setAlfa(value);
				}
				else if(value < 0) {
					value = 0.0f;
					samouczekHints.setFontColor(1.0f, 1.0f, 1.0f, value);
					confirmHint.setBounds(gameFieldBounds.x + (gameFieldBounds.width - gameFieldBounds.height)+gameFieldBounds.height*0.1f, gameFieldBounds.y+gameFieldBounds.height*0.1f - Config.height*(2.0f-value*2f), gameFieldBounds.height*0.8f, gameFieldBounds.height*0.2f);
					arrow.setAlfa(value);
				}
			}
		}
		//animowanie przyciskow widocznych podczas pauzy lub przegranejGry
		if(pauseAnim || gameOver) {
			if(gameOver) {//pokazywanie kontrolek tylko dla GameOver
				if(record || campaingWin)
					loseScreen.setColor(0, 0.75f, 0.2f);
				else
					loseScreen.setColor(1, 0, 0);
			}
			else {
				loseScreen.setColor(0, 0, 0);
			}
			if(Config.pause) {//kierowanie pozycji przyciskow ku dolowi
				if(pauseBtnsPos < 1-Config.viewPosToleration) {
					pauseBtnsPos += (1-pauseBtnsPos)*delta*Config.speedTransition*0.75f;
				}
				else {
					pauseBtnsPos = 1;
					pauseAnim = false;//konczenie animacji
				}
			}
			else {//przesuwanie przyciskow poza ekran
				if(pauseBtnsPos > Config.viewPosToleration) {
					pauseBtnsPos -= (pauseBtnsPos)*delta*Config.speedTransition*0.85f;
				}
				else {
					pauseBtnsPos = 0;
					pauseAnim = false;//konczenie animacji
				}
			}
			//aktualizowanie pozycji przyciskow
			if(gameOver) {//wykluczanie kontrolek niewidocznych podczas smierci
				wznowBtn.setBounds(Config.width*0.1f, Config.height*0.5f+(1)*Config.height*2.5f, Config.width*0.8f, Config.height*0.2f);
				deadLabel.setBounds(Config.width*0.1f, Config.height*0.8f+(1-pauseBtnsPos)*Config.height*3.5f, Config.width*0.8f, Config.height*0.2f);
				resultLabel.setBounds(Config.width*0.1f, Config.height*0.625f+(1-pauseBtnsPos)*Config.height*3.0f, Config.width*0.8f, Config.height*0.2f);
			}
			else {
				wznowBtn.setBounds(Config.width*0.1f, Config.height*0.5f+(1-pauseBtnsPos)*Config.height*2.5f, Config.width*0.8f, Config.height*0.2f);
				deadLabel.setBounds(Config.width*0.1f, Config.height*0.8f+(1)*Config.height*3.5f, Config.width*0.8f, Config.height*0.2f);
				resultLabel.setBounds(Config.width*0.1f, Config.height*0.6f+(1)*Config.height*3.0f, Config.width*0.8f, Config.height*0.2f);
			}
			restartBtn.setBounds(Config.width*0.1f, Config.height*0.25f+(1-pauseBtnsPos)*Config.height*2, Config.width*0.8f, Config.height*0.2f);
			if(((Config.gameType == 1 && gameOver) || campaingWin) && lvl != Config.mapNames.length) {//jesli poziom kampani zostal pomyslnie ukoncziony i nie jest to ostatni poziom
				restartBtn.setBounds(Config.width*0.1f, Config.height*0.2f+(1-pauseBtnsPos)*Config.height*2, Config.width*0.8f, Config.height*0.2f);
				nextLvlBtn.setBounds(Config.width*0.1f, Config.height*0.4f+(1-pauseBtnsPos)*Config.height*2.5f, Config.width*0.8f, Config.height*0.2f);
			}
			exitBtn.setBounds(Config.width*0.1f, Config.height*0.0f+(1-pauseBtnsPos)*Config.height*1.5f, Config.width*0.8f, Config.height*0.2f);
			loseScreen.setAlfa(pauseBtnsPos*0.5f);
		}
	}
	//renderuje wszystkie potrzebne efekty gry i gui
	public void render(float delta) {
		animGui(delta);
		//odliczanie do startu gry
		if(!Config.pause) {
			if(countingTime > 0) {
				countingTime -= delta;
				loseScreen.setAlfa(0.5f);
				loseScreen.setColor(0, 0, 0);
				countingLabel.setText(String.valueOf((int)countingTime+1)+"\n");
				countingLabel.setFontColor(1.0f, 0.5f, 0.2f, countingTime-(int)countingTime);
			}
			else if (countingTime > -2) {
				countingTime -= delta;
				loseScreen.setAlfa((countingTime+2.0f)/4f);
				counting = false;
				countingLabel.setText("START\n");
				countingLabel.setFontColor((countingTime+2.0f)/2f+(1-(countingTime+2.0f)/2f)*0.2f, 0.5f*(countingTime+2.0f)/2f+0.5f*(1-(countingTime+2.0f)/2f), 0.2f, (countingTime+2.0f)/2f);
			}
			else if (countingTime < -2) {
				countingTime = -2;
				countingLabel.setText("");
			}
		}
		if(Config.pause || gameOver || campaingWin || counting || tutorialHint)//jesli pauza jest aktywna lub gra skonczona
			delta = 0;//zerowanie delty czyli zatrzymanie wszystkiego
		
		update(delta);//inne obliczenia dla rozgrywki
		glClearColor(0.0f, 0.0f, 0.0f, 1.0f);//czysci tlo okna na czarno
		glClear(GL_COLOR_BUFFER_BIT);//czysci powierzchnie okna by wyrenderowac nastepna klatke
		gameBackground.render();//renderowanie tla poza gui
		map.render(delta);//renderowanie mapy
		//renderowanie efektow
		for(int i=0; i<effects.size(); i++) {
            if(effects.get(i).lifeTime > Config.effectsLifeTime)
            	effects.remove(i);
            else {
				if(effects.get(i).radius > Config.effectRadius*0.8f) {//jesli efekt jest gotowy do wziecia/usuniecia
					if(!effectCollide(effects.get(i)))//jesli nic nie koliduje z efektem
						effects.get(i).render(delta);
					else//usuwanie obiektu
						effects.remove(i);
				}
				else
					effects.get(i).render(delta);
            }
		}
		//renderowanie bossa
		if(Config.gameType == 2 && lvl==8) {//tylko na kampani na ostatnim poziomie
			boss.render(delta);
			//kolizja z  graczem
			if(player.lastCollideTime > 0.1f) {
				if(circleCollision(boss.pos, player.getPos(), boss.radius, player.radius)) {
					//odskakiwanie gracza od bossa
					player.pos.x += (player.pos.x-boss.pos.x)*0.2f;
					player.pos.y += (player.pos.y-boss.pos.y)*0.2f;
					player.pos.y -= player.radius;
					player.rotation += 180;
					
					while(circleCollision(boss.pos, player.getPos(), boss.radius, player.radius))
						player.pos.y -= player.radius*0.1f;
				}
			}
			//pociski bossa
			for(int i=0; i<boss.bullets.size(); i++) {
				boolean collide = false;
				Vector2f explosionCenter = null;
				if(boss.bullets.get(i).explode) {
					if(boss.bullets.get(i).state == 0)//dla kannonowego pocisku
						explosionCenter = new Vector2f(boss.bullets.get(i).pos.x, 1-boss.bullets.get(i).pos.y);
					collide = true;
				}
				//dla kazdego przeciwnika
				for(int j=0; j<enemies.size(); j++) {
					if(circleCollision(boss.bullets.get(i).pos, enemies.get(j).pos, boss.bullets.get(i).radius, enemies.get(j).radius)) {//jesli zostal trafiony
						if(boss.bullets.get(i).state == 0) {//dla kannonoweego pocisku
							explosionCenter = getCollisionCenter(boss.bullets.get(i).pos, enemies.get(j).pos, boss.bullets.get(i).radius, enemies.get(j).radius);
							enemies.get(j).hp -= Config.gameType != 2 ? Config.enemyShootPower : Config.enemyShootPower - 9.0f*(Config.difficult-1);//odejmowanie hp przeciwnikowi
							float tmpRot = player.rotation;
							Vector2f tmpPos = new Vector2f(player.pos);
							collisionCalculation(j, j, true, false, false);
							player.rotation = tmpRot;
							player.pos = tmpPos;
							collide = true;
						}
					}
				}
				//kolizja z graczem
				if(circleCollision(boss.bullets.get(i).pos, player.pos, boss.bullets.get(i).radius, player.radius)) {//jesli zostal trafiony
					explosionCenter = getCollisionCenter(boss.bullets.get(i).pos, player.pos, boss.bullets.get(i).radius, player.radius);
					collide = true;
					player.hp -= Config.hitStrength;
					allHpLost += Config.hitStrength;
					if(Config.enableSounds)
						hit.play(1.0f, Config.effectsVolume);
					if(Config.particles)
						explosions.add(new Explosion(0, getCollisionCenter(boss.bullets.get(i).pos, player.getPos(), boss.bullets.get(i).radius, player.radius), simpleParticle, new Bounds(gameFieldBounds.x + (gameFieldBounds.width - gameFieldBounds.height), gameFieldBounds.y, gameFieldBounds.height, gameFieldBounds.height)));//nowa eksplozja przy zderzeniu gracza z przeciwnikiem
				}
				if(!collide)
					boss.bullets.get(i).render(delta);
				else {
					if(explosionCenter != null && Config.particles) {
						if(boss.bullets.get(i).state == 0)
							explosions.add(new Explosion(1, explosionCenter, simpleParticle, boss.bullets.get(i).gridBounds));
					}
					boss.bullets.remove(i);
				}
			}
		}
		//renderowanie spawnerow
		for(int i=0; i<spawners.size(); i++) {
			if(spawners.get(i).lifeTime == 0)
				spawners.remove(i);
			else
				spawners.get(i).render(delta);
		}
		//renderowanie przeciwnikow
		for(int i=0; i<enemies.size(); i++) {
			//sprawdzanie kolizji
			for(int j=0; j<enemies.size(); j++) {
				if(i != j) {
					//jesli miedzy obiektami wystepuje kolizja
					if(circleCollision(enemies.get(i).getPos(), enemies.get(j).getPos(), enemies.get(i).radius, enemies.get(j).radius) && (enemies.get(i).lastEnemyCollison != j && enemies.get(j).lastEnemyCollison != i)) {
						//obliczanie zachowania obiektow po kolizji
						collisionCalculation(i,j, false, false, false);
						//robienie jednego dodatkowego ruchu by wyjsc z obszaru kolizji 
						enemies.get(i).resolvePos();
					}
				}
			}
			//wykrywanie kolizji z graczem
			if(circleCollision(enemies.get(i).getPos(), player.getPos(), enemies.get(i).radius, player.radius) && enemies.get(i).lastEnemyCollison != -1) {
				player.hp -= Config.hitStrength;
				allHpLost += Config.hitStrength;
				if(Config.enableSounds)
					hit.play(1.0f, Config.effectsVolume);
				if(Config.particles)
					explosions.add(new Explosion(0, getCollisionCenter(enemies.get(i).getPos(), player.getPos(), enemies.get(i).radius, player.radius), simpleParticle, new Bounds(gameFieldBounds.x + (gameFieldBounds.width - gameFieldBounds.height), gameFieldBounds.y, gameFieldBounds.height, gameFieldBounds.height)));//nowa eksplozja przy zderzeniu gracza z przeciwnikiem
				collisionCalculation(i,i, true, false, false);//obliczanie wektorow i pozycji po kolizji
				enemies.get(i).resolvePos();//przywracanie ostatniej niekolizyjnej pozycji
			}
			//wykrywanie kolizji z bossem
			if(Config.gameType == 2 && lvl==8) {//tylko na kampani na ostatnim poziomie
				if(circleCollision(enemies.get(i).getPos(), boss.pos, enemies.get(i).radius, boss.radius) && enemies.get(i).lastCollideTime > 0.1f) {
					//odskakiwanie przeciwnikow po kolizji z bossem
					enemies.get(i).pos.x += (enemies.get(i).pos.x-boss.pos.x)*0.2f;
					enemies.get(i).pos.y += (enemies.get(i).pos.y-boss.pos.y)*0.2f;
					enemies.get(i).rotation += 180;
					enemies.get(i).resolvePos();//przywracanie ostatniej niekolizyjnej pozycji
					while(circleCollision(boss.pos, enemies.get(i).getPos(), boss.radius, enemies.get(i).radius))
						enemies.get(i).pos.y -= enemies.get(i).radius*0.1f;
				}
			}
			if(enemies.get(i).hp <= 0) {//jesli przeciwnik stracil zycie
				//pojawia sie po nim wybuch
				if(Config.gameType == 0)
					tutorialComplete = true;
				enemiesDead++;//statystyka
				if(Config.particles)
					explosions.add(new Explosion(0, new Vector2f(enemies.get(i).pos.x, 1f-enemies.get(i).pos.y), simpleParticle, enemies.get(i).gridBounds));
				if(Config.enableSounds)
					explode.play(1.0f, Config.effectsVolume);//dzwiek eksplozji
				enemies.remove(i);//i zostaje usuniety
			}
			else
				enemies.get(i).render(delta);//renderowanie obiektu przeciwnika
		}
		//renderowanie pociskow gracza
		for(int i=0; i<player.bullets.size(); i++) {
			boolean collide = false;
			Vector2f explosionCenter = null;
			if(player.bullets.get(i).explode) {
				if(player.bullets.get(i).state == 0 || player.bullets.get(i).state == 2)//dla kannonowego pocisku i bomby
					explosionCenter = new Vector2f(player.bullets.get(i).pos.x, 1-player.bullets.get(i).pos.y);
				if(player.bullets.get(i).state == 2) {//dla bomby
					explode.play(1.0f, Config.effectsVolume);//dzwiek eksplozji
					//uderzanie wszystkiego w pewnym promieniu (sila zalezy od odleglosci)
					//dla kazdego przeciwnika
					for(int j=0; j<enemies.size(); j++) {
						if(getDistance(player.bullets.get(i).pos, enemies.get(j).pos) < Config.bombDmgRadius) {//jesli jest w zasiegu razenia bomby
							enemies.get(j).hp -= 25.0f + (Config.bombDmgRadius-getDistance(player.bullets.get(i).pos, enemies.get(j).pos))/Config.bombDmgRadius*100f;
						}
					}
					if(Config.gameType == 2 && lvl==8) {//tylko na kampani na ostatnim poziomie
						if(getDistance(player.bullets.get(i).pos, boss.pos) < Config.bombDmgRadius) {//jesli jest w zasiegu razenia bomby
							boss.hp -= 25.0f + (Config.bombDmgRadius-getDistance(player.bullets.get(i).pos, boss.pos))/Config.bombDmgRadius*100f;
						}
					}
				}
				collide = true;
			}
			if(player.bullets.get(i).state == 1) {//dla portalowego pocisku
				//dla kolizji z graczem
				if(player.lastCollideTime > 0.5f && circleCollision(player.bullets.get(i).pos, player.pos, player.bullets.get(i).radius, player.radius)) {//jesli zostal trafiony
					for(int k=0; k<player.bullets.size(); k++) {//dla kazdego innego pocisku portalgunowego
						if(i != k && player.bullets.get(k).state == 1) {//jesli ten drugi pocisk tez jest portalgunowy
							player.pos = new Vector2f(player.bullets.get(k).pos.x, player.bullets.get(k).pos.y);
							player.lastEnemyCollison = -1;
							player.lastCollideTime = 0.0f;
						}
					}
				}
				for(int j=0; j<player.bullets.size(); j++) {//dla kazdego innego pocisku
					if(i != j) {
						if(player.bullets.get(j).state == 1) {//portalgunowego
							if(circleCollision(player.bullets.get(i).pos, player.bullets.get(j).pos, player.bullets.get(i).radius, player.bullets.get(j).radius)) {
								player.bullets.get(i).rotation += 180;
								player.bullets.get(i).resolvePos();
								player.bullets.get(j).rotation += 180;
								player.bullets.get(j).resolvePos();
							}
						}
						else if(player.bullets.get(j).state == 0) {//i dla zwyklego pocisku
							if(player.bullets.get(j).lastCollideTime > 0.25f && circleCollision(player.bullets.get(i).pos, player.bullets.get(j).pos, player.bullets.get(i).radius, player.bullets.get(j).radius)) {
								for(int k=0; k<player.bullets.size(); k++) {//dla kazdego innego pocisku portalgunowego
									if(i != k && player.bullets.get(k).state == 1) {//jesli ten drugi pocisk jest portalgunowy
										player.bullets.get(j).pos = new Vector2f(player.bullets.get(k).pos.x, player.bullets.get(k).pos.y);
										player.bullets.get(j).lastEnemyCollison = -1;
										player.bullets.get(j).lastCollideTime = 0.0f;
									}
								}
							}
						}
					}
				}
			}
			//dla bossa
			if(Config.gameType == 2 && lvl==8) {//tylko na kampani na ostatnim poziomie
				if(circleCollision(player.bullets.get(i).pos, boss.pos, player.bullets.get(i).radius, boss.radius)) {//jesli zostal trafiony
					if(player.bullets.get(i).state == 0) {//kanonowy pocisk
						boss.hp -= 10;
						explosionCenter = getCollisionCenter(player.bullets.get(i).pos, boss.pos, player.bullets.get(i).radius, boss.radius);
						collide = true;
					}
					else if(player.bullets.get(i).state == 1) {//portalowy
						player.bullets.get(i).rotation -= 180;
					}
				}
			}
			//dla kazdego przeciwnika
			for(int j=0; j<enemies.size(); j++) {
				if(circleCollision(player.bullets.get(i).pos, enemies.get(j).pos, player.bullets.get(i).radius, enemies.get(j).radius)) {//jesli zostal trafiony
					if(player.bullets.get(i).state == 0) {//dla kannonoweego pocisku
						explosionCenter = getCollisionCenter(player.bullets.get(i).pos, enemies.get(j).pos, player.bullets.get(i).radius, enemies.get(j).radius);
						enemies.get(j).hp -= Config.gameType != 2 ? Config.enemyShootPower : Config.enemyShootPower - 9.0f*(Config.difficult-1);//odejmowanie hp przeciwnikowi
						collisionCalculation(j, i, false, true, false);
						collide = true;
					}
					else if(player.bullets.get(i).state == 1 && enemies.get(j).lastCollideTime > 0.5f) {//dla portalgunowego pocisku
						//szukanie innego portala
						for(int k=0; k<player.bullets.size(); k++) {//dla kazdego innego pocisku portalgunowego
							if(i != k && player.bullets.get(k).state == 1) {//jesli ten drugi pocisk tez jest portalgunowy
								enemies.get(j).pos = new Vector2f(player.bullets.get(k).pos.x, player.bullets.get(k).pos.y);
								enemies.get(j).lastEnemyCollison = -1;
					        	enemies.get(j).lastCollideTime = 0.0f;
							}
						}
					}
				}
			}
				
			if(!collide)
				player.bullets.get(i).render(delta);
			else {
				if(explosionCenter != null && Config.particles) {
					if(player.bullets.get(i).state == 0)
						explosions.add(new Explosion(1, explosionCenter, simpleParticle, player.bullets.get(i).gridBounds));
					if(player.bullets.get(i).state == 2)
						explosions.add(new Explosion(2, explosionCenter, simpleParticle, player.bullets.get(i).gridBounds));
					
				}
				player.bullets.remove(i);
			}
		}
		if(player.pos.x != player.pos.x || player.pos.y != player.pos.y) {//sprawdzanie czy pozycja gracza nie jest NaN
			player.pos = new Vector2f(0.5f, 0.5f);
		}
		player.render(delta);//renderowanie obiektu gracza
		//renderowanie explozji
		if(Config.particles) {
			for(int i=0; i<explosions.size(); i++) {
				if(explosions.get(i).explosionTime < Config.hitExplosionTime)
					explosions.get(i).render(delta);
				else
					explosions.remove(i);
			}
		}
		//renderowanie informacji o czasie trwania efektow
		for(int i=0; i<player.consequences.size(); i++) {
			if(player.consequences.get(i).posY >= i) {
				player.consequences.get(i).posY -= (player.consequences.get(i).posY-i)*delta*2;
			}
			player.consequences.get(i).render(effectsTextures[player.consequences.get(i).index], gameFieldBounds.x + (gameFieldBounds.width - gameFieldBounds.height), gameFieldBounds.y, gameFieldBounds.height, gameFieldBounds.height);
		}
		if(enemies.size() > 0) {
			for(int i=0; i<enemies.get(0).consequences.size(); i++) {//dla efektow pierwszego przeciwnika na liscie
				if(enemies.get(0).consequences.get(i).index == 0) {//dla spowolnienia
					if((int)enemies.get(0).consequences.get(i).posY < player.consequences.size()+i) {
						enemies.get(0).consequences.get(i).posY = player.consequences.size()+i;
					}
					if(enemies.get(0).consequences.get(i).posY >= player.consequences.size()+i) {
						enemies.get(0).consequences.get(i).posY -= (enemies.get(0).consequences.get(i).posY-player.consequences.size()+i)*delta*2;
					}
					enemies.get(0).consequences.get(i).render(effectsTextures[enemies.get(0).consequences.get(i).index], gameFieldBounds.x + (gameFieldBounds.width - gameFieldBounds.height), gameFieldBounds.y, gameFieldBounds.height, gameFieldBounds.height);
				}
			}
		}
		if(Config.particles && (lvl == 5 || lvl == 7))
			weather.render(delta);//renderowanie sniegu
		
		gui.render();//renderowanie graficznego interfejsu uzytkownika
	}
	//uaktualnia plik z danymi mapek
	private void saveRecord() {
		if(Config.gameType == 1)//survival
			Config.mapRecords[lvl-1] = (int)time;//uaktualnianie tablicy z rekordem
		else if (Config.gameType == 2)//kampania
			Config.campaingRecords[lvl-1][Config.difficult] = ocena;//uaktualnianie tablicy z rekordem
		//uchwyt do pliku
		File fileConf = new File("assets" + "/" + Config.mapFolder + "/" + Config.mapConfigFile);
		String mapsConfigText = "";//przechowuje zawartosc pliku tekstowego
		if(fileConf.exists()) {//jesli takowy istnieje
			try {
                mapsConfigText = Config.readFile(fileConf.getPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
		}
		String[] mapsConfig = mapsConfigText.split("\n");//podzial pliku tekstowego na linie
		String[] mapConf = mapsConfig[lvl-1].split(" ");
		if(Config.gameType == 1)//survival
			mapsConfig[lvl-1] = mapConf[0] + " " + Config.mapRecords[lvl-1] + " " + mapConf[2] + " " + mapConf[3] + " " + mapConf[4];
		else if (Config.gameType == 2)//kampania
			mapsConfig[lvl-1] = mapConf[0] + " " + mapConf[1] + " " + Config.campaingRecords[lvl-1][0] + " " + Config.campaingRecords[lvl-1][1] + " " + Config.campaingRecords[lvl-1][2];
		String newMapConfigText = "";
		for(int i=0; i<mapsConfig.length; i++)
			newMapConfigText += mapsConfig[i] + "\n";
		//zapisywanie zaktualizowanego textu do pliku
		if(fileConf.isFile() && fileConf.canWrite()) {//jesli plik ma prawa do zapisu i jest plikiem
			try {
				FileWriter writerFile = new FileWriter(fileConf.getAbsoluteFile(), false);//obiekt potrzebny do stworzenia zmiennej ponizej (drugi argument deprecjonuje to czy plik ma byc uprzednio wyczyszczony)
				BufferedWriter writer = new BufferedWriter(writerFile);//tworzenie obiektu ktory pozwala na zapis do pliku
				writer.write(newMapConfigText);//zapisywanie tekstu do pliku
				writer.close();//zamykanie obiektu
			} catch (IOException e) {
				e.printStackTrace();//ewentualnie wypisanie kodu bledu
			}
		}
		else {
			System.out.println("Blad podczas aktualizowania najlepszego czasu");//wypisanie informacji o bledzie
		}
	}
	//sprawdza kolizje dla jednego obiektu Effect
	public boolean effectCollide(Effect effect) {
		//kolizja dla playera
		if(circleCollision(effect.pos, player.pos, effect.radius, player.radius)) {
			//nadawanie efektu gdy gracz zbierze bonus
			if(setEffect(effect.index, 0, true))
				return true;
		}
		for(int i=0; i<enemies.size(); i++) {
			if(circleCollision(effect.pos, enemies.get(i).pos, effect.radius, enemies.get(i).radius)) {
				//nadawanie efektu gdy przeciwnik zbierze bonus
				if(setEffect(effect.index, i, false))
					return true;
			}
		}
		return false;
	}
	//wprowadza efekt do gry (zaleznie od jego indexu i obiektu ktory zebral bonus) i zwraca true jesli efekt zostal zaimplementowany pomyslnie
	private boolean setEffect(int index, int enemyIndex, boolean isPlayer) {
		switch(index) {
		case 0://spowalnianie wszystkich graczy
			if(isPlayer && enemies.size() > 0) {//tylko jesli gracz zbierze i jest na mapce przynajmniej jeden przeciwnik
				if(Config.enableSounds)
					collect.play(1.0f, Config.effectsVolume);
				for(Enemy enemy : enemies)
					enemy.addEffect(index);
				return true;
			}
			break;
		case 1://przyspieszenie
			if(isPlayer) {
				this.player.addEffect(index);
				if(Config.enableSounds)
					collect.play(1.0f, Config.effectsVolume);
			} else
				enemies.get(enemyIndex).addEffect(index);
			return true;
		case 2://apteczka
			if(isPlayer && player.hp != 100) {//tylko jesli gracz zbierze
				if(Config.enableSounds)
					collect.play(1.0f, Config.effectsVolume);
				player.hp += Config.kitPower;
				if(player.hp > 100)
					player.hp = 100;
				return true;
			}
			break;
		case 3://bron dla gracza
			if(isPlayer && !player.armed) {//tylko jesli gracz zbierze i efekt nie jest aktywny
				if(Config.enableSounds)
					collect.play(1.0f, Config.effectsVolume);
				player.addEffect(index);
				return true;
			}
			break;
		case 4://portal gun
			//liczenie ilosci portali
			int portals = 0;
			for(int i=0; i<player.bullets.size(); i++) {
				if(player.bullets.get(i).state == 1)
					portals++;
			}
			if(isPlayer && !player.armed && portals < 2) {//tylko jesli gracz zbierze, efekt nie jest aktywny i liczba portali nie jest maksymalna
				if(Config.enableSounds)
					collect.play(1.0f, Config.effectsVolume);
				player.addEffect(index);
				return true;
			}
			break;
		case 5://bombka
			if(isPlayer && !player.armed) {//jesli gracz zbierze i efekt nie jest aktywny
				if(Config.enableSounds)
					collect.play(1.0f, Config.effectsVolume);
				player.addEffect(index);
				return true;
			}
			break;
		}
		return false;
	}
	//procedura ktora wykrywa kolizje miedzy dwoma kolami
	private boolean circleCollision(Vector2f pos1, Vector2f pos2, float r1, float r2) {//parametry - srodek pierwszego i drugiego kola, promien pierwszego i drugiego
		//obliczanie odleglosci pomiedzy srodkami okregow (bez pierwiastkowania)
		double dst = Math.pow(pos1.x - pos2.x, 2) + Math.pow(pos1.y - pos2.y, 2);
		//porownanie odleglosci z kwadratem dlugosci srednicy
        if (dst <= Math.pow(r1+r2, 2))
	        return true;//jesli dystans miedzy srodkami okregow jest mniejszy od sumy promieni - zwraca true
        return false;
    }
	//procedura oblicza vektory kierunku dla obiektow po kolizji
    private void collisionCalculation(int ball1, int ball2, boolean isPlayer, boolean isBullet, boolean isBoss) {
    	//obliczanie predkosci kierunkowej po rotacji obiektow
    	float velocityX1 = (float)Math.sin(Math.toRadians(enemies.get(ball1).getRot()));
        float velocityY1 = (float)Math.cos(Math.toRadians(enemies.get(ball1).getRot()));
        float velocityX2 = (float)Math.sin(Math.toRadians(isPlayer ? player.getRot() : ( isBullet ? player.bullets.get(ball2).rotation : (isBoss ? boss.rotation : enemies.get(ball2).getRot()))));
    	float velocityY2 = (float)Math.cos(Math.toRadians(isPlayer ? player.getRot() : ( isBullet ? player.bullets.get(ball2).rotation : (isBoss ? boss.rotation : enemies.get(ball2).getRot()))));
        
        //odleglosc miedzy obiektami
        double dst = Math.sqrt(Math.pow((enemies.get(ball1).getPos().x - (isPlayer ? player.getPos().x : (isBullet ? player.bullets.get(ball2).pos.x : (isBoss ? boss.pos.x : enemies.get(ball2).getPos().x)))), 2) + Math.pow((enemies.get(ball1).getPos().y - (isPlayer ? player.getPos().y : (isBullet ? player.bullets.get(ball2).pos.y : (isBoss ? boss.pos.y : enemies.get(ball2).getPos().y)))), 2));
        double ex = (enemies.get(ball1).getPos().x - (isPlayer ? player.getPos().x : ( isBullet ? player.bullets.get(ball2).pos.x : (isBoss ? boss.pos.x : enemies.get(ball2).getPos().x)))) / dst;
        double ey = (enemies.get(ball1).getPos().y - (isPlayer ? player.getPos().y : ( isBullet ? player.bullets.get(ball2).pos.y : (isBoss ? boss.pos.y : enemies.get(ball2).getPos().y)))) / dst;

        //dalsze matematyczne procedury obliczajace kierunek dwoch obiektow po kolizji
        double eyMin = -ey;

        double e1x = (velocityX1 * ex + velocityY1 * ey) * ex;
        double e1y = (velocityX1 * ex + velocityY1 * ey) * ey;
        double e2x = (velocityX2 * ex + velocityY2 * ey) * ex;
        double e2y = (velocityX2 * ex + velocityY2 * ey) * ey;

        double o1x = (velocityX1 * eyMin + velocityY1 * ex) * eyMin;
        double o1y = (velocityX1 * eyMin + velocityY1 * ex) * ex;
        double o2x = (velocityX2 * eyMin + velocityY2 * ex) * eyMin;
        double o2y = (velocityX2 * eyMin + velocityY2 * ex) * ex;
        double vxs = (e1x + e2x) / 2f;
        double vys = (e1y + e2y) / 2f;
        //////////////////////////////////////////////////////////////////////////////

        //obliczanie wektorow kierunkowych dla pierwszego obiektu
        double vx1 = -e1x + 2 * vxs + o1x;
        double vy1 = -e1y + 2 * vys + o1y;
        //obliczanie wektorow kierunkowych dla drugiego obiektu
        double vx2 = -e2x + 2 * vxs + o2x;
        double vy2 = -e2y + 2 * vys + o2y;

        //zamiana wartosci wektorow na rotacje obiektu
        enemies.get(ball1).setRot((float) -Math.toDegrees(Math.atan2(vy1, vx1)) + 90);
        if(!isPlayer && !isBullet && !isBoss)
        	enemies.get(ball2).setRot((float) -Math.toDegrees(Math.atan2(vy2, vx2)) + 90);
        else if (isPlayer) {
        	player.setRot((float) -Math.toDegrees(Math.atan2(vy2, vx2)) + 90);
        }
        //przypisanie indexow ostatnio udezonych obiektow
        if(!isPlayer && !isBullet && !isBoss) {
        	enemies.get(ball1).setLastEnemyCollision(ball2);
        	enemies.get(ball2).setLastEnemyCollision(ball1);
        	enemies.get(ball1).lastCollideTime = 0.0f;
        	enemies.get(ball2).lastCollideTime = 0.0f;
        }
        else {
        	enemies.get(ball1).lastEnemyCollison = -1;
        	enemies.get(ball1).lastCollideTime = 0.0f;
        }
    }
    //zwraca srodek kolizji miedzy dwoma obiektami
    private Vector2f getCollisionCenter(Vector2f pos1, Vector2f pos2, float r1, float r2) {
    	float x = 0;//punkt x kolizji
    	float y = 0;//punnkt y kolizji
    	float x1;//posX + promien obiektu po lewo
    	float x2;//posX - promien obiketu po prawo
    	float y1;//posX + promien obiektu na dole
    	float y2;//posX - promien obiketu na gorze
    	if(pos1.x < pos2.x) {
    		x1 = pos1.x + r1;
    		x2 = pos2.x - r2;
    	}
    	else {
    		x1 = pos2.x + r2;
    		x2 = pos1.x - r1;
    	}
    	if(pos1.y < pos2.y) {
    		y1 = pos1.y + r1;
    		y2 = pos2.y - r2;
    	}
    	else {
    		y1 = pos2.y + r2;
    		y2 = pos1.y - r1;
    	}
    	x = (x1+x2) / 2f;
    	y = (y1+y2) / 2f;
    	return new Vector2f(x, 1-y);
    }
    //tworzy portal na losowej pozycji z ktorego wylania sie przeciwnik
    private void spawnEnemy() {
    	Random random = new Random();//obiekt potrzebny do generowania losowych liczb
    	float x;
    	float y;
    	do {
    		x = Config.spawnBorder + (1f-Config.spawnBorder*2f)*(random.nextInt(10000)/10000f);
        	y = Config.spawnBorder + (1f-Config.spawnBorder*2f)*(random.nextInt(10000)/10000f);
    	}
    	while(checkSpawnersCollision(x, y));
    	spawners.add(new EnemySpawner(this, x, y, spawnerTexture, simpleParticle, new Bounds(gameFieldBounds.x + (gameFieldBounds.width - gameFieldBounds.height), gameFieldBounds.y, gameFieldBounds.height, gameFieldBounds.height)));
    }
    //tworzy portal na ustalonej pozycji
    private void spawnEnemy(float x, float y) {
    	spawners.add(new EnemySpawner(this, x, y, spawnerTexture, simpleParticle, new Bounds(gameFieldBounds.x + (gameFieldBounds.width - gameFieldBounds.height), gameFieldBounds.y, gameFieldBounds.height, gameFieldBounds.height)));
    }
    //sprawdza czy wylosowana pozycja nie koliduje z innym spawnerem
    private Boolean checkSpawnersCollision(float x, float y) {
    	float doubleRadius = Config.enemySpawnerRadius*2f;//minimalna odleglosc miedzy spawnerami
    	//spradzanie kolizji dla kazdego ze spawnerow na mapie
    	for(int i=0; i<spawners.size(); i++) {
    		Vector2f pos1 = spawners.get(i).getPos();//zwraca pozycje dla danego spawneru
    		if(doubleRadius > Math.sqrt(Math.pow(x-pos1.x, 2) + Math.pow(y-pos1.y, 2)))//sprawdza odleglosc miedzy spawnerami
    			return true;//spawnery koliduja ze soba
    	}
    	//sprawdzanie kolizji z mapa
    	if(checkWallCollision(new Vector2f(x, y), Config.enemySpawnerRadius))
    		return true;
    	return false;//spawnery nie koliduja ze soba
    }
    //sprawdza czy wylosowana pozycja nie koliduje z innym efektem
    private Boolean checkEffectsCollision(float x, float y) {
    	float doubleRadius = Config.effectRadius*2f;//minimalna odleglosc miedzy efektami
    	for(int i=0; i<effects.size(); i++) {
    		Vector2f pos1 = effects.get(i).pos;//zwraca pozycje dla danego efektu
    		if(doubleRadius > Math.sqrt(Math.pow(x-pos1.x, 2) + Math.pow(y-pos1.y, 2)))//sprawdza odleglosc miedzy efektami
    			return true;//efekty koliduja ze soba
    	}
    	//sprawdzanie kolizji z mapa
    	if(checkWallCollision(new Vector2f(x, y), Config.effectRadius))
    		return true;
    	return false;//efekty nie koliduja
    }
    //dodaje przeciwnika do mapy na ustalonej pozycji
    public void addEnemy(float x, float y) {
    	enemies.add(new Enemy(x, y));
		enemies.get(enemies.size()-1).setTexture(enemyTexture);//ustawia texture dla przeciwnika
		enemies.get(enemies.size()-1).setDamageTexture(damage);//ustawia texture dla obrazen
		enemies.get(enemies.size()-1).setShadow(circleShadowTexture);
		enemies.get(enemies.size()-1).setBounds(gameFieldBounds.x + (gameFieldBounds.width - gameFieldBounds.height), gameFieldBounds.y, gameFieldBounds.height, gameFieldBounds.height);//definiuje obszar mapy
		enemies.get(enemies.size()-1).setCollisionLines(collisionLines);//przypisuje vektory kolizji dla scian mapy
    }
    //sprawdza kolizje typu kolo - mapa
    private boolean checkWallCollision(Vector2f pos, float radius) {
    	for(Triangle triangle : map.mapFaces) {//dla kazdego polygona z ktorych sklada sie mapa
    		for(int i=0; i<3; i++) {//da kazdego odcinka z jakiego sklada sie trojkat
    			Vector2f pA = triangle.getVector(i);
				Vector2f pB = triangle.getVector(i+1 < 3 ? i+1 : 0);
				//sprawdzanie kolizji z wierzcholkami
				if(getDistance(pA, pos) <= radius || getDistance(pB, pos) <= radius)
					return true;
				//a dalej z krawedziami
				//parametry funkcji liniowej:
				float A = (pA.y - pB.y) / (pA.x - pB.x);//parametr wspolczynnika a
				if(!isNumber(A))//korygowanie wartosci dla wynikow z dzielenia przez 0
					A = Config.calculationPrecisionFix-1;
				float B = -1;//parametr przy wspolczynniku y
				float C = pA.y - (A * pA.x);//wyraz wolny
				float distance = (float)(Math.abs(A * pos.x + B * pos.y + C) / Math.sqrt(A*A + B*B));
				if(distance <= radius) {
					float a2 = -(1.0f/A);//przeciwny i odwrotny wspolczynnik kierunkowy
					if(!isNumber(a2))
						a2 = Config.calculationPrecisionFix/2f;
					//y = a2*x + b2 	===>>	b2 = y - (a2*x);
					float b2 = pos.y - (a2 * pos.x);//podstawianie wspolzednych srodka kola by obliczyc drugi parametr funkcji
					//obliczanie wspolzednych punktu kolizji
					float kx = (b2 - C) / (A - a2);
					float ky = A * kx + C;// == a2 * kx + b2
					if(Math.min(pA.y, pB.y) < ky+radius*Config.distanceToCollisionLine && 
							Math.max(pA.y, pB.y) > ky-radius*Config.distanceToCollisionLine && 
							Math.min(pA.x, pB.x) < kx+radius*Config.distanceToCollisionLine && 
							Math.max(pA.x, pB.x) > kx-radius*Config.distanceToCollisionLine) {
						return true;
					}
				}
    		}
		}
    	return false;
    }
    //zwraca odleglosc miedzy dwoma punktami
  	private float getDistance(Vector2f a, Vector2f b) {
  		return (float)Math.sqrt((Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2)));
  	}
  	//sprawdzanie czy wartosc jest liczbowa
  	private boolean isNumber(float a) {
  		//zwraca true jesli kolejno: a nie jest NaN czyli a jest rowne a i a nie jest dodatnia nieskonczonoscia ani ujemna
  		return a==a && a != Float.POSITIVE_INFINITY && a!= Float.NEGATIVE_INFINITY;
  	}
    //konfiguruje renderowanie opengl
	private void setUpRender() {
		//konfiguracja renderowania figur opengl
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, Config.width, Config.height, 0, 0, 1);
		
		glEnable(GL_TEXTURE_2D);//wlaczenie textur
		//wlaczenie obslugi przezroczystosci
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
 		
		glEnable(GL_POINT_SMOOTH);
		glHint(GL_POINT_SMOOTH_HINT, GL_NICEST);
	}
	//oblicza obszar gry z proporcjami niezaleznymi od ekranu
	private Bounds calculateBounds() {
		float offsetX = 0;
		float offsetY = 0;
		float gameHeight = 0;
		if(Config.width > Config.height*Config.gameWidthRatio) {//pasy po lewo i prawo
			gameHeight = Config.height;
			offsetX = (Config.width - gameHeight*Config.gameWidthRatio) / 2f;
		}
		else {//pasy na gorze i dole
			gameHeight = Config.width / Config.gameWidthRatio;
			offsetY = (Config.height - gameHeight) / 2f;
		}
		return new Bounds(offsetX, offsetY, gameHeight*Config.gameWidthRatio, gameHeight);
	}
	public void clear() {
		//czyszczenie obiektu Game
	}
}
