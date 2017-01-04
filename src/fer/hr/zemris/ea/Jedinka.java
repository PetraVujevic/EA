package fer.hr.zemris.ea;

import java.util.Random;

public class Jedinka implements Comparable<Jedinka> {
    double kazna;
    double[] geni;

    public Jedinka(double[] geni) {
        this.geni = geni;
    }

    public static Jedinka slucajnoGenerirajJedinku(Random random) {
        double beta0 = randomBeta(random);
        double beta1 = randomBeta(random);
        double beta2 = randomBeta(random);
        double beta3 = randomBeta(random);
        double beta4 = randomBeta(random);

        return new Jedinka(new double[] { beta0, beta1, beta2, beta3, beta4 });
    }

    public static double randomBeta(Random random) {
        double a = (double) random.nextInt(8);
        double b = random.nextDouble();
        return a - 4.0 + b;
    }

    @Override
    public int compareTo(Jedinka o) {
        // bolja jedinka ima manju kaznu
        return Double.valueOf(kazna).compareTo(o.kazna);
    }

    @Override
    public String toString() {
        String s = "b0: " + geni[0];
        s += "\nb1: " + geni[1];
        s += "\nb2: " + geni[2];
        s += "\nb3: " + geni[3];
        s += "\nb4: " + geni[4];
        s += "\nkazna: " + kazna;
        return s;
    }
}
