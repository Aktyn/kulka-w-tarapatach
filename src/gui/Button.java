package gui;

import gra.Config;

import org.newdawn.slick.Sound;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.opengl.Texture;

public class Button extends Label implements GuiPart {
	private Bounds bounds;//prostokatny obszar na ktorym wyswietlany jest obrazek
	
	public Texture[] stateTextures;//przechowuje textury widoczne przy roznych stanach przycisku
	private int state = 0;// 0 - normalny przycisk, 1 - myszka nad przyciskiem, 2 - klikniety przycisk
	
	private ButtonListener listener;//obiekt odpowiedzialny za obsluge przycisku za pomoca myszy
	private Sound click;//dzwiek klikniecia
	
	public Button() {
		this.bounds = new Bounds(0, 0, 100, 100);//domyslne wartosci
		stateTextures = new Texture[3];//trzyelementowa tablica czekajaca na zaladowanie texturami
	}
	//aktualizacja obszaru
	public void setBounds(float x, float y, float width, float height) {
		this.bounds.x = x;
		this.bounds.y = y;
		this.bounds.width = width;
		this.bounds.height = height;
		super.setBounds(bounds.x, bounds.y, bounds.width, bounds.height);//kopiowanie obszaru dla tla
	}
	//przypisane czcionki dla belki z tekstem
	public void setFont(UnicodeFont font) {
		super.setFont(font);
	}
	//przypisanie dzwieku klikniecia
	public void setClickSound(Sound sound) {
		this.click = sound;
	}
	//laduje texture pierwszego stanu do zmiennej
	public void setNormalTexture(Texture texture) {
		stateTextures[0] = texture;
		setTexture(texture);//dodatkowo ustawia domyslnie texture przycisku
	}
	//laduje texture drugiego stanu do zmiennej
	public void setHoverTexture(Texture texture) {
		stateTextures[1] = texture;
	}
	//laduje texture trzeciego stanu do zmiennej
	public void setClickedTexture(Texture texture) {
		stateTextures[2] = texture;
	}
	//ladowanie wszystkich textur naraz
	public void setTextures(Texture[] textures) {
		if(stateTextures.length == textures.length) {//jesli zgadza sie ilosc textur
			for(int i=0; i<stateTextures.length; i++)
				stateTextures[i] = textures[i];//ladowanie wszystkiego do zmiennej
			setTexture(stateTextures[0]);//i domyslna textura ustawiona
		}
	}
	//dodanie intefejsu nasluchujacego do obiektu przycisku
	public void setButtonListener(ButtonListener listener) {
		this.listener = listener;
	}
	//zwraca prawde jesli dla przycisku zostal zdefiniowany interfejs nasluchujacy
	public boolean getListener() {
		return listener != null;
	}
	//aktualizuje przycisk sprawdzajac pozycje kursora i klikniety przycisk
	public void updateListener(float mX, float mY, boolean mClicked) {
		if(buttonHover(mX, mY)) {//jesli myszka jest nad przyciskiem
			if(mClicked) {//jesli przycisk klikniety
				if(state != 2)//jesli stan jest inny niz klikniety
					setBtnState(2);//zostaje zmieniony
			}
			else {
				if(state != 1) {//jesli stan jest inny
					//jesli przycisk byl przed chwila wcisniety a mysz nadal jest nad nim
					if(state == 2) {
						if(click != null && Config.enableSounds)//jesli zmienna dzwieku nie jest pusta
							click.play(1.0f, Config.effectsVolume*0.5f);//odtwarza dzwiek jako efekt dzwiekowy
						listener.clicked();//wywoluje funkcje klikniecia przycisku	
					}
					setBtnState(1);
				}
			}
		}
		else if (state != 0)//jesli stan jest inny niz domyslny
			setBtnState(0);//zostaje zmieniony
	}
	//sprawdza czy kursor jest w srodku obszaru przycisku
	public boolean buttonHover(float x, float y) {
		if(x > bounds.x && y > bounds.y && x < bounds.x + bounds.width && y < bounds.y + bounds.height)
			return true;
		return false;
	}
	//przelacza stany przycisku
	private void setBtnState(int state) {
		if(state < stateTextures.length) {//jesli stan nie przekracza maksymalnej wartosci
			if(stateTextures[state] != null)//jesli textura nie jest pusta
				setTexture(stateTextures[state]);//zmiana textury przycisku
			this.state = state;
		}
	}
	//ustawia texture tla
	public void setTexture(Texture texture) {
		super.setTexture(texture);
	}
	//ustawienie tekstu
	public void setText(String text) {
		super.setText(text);
	}
	//ustalenie pozycji tekstu w poziomie
	public void setTextAlign(int align) {
		super.setTextAlign(align);
	}
	//ustalenie pozycji tekstu dla obu osi
	public void setTextAlign(int align, int verticalAlign) {
		super.setTextAlign(align, verticalAlign);
	}
	//zwraca obiekt z tekstem
	public Label getLabel() {
		return super.returnObject();
	}
	//renderuje elementy przycisku
	public void render() {
		super.render();
	}
	//czysci elementy przycisku i sam przycisk
	public void clear() {
		super.clear();
	}

}
