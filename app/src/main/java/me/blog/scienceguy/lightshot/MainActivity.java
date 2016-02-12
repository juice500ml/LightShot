package me.blog.scienceguy.lightshot;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    int[] color = {255,0,0,0};
    boolean[] isTouch = {false,false,false,false};
    private LinearLayout layout;
    private Button alphaB, redB, greenB, blueB, shotB;

    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            while(true) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if(color[0]<255 && !isTouch[0]) color[0]++;
                        for (int i = 1; i < 4; ++i) {
                            if (color[i] > 0 && !isTouch[i]) color[i]--;
                        }

                        layout.setBackgroundColor(Color.argb(color[0], color[1], color[2], color[3]));
                        alphaB.setText(String.format("Alpha\n%03d", color[0]));
                        alphaB.setTextColor(Color.argb(255, 255 - color[1], 255 - color[2], 255 - color[3]));
                        redB.setText(String.format("Red\n%03d", color[1]));
                        redB.setTextColor(Color.argb(255, 255 - color[1], 255 - color[2], 255 - color[3]));
                        greenB.setText(String.format("Green\n%03d", color[2]));
                        greenB.setTextColor(Color.argb(255, 255 - color[1], 255 - color[2], 255 - color[3]));
                        blueB.setText(String.format("Blue\n%03d", color[3]));
                        blueB.setTextColor(Color.argb(255, 255 - color[1], 255 - color[2], 255 - color[3]));
                        shotB.setTextColor(Color.argb(255, 255 - color[1], 255 - color[2], 255 - color[3]));
                    }
                });
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        thread.setDaemon(true);

        layout = (LinearLayout) this.findViewById(R.id.full);
        alphaB = (Button) this.findViewById(R.id.buttonAlpha);
        redB = (Button) this.findViewById(R.id.buttonRed);
        greenB = (Button) this.findViewById(R.id.buttonGreen);
        blueB = (Button) this.findViewById(R.id.buttonBlue);
        shotB = (Button) this.findViewById(R.id.buttonShot);

        alphaB.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isTouch[0] = true;
                if(event.getAction() == MotionEvent.ACTION_UP) isTouch[0] = false;
                if(color[0]>0) color[0]--;
                return false;
            }
        });

        redB.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isTouch[1] = true;
                if(event.getAction() == MotionEvent.ACTION_UP) isTouch[1] = false;
                if(color[1]<255) color[1]++;
                return false;
            }
        });

        greenB.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isTouch[2] = true;
                if(event.getAction() == MotionEvent.ACTION_UP) isTouch[2] = false;
                if(color[2]<255) color[2]++;
                return false;
            }
        });

        blueB.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isTouch[3] = true;
                if (event.getAction() == MotionEvent.ACTION_UP) isTouch[3] = false;
                if (color[3] < 255) color[3]++;
                return false;
            }
        });

        shotB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0;i<4;++i) isTouch[i] = true;
                takeScreenshot();
                for(int i=0;i<4;++i) isTouch[i] = false;
            }
        });

        thread.start();
    }

    private void takeScreenshot() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("(yyyy-MM-dd_hh:mm)");

        try {
            String mDir = Environment.getExternalStorageDirectory().toString() + "/LightShot/";
            (new File(mDir)).mkdir();

            // image naming and path  to include sd card  appending name you choose for file
            String mPath = mDir + String.format("%03d-%03d-%03d-%03d ", color[0], color[1], color[2], color[3]) + dateFormat.format(new Date()) + ".jpg";

            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            Toast.makeText(this, "Color saved as image", Toast.LENGTH_SHORT).show();
        } catch (Throwable e) {
            // Several error may come out with file handling or OOM
            e.printStackTrace();
        }
    }
}
