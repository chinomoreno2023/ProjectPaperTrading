package options.papertrading.models;

import au.com.bytecode.opencsv.CSVReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OptionCreater {
    private final char csvSplitBy = ',';
    private final String pathSPY = "src/main/resources/static/spy.csv";

    public List parseCSV(String path) {
        CSVReader csvReader = null;
        try {
            csvReader = new CSVReader(new FileReader(path), getCsvSplitBy());
        }
        catch (FileNotFoundException e) { throw new RuntimeException(e); }

        String[] stringFromCSV;
        List<Option> list = new ArrayList<>();
        try {
            while ((stringFromCSV = csvReader.readNext()) != null) {
                list.add(new Option(stringFromCSV[8]));
            }
        }
        catch (IOException exception) { System.out.println("File is empty"); }
        return list;
    }

    public char getCsvSplitBy() { return csvSplitBy; }

    public String getPathSPY() { return pathSPY; }
}
