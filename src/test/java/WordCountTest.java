import org.hamcrest.collection.IsMapContaining;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

public class WordCountTest {

    @Test
    public void testWordCount(){
        WordCount wc = new WordCount();
        Map<String, Integer> results = wc.getWordCount("src/main/resources/");
        assertEquals("Should find three files", 20, results.size());
        assertThat(results, IsMapContaining.hasEntry("File1.txt", 6480000));
        assertThat(results, IsMapContaining.hasEntry("File2.txt", 6480000));
    }

}