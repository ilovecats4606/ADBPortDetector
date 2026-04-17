package com.ilovecats4606.ADBPortDetector;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.net.InetAddress;

public class MainActivity extends AppCompatActivity implements AdbPortCallback {

    private Button detectButton;
    private TextView portTextView;
    private AdbPortDetector detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        detectButton = findViewById(R.id.detectButton);
        portTextView = findViewById(R.id.portTextView);

        detector = new AdbPortDetector(this, this);

        detectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                portTextView.setText(getString(R.string.detecting));
                detector.startDiscovery();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (detector != null) {
            detector.stopDiscovery();
        }
    }

    @Override
    public void onPortDetected(InetAddress host, int port) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                portTextView.setText(getString(R.string.port_label) + port);
            }
        });
    }
}