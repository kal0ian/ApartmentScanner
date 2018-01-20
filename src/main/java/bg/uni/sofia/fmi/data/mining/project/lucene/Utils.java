package bg.uni.sofia.fmi.data.mining.project.lucene;

import java.io.File;

public final class Utils {
    public static final String BULGARIAN_STOPWORDS_FILENAME = "bulgarianST.txt";
    private Utils(){}

    private File getStopWordsFileFromResources(){
        ClassLoader classLoader = getClass().getClassLoader();
        return new File(classLoader.getResource(BULGARIAN_STOPWORDS_FILENAME).getFile());
    }
}
