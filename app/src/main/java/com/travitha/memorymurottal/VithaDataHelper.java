package com.travitha.memorymurottal;

import android.content.*;
import android.os.*;
import java.io.*;
import java.util.*;

public class VithaDataHelper
{

    //fungsi untuk mengambil daftar nama sura
    public static ArrayList<String> getData(Context ctx){
        try{
            String root = Environment.getExternalStorageDirectory().getPath()+"/.Murottal"; //mendeklarasikan path tempat sura di tempatkan
            File f = new File(root); //mendeklarasikan f (File)
            if(!f.exists()){ //cek kondisi apakah folder sudah ada di sdcard
                if(!f.mkdir()){ //jika tidak membuat folder baru
                    utils.toast(ctx,"Access error.. "); //pesan error
                }
            }
            ArrayList<String> result = new ArrayList<>(); //membuat dan mendeklarasikan array baru untuk menyimpan daftar nama sura
            String[] listSura = f.list(); //mengambil daftar nama sura dari sdcard
            for(int i=0;i<listSura.length;++i){ //looping berdasarkan banyak listSura (array[])
                //memindahkan semua isi dari listSura(array[]) ke dalam result (arraylist)
                result.add(listSura[i]);
            }
            Collections.sort(result); //mengurutkan nama berdasarkan abjad
            return result; //hasil akhir
        }catch(Exception e){
            return null; //jika error hasil akhir dikembalikan null
        }
    }
}