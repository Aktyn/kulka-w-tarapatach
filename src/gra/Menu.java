package gra;

import static org.lwjgl.opengl.GL11.*;//importowanie calego OpenGL 11
import gui.*;//pobranie wszystkich klas GUI

import java.io.File;
import java.io.IOException;
import java.util.Random;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.opengl.Texture;

public class Menu {
	private Window callBack;//potrzebne do wywolania funkcji zamykajacej menu i rozpoczynajacej gre
	private GUI gui;//obiekt zawierajacy wszystkie elementy intefejsu
	
	private float viewX = 0, viewY = 0;//zmienne odpowiadajace za renderowany obszar (w procentach wysokosci i szerokosci ekranu)
	private float lastViewX = 0, lastViewY = 0;//potrzebne do algorytmu plynnej animacji przejscia (przechowuje ostatnia pozycje widoku)
	private boolean viewUpdate = false;//zmienna aktywujaca i dezaktywujaca algorytm plynnego ruchu obrazu
	
	private int selectedLvl = 1;//wybrany poziom do gry
	private float visibleLevel;//poziom przesuniecia listy map
	private int campaingSelectedLvl = 1;//wybrany poziom do gry
	private float campaingVisibleLevel;//poziom przesuniecia listy map
	
	private int tmpWidth, tmpHeight;//zmienne uzywane do zmiany rozdzielczosci podczas zmiany ustawiec
	
	private Texture[] backgrounds;//zmienne textury tla
	private int backgroundState = 0;//ktora textura najpierw
	private float timeToSwitch;
	//kontrolki GUI
	private Image background1;//tlo menu glownego
	private Image background2;//drugie menu glownego
	private Image logo;//obrazek loga gry
	private Label gameChoice;//napis pytajacy o tryb rozgrywki
	private Button exitBtn;//przycisk wylaczania gry (wlasciwie przelacza tylko na widok gdzie potwierdza sie wylaczenie aplikacji)
	private Label exitConfirmLabel;//napis z pytaniem o potwierdzenie wylaczenia
	private Button exitYes;//przycisk wylaczajacy gre
	private Button exitNo;//ten przycisk ustawia widok z powrotem na menu glowne
	private Button settings;//przycisk przelacza do widoku z ustawieniami
	private Button clearDataBtn;//przycisk przechodzacy do potwierdzenia czyszczenia postepu gry
	private Label clearDataInfo;//pytanie o potwierdzenie czyszczenia postepu w grze
	private Button clearDataYes;//czyszczenie postepu gry i powrot do widoku ustawien
	private Button clearDataNo;//wraca do widoku ustawien
	private Button settingsBack;//przycisk powrotu z widoku ustawien
	private Button helpBtn;//przelacza na widok pomocy i informacji o grze
	private Button helpBackBtn;//wraca z widoku pomocy
	private Button aboutAuthor;//przelacza na widok z informacjami o autorze gry
	private Button tutorialBtn;//przelacza na widok potwierdzajacy lub odrzucajacy rozpoczecie samouczka
	private Label tutorialInfo;//ostrzezenie o rozpoczeciu samouczka
	private Button tutorialYes;//rozpoczyna samouczek
	private Button tutorialNo;//wraca do poprzedniego widoku (pomoc)
	private Button aboutAuthorBack;//wraca do widoku z przyciskami pomocy
	private Image logoAuthor;//logo autora
	private Label authorInfo;//tekst z informacjami o autorze gry
	private Button playBtn;//przelacza na widok trybu rozgrywki
	private Button survival;//przelacza do wyboru mapy
	private Button campaing;//rozpoczyna kampanie
	private Button powrot;//przycisk powrotu do glownego widoku menu z widoku wyboru trybu rozgrywki
	private Button powrot2;//przycisk powrotu do widoku trybu rozgrywki z widoku wyboru mapy survival
	private Button powrot3;//przycisk powrotu do widoku trybu rozgrywki z widoku wyboru mapy kampani
	private Button startSurvival;//przycisk rozpoczyna tryb rozgrywki przetrwania
	private Button startCampaing;//przycisk rozpoczyna kampanie
	private Label[] mapNames;//pokazuje nazwy mapek
	private Label[] campaingMapNames;//pokazuje nazwy mapek dla kampani
	private Button previousMap;//wybiera poprzednia mape
	private Button nextMap;//wybiera nastepna mape
	private Button previousMap2;//wybiera poprzednia mape kampani
	private Button nextMap2;//wybiera nastepna mape kampani
	private Label fullScreenInfo;//belka z napisem pelny ekran
	private Button fullScreenSwitch;//przelacza miedzy pelnym ekranem a trybem w oknie
	private Image switchImage;//pokazuje ikonke przelaczania dla przyciskow przelaczajacych
	private Button saveSettings;//przycisk aplikujacy i zapisujacy ustawienia do pliku
	private Label ustawRozdzielczoscLb;//belka informujaca o zmienianiu rozdzielczosci
	private Button soundsSwitch;//wlacza i wylacza efekty dzwiekowe
	private Button shadowsSwitch;//adekwatnie jak wyzej dla cieni
	private Button particlesSwitch;//adekwatnie jak wyzej dla czasteczek
	private Image soundIcon;//ikonka dzwieku
	private Image cancelSound;//ikona przekreslenia na ikonce dzwieku
	private Image cancelShadows;//ikona przekreslenia na przycisk przelaczania cieni
	private Image cancelParticles;//ikona przekreslenia na przycisk przelaczania czasteczek
	private Button[] resolutionsBtns;//przyciski zmieniania rozdzielczosci
	private Image znacznikRes;//zaznacza wybrana rozdzielczosc
	private Button[] difficult;//trzy przyciski wyboru poziomu trudnosci
	private Image difficultMarker;//znacznik aktualnie wybranej trudnosci
	private Label difficultLabel;//belka z napisem o zmianie poziomu trudnosci
	
