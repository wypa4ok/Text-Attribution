import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;

/**
 * Created by macbook on 12.04.14.
 */
public class Code_Word implements Serializable{

    private int[] codes = new int[39];

    int[] words = new int[39];

    private  int N; // number of training docs

    private  int M = 768; // max. number of features per doc

    private  int T; // number of test docs

    private String[] directory_train;

    private String[] test_files;

    private String name;

    Classifier[] classifiers = new Classifier[39];

    String path_train = "/Users/macbook/Desktop/train_new/Classifier_";

    String path_train1 = "train_new/Classifier_";

          public Code_Word(String name){


              this.name = name;

              create();
          }


    public void create(){

         int count = 1;

        String  class_name;


        for(int i=0; i < 39; i++){

            class_name = name + "_" + count;

            classifiers[i] = new Classifier(class_name,new File(path_train1 + count));

            count++;

        }
    }


    public void train() throws IOException, ParseException {

       // create();

        int count = 1;

        for(int i = 0; i < 39; i++){

            classifiers[i].train();

            count++;

        }

    }


    public int[] test(String test_file) throws IOException, ParseException {

      //  create();

      for(int i = 0; i < 39; i++){

          words[i] = classifiers[i].test(test_file);

      }

        return words;

    }


    public static String[] listFilesForFolder(final File folder) {

        File [] files = folder.listFiles();

        String d[] = new String [files.length-1];

        for (int i=0; i<d.length; i++ ) {

            d[i] = files[i+1].getAbsolutePath();
            //System.out.println(d[i]);
        }

        return d;

    }



    public void showResults(){

        for(int i = 0; i<39; i++){

            classifiers[i].showResults();

        }

    }

}
