package com.comparty.fastbook;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

    	Spritzer s;
    	
    	public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            
            SpritzerTextView textView = (SpritzerTextView) rootView.findViewById(R.id.spritzer);
            
            s = new Spritzer(textView);
            s.setText(stuff);
            s.setWpm(500);
            
            Button startButton = (Button) rootView.findViewById(R.id.startButton);
            startButton.setOnClickListener(new OnClickListener() {
            	public void onClick(View v) {
            		s.start();
            	}
            });
            
            return rootView;
        }
        String stuff = "Sons of Gondor! Of Rohan! My brothers. I see in your eyes the same fear that would take the heart of me. A day may come when the courage of Men fails, when we forsake our friends and break all bonds of fellowship, but it is not this day. An hour of wolves and shattered shields when the Age of Men comes crashing down, but it is not this day! This day we fight! By all that you hold dear on this good earth, I bid you stand, Men of the West!";
    }    
    
}
