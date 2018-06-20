package com.abc.huongnguyen.edison;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private TextView txvResult;
    //ImageView ImageViewbtnSpeak =(ImageView) findViewById(R.id.btnSpeak);
    Button button_ketnoi;
    boolean check_connect = false;
    String turn_left = "qua trái";
    String turn_right = "qua phải";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txvResult = (TextView) findViewById(R.id.txvResult);
        button_ketnoi = (Button) findViewById(R.id.button_ketnoi);
        mSocket.connect();
        mSocket.on("check_connect", onMessageConnect);
        mSocket.on("check_command", onMessageCommand);
        button_ketnoi.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mSocket.emit("hello", "SDS");
            }
        });
    }

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://192.168.42.1:8080");
        } catch (URISyntaxException e) {}
    }

    private Emitter.Listener onMessageConnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String message ;
                    JSONObject data = (JSONObject) args[0];
                    try {
                        message = data.getString("noidung");
                        check_connect = true;
                        if(message =="true") {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Kết nối thành công",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                        else {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Kết nối không thành công",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
                    catch (JSONException e){
                        return;
                    }
                }
            });
        }
    };

    private Emitter.Listener onMessageCommand = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String message ;
                    JSONObject data = (JSONObject) args[0];
                    try {
                        message = data.getString("noidung");
                        if(message =="true") {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Ra lệnh thành công",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                        else {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Ra lệnh không thành công",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
                    catch (JSONException e){
                        return;
                    }
                }
            });
        }
    };
    public void getSpeechInput(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, java.util.Locale.getDefault());
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 10);
        } else {
            Toast.makeText(this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txvResult.setText(result.get(0));
                    String ketqua = result.get(0);
                    if(turn_left.equals(ketqua)){
                        Toast.makeText(this, "ok", Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(this, ketqua, Toast.LENGTH_SHORT).show();
                    mSocket.emit("command", result.get(0));

                }
                break;
        }
    }
}
