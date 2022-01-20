import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Channel.Channel;
import Channel.ChannelBuilder;

public class MmlReader {
    private class Task implements Comparable<Task> {
        private Integer index;
        private Runnable task;

        protected Task(int index, Runnable task) {
            this.index = index;
            this.task = task;
        }

        @Override
        public int compareTo(Task t) {
            return this.index.compareTo(t.index);
        }

        protected void run() {
            this.task.run();
        }
    }

    private static HashMap<Pattern, BiConsumer<String, ChannelBuilder>> operationMap;

    private String readLineIgnoreComment(BufferedReader bufReader) throws IOException {
        String line;
        Pattern commentBeginPattern = Pattern.compile("/\\*");
        Pattern commentEndPattern   = Pattern.compile("\\*/");
        Matcher commentBeginMatcher, commentEndMatcher;

        line = bufReader.readLine();
        if (line == null) {
            return null;
        }

        // 単一行のコメントを除去する
        line = line.replaceAll("/\\*.*\\*/", "");

        // 複数行コメントを除去する
        commentBeginMatcher = commentBeginPattern.matcher(line);
        if (commentBeginMatcher.find()) {
            String notCommentLine = line.substring(0, commentBeginMatcher.start());

            while (true) {
                line = bufReader.readLine();
                if (line == null) {
                    break;
                }

                commentEndMatcher = commentEndPattern.matcher(line);
                if (commentEndMatcher.find()) {
                    line = line.substring(commentEndMatcher.end(), line.length());
                    line = notCommentLine + line;
                    break;
                }
            }
        }

        return line;
    }

    public Channel convertMmlToChannel(String channelMml) {
        ChannelBuilder builder = new ChannelBuilder(new Channel());

        try {
            BufferedReader bufReader = new BufferedReader(new StringReader(channelMml));
            String line;

            while (true) {
                line = bufReader.readLine();
                if (line == null) {
                    break;
                }

                PriorityQueue<Task> taskQueue = new PriorityQueue<>();
                for (HashMap.Entry<Pattern, BiConsumer<String, ChannelBuilder>> ent: MmlReader.getOperationMapInstance().entrySet()) {
                    boolean finish = false;
                    Matcher matcher = ent.getKey().matcher(line);
                    while (!finish) {
                        finish = true;
                        if (matcher.find()) {
                            // System.out.println("matched Pattern: " + ent.getKey());
                            // System.out.println("  argument: " + matcher.group(1));
                            // System.out.println("  start index: " + matcher.start());

                            String matcherGroup = matcher.groupCount()>0 ? matcher.group(1) : "";
                            String arg = new String(matcherGroup);
                            int start = matcher.start();
                            taskQueue.add(new Task(start,
                                ()->{ ent.getValue().accept(arg, builder); }));

                            finish = false;
                            if (matcher.hitEnd()) {
                                break;
                            }
                        }
                    }
                }

                while(!taskQueue.isEmpty()) {
                    taskQueue.poll().run();
                }
            }

            bufReader.close();
        }
        catch (FileNotFoundException e) {
            System.err.println("file not found");
            e.printStackTrace();
            System.exit(1);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        return builder.getChannel();
    }

    public void mmlCompiler(String filePath, Music music) {
        try {
            BufferedReader bufReader = new BufferedReader(new FileReader("test.mml"));
            String line;
            Pattern bpmConfigPattern = Pattern.compile("T(\\d*)");
            Matcher bpmConfigMatcher = null;
            Pattern channelDefPattern = Pattern.compile(":\\w*");
            Matcher channelDefMatcher = null;
            while (true) {
                line = this.readLineIgnoreComment(bufReader);
                if (line == null) {
                    break;
                }

                // BPMの設定
                bpmConfigMatcher = bpmConfigPattern.matcher(line);
                if (bpmConfigMatcher.find()) {
                    int bpm = Integer.parseInt(bpmConfigMatcher.group(1));
                    music.setBpm(bpm);
                }

                // チャンネル宣言
                channelDefMatcher = channelDefPattern.matcher(line);
                if (channelDefMatcher.find()) {
                    while (true) {
                        if (line == null) {
                            break;
                        }
                        String channelMml = "";
                        while (true) {
                            line = readLineIgnoreComment(bufReader);
                            if (line == null) {
                                break;
                            }
                            channelDefMatcher = channelDefPattern.matcher(line);
                            if (channelDefMatcher.find()) {
                                break;
                            }

                            channelMml += line + "\n";
                        }
                        music.addChannel(this.convertMmlToChannel(channelMml));
                    }
                }
            }

            bufReader.close();
        }
        catch (FileNotFoundException e) {
            System.err.println("file not found");
            e.printStackTrace();
            System.exit(1);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static HashMap<Pattern, BiConsumer<String, ChannelBuilder>> getOperationMapInstance() {
        if (MmlReader.operationMap != null) {
            return MmlReader.operationMap;
        }

        MmlReader.operationMap = new HashMap<Pattern, BiConsumer<String, ChannelBuilder>>();
        // ノート表現
        MmlReader.operationMap.put(Pattern.compile("(\\d*[A-Ga-g][#+-]?)"), // 8A 16F など
            (String arg, ChannelBuilder cb)->{
                cb.addNote(arg);
            });
        // オクターブ
        MmlReader.operationMap.put(Pattern.compile("O(\\d+)"),  // O<number>
            (String arg, ChannelBuilder cb)->{
                cb.setCurrentOctave(Integer.parseInt(arg));
            });
        MmlReader.operationMap.put(Pattern.compile("<"),    // <
            (String arg, ChannelBuilder cb)->{
                cb.addToCurrentOctave(1);
            });
        MmlReader.operationMap.put(Pattern.compile(">"),    // >
            (String arg, ChannelBuilder cb)->{
                cb.addToCurrentOctave(-1);
            });
        // 音量
        MmlReader.operationMap.put(Pattern.compile("V(\\d+)"),  //  V<number>
            (String arg, ChannelBuilder cb)->{
                cb.setCurrentVolume(Integer.parseInt(arg));
            });
        MmlReader.operationMap.put(Pattern.compile("\\((\\d+)"),    // (<number>
            (String arg, ChannelBuilder cb)->{
                cb.addToCurrentVolume(Integer.parseInt(arg));;
            });
        MmlReader.operationMap.put(Pattern.compile("\\)(\\d+)"),    // )<number>
            (String arg, ChannelBuilder cb)->{
                cb.addToCurrentVolume(- Integer.parseInt(arg));;
            });
        // デフォルト音長
        MmlReader.operationMap.put(Pattern.compile("L(\\d+)"),  //  L<number>
            (String arg, ChannelBuilder cb)->{
                cb.setCurrentDefaultToneLength(Integer.parseInt(arg));
            });

        // template
        // MmlReader.operationMap.put(Pattern.compile("regex"),
        //     (String arg, ChannelBuilder cb)->{
        //         // Do something
        //     });

        return MmlReader.operationMap;
    }
}
