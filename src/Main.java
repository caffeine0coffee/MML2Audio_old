import java.io.ByteArrayInputStream;
import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioFileFormat.Type;

public class Main {
    public static void main(String[] args) throws Exception {
        Music music = new Music();
        MmlReader mmlReader = new MmlReader();

        mmlReader.mmlCompiler("test.mml", music);

        byte[] buffer = music.generateAudioBuffer();
        ByteArrayInputStream binput = new ByteArrayInputStream(buffer);
        AudioInputStream audioInputStream = new AudioInputStream(binput, music.getAudioFormat(), buffer.length);
        AudioSystem.write(audioInputStream, Type.WAVE, new File("output.wav"));

        audioInputStream.close();
        binput.close();
    }
}
