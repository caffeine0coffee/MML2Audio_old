package Channel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Note.Note;

public class ChannelBuilder {
    private Channel channel;
    private int currentVolume;
    private int currentOctave;
    private int currentDefaultToneLength;

    public ChannelBuilder(Channel channel) {
        this.channel = channel;
        this.currentOctave = 4;
        this.currentVolume = 200;
        this.currentDefaultToneLength = 4;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public void setCurrentVolume(int currentVolume) {
        this.currentVolume = currentVolume;
        // System.out.println("set Volume to: " + this.currentVolume);
    }

    public void addToCurrentVolume(int amount) {
        this.currentVolume += amount;
        // System.out.println("Volume added " + amount + ", current Volume: " + this.currentVolume);
    }

    public void setCurrentOctave(int currentOctave) {
        this.currentOctave = currentOctave;
        // System.out.println("set Octave to: " + this.currentOctave);
    }

    public void addToCurrentOctave(int amount) {
        this.currentOctave += amount;
        // System.out.println("Octave added " + amount + ", current Octave: " + this.currentOctave);
    }

    public void setCurrentDefaultToneLength(int currentDefaultToneLength) {
        this.currentDefaultToneLength = currentDefaultToneLength;
        // System.out.println("set Default Tone Length to: " + this.currentDefaultToneLength);
    }

    public Channel getChannel() {
        return this.channel;
    }
    
    public void addNote(String noteExpr) {
        Pattern pt = Pattern.compile("(\\d+)(.*)");
        Matcher mc = pt.matcher(noteExpr);
        Note note = null;

        if (mc.find()) {
            note = new Note(mc.group(2), Integer.parseInt(mc.group(1)), this.currentOctave, this.currentVolume);
        }
        else {
            note = new Note(noteExpr, this.currentDefaultToneLength, this.currentOctave, this.currentVolume);
        }
        this.channel.addNote(note);

        // System.out.println("Note added: " + note);
    }

}
