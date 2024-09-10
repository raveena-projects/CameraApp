package com.example.cameraapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private ImageView iv_capturedImage;
    private Button btn_capture;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        btn_capture = findViewById(R.id.btn_capture);
        iv_capturedImage = findViewById(R.id.iv_capturedImage);
        requestPermission();
        btn_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(cameraIntent.resolveActivity(getPackageManager()) !=null) {
                    //Create a file to save the image
                    File photofile = createImageFile();
                    if(photofile !=null){
                        imageUri = FileProvider.getUriForFile(MainActivity.this,
                                "com.example.cameraapp",photofile);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                        startActivityForResult(cameraIntent,1);
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK){
            iv_capturedImage.setImageURI(imageUri);
        }
    }

    private File createImageFile(){
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imagefilename = "JPEG_"+ timestamp + "_";
        File storeDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imFile = null;
        try{
            imFile = File.createTempFile(imagefilename,".jpg",storeDir);
        }catch(IOException e){
            e.printStackTrace();
        }
        return  imFile;
    }

    private void requestPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100){
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //Permission granted
            } else{
                Toast.makeText(this,"Camera and storage permissions are required",
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}