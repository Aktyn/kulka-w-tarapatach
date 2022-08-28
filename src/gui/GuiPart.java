package gui;

import org.newdawn.slick.opengl.Texture;

public interface GuiPart {
	public void setBounds(float x, float y, float width, float height);//aktualizuje obszar
	public Bounds getBounds();//zwraca obszar
	public void setColor(float r, float g, float b);//ustawia kolor obrazka
	public void setAlfa(float alfa);//ustawia przezroczystosc tla kontrolki
	public void setTexture(Texture texture);//ustawia texture tla dla kontroli
	public boolean getListener();//sprawdzanie czy kontrolka jest nasluchiwana
	public void render();//renderowanie kontrolki
	public void clear();//czyszczenie pamieci z kontrolek przy usuwaniu
}
