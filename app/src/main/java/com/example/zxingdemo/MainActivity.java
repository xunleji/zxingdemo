package com.example.zxingdemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.android.CaptureActivity;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ImageView iv1, iv2, iv3;
    private Button btn1, btn2, btn3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv1 = (ImageView) findViewById(R.id.imageView1);
        iv2 = (ImageView) findViewById(R.id.imageView2);
        iv3 = (ImageView) findViewById(R.id.imageView3);
        btn1 = (Button) findViewById(R.id.button1);
        btn2 = (Button) findViewById(R.id.button2);
        btn3 = (Button) findViewById(R.id.button3);
        btn1.setOnClickListener(onclick);
        btn2.setOnClickListener(onclick);
        btn3.setOnClickListener(onclick);
    }

    private View.OnClickListener onclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == btn1) {
                generate("louiskoo", iv1);
            } else if (v == btn2) {
                startActivity(new Intent(MainActivity.this, CaptureActivity.class));
            } else if (v == btn3) {
                Bitmap logogenerate = addlogo(generateBmp("http://www.baidu.com", 400, 400), BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
                iv3.setImageBitmap(logogenerate);
            }
        }
    };

    private void generate(String content, ImageView iv) {
        Bitmap qrBitmap = generateBmp(content, 400, 400);
        iv.setImageBitmap(qrBitmap);
    }

    private Bitmap generateBmp(String content, int w, int h) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, String> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        try {
            BitMatrix encode = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, w, h, hints);
            int[] pixels = new int[w * h];
            for (int i = 0; i < h; i++) {
                for (int j = 0; j < w; j++) {
                    if (encode.get(j, i)) {
                        pixels[i * w + j] = 0x00000000;
                    } else {
                        pixels[i * w + j] = 0xffffffff;
                    }
                }
            }
            return Bitmap.createBitmap(pixels, 0, w, w, h, Bitmap.Config.RGB_565);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Bitmap addlogo(Bitmap qrbmp, Bitmap logobmp) {
        int qrbmpw = qrbmp.getWidth();
        int qrbmph = qrbmp.getHeight();
        int logobmpw = logobmp.getWidth();
        int logobmph = logobmp.getHeight();
        Bitmap blankbmp = Bitmap.createBitmap(qrbmpw, qrbmph, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(blankbmp);
        canvas.drawBitmap(qrbmp, 0, 0, null);
        canvas.save(Canvas.ALL_SAVE_FLAG);
        float scaleSize = 1.0f;
        while ((logobmpw / scaleSize) > (qrbmpw / 5) || (logobmph / scaleSize) > (qrbmph / 5)) {
            scaleSize *= 2;
        }
        float sx = 1.0f / scaleSize;
        canvas.scale(sx, sx, qrbmpw / 2, qrbmph / 2);
        canvas.drawBitmap(logobmp, (qrbmpw - logobmpw) / 2, (qrbmph - logobmph) / 2, null);
        canvas.restore();
        return blankbmp;
    }

}
