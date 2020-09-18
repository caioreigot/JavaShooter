package Entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import Main.*;
import World.*;

public class Player extends Entity {

	public boolean right, up, left, down;
	public int right_dir = 0, left_dir = 1, up_dir = 2, down_dir = 3;
	public int dir = right_dir;
	public double speed = 1.4;
	
	public int frames = 0, maxFrames = 5, index = 0, maxIndex = 3;
	private boolean moved = false;
	private BufferedImage[] rightPlayer;
	private BufferedImage[] leftPlayer;
	private BufferedImage[] upPlayer;
	private BufferedImage[] downPlayer;
	
	private BufferedImage playerDamageRight;
	private BufferedImage playerDamageLeft;
	private BufferedImage playerDamageUp;
	private BufferedImage playerDamageDown;
	
	public int shootCooldown = 0;
	public int maxShootCooldown = 10;
	
	private boolean hasGun = false;
	
	public int ammo = 0;
	
	public boolean isDamaged = false;
	private int damageFrames = 0;
	
	public boolean shoot = false, mouseShoot = false;
	
	public double life = 100, maxlife = 100;
	
	public Player(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);
		
		rightPlayer = new BufferedImage[4];
		leftPlayer = new BufferedImage[4];
		upPlayer = new BufferedImage[4];
		downPlayer = new BufferedImage[4];
		
		playerDamageRight = Game.spritesheet.getSprite(0, 16, 16, 16);
		playerDamageLeft = Game.spritesheet.getSprite(16, 16, 16, 16);
		playerDamageUp = Game.spritesheet.getSprite(16, 48, 16, 16);
		playerDamageDown = Game.spritesheet.getSprite(0, 48, 16, 16);
		
		for (int i = 0; i < 4; i ++) {
			rightPlayer[i] = Game.spritesheet.getSprite(32 + (i * 16), 0, 16, 16);
		}
		
		for (int i = 0; i < 4; i++) {
			leftPlayer[i] = Game.spritesheet.getSprite(32 + (i * 16), 16, 16, 16);
		}
		
		for (int i = 0; i < 4; i++) {
			upPlayer[i] = Game.spritesheet.getSprite(32 + (i * 16), 32, 16, 16);
		}
		
