/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.admin.parkingappfinal;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Provides UI for the main screen.
 */
public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private static final String url = "jdbc:mysql://10.0.2.2:8889/ParkingSpaces";
    private static final String user = "andUser";
    private static final String pass = "password";
    String[] spaces = new String[3];
    int[] filled = new int[3];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Adding Toolbar to Main screen
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Setting ViewPager for each Tabs
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        // Set Tabs inside Toolbar
        //TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        //tabs.setupWithViewPager(viewPager);
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new TileContentFragment(), "Tile");
        viewPager.setAdapter(adapter);
        // Create Navigation drawer and inflate layout
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        // Adding menu icon to Toolbar
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            VectorDrawableCompat indicator
                    = VectorDrawableCompat.create(getResources(), R.drawable.ic_menu, getTheme());
            indicator.setTint(ResourcesCompat.getColor(getResources(),R.color.white,getTheme()));
            supportActionBar.setHomeAsUpIndicator(indicator);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
        // Set behavior of Navigation drawer
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    // This method will trigger on item Click of navigation menu
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // Set item in checked state
                        menuItem.setChecked(true);

                        // TODO: handle navigation

                        // Closing drawer on item click
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
        // Adding Floating Action Button to bottom right of main view
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Hello Snackbar!",
                        Snackbar.LENGTH_LONG).show();
            }
        });
//        super.onCreate(savedInstanceState);
        setContentView(R.layout.park_display);
        Button button = (Button) findViewById(R.id.refreshButton);
        button.setOnClickListener(new View.OnClickListener()   {
            public void onClick(View v)  {
                try {
                    update();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }

    // Add Fragments to Tabs
    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        //adapter.addFragment(new ListContentFragment(), "List");
        adapter.addFragment(new TileContentFragment(), "Tile");
        //adapter.addFragment(new CardContentFragment(), "Card");
        viewPager.setAdapter(adapter);
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void goToParkDisplay(View view){
        Intent intent = new Intent(this, ParkDisplayActivity.class);
        startActivity(intent);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }
    public void update(){
        ImageView a1on = (ImageView) findViewById(R.id.imageView);
        ImageView a1off = (ImageView) findViewById(R.id.A1off);
        ImageView a2on = (ImageView) findViewById(R.id.A2on);
        ImageView a2off = (ImageView) findViewById(R.id.A2off);
        ImageView b1on = (ImageView) findViewById(R.id.B1on);
        ImageView b1off = (ImageView) findViewById(R.id.B1off);
        try {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(url,user,pass);

            String result =  "Database connection success\n";
            System.out.println("We made it here");
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("select * from Lot1");
            ResultSetMetaData rsmd = rs.getMetaData();
            //System.out.println(rs.getString(1));
            //System.out.println(rs.getString(2));
            //System.out.println(rs.getString(3));
            while(rs.next()) {
                //System.out.println(rs.getString(1));
                //System.out.println(rs.getString(2));
                //System.out.println(rs.getString(3));
                //result += rsmd.getColumnName(1) + ": " + rs.getString(1) + "\n";
                result += rsmd.getColumnName(2) + ":" + rs.getString(2);
                result += rsmd.getColumnName(3) + ":" + rs.getString(3);
            }
            //System.out.println(result);
            for(int i = 0;i<3;i++) {
                spaces[i] = result.substring(result.indexOf(":") + 1, result.indexOf(":") + 3);
                String newResult = result.substring(result.indexOf(":")+3);
                System.out.println(newResult);
                filled[i] = Integer.parseInt(newResult.substring(newResult.indexOf(":") + 1, newResult.indexOf(":") + 2));
                result = newResult.substring(newResult.indexOf(":")+2);
            }
            System.out.println(Arrays.toString(spaces));
            System.out.println(Arrays.toString(filled));
            //tv.setText(result);
            if(filled[0]==1)
            {
                a1on.bringToFront();
            }
            if(filled[0]==0)
            {
                a1off.bringToFront();
            }
            if(filled[1]==1)
            {
                a2on.bringToFront();
            }
            if(filled[1]==0)
            {
                a2off.bringToFront();
            }
            if(filled[2]==1)
            {
                b1on.bringToFront();
            }
            if(filled[2]==0)
            {
                b1off.bringToFront();
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            //tv.setText(e.toString());
        }
    }

}
