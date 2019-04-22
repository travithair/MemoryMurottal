package com.travitha.memorymurottal;

import android.content.*;
import android.content.pm.*;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.*;
import android.os.*;
import android.support.v4.content.*;
import android.widget.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;

public class utils
{

    //class pembantu fungsi yg bisa di akses dr class yg membutuhkan
    public static void toast(Context ctx,String message){
        //maenampilkan pesan toast
        Toast.makeText(ctx,message,Toast.LENGTH_SHORT).show();
    }
    public static void toast(Context ctx,int message){
        //menampilkan pesan toast
        Toast.makeText(ctx,message,Toast.LENGTH_SHORT).show();
    }


    public static String remover(String str){
        //bertugas menghapus karakter yg tidak di inginkan
        String content = "#,@,.,(,),1,2,3,4,5,6,7,8,9,0"; //daftar karakter yg ingin di hapus
        String[] explode = content.split(","); //membuat array berdasarkan (,)
        for(int i = 0; i<explode.length;++i){//melakukan perulangan berdasarkan panjang array
            str=str.replace(explode[i],"");//menghapus
        }
        return str; //mengembalikan dalam bentuk String
    }

    public static void setGlobalFont(Context ctx){
        //mengatur custom font untuk aplikasi dr asset
        try{ //handle error
            Typeface tface = Typeface.createFromAsset(ctx.getAssets(),"font/AcademiaPlain.ttf"); //pelajari lebih lanjut ttg TypeFace / mengambil font dr asset
            Field field = Typeface.class.getDeclaredField("Serif"); //mengambil field class Typeface
            field.setAccessible(true); //mengatur aksesibiltas field menjadi true
            field.set(null,tface); //mengatur typeface untuk aplikasi ini
        }catch(Exception e){
            //tanamkan program jika error,biasanya error ketika font di assets tidak dimasukan atau tidak ada
        }
    }

    public static int matchSura(String path){
        //menemukan posisi sura berdasakan path yg sedang d putar
        int res = 0; //variable untuk dikembalikan
        ArrayList<String> listData = MainActivity.listData; //mengambil list sura dari class MainActivity yg di atur Public static
        for(int i=0;i<listData.size();++i){ //melakukan perulangan berdasarkan banyak nya isi data
            if(path.contains(listData.get(i))){ //prinsip nya jika nama sura mirip atau terkandung di dalam string path maka bisa di asumsikan sura yg sedang di putar adalah itu
                //contoh path  (/sdcard/Al-Fatihah/reciter1/00001.mp3) dan nama sura dalam array adalah (Al-Fatihah) maka nilai dr if adalah true/benar
                res = i; //mengisi variable res untuk dikembalikan
            }
        }

        return res; //mengembalikan hasil akhir dalam bentuk int
    }


    public static int matchSuraPosition(Context ctx,String path){
        //menemukan posisi ayat yg sedang diputar
        int res = 0;
        try{
            ArrayList<String> listData = MainActivity.listData; //mengambil daftar sura dr MainActivity
            int suraPos = matchSura(path); //mengambil nama sura yg sedang di putar dengan path yg diterima
            String suraName = listData.get(suraPos); //jika ada nama sura yg cocok / mengambil data sesui dgn data yg di cocokan tdi
            int reciter = 0; //default reciter adalah reciter 1 ,dalam perhitungan array yg dimulai adalah angka nol bukan nya satu
            HashMap<String,String> settings = new VithaPrefs(ctx).getSettings(listData.get(suraPos));// mengambil settings berdasarkan nama sura (pengaturan di simpan dengan key nama sura itu sendiri,jd untuk mengakses/mengambil data diperlukan nama sura tsb);
            reciter = Integer.parseInt(settings.get(VithaPrefs.RECITER)); //mengambil ayat yg sesui dengan reciter yg di atur di preferense
            String[] reciters = new String[]{"reciter1","reciter2"}; // array nama folder reciter
            String root = Environment.getExternalStorageDirectory().getPath()+"/.Murottal/"; // letak daftar sura diletakan di kartu sd/penyimpanan
            String pathMatch = root+suraName+"/"+reciters[reciter]; // mendeklarasikan letak file ayat berdasarkan keputusan logic sebelum nya
            String[] reciterList = new File(pathMatch).list(); //mengambil list ayat
            for(int i = 0;i<reciterList.length;++i){ //melakukan perulangan
                if(path.equals(pathMatch+"/"+reciterList[i])){  //mencocokan path dengan ayat yg diputar konsep nya sama seperti di matchSura tp bedanya ini untuk menemulan ayat
                    res = i; //jika ada yg cocok / mengambil posisi
                    break; //stop perulangan
                }
            }
        }catch(Exception e){
            //jika error
            utils.toast(ctx,e.toString());
        }

        return res; // hasil akhir dapam bentuk integer
    }

    public static boolean checkPermission(Context context,String permission){
        //memeriksa permission/perijinan ini di akses dr MainAcitivity
        boolean result = true; // default value true
        int myBuild =Build.VERSION.SDK_INT; // mengambil versi android
        if(myBuild>=Build.VERSION_CODES.M){ // jika versi android adalah MarshMello(5.0)(sdk 21) atau di atas nya itu artinya meneruskan program jika di bawah marshmello berarti fungsi ini tidak di butuhkan/ di skip
            int checking =ContextCompat.checkSelfPermission(context,permission); //memeriksa perijinan berdasarkan data yg diterima dr splash
            if(PackageManager.PERMISSION_GRANTED==checking){
                //jika di izinkan
                result = true;
            }else{
                //jika tidak di izinkan
                result = false;
            }
        }
        return result; //hasil akhir tergantung logic di atas
    }

    public static void setLocale(Context ctx){
        //preferense
        VithaPrefs prefs = new VithaPrefs(ctx);
        //mengambil bahasa pilihan user
        String lang = prefs.getLang();
        //mengambil resources
        Resources resource = ctx.getResources();
        //mengambil konfigurasi
        Configuration conf = resource.getConfiguration();
        //value default adalah null,ketika user belum memilih bahasa
        if(lang!=null){
            //jika user sudah memilih bahasa
            Locale local = new Locale(lang);
            conf.locale = local;
            //update konfig
            resource.updateConfiguration(conf,resource.getDisplayMetrics());
        }

    }
}