	public Menu(Window callBack) {
		this.callBack = callBack;
		
		tmpWidth = Config.width;
		tmpHeight = Config.height;
		
		setUpRender();
		
		gui = new GUI();
		
		//pobieranie potrzebnych assetow z AssetsManagera do zmiennych
		Texture logoTexture = callBack.assetsManager.getTexture("assets/Textures/logo.png");
		Texture logoAuthorTexture = callBack.assetsManager.getTexture("assets/Textures/GUI/logoAuthor.png");
		Texture znacznikTexture = callBack.assetsManager.getTexture("assets/Textures/GUI/znacznik.png");
		Texture[] buttonTextures = new Texture[]{
				callBack.assetsManager.getTexture("assets/Textures/GUI/buttonNorm.png"),
				callBack.assetsManager.getTexture("assets/Textures/GUI/buttonHover.png"),
				callBack.assetsManager.getTexture("assets/Textures/GUI/buttonClicked.png")
		};
		backgrounds = new Texture[]{
				callBack.assetsManager.getTexture("assets/Textures/GUI/background1.png"),
				callBack.assetsManager.getTexture("assets/Textures/GUI/background2.png"),
				callBack.assetsManager.getTexture("assets/Textures/GUI/background3.png")
		};
		Texture switchTexture = callBack.assetsManager.getTexture("assets/Textures/GUI/switchTexture.png");
		Texture sound = callBack.assetsManager.getTexture("assets/Textures/GUI/sound.png");
		Texture disable = callBack.assetsManager.getTexture("assets/Textures/GUI/disable.png");
		UnicodeFont menuFont = callBack.assetsManager.getFont("Poetsen");
		UnicodeFont smallFont = callBack.assetsManager.getFont("PoetsenSmall");
		UnicodeFont smallestFont = callBack.assetsManager.getFont("PoetsenSmallest");
		
		//tworzenie kontrolek gui
		background1 = new Image();
		background1.setBounds(0, 0, Config.width, Config.height);
		background2 = new Image();
		background2.setBounds(0, 0, Config.width, Config.height);
		
		backgroundState = new Random().nextInt(3);//losowanie jednego z trzech typow tla
		background1.setAlfa(1.0f);
		background1.setTexture(backgrounds[backgroundState]);
		background2.setTexture(backgrounds[backgroundState+1 < 3 ? backgroundState+1 : 0]);
		timeToSwitch = Config.backgroundsSwitchTime;
		
		logo = new Image();
		logo.setTexture(logoTexture);
		logo.setBounds(Config.width-Config.height*Config.logoImageSize, Config.height*0.4f, Config.height*Config.logoImageSize, Config.height*Config.logoImageSize);
		
		gameChoice = new Label();
		gameChoice.setBounds(0, Config.height*1.8f, Config.width, Config.height*0.2f);
		gameChoice.setFont(menuFont);
		gameChoice.setText("Jak chcesz grać?");
		
		exitConfirmLabel = new Label();
		exitConfirmLabel.setFont(menuFont);
		exitConfirmLabel.setBounds(0, -Config.height*0.2f, Config.width, Config.height*0.2f);
		exitConfirmLabel.setText("Wyłączyć grę?");
		
		exitBtn = new Button();
		exitBtn.setBounds(Config.width*0.1f, Config.height*0.0f, Config.width*0.8f, Config.height*0.2f);
		exitBtn.setFont(menuFont);
		exitBtn.setText("Wyjście");
		exitBtn.setTextures(buttonTextures);
		
		exitYes = new Button();
		exitYes.setBounds(Config.width*0.05f, -Config.height*0.9f, Config.width*0.4f, Config.height*0.25f);
		exitYes.setFont(menuFont);
		exitYes.setText("Tak");
		exitYes.setTextures(buttonTextures);
		
		exitNo = new Button();
		exitNo.setBounds(Config.width*0.55f, -Config.height*0.9f, Config.width*0.4f, Config.height*0.25f);
		exitNo.setFont(menuFont);
		exitNo.setText("Nie");
		exitNo.setTextures(buttonTextures);
		
		settings = new Button();
		settings.setBounds(Config.width*0.1f, Config.height*0.25f, Config.width*0.8f, Config.height*0.2f);
		settings.setFont(menuFont);
		settings.setText("Ustawienia");
		settings.setTextures(buttonTextures);
		
		clearDataBtn = new Button();
		clearDataBtn.setBounds(-Config.width, Config.height*0.04f, Config.width*0.3f, Config.height*0.12f);
		clearDataBtn.setFont(smallestFont);
		clearDataBtn.setTextAlign(0, 1);
		clearDataBtn.setText("Wyczyść\npostęp");
		clearDataBtn.setTextures(buttonTextures);
		
		clearDataInfo = new Label();
		clearDataInfo.setText("Twoje wyniki\nna każdej mapie\nzostaną wyczyszczone i\nutracisz dostęp do\nodblokowanych poziomów.\n");
		clearDataInfo.setBounds(-Config.width*2f, Config.height*0.3f, Config.width, Config.height*0.7f);
		clearDataInfo.setFont(smallFont);
		
		clearDataYes = new Button();
		clearDataYes.setText("Tak");
		clearDataYes.setBounds(-Config.width*1.95f, Config.height*0.05f, Config.width*0.4f, Config.height*0.25f);
		clearDataYes.setTextures(buttonTextures);
		clearDataYes.setFont(menuFont);
		
		clearDataNo = new Button();
		clearDataNo.setText("Nie");
		clearDataNo.setBounds(-Config.width*1.45f, Config.height*0.05f, Config.width*0.4f, Config.height*0.25f);
		clearDataNo.setTextures(buttonTextures);
		clearDataNo.setFont(menuFont);
		
		settingsBack = new Button();
		settingsBack.setBounds(-Config.height*0.2f, 0, Config.height*0.2f, Config.height*0.2f);
		settingsBack.setFont(menuFont);
		settingsBack.setText(">");
		settingsBack.setTextAlign(0, 1);
		settingsBack.setTextures(buttonTextures);
		
		helpBtn = new Button();
		helpBtn.setBounds(Config.width*0.1f, Config.height*0.5f, Config.width*0.8f, Config.height*0.2f);
		helpBtn.setFont(menuFont);
		helpBtn.setText("Pomoc");
		helpBtn.setTextures(buttonTextures);
		
		helpBackBtn = new Button();
		helpBackBtn.setBounds(Config.width, Config.height*0.0f, Config.height*0.2f, Config.height*0.2f);
		helpBackBtn.setFont(menuFont);
		helpBackBtn.setText("<");
		helpBackBtn.setTextures(buttonTextures);
		
		aboutAuthor = new Button();
		aboutAuthor.setBounds(Config.width*1.1f, Config.height*0.55f, Config.width*0.8f, Config.height*0.2f);
		aboutAuthor.setFont(menuFont);
		aboutAuthor.setText("O grze");
		aboutAuthor.setTextures(buttonTextures);
		
		tutorialBtn = new Button();
		tutorialBtn.setBounds(Config.width*1.1f, Config.height*0.30f, Config.width*0.8f, Config.height*0.2f);
		tutorialBtn.setFont(menuFont);
		tutorialBtn.setText("Samouczek");
		tutorialBtn.setTextures(buttonTextures);
		
		tutorialInfo = new Label();
		tutorialInfo.setFont(menuFont);
		tutorialInfo.setTextAlign(0, 0);
		tutorialInfo.setText("Czy na pewno\nchcesz rozpocząć\nsamouczek?");
		tutorialInfo.setBounds(Config.width*1.0f, -Config.height*0.75f, Config.width*1.0f, Config.height*1.0f);
		
		tutorialYes = new Button();
		tutorialYes.setBounds(Config.width*1.05f, -Config.height*0.95f, Config.width*0.4f, Config.height*0.2f);
		tutorialYes.setFont(menuFont);
		tutorialYes.setText("Tak");
		tutorialYes.setTextures(buttonTextures);
		
		tutorialNo = new Button();
		tutorialNo.setBounds(Config.width*1.55f, -Config.height*0.95f, Config.width*0.4f, Config.height*0.2f);
		tutorialNo.setFont(menuFont);
		tutorialNo.setText("Nie");
		tutorialNo.setTextures(buttonTextures);
		
		aboutAuthorBack = new Button();
		aboutAuthorBack.setBounds(Config.width*1.1f, Config.height*1.0f, Config.width*0.8f, Config.height*0.2f);
		aboutAuthorBack.setFont(menuFont);
		aboutAuthorBack.setText("Powrót");
		aboutAuthorBack.setTextures(buttonTextures);
		
		logoAuthor = new Image();
		logoAuthor.setTexture(logoAuthorTexture);
		logoAuthor.setBounds(Config.width*1.0f + (Config.width-Config.height*0.9f)/2f, Config.height*1.15f, Config.height*0.9f, Config.height*0.9f);
		
		authorInfo = new Label();
		authorInfo.setFont(smallestFont);
		authorInfo.setTextAlign(0, 0);
		authorInfo.setText("Autorem gry jest Radosław Krajewski.\n\nProgramy używane podczas tworzenia gry:\nEclipse, Gimp, Blender, Audacity.\n\nEfekty dźwiękowe zostały zaczerpnięte ze strony:\nfreesound.org");
		authorInfo.setBounds(Config.width*1.0f, Config.height*1.1f, Config.width*1.0f, Config.height*1.0f);
		
		playBtn = new Button();
		playBtn.setBounds(Config.width*0.1f, Config.height*0.75f, Config.width*0.8f, Config.height*0.2f);
		playBtn.setFont(menuFont);
		playBtn.setText("Graj");
		playBtn.setTextures(buttonTextures);
		
		survival = new Button();
		survival.setBounds(Config.width*0.1f, Config.height*1.55f, Config.width*0.8f, Config.height*0.2f);
		survival.setFont(menuFont);
		survival.setText("Przetrwanie");
		survival.setTextures(buttonTextures);
		
		startSurvival = new Button();
		startSurvival.setBounds(Config.width*0.1f, Config.height*2.8f, Config.width*0.8f, Config.height*0.2f);
		startSurvival.setFont(menuFont);
		startSurvival.setText("Start");
		startSurvival.setTextures(buttonTextures);
		
		startCampaing = new Button();
		startCampaing.setBounds(Config.width*0.1f, Config.height*3.8f, Config.width*0.8f, Config.height*0.2f);
		startCampaing.setFont(menuFont);
		startCampaing.setText("Start");
		startCampaing.setTextures(buttonTextures);
		
		difficult = new Button[3];
		for(int i=0; i<3; i++) {
			difficult[i] = new Button();
			difficult[i].setBounds(Config.height*0.2f + ((Config.width*1.0f-Config.height*0.4f) / 3f)*i, Config.height*3.25f, (Config.width*1.0f-Config.height*0.4f) / 3f, Config.height*0.1f);
			difficult[i].setFont(smallestFont);
			difficult[i].setTextures(buttonTextures);
			difficult[i].setText(Config.difficultNames[i]);
		}
		difficultLabel = new Label();
		difficultLabel.setFont(smallestFont);
		difficultLabel.setText("Ustaw poziom trudności");
		difficultLabel.setBounds(Config.height*0.2f, Config.height*3.35f, Config.width-Config.height*0.4f, Config.height*0.1f);
		
		difficultMarker = new Image();
		difficultMarker.setTexture(znacznikTexture);
		difficultMarker.setBounds(Config.height*0.2f + ((Config.width*1.0f-Config.height*0.4f) / 3f)*Config.difficult, Config.height*3.25f, (Config.width*1.0f-Config.height*0.4f) / 3f, Config.height*0.1f);
		
		campaing = new Button();
		campaing.setBounds(Config.width*0.1f, Config.height*1.3f, Config.width*0.8f, Config.height*0.2f);
		campaing.setFont(menuFont);
		campaing.setText("Kampania");
		campaing.setTextures(buttonTextures);
		
		powrot = new Button();
		powrot.setBounds(Config.width*0.1f, Config.height*1.05f, Config.width*0.8f, Config.height*0.2f);
		powrot.setFont(menuFont);
		powrot.setText("Wróć");
		powrot.setTextures(buttonTextures);
		
		powrot2 = new Button();
		powrot2.setBounds(Config.width*0.1f, Config.height*2.0f, Config.width*0.8f, Config.height*0.2f);
		powrot2.setFont(menuFont);
		powrot2.setText("Wróć");
		powrot2.setTextures(buttonTextures);
		
		powrot3 = new Button();
		powrot3.setBounds(Config.width*0.1f, Config.height*3.0f, Config.width*0.8f, Config.height*0.2f);
		powrot3.setFont(menuFont);
		powrot3.setText("Wróć");
		powrot3.setTextures(buttonTextures);
		
		fullScreenInfo = new Label();
		fullScreenInfo.setBounds(-Config.width, Config.height*0.8f, Config.width, Config.height*0.2f);
		fullScreenInfo.setFont(smallFont);
		fullScreenInfo.setTextAlign(1);
		if(Config.fullscreen)
    		fullScreenInfo.setText("Widok: pełny ekran");
    	else
    		fullScreenInfo.setText("Widok: w oknie");
		
		fullScreenSwitch = new Button();
		fullScreenSwitch.setBounds(-Config.height*0.2f, Config.height*0.8f, Config.height*0.2f, Config.height*0.2f);
		fullScreenSwitch.setTextures(buttonTextures);
		
		switchImage = new Image();
		switchImage.setTexture(switchTexture);
		switchImage.setBounds(-Config.height*0.2f, Config.height*0.8f, Config.height*0.2f, Config.height*0.2f);
		
		ustawRozdzielczoscLb = new Label();
		ustawRozdzielczoscLb.setBounds(-Config.width, Config.height*0.6f, Config.width, Config.height*0.2f);
		ustawRozdzielczoscLb.setFont(smallFont);
		ustawRozdzielczoscLb.setText("Wybierz rozdzielczość");
		
		resolutionsBtns = new Button[Config.resolutions.length];//tworzy tyle przyciskow ile jest dostepnych rozdzielczosci
		float btnSize = 1 / (float)resolutionsBtns.length;//zeby przyciski sie zmiescily, trzeba jakos podzielic szerokosc
		for(int i=0; i<resolutionsBtns.length; i++) {//tworzenie poszczegolnych przyciskow
			resolutionsBtns[i] = new Button();
			resolutionsBtns[i].setBounds(-Config.width+Config.width*btnSize*i, Config.height*0.4f, Config.width*btnSize, Config.height*0.2f);
			resolutionsBtns[i].setFont(smallestFont);
			resolutionsBtns[i].setText(Config.resolutions[i]);
			resolutionsBtns[i].setTextures(buttonTextures);
		}
		znacznikRes = new Image();
		znacznikRes.setTexture(znacznikTexture);
		znacznikRes.setBounds(-Config.width+Config.width*btnSize*Config.resolution/*Config.resolution to numer przycisku*/, Config.height*0.4f, Config.width*btnSize, Config.height*0.2f);
		
		soundsSwitch = new Button();
		soundsSwitch.setBounds(-Config.width*0.6f, Config.height*0.02f, Config.height*0.16f, Config.height*0.16f);
		soundsSwitch.setTextures(buttonTextures);
		
		shadowsSwitch = new Button();
		shadowsSwitch.setFont(smallestFont);
		shadowsSwitch.setText("Cienie");
		shadowsSwitch.setBounds(-Config.width*0.6f + Config.height*0.18f, Config.height*0.02f, Config.height*0.16f, Config.height*0.16f);
		shadowsSwitch.setTextures(buttonTextures);
		
		particlesSwitch = new Button();
		particlesSwitch.setFont(smallestFont);
		particlesSwitch.setText("Czą\nste\nczki");
		particlesSwitch.setBounds(-Config.width*0.6f + Config.height*0.36f, Config.height*0.02f, Config.height*0.16f, Config.height*0.16f);
		particlesSwitch.setTextures(buttonTextures);
		
		soundIcon = new Image();
		soundIcon.setBounds(-Config.width*0.6f, Config.height*0.02f, Config.height*0.16f, Config.height*0.16f);
		soundIcon.setTexture(sound);
		
		cancelShadows = new Image();
		cancelShadows.setTexture(disable);
		
		cancelParticles = new Image();
		cancelParticles.setTexture(disable);
		
		cancelSound = new Image();
		cancelSound.setTexture(disable);
		
		if(Config.shadows)
    		cancelShadows.setBounds(0, 0, 0, 0);
    	else
    		cancelShadows.setBounds(-Config.width*0.6f + Config.height*0.18f, Config.height*0.02f, Config.height*0.16f, Config.height*0.16f);
		if(Config.particles)
    		cancelParticles.setBounds(0, 0, 0, 0);
    	else
    		cancelParticles.setBounds(-Config.width*0.6f + Config.height*0.36f, Config.height*0.02f, Config.height*0.16f, Config.height*0.16f);
		
		if(Config.enableSounds)
    		cancelSound.setBounds(0, 0, 0, 0);
    	else
    		cancelSound.setBounds(-Config.width*0.6f, Config.height*0.02f, Config.height*0.16f, Config.height*0.16f);
		
		saveSettings = new Button();
		saveSettings.setBounds(-Config.width, Config.height*0.2f, Config.width, Config.height*0.2f);
		saveSettings.setFont(smallFont);
		saveSettings.setText("Zastosuj");
		saveSettings.setTextures(buttonTextures);
		
		String[][] mapsRecords = getLevels(true);//pobiera nazwy map i najlepsze czasy lub napis zablokowany z plikow
		mapNames = new Label[mapsRecords[0].length];
		for(int i=0; i<mapNames.length; i++) {//dla kazdej mapy
			mapNames[i] = new Label();
			mapNames[i].setBounds(Config.width*i, Config.height*2.2f, Config.width, Config.height*0.6f);
			mapNames[i].setFont(smallFont);
			mapNames[i].setTextAlign(0, 1);
			mapNames[i].setText(mapsRecords[0][i]);
		}
		campaingMapNames = new Label[mapsRecords[1].length];
		for(int i=0; i<campaingMapNames.length; i++) {//dla kazdej mapy
			campaingMapNames[i] = new Label();
			campaingMapNames[i].setBounds(Config.width*i, Config.height*3.2f, Config.width, Config.height*0.6f);
			campaingMapNames[i].setFont(smallFont);
			campaingMapNames[i].setTextAlign(0, 1);
			campaingMapNames[i].setText(mapsRecords[1][i]);
		}
		
		previousMap = new Button();
		previousMap.setBounds(Config.width*0.0f, Config.height*2.25f, Config.height*0.2f, Config.height*0.2f);
		previousMap.setFont(menuFont);
		previousMap.setText("<");
		previousMap.setTextures(buttonTextures);
		
		nextMap = new Button();
		nextMap.setBounds(Config.width*1.0f - Config.height*0.2f, Config.height*2.25f, Config.height*0.2f, Config.height*0.2f);
		nextMap.setFont(menuFont);
		nextMap.setText(">");
		nextMap.setTextures(buttonTextures);
		
		previousMap2 = new Button();
		previousMap2.setBounds(Config.width*0.0f, Config.height*3.25f, Config.height*0.2f, Config.height*0.2f);
		previousMap2.setFont(menuFont);
		previousMap2.setText("<");
		previousMap2.setTextures(buttonTextures);
		
		nextMap2 = new Button();
		nextMap2.setBounds(Config.width*1.0f - Config.height*0.2f, Config.height*3.25f, Config.height*0.2f, Config.height*0.2f);
		nextMap2.setFont(menuFont);
		nextMap2.setText(">");
		nextMap2.setTextures(buttonTextures);
		
		//dodanie kontrolek do obiektu gui (wyswietlane w kolejnosci dodania)
		gui.addPart(background2);
		gui.addPart(background1);
		gui.addPart(gameChoice);
		gui.addPart(exitBtn);
		gui.addPart(exitConfirmLabel);
		gui.addPart(exitYes);
		gui.addPart(exitNo);
		gui.addPart(settings);
		gui.addPart(clearDataBtn);
		gui.addPart(clearDataInfo);
		gui.addPart(clearDataYes);
		gui.addPart(clearDataNo);
		gui.addPart(settingsBack);
		gui.addPart(helpBtn);
		gui.addPart(aboutAuthor);
		gui.addPart(tutorialBtn);
		gui.addPart(tutorialInfo);
		gui.addPart(tutorialYes);
		gui.addPart(tutorialNo);
		gui.addPart(logoAuthor);
		gui.addPart(authorInfo);
		gui.addPart(aboutAuthorBack);
		gui.addPart(playBtn);
		gui.addPart(helpBackBtn);
		gui.addPart(survival);
		gui.addPart(startSurvival);
		gui.addPart(startCampaing);
		gui.addPart(campaing);
		gui.addPart(powrot);
		gui.addPart(powrot2);
		gui.addPart(powrot3);
		gui.addPart(previousMap);
		gui.addPart(nextMap);
		gui.addPart(previousMap2);
		gui.addPart(nextMap2);
		gui.addPart(fullScreenInfo);
		gui.addPart(fullScreenSwitch);
		gui.addPart(switchImage);
		gui.addPart(saveSettings);
		gui.addPart(ustawRozdzielczoscLb);
		gui.addPart(shadowsSwitch);
		gui.addPart(soundsSwitch);
		gui.addPart(particlesSwitch);
		gui.addPart(soundIcon);
		gui.addPart(cancelShadows);
		gui.addPart(cancelSound);
		gui.addPart(cancelParticles);
		for(Label map : mapNames)
			gui.addPart(map);
		for(Label map : campaingMapNames)
			gui.addPart(map);
		for(Button resButton : resolutionsBtns)
			gui.addPart(resButton);
		for(Button diff : difficult)
			gui.addPart(diff);
		gui.addPart(difficultMarker);
		gui.addPart(difficultLabel);
		gui.addPart(znacznikRes);
		
		gui.addPart(logo);
		
		setListeners();//dodawanie obslugi dla niektorych kontrolek
		
		//nadawanie dzwieku klikniecia dla przyciskow ktore mozna kliknac
		gui.setClickEffect(callBack.assetsManager.getSound("assets/Sounds/click.ogg"));
	}
	//funkcja dodaje intefejsy nasluchiwania do kontrolek ktore na to pozwalaja
	private void setListeners() {
		exitBtn.setButtonListener(new ButtonListener(){
            public void clicked(){
            	//przy kliknieciu przycisku exitBtn
            	setViewPos(0, -1);//zmiana widoku
            }
        });
		exitYes.setButtonListener(new ButtonListener(){
            public void clicked(){
            	//zamykanie aplikacji
            	System.exit(0);
            }
        });
		exitNo.setButtonListener(new ButtonListener(){
            public void clicked(){
            	//wracanie do widoku menu glownego
            	setViewPos(0, 0);
            }
        });
		settings.setButtonListener(new ButtonListener(){
            public void clicked(){
            	//przeskok na widok z ustawieniami
            	setViewPos(1, 0);
            }
        });
		settingsBack.setButtonListener(new ButtonListener(){
            public void clicked(){
            	//wracanie do widoku menu glownego
            	setViewPos(0, 0);
            	
            	/*******************************/
            	//uaktywnia funkcje ktora zastosowywuje i zapisuje ustawienia
            	boolean reload = !(Config.width == tmpWidth && Config.height == tmpHeight);//zmienna sprawdzajaca czy zostala zmieniona rozdzielczosc w ustawieniach
            	Config.width = tmpWidth;
            	Config.height = tmpHeight;
            	
            	Config.updateDisplay();//trzeba wygenerowac czcionki z nowymi parametrami
        		if(reload)//jesli rozdzielczosc zostala zmieniona
        			callBack.reloadMenu();
            	Config.save();//zapisywanie ustawien
            }
        });
		clearDataBtn.setButtonListener(new ButtonListener(){
            public void clicked(){
            	//widok w ktorym uzytkownik potwierdza lub nie czyszczenie postepu w grze
            	setViewPos(2, 0);
            }
        });
		clearDataYes.setButtonListener(new ButtonListener(){
            public void clicked(){
            	//czysci dane mapek i wraca do
            	Config.clearData();
            	callBack.reloadMenu();
            }
        });
		clearDataNo.setButtonListener(new ButtonListener(){
            public void clicked(){
            	//wraca do widoku z ustawieniami
            	setViewPos(1, 0);
            }
        });
		helpBtn.setButtonListener(new ButtonListener(){
            public void clicked(){
            	//wracanie do widoku pomocy
            	setViewPos(-1, 0);
            }
        });
		aboutAuthor.setButtonListener(new ButtonListener(){
            public void clicked(){
            	//przelacza do widoku z informacjami o autorze
            	setViewPos(-1, 1);
            }
        });
		tutorialBtn.setButtonListener(new ButtonListener(){
            public void clicked(){
            	//przelacza do widoku z rozpoczeciem lub odrzuceniem rozpoczecia samouczka
            	setViewPos(-1, -1);
            }
        });
		tutorialNo.setButtonListener(new ButtonListener(){
            public void clicked(){
            	//wraca do widoku pomocy
            	setViewPos(-1, 0);
            }
        });
		aboutAuthorBack.setButtonListener(new ButtonListener(){
            public void clicked(){
            	//wraca do widoku z przyciskami pomocy
            	setViewPos(-1, 0);
            }
        });
		helpBackBtn.setButtonListener(new ButtonListener(){
            public void clicked(){
            	//wracanie do widoku menu glownego
            	setViewPos(0, 0);
            }
        });
		playBtn.setButtonListener(new ButtonListener(){
            public void clicked(){
            	//przeskok na widok wybierania trybu rozgrywki
            	setViewPos(0, 1);
            }
        });
		powrot.setButtonListener(new ButtonListener(){
            public void clicked(){
            	//wracanie do widoku menu glownego
            	setViewPos(0, 0);
            }
        });
		soundsSwitch.setButtonListener(new ButtonListener(){
            public void clicked(){
            	//wlacza / wylacza efekty dzwiekowe
            	Config.enableSounds = !Config.enableSounds;
            	if(Config.enableSounds)
            		cancelSound.setBounds(0, 0, 0, 0);
            	else
            		cancelSound.setBounds(-Config.width*0.6f, Config.height*0.02f, Config.height*0.16f, Config.height*0.16f);
            }
        });
		shadowsSwitch.setButtonListener(new ButtonListener(){
            public void clicked(){
            	//wlacza / wylacza cienie
            	Config.shadows = !Config.shadows;
            	if(Config.shadows)
            		cancelShadows.setBounds(0, 0, 0, 0);
            	else 
            		cancelShadows.setBounds(-Config.width*0.6f + Config.height*0.18f, Config.height*0.02f, Config.height*0.16f, Config.height*0.16f);
            }
        });
		particlesSwitch.setButtonListener(new ButtonListener(){
            public void clicked(){
            	//wlacza / wylacza czasteczki
            	Config.particles = !Config.particles;
            	if(Config.particles)
            		cancelParticles.setBounds(0, 0, 0, 0);
            	else
            		cancelParticles.setBounds(-Config.width*0.6f + Config.height*0.36f, Config.height*0.02f, Config.height*0.16f, Config.height*0.16f);
            }
        });
		survival.setButtonListener(new ButtonListener(){
            public void clicked(){
            	//pokazuje widok wyboru mapek
            	setViewPos(0, 2);
            }
        });
		startSurvival.setButtonListener(new ButtonListener(){
            public void clicked(){
            	//rozpoczynanie gry w trybie survival
            	if(selectedLvl == 1 || Config.mapRecords[selectedLvl-2] != 0) {//jesli poziom nie jest zablokowany
	            	Config.survivalLvl = selectedLvl;
	            	callBack.startGame(1);
            	}
            }
        });
		startCampaing.setButtonListener(new ButtonListener(){
            public void clicked(){
            	//rozpoczynanie gry w trybie survival
            	if(campaingSelectedLvl == 1 || Config.campaingRecords[campaingSelectedLvl-2][0] != 0 || Config.campaingRecords[campaingSelectedLvl-2][1] != 0 || Config.campaingRecords[campaingSelectedLvl-2][2] != 0) {//jesli poziom nie jest zablokowany
	            	Config.campainglLvl = campaingSelectedLvl;
	            	callBack.startGame(2);
            	}
            }
        });
		tutorialYes.setButtonListener(new ButtonListener(){
            public void clicked(){
            	//rozpoczyna samouczek
            	callBack.startGame(0);
            }
        });
		campaing.setButtonListener(new ButtonListener(){
            public void clicked(){
            	//przelacza do widoku wyboru poziomu kampani
            	setViewPos(0, 3);
            }
        });
		powrot2.setButtonListener(new ButtonListener(){
            public void clicked(){
            	//wracanie do widoku wyboru rozgrywki
            	setViewPos(0, 1);
            }
        });
		powrot3.setButtonListener(new ButtonListener(){
            public void clicked(){
            	//wracanie do widoku wyboru rozgrywki
            	setViewPos(0, 1);
            }
        });
		previousMap.setButtonListener(new ButtonListener(){
            public void clicked(){
            	//wybor poprzedniej mapki
            	if(selectedLvl > 1)
            		selectedLvl--;
            }
        });
		nextMap.setButtonListener(new ButtonListener(){
            public void clicked(){
            	//wybor nastepnej mapki
            	if(selectedLvl < mapNames.length)
            		selectedLvl++;
            }
        });
		previousMap2.setButtonListener(new ButtonListener(){
            public void clicked(){
            	//wybor poprzedniej mapki kampani
            	if(campaingSelectedLvl > 1)
            		campaingSelectedLvl--;
            }
        });
		nextMap2.setButtonListener(new ButtonListener(){
            public void clicked(){
            	//wybor nastepnej mapki kampani
            	if(campaingSelectedLvl < campaingMapNames.length)
            		campaingSelectedLvl++;
            }
        });
		fullScreenSwitch.setButtonListener(new ButtonListener(){
            public void clicked(){
            	//przelacza miedzy pelnym ekranem a trybem w oknie
            	Config.fullscreen = !Config.fullscreen;
            	if(Config.fullscreen)
            		fullScreenInfo.setText("Widok: pełny ekran");
            	else
            		fullScreenInfo.setText("Widok: w oknie");
            }
        });
		saveSettings.setButtonListener(new ButtonListener(){
            public void clicked(){
            	//uaktywnia funkcje ktora zastosowywuje i zapisuje ustawienia
            	boolean reload = !(Config.width == tmpWidth && Config.height == tmpHeight);//zmienna sprawdzajaca czy zostala zmieniona rozdzielczosc w ustawieniach
            	Config.width = tmpWidth;
            	Config.height = tmpHeight;
            	
            	Config.updateDisplay();//trzeba wygenerowac czcionki z nowymi parametrami
        		if(reload)//jesli rozdzielczosc zostala zmieniona
        			callBack.reloadMenu();
        		Config.save();//zapisywanie ustawien
            }
        });
		for(final Button diff : difficult) {
			diff.setButtonListener(new ButtonListener(){
	            public void clicked(){
	            	//pozwala na zmiane poziomu trudnosci
	            	for(int i=0; i<3; i++) {//dla kazdego poziomu trudnosci
	            		if(Config.difficultNames[i].equals(diff.getText()) && i != Config.difficult) {//jesli nazwa sie zgadza
	            			Config.difficult = i;//przypisanie poziomu trudnosci
	            			//ustawianie markera na odpowiedniej pozycji
	            			difficultMarker.setBounds(Config.height*0.2f + ((Config.width*1.0f-Config.height*0.4f) / 3f)*Config.difficult, Config.height*3.25f, (Config.width*1.0f-Config.height*0.4f) / 3f, Config.height*0.1f);
	            			
	            			//zmiana rekordow wyswietlanych przy wyborze mapy kampani
	            			String[][] mapsRecords = getLevels(false);//pobiera nazwy map i najlepsze czasy lub napis zablokowany z plikow
	            			for(int j=0; j<mapNames.length; j++)//dla kazdej mapy
	            				mapNames[j].setText(mapsRecords[0][j]);
	            			for(int j=0; j<campaingMapNames.length; j++)//dla kazdej mapy
	            				campaingMapNames[j].setText(mapsRecords[1][j]);
	            		}
	            	}
	            }
	        });
		}
		for(final Button resButton : resolutionsBtns) {
			resButton.setButtonListener(new ButtonListener(){
	            public void clicked(){
	            	//zmienia rozdzielczosc w ustawieniach
	            	String[] values = resButton.getLabel().getText().split("\nx\n");//przypisuje do tablicy dwa stringi ktore zawieraja szerokosc i wysokosc rozdzielczosci
	            	if(values.length == 2) {//jesli wszystko idzie zgodnie z planem
	            		try {
	            			int width = Integer.valueOf(values[0]);
		            		int height = Integer.valueOf(values[1]);
		            		if(width != Character.MIN_RADIX && height != Character.MIN_RADIX) {//jesli wszyystko poszlo z godnie z planem
		            			tmpWidth = width;
		            			tmpHeight = height;
		            			//i przestawienie znacznika
		            			float btnSize = 1 / (float)resolutionsBtns.length;//zeby przyciski sie zmiescily, trzeba jakos podzielic szerokosc
		            			int index = getResIndex(resButton.getLabel().getText());
		            			Config.resolution = index;//ustawia index wybranej rozdzielczosci
		            			znacznikRes.setBounds(-Config.width+Config.width*btnSize*Config.resolution/*0 to numer przycisku*/, Config.height*0.4f, Config.width*btnSize, Config.height*0.2f);
		            		}
	            		}
	            		catch(NumberFormatException e) {
	            			e.printStackTrace();
	            		}
	            	}
	            }
	        });
		}
	}
	//zwraca index przycisku zmiany rozdzielczosci z dana zawartoscia
	private int getResIndex(String  text) {
		for(int i=0; i<resolutionsBtns.length; i++)//dla kazdego przycisku rozdzielczosci
			if(resolutionsBtns[i].getLabel().getText().equals(text))//jesli zawartosc tekstowa sie zgadza
				return i;//zwraca index
		return 0;
	}
	//pobiera z plikow nazwy lvli i zwraca tablice stringow
	private String[][] getLevels(boolean setDefault) {
		int lvlCount = 0;//liczba plikow z mapami w folderze z mapami
		int index = 1;//numer aktualnie sprawdzanego pliku z lvl
		while(index != 0) {
			//tworzenie uchwytu do pliku
			File f = new File("assets" + "/" + Config.mapFolder + "/" + Config.mapPrefix + index + Config.mapExtension);
			if (f.exists() && f.isFile()) {//jesli plik istnieje i jest plikiem
				lvlCount++;//liczba plikow z poziomami rosnie
				index++;
			}
			else
				index = 0;//konczenie petli sprawdzajacej
		}
		if(setDefault) {
			selectedLvl = lvlCount;
			campaingSelectedLvl = lvlCount;
		}
		//CZYTANIE PLIKU Z NAZWAMI I REKORDAMI MAP
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
		//tworzenie tablicy z nazwami map
		String[][] mapNames = new String[2][];
		mapNames[0] = new String[lvlCount];
		mapNames[1] = new String[lvlCount];
		Config.mapRecords = new int[lvlCount];//tworzenie tablicy o rozmiarze rownym ilosci mapek
		Config.mapNames = new String[lvlCount];//tworzenie tablicy z nazwami map
		boolean locked = false;//deprecyzuje czy ma sie pojawic napis "zablokowany"
		boolean campaingLocked = false;//deprecyzuje czy ma sie pojawic napis "zablokowany" w kampani
		Config.campaingRecords = new int[lvlCount][];//dla kazdej mapy
		for(int j=0; j<lvlCount; j++)
			Config.campaingRecords[j] = new int[3];//i dla kazdego poziomu trudnosci
		for(int i=0; i < lvlCount; i++) {//dla kazdej mapy
			Config.mapRecords[i] = 0;//zerowanie rekordu dla tej mapy
			String[] singleMap = mapsConfig[i].split(" ");//oddzielanie nazwy od rekordu (rekord w sekundach)
			if(singleMap.length > 0) {//jesli linia nie byla pusta
				String mapName = singleMap[0].replaceAll("_", " ");
				mapNames[0][i] = mapName;//przypisywanie nazwy mapy
				mapNames[1][i] = mapName;//to samo jak wyzej
				Config.mapNames[i] = mapName;
				mapNames[0][i] += "\n";//przejscie do nastepnego wiersza
				mapNames[1][i] += "\n";//przejscie do nastepnego wiersza
				if(singleMap.length > 1) {
					int seconds = Integer.valueOf(singleMap[1]);//rekord mapy w trybie przetrwania (w sekundach)
					int minutes = 0;
					Config.mapRecords[i] = seconds;//ustawianie rekordu dla tej mapy
					while(seconds >= 60) {
						seconds -= 60;
						minutes++;
					}
					if(locked)
						mapNames[0][i] += "Dostęp zablokowany";
					else if (minutes != 0 || seconds != 0)
						mapNames[0][i] += "Najlepszy wynik" + "\n" + (minutes != 0 ? (minutes + " minut i ") : "") + seconds + " sekund";//najlepszy czas
					if(Integer.valueOf(singleMap[1]) == 0) {
						if(!locked && setDefault)
							selectedLvl = i+1;//ustawianie znacznika wyboru mapy survival na ostatniej niezablokowanej
						locked = true;
					}
					//dla kampani
					if(singleMap.length == 5) {//jesli zawiera ilosc gwiazdek mapy kampani
						Config.campaingRecords[i][0] = Integer.valueOf(singleMap[2]);//poziom latwy
						Config.campaingRecords[i][1] = Integer.valueOf(singleMap[3]);//poziom normalny
						Config.campaingRecords[i][2] = Integer.valueOf(singleMap[4]);//poziom trudny
						switch(Config.difficult) {//wyswietlanie rekordu zaleznie od poziomu trudnosci
						case 0:
							if(Config.campaingRecords[i][0] != 0)
								mapNames[1][i] += "Ocena: " + Config.campaingRecords[i][0] + " / 3\n";
							break;
						case 1:
							if(Config.campaingRecords[i][1] != 0)
								mapNames[1][i] += "Ocena: " + Config.campaingRecords[i][1] + " / 3\n";
							break;
						case 2:
							if(Config.campaingRecords[i][2] != 0)
								mapNames[1][i] += "Ocena: " + Config.campaingRecords[i][2] + " / 3\n";
							break;
						}
						if(campaingLocked)
							mapNames[1][i] += "Zablokowany";
						//jesli ktorys z poziomow nie byl grany
						if(Config.campaingRecords[i][0] == 0 && Config.campaingRecords[i][1] == 0 && Config.campaingRecords[i][2] == 0) {
							if(!campaingLocked && setDefault)
								campaingSelectedLvl = i+1;//ustawianie znacznika wyboru mapy kampani na ostatniej niezablokowanej
							campaingLocked = true;//blokowanie nastepnych
						}
					}
					else {//przypisywanie zera dla rekordow
						Config.campaingRecords[i][0] = 0;
						Config.campaingRecords[i][1] = 0;
						Config.campaingRecords[i][2] = 0;
					}
				}
				else if (i != 0){//jesli nie posiada rekordu i nie jest to pierwszy poziom - dodanie informacji o zablokowanej mapce (to tylko opcja awaryjna w przypadku bledu zawartosci pliku)
					mapNames[0][i] += "Dostęp zablokowany";
				}
			}
		}
		return mapNames;
	}
	//funkcja rozpoczyna procedure plynnego przejscia widoku
	private void setViewPos(float x, float y) {
		//przypisanie pozycji
		viewX = x;
		viewY = y;
		//wlaczenie animacji
		viewUpdate = true;
	}
	
