package org.babel.pokedex2;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    Uri output;
    File f;
    ImageView imagen;
    static Bitmap b;
    private String NameOfFolder = "/Pokedex";
    private String NameOfFile = "imagen";


    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        verifyStoragePermissions(this);
        imagen = findViewById(R.id.photo);
    }

    public void SendPost(View view) {
        new requests(getApplicationContext()).execute();
    }

    public void takePicture(View view) {
        getCamera();
    }

    private void getCamera(){
        String file_path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+NameOfFolder;
        String CurrentDateanTime = getCurrentDateAndTime();
        File dir = new File(file_path);
        if(!dir.exists()){
            dir.mkdir();
        }
        f= new File(dir,NameOfFile+CurrentDateanTime+".jpg");
        Log.i("algo", "getCamera: "+f);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        output=Uri.fromFile(f);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,output);
        startActivityForResult(intent,1);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ContentResolver cr = this.getContentResolver();
        try {
            b = MediaStore.Images.Media.getBitmap(cr,output);
            int rotate = 0;
            ExifInterface exif = new ExifInterface(f.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL);
            switch (orientation){
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate=270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate=180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate=90;
                    break;
            }

            Matrix matrix = new Matrix();
            matrix.postRotate(rotate);
            b = Bitmap.createBitmap(b,0,0,b.getWidth(),b.getHeight(),matrix,true);
            imagen.setImageBitmap(Bitmap.createScaledBitmap(b,240,180,false));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String getCurrentDateAndTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-­ss");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }


}
