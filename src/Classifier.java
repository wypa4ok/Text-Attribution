import jnisvmlight.LabeledFeatureVector;
import jnisvmlight.SVMLightInterface;
import jnisvmlight.SVMLightModel;
import jnisvmlight.TrainingParameters;

import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by macbook on 12.04.14.
 */
public class Classifier implements Serializable {

    private int N; // number of training docs

    private int T; // number of test docs

    private int sign;

    String name;

    File file;

    Matrix matrix = new Matrix();

    int[][] m = new int[10][39];

    private double precision = 0;

    private double recall = 0;

    Classifier(String name, File file) {

        this.file = file;

        this.name = name;

        matrix.create();

        m = matrix.create_matrix();

    }

    public String train() throws IOException, ParseException {

        String a = name.substring(name.lastIndexOf("_"));

        String[] directory_train = listFilesForFolder(file);

        File model_file;

        N = directory_train.length;

        SVMLightInterface trainer = new SVMLightInterface();

        LabeledFeatureVector[] traindata = new LabeledFeatureVector[N * 10];

        SVMLightInterface.SORT_INPUT_VECTORS = true;

        System.out.print("CREATING A TRAINING SET [N=" + N + "] ..");

        int count = 0;

        double max = 0;

        for (int i = 0; i < N; i++) {

            for (int k = 0; k < 10; k++) {

                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(directory_train[i]))));

                String line;

                int val;

                ArrayList<Integer> list_dims = new ArrayList<Integer>();

                ArrayList<Double> list_values = new ArrayList<Double>();

                val = Integer.valueOf(reader.readLine());

                if (val == 0) val = -1;

                double value;

                int new_value;

                while ((line = reader.readLine()) != null) {

                    list_dims.add(Integer.valueOf(line.substring(0, line.indexOf(":"))));

                    value = Double.valueOf(line.substring(line.indexOf(":") + 1));

                    list_values.add((double) value);

                }

                int s1 = list_dims.size();

                int s2 = list_values.size();

                int[] dims_train = new int[s1];

                double[] values_train = new double[s2];


                for (int k1 = 0; k1 < s1; k1++) {

                    dims_train[k1] = list_dims.get(k1).intValue();

                }

                for (int k2 = 0; k2 < s2; k2++) {

                    values_train[k2] = list_values.get(k2).doubleValue();

                }

                traindata[count] = new LabeledFeatureVector(val, dims_train, values_train);

                count++;

            }
        }

        TrainingParameters tp = new TrainingParameters();

        tp.getLearningParameters().verbosity = 2;

        System.out.println("\nTRAINING SVM-light MODEL ..");

        SVMLightModel model_tr; //= trainer.trainModel(traindata, tp);

        model_tr = trainer.trainModel(traindata, new String[]{"-z", "c", "-v", "1", "-t", "0", "-g", "200", "-d", "40", "-s", "3", "-r", "3"});

        System.out.println(" DONE.");

        model_file = new File("jni_model_" + name + ".dat");

        model_tr.writeModelToFile(String.valueOf(model_file));

        String s = "";

        Scanner r = new Scanner(new File("jni_model_" + name + ".dat"));

        while (r.hasNextLine()) {
            System.out.println(s);

            s += r.nextLine();
        }


        return s;

    }


    public int test(String directory_test) throws IOException, ParseException {

        int result;

        int val = 1;

        LabeledFeatureVector testdata;

        SVMLightModel model_test;

        SVMLightInterface.SORT_INPUT_VECTORS = true;


        model_test = SVMLightModel.readSVMLightModelFromURL(new java.io.File("models/jni_model_" + name + ".dat").toURL());

        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(directory_test))));

        ArrayList<Integer> list_dims = new ArrayList<Integer>();

        ArrayList<Double> list_values = new ArrayList<Double>();

        String line;


        double value;

        val = Integer.valueOf(reader.readLine());


        while ((line = reader.readLine()) != null) {

            list_dims.add(Integer.valueOf(line.substring(0, line.indexOf(":"))));

            value = Double.valueOf(line.substring(line.indexOf(":") + 1));

            list_values.add((double) value);

        }

        int s1 = list_dims.size();

        int s2 = list_values.size();

        int[] dims_test = new int[s1];

        double[] values_test = new double[s2];


        for (int k1 = 0; k1 < s1; k1++) {

            dims_test[k1] = list_dims.get(k1).intValue();


        }


        for (int k2 = 0; k2 < s2; k2++) {

            values_test[k2] = list_values.get(k2).doubleValue();

        }


        normalizeValues(values_test);

        testdata = new LabeledFeatureVector(val, dims_test, values_test);

        double d = model_test.classify(testdata);

        if (d > 0) result = 1;

        else result = 0;

        if (result == m[val][Integer.valueOf(name.substring(name.lastIndexOf("_") + 1)) - 1]) precision++;

        if ((m[val][Integer.valueOf(name.substring(name.lastIndexOf("_") + 1)) - 1] == 1) &&
                (result == m[val][Integer.valueOf(name.substring(name.lastIndexOf("_") + 1)) - 1])) recall++;

        return result;

    }

    public void showResults() {

        System.out.println("Precision of " + name.substring(name.lastIndexOf("_") + 1) + " classifier = " + precision / 33);
        System.out.println("Recall of " + name.substring(name.lastIndexOf("_") + 1) + " classifier = " + precision / 33);


    }


    public void transformValues(double[] vals) {
        for (int i = 0; i < vals.length; i++) {
            //m_vals[i] = 1 / m_vals[i];
            vals[i] = 1 - (1 / Math.pow(1.01, vals[i]));
        }
    }


    public void normalizeValues(double[] m_vals) {
        double sum = 0;

        for (int i = 0; i < m_vals.length; i++) {
            if (m_vals[i] > 1) {
                sum += Math.pow(m_vals[i], 2);
            }
        }

        sum = Math.sqrt(sum);

        for (int i = 0; i < m_vals.length; i++) {
            if (m_vals[i] > 1) {
                m_vals[i] = m_vals[i] / sum;
            }
        }
    }


    public void normalize(double[] value) {

        double min = value[0];

        double max = value[0];

        for (int i = 1; i < value.length; i++) {

            if (value[i] < min) {
                min = value[i];
            }

            if (value[i] > max) {
                max = value[i];
            }

        }

        for (int j = 0; j < value.length; j++) {

            value[j] = (value[j] - min) / (max - min);

        }

    }

    public void standardization(double[] value) {

        double sum = 0, sum_v = 0;

        double mean;

        double variance;

        for (int i = 0; i < value.length; i++) {

            sum += value[i];

        }

        mean = sum / value.length;

        for (int i = 0; i < value.length; i++) {

            sum_v += Math.pow((value[i] - mean), 2);

        }

        variance = Math.sqrt(sum_v / value.length);


        for (int i = 0; i < value.length; i++) {

            value[i] = (value[i] - mean) / variance;

        }
    }


    public static String[] listFilesForFolder(File folder) {

        File[] files = folder.listFiles();

        String d[] = new String[files.length - 1];

        for (int i = 0; i < d.length; i++) {

            d[i] = files[i + 1].getAbsolutePath();

        }

        return d;

    }

}
