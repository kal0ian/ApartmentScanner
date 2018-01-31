package bg.uni.sofia.fmi.data.mining.project.lucene;

import bg.uni.sofia.fmi.data.mining.project.utils.ResourcesUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class Searcher {
    private Searcher() {
    }

    public static List<String> search(Directory indexDir, String queryString) throws IOException, ParseException {
        IndexReader indexReader = DirectoryReader.open(indexDir);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        Analyzer analyzer = new StandardAnalyzer(new ResourcesUtils().getStopWordsFileFromResources());
        MultiFieldQueryParser queryParser = new MultiFieldQueryParser(
                new String[] { "title", "content"},
                analyzer);
        //QueryParser queryParser = new QueryParser("content",
        System.out.println("!!! BEFORE PARSE: " + queryString);
        Query query = queryParser.parse(QueryParser.escape(queryString));
        TopDocs topDocs = indexSearcher.search(query, 200);
        List<String> resultFilesPaths = new ArrayList<>();
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document doc = indexSearcher.doc(scoreDoc.doc);
            //System.out.println(doc.get("path_to_file") +" score: "+ scoreDoc.score);
            //System.out.println(indexSearcher.explain(query, scoreDoc.doc));
            resultFilesPaths.add(doc.get("path_to_file")); //parameter
        }
        indexReader.close();
        return resultFilesPaths;
    }

}
