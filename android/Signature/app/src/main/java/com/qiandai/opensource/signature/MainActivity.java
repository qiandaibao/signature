package com.qiandai.opensource.signature;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.qiandai.opensource.signature.com.qiandai.opensource.signature.utils.HttpUtil;
import com.qiandai.opensource.signature.com.qiandai.opensource.signature.utils.SignList;
import com.qiandai.opensource.signaturelibrary.views.SignaturePad;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {

    private Button mClearButton;
    private Button mSaveButton;
    private QDSignaturePad qdSignaturePad;
    SignList signList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        qdSignaturePad =(QDSignaturePad) findViewById(R.id.signature_pad);
        signList=qdSignaturePad.getSignList();
        qdSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onSigned() {
                mSaveButton.setEnabled(true);
                mClearButton.setEnabled(true);
            }

            @Override
            public void onClear() {
                mSaveButton.setEnabled(false);
                mClearButton.setEnabled(false);
            }
        });

        mClearButton = (Button) findViewById(R.id.clear_button);
        mSaveButton = (Button) findViewById(R.id.save_button);

        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qdSignaturePad.clear();
                signList.saveCleanedSign();//clearndSign
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap signatureBitmap = qdSignaturePad.getSignatureBitmap();
                if(addSignatureToGallery(signatureBitmap)) {
                    Toast.makeText(MainActivity.this, "Signature saved into the Gallery", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Unable to store the signature", Toast.LENGTH_SHORT).show();
                }
                signList.saveFinalSign();
                getData();
            }
        });
    }
    public void getData(){
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("coord", Arrays.toString(signList.getFinalSign()));
            jsonObject.put("timestamp",Arrays.toString(signList.getTimeInMillies()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("getData"   ,"jsonObject"+jsonObject.toString() );
        goHttp(jsonObject.toString());
    }
    public void goHttp(String data){
        Map<String, String> map = new HashMap<String, String>();
        map.put("user", "hcl");
        map.put("pass", "time9818");
        map.put("data", data.replace(" ", ""));
        // 定义发送请求的URL
        String url = "http://192.168.208.242:8080/SignatureServer/interserver";
        // 发送请求
        try {
            String str= HttpUtil.postRequest(url, map);
            qdSignaturePad.clear();//clear
            signList.saveCleanedSign();//clearndSign
            System.out.println(str);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        };
    }
    public File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e("SignaturePad", "Directory not created");
        }
        return file;
    }

    public void saveBitmapToJPG(Bitmap bitmap, File photo) throws IOException {
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        OutputStream stream = new FileOutputStream(photo);
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        stream.close();
    }

    public boolean addSignatureToGallery(Bitmap signature) {
        boolean result = false;
        try {
            File photo = new File(getAlbumStorageDir("SignaturePad"), String.format("Signature_%d.jpg", System.currentTimeMillis()));
            saveBitmapToJPG(signature, photo);
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(photo);
            mediaScanIntent.setData(contentUri);
            MainActivity.this.sendBroadcast(mediaScanIntent);
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
