package bg.uni.sofia.fmi.data.mining.project.rest;

import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;

import bg.uni.sofia.fmi.data.mining.project.lucene.ApartmentDocumentCreator;
import bg.uni.sofia.fmi.data.mining.project.lucene.Indexer;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.spell.Dictionary;
import org.apache.lucene.search.spell.PlainTextDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletContextEvent;

@WebListener
public class ContextLoader implements ServletContextListener{

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		try {
			indexApartments();
			indexSpellcheckDictionary();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Auto-generated method stub
	}

	private void indexApartments(){
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

	private void indexSpellcheckDictionary() throws IOException {
		File dir = new File(Constants.SPELLCHECK_INDEX_DIRECTORY);
		Directory directory = FSDirectory.open(dir.toPath());
		SpellChecker spellchecker = new SpellChecker(directory);
		Dictionary dic = new PlainTextDictionary(new File(Constants.SPELLCHECK_DICTIONARY).toPath());
		spellchecker.indexDictionary(dic,new IndexWriterConfig(new StandardAnalyzer()),true);
		spellchecker.close();

	}
	
}
