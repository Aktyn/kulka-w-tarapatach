package manageassets;

import org.newdawn.slick.UnicodeFont;

public class FontHandle {
	public UnicodeFont font;
	public String name;//nazwa wygenerowanej czcionki
	public float size;//rozmiar wygenerowanej czcionki

	public FontHandle(UnicodeFont font, String path, float size) {
		this.font = font;
		this.name = path;
		this.size = size;
	}
}
