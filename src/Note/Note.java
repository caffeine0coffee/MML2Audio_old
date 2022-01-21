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
    private int waveGeneratorId;
    private String noteName;

    public Note(int toneLength, int octave, int volume, int waveGeneratorId) {
        this.octave = octave;
        this.toneLength = toneLength;
        this.volume = volume;
        this.waveGeneratorId = waveGeneratorId;
    }

    public Note(int scaleIndex, int toneLength, int octave, int volume, int waveGeneratorId) {
        this(toneLength, octave, volume, waveGeneratorId);

        try {
            this.noteName = NoteNameConverter.scaleIndexToNoteName(scaleIndex);
        } catch (InvalidScaleIndexException e) {
            e.printStackTrace();
            System.exit(1);
        }

        this.scaleIndex = scaleIndex;
        calcFreq();
    }

    public Note(String noteName, int toneLength, int octave, int volume, int waveGeneratorId) {
        this(toneLength, octave, volume, waveGeneratorId);

        try {
            this.scaleIndex = NoteNameConverter.noteNameToScaleIndex(noteName);
        } catch (InvalidNoteNameException e) {
            e.printStackTrace();
            System.exit(1);
        }

        this.noteName = noteName;
        calcFreq();

        if (noteName.equals("R")) {
            this.volume = 0;
        }
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

    public int getWaveGeneratorId() {
        return this.waveGeneratorId;
    }

    @Override
    public String toString() {
        return "[ "
            + "'" + this.noteName + "', "
            + "index=" + this.scaleIndex + ", "
            + "octave=" + this.octave + ", "
            + "toneLength=" + this.toneLength + ", "
            + "freq=" + this.freq + ", "
            + "wave=" + this.waveGeneratorId + " "
            + "]";
    }
}
