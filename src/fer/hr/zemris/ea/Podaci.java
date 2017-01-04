package fer.hr.zemris.ea;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Podaci {

    private static HashMap<File, ArrayList<Double[]>> mjerenja = new HashMap<File, ArrayList<Double[]>>();

    public static ArrayList<Double[]> dohvatiMjerenja(String putDoDatoteke) {
        File datoteka = new File(putDoDatoteke);
        if (!mjerenja.containsKey(datoteka))
            mjerenja.put(datoteka, parsiraj(datoteka));

        return mjerenja.get(datoteka);
    }

    private static ArrayList<Double[]> parsiraj(File datoteka) {
        Path path = Paths.get("dataset1.txt");
        Scanner sc = null;
        try {
            sc = new Scanner(path.toFile());
        } catch (FileNotFoundException e) {
            System.out.println("File " + path + " doesn't exist.");
        }

        ArrayList<Double[]> mjerenja = new ArrayList<Double[]>();
        while (sc.hasNextLine()) {
            String xString = sc.next();
            String yString = sc.next();
            String resultString = sc.next();

            double x = Double.parseDouble(xString);
            double y = Double.parseDouble(yString);
            double result = Double.parseDouble(resultString);
            Double[] mjerenje = new Double[3];
            mjerenje[0] = x;
            mjerenje[1] = y;
            mjerenje[2] = result;
            mjerenja.add(mjerenje);
        }
        sc.close();
        return mjerenja;
    }
}
