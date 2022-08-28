package gra;

import java.io.File;
import java.io.IOException;

import org.lwjgl.util.vector.Vector2f;

public class MapLoader {
	//czyta z pliku mapy punkty z ktorych stworzone sa trojakty i zwraca tablice z obiektami trojkatow
	public static Triangle[] loadMap(int lvl) {
		File file = new File("assets" + "/" + Config.mapFolder + "/" + Config.mapPrefix + lvl + Config.mapExtension);
		String fileText = "";
		if(file.exists()) {//jesli plik z obiektem mapy istnieje
			fileText = "";
			//czytanie zawartosci pliku
			try {
                fileText = Config.readFile(file.getPath());
			} catch (IOException e) {
                e.printStackTrace();//wyswietlanie bledu przy czytaniu pliku
            }
		}
		String[] fileLines = fileText.split("\n");
		//czyszczenie pliku z niepotrzebnych lini
		fileText = "";//czyszczenie zmiennej
		for(int i=0; i<fileLines.length; i++) {
			if(fileLines[i].charAt(0) == 'o' || fileLines[i].charAt(0) == 'v')
				fileText += fileLines[i] + "\n";
		}
		fileLines = fileText.split("\n");//z powrotem dzieli na linie odfiltrowana zawartosc pliku
		if(fileLines.length % 4 != 0) {//jesli ilosc lini nie jest podzielna przez 4 
			return null;//i zwraca pusta wartosc
		}
		Triangle[] triangles = new Triangle[fileLines.length/4];//tworzenie pustej tablicy o rozmiarze rownej ilosci trojkatow w pliku
		int triangleIndex = 0;
		for(int i=1; i<fileLines.length; i+=4) {
			Vector2f vec1, vec2, vec3;
			vec1 = new Vector2f(Float.valueOf(fileLines[i].split(" ")[1])+0.5f, Float.valueOf(fileLines[i].split(" ")[3])+0.5f);
			vec2 = new Vector2f(Float.valueOf(fileLines[i+1].split(" ")[1])+0.5f, Float.valueOf(fileLines[i+1].split(" ")[3])+0.5f);
			vec3 = new Vector2f(Float.valueOf(fileLines[i+2].split(" ")[1])+0.5f, Float.valueOf(fileLines[i+2].split(" ")[3])+0.5f);
			triangles[triangleIndex++] = new Triangle(vec1, vec2, vec3);
		}
		return triangles;
	}
	//laduje punkty miedzy ktorymi sa krawedzie kolizji mapy
	public static Vector2f[][] loadCollisionLines(int lvl) {
		File file = new File("assets" + "/" + Config.mapFolder + "/" + Config.mapPrefix + lvl  + "x" + Config.mapExtension);
		String fileText = "";
		if(file.exists()) {//jesli plik z obiektem mapy istnieje
			fileText = "";
			//czytanie zawartosci pliku
			try {
                fileText = Config.readFile(file.getPath());
			} catch (IOException e) {
                e.printStackTrace();//wyswietlanie bledu przy czytaniu pliku
            }
		}
		String[] fileLines = fileText.split("\n");
		
		//czyszczenie pliku z niepotrzebnych lini
		fileText = "";//czyszczenie zmiennej
		int parts = 0;//licznik odzdzielnych czesci w kolizji
		for(int i=0; i<fileLines.length; i++) {
			if(fileLines[i].charAt(0) == 'o' || fileLines[i].charAt(0) == 'v')
				fileText += fileLines[i] + "\n";
			if(fileLines[i].charAt(0) == 'o')
				parts++;
		}
		fileLines = fileText.split("\n");
		Vector2f[][] lines = new Vector2f[parts][];
		int index = 0;
		for(int i=0; i<fileLines.length; i++) {
			if(fileLines[i].charAt(0) == 'o') {
				int pointsInPart = 0;
				while(i+pointsInPart+1 < fileLines.length && fileLines[i+pointsInPart+1].charAt(0) != 'o') 
					pointsInPart++;
				lines[index] = new Vector2f[pointsInPart];
				for(int j=0; j<pointsInPart; j++) {
					lines[index][j] = new Vector2f(Float.valueOf(fileLines[i+j+1].split(" ")[1])+0.5f, 1.0f-(Float.valueOf(fileLines[i+j+1].split(" ")[3])+0.5f));
				}
				index++;
			}
		}
		return lines;
	}
}