	//algorytym plynnego przejscia widoku
	private void updateView(float deltaTime) {
		//obliczanie odleglosci miedzy aktualnym przesunieciem a pozadanym
		float deltaX = viewX - lastViewX;
		float deltaY = viewY - lastViewY;
		//jesli odleglosci sa wieksze niz minimum
		if(Math.abs(deltaX) > Config.viewPosToleration || Math.abs(deltaY) > Config.viewPosToleration) {
			//plynne przesuwanie
			lastViewX += deltaX * deltaTime * Config.speedTransition;
			lastViewY += deltaY * deltaTime * Config.speedTransition;
		}
		else {//precyzyjne ustawianie widoku i zakonczenie animacji
			lastViewX = viewX;
			lastViewY = viewY;
			viewUpdate = false;
		}
		
		glLoadIdentity();//ustawia pozycje opengl na srodku
		glOrtho(-lastViewX*Config.width, Config.width-lastViewX*Config.width, Config.height-lastViewY*Config.height, -lastViewY*Config.height, 0, 1);//przesuwa widok na wlasciwe wspolzedne
		
		background1.setBounds(-lastViewX*Config.width, lastViewY*Config.height, Config.width, Config.height);//przesuwa tlo w przeciwnym kierunku co daje wrazenie ze tlo sie nie porusza
		background2.setBounds(-lastViewX*Config.width, lastViewY*Config.height, Config.width, Config.height);//przesuwa tlo w przeciwnym kierunku co daje wrazenie ze tlo sie nie porusza
		//aktualizuje parametry przesuniecia w obiekcie GUI
		gui.viewOffset.x = -lastViewX*Config.width;
		gui.viewOffset.y = lastViewY*Config.height;
	}
	//inne kalkulacje
	private void update(float delta) {
		//zmiana tla
		if(timeToSwitch > 0)
			timeToSwitch -= delta;
		else if(timeToSwitch > -10){
			backgroundState++;
			if(backgroundState > 2)
				backgroundState = 0;
			timeToSwitch = -10;
		}
		else {//gdy timeToSwitch < -10
			timeToSwitch -= delta;
			float value = (Config.backgroundSwitchSpeed - (-timeToSwitch-10.0f))/Config.backgroundSwitchSpeed;
			background1.setAlfa(value);
			if(value < 0) {
				background1.setAlfa(1.0f);
				background1.setTexture(backgrounds[backgroundState]);
				background2.setTexture(backgrounds[backgroundState+1 < 3 ? backgroundState+1 : 0]);
				timeToSwitch = Config.backgroundsSwitchTime;
			}
		}
		
		//zarzadzanie wyborem poziomu
		if(Math.abs(visibleLevel - selectedLvl) > Config.viewPosToleration) {//przesuwanie listy mapek
			visibleLevel -= (visibleLevel - selectedLvl) * delta * Config.speedTransition;
			updateLevelView(0);
		}
		else if(visibleLevel != selectedLvl){
			visibleLevel = selectedLvl;
			updateLevelView(0);
		}
		///////////////////////////////////////////////////////////////
		if(Math.abs(campaingVisibleLevel - campaingSelectedLvl) > Config.viewPosToleration) {//przesuwanie listy mapek dla kampani
			campaingVisibleLevel -= (campaingVisibleLevel - campaingSelectedLvl) * delta * Config.speedTransition;
			updateLevelView(1);
		}
		else if(campaingVisibleLevel != campaingSelectedLvl){
			campaingVisibleLevel = campaingSelectedLvl;
			updateLevelView(1);
		}
	}
	//aktualizuje pozycje przesuwanej listy mapek
	private void updateLevelView(int type) {
		switch (type) {
		case 0:
			for(int i=0; i<mapNames.length; i++) {//dla kazdej mapy
				mapNames[i].setBounds(Config.width*i - Config.width*(visibleLevel-1), Config.height*2.2f, Config.width, Config.height*0.6f);
			}
		break;
		case 1:
			for(int i=0; i<campaingMapNames.length; i++) {//dla kazdej mapy
				campaingMapNames[i].setBounds(Config.width*i - Config.width*(campaingVisibleLevel-1), Config.height*3.2f, Config.width, Config.height*0.6f);
			}
		break;
		}
	}
	//renderowanie menu
	public void render(float delta) {
		update(delta);//inne kalkulacje
		if(viewUpdate)
			updateView(delta);//animowanie przesuwania widoku
		
		glClearColor(0.0f, 0.0f, 0.0f, 1.0f);//czysci tlo okna na czarno
		glClear(GL_COLOR_BUFFER_BIT);//czysci powierzchnie okna by wyrenderowac nastepna klatke
		gui.render();//renderowanie graficznego interfejsu uzytkownika
	}
	private void setUpRender() {
		//konfiguracja renderowania figur opengl
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, Config.width, Config.height, 0, 0, 1);
		glViewport(0, 0, Config.width, Config.height);
		
		glEnable(GL_TEXTURE_2D);//wlaczenie textur
		//wlaczenie obslugi przezroczystosci
		glEnable(GL_BLEND);
 		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	}
	
	//czyszczenie pamieci przed usunieciem obiektu Menu
	public void clear() {
		gui.clear();
	}
}
