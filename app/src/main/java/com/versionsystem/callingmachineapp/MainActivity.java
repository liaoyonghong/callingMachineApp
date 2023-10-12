package com.versionsystem.callingmachineapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.AudioManager;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.speech.tts.UtteranceProgressListener;
import android.view.Window;
import android.webkit.WebView;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import android.media.MediaPlayer;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private Handler handler;
    private Runnable runnable;

    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private TextToSpeech textToSpeech;

    private MachineData data = new MachineData();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 隐藏标题栏
        setContentView(R.layout.activity_main);
        webView = findViewById(R.id.webView);
        handler = new Handler(Looper.getMainLooper());

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy gfgPolicy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(gfgPolicy);
        }
        mediaPlayer = MediaPlayer.create(this, R.raw.beep);
        mediaPlayer.setVolume(2.0f, 2.0f);


        /*textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    // 设置语言为英语
                    //Locale locale = new Locale("yue", "HK");
                    //int result = textToSpeech.setLanguage(locale);
                    Toast.makeText(MainActivity.this, "初始化成功", Toast.LENGTH_SHORT).show();
                    int result = textToSpeech.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(MainActivity.this, "不支持当前语言", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "初始化失败", Toast.LENGTH_SHORT).show();
                }
            }
        });*/
        // 创建一个定时任务，每隔5秒获取服务器数据并刷新 WebView
        runnable = new Runnable() {
            @Override
            public void run() {
                // 在此处执行获取服务器数据的逻辑，并将数据存储到 serverData 变量中
                MachineData serverData = fetchDataFromServer();

                // 更新 WebView 中的内容
                updateWebViewContent(serverData);

                // 5秒后再次执行定时任务
                handler.postDelayed(this, 5000);
            }
        };

        // 启动定时任务
        handler.postDelayed(runnable, 5000);
        //webView.loadData("<htm><font color='red'>testing...</font></html>", "text/html", "UTF-8");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 移除定时任务，防止内存泄漏
        handler.removeCallbacks(runnable);
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }


    private MachineData fetchDataFromServer() {
        // 在此处编写从服务器获取数据的逻辑，并返回获取的数据
        // 例如使用 Retrofit 或 Volley 等网络请求库进行数据的获取

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://192.168.88.206:8080/service2.jsp")
                .build();
        try {
            Response response=client.newCall(request).execute();
            String jsonResponse = response.body().string();

            // 使用 JSON 解析库解析 jsonResponse，并处理响应数据
            Gson gson = new Gson();
            data = gson.fromJson(jsonResponse, MachineData.class);


        }catch (Exception e){
            e.printStackTrace();
        }

        return data; // 假设从服务器获取的数据为字符串形式
    }

    private void updateWebViewContent(MachineData data) {
        // 使用 loadData() 或 loadDataWithBaseURL() 方法更新 WebView 中的内容
        webView.loadDataWithBaseURL(null,data.getHtmlContent(), "text/html", "UTF-8",null);
        if (data.getPlayList()!=null && !"".equals(data.getPlayList())){
            if (isPlaying) {
                stopAudio();
            } else {
                playAudio();
            }
        }
        //textToSpeech.speak("Welcome", TextToSpeech.QUEUE_FLUSH, null, "1");
    }

    private void playAudio() {

        mediaPlayer.start();
        isPlaying = true;

    }

    private void stopAudio() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
           // mediaPlayer.release();
            //mediaPlayer = null;
        }
        isPlaying = false;

    }





}