import org.hamcrest.collection.IsMapContaining;
import org.junit.Test;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

public class WordCountParallelTest {

    @Test
    public void testWordCount() throws ExecutionException, InterruptedException {
        WordCountParallel wcp = new WordCountParallel();
        Map<String, Integer> results = wcp.getWordCount("src/main/resources/");
        assertEquals("Should find three files", 20, results.size());
        assertThat(results, IsMapContaining.hasEntry("File1.txt", 6480000));
        assertThat(results, IsMapContaining.hasEntry("File2.txt", 6480000));
    }

}