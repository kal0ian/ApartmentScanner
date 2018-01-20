package bg.uni.sofia.fmi.data.mining.project.lucene;

import org.apache.lucene.document.*;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ApartmentDocumentCreator extends DocumentCreator {

    @Override
    public Document createDocument(File file) {
        Document document = new Document();
        List<String> fields = parse(file);
        document.add(new TextField("title", fields.get(0), Field.Store.YES));
        document.add(new TextField("content", fields.get(1), Field.Store.NO));
        document.add(new StringField("address", fields.get(2), Field.Store.YES));
        document.add(new StringField("area", fields.get(3), Field.Store.YES));
        document.add(new StringField("floor", fields.get(4), Field.Store.YES));
        document.add(new StringField("construction_type", fields.get(5), Field.Store.YES));
        document.add(new StringField("telephone", fields.get(6), Field.Store.YES));
        document.add(new StringField("url", fields.get(7), Field.Store.YES));
        document.add(new StringField("path_to_file", file.getAbsolutePath(), Field.Store.YES));
        return document;
    }

    private List<String> parse(File file) {
        List<String> lines = new ArrayList<>();
        String line = "";
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
            while ((line = br.readLine()) != null) {
                lines.add(line);
                System.out.println(line);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lines;
    }
}
