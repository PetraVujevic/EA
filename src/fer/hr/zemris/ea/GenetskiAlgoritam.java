package fer.hr.zemris.ea;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

public class GenetskiAlgoritam {
    static int vel_pop = 200;

    static int broj_iteracija = 10000;

    static int mortalitet = 10;

    static double pm = 1. / 200;

    static boolean elitizam = true;

    static Random random = new Random();

    static String datoteka = "dataset1.txt";

    // funkcija koju optimiramo
    static PrijenosnaKarakteristika f = new PrijenosnaKarakteristika() {
        @Override
        public double izracunaj(double x, double y, double b0, double b1,
                double b2, double b3, double b4) {
            return Math.sin(b0 + b1 * x) + b2 * Math.cos(x * (b3 + y))
                    * (1.0 / (1 + Math.pow(Math.E, (x - b4) * (x - b4))));
        }
    };

    public static void main(String[] args) {
        System.out
                .println("Zelim provesti(1\\2): \n 1. Generacijski genetski algoritam \n 2. Eliminacijski genetski algoritam");
        Scanner sc = new Scanner(System.in);
        String line = sc.nextLine();
        sc.close();

        if (line.contains("1"))
            generacijskiGA();
        else if (line.contains("2"))
            eliminacijskiGA();
        else
            System.out.println("Citaj upute pazljivije.");
    }

    private static void generacijskiGA() {
        // stvori pocetnu populaciju
        ArrayList<Jedinka> populacija = stvoriPopulaciju(vel_pop);

        // evaluiraj populaciju
        for (int i = 0; i < vel_pop; i++) {
            evaluirajJedinku(populacija.get(i));
        }

        Collections.sort(populacija);
        Jedinka najbolja = null;

        for (int generacija = 1; generacija <= broj_iteracija; generacija++) {
            // selekcija
            ArrayList<Jedinka> novaPopulacija = new ArrayList<Jedinka>();
            int i = 0;
            if (elitizam) {
                novaPopulacija.add(populacija.get(0));
                novaPopulacija.add(populacija.get(1));
                i++;
            }
            for (; i < vel_pop / 2; i++) {
                // odaberi roditelje za novu populaciju
                Jedinka r1 = jednostavnaSelekcija(populacija);
                Jedinka r2 = jednostavnaSelekcija(populacija);

                // krizaj roditelje
                Jedinka[] djeca = jednostavnaAritmetickaKombinacija(r1, r2);

                // mutiraj djecu
                mutiraj(djeca[0]);
                mutiraj(djeca[1]);
                novaPopulacija.add(djeca[0]);
                novaPopulacija.add(djeca[1]);

                // evaluiraj djecu
                evaluirajJedinku(djeca[0]);
                evaluirajJedinku(djeca[1]);
            }

            populacija = novaPopulacija;
            Collections.sort(populacija);

            if (najbolja == null || najbolja.kazna > populacija.get(0).kazna) {
                najbolja = populacija.get(0);
                System.out.println("\nGeneracija " + generacija + ": \n"
                        + najbolja);
            }
        }
    }

    private static void eliminacijskiGA() {
        // stvori pocetnu populaciju
        ArrayList<Jedinka> populacija = stvoriPopulaciju(vel_pop);

        // evaluiraj populaciju
        for (int i = 0; i < vel_pop; i++) {
            evaluirajJedinku(populacija.get(i));
        }

        Jedinka najbolja = null;

        for (int generacija = 1; generacija <= broj_iteracija; generacija++) {
            for (int j = 0; j < mortalitet; j++) {
                // odaberi slucajne jedinke za 3-turnirsku selekciju
                ArrayList<Jedinka> turnir = new ArrayList<Jedinka>();
                for (int k = 0; k < 3; k++)
                    turnir.add(odaberiSlucajnu(populacija));

                // krizaj dvije najbolje jedinke
                Collections.sort(turnir);
                Jedinka dijete = potpunaAritmetickaRekombinacija(turnir.get(0),
                        turnir.get(1));

                // mutiraj dijete
                mutiraj(dijete);

                // evaluiraj dijete
                evaluirajJedinku(dijete);

                // zamijeni dijete s najgorom jedinkom
                int indexNajgora = populacija.indexOf(turnir.get(2));
                populacija.set(indexNajgora, dijete);
            }

            Collections.sort(populacija);

            if (najbolja == null || najbolja.kazna > populacija.get(0).kazna) {
                najbolja = populacija.get(0);
                System.out.println("\nGeneracija " + generacija + ": \n"
                        + najbolja);
            }
        }
    }

