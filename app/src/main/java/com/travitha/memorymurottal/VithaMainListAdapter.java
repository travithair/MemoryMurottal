package com.travitha.memorymurottal;

import android.support.v7.widget.RecyclerView;
import android.view.*;
import java.util.*;
import android.widget.*;
import android.content.*;
import android.support.v7.widget.*;
import java.io.*;
import android.media.*;
import android.os.*;

public class VithaMainListAdapter extends RecyclerView.Adapter<VithaMainListAdapter.Holder> implements View.OnClickListener
{
    private ArrayList<String> listData; //array untuk menyimpan nama sura
    VithaServicePlayer play = null; //VithaPrefs berbentuk null,karena yg d butuhin hanya namanya bukan fungsi nya
    private Context ctx;
    //consructor ArrayList<String>
    public VithaMainListAdapter(Context ctx,ArrayList<String> listData){
        this.listData=listData; //mengambil nama sura dr MainActivity
        this.ctx=ctx; //mengambil Context dr MainActivity
    }

    //class Holder
    //tempat deklarasi variable
    public class Holder extends RecyclerView.ViewHolder{
        View v; //variable View
        TextView tvTitle; //Menampilkan nama sura
        ImageButton ibPlay,ibPref; // menampilkan button play dan prefs AlertDialog
        LinearLayout llAyat; // layout utama/parent
        public Holder(View v){
            //menerima item dari OncreateViewHolder
            super(v);
            this.v=v;//mengambil view dr pengirim  > OnCreateViewHolder()
            //inisialisasi item dari row_main_list.xml
            tvTitle = v.findViewById(R.id.rowmainlistTextViewNamaSura);
            ibPlay = v.findViewById(R.id.rowmainlistImageButtonPlay);
            ibPref = v.findViewById(R.id.rowmainlistImageButtonPref);
            llAyat = v.findViewById(R.id.rowmainlistLLAyat);
        }
    }

