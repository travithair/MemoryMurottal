package com.travitha.memorymurottal;

import android.app.*;
import android.os.*;
import android.content.*;
import android.media.*;
import java.util.*;
import java.io.*;
import android.content.res.*;
import android.widget.*;

public class VithaServicePlayer extends Service // class yg bertugas memutar audio (Service)
{

    @Override
    public IBinder onBind(Intent p1)
    {
        // untuk sekarang tidak dibutuhkan tetapi perlu ditulis
        return null;
    }

    //publik akses
    //variable berikut akan di akses dr class VithaMainList.java
    public static boolean isPlaying = false; //membuat dan mendeklarasikan variable isPlaying(Boolean) yg memiliki value default false ketika app dimulai
    public static String latestSura = null; //membuat variable latestSura(String)
    public static int ayatPos = 0; //membuat dan mendeklarasikan variable ayatPos(Integer) yg memiliki value default 0 ketika app dimulai
    private static String[] repetList =  {"1","-1","2","5","10","15","18","20","25","30"}; //membuat dan mendeklarasikan variable repeatList (Array)  dan memiliki value
    private static VithaPrefs prefs; //membuat variable prefs (VithaPrefs)
    private static ArrayList<String> listAyat = null; //membuat array listAyat untuk menamping daftar ayat
    private static ArrayList<String> listData = null; //membuat array listAyat untuk menamping daftar sura
    private static MediaPlayer mp = null; //membuat variable mp (MediaPlayer) yg bertugas memutar audio

    @Override
    public void onCreate() //fungsi default class service dipanggil ketika app pertama di panggil/dijalankan
    {
        // TODO: Implement this method
        super.onCreate();
        try{ //membuat try untuk handle jikalau ada error agar app tidak force close
            //data
            prefs = new VithaPrefs(this); //mendeklarasikan variable prefs (VithaPrefs)
            ayatPos = 0; // memberi value untuk variable ayatPos (int) yg digunakan untuk menyimpan ayat terakhir yg diputar untuk sementara
            listData = MainActivity.listData; // mengambil value dari MainActivity.listData; agar tidak perlu melakikan pengumpulan ulang data yg menyebabkan app tidak responsif
            latestSura = prefs.getLatestSura(); // mengambil sura terakhir dr sharedpreferense; latestSura (String) akan bernilai null ketika aplikasi pertama di install
            mp = new MediaPlayer(); //mendeklarasikan mediaplayer
            if(latestSura!=null){ //memeriksa kondisi apakah variable latestSura sama dengan null atau tidak ini hanya untuk handle ketika aplikasi pertama kali di install dan di jalankan
                HashMap<String,String> settings = prefs.getSettings(prefs.getLatestSura()); //membuat Array (HashMap) dan mengambil value nya dari variable prefs(VithaPrefs)
                int enableSettings = Integer.parseInt(settings.get(prefs.ENABLE_SETTINGS)); //membuat variable enableSettings(integer) dan mengambil value dari variable setting(hashmap) kemudian di ubah dari nentuk String kedalam benyuk integer
                if(enableSettings==1){ //memeriksa value/nilai enableSetting(Integer)
                    //jika enableSettings benilai satu artinya memberi izin (sura akan diputar berdasarkan settings dr user)
                    repeat = Integer.parseInt(repetList[Integer.parseInt(prefs.getSettings(latestSura).get(prefs.REPEAT))]); // variable repeat (int) di isi value tergantung dr pilihan user yg di integrasikan dengan repeatList(array) yg artinya diputar sesuai isi array
                }else{ //jika value/nilai enableSettings  bernilai selain satu
                    repeat = 1; // variable repeat (int) di isi value satu yg artinya sura hanya akan diputar sekali
                }
                //listAllAyat = getAllAyat(latestSura);
                listAyat = getListAyat(latestSura); //memngambil sura terakhir dari sharedpreferense
                mp = setMedia(listAyat.get(ayatPos)); //mengatur dan mengambil media yg akan diputar dari fungsi VithaServicePlayer.setmedia()

            }
        }catch(Exception e){ // jika terjadi error
            Toast.makeText(this,e.toString(),0).show(); //menampilkan pesan error dengan Toast
        }
    }




