package gra;

import static org.lwjgl.opengl.GL11.*;//importowanie calego OpenGL 11
import gui.Bounds;

import org.newdawn.slick.opengl.Texture;

public class Map {
	private Texture mapTexture;//textura scian
	private Texture backTexture;//textura tla
	private Bounds gridBounds;//prostokatny obszar mapy
	
	Triangle[] mapFaces;//polygony z jakich sklada sie obiekt mapy
	
	//ustawia texture scian i tla, w tej kolejnosci
	public void setTexture(Texture texture, Texture texture2) {
		this.mapTexture = texture;
		this.backTexture = texture2;
	}
	//ustawia prostokatny obszar mapy
	public void setBounds(float x, float y, float width, float height) {
		this.gridBounds = new Bounds(x,y,width,height);
	}
	//przypisuje vektory do scian
	public void setWalls(Triangle[] walls) {
		this.mapFaces = walls;
	}
	
	public void render(float delta) {
		glColor3f(1, 1, 1);//kolor bialy bez przezroczystosci
		//renderowanie tla
		if(backTexture != null) 
			backTexture.bind();
		else
			glBindTexture(GL_TEXTURE_2D, 0);//usuwanie textury z opengl dla nastepnych polygonow
		glBegin(GL_QUADS);
	      glTexCoord2f(0, 0);
	      glVertex2f(gridBounds.x, Config.height-gridBounds.y-gridBounds.height);
	      glTexCoord2f(1, 0);
	      glVertex2f(gridBounds.x+gridBounds.width, Config.height-gridBounds.y-gridBounds.height);
	      glTexCoord2f(1, 1);
	      glVertex2f(gridBounds.x+gridBounds.width, Config.height-gridBounds.y);
	      glTexCoord2f(0, 1);
	      glVertex2f(gridBounds.x, Config.height-gridBounds.y);
	   glEnd();
	   
	   if(Config.shadows) {//rysowanie cienia scian
		   glBindTexture(GL_TEXTURE_2D, 0);//rysowanie cienia bez textury
		   glColor4f(0, 0, 0, 0.35f);//polprzezroczysty czarny kolor
		   float przesuniecie = 0.018f;
		   for(Triangle triangle : mapFaces) {
				glBegin(GL_TRIANGLES);
					for(int i=0; i<3; i++) {
						float x = gridBounds.x + triangle.vertexes[i].x*gridBounds.width + gridBounds.width*przesuniecie;
						float y = gridBounds.y + triangle.vertexes[i].y*gridBounds.height + gridBounds.height*przesuniecie;
						if(x > gridBounds.x + gridBounds.width)
							x = gridBounds.x + gridBounds.width;
						if(y > gridBounds.y + gridBounds.height)
							y = gridBounds.y + gridBounds.height;
						glVertex2f(x, y);
					}
				glEnd();
		   }
	   }
		//rysowanie scian
	   if(mapTexture != null)
		   glBindTexture(GL_TEXTURE_2D, mapTexture.getTextureID());
	   else
		   glBindTexture(GL_TEXTURE_2D, 0);//usuwanie textury z opengl dla nastepnych polygonow
	   glColor3f(1, 1, 1);//kolor bialy bez przezroczystosci
	   for(Triangle triangle : mapFaces) {
			glBegin(GL_TRIANGLES);
				glTexCoord2f(triangle.vertexes[0].x*Config.mapTextureTiles, triangle.vertexes[0].y*Config.mapTextureTiles);
				glVertex2f(gridBounds.x + triangle.vertexes[0].x*gridBounds.width, gridBounds.y + triangle.vertexes[0].y*gridBounds.height);
				glTexCoord2f(triangle.vertexes[1].x*Config.mapTextureTiles, triangle.vertexes[1].y*Config.mapTextureTiles);
				glVertex2f(gridBounds.x + triangle.vertexes[1].x*gridBounds.width, gridBounds.y + triangle.vertexes[1].y*gridBounds.height);
				glTexCoord2f(triangle.vertexes[2].x*Config.mapTextureTiles, triangle.vertexes[2].y*Config.mapTextureTiles);
				glVertex2f(gridBounds.x + triangle.vertexes[2].x*gridBounds.width, gridBounds.y + triangle.vertexes[2].y*gridBounds.height);
			glEnd();
		}
	}
}