    @Override
    public VithaMainListAdapter.Holder onCreateViewHolder(ViewGroup parent, int p2)
    {
        // TODO: Implement this method
        //mengambil layout = diubah menjadi View dengan LayoutInflater
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_main_list,parent,false); //inflate / convert xml ke dalam bentuk view
        v.setOnClickListener(this); //mengatur ketika View di klik
        v.setId(1512); //mengatur id untuk View
        return new Holder(v); //mengirim View ke class Holder
    }

    @Override
    public int getItemCount()
    {
        //mengatur jumlah item berdasarkan besar listData
        return listData.size();
    }

    String latestSura = null;
    @Override
    public void onBindViewHolder(VithaMainListAdapter.Holder h, int position)
    {
        // TODO: Implement this method
        //mrngatur tampilan
        latestSura = new VithaPrefs(ctx).getLatestSura();
        String suraName = listData.get(position);
        h.tvTitle.setText(utils.remover(suraName));
        if(!suraName.equals(latestSura)){
            //untuk sura yg tidak diputar
            h.tvTitle.setTextColor(0xFFffffff); //text warna putih 0xFFffffff = #ffffff
            h.v.setBackgroundResource(R.drawable.bg_of_active_item); // mengatur Background untuk sura yg sedang tidak diputar
            //mengatur ibPlay
            h.ibPlay.setImageResource(android.R.drawable.ic_media_play); //mengatur tombol play untuk sura yg sedang tidak d putar
        }else{
            //untuk sura yg diputar
            h.tvTitle.setTextColor(0xFF000000); //text warna hitam 0xFF000000 = #000000
            h.v.setBackgroundResource(R.drawable.bg_of_un_active_item); //mengatur background untuk sura yg sedang di putar
            //mengatur ibPlay
            if(play.isPlaying){
                h.ibPlay.setImageResource(android.R.drawable.ic_media_pause); //jika sura sedang di putar menampilkan gambar pause
            }else{
                h.ibPlay.setImageResource(android.R.drawable.ic_media_play);//jika sura sedang di putar menampilkan gambar play
            }

        }
        try
        {
            //view untuk menampilkan daftar ayat
            View childView = listAyat(suraName); //mengambil view dr fungsi listAyat(Nama Sura)
            if(position == expand){ //jika posisi sesuai dengan expand(int) maka menampilkan daftar ayat
                h.llAyat.removeAllViews(); //menghapus View Sebelum nya agar bisa dimasukan View baru (daftar ayat baru)
                h.llAyat.addView(childView); // menambahkan View (Daftar Ayat)
            }else{
                h.llAyat.removeAllViews(); //jika position(int) dan expand(int) nilai nya tidak sama maka yg perlu dilakukan adalah menghapus View (Tidak menampilan daftar ayat)
            }//
        }
        catch (Exception e)
        {
            //error
            Toast.makeText(ctx,e.toString(),0).show();
        }

        //mengatur tag untuk view untuk dikirim ke onclick
        //tag di gunakan untuk mengenali view mana yg di klik
        //tag di sesuaikan dengan View yg di klik (apakah itu nama sura,tombol play atau tombol pengaturan) nanti akan di proses di onclick
        h.v.setTag(position);
        h.ibPlay.setTag(suraName);
        h.ibPref.setTag(suraName);
        //mengatur onclick untuk tombol play dan pengaturan
        h.ibPlay.setOnClickListener(this);
        h.ibPref.setOnClickListener(this);
    }

    int expand = -1;//mengatur posisi mana yg ingin di expand lihat di onBindViewHolder()
    @Override
    public void onClick(View v)
    {                 // ^pintu masuk

        //di sini akan di proses sesuai sengan View Mana yg diklik (Nama Sura,Tombol play atau Pengaturan)
        switch(v.getId()){
            case 1512: //id dri View Utama atau nama sura
                int pos = (int) v.getTag(); //mengambil tag dan di jadikan pengenal posisi Sura Mana yg di klik
                if(expand==pos){
                    //jika expand sama dengan posisi sura artinya sembunyikan daftar ayat
                    expand = -1; //mengatur expand ke -1 atau dibawah nol
                }else{
                    //jika wxpand tidak sama dengan posisi / nilai expand sebelumnya maka expand di atur sesuai nomor urut sura mana yg di klik
                    expand = pos; //
                }
                notifyDataSetChanged(); // memberitaukan Recyclerview untuk memperbahrui tampilan nya (tergantung status sura dan ayat mana yg sedang di putar)
                break;//tanda berhenti / pembatas
            case R.id.rowmainlistImageButtonPlay: // id dr tombol play
                // play adalah nama VithaServicePlayer yg telah diatur di atas Public VithaMainListAdapter()
                String suraName = (String) v.getTag(); //mengambil nama sura dr tag
                if(suraName.equals(latestSura)){ //jika nama sura sama dengan sura terakhir yg di putar maka sura akan di pause
                    play.player(suraName,play.KEY_PLAY_PAUSE); // code untuk mengirim perintah pause atau play (servive akan melakukan sesuai kondisi yg sedang berlangsung,jika audio sedang di putar maka otomatis audio d hentikan ,bgitu jga sebalik nya)
                }else{
                    //jika berlawanan dengan kondisi di atas maka sura akan di play
                    play.player(suraName,play.KEY_PLAY_NEW); // code untuk memulai memutar sura baru
                }
                break;
            case R.id.rowmainlistImageButtonPref: // id dr tombol pengaturan
                suraName = (String) v.getTag(); // mengambil nama sura dr tag yg diatur
                VithaPrefsAlert.alert(ctx,suraName); // menampikan dialog,proses selanjut nya,lihat VithaPrefsAlert.java
                break;
        }

    }
    public static VithaAyatAdapter adapter ; // mengatur adapter daftar ayat(bukan sura) untuk bisa di akses dr class lain (VithaServivePlayer.java)
    private View listAyat(String suraName) throws Exception{ //fungsi untuk  mengambil daftar ayat dan menampilkan nya yg di panggil dr OnBindViewHolder
        String root = Environment.getExternalStorageDirectory().getPath()+"/.Murottal/";
        HashMap<String,String> settings = new VithaPrefs(ctx).getSettings(suraName);// mengambil pengaturan untuk sura yg di klik
        String[] reciters = new String[]{"reciter1" ,"reciter2"};// array nama folder reciter
        int reciter = Integer.parseInt(settings.get(VithaPrefs.RECITER)); // mengambil reciter sesuai pengaturan
        String[] listAya = new File(root+suraName+"/"+reciters[reciter]).list(); // mengambil daftar ayat
        String[] listAyat = new String[listAya.length]; // array untuk menyimpan daftar path ayat
        for(int i=0;i<listAya.length;++i){
            listAyat[i] = root+suraName+"/"+reciters[reciter]+"/"+listAya[i]; // mengambil path file ayat
        }
        View v = LayoutInflater.from(ctx).inflate(R.layout.list_ayat_layout,null); // inflate list_ayat.xml ke dalam bentuk View
        RecyclerView rv = v.findViewById(R.id.listAyatLayoutRVList);
        GridLayoutManager glmanager = new GridLayoutManager(ctx,5); // layout manager untuk recyclerView menjadi grid,perhatikan bedanya dengan RecyclerView Nama sura
        adapter = new VithaAyatAdapter(ctx,suraName,listAyat); // nendeklarasikan adapter untuk daftar ayat
        rv.setLayoutManager(glmanager); // mengatur layput manager
        rv.setAdapter(adapter);//mengatur adapter
        return v; // hasil akhir di kembalikan atau d ikirim ke onBindViewHolder()
    }
}