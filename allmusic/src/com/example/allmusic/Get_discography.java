package com.example.allmusic;

import android.app.ListActivity;

import java.util.*;
import java.io.*;
import java.net.*;

import org.json.*;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.graphics.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.widget.WebDialog;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;

import java.net.MalformedURLException;
import java.net.URL;

import android.media.SoundPool;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;

public class Get_discography extends ListActivity {
	public static final int DEFAULT_THEME = 1;
	String heguang_id = "497236067010123";
	ArrayList<Map<String, Object>> showList;
	ArrayList<Map<String, Object>> storeList;
	String type;
	String title;
	ProgressDialog dialog;
	Boolean enable = false;
	String urlString;
	URL url;
	String json_text;
	int debug = 0;
	Bundle content;
	Dialog pop;
	int index;
	MediaPlayer mp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dialog = new ProgressDialog(this);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setMessage("Loading...");
		dialog.show();

		LoadJSON loader = new LoadJSON();
		loader.execute(this);
	}

	protected class LoadJSON extends AsyncTask<Context, Integer, String> {
		@Override
		protected String doInBackground(Context... arg0) {
			InputStreamReader input = null;
			BufferedReader buffer = null;

			try {
				urlString = getIntent().getExtras().getString("url").trim();
				type = getIntent().getExtras().getString("type");
				url = new URL(urlString);
				json_text = new String();
				HttpURLConnection connect = (HttpURLConnection) url
						.openConnection();

				input = new InputStreamReader(connect.getInputStream());
				buffer = new BufferedReader(input);

				json_text = Html.fromHtml(buffer.readLine()).toString();
				storeList = new ArrayList<Map<String, Object>>();
				showList = new ArrayList<Map<String, Object>>();
				if (type.equals("Artists")) {
					getArtistsList(json_text);
					json_text = type;
				} else if (type.equals("Albums")) {
					getAlbumsList(json_text);
					json_text = type;
				} else if (type.equals("Songs")) {
					getSongsList(json_text);
					json_text = type;
				}
				if (storeList.size() == 0) {
					return "NA";
				}
				return "not_NA";
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return "error:" + e.getMessage();
			} catch (IOException e) {
				e.printStackTrace();
				return "error:" + e.getMessage();
			} catch (Exception e) {
				e.printStackTrace();
				return "error:" + e.getMessage();
			} finally {
				if (input != null) {
					try {
						input.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (buffer != null) {
					try {
						buffer.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		@Override
		protected void onPostExecute(String result) {
			dialog.dismiss();
			if (result.equals("not_NA")) {
				showResult();
			} else {
				showExcept(result);
			}
		}

		private Bitmap downloadBitmap(String urlString) throws IOException {
			URL imgURL = new URL(urlString);
			HttpURLConnection imgConnect = (HttpURLConnection) imgURL
					.openConnection();
			InputStream imgInput = imgConnect.getInputStream();
			Bitmap img = BitmapFactory.decodeStream(imgInput);
			return img;
		}

		void getArtistsList(String text) {
			try {
				JSONObject root = new JSONObject(text);
				JSONObject results = root.getJSONObject("results");
				JSONArray array = results.getJSONArray("result");

				if (array.length() != 0) {
					for (int i = 0; i < array.length(); i++) {
						JSONObject jsonItem = (JSONObject) array.get(i);
						Map<String, Object> showMap = new HashMap<String, Object>();
						Map<String, Object> storeMap = new HashMap<String, Object>();
						Bitmap cover;
						if (!((String) jsonItem.get("cover")).equals("NA")) {
							cover = downloadBitmap((String) jsonItem
									.get("cover"));
							storeMap.put("coverurl_artists_store",
									(String) jsonItem.get("cover"));
						} else {
							cover = downloadBitmap("http://cs-server.usc.edu:38512/noImage_artist.png");
							storeMap.put("coverurl_artists_store",
									"http://cs-server.usc.edu:38512/noImage_artist.png");
						}

						storeMap.put("cover_artists_store", (Object) cover);
						storeMap.put("name_artists_store",
								(String) jsonItem.get("name"));
						storeMap.put("genre_artists_store",
								(String) jsonItem.get("genre"));
						storeMap.put("year_artists_store",
								(String) jsonItem.get("year"));
						storeMap.put("details_artists_store",
								(String) jsonItem.get("details"));

						showMap.put("cover_artists_show",
								storeMap.get("cover_artists_store"));
						showMap.put("name_artists_show",
								"Name: " + storeMap.get("name_artists_store"));
						showMap.put("genre_artists_show",
								"Genre: " + storeMap.get("genre_artists_store"));
						showMap.put("year_artists_show",
								"Year: " + storeMap.get("year_artists_store"));
						debug++;
						storeList.add(storeMap);
						showList.add(showMap);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// System.out.println(storeList.size());
		}

		void getAlbumsList(String json_text) {
			try {
				JSONArray array = ((new JSONObject(json_text))
						.getJSONObject("results").getJSONArray("result"));
				if (array.length() != 0) {
					for (int i = 0; i < array.length(); i++) {
						JSONObject jsonItem = (JSONObject) array.get(i);
						Map<String, Object> showMap = new HashMap<String, Object>();
						Map<String, Object> storeMap = new HashMap<String, Object>();
						Bitmap cover;

						if (!((String) jsonItem.get("cover")).equals("NA")) {
							cover = downloadBitmap((String) jsonItem
									.get("cover"));
							storeMap.put("coverurl_albums_store",
									(String) jsonItem.get("cover"));
						} else {
							cover = downloadBitmap("http://cs-server.usc.edu:38512/noImage_album.png");
							storeMap.put("coverurl_albums_store",
									"http://cs-server.usc.edu:38512/noImage_album.png");
						}
						storeMap.put("cover_albums_store", cover);
						storeMap.put("title_albums_store",
								(String) jsonItem.get("title"));
						storeMap.put("artist_albums_store",
								(String) jsonItem.get("artist"));
						storeMap.put("genre_albums_store",
								(String) jsonItem.get("genre"));
						storeMap.put("year_albums_store",
								(String) jsonItem.get("year"));
						storeMap.put("details_albums_store",
								(String) jsonItem.get("details"));

						showMap.put("cover_albums_show",
								storeMap.get("cover_albums_store"));
						showMap.put("title_albums_show",
								"Title: " + storeMap.get("title_albums_store"));
						showMap.put(
								"artist_albums_show",
								"Artist: "
										+ storeMap.get("artist_albums_store"));
						showMap.put("genre_albums_show",
								"Genre: " + storeMap.get("genre_albums_store"));
						showMap.put("year_albums_show",
								"Year: " + storeMap.get("year_albums_store"));
						storeList.add(storeMap);
						showList.add(showMap);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		void getSongsList(String json_text) {
			try {
				JSONArray array = ((new JSONObject(json_text))
						.getJSONObject("results").getJSONArray("result"));
				if (array.length() != 0) {
					for (int i = 0; i < array.length(); i++) {
						JSONObject jsonItem = (JSONObject) array.get(i);
						Map<String, Object> showMap = new HashMap<String, Object>();
						Map<String, Object> storeMap = new HashMap<String, Object>();
						Bitmap cover;
						cover = downloadBitmap("http://cs-server.usc.edu:38512/noImage_song.png");
						storeMap.put("coverurl_songs_store",
								"http://cs-server.usc.edu:38512/noImage_song.png");
						storeMap.put("cover_songs_store", cover);
						storeMap.put("title_songs_store",
								(String) jsonItem.get("title"));
						storeMap.put("performer_songs_store",
								(String) jsonItem.get("performer"));
						storeMap.put("composers_songs_store",
								(String) jsonItem.get("composers"));
						storeMap.put("details_songs_store",
								(String) jsonItem.get("details"));
						storeMap.put("sample_songs_store",
								(String) jsonItem.get("sample"));

						showMap.put("cover_songs_show",
								storeMap.get("cover_songs_store"));
						showMap.put("title_songs_show",
								"Title: " + storeMap.get("title_songs_store"));
						showMap.put("performer_songs_show", "Performer: "
								+ storeMap.get("performer_songs_store"));
						showMap.put("composers_songs_show", "Composers: "
								+ storeMap.get("composers_songs_store"));
						storeList.add(storeMap);
						showList.add(showMap);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	void showResult() {
		enable = true;
		String[] list_item;
		int[] list_id;
		SimpleAdapter adapter = null;
		if (type.equals("Artists")) {
			list_item = new String[] { "cover_artists_show",
					"name_artists_show", "genre_artists_show",
					"year_artists_show" };
			list_id = new int[] { R.id.cover_artists_show,
					R.id.name_artists_show, R.id.genre_artists_show,
					R.id.year_artists_show };
			adapter = new SimpleAdapter(this, showList,
					R.layout.artists_result, list_item, list_id);
		} else if (type.equals("Albums")) {
			list_item = new String[] { "cover_albums_show",
					"title_albums_show", "artist_albums_show",
					"genre_albums_show", "year_albums_show" };
			list_id = new int[] { R.id.cover_albums_show,
					R.id.title_albums_show, R.id.artist_albums_show,
					R.id.genre_albums_show, R.id.year_albums_show };
			adapter = new SimpleAdapter(this, showList, R.layout.albums_result,
					list_item, list_id);
		} else if (type.equals("Songs")) {
			list_item = new String[] { "cover_songs_show", "title_songs_show",
					"performer_songs_show", "composers_songs_show" };
			list_id = new int[] { R.id.cover_songs_show, R.id.title_songs_show,
					R.id.performer_songs_show, R.id.composers_songs_show };
			adapter = new SimpleAdapter(this, showList, R.layout.songs_result,
					list_item, list_id);
		}
		adapter.setViewBinder(new ViewBinder() {
			@Override
			public boolean setViewValue(View arg0, Object arg1, String arg2) {
				if (arg0 instanceof ImageView && arg1 instanceof Bitmap) {
					ImageView img = (ImageView) arg0;
					img.setImageBitmap((Bitmap) arg1);
					return true;
				} else {
					return false;
				}
			}
		});
		setListAdapter(adapter);
	}

	void showExcept(String result) {
		showList = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		if (result.equals("NA")) {
			map.put("none", "No discography found!");
		} else {
			map.put("none", "Error: " + result + " ,please try again.");
		}
		showList.add(map);
		SimpleAdapter adapter = new SimpleAdapter(this, showList,
				R.layout.no_result, new String[] { "none" },
				new int[] { R.id.none });
		setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		if (!enable)
			return;
		index = (int) id;

		pop = new Dialog(Get_discography.this);
		if (type.equals("Artists")
				|| type.equals("Albums")
				|| (type.equals("Songs") && (storeList.get((int) index)
						.get("sample_songs_store")).equals("NA"))) {
			pop.setTitle("Post to Facebook");
			pop.setContentView(R.layout.post_to_fb);
			Button postButton = (Button) pop.findViewById(R.id.post0);
			postButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Session.openActiveSession(Get_discography.this, true,
							new Session.StatusCallback() {
								// callback when session changes state
								@Override
								public void call(Session session,
										SessionState state, Exception exception) {
									if (session.isOpened()) {
										// make request to the /me API
										Request.executeMeRequestAsync(
												session,
												new Request.GraphUserCallback() {
													// callback after Graph API
													// response with user object
													@Override
													public void onCompleted(
															GraphUser user,
															Response response) {
														if (user != null) {
															publishFeedDialog(storeList
																	.get(index));
														}
													}
												});
									}
								}
							});
				}
			});
			pop.show();
		} else {
			pop.setContentView(R.layout.play_sample);
			pop.setTitle("Post to Facebook");
			Button postButton = (Button) pop.findViewById(R.id.post);
			Button playButton = (Button) pop.findViewById(R.id.play);
			content = new Bundle();
			postButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (mp != null) {
						mp.stop();
						mp.release();
					}
					Session.openActiveSession(Get_discography.this, true,
							new Session.StatusCallback() {
								@Override
								public void call(Session session,
										SessionState state, Exception exception) {
									if (session.isOpened()) {
										Request.executeMeRequestAsync(
												session,
												new Request.GraphUserCallback() {
													@Override
													public void onCompleted(
															GraphUser user,
															Response response) {
														if (user != null) {
															publishFeedDialog(storeList
																	.get((int) index));
														}
													}
												});
									}
								}
							});
				}
			});
			playButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					try {
						mp = new MediaPlayer();
						mp.setAudioStreamType(2);
						Runnable r = new Runnable() {
							public void run() {
								try {
									downloadSample((String) (storeList
											.get((int) index)
											.get("sample_songs_store")));
								} catch (IOException e) {

								}
								try {
									mp.prepare();
								} catch (IllegalStateException e) {

									e.printStackTrace();
								} catch (IOException e) {
									e.printStackTrace();
								}
								mp.start();
							}
						};
						new Thread(r).start();
					} catch (Exception e) {
						if (mp != null) {
							mp.stop();
							mp.release();
						}
					}

				}

			});
			pop.show();
		}
	}

	private void downloadSample(String musicURL) throws IOException {
		URL url = new URL(musicURL);
		URLConnection cn = url.openConnection();
		cn.connect();
		InputStream stream = cn.getInputStream();
		if (stream == null)
			throw new RuntimeException("stream is null");
		File temp = File.createTempFile("mediaplayertmp", "dat");
		String tempPath = temp.getAbsolutePath();
		FileOutputStream out = new FileOutputStream(temp);
		byte buf[] = new byte[128];

		do {
			int numread = stream.read(buf);
			if (numread <= 0)
				break;
			out.write(buf, 0, numread);
		} while (true);
		mp.setDataSource(tempPath);
		try {
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode,
				resultCode, data);
	}

	private void publishFeedDialog(final Map<String, Object> map) {
		if (type.equals("Artists")) {
			content = new Bundle();
			pop.dismiss();
			content.putString("link", (String) map.get("details_artists_store"));
			content.putString("picture",
					(String) map.get("coverurl_artists_store"));
			content.putString("name", (String) map.get("name_artists_store"));
			content.putString(
					"caption",
					"I like " + (String) map.get("name_artists_store")
							+ " who is active since year "
							+ (String) map.get("year_artists_store"));
			content.putString("description", "Genre of Music is: "
					+ (String) map.get("genre_artists_store"));
			JSONObject proper = new JSONObject();
			try {
				proper.put("Look at details:", (new JSONObject().put("text",
						"here")).put("href",
						(String) map.get("details_artists_store")));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			content.putString("properties", proper.toString());
			WebDialog poster = (new WebDialog.FeedDialogBuilder(
					Get_discography.this, Session.getActiveSession(), content))
					.setOnCompleteListener(new OnCompleteListener() {

						@Override
						public void onComplete(Bundle values,
								FacebookException error) {
							if (error == null) {
								final String postId = values
										.getString("post_id");
								if (postId != null) {
									Toast.makeText(Get_discography.this,
											"Posted story, id: " + postId,
											Toast.LENGTH_SHORT).show();
								} else {

									Toast.makeText(
											Get_discography.this
													.getApplicationContext(),
											"Publish cancelled",
											Toast.LENGTH_SHORT).show();
								}
							} else if (error instanceof FacebookOperationCanceledException) {
								Toast.makeText(
										Get_discography.this
												.getApplicationContext(),
										"Publish cancelled", Toast.LENGTH_SHORT)
										.show();
							} else {
								Toast.makeText(
										Get_discography.this
												.getApplicationContext(),
										"Error posting story",
										Toast.LENGTH_SHORT).show();
							}
						}
					}).build();

			poster.show();
		} else if (type.equals("Albums")) {
			pop.dismiss();
			content = new Bundle();
			content.putString("link", (String) map.get("details_albums_store"));
			content.putString("picture",
					(String) map.get("coverurl_albums_store"));
			content.putString("name", (String) map.get("title_albums_store"));
			content.putString(
					"caption",
					"I like " + (String) map.get("title_albums_store")
							+ " released in "
							+ (String) map.get("year_albums_store"));
			content.putString(
					"description",
					"Artist: " + (String) map.get("artist_albums_store")
							+ " Genre: "
							+ (String) map.get("genre_albums_store"));
			JSONObject proper = new JSONObject();
			try {
				proper.put("Look at details:", (new JSONObject().put("text",
						"here")).put("href",
						(String) map.get("details_albums_store")));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			content.putString("properties", proper.toString());
			WebDialog poster = (new WebDialog.FeedDialogBuilder(
					Get_discography.this, Session.getActiveSession(), content))
					.setOnCompleteListener(new OnCompleteListener() {

						@Override
						public void onComplete(Bundle values,
								FacebookException error) {
							if (error == null) {
								final String postId = values
										.getString("post_id");
								if (postId != null) {
									Toast.makeText(Get_discography.this,
											"Posted story, id: " + postId,
											Toast.LENGTH_SHORT).show();
								} else {
									Toast.makeText(
											Get_discography.this
													.getApplicationContext(),
											"Publish cancelled",
											Toast.LENGTH_SHORT).show();
								}
							} else if (error instanceof FacebookOperationCanceledException) {
								Toast.makeText(
										Get_discography.this
												.getApplicationContext(),
										"Publish cancelled", Toast.LENGTH_SHORT)
										.show();
							} else {
								Toast.makeText(
										Get_discography.this
												.getApplicationContext(),
										"Error posting story",
										Toast.LENGTH_SHORT).show();
							}
						}
					}).build();
			poster.show();
		} else {
			pop.dismiss();
			content = new Bundle();
			content.putString("link", (String) map.get("details_songs_store"));
			content.putString("picture",
					(String) map.get("coverurl_songs_store"));
			content.putString("name", (String) map.get("title_songs_store"));
			content.putString(
					"caption",
					"I like " + (String) map.get("title_songs_store")
							+ " composed by "
							+ (String) map.get("composers_songs_store"));
			content.putString("description",
					"Performer: " + (String) map.get("performer_songs_store"));
			JSONObject proper = new JSONObject();
			try {
				proper.put("Look at details:", (new JSONObject().put("text",
						"here")).put("href",
						(String) map.get("details_songs_store")));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			content.putString("properties", proper.toString());

			WebDialog poster = (new WebDialog.FeedDialogBuilder(
					Get_discography.this, Session.getActiveSession(), content))
					.setOnCompleteListener(new OnCompleteListener() {

						@Override
						public void onComplete(Bundle values,
								FacebookException error) {
							if (error == null) {
								final String postId = values
										.getString("post_id");
								if (postId != null) {
									Toast.makeText(Get_discography.this,
											"Posted story, id: " + postId,
											Toast.LENGTH_SHORT).show();
								} else {
									Toast.makeText(
											Get_discography.this
													.getApplicationContext(),
											"Publish cancelled",
											Toast.LENGTH_SHORT).show();
								}
							} else if (error instanceof FacebookOperationCanceledException) {
								Toast.makeText(
										Get_discography.this
												.getApplicationContext(),
										"Publish cancelled", Toast.LENGTH_SHORT)
										.show();
							} else {
								Toast.makeText(
										Get_discography.this
												.getApplicationContext(),
										"Error posting story",
										Toast.LENGTH_SHORT).show();
							}
						}
					}).build();
			poster.show();
		}
	}

}
