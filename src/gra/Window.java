package gra;

import manageassets.AssetsManager;//klasa AssetsManager z innej paczki

import org.lwjgl.Sys;//import biblioteki lwjgl z informacjami o systemie operacyjnym

public class Window {
	private int state; //stan gry, 0 - ladowanie, 1 - menu, 2 - gra
	
	private Loading loading;//obiekt odpowiedzialny za ladowanie assetow i wyswietalnie przy tym animacji
	private Menu menu;//obiekt zarzadzajacy calym menu gry
	private Game game;//obiekt zarzadzajacy rozgrywką
	
	public AssetsManager assetsManager;//obiekt ladujacy i czyszczazcy z pamieci textury, dzwieki, czcionki
	
	private long lastTime = 0;//przechowuje czas systemowy z poprzedniej klatki gry dla obliczenia deltaTime
	private Runtime runtime;//zmienna potrzebna do sprawdzania pamieci ram
	
	public Window() {
		lastTime = Sys.getTime();//pierwsze przypisanie czasu systemowego do zmiennej
		assetsManager = new AssetsManager();//deklaracja obiektu
		setState(0);//start ladowania
		runtime = Runtime.getRuntime();
	}
	
	public void render() {
		//spradzanie pamieci
		long memoryUsed = ((runtime.totalMemory() - runtime.freeMemory())/ 1024 / 1024);
		if(memoryUsed > Config.maxMemory)//czyszczenie pamieci jesli jej zuzycie przekroczy 64 mega
			System.gc();
		//renderowanie odpowiednich obiektow w poszczegolnych stanach gry
		switch(state) {
			case 0:
				if(loading != null)
					loading.render(getDeltaTime());
				break;
			case 1:
				if(menu != null)
					menu.render(getDeltaTime());
				break;
			case 2:
				if(game != null)
					game.render(getDeltaTime());
				break;
		}
	}
	//rozpoczyna widok menu
	public void startMenu() {
		loading = null;//usuwanie obiektu Loading
		setState(1);//stworzenie menu
	}
	//restartuje widok menu z poziomu menu
	public void reloadMenu() {
		menu.clear();//czyszczenie pamieci z niepotrzebnych juz assetow menu
		menu = null;//usuwanie obiektu null
		Config.unloadAllFonts(assetsManager);//czysci wszystkie czcionki z pamieci
		Config.loadFonts(assetsManager);//ponowne ladowanie czcionek z nowymi wartosciami (po zmianie rozdzielczosci)
		setState(1);//ponowne uruchamianie menu
	}
	//procedura rozpoczecia gry w trybie przetrwania, kampani lub samouczla
	public void startGame(int gameType) {
		menu.clear();//czyszczenie pamieci z niepotrzebnych juz assetow menu
		menu = null;//usuwanie obiektu menu
		Config.unloadMenuAssets(assetsManager);//czyszczenie textur menu
		Config.loadGameAssets(assetsManager);
		Config.gameType = gameType;//ustawianie trybu gry
		setState(2);//start gry survival
	}
	//restartuje gre
	public void restartGame() {
		game.clear();
		game = null;
		setState(2);
	}
	//wylacza gre i przechodzi do menu
	public void exitToMenu() {
		game.clear();//czyszczenie pamieci po grze
		game = null;
		Config.unloadGameAssets(assetsManager);
		Config.loadMenuAssets(assetsManager);
		setState(1);
	}
	
	private void setState(int state) {//przelacza stany gry
		switch(state) {
			case 0://tworzy obiekt Loading
				loading = new Loading(this);
				break;
			case 1://tworzy obiekt Menu
				menu = new Menu(this);
				break;
			case 2://tworzy obiekt Game
				game = new Game(this);
				break;
		}
		this.state = state;//przypisuje stan do zmiennej globalnej
	}
	
	private float getDeltaTime() {//funkcja liczy czas jaki uplynal od poprzedniej klatki (w sekundach)
		long time = Sys.getTime();
		float delta = (time-lastTime) / (float)Sys.getTimerResolution();
		lastTime = time;//ustawia czas na koniec klatki
		return delta < 0.2f ? delta : 0;//zwraca delte jesli jest mniejsza od maximum
	}
}
