package bg.uni.sofia.fmi.data.mining.project.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

public final class ResourcesUtils {
    private static final String BULGARIAN_STOPWORDS_FILENAME = "bulgarianST.txt";
    private static final String BULGARIAN_APARTMENT_DICTIONARY_FILENAME = "bg_apartment_dictionary";

    private ClassLoader classLoader;

    public ResourcesUtils() {
        classLoader = getClass().getClassLoader();
    }
    
    public Reader getStopWordsFileFromResources() throws FileNotFoundException {
        return new FileReader(new File(classLoader.getResource(BULGARIAN_STOPWORDS_FILENAME).getFile()));
    }

    public File getApartmentDictionary() throws FileNotFoundException {
        return new File(classLoader.getResource(BULGARIAN_APARTMENT_DICTIONARY_FILENAME).getFile());
    }
}
