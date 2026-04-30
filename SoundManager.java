// for playing sound clips
import java.io.*;
import java.util.HashMap;
import javax.sound.sampled.*;				// for storing sound clips


public class SoundManager {				// a Singleton class
	HashMap<String, Clip> clips;

   	Clip hitClip = null;				// played when bat hits ball
   	Clip appearClip = null;				// played when ball is re-generated
   	Clip backgroundClip = null;			// played continuously after ball is created

	private static SoundManager instance = null;	// keeps track of Singleton instance

	private SoundManager () {
		Clip clip;
		clips = new HashMap<String, Clip>();
		
		//Clip clip = loadClip("sounds/background.wav");
		//clips.put("background", clip);		// background theme sound

		//clip = loadClip("sounds/hitSound.wav");
		//clips.put("hit", clip);			// played when player's sprite collides 
							//   with another sprice

		clip = loadClip("sounds/appearSound.wav");
		clips.put("appear", clip);		// played when a special sprite 
							//   makes an appearance

		clip = loadClip("sounds/BirdSound.wav");
		clips.put("birdSound", clip);		// played for bird-flying animation

		clip = loadClip("sounds/epicBackground.wav");
		clips.put("background", clip);	//background music
		clip = loadClip("sounds/boxeffect1.wav");
		clips.put("boxeffect1", clip);
		clip = loadClip("sounds/boxeffect2.wav");
		clips.put("boxeffect2", clip);
		clip = loadClip("sounds/collectCoin.wav");
		clips.put("coin", clip);
		clip = loadClip("sounds/correctLetter.wav");
		clips.put("correct", clip);
		clip = loadClip("sounds/died.wav");
		clips.put("died", clip);
		clip = loadClip("sounds/fall.wav");
		clips.put("fall", clip);
		clip = loadClip("sounds/hit.wav");
		clips.put("hit", clip);
		clip = loadClip("sounds/lifeLost.wav");
		clips.put("lostlife", clip);
		clip = loadClip("sounds/loadIn.wav");
		clips.put("loadIn", clip);
		clip = loadClip("sounds/mysteryBox.wav");
		clips.put("box", clip);
		clip = loadClip("sounds/wrongLetter.wav");
		clips.put("wrong", clip);
	}


	public static SoundManager getInstance() {	// class method to get Singleton instance
		if (instance == null)
			instance = new SoundManager();
		
		return instance;
	}		


	public Clip getClip (String title) {

		return clips.get(title);		// gets a sound by supplying key
	}


    	public Clip loadClip (String fileName) {	// gets clip from the specified file
 		AudioInputStream audioIn;
		Clip clip = null;

		try {
    			File file = new File(fileName);
    			audioIn = AudioSystem.getAudioInputStream(file.toURI().toURL()); 
    			clip = AudioSystem.getClip();
    			clip.open(audioIn);
		}
		catch (Exception e) {
 			System.out.println ("Error opening sound files: " + e);
		}
    		return clip;
    	}


    	public void playSound(String title, Boolean looping) {
		Clip clip = getClip(title);
		if (clip != null) {
			clip.setFramePosition(0);
			if (looping)
				clip.loop(Clip.LOOP_CONTINUOUSLY);
			else
				clip.start();
		}
    	}


    	public void stopSound(String title) {
		Clip clip = getClip(title);
		if (clip != null) {
			clip.stop();
		}
    	}

}