    //fungsi untuk memngambil daftar Ayat berdasarkan nama sura dan dikembalikan dalam bentuk arraylist
    private static ArrayList<String> getListAyat(String suraName){
        ArrayList<String> result = new ArrayList<>(); // membuat array baru untuk menampung daftar ayat
        try //menerapkan try untuk handle error
        {

            HashMap<String,String> settings = prefs.getSettings(suraName); //mengambil pengaturan dari sharedpreferense
            String root = Environment.getExternalStorageDirectory().getPath()+"/.Murottal"; //mendeklarasikan path root/akar tempat file audio diletakan
            String[] listAyat = null; //membuat array baru untuk menampung daftar nama ayat (nama bukan path) yg dibutuhkan adalah path nya
            int enableSettings = Integer.parseInt(settings.get(prefs.ENABLE_SETTINGS)); //mengambil data dr variable settings (array)

            if(enableSettings == 1){
                //jika pengaturan di aktifkan
                int fromVerse = Integer.parseInt(settings.get(prefs.FROM_VERSE)); //mengambil data dr variable settings (array)
                int toVerse = Integer.parseInt(settings.get(prefs.TO_VERSE)); //mengambil data dr variable settings (array)
                int reciter = Integer.parseInt(settings.get(prefs.RECITER)); //mengambil data dr variable settings (array)
                String[] reciterDirs = new String[]{"reciter1","reciter2"}; //array untuk menampung nama directory reciter
                listAyat = new File(root+"/"+suraName+"/"+reciterDirs[reciter]).list(); // mengambil daftar ayat berdasarkan pengaturan
                if(toVerse==-1){
                    toVerse = listAyat.length; //jika value toVerse (integer) bernilai dibawah nol itu berarti pengaturan belum di atur oleh user (masih default)
                }
                for(int i = fromVerse;i<=toVerse;++i){ //melakukan perulangan berdasarkan pengaturan user
                    result.add(root+"/"+suraName+"/"+reciterDirs[reciter]+"/"+listAyat[i]); //menambahkan path ayat sesuai pengaturan
                }
            }else{
                //jika pengaturan tidak di aktifkan
                //pengaturan default
                int reciter = 0; //reciter default Reciter1
                String[] reciterDirs = new String[]{"reciter1","reciter2"};
                listAyat = new File(root+"/"+suraName+"/"+reciterDirs[reciter]).list(); //melakukan perulangan sesuai dengan banyak ayat
                for(int i = 0;i<listAyat.length;++i){
                    result.add(root+"/"+suraName+"/"+reciterDirs[reciter]+"/"+listAyat[i]);
                }
            }
        }
        catch (Exception e)
        {}
        return result;
    }

    //fungsi yang bertugas mengatur media yang akan di putar
    private static MediaPlayer setMedia(String path){ //meminta path sura untuk di siapkan kedalam bentuk mediaplayer
        MediaPlayer mediapPlayer = new MediaPlayer(); // mendeklarasikan mediaplayer baru

        try{ //untuk handle error
            //menyiapkan file
            //mengatur data sumber berdasarkan path ayat
            mediapPlayer.setDataSource(path); //menyiapkan mediaplayer
            mediapPlayer.prepare();
        }catch(Exception e){
            //jika ada kesalahan
            //tidak ada aksi yang akan dilakukan

        }
        prefs.putLatestPath(path); //menyimpan path ayat terkhir ke sharedpreferense
        return mediapPlayer; //mengembalikan ke peminta dalam bentuk media player
    }

    //variable berikut hanya untuk menyesuaikan key/kata kunci untuk komunikasi antar class dan fungsi
    //publik akses
    public static String KEY = "JJDKDKDHGDKDJSJJ";
    public static final int KEY_PLAY_NEW = 799;
    public static final int KEY_PLAY_NEXT = 897;
    public static final int KEY_PLAY_NEXT_STOP = 006;
    public static final int KEY_PLAY_PAUSE = 926;
    public static final int KEY_PLAY_RESUME = 107;

