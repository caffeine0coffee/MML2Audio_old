package Note;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Exception.InvalidNoteNameException;
import Exception.InvalidScaleIndexException;

// ScaleIndex:
//      C:0, C#:1, D:2, D#:3, ... , B: 11

public class NoteNameConverter {
    private static HashMap<String, Integer> scaleMap = null;

    private static HashMap<String, Integer> getScaleMapInstance() {
        if (NoteNameConverter.scaleMap != null) {
            return NoteNameConverter.scaleMap;
        }
        
        NoteNameConverter.scaleMap = new HashMap<>();
        NoteNameConverter.scaleMap.put("C", 0);
        NoteNameConverter.scaleMap.put("D", 2);
        NoteNameConverter.scaleMap.put("E", 4);
        NoteNameConverter.scaleMap.put("F", 5);
        NoteNameConverter.scaleMap.put("G", 7);
        NoteNameConverter.scaleMap.put("A", 9);
        NoteNameConverter.scaleMap.put("B", 11);
        return NoteNameConverter.scaleMap;
    }

    public static int noteNameToScaleIndex(String noteName) throws InvalidNoteNameException {
        if (! Pattern.matches("^[A-Ga-gR][#+-]?$", noteName)) {
            throw new InvalidNoteNameException();
        }
        if (Pattern.matches("R", noteName)) {
            return 0;
        }

        String scaleName = String.valueOf(noteName.charAt(0));
        String semitoneName = "";
        Pattern pattern = Pattern.compile("^.([#+-])$");
        Matcher matcher = pattern.matcher(noteName);
        if (matcher.find()) {
            semitoneName = matcher.group(1);
        }

        int scaleIndex = NoteNameConverter.getScaleMapInstance().get(scaleName);
        if (semitoneName.equals("-")) {
            scaleIndex -= 1;
        }
        else if (semitoneName.equals("+") || semitoneName.equals("#")) {
            scaleIndex += 1;
        }

        return scaleIndex;
    }

    public static String scaleIndexToNoteName(int scaleIndex) throws InvalidScaleIndexException {
        boolean addSemitone = false;

        if (scaleIndex > 11) {
            throw new InvalidScaleIndexException("scale index is above 11");
        }

        if (! NoteNameConverter.getScaleMapInstance().containsValue(scaleIndex)) {
            scaleIndex -= 1;
            scaleIndex %= 12;
            addSemitone = true;
        }
        for (HashMap.Entry<String, Integer> ent: NoteNameConverter.getScaleMapInstance().entrySet()) {
            if (ent.getValue() == scaleIndex) {
                return ent.getKey() + (addSemitone ? "#" : "");
            }
        }

        throw new InvalidScaleIndexException();
    }
}