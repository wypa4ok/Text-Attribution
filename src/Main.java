import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Serializable;

/**
 * Created by macbook on 11.04.14.
 */



public class Main implements Serializable {




    public static String[] listFilesForFolder(final File folder) {

        File [] files = folder.listFiles();

        String d[] = new String [files.length-1];

        for (int i=0; i<d.length; i++ ) {

            d[i] = files[i+1].getAbsolutePath();
            //System.out.println(d[i]);
        }

        return d;

    }



    public static int[][] create_words(){

        Matrix m = new Matrix();

        m.create();

        return m.create_matrix();


    }




    public static void main(String[] args) throws Exception {

        //String path_train = "/Users/macbook/Desktop/train_new/Classifier_";

        String path_test = "test_1";

        int numb = 1;

        int[][] codes_matrix = create_words();

        int result[][] = new int[33][39];

        int result_pre[] = new int[39];

        File train_data[] = new File[10];

        File test_data[] = new File[10];

        int hemming_dist = 0;

        int min_dist = 40;

        int row = 0;

        double precision = 0;


        String name = "Pavlo";

        String[] test_files = new String[33];

        for(int i = 0; i < test_files.length; i++){

            test_files[i] = listFilesForFolder(new File(path_test))[i];


        }


        Code_Word code_word = new Code_Word(name);

          //code_word.train();

        for(int i=0; i< test_files.length; i++){

            //System.out.println(test_files[i].substring(30));

            result_pre = code_word.test(test_files[i]);

            for(int j=0; j<39; j++) {

                result[i][j] = result_pre[j];

            }

            for(int k=0; k< 10; k++){

                for(int j=0; j<39; j++) {

                    if (result_pre[j] != codes_matrix[k][j]) {

                        hemming_dist++;

                    }
                }

                 if(hemming_dist < min_dist ){

                     min_dist = hemming_dist;

                     row = k;
                 }

                hemming_dist =0;
            }

            BufferedReader reader = new BufferedReader(new FileReader(new File(test_files[i])));

            int val = Integer.valueOf(reader.readLine());

            if(val == row) precision++;

        System.out.println( "The file " + test_files[i].substring(30) + " is the example of the class number " + row);
          //  System.out.println("Hemming distance = " + min_dist);

            min_dist = 40;

        }


        System.out.println("Precision = " + precision/test_files.length);


       /* for(int i=0; i<10; i++){

            for(int j=0; j<39; j++){

                System.out.print(codes_matrix[i][j]);

            }

            System.out.println();

        }*/

        System.out.println();


        for(int i=0; i<33; i++){

            for(int j=0; j<39; j++){

                System.out.print(result[i][j]);

            }

            System.out.println();

        }


    }
}
