package bg.uni.sofia.fmi.data.mining.project.lucene;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
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
        QueryParser queryParser = new QueryParser("content", new StandardAnalyzer());
        Query query = queryParser.parse(queryString);
        TopDocs topDocs = indexSearcher.search(query, 20);
        List<String> resultFilesPaths = new ArrayList<>();
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document doc = indexSearcher.doc(scoreDoc.doc);
            System.out.println(doc.get("path_to_file") +" score: "+ scoreDoc.score);
            System.out.println(indexSearcher.explain(query, scoreDoc.doc));
            resultFilesPaths.add(doc.get("path_to_file")); //parameter
        }
        indexReader.close();
        return resultFilesPaths;
    }

}
