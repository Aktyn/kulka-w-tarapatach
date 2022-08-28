package manageassets;

import org.newdawn.slick.opengl.Texture;

public class TextureHandle {
	public Texture texture;
	public String path;//sciezka do pliku textury
	
	public TextureHandle(Texture texture, String path) {
		this.texture = texture;
		this.path = path;
	}
}
