package com.jiahuan.svgmapview.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


/***
 * Activity made to present the user the possible options of indoor location
 */
public class MainActivity extends ActionBarActivity
{
    private Class[] mClasses = {LocationOverlayActivity.class,SparkActivity.class,SparkStepActivity.class}; //array of activities (modes of traking) to post on a listview

    /**
     * Called upon criation of MainActivity
     * Sets layout
     * Calls initialize method
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();
    }


    private void initialize()
    {
     /*   mSelectListView = (ListView) findViewById(R.id.main_select_lv);
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.activity_select_array));
        mSelectListView.setAdapter(mAdapter);
        mSelectListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                startActivity(new Intent(MainActivity.this, mClasses[position]));
            }
        });*/


        String [] activityarray = {"Location with beacons","Location with accelerometer","Location with step detector"}; // array of content to put on the list view

        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, activityarray);

        ListView listView = (ListView) findViewById(R.id.main_select_lv);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                startActivity(new Intent(MainActivity.this, mClasses[position])); // after selecting calls activity on the correspondent listview
            }
        });

    }


}
