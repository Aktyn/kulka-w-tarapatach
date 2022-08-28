package gui;

import gra.Config;

import org.newdawn.slick.Color;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.opengl.Texture;

public class Label extends Image implements GuiPart {
	private Bounds bounds;//prostokatny obszar belki
	private String[] text;//tekst do wyswietlenia podzielony na linie
	private UnicodeFont font;//czcionka tekstu
	private Bounds[] fontBounds;//osobny obszar obliczany dla wyswietlania czcionki
	private Color fontColor;//kolor czcionki
	
	//pozycjonowanie tekstu
	private int textAling = 0; // 0 - srodek, 1 - lewo, 2 - prawo
	private int verticalAlign = 0; // 0 - srodek, 1 - gora, 2 - dol
	
	public Label() {
		this.text = new String[]{""};
		this.bounds = new Bounds(0, 0, 100, 100);//domyslne wartosci
		this.fontBounds = new Bounds[]{new Bounds(0, 0, 0, 0)};//jak wyzej
		this.fontColor = new Color(1,1,1);//jak wyzej
		setFontColor(1,1,1,1);//jak wyzej
	}
	
	//zwraca siebie
	public Label returnObject() {
		return this;
	}
	
	//aktualizacja obszaru
	public void setBounds(float x, float y, float width, float height) {
		this.bounds.x = x;
		this.bounds.y = y;
		this.bounds.width = width;
		this.bounds.height = height;
		super.setBounds(bounds.x, bounds.y, bounds.width*2f, bounds.height);//kopiowanie obszaru dla tla
		updateFontBounds();
	}
	//ustawienie tekstu
	public void setText(String text) {
		this.text = text.split("\n");//dzielenie tekstu na linie
		this.fontBounds = new Bounds[this.text.length];//tworzenie takiej samej ilosci obszarow tekstu
		for(int i=0; i<this.text.length; i++)
			this.fontBounds[i] = new Bounds(0, 0, 0, 0);
		updateFontBounds();
	}
	//zwraca tekst belki z przelamanymi wierszami
	public String getText() {
		String text = "";
		for(int i=0; i<this.text.length; i++) {
			text += this.text[i];//dodaje linie textu
			if(i != this.text.length-1)//jesli nie jest to ostatnia linia
				text += "\n";//dodaje znak przelamania lini
		}
		return text;
	}
	//ustalenie pozycji tekstu w poziomie
	public void setTextAlign(int align) {
		this.textAling = align;
		updateFontBounds();
	}
	//ustalenie pozycji tekstu dla obu osi
	public void setTextAlign(int align, int verticalAlign) {
		this.verticalAlign = verticalAlign;
		setTextAlign(align);
	}
	//przypisane czcionki dla belki z tekstem
	public void setFont(UnicodeFont font) {
		this.font = font;
		updateFontBounds();
	}
	//ustawienie koloru czcionki
	public void setFontColor(float r, float g, float b, float a) {
		fontColor.r = r;
		fontColor.g = g;
		fontColor.b = b;
		fontColor.a = a;
	}
	//zwraca kolor czcionki
	public Color getFontColor() {
		return fontColor;
	}
	//przypisanie textury
	public void setTexture(Texture texture) {
		super.setTexture(texture);//ustawianie textury dla obrazka
		super.setBounds(bounds.x, bounds.y, bounds.width*2f, bounds.height);//kopiowanie obszaru dla tla
	}
	//obliczanie obszaru dla tekstu zaleznie od jego pozycji
	private void updateFontBounds() {
		for(int i=0; i<text.length; i++) {
			float allHeight = 0;
			float textHeight = 0;
			float textWidth = 0;
			if(font != null) {
				textHeight = font.getHeight("H");//H to przykladowa duza litera od ktorej mierdzona jest wysokosc lini tekstu
				textWidth = font.getWidth(text[i]);
				allHeight = text.length * font.getHeight("H");//suma wysokosci wszystkich wierszy
			}
			fontBounds[i].width = textWidth;
			fontBounds[i].height = textHeight;
			switch(textAling) {
				case 0://ustawianie tekstu na srodku obszaru
					fontBounds[i].x = (int)(bounds.x + (bounds.width-textWidth)/2f);
					break;
				case 1://przyleganie tekstu do lewej krawedzi
					fontBounds[i].x = (int)(bounds.x);
					break;
				case 2://przyleganie do prawej
					fontBounds[i].x = (int)(bounds.x + bounds.width - textWidth);
					break;
			}
			switch(verticalAlign) {
				case 0://tekst na srodku w pionie
					fontBounds[i].y = (int)(bounds.y + (bounds.height - allHeight)/2f);
					fontBounds[i].y += allHeight-textHeight*(i+1);
					break;
				case 1://tekst przylegajacy do gornej krawedzi obszaru
					fontBounds[i].y = (int)(bounds.y + bounds.height - allHeight);
					fontBounds[i].y += allHeight-textHeight*(i+1);
					break;
				case 2://tekst przylegajacy do dolnej krawedzi
					fontBounds[i].y = (int)bounds.y;
					fontBounds[i].y += allHeight-textHeight*(i+1);
					break;
			}
		}
	}

	public void render() {
		super.render();//renderowanie tla
		if(font != null) {//jesli czcionka nie jest pusta
			for(int i=0; i<text.length; i++)
				font.drawString(fontBounds[i].x, Config.height-fontBounds[i].y-fontBounds[i].height, text[i], fontColor);//zostaje wyrenderowana z odpowiednimi parametrami
		}
	}

	//czyszczenie przed zniszczeniem kontrolki
	public void clear() {
		super.clear();//czyszczenie tla
	}
}