    //fungsi yg bertugas memutar media
    public static void player(String suraName,int key){

        switch(key){
            case KEY_PLAY_NEW: //request ketika ingin memulai memutar sura yg baru.
                try{
                    listAyat = getListAyat(suraName); // mengambil list ayat
                    prefs.putLatestSura(suraName); //menyimpan sura ke preferense untuk di akses ketika app di restart atau class/method lain
                    HashMap<String,String> settings = prefs.getSettings(prefs.getLatestSura()); //mengambil data settings dari sharedpreferense
                    int enableSettings = Integer.parseInt(settings.get(prefs.ENABLE_SETTINGS)); //membuat variable enableSettings(integer) dan mengambil value dari variable setting(hashmap) kemudian di ubah dari nentuk String kedalam benyuk integer
                    if(enableSettings==1){ //memeriksa value/nilai enableSetting(Integer)
                        //jika enableSettings benilai satu artinya memberi izin (sura akan diputar berdasarkan settings dr user)
                        repeat = Integer.parseInt(repetList[Integer.parseInt(prefs.getSettings(suraName).get(prefs.REPEAT))]); // variable repeat (int) di isi value tergantung dr pilihan user yg di integrasikan dengan repeatList(array) yg artinya diputar sesuai isi array
                    }else{ //jika value/nilai enableSettings  bernilai selain satu
                        repeat = 1; // variable repeat (int) di isi value satu yg artinya sura hanya akan diputar sekali
                    }

                }catch(Exception e){
                    //jika error
                }
                if(mp!=null){ //memeriksa kondisi apakah variable mp (MediaPlayer) tidak null
                    mp.reset(); //jika tidak null mp akan di reset untuk di isi media baru
                }
                mp = setMedia(listAyat.get(ayatPos));  // mengatur media baru dengan fungsi VithaServicePlayer.setMedia()
                mp.start(); //mulai memutar audio
                isPlaying = true; // mengubah nilai isPlaying(boolean) menjadi true/benar
                break;
            case KEY_PLAY_NEXT: //request ketika ingin memutar ayat selanjut nya
                if(mp!=null){ //memeriksa kondisi apakah variable mp (MediaPlayer) tidak null
                    mp.reset(); //jika tidak null mp akan di reset untuk di isi media baru
                }
                ayatPos = prefs.getLatestPos(); //mengambil posisi ayat terakhir yg di putar dari sharedpreferense
                if(ayatPos>=listAyat.size()-1){ //memeriksa kondisi jika ayat terakhir sama dengan atau di atas jumlah sura listAyat(array)
                    ayatPos = 0; ;// jika ya akan dikemnalilan ke nol
                }else{
                    ayatPos = ++ayatPos; //jika tidak akan di tambah satu
                }

                mp = setMedia(listAyat.get(ayatPos)); //mengatur media berdasarkan posisi
                mp.start(); //mulai memutar audio
                isPlaying = true; //mengubah value isPlaying (boolean) menjadi true/benar (artinya ayat sedang diputar)
                break; //batas
            case KEY_PLAY_PAUSE: //dipanggil ketika ayat ingin di pause
                if(mp!=null){
                    if(mp!=null&&mp.isPlaying()){
                        mp.pause();
                        isPlaying = false;
                    }else if(!mp.isPlaying()){
                        mp.start();
                        isPlaying=true;
                    }
                }else{
                    player(suraName,KEY_PLAY_NEW); //jika mediaplayer null akan dikembalikan ke KEY_PLAY_NEW
                }
                break;
            case KEY_PLAY_NEXT_STOP: //di panggil ketika semua sura selesai dimainkan keposisi nol/pertama

                listAyat = getListAyat(suraName);
                prefs.putLatestSura(suraName);
                HashMap<String,String> settings = prefs.getSettings(prefs.getLatestSura());
                int enableSettings = Integer.parseInt(settings.get(prefs.ENABLE_SETTINGS));
                if(enableSettings==1){
                    repeat = Integer.parseInt(repetList[Integer.parseInt(prefs.getSettings(suraName).get(prefs.REPEAT))]);
                }else{
                    repeat = 1;
                }
                if(mp!=null){
                    mp.reset();
                }
                mp = setMedia(listAyat.get(0));
                ayatPos = 0;
                isPlaying = false;
                if(mp.isPlaying()){

                    mp.pause();


                }
                break;
        }
        prefs.putLatestPos(ayatPos); //menyimpan posisi ayat terakhir

        mp.setOnCompletionListener(ifcomplete); //menalihkan aksi ketika audio selesai dimainkan

        MainActivity.listAdapter.notifyDataSetChanged(); //fungsi memberiathukan mainList untuk mengupdate tampilan list (terutama bagian button play dan pausenya)
        VithaMainListAdapter.adapter.notifyDataSetChanged();  //fungsi memberitaukan suraList untuk mengupdate tampilan nya (bagian ayat yg sedang di putar)

    }


