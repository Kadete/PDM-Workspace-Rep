package pt.isel.pdm.grupo17.thothnews.utils;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.Toast;

import pt.isel.pdm.grupo17.thothnews.R;
import pt.isel.pdm.grupo17.thothnews.activities.SettingsActivity;
import pt.isel.pdm.grupo17.thothnews.data.ThothContract;

public class SQLiteUtils {

    public static final String TRUE = "1", FALSE = "0";

    private static String wherePrefixOr = " OR (",
            wherePrefixAnd = " AND (",
            whereInitialPrefix = " (",
            whereSuffix = " LIKE ? )";

    public static String appendWhereCondition(String src, String... conditions){
        // No conditions to add, do nothing
        if(conditions == null || conditions.length == 0){
            return src;
        }

        StringBuilder builder;
        if(src==null){
            builder = new StringBuilder();
            // At the first argument, it shouldn't be appended "AND" suffix
            builder.append(whereInitialPrefix);

        }else{
            builder = new StringBuilder(src);
            builder.append(wherePrefixAnd);

        }

        builder.append(conditions[0])
                .append(whereSuffix);


        // Append ", AND (" and " = ? )" to each arg
        for(int idx=1;idx<conditions.length;++idx){
            builder.append(wherePrefixAnd)
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

        if(src == null)
            src = new String[]{};

        int srcSize = src.length,
            argsSize = args.length;

        String[] retArr = new String[srcSize + argsSize];


        System.arraycopy(src,0,retArr,0,srcSize);
        System.arraycopy(args,0,retArr,srcSize,argsSize);
        return retArr;
    }

    public static void startPrefsIfNoClassesEnrolled(Context context){
        Cursor cClassesEnrolled = context.getContentResolver().query(ThothContract.Classes.ENROLLED_URI, null, null, null, null);
        if(!cClassesEnrolled.moveToNext()){
            Toast.makeText(context, context.getString(R.string.setup_classes_request), Toast.LENGTH_LONG).show();
            Intent intent = new Intent(context, SettingsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            cClassesEnrolled.close();
            context.startActivity(intent);
        }
        cClassesEnrolled.close();
    }

}
