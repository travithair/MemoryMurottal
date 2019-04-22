package com.travitha.memorymurottal;

import android.support.v7.app.*;
import android.os.*;
import android.content.*;
import android.*;

public class VithaSplash extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO: Implement this method
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        //memeriksa permission untuk android 5 keatas
        if(utils.checkPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
            //jika di ijinkan timer dimulai
            //memulai hitung mundur
            timer.start();
        }else{
            //jika tidak di ijinnkan,
            //meminta ijin
            this.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1512);
        }
    }


    private void startMainActivity(){
        Intent itn = new Intent(this,MainActivity.class);
        startActivity(itn);
        finish(); //menutup splash activity
    }

    CountDownTimer timer = new CountDownTimer(2*1000,1000){

        @Override
        public void onTick(long p1)
        {
            // TODO: Implement this method
        }

        @Override
        public void onFinish()
        {
            // TODO: Implement this method
            startMainActivity();
        }



    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        // TODO: Implement this method
        super.onRequestPermissionsResult(requestCode, permissions, grantResults); //memeriksa kembali perizinan terutama ketika user menekan tidak mengizinkan
        if(utils.checkPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
            //setelah diberi izin
            //memulai timer
        }else{
            //jika tidak di beri izin
            utils.toast(this,"Please Grant Access first !"); // menampilkan pesan error
            finish(); // menutup aplikasi
        }
    }



}