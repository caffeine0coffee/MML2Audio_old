import java.util.ArrayList;
import java.util.Arrays;

import javax.sound.sampled.AudioFormat;

import Channel.Channel;
import Note.Note;

public class Music {
    private AudioFormat audioFormat;
    private ArrayList<Channel> channelList = new ArrayList<>();
    private int bpm;
    private int maxVolumeValue;

    public static final int MIN_TONE_LENGTH = 64;
    public static final double SAMPLE_RATE = 44100.0;

    public Music() {
        this.audioFormat = new AudioFormat(
        (int)Music.SAMPLE_RATE,
            8,      // sample size in bit
            1,      // channels
            true,   // is signed
            true    // is Big Endian
        );
        this.maxVolumeValue = (int) Math.pow(2, this.audioFormat.getSampleSizeInBits()) - 1;
        this.bpm = 100;
    }

    public Music(ArrayList<Channel> channelList) {
        this();
        this.channelList = channelList;
    }

    private double calcLengthOfMusicInSecond() {
        double maxLength = -1;
        for (Channel c: this.channelList) {
            double length = 0;
            for (Note n: c.getNoteList()) {
                length += (4.0 / n.getToneLength()) * (60.0 / this.bpm);
            }
            maxLength = Math.max(maxLength, length);
        }

        return maxLength;
    }

    public ArrayList<Channel> getChannelList() {
        return this.channelList;
    }

    public void addChannel(Channel channel) {
        this.channelList.add(channel);
    }

    public int getNumberOfChannel() {
        return this.channelList.size();
    }

    public byte[] generateAudioBuffer() {
        int numOfByte = (int)Music.SAMPLE_RATE * (int)this.calcLengthOfMusicInSecond();
        numOfByte += Music.SAMPLE_RATE * 2.0;

        byte[] audioBuffer = new byte[numOfByte];
        Arrays.fill(audioBuffer, (byte)0);
        int attackTime = (int) (Music.SAMPLE_RATE * 0.02);
        int decreaseTime = (int) (Music.SAMPLE_RATE * 0.1);

        for (int c=0; c<this.getNumberOfChannel(); c++) {
            int phase = 0; // 波の位相
            int noteIndex = -1;
            double waveLength = 0;
            int noteLength = 0; // in [samples]
            int count = 0;
            int volume = 0;
            byte value = 0;
            Note note;
            for (int i=0; i<audioBuffer.length; i++) {
                if (count <= 0) {
                    noteIndex++;
                    if (noteIndex < channelList.get(c).getNoteList().size()) {
                        note = channelList.get(c).getNoteList().get(noteIndex);
                        waveLength = Music.SAMPLE_RATE / note.getFreq();
                        noteLength = (int) ((4.0 / note.getToneLength()) * (60.0 / bpm) * Music.SAMPLE_RATE);
                        volume = note.getVolume();
                        count = noteLength;
                        phase = 0;
                    }
                    else {
                        break;
                    }
                }

                double amp = this.maxVolumeValue * 0.5 * (volume / (double)Note.MAX_VOLUME);
                amp /= (double) this.getNumberOfChannel();
                value = (byte) (amp * Math.sin((phase/waveLength)*Math.PI*2));

                // 音のアタックを付ける
                if (phase < attackTime) {
                    value *= (phase / (double)attackTime);
                }
                // 音の終端で減衰させる
                if (count < decreaseTime) {
                    value *= (count / (double)decreaseTime);
                }

                audioBuffer[i] += value;

                count--;
                phase++;
            }
        }

        return audioBuffer;
    }

    public AudioFormat getAudioFormat() {
        return audioFormat;
    }

    public void setBpm(int bpm) {
        this.bpm = bpm;
    }
}
