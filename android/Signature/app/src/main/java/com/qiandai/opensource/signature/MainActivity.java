package com.qiandai.opensource.signature;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.qiandai.opensource.signaturelibrary.utils.ColorPickerDialog;
import com.qiandai.opensource.signaturelibrary.utils.FontSizeDialog;
import com.qiandai.opensource.signaturelibrary.utils.SignList;
import com.qiandai.opensource.signaturelibrary.views.SignaturePad;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends Activity implements ColorPickerDialog.OnColorChangedListener,FontSizeDialog.OnSeekbarChangedListener {

    private SignaturePad mSignaturePad;
    private Button mClearButton;
    private Button mSaveButton;
    private Paint mPaint;
    FontSizeDialog fontSizeDialog;

    SignList signList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSignaturePad = (SignaturePad) findViewById(R.id.signature_pad);
        signList=new SignList();
        mPaint = new Paint();

        mPaint.setColor(Color.BLACK);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);

        mSignaturePad.setPaint(mPaint);
        mSignaturePad.setOnTouchEventListener(new SignaturePad.OnTouchEventListener(){

            @Override
            public void onStart(float x, float y, float timestamp) {
                Log.d("onStart", "x:"+x+"  y:"+y+"  timestamp:"+timestamp);
                signList.getSignList().add((int)x);
                signList.getSignList().add((int)y);
            }

            @Override
            public void onMove(float x, float y, float timestamp) {
                Log.d("onMove", "x:"+x+"  y:"+y+"  timestamp:"+timestamp);
                System.out.println(timestamp);
                signList.getSignList().add((int)x);
                signList.getSignList().add((int)y);
            }

            @Override
            public void onUp() {
                Log.d("onUp", "--------------------------");
                signList.getSignList().add(-1);
                signList.getSignList().add(0);
            }
        });
        mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
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
                mSignaturePad.clear();
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap signatureBitmap = mSignaturePad.getSignatureBitmap();
                if(addSignatureToGallery(signatureBitmap)) {
                    Toast.makeText(MainActivity.this, "Signature saved into the Gallery", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Unable to store the signature", Toast.LENGTH_SHORT).show();
                }
            }
        });
        fontSizeDialog=new FontSizeDialog(MainActivity.this);
        fontSizeDialog.setmListener(this);
    }

    public File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.d("SignaturePad", "Directory not created");
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
    private static final int COLOR_MENU_ID = Menu.FIRST;
    private static final int SIZE_MENU_ID = Menu.FIRST + 1;
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);

        menu.add(0, COLOR_MENU_ID, 0, "颜色").setShortcut('1', 'c');
        menu.add(0, SIZE_MENU_ID, 0, "字号").setShortcut('2', 's');
        /****
         * Is this the mechanism to extend with filter effects? Intent intent =
         * new Intent(null, getIntent().getData());
         * intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
         * menu.addIntentOptions( Menu.ALTERNATIVE, 0, new ComponentName(this,
         * NotesList.class), null, intent, 0, null);
         *****/
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        mPaint.setXfermode(null);
        mPaint.setAlpha(0xFF);

        switch (item.getItemId())
        {
            case COLOR_MENU_ID://颜色
                new ColorPickerDialog(this, this, mPaint.getColor()).show();
                return true;
            case SIZE_MENU_ID://字号
                fontSizeDialog.show();
                return true;
        }
        mSignaturePad.setPaint(mPaint);
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void colorChanged(int color) {
        mPaint.setColor(color);
    }

    @Override
    public void onChange(int progress) {
        mSignaturePad.setMaxWidth(progress);
    }
}
