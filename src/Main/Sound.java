package Main;

import java.applet.Applet;
import java.applet.AudioClip;

public class Sound {
	
	private AudioClip clip;
	
	public static final Sound menuSelect = new Sound("/menu_select.wav");
	public static final Sound menuEnter = new Sound("/enterMenu.wav");
	public static final Sound hurtEffect = new Sound("/hurt.wav");
	public static final Sound shoot = new Sound("/shoot.wav");
	public static final Sound noBullets = new Sound("/noBullets.wav");
	public static final Sound pickupItem = new Sound("/pickup_item.wav");
	
	private Sound(String name) {
		try {
			clip = Applet.newAudioClip(Sound.class.getResource(name));
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	public void play() {
		try {
			new Thread() {
				public void run() {
					clip.play();
				}
			}.start();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	public void loop() {
		try {
			new Thread() {
				public void run() {
					clip.loop();
				}
			}.start();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
}
