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
import android.app.Activity;
import android.content.Intent;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ReciteActivity extends Activity implements SpeechSynthesizerListener,OnClickListener{
	
	/**背诵部分**/
	Integer _id;
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
	
	/********语音合成部分*************/
	private Button mSpeak, mPause, mResume, mStop, mSynthesize, mPlay, mBatchSpeak, mNextActivity;
    private EditText mInput;
    private TextView mShowText;

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
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recite);
		Intent intent = getIntent();
		_id = intent.getIntExtra("_id", -1);
		initialEnv();
        initialTts();
        ParagraphDAO dao = new ParagraphDAO(this);
		Paragraph pag = dao.getById(_id);
		content_for_voice = pag.getPcontent();
        mSpeak = (Button) findViewById(R.id.bt_voice);
        mSpeak.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d("mspeak","anle");
				speak();
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
    	     /*content  = content_for_voice = "华安上小学第一天，我和他手牵着手，穿过好几条街，到维多利亚小学。" +
				"九月初，家家户户院子里的苹果和梨树都缀满了拳头大小的果子，枝丫因为负重而沉沉下垂，越出了树篱，" +
				"勾到过路行人的头发。很多很多的孩子，在操场上等候上课的第一声铃响。小小的手，圈在爸爸的、妈妈的手心里，" +
				"怯怯的眼神，打量着周遭。他们是幼稚园的毕业生，但是他们还不知道一个定律：一件事情的毕业，永远是另一件事情的开启。" +
				"铃声一响，顿时人影错杂，奔往不同方向，但是在那么多穿梭纷乱的人群里，" +
				"我无比清楚地看着自己孩子的背影——就好象在一百个婴儿同时哭声大作时，你仍旧能够准确听出自己那一个的位置。" +
				"华安背着一个五颜六色的书包往前走，但是他不断地回头；好象穿越一条无边无际的时空长河，" +
				"他的视线和我凝望的眼光隔空交会。我看着他瘦小的背影消失在门里。十六岁，他到美国作交换生一年。" +
				"我送他到机场。告别时，照例拥抱，我的头只能贴到他的胸口，好象抱住了长颈鹿的脚。他很明显地在勉强忍受母亲的深情。" +
				"他在长长的行列里，等候护照检验；我就站在外面，用眼睛跟着他的背影一寸一寸往前挪。" +
				"终于轮到他，在海关窗口停留片刻，然后拿回护照，闪入一扇门，倏乎不见。我一直在等候，等候他消失前的回头一瞥。" +
				"但是他没有，一次都没有。现在他二十一岁，上的大学，正好是我教课的大学。但即使是同路，他也不愿搭我的车。" +
				"即使同车，他戴上耳机——只有一个人能听的音乐，是一扇紧闭的门。有时他在对街等候公车，我从高楼的窗口往下看：" +
				"一个高高瘦瘦的青年，眼睛望向灰色的海；我只能想象，他的内在世界和我的一样波涛深邃，但是，我进不去。" +
				"一会儿公车来了，挡住了他的身影。车子开走，一条空荡荡的街，只立着一只邮筒。" +
				"我慢慢地、慢慢地了解到，所谓父女母子一场，只不过意味着，你和他的缘分就是今生今世不断地在目送他的背影渐行渐远。" +
				"你站立在小路的这一端，看着他逐渐消失在小路转弯的地方，而且，他用背影默默告诉你：不必追。" +
				"我慢慢地、慢慢地意识到，我的落寞，仿佛和另一个背影有关。博士学位读完之后，我回台湾教书。" +
				"到大学报到第一天，父亲用他那辆运送饲料的廉价小货车长途送我。" +
				"到了我才发觉，他没开到大学正门口，而是停在侧门的窄巷边。" +
				"卸下行李之后，他爬回车内，准备回去，明明启动了引擎，却又摇下车窗，" +
				"头伸出来说：“女儿，爸爸觉得很对不起你，这种车子实在不是送大学教授的车子。 " +
				"”我看着他的小货车小心地倒车，然后噗噗驶出巷口，留下一团黑烟。直到车子转弯看不见了，" +
				"我还站在那里，一口皮箱旁。每个礼拜到医院去看他，是十几年后的时光了。推着他的轮椅散步，" +
				"他的头低垂到胸口。有一次，发现排泄物淋满了他的裤腿，我蹲下来用自己的手帕帮他擦拭，裙子也沾上了粪便，" +
				"但是我必须就这样赶回台北上班。护士接过他的轮椅，我拎起皮包，看着轮椅的背影，在自动玻璃门前稍停，然后没入门后。" +
				"我总是在暮色沉沉中奔向机场。火葬场的炉门前，棺木是一只巨大而沉重的抽屉，缓缓往前滑行。没有想到可以站得那么近，" +
				"距离炉门也不过五公尺。雨丝被风吹斜，飘进长廊内。我掠开雨湿了前额的头发，深深、深深地凝望，" +
				"希望记得这最后一次的目送。我慢慢地、慢慢地了解到，所谓父女母子一场，只不过意味着，" +
				"你和他的缘分就是今生今世不断地在目送他的背影渐行渐远。你站立在小路的这一端，" +
				"看着他逐渐消失在小路转弯的地方，而且，他用背影默默告诉你：不必追。";*/
    	ParagraphDAO dao = new ParagraphDAO(this);
		Paragraph pag = dao.getById(_id);
		content = content_for_voice = pag.getPcontent();
		
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
     * 灏唖ample宸ョ▼闇�瑕佺殑璧勬簮鏂囦欢鎷疯礉鍒癝D鍗′腑浣跨敤锛堟巿鏉冩枃浠朵负涓存椂鎺堟潈鏂囦欢锛岃娉ㄥ唽姝ｅ紡鎺堟潈锛�
     * 
     * @param isCover 鏄惁瑕嗙洊宸插瓨鍦ㄧ殑鐩爣鏂囦欢
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
        // 鏂囨湰妯″瀷鏂囦欢璺緞 (绂荤嚎寮曟搸浣跨敤)
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, mSampleDirPath + "/"
                + TEXT_MODEL_NAME);
        // 澹板妯″瀷鏂囦欢璺緞 (绂荤嚎寮曟搸浣跨敤)
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, mSampleDirPath + "/"
                + SPEECH_FEMALE_MODEL_NAME);
        // 鏈湴鎺堟潈鏂囦欢璺緞,濡傛湭璁剧疆灏嗕娇鐢ㄩ粯璁よ矾寰�.璁剧疆涓存椂鎺堟潈鏂囦欢璺緞锛孡ICENCE_FILE_NAME璇锋浛鎹㈡垚涓存椂鎺堟潈鏂囦欢鐨勫疄闄呰矾寰勶紝浠呭湪浣跨敤涓存椂license鏂囦欢鏃堕渶瑕佽繘琛岃缃紝濡傛灉鍦╗搴旂敤绠＄悊]涓紑閫氫簡绂荤嚎鎺堟潈锛屼笉闇�瑕佽缃鍙傛暟锛屽缓璁皢璇ヨ浠ｇ爜鍒犻櫎锛堢绾垮紩鎿庯級
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_LICENCE_FILE, mSampleDirPath + "/"
                + LICENSE_FILE_NAME);
        // 璇锋浛鎹负璇煶寮�鍙戣�呭钩鍙颁笂娉ㄥ唽搴旂敤寰楀埌鐨凙pp ID (绂荤嚎鎺堟潈)
        this.mSpeechSynthesizer.setAppId("7436440");
        // 璇锋浛鎹负璇煶寮�鍙戣�呭钩鍙版敞鍐屽簲鐢ㄥ緱鍒扮殑apikey鍜宻ecretkey (鍦ㄧ嚎鎺堟潈)
        this.mSpeechSynthesizer.setApiKey("dKBvmEFS1qSzuMRo9pP7YkOG", "9163c2194135946f0569bf294339b29e");
        // 鎺堟潈妫�娴嬫帴鍙�
        AuthInfo authInfo = this.mSpeechSynthesizer.auth(TtsMode.MIX);
        if (authInfo.isSuccess()) {
//            toPrint("auth success");
            this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, SpeechSynthesizer.SPEAKER_FEMALE);
            mSpeechSynthesizer.initTts(TtsMode.MIX);
        } else {
            String errorMsg = authInfo.getTtsError().getDetailMessage();
//            toPrint("auth failed errorMsg=" + errorMsg);
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
        int result = this.mSpeechSynthesizer.speak(content_for_voice);
        if (result < 0) {
//            toPrint("error,please look up error code in doc or URL:http://yuyin.baidu.com/docs/tts/122 ");
        }
    }

    private void pause() {
        this.mSpeechSynthesizer.pause();
    }

    private void resume() {
        this.mSpeechSynthesizer.resume();
    }

    private void stop() {
        this.mSpeechSynthesizer.stop();
    }

    private void synthesize() {
        String text = this.mInput.getText().toString();
        int result = this.mSpeechSynthesizer.synthesize(text);
        if (result < 0) {
//            toPrint("error,please look up error code in doc or URL:http://yuyin.baidu.com/docs/tts/122 ");
        }
    }

    private void batchSpeak() {
        List<SpeechSynthesizeBag> bags = new ArrayList<SpeechSynthesizeBag>();
        bags.add(getSpeechSynthesizeBag("123456", "0"));
        bags.add(getSpeechSynthesizeBag("浣犲ソ", "1"));
        bags.add(getSpeechSynthesizeBag("浣跨敤鐧惧害璇煶鍚堟垚SDK", "2"));
        bags.add(getSpeechSynthesizeBag("hello", "3"));
        bags.add(getSpeechSynthesizeBag("杩欐槸涓�涓猟emo宸ョ▼", "4"));
        int result = this.mSpeechSynthesizer.batchSpeak(bags);
        if (result < 0) {
//            toPrint("error,please look up error code in doc or URL:http://yuyin.baidu.com/docs/tts/122 ");
        }
    }

    private SpeechSynthesizeBag getSpeechSynthesizeBag(String text, String utteranceId) {
        SpeechSynthesizeBag speechSynthesizeBag = new SpeechSynthesizeBag();
        speechSynthesizeBag.setText(text);
        speechSynthesizeBag.setUtteranceId(utteranceId);
        return speechSynthesizeBag;
    }

    /*
     * @param arg0
     */
    @Override
    public void onSynthesizeStart(String utteranceId) {
//        toPrint("onSynthesizeStart utteranceId=" + utteranceId);
    }

    /*
     * @param arg0
     * 
     * @param arg1
     * 
     * @param arg2
     */
    @Override
    public void onSynthesizeDataArrived(String utteranceId, byte[] data, int progress) {
        // toPrint("onSynthesizeDataArrived");
    }

    /*
     * @param arg0
     */
    @Override
    public void onSynthesizeFinish(String utteranceId) {
//        toPrint("onSynthesizeFinish utteranceId=" + utteranceId);
    }

    /*
     * @param arg0
     */
    @Override
    public void onSpeechStart(String utteranceId) {
//        toPrint("onSpeechStart utteranceId=" + utteranceId);
    }

    /*
     * @param arg0
     * 
     * @param arg1
     */
    @Override
    public void onSpeechProgressChanged(String utteranceId, int progress) {
        // toPrint("onSpeechProgressChanged");
    }

    /*
     * @param arg0
     */
    @Override
    public void onSpeechFinish(String utteranceId) {
//        toPrint("onSpeechFinish utteranceId=" + utteranceId);
    }

    /*
     * @param arg0
     * 
     * @param arg1
     */
    @Override
    public void onError(String utteranceId, SpeechError error) {
//        toPrint("onError error=" + "(" + error.code + ")" + error.description + "--utteranceId=" + utteranceId);
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

}
