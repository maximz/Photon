package com.comparty.photon;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.comparty.photon.spritz.Spritzer;
import com.comparty.photon.spritz.SpritzerTextView;

public class SpritzFragment extends Fragment {
	Spritzer s;

	boolean playing = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_spritz, container,
				false);
		SpritzerTextView textView = (SpritzerTextView) rootView.findViewById(R.id.spritzer);

		s = new Spritzer(textView);
		s.setText(stuff);
		s.setWpm(500);

		ImageButton startButton = (ImageButton) rootView.findViewById(R.id.start_pause);
		startButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (!playing) {
					s.start();
					playing = true;
					
				} else {
					s.pause();
					playing = false;
				}
			}
		});

		return rootView;
	}
	String stuff = "Sons of Gondor! Of Rohan! My brothers. I see in your eyes the same fear that would take the heart of me. A day may come when the courage of Men fails, when we forsake our friends and break all bonds of fellowship, but it is not this day. An hour of wolves and shattered shields when the Age of Men comes crashing down, but it is not this day! This day we fight! By all that you hold dear on this good earth, I bid you stand, Men of the West!";

}
