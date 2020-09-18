package Entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import Main.Game;
import World.Camera;
import World.WallTile;

public class WeaponBullet extends Entity {

	private double dx;
	private double dy;
	private double speed = 4;
	
	private int life = 30, curLife = 0;
	
	public WeaponBullet(int x, int y, int width, int height, BufferedImage sprite, double dx, double dy) {
		super(x, y, width, height, sprite);
		this.dx = dx;
		this.dy = dy;
	}

	public void update() {
		x += dx * speed;
		y += dy * speed;
		
		curLife++;
		if(curLife == life) {
			Game.bullets.remove(this);
			return;
		}
		
		// Checando colisão com a parede
		WallTile.isCollidingWithBullet();
		
		// Método para remover as balas apenas quando sairem da tela
		/*
		if (x - Camera.x > Game.WIDTH || x - Camera.x < 0) {
			Game.bullets.remove(this);
		}
		if (y - Camera.y > Game.HEIGHT || y - Camera.y < 0) {
			Game.bullets.remove(this);
		}
		*/
	}
	
	public void render(Graphics g) {
		g.setColor(Color.YELLOW);
		g.fillRect(this.getX()- Camera.x, this.getY() - Camera.y, width, height);
	}
	
}
