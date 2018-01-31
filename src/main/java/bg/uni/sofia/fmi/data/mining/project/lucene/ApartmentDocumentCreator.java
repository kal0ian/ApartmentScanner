package bg.uni.sofia.fmi.data.mining.project.lucene;

import org.apache.lucene.document.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class ApartmentDocumentCreator extends DocumentCreator {

    @Override
    public Document createDocument(File file) {
        Document document = new Document();
        List<String> fields = parse(file);
        document.add(new TextField("title", fields.get(0), Field.Store.NO));
        document.add(new StringField("price", fields.get(1), Field.Store.NO));
        document.add(new TextField("content", fields.get(2), Field.Store.NO));
        document.add(new TextField("address", fields.get(3), Field.Store.NO));
        document.add(new IntPoint("area", Integer.parseInt(fields.get(4))));
        document.add(new IntPoint("floor", Integer.parseInt(fields.get(5))));
        document.add(new StringField("construction_type", fields.get(6), Field.Store.NO));
        document.add(new StringField("telephone", fields.get(7), Field.Store.NO));
        document.add(new StringField("url", fields.get(8), Field.Store.NO));
        document.add(new StringField("path_to_file", file.getAbsolutePath(), Field.Store.YES));
        return document;
    }

    private List<String> parse(File file) {
        List<String> lines = new ArrayList<>();
        String line;
        int i=0;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while ((line = br.readLine()) != null) {
                System.out.println("Before processing: " + line);
                System.out.println("Before processing: " + file.getAbsolutePath());
                if(i==4){
                    line = line.substring(0,line.indexOf(" "));
                }
                if(i==5){
                    if(!Character.isDigit(line.charAt(0))) {
                        line = "0";
                    }else if("".equals(line) || "-".equals(line) || line.contains(".")){
                        line="-1";
                    }else{
                        line = line.substring(0,line.indexOf("-"));
                    }
                }
                lines.add(line);
                i++;
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }
}
