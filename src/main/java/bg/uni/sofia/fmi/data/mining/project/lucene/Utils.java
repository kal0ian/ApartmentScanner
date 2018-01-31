package bg.uni.sofia.fmi.data.mining.project.lucene;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

public final class Utils {
    public static final String BULGARIAN_STOPWORDS_FILENAME = "bulgarianST.txt";
    public static final String BULGARIAN_APARTMENT_DICTIONARY_FILENAME = "bg_apartment_dictionary";
    public static final String APARTMENT_DOCUMENTS_DIRECTORY = "apartment_documents";
    private ClassLoader classLoader;
    public Utils() {
        classLoader = getClass().getClassLoader();
    }
    
    public Reader getStopWordsFileFromResources() throws FileNotFoundException {
        return new FileReader(new File(classLoader.getResource(BULGARIAN_STOPWORDS_FILENAME).getFile()));
    }

    public File getApartmentDictionary() throws FileNotFoundException {
        return new File(classLoader.getResource(BULGARIAN_APARTMENT_DICTIONARY_FILENAME).getFile());
    }

    public File getApartmentDocumentsDirectory() throws FileNotFoundException {
        return new File(classLoader.getResource(APARTMENT_DOCUMENTS_DIRECTORY).getFile());
    }
}
