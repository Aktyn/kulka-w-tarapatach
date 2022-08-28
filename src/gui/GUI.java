package gui;

import gra.Config;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.Sound;

public class GUI {
	private List<GuiPart> parts;//lista roznych obiektow (czesci interfejsu takich jak przycisk, obrazek, tekst) polaczonych interfejsem GuiPart
	private float mouseX, mouseY;//pozycja kursora myszy
	private boolean mouseClicked;//klikniecie lewego przycisku myszy
	public Vector2f viewOffset;//przesuniecie widoku

	public GUI() {
		parts = new ArrayList<GuiPart>();//tworzenie nowej pustej listy
		//domyslne ustawienia zmiennych globalnych
		this.mouseX = 0;
		this.mouseY = 0;
		this.mouseClicked = false;
		this.viewOffset = new Vector2f(0,0);
	}
	//dodanie obiektu do gui
	public void addPart(GuiPart part) {
		parts.add(part);
	}
	public void render() {
		getInput();//aktualizowanie pozycji myszy i kliknietych przyciskow
		for(int i=0; i<parts.size(); i++) {
			if(parts.get(i) != null) {//jesli kontrolka zostala zadeklarowana
				//jesli obszar kontrolki jest w widzialnym obszarze
				if(parts.get(i).getBounds().y+parts.get(i).getBounds().height >= viewOffset.y-Config.height*Config.globalMenuOffset &&
						parts.get(i).getBounds().y <= viewOffset.y+Config.height*(1+Config.globalMenuOffset) &&
						parts.get(i).getBounds().x+parts.get(i).getBounds().width >= viewOffset.x-Config.width*Config.globalMenuOffset &&
						parts.get(i).getBounds().x <= viewOffset.x+Config.width*(1+Config.globalMenuOffset)) {
					if(parts.get(i).getListener()) {//sprawdzanie czy kontrolka jest nasluchiwana
						if(parts.get(i).toString().contains("Button")) {//sprawdzanie czy obiekt jest przyciskiem
							Button buttonToUpdate = (Button) parts.get(i);//zadeklarowanie obiektu button i wywolanie funkcji aktualizujacej go
							buttonToUpdate.updateListener(viewOffset.x + mouseX, viewOffset.y + mouseY, mouseClicked);
						}
					}
					parts.get(i).render();
				}
			}
		}
	}
	//nadaje przyciskom dzwiek klikeniecia
	public void setClickEffect(Sound sound) {
		for(int i=0; i<parts.size(); i++) {//dla kazdego elementu gui
			if(parts.get(i) != null && parts.get(i).getListener()) {//jesli kontrolka jest nasluchiwana
				if(parts.get(i).toString().contains("Button")) {//i jest przyciskiem
					((Button) parts.get(i)).setClickSound(sound);//nadanie kontrolce efektu dzwiekowego
				}
			}
		}
	}
	//aktualizowanie pozycji myszy i kliknietych przyciskow
	private void getInput() {
		if(Mouse.next()) {
			mouseClicked = Mouse.isButtonDown(0);//lewy przycisk myszy klikniety
			mouseX = Mouse.getX();//pozioma wspolzenda kursora wzgledem okna gry
			mouseY = Mouse.getY();//pionowa jak wyzej
		}
	}
	//czyszczenie wszystkich kontrolek przed usunieciem obiektu GUI
	public void clear() {
		for(int i=0; i<parts.size(); i++) {
			parts.get(i).clear();//wywolywanie funkcji czyszczacej dla kazdego elementu intefejsu
		}
	}
}
