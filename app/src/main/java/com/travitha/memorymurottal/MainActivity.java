package com.travitha.memorymurottal;

import android.os.*; //untuk bundle di oncreate
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity; //memanggil appcompatactivity
import android.support.v7.widget.*;
import java.util.*;
import android.content.*;
import android.text.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener
{
    //untuk akses publik
    public static ArrayList<String> listData;
    //untuk akses pribadi
    private SearchView mainSearch;
    private RecyclerView mainList;

    private VithaPrefs prefs;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //mengatur bahasa
        utils.setLocale(this);
        //mengatur font
        utils.setGlobalFont(this);
        setContentView(R.layout.main); //mengatur layout

        listData = VithaDataHelper.getData(this);
        prefs = new VithaPrefs(this);
        //memulai service
        setService();
        //inisialisasi
        inisialisasi();
        //setSearch fungsi lokal
        setSearch();
        //setList fungsi lokal
        setList();
    }



    //akses pribadi
    //inisialisasi
    private void inisialisasi(){
        mainSearch = (SearchView) findViewById(R.id.mainSearchView);
        mainList = (RecyclerView) findViewById(R.id.mainList);
    }



    //akses pribadi
    //mengatur pengaturan untuk mainSearch
    private void setService(){
        //bertugas memulai class VithaServicePlayer
        Intent intent = new Intent(this,VithaServicePlayer.class);

        startService(intent);

    }

    private void setSearch(){
        mainSearch.setIconifiedByDefault(false);
        mainSearch.setSubmitButtonEnabled(false);
        mainSearch.setQueryHint(getString(R.string.search_hint));
        mainSearch.setFocusable(false);
        mainSearch.setBackgroundResource(R.drawable.bg_of_search);
        mainSearch.setOnQueryTextListener(this);
    }

    //akses pribadi
    //mengatur pengaturan daftar sura
    public static VithaMainListAdapter listAdapter;
    private void setList(){
        //inisialisasi LinearLayoutManager
        LinearLayoutManager llmanager = new LinearLayoutManager(this);
        //mengatur adapter untuk mainList

        listAdapter = new VithaMainListAdapter(this,listData);
        //mengaplikasikan layout manager dan adapter untuk mainlist
        mainList.setLayoutManager(llmanager);
        mainList.setAdapter(listAdapter);

    }

    @Override
    public void onBackPressed()
    {
        //gg ada yg perlu di lakukan disini
        // menonaktifkan tombol kembali||back
    }

    @Override
    public boolean onQueryTextChange(String que)
    {
        // TODO: bertugas melakukan aksi ketika pencarian ditulis
        if(TextUtils.isEmpty(que)){ //memeriksa kondisi mainSearch (SearchView)
            //jika kotak pencarian kosong/empty
            mainList.setAdapter(listAdapter); // menampilkan semua sura /mengatur ulang adapter widget mainList(RecyvlerView) jika widget mainSearch kosong
            mainSearch.setBackgroundResource(R.drawable.bg_of_search); //mengatur background widget mainSearch(SearchView)
            String latestSura = prefs.getLatestSura(); //mengambil sura terakhir yg diputar dari sharedPreferense dalam bentuk String
            if(latestSura!=null){ //memeriksa kondisi variable latestSura apakah masih berbentuk null atau memiliki value
                mainList.smoothScrollToPosition(utils.matchSura(latestSura)/*fungsi untuk memeriksa sura terakhir*/); //jika tidak null mainList akan diarahkan ke posisi surah terakhir
            }
        }else{ // jika mainSearch di isi query maka mainSearch akan di isi dengan nama surah yg sesuai dengan query yg dimasukan
            mainSearch.setBackground(bg_on_search()); //mengatur background mainSearch (SearchView)
            ArrayList<String> sdata = new ArrayList<String>(); //membuat dan mendeklarasikan array baru untuk menampung nama sura yg sesuai dengan query dari mainSearch (SearchView)
            for(int i = 0;i<listData.size();++i){ //melakukan perulangan berdasarkan banyak content dr listData (Array)
                String suraName = listData.get(i); //mengambil nama sura dari listData(Array) dalam bentuk String
                if(suraName.toLowerCase().contains(utils.remover(que))){ // memeriksa kondisi apabila nama sura sesuai dengan query yg dimasukan dari mainSearch (SearchView)
                    sdata.add(suraName); //memasukan nama sura kedalam array
                }
                VithaMainListAdapter sadapter = new VithaMainListAdapter(this,sdata); // membuat dan mendeklarasikan adapter baru untuk mainList (RecyclerView)
                mainList.setAdapter(sadapter); //mengatur adapter untuk mainList (RecyclerView)
            }
        }

        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String p1)
    {
        // TODO: tidak dibutuhkan tp harus ditulis
        return false;
    }


    private Drawable bg_on_search(){ // fungsi membuat atau membuat background untuk mainSearch(SearchView) yg digunakan ketika query di masukan ke mainSearch (SearchView)
        GradientDrawable gd = new GradientDrawable(); // membuat dan mendeklarasikan variable gd ( GradientDrawable )
        gd.setStroke(1,Color.parseColor("#E0F2F1")); // mendeklarasikan warna border/dinding/stroke
        gd.setCornerRadius(20); //memngatur corner/batas bagian sudut
        return gd; //mengembalikan dalam bentuk drawable
    }

    // membuat menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.vitha_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        //mengambil string
        String indonesia = getResources().getString(R.string.lang_id);
        String inggris = getResources().getString(R.string.lang_en);
        //aray kontent
        String[] lang = new String[]{indonesia,inggris};
        //alert
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        ArrayAdapter adapterLanguage = new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,lang);
        alert.setAdapter(adapterLanguage,onLangClick());
        alert.show();
        return super.onOptionsItemSelected(item);
    }

    DialogInterface.OnClickListener onLangClick(){
        return new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface p1, int position)
            {
                switch(position){
                    case 0:
                        //bahasa indonesia
                        prefs.putLang(prefs.LANG_ID);
                        break;
                    case 1:
                        //bahasa inggris
                        prefs.putLang(prefs.LANG_EN);
                }
                //mulai ulang activity
                recreate();
            }
        };
    }



}