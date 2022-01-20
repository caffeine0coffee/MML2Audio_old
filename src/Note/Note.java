package Note;

import Exception.InvalidNoteNameException;
import Exception.InvalidScaleIndexException;

public class Note {
    public static final int MAX_VOLUME = 255;

    private double freq;
    private int scaleIndex;
    private int octave;
    private int toneLength;
    private int volume;  // 0 ~ MAX_VOLUME
    private String noteName;

    public Note(int toneLength, int octave, int volume) {
        this.octave = octave;
        this.toneLength = toneLength;
        this.volume = volume;
    }

    public Note(int scaleIndex, int toneLength, int octave, int volume) {
        this(toneLength, octave, volume);

        try {
            this.noteName = NoteNameConverter.scaleIndexToNoteName(scaleIndex);
        } catch (InvalidScaleIndexException e) {
            e.printStackTrace();
            System.exit(1);
        }

        this.scaleIndex = scaleIndex;
        calcFreq();
    }

    public Note(String noteName, int toneLength, int octave, int volume) {
        this(toneLength, octave, volume);

        try {
            this.scaleIndex = NoteNameConverter.noteNameToScaleIndex(noteName);
        } catch (InvalidNoteNameException e) {
            e.printStackTrace();
            System.exit(1);
        }

        this.noteName = noteName;
        calcFreq();
    }

    private void calcFreq() {
        int delta = this.octave - 4;
        int cIndex = this.scaleIndex - 9;
        this.freq = 440 * Math.pow(2, 1/12.0 * (cIndex + delta*12));
    }

    public double getFreq() {
        return this.freq;
    }

    public String getNoteName() {
        return this.noteName;
    }

    public int getToneLength() {
        return toneLength;
    }

    public int getVolume() {
        return volume;
    }

    @Override
    public String toString() {
        return "[ "
            + "'" + this.noteName + "', "
            + "index=" + this.scaleIndex + ", "
            + "octave=" + this.octave + ", "
            + "toneLength=" + this.toneLength + ", "
            + "freq=" + this.freq
            + "]";
    }
}
