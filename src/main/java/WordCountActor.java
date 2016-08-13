import akka.actor.UntypedActor;

import java.io.Serializable;
import java.util.Map;

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
        public void onReceive(Object message) {
            if (message instanceof StartCounting){

            }
        }
    }
}
