package bg.uni.sofia.fmi.data.mining.project.lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, ParseException, InterruptedException {
        Indexer indexer = new Indexer("C:\\ApartmentIndex");
        ApartmentDocumentCreator apartmentDocumentCreator = new ApartmentDocumentCreator();
        List<Document> documents = apartmentDocumentCreator.createDocumentsFromDir(new File("C:\\ApartmentScanner_copy"));
        indexer.indexDocuments(documents);
        indexer.close();
        Thread.sleep(1000);
        Searcher.search(indexer.getIndexDir(),"соларен бойлер");
        //Searcher.search(FSDirectory.open(new File("C:\\ApartmentIndex").toPath()),"соларен бойлер");


    }
}
