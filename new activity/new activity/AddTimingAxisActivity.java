package com.example.memorybread;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;

public class AddTimingAxisActivity extends Activity implements OnClickListener{
	private ImageButton contest,dinner,exam,exp,travel,email,meeting,interview,date,others;
	private ImageView imageView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_timing_axis);
		initial();
	}
	
	private void initial() {
		contest = (ImageButton) findViewById(R.id.time_contest);
		contest.setOnClickListener(this);
		dinner = (ImageButton) findViewById(R.id.time_dinner);
		dinner.setOnClickListener(this);
		exam = (ImageButton) findViewById(R.id.time_exam);
		exam.setOnClickListener(this);
		exp = (ImageButton) findViewById(R.id.time_exp);
		exp.setOnClickListener(this);
		travel = (ImageButton) findViewById(R.id.time_travel);
		travel.setOnClickListener(this);
		email = (ImageButton) findViewById(R.id.time_email);
		email.setOnClickListener(this);
		meeting = (ImageButton) findViewById(R.id.time_meeting);
		meeting.setOnClickListener(this);
		interview = (ImageButton) findViewById(R.id.time_interview);
		interview.setOnClickListener(this);
		date = (ImageButton) findViewById(R.id.time_date);
		date.setOnClickListener(this);
		others = (ImageButton) findViewById(R.id.time_others);
		others.setOnClickListener(this);
		imageView = (ImageView) findViewById(R.id.time_imgview);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.time_contest:
			imageView.setBackgroundResource(R.drawable.time_contest);
			break;
		case R.id.time_dinner:
			imageView.setBackgroundResource(R.drawable.time_dinner);
			break;
		case R.id.time_exam:
			imageView.setBackgroundResource(R.drawable.time_exam);
			break;
		case R.id.time_exp:
			imageView.setBackgroundResource(R.drawable.time_exp);
			break;
		case R.id.time_travel:
			imageView.setBackgroundResource(R.drawable.time_travel);
			break;
		case R.id.time_email:
			imageView.setBackgroundResource(R.drawable.time_email);
			break;
		case R.id.time_meeting:
			imageView.setBackgroundResource(R.drawable.time_meeting);
			break;
		case R.id.time_interview:
			imageView.setBackgroundResource(R.drawable.time_interview);
			break;
		case R.id.time_date:
			imageView.setBackgroundResource(R.drawable.time_date);
			break;
		case R.id.time_others:
			imageView.setBackgroundResource(R.drawable.time_others);
			break;
		default:
			break;
		}
	}
}