		for (int i = 0; i < 4; i++) {
			downPlayer[i] = Game.spritesheet.getSprite(32 + (i * 16), 48, 16, 16);
		}
		
	}
	
	public void update() {
		moved = false;
		
		if (right && World.isFree((int)(x + speed), this.getY())) {
			moved = true;
			dir = right_dir;
			x += speed;
		}

		else if (left && World.isFree((int)(x - speed), this.getY())) {
			moved = true;
			dir = left_dir;
			x -= speed;
		}

		if (up && World.isFree(this.getX(), (int)(y - speed))) {
			moved = true;
			dir = up_dir;
			y -= speed;
		}

		else if (down && World.isFree(this.getX(), (int)(y + speed))) {
			moved = true;
			dir = down_dir;
			y += speed;
		}
		
		if (moved) {
			frames++;
			if (frames == maxFrames) {
				frames = 0;
				index++;
				
				if (index > maxIndex)
					index = 0;
			}
		}
		
		checkCollisionLifePack();
		checkCollisionAmmo();
		checkCollisionGun();
		
		if (isDamaged) {
			this.damageFrames++;
			if (this.damageFrames == 8) {
				this.damageFrames = 0;
				isDamaged = false;
			}
		}
		
		if (shootCooldown <= 0) {
			
			if (shoot) {
				shoot = false;

				if (hasGun && ammo > 0) {
					Sound.shoot.play();
					ammo--;
					
					int dx = 0;
					int dy = 0;
					
					int px = 0;
					int py = 9;
					
					if (dir == right_dir) {
						px = 20;
						dx = 1;
					} else if (dir == left_dir) {
						px = -8;
						dx = -1;
					}
					
					if (dir == up_dir) {
						dx = 0;
						dy = -1;
						
						px = 11;
						py = -1;
						
					} else if (dir == down_dir) {
						dx = 0;
						dy = 1;
						
						px = 11;
						py = 19;
						
					}
					
					if (dir == left_dir || dir == right_dir) {
						WeaponBullet bullet = new WeaponBullet(this.getX() + px, this.getY() + py, 4, 2, null, dx, dy);
						Game.bullets.add(bullet);
					}
					
					else {
						WeaponBullet bullet = new WeaponBullet(this.getX() + px, this.getY() + py, 2, 4, null, dx, dy);
						Game.bullets.add(bullet);
					}
					
					shootCooldown = maxShootCooldown;
				} else if (hasGun) {
					Sound.noBullets.play();
				}
				
			}
			
		} else {
			shootCooldown--;
		}
		
		/*if (mouseShoot && !moved) {
			if (hasGun && ammo > 0) {
				mouseShoot = false;
				
				// Atirando pra cima
				if (Game.mouseX >= 0
					&& Game.mouseX <= 960
					&& Game.mouseY >= 0
					&& Game.mouseY <= 270) {
					dir = up_dir;
					shoot = true;
				}
				
				// Atirando pra baixo
				if (Game.mouseX >= 0
					&& Game.mouseX <= 960
					&& Game.mouseY >= 420
					&& Game.mouseY <= 650) {
						dir = down_dir;
						shoot = true;	
				}
				
				// Atirando pra esquerda
				if (Game.mouseX >= 0
					&& Game.mouseX <= 500
					&& Game.mouseY >= 270
					&& Game.mouseY <= 410) {
						dir = left_dir;
						shoot = true;	
				}
				
				// Atirando pra direita
				if (Game.mouseX >= 500
					&& Game.mouseX <= 960
					&& Game.mouseY >= 270
					&& Game.mouseY <= 410) {
						dir = right_dir;
						shoot = true;	
				}
			}
		}*/
		
		if (life <= 0) {
			life = 0;
			Game.gameState = "GAME_OVER";
		}
		
		updateCamera();
		
	}
	
	public void updateCamera() {
		Camera.x = Camera.clamp(this.getX() - (Game.WIDTH/2), 0, World.WIDTH * 16 - Game.WIDTH);
		Camera.y = Camera.clamp(this.getY() - (Game.HEIGHT/2), 0, World.HEIGHT * 16 - Game.HEIGHT);
	}
	
	public void checkCollisionGun() {
		for (int i = 0; i < Game.entities.size(); i++) {
			Entity e = Game.entities.get(i);
			
			if (e instanceof Weapon) {
				if (Entity.isColliding(this, e)) {
					Sound.pickupItem.play();
					hasGun = true;
					Game.entities.remove(e);
					return;
				}
			}
		}
	}
	
	public void checkCollisionAmmo() {
		for (int i = 0; i < Game.entities.size(); i++) {
			Entity e = Game.entities.get(i);
			
			if (e instanceof Bullet) {
				if (Entity.isColliding(this, e)) {
					Sound.pickupItem.play();
					ammo += 8;
					Game.entities.remove(e);
					return;
				}
			}
		}
	}
	
	public void checkCollisionLifePack() {
		for (int i = 0; i < Game.entities.size(); i++) {
			Entity e = Game.entities.get(i);
			
			if (e instanceof Lifepack) {
				if (Entity.isColliding(this, e)) {
					if (life < 100) {
						Sound.pickupItem.play();
						life += 10;
						if (life >= 100)
							life = 100;
						Game.entities.remove(e);
						return;
					}
				}
			}
		}
	}
	
	public void render(Graphics g) {
		if (!isDamaged) {
			if (dir == right_dir) {
				g.drawImage(rightPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
				if (hasGun) {
					// Desenhar arma para direita.
					g.drawImage(Entity.GUN_RIGHT, this.getX() - Camera.x + 8, this.getY() - Camera.y + 2, null);
				}
			} else if (dir == left_dir) {
				g.drawImage(leftPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
				if (hasGun) {
					// Desenhar arma para esquerda.
					g.drawImage(Entity.GUN_LEFT, this.getX() - Camera.x - 8, this.getY() - Camera.y + 2, null);
				}
			}
			
			if (dir == up_dir) 
			{
				// hasGun por cima para renderizar por trás do personagem
				if (hasGun) 
				{
					// Desenhar arma para cima.
					g.drawImage(Entity.GUN_UP, this.getX() - Camera.x + 3, this.getY() - Camera.y - 2, null);
				}
				g.drawImage(upPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
			} 
			
			else if (dir == down_dir) 
			{
				g.drawImage(downPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
				if (hasGun) 
				{
					// Desenhar arma para baixo.
					g.drawImage(Entity.GUN_DOWN, this.getX() - Camera.x + 3, this.getY() - Camera.y + 8, null);
				}
			}
		}
		else {
			if (dir == right_dir)
				g.drawImage(playerDamageRight, this.getX() - Camera.x, this.getY() - Camera.y, null);
			else if (dir == left_dir)
				g.drawImage(playerDamageLeft, this.getX() - Camera.x, this.getY() - Camera.y, null);
			
			if (dir == up_dir)
				g.drawImage(playerDamageUp, this.getX() - Camera.x, this.getY() - Camera.y, null);
			else if (dir == down_dir)
				g.drawImage(playerDamageDown, this.getX() - Camera.x, this.getY() - Camera.y, null);
			
			if (hasGun) 
			{
				// GUN_DAMAGE LEFT e RIGHT
				if (dir == left_dir)
					g.drawImage(Entity.GUN_DAMAGE_LEFT, this.getX() - 8 - Camera.x, this.getY() - Camera.y + 2, null);
				else if (dir == right_dir)
					g.drawImage(Entity.GUN_DAMAGE_RIGHT, this.getX() + 9 - Camera.x, this.getY() - Camera.y + 2, null);
				
				// GUN_DAMAGE UP e DOWN
				if (dir == up_dir) 
				{
					g.drawImage(Entity.GUN_DAMAGE_UP, this.getX() - Camera.x + 3, this.getY() - Camera.y - 2, null);
					g.drawImage(playerDamageUp, this.getX() - Camera.x, this.getY() - Camera.y, null);
				}
				else if (dir == down_dir) 
					g.drawImage(Entity.GUN_DAMAGE_DOWN, this.getX() - Camera.x + 3, this.getY() - Camera.y + 9, null);
			}
		}
	}

}
