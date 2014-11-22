package pt.isel.pdm.grupo17.thothnews.utils;

/**
 * Created by Tiago on 22/11/2014.
 */
public class SQLiteUtils {

    private static String wherePrefix = ", AND (",
            whereInitialPrefix = " (",
            whereSuffix = " = ? )";

    public static String appendWhereCondition(String src, String... conditions){
        // No conditions to add, do nothing
        if(conditions == null || conditions.length == 0){
            return src;
        }

        StringBuilder builder;
        if(conditions==null){
            builder = new StringBuilder();
        }else{
            builder = new StringBuilder(src);
        }
        // At the first argument, it shouldn't be appended "AND" suffix
        builder.append(whereInitialPrefix)
                .append(conditions[0])
                .append(whereSuffix);

        // Append ", AND (" and " = ? )" to each arg
        for(int idx=1;idx<conditions.length;++idx){
            builder.append(wherePrefix)
                    .append(conditions[idx])
                    .append(whereSuffix);
        }
        return builder.toString();
    }

    public static String[] appendArgs(String[] src, String... args){
        // No args to add, do nothing
        if(args==null || args.length == 0){
            return src;
        }
        int srcSize = src == null? 0:src.length,
            argsSize = args.length;

        String[] retArr = new String[srcSize + argsSize];

        System.arraycopy(src,0,retArr,0,srcSize);
        System.arraycopy(args,0,retArr,srcSize,argsSize);
        return retArr;
    }

}
