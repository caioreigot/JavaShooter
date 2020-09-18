package Main;

import Entities.*;
import Graphics.*;
import World.*;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class Game extends Canvas implements Runnable, KeyListener, MouseListener {
	
	public static JFrame frame;
	private Thread thread;
	private boolean isRunning = true;
	
	public static final int WIDTH = 240;
	public static final int HEIGHT = 160;
	public static final int SCALE = 4;
	
	private int CUR_LEVEL = 1, MAX_LEVEL = 4;
	private final BufferedImage image;
	
	public static List<Entity> entities;
	public static List<Enemy> enemies;
	public static List<WeaponBullet> bullets; 
	public static Spritesheet spritesheet;
	
	public static World world;
	
	public static Player player;
	
	public static Random rand;
	
	public UI ui;
	
	public static String gameState = "MENU";
	private boolean showMessageGameOver = true;
	private int framesGameOver = 0;
	private boolean restartGame = false;
	
	public static int mouseX;
	public static int mouseY;
	
	public Menu menu;
	
	public Game() {
		addKeyListener(this);
		addMouseListener(this);
		rand = new Random();
		
		// Inicializando tela
		setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		initFrame();
		
		// Inicializando objetos
		ui = new UI();
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		entities = new ArrayList<Entity>();
		enemies = new ArrayList<Enemy>();
		bullets = new ArrayList<WeaponBullet>();
		
		spritesheet = new Spritesheet("/spritesheet.png");
		
		player = new Player(0, 0, 16, 16, spritesheet.getSprite(32, 0, 16, 16));
		entities.add(player);
		world = new World("/level1.png");
		
		menu = new Menu();
	}
	
	// Configurações da tela
	public void initFrame() {
		frame = new JFrame("Java Shooter");
		frame.add(this);
		frame.setResizable(false);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		// Ícone da janela
		Image icone = null;
		try {
			icone = ImageIO.read(getClass().getResource("/icon.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		frame.setIconImage(icone);
		
		// Cursor na tela
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Image cursor = toolkit.getImage(getClass().getResource("/icon.png"));
		Cursor c = toolkit.createCustomCursor(cursor, new Point(8, 8), "img");
	
		frame.setCursor(c);
	}
	
	public synchronized void start() {
		thread = new Thread(this);
		isRunning = true;
		thread.start();
	}
	
	public synchronized void stop() {
		isRunning = false;
		
		try {
			thread.join();
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(final String[] args) {
		final Game game = new Game();
		game.start();
	}
	
	// Atualizar jogo
	public void update() {
		if (gameState == "PLAYING") {
			for (int i = 0; i < entities.size(); i++) {
				final Entity e = entities.get(i);
				e.update();
			}
			
			for (int i = 0; i < bullets.size(); i++) {
				bullets.get(i).update();
			}
			
			if (enemies.size() == 0) {
				// Próximo level
				CUR_LEVEL++;
				if (CUR_LEVEL > MAX_LEVEL) {
					gameState = "MENU";
					CUR_LEVEL = 1;
				}
				String newWorld = "level"+CUR_LEVEL+".png";
				World.restartGame(newWorld);
			}
		} else if (gameState == "GAME_OVER") {
			this.framesGameOver++;
			
			if (this.framesGameOver == 30) {
				this.framesGameOver = 0;
				if (this.showMessageGameOver)
					this.showMessageGameOver = false;
				else
					this.showMessageGameOver = true;
			}
			
			if (restartGame) {
				restartGame = false;
				gameState = "PLAYING";
				
				CUR_LEVEL = 1;
				String newWorld = "level"+CUR_LEVEL+".png";
				World.restartGame(newWorld);
			}
		} else if (gameState == "MENU") {
			menu.update();
		}
	}
	
	// Renderizar (desenhar as informações na tela)
	public void render() {
		final BufferStrategy bs = this.getBufferStrategy();
		
		if (bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		
		Graphics g = image.getGraphics();
		
		// Limpando a tela com uma cor de background
		g.setColor(new Color(0, 0, 0));
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		/* Renderização do jogo */
		world.render(g);
		// Renderizando todas as entities
		for (int i = 0; i < entities.size(); i++) {
			final Entity e = entities.get(i);
			e.render(g);
		}
		
		for (int i = 0; i < bullets.size(); i++) {
			bullets.get(i).render(g);
		}
		
		ui.render(g);
		/* -------------------- */
		
		g.dispose(); // Só faz diferença na performance
		g = bs.getDrawGraphics();
		g.drawImage(image, 0, 0, WIDTH * SCALE, HEIGHT * SCALE, null);
		
		// UI das balas
		g.setFont(new Font("arial", Font.BOLD, 25));
		g.setColor(Color.white);
		g.drawString(player.ammo + "x", WIDTH * SCALE - 90, 43);
		g.drawImage(Entity.BULLET_EN, WIDTH * SCALE - 55, -12, 16 * SCALE, 16 * SCALE, null);
		
		if (gameState == "GAME_OVER") {
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(new Color(0, 0, 0, 160));
			g2.fillRect(0, 0, WIDTH * SCALE, HEIGHT * SCALE);
			
			g.setFont(new Font("arial", Font.BOLD, 34));
			g.setColor(Color.white);
			g.drawString("GAME OVER", (WIDTH * SCALE) / 2 - 85, (HEIGHT * SCALE) / 2 - 5);
			
			g.setFont(new Font("arial", Font.BOLD, 24));
			if (showMessageGameOver)
				g.drawString("> Pressione ENTER para reiniciar", (WIDTH * SCALE) / 2 - 175, (HEIGHT * SCALE) / 2 + 40);
				player.life = 0;
		} else if (gameState == "MENU") {
			menu.render(g);
		}
		
		bs.show();
	}
	
	public void run() {
		
		long lastTime = System.nanoTime();
		final double amountOfTicks = 60.0;
		final double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		
		// Debug (fps)
		// int frames = 0;
		double timer = System.currentTimeMillis();
		requestFocus();
		
		while(isRunning) {
			final long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			
			// Loop do update e render
			if (delta >= 1) {
				
				update();
				render();
				
				delta--;
			}
			
			if (System.currentTimeMillis() - timer >= 1000) {
				timer += 1000;
			}
		}
		
		stop();
	}

	@Override
	public void keyPressed(final KeyEvent e) {
		// Direita e esquerda (D, A, ArrowRight, ArrowLeft)
		if (e.getKeyCode() == KeyEvent.VK_RIGHT 
			|| e.getKeyCode() == KeyEvent.VK_D) {
			player.right = true;
		} else if (e.getKeyCode() == KeyEvent.VK_LEFT 
			|| e.getKeyCode() == KeyEvent.VK_A) {
			player.left = true;
		}
		
		// Cima e baixo (W, S, ArrowUp, ArrowDown)
		if (e.getKeyCode() == KeyEvent.VK_UP 
			|| e.getKeyCode() == KeyEvent.VK_W) {
			player.up = true;
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN 
			|| e.getKeyCode() == KeyEvent.VK_S) {
			player.down = true;
		}
 		
	}

	@Override
	public void keyReleased(final KeyEvent e) {
		// Direita e esquerda (D, A, ArrowRight, ArrowLeft)
		if (e.getKeyCode() == KeyEvent.VK_RIGHT 
			|| e.getKeyCode() == KeyEvent.VK_D) {
			player.right = false;
			
			// Voltando para a animação padrão
			player.index = 0;
		} else if (e.getKeyCode() == KeyEvent.VK_LEFT 
			|| e.getKeyCode() == KeyEvent.VK_A) {
			player.left = false;
			
			// Voltando para a animação padrão
			player.index = 3;
		}
		
		// Cima e baixo (W, S, ArrowUp, ArrowDown)
		if (e.getKeyCode() == KeyEvent.VK_UP 
			|| e.getKeyCode() == KeyEvent.VK_W) {
			player.up = false;
			
			// Voltando para a animação padrão
			player.index = 0;
			
			if (gameState == "MENU")
				menu.up = true;
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN || 
				e.getKeyCode() == KeyEvent.VK_S) {
			player.down = false;
			
			// Voltando para a animação padrão
			player.index = 0;
			
			if (gameState == "MENU")
				menu.down = true;
		}
		
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			player.shoot = true;
		}
		
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			if (gameState == "GAME_OVER")
				this.restartGame = true;
			if (gameState == "MENU")
				menu.enter = true;
		}
		
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			gameState = "MENU";
		}
	}
	
	@Override
	public void keyTyped(final KeyEvent e) {}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		player.shoot = true;
		
		mouseX = e.getX();
		mouseY = e.getY();
	}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

}
