package com.example.memorybread;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import com.googlecode.tesseract.android.TessBaseAPI;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class AddActivity extends Activity {

	/******** ����ʶ�𲿷� *************/
	private static final int PHOTO_CAPTURE = 0x11;// ����
	private static final int PHOTO_RESULT = 0x12;// ���

	private static String LANGUAGE = "eng";
	private static String IMG_PATH = getSDPath() + java.io.File.separator
			+ "ocrtest";
	private static String TESSDATA_PATH = "/storage/sdcard0/";

	private static EditText tvResult;
	private static ImageButton btnCamera;
	private static ImageButton btnSelect;
	private static CheckBox chPreTreat;
	private static RadioGroup radioGroup;
	private static String textResult;
	private static Bitmap bitmapSelected;
	private static Bitmap bitmapTreated;
	private static final int SHOWRESULT = 0x101;
	private static final int SHOWTREATEDIMG = 0x102;
	// public static String p_name;

	private EditText para_name;
	private Button finish;
	private int already_used = 0;

	// ��handler���ڴ����޸Ľ��������
	public static Handler myHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOWRESULT:
				if (textResult.equals(""))
					tvResult.setText("ʶ��ʧ��");
				else
					tvResult.setText(textResult);
				break;
			case SHOWTREATEDIMG:
				tvResult.setText("ʶ����......");
				break;
			}
			super.handleMessage(msg);
		}
	};

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				JSONObject j;
				String str = null;
				Boolean success = null;
				try {
					j = new JSONObject(msg.obj.toString());
					str = j.getString("msg").toString();
					success = j.getBoolean("success");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// String response = (String)msg.obj;
				if (success) {
					// in = "yes";
					// p_name = para_name.getText().toString();
					already_used = 0;
					Toast.makeText(AddActivity.this, "�ɹ�����", Toast.LENGTH_SHORT)
							.show();
					// Intent i = new
					// Intent(AddActivity.this,ReciteListActivity.class);
					finish();
					// startActivity(i);

				} else {
					already_used = 1;
					Toast.makeText(AddActivity.this, "���ı������ѱ�ʹ��",
							Toast.LENGTH_SHORT).show();
				}
				break;
			default:
				break;
			}
		}
	};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add);
		ActionBar actionBar = getActionBar();
		actionBar.hide();

		para_name = (EditText) findViewById(R.id.et_paragraph_name);
		finish = (Button) findViewById(R.id.bt_finish);

		// ���ļ��в����� ���ȴ����ļ���
		File path = new File(IMG_PATH);
		if (!path.exists()) {
			path.mkdirs();
		}

		tvResult = (EditText) findViewById(R.id.tv_result);

		btnCamera = (ImageButton) findViewById(R.id.btn_camera);
		btnSelect = (ImageButton) findViewById(R.id.btn_select);
		radioGroup = (RadioGroup) findViewById(R.id.radiogroup);

		btnCamera.setOnClickListener(new cameraButtonListener());
		btnSelect.setOnClickListener(new selectButtonListener());

		// �������ý�������
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rb_en:
					LANGUAGE = "eng";
					break;
				case R.id.rb_ch:
					LANGUAGE = "chi_sim";
					break;
				}
			}
		});

		finish.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String pname = para_name.getText().toString();
				String cont = tvResult.getText().toString();
				if (pname.equals("")) {
					Toast.makeText(v.getContext(), "�����������ְ�",
							Toast.LENGTH_SHORT).show();
				} else if (cont.equals("")) {
					Toast.makeText(v.getContext(), "�����", Toast.LENGTH_SHORT)
							.show();
				} else {
					sendTo(LoginActivity.account, pname, cont);
				}
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add, menu);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_CANCELED)
			return;
		if (requestCode == PHOTO_CAPTURE) {
			tvResult.setText("abc");
			startPhotoCrop(Uri.fromFile(new File(IMG_PATH, "temp.jpg")));
		}
		// ������
		if (requestCode == PHOTO_RESULT) {
			bitmapSelected = decodeUriAsBitmap(Uri.fromFile(new File(IMG_PATH,
					"temp_cropped.jpg")));
			tvResult.setText("ʶ����......");
			// ���߳�������ʶ��
			new Thread(new Runnable() {
				@Override
				public void run() {
					bitmapTreated = ImgPretreatment
							.doPretreatment(bitmapSelected);
					Message msg = new Message();
					msg.what = SHOWTREATEDIMG;
					myHandler.sendMessage(msg);
					textResult = doOcr(bitmapTreated, LANGUAGE);
					Message msg2 = new Message();
					msg2.what = SHOWRESULT;
					myHandler.sendMessage(msg2);
				}
			}).start();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	// ����ʶ��
	class cameraButtonListener implements OnClickListener {
		@Override
		public void onClick(View arg0) {
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(new File(IMG_PATH, "temp.jpg")));
			startActivityForResult(intent, PHOTO_CAPTURE);
		}
	};

	// �����ѡȡ��Ƭ���ü�
	class selectButtonListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.addCategory(Intent.CATEGORY_OPENABLE);
			intent.setType("image/*");
			intent.putExtra("crop", "true");
			intent.putExtra("scale", true);
			intent.putExtra("return-data", false);
			intent.putExtra(MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(new File(IMG_PATH, "temp_cropped.jpg")));
			intent.putExtra("outputFormat",
					Bitmap.CompressFormat.JPEG.toString());
			intent.putExtra("noFaceDetection", true); // no face detection
			startActivityForResult(intent, PHOTO_RESULT);
		}
	}

	/**
	 * ����ͼƬʶ��
	 * 
	 * @param bitmap
	 *            ��ʶ��ͼƬ
	 * @param language
	 *            ʶ������
	 * @return ʶ�����ַ���
	 */
	public String doOcr(Bitmap bitmap, String language) {
		TessBaseAPI baseApi = new TessBaseAPI();
		baseApi.init(TESSDATA_PATH, language);
		// ����Ӵ��У�tess-twoҪ��BMP����Ϊ������
		bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
		baseApi.setImage(bitmap);
		String text = baseApi.getUTF8Text();
		baseApi.clear();
		baseApi.end();
		return text;
	}

	/**
	 * ��ȡsd����·��
	 * 
	 * @return ·�����ַ���
	 */
	public static String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // �ж�sd���Ƿ����
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();// ��ȡ���Ŀ¼
		}
		Log.d("sdDir", sdDir.toString());
		return sdDir.toString();
	}

	/**
	 * ����ϵͳͼƬ�༭���вü�
	 */
	public void startPhotoCrop(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("scale", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT,
				Uri.fromFile(new File(IMG_PATH, "temp_cropped.jpg")));
		intent.putExtra("return-data", false);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		startActivityForResult(intent, PHOTO_RESULT);
	}

	/**
	 * ����URI��ȡλͼ
	 * 
	 * @param uri
	 * @return ��Ӧ��λͼ
	 */
	private Bitmap decodeUriAsBitmap(Uri uri) {
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(getContentResolver()
					.openInputStream(uri));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}

	private void sendTo(final String _account, final String _pname,
			final String _cont) {
		new Thread(new Runnable() {
			public void run() {
				HttpURLConnection connection = null;
				try {
					URL url = new URL("http://115.28.204.167:8000/add");
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("POST");
					DataOutputStream out = new DataOutputStream(
							connection.getOutputStream());
					String account = "";
					String pname = "";
					String cont = "";

					account = URLEncoder.encode(_account, "UTF-8");
					pname = URLEncoder.encode(_pname, "UTF-8");
					cont = URLEncoder.encode(_cont, "UTF-8");

					out.writeBytes("account=" + account + "&pname=" + pname
							+ "&content=" + cont);

					InputStream in = connection.getInputStream();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(in));
					StringBuilder response = new StringBuilder();
					String line;
					while ((line = reader.readLine()) != null) {
						response.append(line);
					}
					Message message = new Message();
					message.what = 1;
					message.obj = response.toString();
					handler.sendMessage(message);

				} catch (Exception e) {
					e.printStackTrace();

				} finally {
					if (connection != null)
						connection.disconnect();
				}
			}
		}).start();
	}
}
