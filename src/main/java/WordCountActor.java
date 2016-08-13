import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public class WordCountActor {

    public static class FileToCount implements Serializable {
        public final String fileName;

        public FileToCount(String fileName) {
            this.fileName = fileName;
        }
    }

    public static class WordCount implements Serializable {
        public final String fileName;
        public final int count;

        public WordCount(String fileName, int count) {
            this.fileName = fileName;
            this.count = count;
        }
    }

    public static class StartCounting implements Serializable {
        public final String docRoot;
        public final int numActors;

        public StartCounting(String docRoot, int numActors) {
            this.docRoot = docRoot;
            this.numActors = numActors;
        }
    }

    public static class WordCountSuccess implements Serializable {
        public final Map<String, Integer> result;

        public WordCountSuccess(Map<String, Integer> result) {
            this.result = result;
        }
    }


    public static class WordCountMaster extends UntypedActor {
        List<String> files;
        Map<String, Integer> wordCount = new HashMap<>();
        ActorRef requester;


        public void onReceive(Object message) {
            if (message instanceof StartCounting) {
                requester = sender();
                StartCounting scm = (StartCounting) message;
                List<ActorRef> workers = createWorkers(scm.numActors);
                files = getFilesList(scm.docRoot);
                beginSorting(files, workers);
            } else if (message instanceof WordCount){
                WordCount wc = (WordCount) message;
                wordCount.put(wc.fileName, wc.count);
                if(wordCount.size() == files.size()) {
                    requester.tell(new WordCountSuccess(wordCount), self());
                }
            }
        }

        private List<ActorRef> createWorkers(int numActors) {
            List<ActorRef> actorRefs = new ArrayList<>();
            for (int i = 0; i < numActors; i++) {
                actorRefs.add(context().actorOf(Props.create(WordCountWorker.class), "wc-worker-" + i));
            }

            return actorRefs;
        }

        private List<String> getFilesList(String rootPath) {
            List<String> results = new ArrayList<>();
            File[] files = new File(rootPath).listFiles();

            for (File file : files != null ? files : new File[0]) {
                if (file.isFile()) {
                    results.add(file.getPath());
                }
            }
            return results;
        }

        private void beginSorting(List<String> fileNames, List<ActorRef> workers) {
            for (int i = 0; i < fileNames.size(); i++) {
                workers.get(i % workers.size()).tell(new FileToCount(fileNames.get(i)), self());
            }
        }
    }

    public static class WordCountWorker extends UntypedActor {

        @Override
        public void onReceive(Object message) throws Throwable {
            if (message instanceof FileToCount) {
                FileToCount ftc = (FileToCount) message;
                int count = getWordCount(ftc.fileName);
                sender().tell(new WordCount(ftc.fileName, count), self());
            }

        }

        private Integer getWordCount(String fileName) {
            Path filePath = Paths.get(fileName);
            int count = 0 ;
            try (BufferedReader reader = Files.newBufferedReader(filePath, UTF_8)) {
                count = reader.lines().filter(l -> !l.trim().isEmpty()).mapToInt(l -> l.split(" ").length).sum();
            } catch (IOException | UncheckedIOException e) {
                System.out.println(e.getMessage());
            }
            return count;
        }

    }
}


