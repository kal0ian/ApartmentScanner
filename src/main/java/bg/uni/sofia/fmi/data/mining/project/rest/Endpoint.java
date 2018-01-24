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
import java.util.Arrays;
import java.util.List;

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
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/spellcheck")
    public List<String> spellcheck(@QueryParam("misspellText") String misspellText) throws IOException, ParseException {
        File dir = new File(Constants.SPELLCHECK_INDEX_DIRECTORY);
        Directory directory = FSDirectory.open(dir.toPath());
        SpellChecker spellchecker = new SpellChecker(directory);
        String[] suggestions = spellchecker.suggestSimilar(misspellText, 3);
        spellchecker.close();
        return Arrays.asList(suggestions);
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
}
