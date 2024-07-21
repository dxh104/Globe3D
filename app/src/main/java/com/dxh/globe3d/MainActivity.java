package com.dxh.globe3d;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.dxh.globe3d.utils.Logger;
import com.dxh.globe3d.widget.GlobeChildView;
import com.dxh.globe3d.widget.GlobeParentView;

public class MainActivity extends AppCompatActivity {
    TextView textView;
    GlobeChildView globeChildView;
    private GlobeParentView globeParentView;
    private int count = 0;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        for (int a = 0; a < 6; a++) {
            for (int b = 0; b < 12; b++) {
                count++;
                GlobeChildView globeChildView = new GlobeChildView(this, null);
                globeChildView.setBackgroundColor(Color.parseColor("#00ff00"));
                globeChildView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                final TextView textView = new TextView(this, null);
                textView.setText(count + "");
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Logger.e("onClick " + textView.getText().toString());
                        Toast.makeText(MainActivity.this, textView.getText().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
                globeChildView.addView(textView);
                globeChildView.setAngleABR(a * 30 % 180, b * 30 % 360);//方位角，仰角
                if (globeChildView.angleB == 0 || globeChildView.angleB == 180) {
                    continue;
                }
                globeParentView.addView(globeChildView);
            }
        }
        count++;

        globeChildView = new GlobeChildView(this, null);
        globeChildView.setBackgroundColor(Color.parseColor("#00ff00"));
        globeChildView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textView = new TextView(this, null);
        textView.setText(count + "");
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.e("onClick " + ((TextView) v).getText().toString());
                Toast.makeText(MainActivity.this, ((TextView) v).getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });
        globeChildView.addView(textView);
        globeChildView.angleA = 0;//方位角
        globeChildView.angleB = 0;//仰角
        globeParentView.addView(globeChildView);
        count++;
        globeChildView = new GlobeChildView(this, null);
        globeChildView.setBackgroundColor(Color.parseColor("#00ff00"));
        globeChildView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textView = new TextView(this, null);
        textView.setText(count + "");
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.e("onClick " + ((TextView) v).getText().toString());
                Toast.makeText(MainActivity.this, ((TextView) v).getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });
        globeChildView.addView(textView);
        globeChildView.angleA = 0;//方位角
        globeChildView.angleB = 180;//仰角
        globeParentView.addView(globeChildView);
        globeParentView.post(new Runnable() {
            @Override
            public void run() {
                globeParentView.updateZAllView();//这里延迟是因为Z轴高度还没设置
                globeParentView.recoveryAnimal();
            }
        });


    }

    private void initView() {
        globeParentView = (GlobeParentView) findViewById(R.id.globeParentView);
    }
}
