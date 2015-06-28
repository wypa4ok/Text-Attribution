import java.io.Serializable;

/**
 * Created by macbook on 11.04.14.
 */
public class Matrix implements Serializable {

    int matrix[][] = new int[10][511];

    int power=10;

    int v = 8;



    int set[] = {14, 17, 24, 25, 33, 40, 41, 50, 61, 69, 75, 82, 93, 101, 106, 111, 118, 146, 157, 165, 170, 175, 182, 194, 205, 214, 234, 290, 301, 310, 322, 333, 342, 362, 386, 397, 406, 426, 454};

    int[][] result = new int[10][39];


    Boolean contains(int a) {

        for (int i = 0; i < set.length; i++) {

            if (set[i] == a) return true;

        }

        return false;

    }



    public int[][] create(){

        for(int j=0; j<511; j++){

            matrix[0][j] = 1;

        }

        for(int i=1; i<10; i++) {


            for (int j = 0; j < 511; j++) {

               if( (j/(int)Math.pow(2,v))%2 == 0 )  matrix[i][j] = 0;

               else matrix[i][j] = 1;

            }

            v--;

            }

        return matrix;

    }


    int[][] create_matrix() {

        int x=0, y=0;

        for(int i=0; i<10; i++) {

            for (int j = 0; j < 511; j++) {

                if(contains(j)) {

                    result[x][y] = matrix[i][j];


                    y++;

                }

            }

            y=0;

            x++;

        }

        return result;

    }


}
