package manageassets;

import org.newdawn.slick.Music;
import org.newdawn.slick.Sound;

public class SoundHandle {
	public Object sound;//dzieki polimorfizmowi javy zmienna staje sie obiektem Sound lub Music zaleznie od wywolanego konstruktora
	public String path;//sciezka do pliku dzwiekowego

	public SoundHandle(Music sound, String path) {
		this.sound = sound;
		this.path = path;
	}
	public SoundHandle(Sound sound, String path) {
		this.sound = sound;
		this.path = path;
	}
}
