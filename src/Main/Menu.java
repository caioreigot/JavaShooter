package Main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Menu {
	
	public static BufferedImage MENU_BACKGROUND;
	
	public Menu() {
		try {
			MENU_BACKGROUND = ImageIO.read(getClass().getResource("/menuBackground.png")).getSubimage(0, 0, 960, 640);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String[] options = {"jogar", "sair"};
	
	public int currentOption = 0;
	public int maxOption = options.length - 1;
	
	public boolean up, down, enter;
	
	public void update() {
		if (Game.mouseY >= 265 
			&& Game.mouseY <= 300
			&& Game.mouseX >= 410
			&& Game.mouseX <= 530) {
			Sound.menuSelect.play();
			currentOption = 0;
			
			Game.mouseX = 0;
			Game.mouseY = 0;
		}
		
		if (Game.mouseY >= 335 
				&& Game.mouseY <= 365
				&& Game.mouseX >= 420
				&& Game.mouseX <= 510) {
				Sound.menuSelect.play();
				currentOption = 1;
				
				Game.mouseX = 0;
				Game.mouseY = 0;
			}
		
		
		if (up) {
			Sound.menuSelect.play();
			up = false;
			currentOption--;
			if (currentOption < 0)
				currentOption = maxOption;
		}
		
		if (down) {
			Sound.menuSelect.play();
			down = false;
			currentOption++;
			
			if (currentOption > maxOption)
				currentOption = 0;
		}
		
		if (enter) {
			Sound.menuEnter.play();
			enter = false;
			if (options[currentOption] == "jogar") {
				Game.gameState = "PLAYING";
			} else if (options[currentOption] == "sair") {
				System.exit(1);
			}
		}
	}
	
	public void render (Graphics g) {
		g.drawImage(MENU_BACKGROUND, 0, 0, null); // Background do menu
		
		g.setColor(Color.yellow);
		g.setFont(new Font("arial", Font.BOLD, 50));
		
		// Titulo do jogo
		g.drawString("<\\ Java Shooter />", (Game.WIDTH * Game.SCALE) / 2 - 210, (Game.HEIGHT * Game.SCALE) / 2 - 100);
	
		// Opções do menu
		g.setColor(Color.white);
		g.setFont(new Font("arial", Font.BOLD, 40));
		
		g.drawString("Jogar", (Game.WIDTH * Game.SCALE) / 2 - 60, (Game.HEIGHT * Game.SCALE) / 2 - 20);
		g.drawString("Sair", (Game.WIDTH * Game.SCALE) / 2 - 45, (Game.HEIGHT * Game.SCALE) / 2 + 50);
		
		g.setColor(Color.yellow);
		if (options[currentOption] == "jogar") {
			g.drawString(">", (Game.WIDTH * Game.SCALE) / 2 - 90, (Game.HEIGHT * Game.SCALE) / 2 - 20);
		} else if (options[currentOption] == "sair") {
			g.drawString(">", (Game.WIDTH * Game.SCALE) / 2 - 76, (Game.HEIGHT * Game.SCALE) / 2 + 50);
		}
	
	}
	
}
