package com.comparty.photon;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.comparty.photon.spritz.Spritzer;
import com.comparty.photon.spritz.SpritzerTextView;

public class SpritzFragment extends Fragment {
	Spritzer s;

	boolean playing = false;

	ImageButton startButton;
	TextView wpmText;

	ProgressBar progress;
	LinearLayout layout;
	
	CameraFragment cf;
	
	public interface Callback {
		public void done();
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_spritz, container,
				false);
		
		layout = (LinearLayout) rootView.findViewById(R.id.spritzer_layout);
		progress = (ProgressBar) rootView.findViewById(R.id.progress);
		
		SpritzerTextView textView = (SpritzerTextView) rootView.findViewById(R.id.spritzer);

		s = new Spritzer(textView);
		s.setCallback(new Callback() {
			public void done() {
				startButton.setImageResource(R.drawable.ic_media_play);
				playing = false;
			}
		});

		//s.setText(stuff);
		s.setWpm(((MainActivity) getActivity()).wpm);

		startButton = (ImageButton) rootView.findViewById(R.id.start_pause);
		startButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (!playing) {
					s.start();
					playing = true;
					startButton.setImageResource(R.drawable.ic_media_pause);
				} else {
					s.pause();
					playing = false;
					startButton.setImageResource(R.drawable.ic_media_play);
				}
			}
		});

		ImageButton restartButton = (ImageButton) rootView.findViewById(R.id.restart);
		restartButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				s.clearText();
				s.setText(((MainActivity) getActivity()).currentText);
				s.start();
			}
		});

		ImageButton close = (ImageButton) rootView.findViewById(R.id.close);
		close.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				ft.setCustomAnimations(R.animator.nochange, R.animator.alpha_out);
				ft.remove(SpritzFragment.this);
				ft.commit();
				
				cf.startPreview();
			}
		});

		Button plus = (Button) rootView.findViewById(R.id.faster);
		plus.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				((MainActivity) getActivity()).wpm += 50;
				if (((MainActivity) getActivity()).wpm > 1500)
					((MainActivity) getActivity()).wpm = 1500;

				s.setWpm(((MainActivity) getActivity()).wpm);
				wpmText.setText(((MainActivity) getActivity()).wpm+"wpm");
			}
		});

		Button minus = (Button) rootView.findViewById(R.id.slower);
		minus.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				((MainActivity) getActivity()).wpm -= 50;
				if (((MainActivity) getActivity()).wpm < 100)
					((MainActivity) getActivity()).wpm = 100;
				s.setWpm(((MainActivity) getActivity()).wpm);
				wpmText.setText(((MainActivity) getActivity()).wpm+"wpm");
			}
		});

		wpmText = (TextView) rootView.findViewById(R.id.speed);
		wpmText.setText(((MainActivity) getActivity()).wpm+"wpm");


		return rootView;
	}
	
	public void setCameraFragment(CameraFragment c) {
		this.cf = c;
	}
	
	public void showProgress() {
		layout.setVisibility(View.GONE);
		progress.setVisibility(View.VISIBLE);
	}
	
	public void showSpritzer() {
		layout.setVisibility(View.VISIBLE);
		progress.setVisibility(View.GONE);
		
		s.setText(((MainActivity) getActivity()).currentText);
	}
	String stuff = "Sons of Gondor! Of Rohan! My brothers. I see in your eyes the same fear that would take the heart of me. A day may come when the courage of Men fails, when we forsake our friends and break all bonds of fellowship, but it is not this day. An hour of wolves and shattered shields when the Age of Men comes crashing down, but it is not this day! This day we fight! By all that you hold dear on this good earth, I bid you stand, Men of the West!";

}
