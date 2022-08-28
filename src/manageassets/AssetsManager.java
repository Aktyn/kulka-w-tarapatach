package manageassets;

import static org.lwjgl.opengl.GL11.*;//importowanie calego OpenGL 11

import java.awt.Color;
//importowanie bibliotek potrzebnych do ladowania assetow
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.font.effects.OutlineEffect;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class AssetsManager {
	public Parameter parameter;//przechowuje zmienne dotyczace sposobu filtrowania textur
	
	private List<TextureHandle> textures;//lista obiektow przechowujacych dane pliku textury
	private List<FontHandle> fonts;//adekwatnie jak wyzej
	private List<SoundHandle> sounds;//jak wyzej
	
	public AssetsManager() {
		textures = new ArrayList<TextureHandle>();//tworzenie pustej listy 
		fonts = new ArrayList<FontHandle>();//jak wyzej
		sounds = new ArrayList<SoundHandle>();//tak jak powyzej
		parameter = new Parameter(GL_LINEAR, GL_LINEAR);//stworzenie obiektu domyslnymi wartosciami filtrowania textur
	}
	
	/**			  Textury		 	**/
	public void loadTexture(String path) {
		Texture textura = null;
		///////// SPRAWDZANIE ROZSZERZENIA PLIKU TEXTURY ///////////
		//rozszerzenie pliku
		String rozszerzenie = null;
		if(path.contains(".jpg")) {
			rozszerzenie = "JPG";
		}
		if(path.contains(".png")) {
			rozszerzenie = "PNG";
		}
		if(rozszerzenie == null)
			rozszerzenie = "JPG";//domyslne
		////////////////////////////////////////////////////////
		try {
			if (new File(path).exists()) {//jesli plik z textura istnieje
				textura = TextureLoader.getTexture(rozszerzenie, ResourceLoader.getResourceAsStream(path));//zostaje zaladowany
			}
			setTextureFilter();//ustawianie parametru dopiero co zaladowanej textury w opengl
		} catch (IOException e) {
			e.printStackTrace();//wyrzucenie bledu
		}
		if(textura != null) {//jesli udal sie import textury
			textures.add(new TextureHandle(textura, path));//dodanie nowego obiektu z informacjami o texturze do listy
		}
	}
	//czyszczenie z pamieci pojedynczej textury po sciezce
	public void unloadTexture(String path) {
		for(int i=0; i<textures.size(); i++) {
			if(textures.get(i).path.equals(path)) {
				glDeleteTextures(textures.get(i).texture.getTextureID());
				textures.remove(i);//usuniecie textury z listy zaladowanych textur po identyfikatorze
			}
		}
	}
	//czyszczenie z pamieci pojedynczej textury
	public void unloadTexture(Texture texture) {
		glDeleteTextures(texture.getTextureID());
		textures.remove(texture);//usuniecie textury z listy zaladowanych textur
	}
	//czyszczenie wszystkich textur
	public void unloadTextures() {
		for(int i=0; i<textures.size(); i++)
			glDeleteTextures(textures.get(i).texture.getTextureID());
		for(int i=0; i<textures.size(); i++)
			textures.remove(i);//usuniecie textury z listy zaladowanych textur po indentyfikatorze
	}
	//pobieranie zmiennej textury z listy
	public Texture getTexture(String path) {
		for(int i=0; i<textures.size(); i++) {
			if(textures.get(i).path.equals(path))//sprawdzanie po sciezce
				return textures.get(i).texture;
		}
		return null;//zwraca pusta texture jesli nie ma szukanej na liscie
	}
	
	/**			Fonts			**/
	@SuppressWarnings("unchecked")//jakas glupia linia dodana przez eclipse zeby nie bylo zoltego wykrzyknika
	//funkcja ladujaca czcionke do pamieci
	public void loadFont(String path, float size, String name,  boolean outline) {
	    UnicodeFont font = null;
	    if (new File(path).exists()) {//jesli plik czcionki istnieje
		    try {
				font = new UnicodeFont(path, (int)size, false, false);//parametry tworzenia czcionki w tej lini to kolejno: sciezka, rozmiar, pogrubienie, kursywa
				font.addAsciiGlyphs();//ustala wszystkie znaki ascii mozliwe do wyswietlenia
				//font.addGlyphs(0, 512);//ustawia znaki w pewnym zasiegu liczbowym
				font.addGlyphs("ąĄćĆęĘłŁńŃóÓśŚźŹżŻ");//dodanie polskich znakow do czcionki
				font.getEffects().add(new ColorEffect(new Color(1f,1f,1f)));//ustawia czcionke w kolorze bialym
				if(outline)
					font.getEffects().add(new OutlineEffect((int)(size/50f), Color.black));//dodaje czarny obrys o grubosci 2 pixeli
				font.loadGlyphs();//laduje wszystkie znaki
			} catch (SlickException e) {
				e.printStackTrace();
			}
	    }
	    if(font != null)//jesli import czcionki sie udal
	    	fonts.add(new FontHandle(font, name, size));//dodaje do listy obiekt czcionki
	}
	//usuwanie z pamieci wszystkich czcionek
	public void unloadFonts() {
		for(int i=0; i<fonts.size(); i++) {//dla kazdego obiektu w liscie z uchwytami czcionek
			fonts.get(i).font.clearGlyphs();//czyszczenie znakow
			fonts.get(i).font.destroy();//czyszczenie wszystkich srodkow jakich uzywa czcionka
		}
		fonts.clear();//czyszczenie listy
	}
	//pobieranie czcionki z listy po nazwie
	public UnicodeFont getFont(String name) {
		for(int i=0; i<fonts.size(); i++) {
			if(fonts.get(i).name.equals(name))//szuka po nazwie
				return fonts.get(i).font;
		}
		return null;
	}
	//DZWIEKI
	//laduje plik dzwiekowy
	public void loadSound(String path, boolean stream) {
		try {
			if(stream) {//ladowanie muzyki
				Music music = new Music(path);
				if(music != null)
					sounds.add(new SoundHandle(music, path));
			}
			else {//ladowanie efektow dzwiekowych
				Sound sound = new Sound(path);
				if(sound != null)
					sounds.add(new SoundHandle(sound, path));
			}
		}
		catch(SlickException e) {
			e.printStackTrace();
		}
	}
	//pobieranie zmiennej dzwieku z listy
	public Sound getSound(String path) {
		for(int i=0; i<sounds.size(); i++) {
			if(sounds.get(i).path.equals(path))//sprawdzanie po sciezce
				return (Sound) sounds.get(i).sound;
		}
		return null;//zwraca pusta texture jesli nie ma szukanej na liscie
	}
	//pobieranie zmiennej muzyki z listy
	public Music getMusic(String path) {
		for(int i=0; i<sounds.size(); i++) {
			if(sounds.get(i).path.equals(path))//sprawdzanie po sciezce
				return (Music) sounds.get(i).sound;
		}
		return null;//zwraca pusta texture jesli nie ma szukanej na liscie
	}
	//ustawia parametry filtrowania textur w opengl
	private void setTextureFilter() {
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, parameter.minFilter);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, parameter.magFilter);
	}
}
