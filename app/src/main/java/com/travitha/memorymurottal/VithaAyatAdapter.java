package com.travitha.memorymurottal;

import android.content.*;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;
import android.graphics.*;

public class VithaAyatAdapter extends RecyclerView.Adapter<VithaAyatAdapter.Holder> implements View.OnClickListener //implementasi onclick untuk class ini
{
    /*jalur pemanggilan fungsi ketika pertama kali di panggil
	   OnCreateViewHolder() > getItemCount() >   onBindViewHolder() */
    // Note : semua fungsi berawal @overide kecuali Onclick() adalah fungsi Bawaan RecyclerView.Adapter
    // Onclick adalah fungsi bawaan class OnClickListener
    private String[] listAyat; //array until menampung daftar ayat
    private VithaPrefs prefs; // variable prefs (VithaPrefs.java) yg digunakan untuk mengambil dan menyimpan data ke SharedPreferense
    private Context ctx; // variable ctx(Context) > Ruang Lingkup
    private String suraName; // variable suraName (String) > menyimpan nama sura
    public VithaAyatAdapter(Context ctx,String suraName,String[] listAyat){
        this.listAyat=listAyat; //mengambil daftar ayat dari pemanggil (dr class VithaMainListAdapter.java)
        this.ctx=ctx; //mengambil Context (Ruang Lingkup) Dari pemanggil
        prefs = new VithaPrefs(ctx); // mendeklarasikan variable prefs(VithaPrefs.java)
        this.suraName=suraName; // mengambil nama sura
    }

    public class Holder extends RecyclerView.ViewHolder{
        View v; // variable v (View)
        TextView tvTitle; // tvTitle (TextView) > menampilkan nama sura
        public Holder(View v){
            super(v);
            this.v=v; // > mengambil v (View) dari OnCreateViewHolder()
            tvTitle = v.findViewById(R.id.rowayatTVTitle); // mendeklarasikan tvTitle (TextView) dari row_ayat.xml
        }
    }

    @Override
    public VithaAyatAdapter.Holder onCreateViewHolder(ViewGroup parent, int p2)
    {
        // TODO: Implement this method
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_ayat,parent,false); // inflate/convert row_ayat.xml menjadi View
        //mengatur onclick ketika ayat ditekan
        v.setOnClickListener(this); //mendeklarasikan OnClickLisrener untuk View
        return new Holder(v); // mengirim View ke class VithaAyatAdapter.Holder()
    }

    @Override
    public int getItemCount()
    {
        // TODO: Implement this method
        return listAyat.length; // mendeklarasikan jumlah sura yang akan ditampilkan
    }


    @Override
    public void onBindViewHolder(VithaAyatAdapter.Holder H, int position)
    {
        // TODO: Implement this method
        H.tvTitle.setText(position+1+""); // mengatur Holder.tvTitle (nama sura sesuai posisi)
        H.v.setTag(listAyat[position]+","+position); // mengatur tag untuk View untuk di kirim ke Onclick untuk di proses (pengenal)
        String lastPath = prefs.getLatestPath(); // mengambil path terakhir dari SharedPreferense
        if(lastPath!=null){ // cek kondisi apakah path terakhir tidak null?

            if(prefs.getLatestSura().equals(suraName)){ //mencocokan sura terakhir dengan list untuk di tandai
                if(utils.matchSuraPosition(ctx,lastPath)!=position){ //mencocokan posisi ayat untuk di tandai
                    //untuk ayat yg sedang dimainkan
                    H.v.setBackgroundResource(R.drawable.bg_of_active_item); // mengatur background untuk ayat(bkan sura) yg sedang dimainkan
                    H.tvTitle.setTextColor(Color.WHITE); // mengatur warna text untuk ayat (bukan sura) yg sedang di mainkan
                }else{
                    //tampilan ayat yg tidak terpilih di sura yg dimainkan
                    H.v.setBackgroundResource(R.drawable.bg_of_un_active_item); //mengatur background untuk ayat (bulan sura) yg tidak dimainkan
                    H.tvTitle.setTextColor(Color.BLACK); // mengatur warna text untuk ayat (bukan sura) yang tidak diputar
                }

            }else{
                //tampilan daftar ayat yg tidak terpilih
                H.v.setBackgroundResource(R.drawable.bg_of_un_active_item);
                H.tvTitle.setTextColor(Color.BLACK);
            }
        }else{
            //tampilan daftar ayat pertama dibuka
            H.v.setBackgroundResource(R.drawable.bg_of_un_active_item);
            H.tvTitle.setTextColor(Color.BLACK);
        }
    }

    @Override
    public void onClick(View v)
    {                   //^jalan masuk
        // TODO: Implement this method.
        String t = (String) v.getTag(); //mengambil tag yg di atur dari
        String[] tags = t.split(","); //tag isinya nama ayat(bukan sura) dan posisi dan di jadikan array (dipisah)
        String pathAyat = tags[0]; // mengambil nilai pertama tags array (nama ayat)
        int pos = Integer.parseInt(tags[1]); // mengambil nilai array tags nilai kedua (posisi ayat)
        VithaServicePlayer play = null; // variable play tidak perlu di deklarasikan karna yg di butuhkan nama nya melainkan bukan fungsi nya jika pada pemrograman python harus nya lebih gampang
        play.playByAyat(pathAyat,pos); // memerintahkan fungsoi VithaServicePlayer.playByAyat() untuk memutar ayat sesuai yang di klik
    }
}
