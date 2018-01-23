package bg.uni.sofia.fmi.data.mining.project.rest;

import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.lucene.document.Document;

import bg.uni.sofia.fmi.data.mining.project.lucene.ApartmentDocumentCreator;
import bg.uni.sofia.fmi.data.mining.project.lucene.Indexer;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletContextEvent;

@WebListener
public class ContextLoader implements ServletContextListener{

	@Override
	public void contextInitialized(ServletContextEvent sce) {
        Indexer indexer = null;
		try {
			indexer = new Indexer(Constants.INDEX_DIRECTORY);
	        ApartmentDocumentCreator apartmentDocumentCreator = new ApartmentDocumentCreator();
	        List<Document> documents = apartmentDocumentCreator.createDocumentsFromDir(new File(Constants.DOCUMENTS_DIRECTORY));
	        indexer.indexDocuments(documents);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			indexer.close();
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Auto-generated method stub
	}
	
}
