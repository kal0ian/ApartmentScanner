package bg.uni.sofia.fmi.data.mining.project.lucene;

import bg.uni.sofia.fmi.data.mining.project.rest.Constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

public final class Utils {

    private ClassLoader classLoader;
    public Utils() {
        classLoader = getClass().getClassLoader();
    }
    
    public Reader getStopWordsFileFromResources() throws FileNotFoundException {
        return new FileReader(new File(classLoader.getResource(Constants.BULGARIAN_STOPWORDS_FILENAME).getFile()));
    }

    public File getApartmentDictionary() throws FileNotFoundException {
        return new File(classLoader.getResource(Constants.BULGARIAN_APARTMENT_DICTIONARY_FILENAME).getFile());
    }

    public File getApartmentDocumentsDirectory() throws FileNotFoundException {
        return new File(classLoader.getResource(Constants.APARTMENT_DOCUMENTS_DIRECTORY).getFile());
    }
}
