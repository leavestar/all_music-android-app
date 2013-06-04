package com.example.allmusic;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

import com.example.allmusic.R;

public class MainActivity extends Activity {
	private String types[];
	private Button button;
	private Spinner spinner;
	private EditText edittext;
	private ArrayAdapter<String> adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		types = new String[] { "Artists", "Albums", "Songs" };
		spinner = (Spinner) findViewById(R.id.type);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, types);
		spinner.setAdapter(adapter);

		edittext = (EditText) findViewById(R.id.title);

		button = (Button) findViewById(R.id.submit);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String title = edittext.getText().toString();
				String type = spinner.getSelectedItem().toString();
				if (title.equals("")) {
					Toast.makeText(MainActivity.this,
							"Please refine your input.", Toast.LENGTH_LONG)
							.show();
				} else {
					try {
						URL url = null;
						if (type == "Artists") {
							url = new URL(
									"http://cs-server.usc.edu:38513/examples/servlet/HelloWorldExample?title="
											+ URLEncoder.encode(title, "utf-8")
											+ "&type=artists");
						} else if (type == "Albums") {
							url = new URL(
									"http://cs-server.usc.edu:38513/examples/servlet/HelloWorldExample?title="
											+ URLEncoder.encode(title, "utf-8")
											+ "&type=albums");
						} else {
							url = new URL(
									"http://cs-server.usc.edu:38513/examples/servlet/HelloWorldExample?title="
											+ URLEncoder.encode(title, "utf-8")
											+ "&type=songs");
						}

						Intent intent = new Intent(MainActivity.this,
								Get_discography.class);
						intent.putExtra("url", url.toString());
						intent.putExtra("title", title);
						intent.putExtra("type", type);
						startActivity(intent);
					} catch (UnsupportedEncodingException e) {
						Toast.makeText(
								MainActivity.this,
								"Error: UnsupportedEncodingException: "
										+ e.getMessage(), Toast.LENGTH_LONG)
								.show();
					} catch (Exception e) {
						Toast.makeText(MainActivity.this,
								"Error: Exception: " + e.getMessage(),
								Toast.LENGTH_LONG).show();
					}
				}
			}
		});
	}

}
