package com.example.qr.hellocamera;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;
import android.graphics.Matrix;

public class MainActivity extends AppCompatActivity {

    private Button button;
    private int CAMERA=1;
    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.button);
        img=(ImageView)findViewById(R.id.imageView);
        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);    //open the camera
                startActivityForResult(camera, CAMERA);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA && resultCode == Activity.RESULT_OK && null != data) {
            String sdState = Environment.getExternalStorageState();
            if(!sdState.equals(Environment.MEDIA_MOUNTED)){
                Log.w("TestFile","SD card is not avaiable/writeable right now.");
                return;
            }

            String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + System.currentTimeMillis()+ ".jpg";
            Bundle bundle=data.getExtras();
            Bitmap bm=(Bitmap)bundle.get("data");

            FileOutputStream b  = null;
            try {
                b = new FileOutputStream(filePath);
                bm.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            bm=switchColor(bm);
            img.setImageBitmap(bm);
        }
    }

    public Bitmap switchColor(Bitmap switchBitmap){
        int width=switchBitmap.getWidth();
        int height=switchBitmap.getHeight();

        // Turn the picture black and white
        Bitmap newBitmap=Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);

        Canvas canvas=new Canvas(newBitmap);
        canvas.drawBitmap(switchBitmap, new Matrix(), new Paint());

        int current_color,red,green,blue,alpha,avg=0;
        for (int i=0;i<width;i++){
            for (int j=0;j<height;j++){
                //获得每一个位点的颜色
                current_color=switchBitmap.getPixel(i, j);
                //获得三原色
                red = Color.red(current_color);
                green=Color.green(current_color);
                blue=Color.blue(current_color);
                alpha=Color.alpha(current_color);
                avg=(red+green+blue)/3;// RGB average
                if (avg>=210){  //亮度：avg>=126
                    //设置颜色
                    newBitmap.setPixel(i, j, Color.argb(alpha, 255, 255, 255));// white
                } else if (avg<210 && avg>=80){  //avg<126 && avg>=115
                    newBitmap.setPixel(i, j, Color.argb(alpha, 108,108,108));//grey
                }else{
                    newBitmap.setPixel(i, j, Color.argb(alpha, 0, 0, 0));// black
                }
            }
        }
        return newBitmap;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
