package World;

import java.awt.image.BufferedImage;

import Entities.WeaponBullet;
import Main.Game;

public class WallTile extends Tile {

	public WallTile(int x, int y, BufferedImage sprite) {
		super(x, y, sprite);
	}
	
	public static void isCollidingWithBullet() {
		for (int i = 0; i < World.tiles.length; i++) {
			
			Tile tile = World.tiles[i];
			if (tile instanceof WallTile) {
				for (int j = 0; j < Game.bullets.size(); j++) {
					WeaponBullet bullet = Game.bullets.get(j);
					
					if (World.isColliding(bullet, tile)) {
						Game.bullets.remove(bullet);
						return;
					}
				}
			}
		}
	}

}
