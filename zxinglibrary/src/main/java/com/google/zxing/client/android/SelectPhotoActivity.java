package com.google.zxing.client.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.RunnableFuture;

/**
 * Created by xujuan on 2017/5/17.
 */

public class SelectPhotoActivity extends Activity {

    private Button btn;
    private static final int REQUEST_CODE = 1;
    private String photo_path;
    private Bitmap scanBitmap;
    private static final int SELECTPHOTO_REQEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selectphoto);
        btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectphoto();
            }
        });
    }

    private void selectphoto() {
        Intent innerIntent = new Intent();
        if (Build.VERSION.SDK_INT < 19) {
            innerIntent.setAction(Intent.ACTION_GET_CONTENT);
        } else {
            innerIntent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        }
        innerIntent.setType("image/*");
        Intent wrapperIntent = Intent.createChooser(innerIntent, "选择二维码图片");
        SelectPhotoActivity.this
                .startActivityForResult(wrapperIntent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE:
                    String[] proj = {MediaStore.Images.Media.DATA};
                    // 获取选中图片的路径
                    Cursor cursor = getContentResolver().query(data.getData(),
                            proj, null, null, null);
                    if (cursor.moveToFirst()) {
                        int column_index = cursor
                                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        photo_path = cursor.getString(column_index);
                        Diary.out("1111photo_path=" + photo_path);
                        if (photo_path == null) {
                            photo_path = Utils.getPath(getApplicationContext(),
                                    data.getData());
                        }
                        Diary.out("2222photo_path=" + photo_path);
                    }
                    cursor.close();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Result result = scanningImage(photo_path);
                            if (result == null) {
                                Looper.prepare();
                                Toast.makeText(SelectPhotoActivity.this, "未识别到二维码", Toast.LENGTH_LONG)
                                        .show();
                                Looper.loop();
                            } else {
                                Diary.out("result=" + result);
                                SerializableMap map = new SerializableMap();
                                map.setScaleFactor(1.0f);
                                map.setResult(result);
                                map.setBmp(scanBitmap);
                                Intent data = new Intent();
//                                Bundle bundle = new Bundle();
//                                bundle.putSerializable("data", map);
//                                data.putExtras(bundle);
                                data.putExtra("test", "test");
                                Diary.out("data=" + data);
//                                setResult(SELECTPHOTO_REQEST_CODE, data);
                                CaptureActivity.capture.handleDecode(result, scanBitmap, 1.0f);
                                finish();
                            }
                        }
                    }).start();
                    break;
            }
        }
    }


    protected Result scanningImage(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        // DecodeHintType 和EncodeHintType
        Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8"); // 设置二维码内容的编码
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 先获取原大小
        scanBitmap = BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false; // 获取新的大小
        int sampleSize = (int) (options.outHeight / (float) 200);
        if (sampleSize <= 0)
            sampleSize = 1;
        options.inSampleSize = sampleSize;
        scanBitmap = BitmapFactory.decodeFile(path, options);
        int[] data = new int[scanBitmap.getWidth() * scanBitmap.getHeight()];
        scanBitmap.getPixels(data, 0, scanBitmap.getWidth(), 0, 0, scanBitmap.getWidth(), scanBitmap.getHeight());
        RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap.getWidth(), scanBitmap.getHeight(), data);
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        try {
            return reader.decode(bitmap1, hints);
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        return null;

    }

}
