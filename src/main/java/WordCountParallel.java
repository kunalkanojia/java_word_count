import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

class WordCountParallel {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        long startTime = System.nanoTime();
        WordCountParallel wcp = new WordCountParallel();
        wcp.getWordCount("src/main/resources/");
        long endTime = System.nanoTime();

        System.out.println("Completed in: " + (endTime - startTime)/1000000 + "ms");
    }

    public Map<String, Integer> getWordCount(String path) throws InterruptedException, ExecutionException {
        Map<String, Integer> resultMap = new HashMap<>();

        List<File> files = getFilesList(path);

        ExecutorService executor = Executors.newFixedThreadPool(16);

        List<Callable<WordCountResult>> callables = files.stream().map(this::getWordCountCallable).collect(Collectors.toList());
        List<Future<WordCountResult>> resultFutures = executor.invokeAll(callables);

        for(Future<WordCountResult> resultFuture: resultFutures){
            WordCountResult result = resultFuture.get();
            resultMap.put(result.getFileName(), result.getCount());
        }

        executor.shutdown();

        return resultMap;
    }

    Callable<WordCountResult> getWordCountCallable(File file) {
        return () -> getWordCount(file);
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

    private WordCountResult getWordCount(File file) {
        Path filePath = Paths.get(file.getAbsolutePath());
        int count = 0 ;
        try (BufferedReader reader = Files.newBufferedReader(filePath, UTF_8)) {
            count = reader.lines().filter(l -> !l.trim().isEmpty()).mapToInt(l -> l.split(" ").length).sum();
        } catch (IOException | UncheckedIOException e) {
            System.out.println(e.getMessage());
        }
        return new WordCountResult(file.getName(), count);
    }


}

class WordCountResult {
    private String fileName;
    private int count;

    public WordCountResult(String fileName, int count) {
        this.fileName = fileName;
        this.count = count;
    }

    public String getFileName() {
        return fileName;
    }

    public int getCount() {
        return count;
    }

}
