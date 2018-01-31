package bg.uni.sofia.fmi.data.mining.project.lucene;

import org.apache.lucene.document.Document;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class DocumentCreator {

    public abstract Document createDocument(File file);

    public List<Document> createDocumentsFromDir(File dir) {
        List<Document> documents = new ArrayList<>();
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            for (File file : files) {
                if (file != null && !file.isDirectory()) {
                    documents.add(createDocument(file));
                }
            }
        }
        return documents;
    }
}
