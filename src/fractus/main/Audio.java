package fractus.main;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Audio {
	private AudioInputStream soundStream;
	private Clip soundClip;
	
	public void playReceive() {
		if (soundClip == null) { return; }
		soundClip.stop();
		soundClip.setFramePosition(0);
		soundClip.start();
	}
	
	public void initSound() {
		try { 
			File audioFile = new File( "lib/receive.wav" );
			soundStream = AudioSystem.getAudioInputStream( audioFile );
			AudioFormat streamFormat = soundStream.getFormat( );
			DataLine.Info clipInfo = new DataLine.Info( Clip.class, streamFormat );
			try {
				soundClip = ( Clip ) AudioSystem.getLine( clipInfo );
				soundClip.open( soundStream );
			} catch (Exception e) {
				Logger.getAnonymousLogger().log(Level.INFO,"fractus: could not initialize sound. running silently.");
				soundClip = null;
				//e.printStackTrace();
			}
		}
		catch ( UnsupportedAudioFileException e ) { }
		catch ( IOException e ) { }
	}
}
