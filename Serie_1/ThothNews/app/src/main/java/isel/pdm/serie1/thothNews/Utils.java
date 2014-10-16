package isel.pdm.serie1.thothNews;

import java.io.InputStream;
import java.util.Scanner;

/**
 * Created by Kadete on 15/10/2014.
 */
public class Utils {


    protected static String readAllFrom(InputStream is){
        Scanner s = new Scanner(is);
        try{
            s.useDelimiter("\\A");
            return s.hasNext() ? s.next() : null;
        }finally{
            s.close();
        }
    }
}
