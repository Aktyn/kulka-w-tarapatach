package gra;

//importowanie bibliotek lwgl potrzebnych do stworzenia i wyswietlenia okna
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.LWJGLException;//obsluga wyjatkow bibliotek lwjgl
import org.lwjgl.opengl.Display;

public class Main {
	public static void main(String[] args) {
		//ta funkcja zostaje wykonana po uruchomieniu gry
		try {
			Config.load();//ladowanie ustawien z pliku z ustawieniami
			//konfiguracja okna z gra
			Config.updateDisplay();//po raz pierwszy wywolywana funkcja konfigurujaca okienko gry
			Display.setVSyncEnabled(Config.vsync);//wlacza (lub nie wlacza) synchronizacje pionowa
			Display.setTitle("Kulka w tarapatach");//ustawia tytul dla okna
			Display.create();//tworzy okno
		} catch (LWJGLException e) {
			//jesli wystapi blad podczas konfiguracji okna - zamyka aplikacje
			e.printStackTrace();
			Display.destroy();
			System.exit(1);
		}
		setIcon();//ustawia texture okna
		
		Window window = new Window();//tworzy nowy obiekt Window
		while (!Display.isCloseRequested()) {//petla ktora podtrzymuje istnienie okna z gra
			window.render();//wywolanie funkcji render w obiekcie Window
			Display.update();//odswiezenie okna gry
			Display.sync(Config.fps);//czestotliwosc nastepnych odswiezen (klatki na sekunde)
		}
		//po zakonczeniu gry
		Display.destroy();//niszczy okno
		System.exit(0);//i zamyka aplikacje
	}
	//ustawia ikonke widoczna w pasku zadan i na pasku okna w windows
	private static void setIcon() {
		try {
		    ByteBuffer[] icons = new ByteBuffer[4];//tablica z danymi czterech ikon
		    //ladowanie ikon w czterech rozdzielczosciach do tablicy
		    icons[0] = loadIcon("assets/Icon/kulka16.png", 16, 16);
		    icons[1] = loadIcon("assets/Icon/kulka32.png", 32, 32);
		    icons[2] = loadIcon("assets/Icon/kulka64.png", 64, 64);
		    icons[3] = loadIcon("assets/Icon/kulka128.png", 128, 128);
		    Display.setIcon(icons);//zmiana ikony okna
		} catch (IOException ex) {
		    ex.printStackTrace();//wyswietla kod bledu
		}
	}
	//zwraca obiekt ByteBuffer zawartosci pliku ikony
	private static ByteBuffer loadIcon(String filename, int width, int height) throws IOException {
	    BufferedImage image = ImageIO.read(new File(filename)); //laduje obraz z pliku

	    //zamienia obraz na tablice z bitami
	    byte[] imageBytes = new byte[width * height * 4];
	    //dla kazdego pixela
	    for (int i = 0; i < height; i++) {
	        for (int j = 0; j < width; j++) {
	            int pixel = image.getRGB(j, i);//pobiera wartosc koloru dla danego pixela
	            for (int k = 0; k < 3; k++) //dla kolorow podstawowych
	                imageBytes[(i*height+j)*4 + k] = (byte)(((pixel>>(2-k)*8))&255);
	            imageBytes[(i*height+j)*4 + 3] = (byte)(((pixel>>(3)*8))&255); //kanal alfa
	        }
	    }
	    return ByteBuffer.wrap(imageBytes);
	}
}
