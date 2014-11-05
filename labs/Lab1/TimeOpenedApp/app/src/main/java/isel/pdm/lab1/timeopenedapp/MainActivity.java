package isel.pdm.lab1.timeopenedapp;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends ActionBarActivity {

    private String currentDateandTime = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        currentDateandTime = sdf.format(new Date());
        TextView timerTxt = (TextView) findViewById(R.id.tv1);
        timerTxt.setText(currentDateandTime.toString());
    }

    /*
    The method onSaveInstanceState(Bundle) is called before placing the activity in such a background state,
    allowing you to save away any dynamic instance state in your activity into the given Bundle,
    to be later received in onCreate(Bundle) if the activity needs to be re-created

    Note that it is important to save persistent data in onPause() instead of onSaveInstanceState(Bundle)
    because the latter is not part of the lifecycle callbacks
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("MyTime",currentDateandTime);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentDateandTime = savedInstanceState.getString("MyTime");
        TextView timerTxt = (TextView) findViewById(R.id.tv1);
        timerTxt.setText(currentDateandTime.toString());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
