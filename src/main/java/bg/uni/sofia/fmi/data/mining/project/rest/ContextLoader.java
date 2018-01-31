package bg.uni.sofia.fmi.data.mining.project.rest;

import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import bg.uni.sofia.fmi.data.mining.project.utils.ResourcesUtils;
import bg.uni.sofia.fmi.data.mining.project.utils.Constants;
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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;

@WebListener
public class ContextLoader implements ServletContextListener{

	private static final Logger LOGGER = Logger.getLogger(ContextLoader.class.getName());

	@Override
	public void contextInitialized(ServletContextEvent sce) {
			indexApartments();
			indexSpellcheckDictionary();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// TODO
	}

	private void indexApartments() {
		Indexer indexer = null;
		try {
			indexer = new Indexer(Constants.INDEX_DIRECTORY);
			ApartmentDocumentCreator apartmentDocumentCreator = new ApartmentDocumentCreator();
			List<Document> documents = apartmentDocumentCreator.createDocumentsFromDir(new File(Constants.DOCUMENTS_DIRECTORY));
			indexer.indexDocuments(documents);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Error while indexing apartments", e);
		} finally {
			if(null != indexer) {
				indexer.close();
			}
		}
	}

	private void indexSpellcheckDictionary() {
		SpellChecker spellchecker = null;
		try {
			File dir = new File(Constants.SPELLCHECK_INDEX_DIRECTORY);
			Directory directory = FSDirectory.open(dir.toPath());
			spellchecker = new SpellChecker(directory);
			Dictionary dic = new PlainTextDictionary(new ResourcesUtils().getApartmentDictionary().toPath());
			spellchecker.indexDictionary(dic, new IndexWriterConfig(new StandardAnalyzer()), true);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Error while indexing spellcheck dictionary", e);
		} finally {
			try {
				if(null != spellchecker) {
					spellchecker.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
