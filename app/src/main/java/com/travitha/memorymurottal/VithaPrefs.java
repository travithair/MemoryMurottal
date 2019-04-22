package com.travitha.memorymurottal;

import android.content.*;
import java.util.*;

public class VithaPrefs
{
    private SharedPreferences prefs;
    private SharedPreferences.Editor edit;
    private Context ctx;
    public VithaPrefs(Context ctx){
        this.ctx = ctx; // mengambil context / menjadikan global variable
        prefs = ctx.getSharedPreferences("VithaMurottal",0); // mendelarasikan nama dan akses SharedPreferense
        edit = prefs.edit(); // mendeklarasikan editor / pengedit data
    }

    //simpan sura terakhir
    String suraTerakhir = "jdbdujjdjsj"; // key untuk sura terakhir ,nanti di gunakan untuk menandai sura yg sedang diputar di RecyclerView
    boolean putLatestSura(String suraName){
        //menyimpan nama sura yg sedang di putar
        return edit.putString(suraTerakhir,suraName).commit();
    }
    String getLatestSura(){
        //mengambil nama sura terakhir yg sedang di putar
        return prefs.getString(suraTerakhir,null);
    }

    private String LatestPosition = "kjjdjdkdk"; // key untuk posisi ayat(bukan sura) yg sedang diputar / digunakan untuk mengenali ayat yg akan diputar selanjutx di VithaServicePlayer
    boolean putLatestPos(int pos){
        // menyimpan posisi ayat
        return edit.putInt(LatestPosition,pos).commit();
    }

    int getLatestPos(){
        //mengambil posisi ayat
        return prefs.getInt(LatestPosition,0);
    }

    //simpan pengaturan sura
    boolean putSettings(String suraName,String fromVerse,String toVerse,String repeat,String reciter,String enableSettings){
        //pengaturan di simpan berdasarkan nama sura / prefs untuk pengaturan repeat dan lain nya.
        return edit.putString(suraName,fromVerse+","+toVerse+","+repeat+","+reciter+","+enableSettings).commit();
    }

    //daftar kata kunci HashMap untuk di akses method / fungsi lain
    //dr nama variable bisa di pahami tanpa penjelasan
    public static String FROM_VERSE = "HIDAYATULLAH";
    public static String TO_VERSE = "MAGDALENA";
    public static String REPEAT = "DELTA";
    public static String RECITER = "RIZA";
    public static String ENABLE_SETTINGS = "TRAVITHAIR";

    HashMap<String,String> getSettings(String suraName){
        HashMap<String,String> result = new HashMap<>(); // HashMap untuk hasil akhir
        String[] settings = prefs.getString(suraName,"0,-1,0,0,0").split(","); //mengubah string kedalam bentuk array[] dan tanda (,) sebagai pembatas
        //memasukan nilai berdasarkan kata kunci tadi
        result.put(FROM_VERSE,settings[0]);
        result.put(TO_VERSE,settings[1]);
        result.put(REPEAT,settings[2]);
        result.put(RECITER,settings[3]);
        result.put(ENABLE_SETTINGS,settings[4]);
        return result; //hasil akhir
    }

    private String latestPath = "hsiebeudjshsj"; //kata kunci untuk path/lokasi file yg terakhir di putar
    public boolean putLatestPath(String path){
        //menyimpan path terakhir
        return edit.putString(latestPath,path).commit();
    }

    public String getLatestPath(){
        //mengambil path terakhir
        return prefs.getString(latestPath,null);
    }

    private static String key_lang = "hshdhdj$";
    public static String LANG_ID = "in";
    public static String LANG_EN = "en";
    public boolean putLang(String lang){
        return edit.putString(key_lang,lang).commit();
    }

    public String getLang(){
        return prefs.getString(key_lang,null);
    }

}
