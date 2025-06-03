package Parse;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Parser {
    private Scanner leitor;
    private boolean fileFound = true;

    public Parser(String filePath) {
        try {
            File file = new File(filePath);
            leitor = new Scanner(file);
        } catch (FileNotFoundException e) {
            System.err.println("Arquivo não encontrado: " + filePath);
            fileFound = false;
        }
    }


    public boolean hasNext() {
        if (!fileFound || leitor == null) {
            return false;
        }
        return leitor.hasNextLine();
    }

    public String nextLine() {
        if (!fileFound || leitor == null || !leitor.hasNextLine()) {
            return null; 
        }
        return leitor.nextLine();
    }

    public void close() {
        if (leitor != null) {
            leitor.close();
        }
    }
}