    //ini fungsi yg khusus memutar ayat satu-satu jadi tidak akan d ulang dan otomatis berhenti ketika ayat selesai dimainkan
    public static void playByAyat(String path,int pos){
        mp.setOnCompletionListener(null); //mengubah OnCompletionListener tujuan nya agar setelah selesai di mainkan tidak akan di lanjutkan ke ayat lain
        prefs.putLatestPath(path); //menyimpan path ayat terkahir yg di putar
        prefs.putLatestSura(listData.get(utils.matchSura(path))); //menimpan sura yg berkaitan dengan ayat terakhir
        prefs.putLatestPos(pos); //menyimpan posisi ayat terakhir yg diputar
        if(mp!=null){ //check kondisi jika mp(mediaplayer) tidak sedang kosong/null
            mp.reset(); //jika mp (mediaplayer) tidak null maka akan di reset atau di dikosongkan untuk dimasukan file audio baru
        }
        mp = setMedia(path); //menhmgatur media baru dengan fungsi VithaServicePlayer.setMedia()
        mp.start(); //mulai memutar ayat yang di klik
        isPlaying = true; //mengubah value/nilai isPlaying (boolean) menjadi true/benar
        MainActivity.listAdapter.notifyDataSetChanged(); //fungsi memberiathukan mainList untuk mengupdate tampilan list (terutama bagian button play dan pausenya)
        VithaMainListAdapter.adapter.notifyDataSetChanged(); //fungsi memberitaukan suraList untuk mengupdate tampilan nya (bagian ayat yg sedang di putar)
        mp.setOnCompletionListener(onAyatComplete);
    }

    private static int repeat;
    //membuat variable repeat yg berfungsi menyimpan pengaturan repeat
    //fungsi bertugas menghandle ketika sura (semua ayat) selesai dimainkan


    private static MediaPlayer.OnCompletionListener ifcomplete = new MediaPlayer.OnCompletionListener(){

        @Override
        public void onCompletion(MediaPlayer p1)
        {

            //menonaktifkan listener
            if(mp!=null){
                mp.setOnCompletionListener(null);
            }

            if(prefs.getLatestPos()==listAyat.size()-1){ //mengambil posisi ayat terakhir dan check kondisi jika posisi ayat terakhir adalah yg terakhir
                repeat = repeat-1; //jika true /benar repeat (integer) akan di nilai nya kurang satu sampai menjadi nol dan sura akan di stop
            }


            if(repeat==0){ //check nilai repeat apakah sudah bervalue nol?
                player(prefs.getLatestSura(),KEY_PLAY_NEXT_STOP); //jika ya menghentikan pemutar dan mengatue ulang posisi ke ayat pertama
            }else{ //jika masih belum nol
                player(prefs.getLatestSura(),KEY_PLAY_NEXT); //sura akan lanjut di putar
            }

        }


    };

    //fungsi ketika ayat (ayat by ayat) selesai dimainkan
    private static MediaPlayer.OnCompletionListener  onAyatComplete= new MediaPlayer.OnCompletionListener(){

        @Override
        public void onCompletion(MediaPlayer p1)
        {
            // TODO: Implement this method
            isPlaying = false; //mengubah nilao isPlaying(boolean) menjadi false
            MainActivity.listAdapter.notifyDataSetChanged();
            VithaMainListAdapter.adapter.notifyDataSetChanged();
            player(prefs.getLatestSura(),KEY_PLAY_NEXT_STOP); //memghentikan pemutar
        }



    };




}