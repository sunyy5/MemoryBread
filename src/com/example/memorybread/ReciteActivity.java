package com.example.memorybread;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.baidu.tts.answer.auth.AuthInfo;
import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizeBag;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ReciteActivity extends Activity implements SpeechSynthesizerListener,OnClickListener{
	
	private ListView lv;
	private List<Map<String, String>> paragraphs = new ArrayList<Map<String, String>>();
	String[] sub;           //记录每一句
	String[] blank;         //记录每一句对应的空白行
	int[] p;                //记录是否被点击过
	private SeekBar seekBar;
	int recite_level;
	Random rnd;
	static String content;
	static String content_for_voice;
	private String getFromListPname;
	private String getFromListPcontent;
	
	/********语音合成部分*************/
	private ImageButton mSpeak, manVoice, womanVoice;
    private EditText mInput;

    private SpeechSynthesizer mSpeechSynthesizer;
    private String mSampleDirPath;
    private static final String SAMPLE_DIR_NAME = "baiduTTS";
    private static final String SPEECH_FEMALE_MODEL_NAME = "bd_etts_speech_female.dat";
    private static final String SPEECH_MALE_MODEL_NAME = "bd_etts_speech_male.dat";
    private static final String TEXT_MODEL_NAME = "bd_etts_text.dat";
    private static final String LICENSE_FILE_NAME = "temp_license";
    private static final String ENGLISH_SPEECH_FEMALE_MODEL_NAME = "bd_etts_speech_female_en.dat";
    private static final String ENGLISH_SPEECH_MALE_MODEL_NAME = "bd_etts_speech_male_en.dat";
    private static final String ENGLISH_TEXT_MODEL_NAME = "bd_etts_text_en.dat";

    private static final int PRINT = 0;
    private static final String TAG = "MainActivity";
	boolean finish = true;
	boolean pause = true;
	private finishhandler fhandler;
	String sex = "0";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recite);
		Intent intent = getIntent();
		getFromListPname = intent.getStringExtra("_pname");
		getFromListPcontent = intent.getStringExtra("_pcontent");
		Log.d("test2", "test");
		Log.d("test2", getFromListPname);
		Log.d("test2", getFromListPcontent);
		
		initialEnv();
        initialTts();
        fhandler = new finishhandler();
        mSpeak = (ImageButton) findViewById(R.id.bt_play);
        mSpeak.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(finish) {
					speak();
					finish = false;
					mSpeak.setBackgroundResource(R.drawable.pause);
				} else if(!finish && !pause) {
					pause();
					mSpeak.setBackgroundResource(R.drawable.play);
				} else if(!finish && pause) {
					resume();
					mSpeak.setBackgroundResource(R.drawable.pause);
				}
			}
		});
        manVoice = (ImageButton) findViewById(R.id.bt_man);
        womanVoice = (ImageButton) findViewById(R.id.bt_woman);
        
        manVoice.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				sex = "1";
				manVoice.setBackgroundResource(R.drawable.man2);
				womanVoice.setBackgroundResource(R.drawable.woman);
			}
		});
        
        
        womanVoice.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				sex = "0";
				manVoice.setBackgroundResource(R.drawable.man);
				womanVoice.setBackgroundResource(R.drawable.woman2);
			}
		});
        
        
        
		seekBar = (SeekBar)findViewById(R.id.recite_level);
		seekBar.setMax(9);
		seekBar.setProgress(0);
		seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {				
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
			}
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
			}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				// TODO Auto-generated method stub
				if(fromUser){
					//背诵等级
					recite_level = seekBar.getProgress();
					updateList();
				}
			}
		});
		
		rnd = new Random();
		lv = (ListView)findViewById(R.id.listView_paragraph);
		initList();
		updateList();
		
        
	}
	
	private void onItemClick_update() {
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				TextView tx = (TextView)view.findViewById(R.id.itm_id);
				Map<String, String> map = new HashMap<String, String>();
				Integer i = Integer.valueOf(tx.getText().toString());
				p[i] = 1;
				setSentence(i, false);
				((SimpleAdapter)lv.getAdapter()).notifyDataSetChanged();
			}
		});
	}
	
	//初始化列表:列表的每一项是一个句子
    private void initList(){
    	//取出数据库中这一个段落的内容
    	content = content_for_voice = getFromListPcontent;
    	//以逗号为间隔划为段落
    	sub = content.split(",|，");
    	blank = new String[sub.length];
    	p = new int[sub.length];
    	for(int i = 0; i < sub.length - 1; i++) {
    		sub[i] = sub[i] + ",";
    		blank[i] = new String("");
    		for(int j = 0; j < sub[i].length() - 1; j++) {
    			blank[i] += "__";
    		}
    		blank[i] += "_,";
    		p[i] = 0;
    	}
    	blank[sub.length - 1] = new String("________________________.");

    	//转换为 Adapter 的数据格式 （List<Map<String,String>>）
    	for (Integer i = 0; i < sub.length; i++){
    		Map<String, String> map = new HashMap<String, String>();
    		map.put("_id", i.toString());
    		map.put("sentence", sub[i]);
    		paragraphs.add(map);
    	}
    	SimpleAdapter adapter = new SimpleAdapter(this, paragraphs, R.layout.sentence_item
                 , new String[]{"_id","sentence"}, new int[]{R.id.itm_id, R.id.textView_each_sentence});
    	lv.setAdapter(adapter);
    	onItemClick_update();
    }
    
    private void setSentence(Integer i, boolean setBlank) {
    	Map<String, String> map = new HashMap<String, String>();
    	map.put("_id", i.toString());
    	if(setBlank == true)
    		map.put("sentence", blank[i]);
    	else
    		map.put("sentence", sub[i]);
		paragraphs.set(i, map);
    }
    
    private void updateList() {
		int random_number;
		String content = "";
		for(Integer i = 0; i < sub.length; i++) {
			random_number = rnd.nextInt(10);//返回0-9之间的随机数
			if(random_number >= recite_level) {
				//本来应该显示，但是进一步判断是否是之前没有背出来的
				if(p[i] == 1) {//如果是，就空出
					setSentence(i, true);
				} else {
					setSentence(i, false);
				}
			} else {
				setSentence(i, true);
			}
		}
    	// 刷新ListView
    	((SimpleAdapter)lv.getAdapter()).notifyDataSetChanged();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.recite, menu);
		return true;
	}
	

    private void initialEnv() {
        if (mSampleDirPath == null) {
            String sdcardPath = Environment.getExternalStorageDirectory().toString();
            mSampleDirPath = sdcardPath + "/" + SAMPLE_DIR_NAME;
        }
        makeDir(mSampleDirPath);
        copyFromAssetsToSdcard(false, SPEECH_FEMALE_MODEL_NAME, mSampleDirPath + "/" + SPEECH_FEMALE_MODEL_NAME);
        copyFromAssetsToSdcard(false, SPEECH_MALE_MODEL_NAME, mSampleDirPath + "/" + SPEECH_MALE_MODEL_NAME);
        copyFromAssetsToSdcard(false, TEXT_MODEL_NAME, mSampleDirPath + "/" + TEXT_MODEL_NAME);
        copyFromAssetsToSdcard(false, LICENSE_FILE_NAME, mSampleDirPath + "/" + LICENSE_FILE_NAME);
        copyFromAssetsToSdcard(false, "english/" + ENGLISH_SPEECH_FEMALE_MODEL_NAME, mSampleDirPath + "/"
                + ENGLISH_SPEECH_FEMALE_MODEL_NAME);
        copyFromAssetsToSdcard(false, "english/" + ENGLISH_SPEECH_MALE_MODEL_NAME, mSampleDirPath + "/"
                + ENGLISH_SPEECH_MALE_MODEL_NAME);
        copyFromAssetsToSdcard(false, "english/" + ENGLISH_TEXT_MODEL_NAME, mSampleDirPath + "/"
                + ENGLISH_TEXT_MODEL_NAME);
    }

    private void makeDir(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }
    
    /**
     * 将sample工程需要的资源文件拷贝到SD卡中使用（授权文件为临时授权文件，请注册正式授权）
     * 
     * @param isCover 是否覆盖已存在的目标文件
     * @param source
     * @param dest
     */
    private void copyFromAssetsToSdcard(boolean isCover, String source, String dest) {
        File file = new File(dest);
        if (isCover || (!isCover && !file.exists())) {
            InputStream is = null;
            FileOutputStream fos = null;
            try {
                is = getResources().getAssets().open(source);
                String path = dest;
                fos = new FileOutputStream(path);
                byte[] buffer = new byte[1024];
                int size = 0;
                while ((size = is.read(buffer, 0, 1024)) >= 0) {
                    fos.write(buffer, 0, size);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void initialTts() {
        this.mSpeechSynthesizer = SpeechSynthesizer.getInstance();
        this.mSpeechSynthesizer.setContext(this);
        this.mSpeechSynthesizer.setSpeechSynthesizerListener(this);
        // 文本模型文件路径 (离线引擎使用)
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, mSampleDirPath + "/"
                + TEXT_MODEL_NAME);
        // 声学模型文件路径 (离线引擎使用)
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, mSampleDirPath + "/"
                + SPEECH_FEMALE_MODEL_NAME);
        // 本地授权文件路径,如未设置将使用默认路径.设置临时授权文件路径，LICENCE_FILE_NAME请替换成临时授权文件的实际路径，仅在使用临时license文件时需要进行设置，如果在[应用管理]中开通了离线授权，不需要设置该参数，建议将该行代码删除（离线引擎）
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_LICENCE_FILE, mSampleDirPath + "/"
                + LICENSE_FILE_NAME);
        // 请替换为语音开发者平台上注册应用得到的App ID (离线授权)
        this.mSpeechSynthesizer.setAppId("7436440");
        // 请替换为语音开发者平台注册应用得到的apikey和secretkey (在线授权)
        this.mSpeechSynthesizer.setApiKey("dKBvmEFS1qSzuMRo9pP7YkOG", "9163c2194135946f0569bf294339b29e");
        // 授权检测接口
        AuthInfo authInfo = this.mSpeechSynthesizer.auth(TtsMode.MIX);
        if (authInfo.isSuccess()) {
            this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, SpeechSynthesizer.SPEAKER_FEMALE);
            mSpeechSynthesizer.initTts(TtsMode.MIX);
        } else {
            String errorMsg = authInfo.getTtsError().getDetailMessage();
        }
    }

    /* 
     * 
     */
    @Override
    protected void onDestroy() {
        this.mSpeechSynthesizer.release();
        super.onDestroy();
    }


    private void speak() {
    	this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, sex);
        int result = this.mSpeechSynthesizer.speak(content_for_voice);
        pause = false;
    }

    private void pause() {
        this.mSpeechSynthesizer.pause();
        pause = true;
    }

    private void resume() {
    	this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, sex);
        this.mSpeechSynthesizer.resume();
        pause = false;
    }

    private void stop() {
        this.mSpeechSynthesizer.stop();
    }

    private void synthesize() {
        String text = this.mInput.getText().toString();
        int result = this.mSpeechSynthesizer.synthesize(text);
    }

    private void batchSpeak() {
        List<SpeechSynthesizeBag> bags = new ArrayList<SpeechSynthesizeBag>();
        bags.add(getSpeechSynthesizeBag("123456", "0"));
        bags.add(getSpeechSynthesizeBag("浣犲ソ", "1"));
        bags.add(getSpeechSynthesizeBag("浣跨敤鐧惧害璇煶鍚堟垚SDK", "2"));
        bags.add(getSpeechSynthesizeBag("hello", "3"));
        bags.add(getSpeechSynthesizeBag("杩欐槸涓�涓猟emo宸ョ▼", "4"));
        int result = this.mSpeechSynthesizer.batchSpeak(bags);
    }

    private SpeechSynthesizeBag getSpeechSynthesizeBag(String text, String utteranceId) {
        SpeechSynthesizeBag speechSynthesizeBag = new SpeechSynthesizeBag();
        speechSynthesizeBag.setText(text);
        speechSynthesizeBag.setUtteranceId(utteranceId);
        return speechSynthesizeBag;
    }

    @Override
    public void onSynthesizeStart(String utteranceId) {
    }


    @Override
    public void onSynthesizeDataArrived(String utteranceId, byte[] data, int progress) {
    }

    @Override
    public void onSynthesizeFinish(String utteranceId) {
    }

    @Override
    public void onSpeechStart(String utteranceId) {
    }

    @Override
    public void onSpeechProgressChanged(String utteranceId, int progress) {
    }
    
	public class finishhandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle bundle = msg.getData();
			boolean f = bundle.getBoolean("finish");
			if(f) {
				mSpeak.setBackgroundResource(R.drawable.play1);
			}
		}
	}
	
    @Override
    public void onSpeechFinish(String utteranceId) {
    	finish = true;
    	Bundle bundle = new Bundle();
    	Message msg = new Message();
    	bundle.putBoolean("finish", true);
    	msg.setData(bundle);
    	ReciteActivity.this.fhandler.sendMessage(msg);
    }
    
    @Override
    public void onError(String utteranceId, SpeechError error) {
    }

	@Override
	public void onClick(View v) {
	}
	


}
