package bg.uni.sofia.fmi.data.mining.project.rest;

import bg.uni.sofia.fmi.data.mining.project.lucene.*;
import org.apache.lucene.queryparser.classic.ParseException;
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
import java.util.List;
import java.util.Random;

@Path("/")
public class Endpoint {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/search")
    public List<ResultApartment> search(@QueryParam("searchText") String searchText) throws IOException, ParseException {
    	File indexDir = new File(Constants.INDEX_DIRECTORY);
        List<String> resultFilesPaths = Searcher.search(FSDirectory.open(indexDir.toPath()),searchText);
        List<ResultApartment> results = new ArrayList<>();
        for(String pathToFile:resultFilesPaths){
            results.add(createResultApartment(parse(new File(pathToFile))));
        }
        return results;
    }

    @GET
    @Path("/spellcheck")
    public String getSuggestion(@QueryParam("misspellText") String misspellText) throws IOException, ParseException {
        return suggestCorrectSearchText(misspellText);
    }

    private String[] spellcheck(String misspellText) throws IOException, ParseException {
        File dir = new File(Constants.SPELLCHECK_INDEX_DIRECTORY);
        Directory directory = FSDirectory.open(dir.toPath());
        SpellChecker spellchecker = new SpellChecker(directory);
        String[] suggestions = spellchecker.suggestSimilar(misspellText, Constants.WORD_SUGGESTIONS_COUNT);
        spellchecker.close();
        return suggestions;
    }

    private String suggestCorrectSearchText(String searchText) throws IOException, ParseException {
        String[] terms = searchText.split(" ");
        String[][] suggestionsMatrix = new String[terms.length][Constants.WORD_SUGGESTIONS_COUNT];
        for(int i=0;i<terms.length;i++){
            suggestionsMatrix[i] = spellcheck(terms[i]);
        }

//        List<String> randomSuggestions = new ArrayList<>();
//        for(int i=0;i<50;i++){
//            String tempSuggestion = "";
//            for(int j=0;j<suggestionsMatrix.length;j++){
//                Random rnd = new Random();
//                int randomNumber = rnd.nextInt(suggestionsMatrix[j].length);
//                tempSuggestion+=suggestionsMatrix[j][randomNumber]+" ";
//            }
//            System.out.println(tempSuggestion);
//            randomSuggestions.add(tempSuggestion);
//        }

        List<String> randomSuggestions = combine(suggestionsMatrix);

        int maxScore=0;
        String bestSuggestion="";
        for(String suggestion:randomSuggestions){
            System.out.println(suggestion);
            int currentScore = search(suggestion).size();
            if(currentScore>maxScore){
                maxScore=currentScore;
                bestSuggestion=suggestion;
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

    private ResultApartment createResultApartment(List<String> fields){
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
        return  resultApartment;
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
                if(i!= matrix.length-1){
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
