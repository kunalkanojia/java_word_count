import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

class WordCount {

    public static void main(String[] args) {
        long startTime = System.nanoTime();
        WordCount wc = new WordCount();
        wc.getWordCount("src/main/resources/");
        long endTime = System.nanoTime();

        System.out.println((endTime - startTime)/1000000 + "ms");
    }

    public Map<String, Integer> getWordCount(String path) {
        Map<String, Integer> resultMap = new HashMap<>();
        List<File> files = getFilesList(path);

        for(File file: files){
            Integer count = getWordCount(file);
            resultMap.put(file.getName(), count);
        }

        return resultMap;
    }

    private List<File> getFilesList(String rootPath) {
        List<File> results = new ArrayList<>();
        File[] files = new File(rootPath).listFiles();

        for (File file : files != null ? files : new File[0]) {
            if (file.isFile()) {
                results.add(file);
            }
        }

        return results;
    }

    private Integer getWordCount(File file){
        int count=0;
        try(Scanner sc = new Scanner(file)){
            while(sc.hasNext()){
                sc.next();
                count++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return count;
    }


}
