package gra;

import org.lwjgl.util.vector.Vector2f;

public class Triangle {
	public Vector2f[] vertexes;//tablica punktow z jakich sklada sie trojkat
	public Triangle(Vector2f vertex1, Vector2f vertex2, Vector2f vertex3) {
		this.vertexes = new Vector2f[]{vertex1, vertex2, vertex3};
	}
	public Vector2f getVector(int vertex) {//zwraca dany punkt
		return new Vector2f(vertexes[vertex].x, 1.0f - vertexes[vertex].y);
	}
}
