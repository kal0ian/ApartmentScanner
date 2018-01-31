package bg.uni.sofia.fmi.data.mining.project.rest;

import bg.uni.sofia.fmi.data.mining.project.lucene.*;
import bg.uni.sofia.fmi.data.mining.project.utils.Constants;
import bg.uni.sofia.fmi.data.mining.project.utils.ResourcesUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Path("/")
public class Endpoint {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/search")
    public List<ResultApartment> findResults(@QueryParam("searchText") String searchText, @QueryParam("address") String address, @QueryParam("areaFrom") String areaFrom, @QueryParam("areaTo") String areaTo, @QueryParam("floorFrom") String floorFrom, @QueryParam("floorTo") String floorTo) throws IOException, ParseException {
//        if (address == null || "".equals(address)) {
//            return search(searchText);
//        }
        return search(searchText, address, areaFrom, areaTo, floorFrom, floorTo);
    }

    private List<ResultApartment> search(String searchText) throws IOException, ParseException {
        File indexDir = new File(Constants.INDEX_DIRECTORY);
        List<String> resultFilesPaths = Searcher.search(FSDirectory.open(indexDir.toPath()), searchText);
        List<ResultApartment> results = new ArrayList<>();
        for (String pathToFile : resultFilesPaths) {
            results.add(createResultApartment(parse(new File(pathToFile))));
        }
        return results;
    }

