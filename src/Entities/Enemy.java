package Entities;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import Main.Game;
import Main.Sound;
import World.*;

public class Enemy extends Entity {
	
	private double speed = 0.5;
	
	public int frames = 0, maxFrames = 20, index = 0, maxIndex = 1;
	
	private BufferedImage[] sprites;
	
	private int life = 4;
	
	private boolean isDamaged = false;
	private int damageFrames = 10, damageCurrent = 0;
	
	// Valores da máscara de colisão dos inimigos
	private int maskx = 8, masky = 8, maskw = 6, maskh = 6;

	public Enemy(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, null);
		sprites = new BufferedImage[2];
		sprites[0] = Entity.ENEMY_EN;
		sprites[1] = Entity.ENEMY_EN2;
	}
	
	public void update() {
		if (!isCollidingWithPlayer()) {
			if ((int)x < Game.player.getX() && World.isFree((int)(x + speed), this.getY())
				&& !isColliding((int)(x + speed), this.getY())) {
				x += speed;
			} else if ((int)x > Game.player.getX() && World.isFree((int)(x - speed), this.getY())
					&& !isColliding((int)(x - speed), this.getY())) {
				x -= speed;
			}
			
			if ((int)y < Game.player.getY() && World.isFree(this.getX(), (int)(y + speed))
					&& !isColliding(this.getX(), (int)(y + speed))) {
				y += speed;
			} else if ((int)y > Game.player.getY() && World.isFree(this.getX(), (int)(y - speed))
					&& !isColliding(this.getX(), (int)(y - speed))) {
				y -= speed;
			}
			
		} else { // Colidindo com o player
			if (Game.rand.nextInt(100) < 25) {
				Sound.hurtEffect.play();
				Game.player.life -= Game.rand.nextInt(5);
				Game.player.isDamaged = true;
			}
		}
		
		frames++;
		if (frames == maxFrames) {
			frames = 0;
			index++;
			
			if (index > maxIndex)
				index = 0;
		}
		
		collidingWithBullet();
		
		if (life <= 0) {
			destroySelf();
			return;
		}
		
		if (isDamaged) {
			this.damageCurrent++;
			if (this.damageCurrent == this.damageFrames) {
				this.damageCurrent = 0;
				this.isDamaged = false;
			}
		}
	}
	
	public void destroySelf() {
		Game.enemies.remove(this);
		Game.entities.remove(this);
	}

	public void collidingWithBullet() {
		for (int i = 0; i < Game.bullets.size(); i++) {
			
			Entity e = Game.bullets.get(i);
			if (e instanceof WeaponBullet) {
				if (Entity.isColliding(this, e)) {
					isDamaged = true;
					life--;
					Game.bullets.remove(i);
					return;
				}
			}
		}
	}
	
	public boolean isCollidingWithPlayer() {
		Rectangle enemyCurrent = new Rectangle(this.getX() + maskx, this.getY() + masky, maskw, maskh);
		Rectangle player = new Rectangle(Game.player.getX(), Game.player.getY(), 16, 16);
		return enemyCurrent.intersects(player);
	}
	
	public boolean isColliding(int xnext, int ynext) {
		Rectangle enemyCurrent = new Rectangle(xnext + maskx, ynext + masky, maskw, maskh);
		
		for (int i = 0; i < Game.enemies.size(); i++) {
			Enemy e = Game.enemies.get(i);
			if (e == this) {
				continue;
			}
			
			Rectangle targetEnemy = new Rectangle(e.getX() + maskx, e.getY() + masky, maskw, maskh);
			if (enemyCurrent.intersects(targetEnemy)) {
				return true;
			}
 		}
		
		return false;
	}
	
	public void render(Graphics g) {
		if (!isDamaged) 
			g.drawImage(sprites[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
		else
			g.drawImage(Entity.ENEMY_FEEDBACK, this.getX() - Camera.x, this.getY() - Camera.y, null);
	}
}
