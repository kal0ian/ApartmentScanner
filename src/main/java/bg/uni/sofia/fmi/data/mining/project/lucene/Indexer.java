package bg.uni.sofia.fmi.data.mining.project.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Indexer {

    private IndexWriter indexWriter;
    private Directory indexDir;

    public Indexer(String dirPath) {
        indexDir = createIndexDir(dirPath);
        Analyzer analyzer = new StandardAnalyzer(); //add stopwords
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        indexWriter = createIndexWriter(indexDir, indexWriterConfig);
    }

    public void indexDocument(Document document) {
        try {
            indexWriter.addDocument(document);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void indexDocuments(List<Document> documents) {
        for (Document doc : documents) {
            indexDocument(doc);
        }
    }

    public Directory getIndexDir() {
        return indexDir;
    }

    public void close() {
        try {
            indexWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Directory createIndexDir(String dirPath) {
        Directory indexDir = null;
        try {
            if (dirPath != null) {
                indexDir = FSDirectory.open(new File(dirPath).toPath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return indexDir;
    }

    private IndexWriter createIndexWriter(Directory indexDir, IndexWriterConfig indexWriterConfig) {
        IndexWriter writer = null;
        try {
            writer = new IndexWriter(indexDir, indexWriterConfig);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return writer;
    }
}