    static ArrayList<Jedinka> stvoriPopulaciju(int velPop) {
        ArrayList<Jedinka> populacija = new ArrayList<Jedinka>();
        for (int i = 0; i < velPop; i++) {
            populacija.add(Jedinka.slucajnoGenerirajJedinku(random));
        }
        Collections.sort(populacija);
        return populacija;
    }

    private static void evaluirajJedinku(Jedinka jedinka) {
        double sumaKvadrata = 0;
        for (Double[] mjerenje : Podaci.dohvatiMjerenja(datoteka)) {
            double predvidenRezultat = f.izracunaj(mjerenje[0], mjerenje[1],
                    jedinka.geni[0], jedinka.geni[1], jedinka.geni[2],
                    jedinka.geni[3], jedinka.geni[4]);
            double dobivenRezultat = mjerenje[2];
            double razlika = dobivenRezultat - predvidenRezultat;
            sumaKvadrata += Math.pow(razlika, 2);
        }

        // kazna je srednja kvadratna pogreska
        int ukupniBrojUzoraka = Podaci.dohvatiMjerenja(datoteka).size();
        jedinka.kazna = sumaKvadrata / ukupniBrojUzoraka;
    }

    private static Jedinka[] jednostavnaAritmetickaKombinacija(Jedinka r1,
            Jedinka r2) {
        double[] geni1 = new double[5];
        double[] geni2 = new double[5];

        int tocka_prijeloma = random.nextInt(4);

        for (int i = 0; i < tocka_prijeloma; i++) {
            geni1[i] = r1.geni[i];
            geni2[i] = r2.geni[i];
        }
        for (int i = tocka_prijeloma; i < 5; i++) {
            geni1[i] = r2.geni[i];
            geni2[i] = r1.geni[i];
        }

        Jedinka dijete1 = new Jedinka(geni1);
        Jedinka dijete2 = new Jedinka(geni2);
        return new Jedinka[] { dijete1, dijete2 };
    }

    private static Jedinka jednostavnaSelekcija(ArrayList<Jedinka> populacija) {
        double najvecaKazna = populacija.get(populacija.size() - 1).kazna;
        double sumaDobrota = 0;

        for (int i = 0; i < populacija.size(); i++)
            sumaDobrota += najvecaKazna - populacija.get(i).kazna;

        double r = random.nextDouble() * sumaDobrota;
        double q = 0;

        for (int i = 0; i < populacija.size(); i++) {
            q += najvecaKazna - populacija.get(i).kazna;
            if (r < q)
                return populacija.get(i);
        }
        return populacija.get(populacija.size() - 1);
    }

    // jednostavna mutacija
    private static void mutiraj(Jedinka dijete) {
        for (int i = 0; i < dijete.geni.length; i++) {
            double r = random.nextDouble();
            if (r <= pm)
                dijete.geni[i] = Jedinka.randomBeta(random);
        }
    }

    static Jedinka odaberiSlucajnu(ArrayList<Jedinka> jedinke) {
        int index = random.nextInt(jedinke.size());
        return jedinke.get(index);
    }

    private static Jedinka potpunaAritmetickaRekombinacija(Jedinka r1,
            Jedinka r2) {
        double beta0 = (r1.geni[0] + r2.geni[0]) / 2;
        double beta1 = (r1.geni[1] + r2.geni[1]) / 2;
        double beta2 = (r1.geni[2] + r2.geni[2]) / 2;
        double beta3 = (r1.geni[3] + r2.geni[3]) / 2;
        double beta4 = (r1.geni[4] + r2.geni[4]) / 2;
        double[] geni = new double[] { beta0, beta1, beta2, beta3, beta4 };
        Jedinka dijete = new Jedinka(geni);
        return dijete;
    }
}
