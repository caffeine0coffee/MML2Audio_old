package Channel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Note.Note;

public class ChannelBuilder {
    private Channel channel;
    private int currentVolume;
    private int currentOctave;
    private int currentDefaultToneLength;
    private int currentWaveGeneratorId;

    public ChannelBuilder(Channel channel) {
        this.channel = channel;
        this.currentOctave = 4;
        this.currentVolume = 200;
        this.currentDefaultToneLength = 4;
        this.currentWaveGeneratorId = 0;
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

    public void setCurrentWaveGeneratorId(int id) {
        this.currentWaveGeneratorId = id;
    }

    public Channel getChannel() {
        return this.channel;
    }
    
    public void addNote(String noteExpr) {
        Pattern noteLengthPattern = Pattern.compile("(\\d+)(.*)");
        Matcher noteLengthMatcher = noteLengthPattern.matcher(noteExpr);
        Note note = null;

        if (noteLengthMatcher.find()) {
            note = new Note(noteLengthMatcher.group(2), Integer.parseInt(noteLengthMatcher.group(1)), this.currentOctave, this.currentVolume, this.currentWaveGeneratorId);
        }
        else {
            note = new Note(noteExpr, this.currentDefaultToneLength, this.currentOctave, this.currentVolume, this.currentWaveGeneratorId);
        }
        this.channel.addNote(note);

        // System.out.println("Note added: " + note);
    }
}