    private List<ResultApartment> search(String searchText, String address, String areaFrom, String areaTo, String floorFrom, String floorTo) throws IOException, ParseException {
        File indexDir = new File(Constants.INDEX_DIRECTORY);
        IndexReader indexReader = DirectoryReader.open(FSDirectory.open(indexDir.toPath()));
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        Analyzer analyzer = new StandardAnalyzer(new ResourcesUtils().getStopWordsFileFromResources());

        //MultiFieldQueryParser queryParser = new MultiFieldQueryParser(
        //       new String[]{"title", "content"},
        //        analyzer);
        //QueryParser queryParser = new QueryParser("floor",analyzer);

        // if floorFrom contains "Parter"


        //String special = "title:" + searchText + " OR content:" + searchText + " OR address:" + address + " AND area:[" + areaFrom
        //        + " TO " + areaTo + "] AND floor:[" + floorFrom + " TO " + floorTo + "]";

        BooleanQuery.Builder booleanQueryBuilder = new BooleanQuery.Builder();
        if(null != searchText && !"".equals(searchText)){
            Query titleQuery = new QueryParser("title", analyzer).parse(searchText);
            Query contentQuery = new QueryParser("content", analyzer).parse(searchText);
            booleanQueryBuilder.add(titleQuery, BooleanClause.Occur.SHOULD)
                    .add(contentQuery, BooleanClause.Occur.SHOULD);
        }
        if(null != address && !"".equals(address)){
            Query addressQuery = new QueryParser("address", analyzer).parse(address);
            booleanQueryBuilder.add(addressQuery, BooleanClause.Occur.SHOULD);
        }
        if(null != areaFrom && !"".equals(areaFrom) && null != areaTo && !"".equals(areaTo)){
            Query areaRangeQuery = IntPoint.newRangeQuery("area", Integer.parseInt(areaFrom), Integer.parseInt(areaTo));
            booleanQueryBuilder.add(areaRangeQuery, BooleanClause.Occur.MUST);
        }
        if(null != floorFrom && !"".equals(floorFrom) && null != floorTo && !"".equals(floorTo)){
            if (!Character.isDigit(floorFrom.charAt(0))) {
                floorFrom = "0";
            }
            if (!Character.isDigit(floorTo.charAt(0))) {
                floorTo = "0";
            }
            Query floorRangeQuery = IntPoint.newRangeQuery("floor", Integer.parseInt(floorFrom), Integer.parseInt(floorTo));
            booleanQueryBuilder.add(floorRangeQuery, BooleanClause.Occur.MUST).build();
        }

        BooleanQuery booleanQuery = booleanQueryBuilder.build();


        TopDocs topDocs = indexSearcher.search(booleanQuery, 200);
        List<String> resultFilesPaths = new ArrayList<>();
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document doc = indexSearcher.doc(scoreDoc.doc);
            //System.out.println(doc.get("path_to_file") +" score: "+ scoreDoc.score);
            //System.out.println(indexSearcher.explain(query, scoreDoc.doc));
            resultFilesPaths.add(doc.get("path_to_file")); //parameter
        }
        indexReader.close();
        List<ResultApartment> results = new ArrayList<>();
        for (String pathToFile : resultFilesPaths) {
            results.add(createResultApartment(parse(new File(pathToFile))));
        }
        return results;
    }

    @GET
    @Path("/spellcheck")
    public String getSuggestion(@QueryParam("misspellText") String misspellText, @QueryParam("address") String address, @QueryParam("areaFrom") String areaFrom, @QueryParam("areaTo") String areaTo, @QueryParam("floorFrom") String floorFrom, @QueryParam("floorTo") String floorTo) throws IOException, ParseException {
        System.out.println("BEFORE SPELLCHECK:" + misspellText);
        if("".equals(misspellText)){
            return "";
        }
        return suggestCorrectSearchText(misspellText,address,areaFrom,areaTo,floorFrom,floorTo);
    }

    private String[] spellcheck(String misspellText) throws IOException, ParseException {
        File dir = new File(Constants.SPELLCHECK_INDEX_DIRECTORY);
        Directory directory = FSDirectory.open(dir.toPath());
        SpellChecker spellchecker = new SpellChecker(directory);
        String[] suggestions = spellchecker.suggestSimilar(misspellText, Constants.WORD_SUGGESTIONS_COUNT);
        spellchecker.close();
        return suggestions;
    }

    private String suggestCorrectSearchText(String searchText, String address, String areaFrom, String areaTo, String floorFrom, String floorTo) throws IOException, ParseException {
        String[] terms = searchText.split(" ");
        String[][] suggestionsMatrix = new String[terms.length][Constants.WORD_SUGGESTIONS_COUNT];
        for (int i = 0; i < terms.length; i++) {
            String[] tmp = spellcheck(terms[i]);
            if (tmp.length == 0) {
                suggestionsMatrix[i] = new String[]{terms[i]};
            } else {
                suggestionsMatrix[i] = tmp;
            }

        }
        System.out.println("SUGGESTION MATRIX");
        for (String[] mtrx : suggestionsMatrix) {
            System.out.println(Arrays.toString(mtrx));
        }

        List<String> randomSuggestions = combine(suggestionsMatrix);
        System.out.println("SUGGESTIONS:" + randomSuggestions.toString());
        int maxScore = 0;
        String bestSuggestion = "";
        for (String suggestion : randomSuggestions) {
            System.out.println(suggestion);
            int currentScore = search(suggestion,address,areaFrom,areaTo,floorFrom,floorTo).size();
            if (currentScore > maxScore) {
                maxScore = currentScore;
                bestSuggestion = suggestion;
            }
        }
        return bestSuggestion;
    }

    private List<String> parse(File file) {
        List<String> lines = new ArrayList<>();
        String line = "";
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    private ResultApartment createResultApartment(List<String> fields) {
        ResultApartment resultApartment = new ResultApartment();
        resultApartment.setTitle(fields.get(0));
        resultApartment.setPrice(fields.get(1));
        resultApartment.setContent(fields.get(2));
        resultApartment.setAddress(fields.get(3));
        resultApartment.setArea(fields.get(4));
        resultApartment.setFloor(fields.get(5));
        resultApartment.setConstruction_type(fields.get(6));
        resultApartment.setTelephone(fields.get(7));
        resultApartment.setUrl(fields.get(8));
        return resultApartment;
    }

    private List<String> combine(String[][] matrix) {
        int sizeArray[] = new int[matrix.length];
        int counterArray[] = new int[matrix.length];
        int total = 1;
        for (int i = 0; i < matrix.length; ++i) {
            sizeArray[i] = matrix[i].length;
            total *= matrix[i].length;
        }
        List<String> list = new ArrayList<>(total);
        StringBuilder sb;
        for (int count = total; count > 0; --count) {
            sb = new StringBuilder();
            for (int i = 0; i < matrix.length; ++i) {
                sb.append(matrix[i][counterArray[i]]);
                if (i != matrix.length - 1) {
                    sb.append(" ");
                }
            }
            list.add(sb.toString());
            for (int incIndex = matrix.length - 1; incIndex >= 0; --incIndex) {
                if (counterArray[incIndex] + 1 < sizeArray[incIndex]) {
                    ++counterArray[incIndex];
                    break;
                }
                counterArray[incIndex] = 0;
            }
        }
        return list;
    }
}
