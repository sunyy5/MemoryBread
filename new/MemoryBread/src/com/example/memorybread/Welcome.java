package com.example.memorybread;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

public class Welcome extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);  
	        //全屏显示welcome画面  
	        requestWindowFeature(Window.FEATURE_NO_TITLE);   
	        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
	                WindowManager.LayoutParams.FLAG_FULLSCREEN);          
	        setContentView(R.layout.activity_welcome);  
	        //延迟0.7秒后执行run方法中的页面跳转  
	        new Handler().postDelayed(new Runnable() {            
	            @Override  
	            public void run() {  
	                Intent intent = new Intent(Welcome.this, LoginActivity.class);  
	                startActivity(intent);  
	                Welcome.this.finish();  
	            }  
	        }, 3000);  
	}
}
