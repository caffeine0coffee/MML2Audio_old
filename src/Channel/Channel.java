package Channel;

import java.util.ArrayList;

import Note.Note;

public class Channel {
    private ArrayList<Note> noteList = new ArrayList<>();

    public Channel(ArrayList<Note> noteList) {
        this.noteList = noteList;
    }

    public Channel() { }

    public ArrayList<Note> getNoteList() {
        return noteList;
    }

    public void addNote(Note note) {
        this.noteList.add(note);
    }
}
