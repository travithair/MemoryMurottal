package com.travitha.memorymurottal;
import android.content.*;
import android.os.*;
import android.support.v7.app.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.util.*;
import android.widget.AdapterView.*;

//class untuk menampilkan alertdialog
public class VithaPrefsAlert
{
	private static TextView tvSuraName; // untuk menampilkan nama sura
	private static Spinner spFromVerse,spToVerse,spRepeatFor,spReciter; // untuk menampilkan spinner From Verse,To Verse dan lain nya
	private static String suraName; // nama String untuk menyimpan nama sura lalu untuk di tampilkan di tvSuraName
	private static CheckBox cbEnableSettings; // menampilkan checkbox untuk enable settings


	private static String[] listAyat; // array untuk menyimpan dafatar ayat
	private static String[] reciter; // array untuk menyimpan jumlah reciter yg tersedia reciter satu dan dua
	private static String[] repeat; // array untuk menyimpan daftar pilihan repeat yg tersedia

	private static VithaPrefs prefs; // untuk pengaturan(tambah/edit data tersimpan)

	private static Context ctx; // untuk Context (siklus hidup)
	private static AlertDialog.Builder alert;

	private static boolean isShow = false;
	public static void alert(Context ctxx,String suraNamee){
		// di butuhkan Context dan Nama Sura
		suraName=suraNamee; // mengambil nama sura
		ctx=ctxx; // mengambil Context
		prefs = new VithaPrefs(ctx); // deklarasi prefs
		try // menghindari fc jika error
		{
			String noRepeat = ctx.getResources().getString(R.string.spin_no_repeat);
			String stopManually = ctx.getResources().getString(R.string.spin_stop_manually);
			String root = Environment.getExternalStorageDirectory().getPath()+"/.Murottal/"; // mendeklarasikan letak nama sura di simpan di memory
			listAyat = new File(root+suraName+"/reciter1").list(); //mengambil daftar nama reciter/sura
			reciter = new String[]{"Abdul_Basit","Maher_AlMuaqli"}; // mendeklarasikan jumlah reciter yg tersedia
			repeat = new String[]{noRepeat,stopManually,"2","5","10","15","18","20","25","30"}; // mendeklarasikan daftar pilihan repeat yg tersedia
		}
		catch (Exception e)
		{
			//jika error akan dilempar kesini
		}

		alert = new AlertDialog.Builder(ctx); // mendeklarasikan AlerrDialog.Builder untuk menampilkan dialog
		alert.setCancelable(false); // menonaktifkan tombol back dan klik diluar jendela
		alert.setView(theView()); // mengatur View dr fungsi theView()
		alert.setPositiveButton(R.string.save_changes,onButtonClick); //mengatur tulisan/title untuk tombol positif
		alert.setNegativeButton(R.string.cancel,onButtonClick); // untuk tombol negatif ,karena tidaj ada aksi yg perlu dilakukan untuk tombol negatif (null)
		alert.setCancelable(false);
		//jika dialog belum tampil > tampilkan
		if(!isShow){
			alert.show();// menampilakan dialog
			isShow = true; //status dialog sudah di tampilkan
		}
	}

	private static View theView(){
		//view untuk tampilan alert
		View v = LayoutInflater.from(ctx).inflate(R.layout.prefs_alert,null); // inflate prefs_alert.xml kedalam bentuk View
		cbEnableSettings = v.findViewById(R.id.prefsalertCbEnable_settings); // deklarasi checkbox enabne setting
		cbEnableSettings.setOnCheckedChangeListener(onCheck); // memgatur ketika CheckBox di centang

		tvSuraName = v.findViewById(R.id.prefsalertTextViewSuraName); // deklarasi TextView Untuk nama sura
		tvSuraName.setText(utils.remover(suraName)); // mengatur Nama Sura
		spFromVerse = v.findViewById(R.id.prefsalertSpinnerFromVerse); // deklrasi Spinner untuk From Verse
		spFromVerse.setAdapter(new ArrayAdapter<String>(ctx,android.R.layout.simple_spinner_item,ayatPosition())); // mengatur adapter untuk From Verse dan data di ambil dr ayatPosition()
		spToVerse = v.findViewById(R.id.prefsalertSpinnerToVerse); // mendeklarasikan spinner untuk To Verse
		spToVerse.setAdapter(new ArrayAdapter<String>(ctx,android.R.layout.simple_spinner_item,ayatPosition()));// mengatur adapter untukbTo Verse  ,data di ambil dr ayatPosition()
		spRepeatFor = v.findViewById(R.id.prefsalertSpinnerRepeatFor); // mendeklarasikan Spinner untuk Repeat
		spRepeatFor.setAdapter(new ArrayAdapter<String>(ctx,android.R.layout.simple_spinner_item,repeat)); // mengatur adapter untuk Repeat,data di ambil dr array repeat
		spReciter = v.findViewById(R.id.prefsalertSpinnerReciter); // mendeklarasikan Spinner untuk Reciter
		spReciter.setAdapter(new ArrayAdapter<String>(ctx,android.R.layout.simple_spinner_item,reciter)); // mengatur adapter untuk Reciter,,data di ambil dr ayat Reciter
		readSettings(); // memanggil fungsi readSettings()
		return v; // kembalikan dalam bentuk View
	}




	private static String[] ayatPosition(){
		// mengubah nama semua ayat kedalam bentuk angka
		String[] res = new String[listAyat.length];
		for(int i = 0;i<listAyat.length;++i){
			res[i] = Integer.toString(i+1);
		}
		return res; //dikembalikan dalam bentuk array
	}

	private static DialogInterface.OnClickListener onButtonClick = new DialogInterface.OnClickListener(){

		@Override
		public void onClick(DialogInterface p1, int p2)
		{
			if(p1.BUTTON_POSITIVE==p2){
				//jika tombol postif ditekan
				positive();
			}
			isShow = false; //mengubah status dialog sudah di tutup

		}
	};

	private static void positive(){
		//aksi ketika tombol positif (Save Settings) di tekan
		//mengambil nilai spinner yang terpilih
		int fVerse = spFromVerse.getSelectedItemPosition();
		int tVerse = spToVerse.getSelectedItemPosition();
		int rFor = spRepeatFor.getSelectedItemPosition();
		int reci = spReciter.getSelectedItemPosition();

		if(tVerse<fVerse){
			tVerse = fVerse; //jika ayat yg dipilih di bawah to verse akan di lupakan atau tidak dimasukan ledalam pengaturan
		}

		String enableSettings = null; // variable untuk menyimpan pengaturan enable atau disable settings
		String fromVerse = Integer.toString(fVerse); // variable untuk menyimpan nilai Spinner From Verse
		String toVerse = Integer.toString(tVerse);  // variable untuk menyimpan nilai Spinner To Verse
		String repeatFor = Integer.toString(rFor); // ... ... ... ... Repeat
		String reciter = Integer.toString(reci); // ... ... ... ... Reciter


		//memeriksa checkbox
		if(cbEnableSettings.isChecked()){
			enableSettings = "1"; //satu untuk memperbolehkan pengaturan
		}else{
			enableSettings = "0"; //nol untuk tidak
		}
		//pengaturan yg di ambil dr spinner tadi akan di simpan
		//menyimpan pengaturan dengan key nama sura ke sharedpreferense
		prefs.putSettings(suraName /*nama sura untuk key*/,fromVerse /*pengaturan from verse*/,toVerse/*pengaturan to verse*/,repeatFor/*pengaturan repeat*/,reciter/*pengaturan reciter*/,enableSettings/*pengaturan enable settings*/);
		utils.toast(ctx,R.string.prefs_alert_message);  //text pesan nya dari /res/values/string.xml

		String latestSura = prefs.getLatestSura(); //mengambil sura terakhir yg di putar
		if(latestSura!=null){
			//jika sura terakhir tidak null/artinya ada sura yg sedang diputar sebelum nya
			if(latestSura.equals(suraName)){
				//jika user sedang mengedit sura yg sedang di putar maka sura akan di atur ulang di service tergantung pengaturan user dan jika bukan sura akan tetap dimainkan
				VithaServicePlayer.player(suraName,VithaServicePlayer.KEY_PLAY_NEXT_STOP); // perintah untuk memulai ulang sura
			}
		}


	}



	private static void readSettings(){
		//fungsi untuk membaca pengaturan dr shared preferense
		HashMap<String,String> settingsData = prefs.getSettings(suraName); // mengambil pengaturan dr VithaPrefs(sharedpreferense) dalam bentuk HashMap(array dengan key)
		// memecah settings(HashMap) ke dalam bentuk integer
		//untuk kata kunci sudah di atur di VithaPrefs.java lalu tinggal di panggil dr class ini
		int enableSettings = Integer.parseInt(settingsData.get(prefs.ENABLE_SETTINGS)); // pengaturan enable settings
		int fromVerse = Integer.parseInt(settingsData.get(prefs.FROM_VERSE)); // pengaturan From Verse
		int toVerse = Integer.parseInt(settingsData.get(prefs.TO_VERSE));// pengaturan To Verse
		int repeatFor = Integer.parseInt(settingsData.get(prefs.REPEAT)); // pengaturan Repeat
		int reciter = Integer.parseInt(settingsData.get(prefs.RECITER)); // pengaturan Reciter

		if(toVerse==-1){
			// jika toVerse masih di bawah nol,artinya pengaturan belum pernah di ubah oleh user
			toVerse = listAyat.length-1; // mengatur toVerse tergantung banyak nya ayat dr sura
		}
		//mengatur spinner sesuai dr nilai pengaturan user yg d baca tdi
		spFromVerse.setSelection(fromVerse);
		spToVerse.setSelection(toVerse);
		spRepeatFor.setSelection(repeatFor);
		spReciter.setSelection(reciter);

		if(enableSettings==0){
			//jika enable setting bernilai nol,artinya pengaturan sura di alihkan ke pengaturan default
			cbEnableSettings.setChecked(false); // menghilangkan centang
			setDisable(); // mematikan/disable spinner
		}else{
			// jika enable settings bernilai bukan 0 (bisa 1,2,3 etc)
			cbEnableSettings.setChecked(true); // mengaktifkan centang
			setEnable(); // mengaktifkan/enable spinner
		}
	}

	//mengatur acksesibiltas widget

	private static void setEnable(){
		//fungsi mengaktifkan spinner yg di panggil dr readSettings() dan onCheckedChanged()
		spFromVerse.setEnabled(true);
		spToVerse.setEnabled(true);
		spRepeatFor.setEnabled(true);
		spReciter.setEnabled(true);
	}

	private static  void setDisable(){
		//fungsi menonaktifkan spinner yg di panggil dr readSettings() dan onCheckedChanged()
		spFromVerse.setEnabled(false);
		spToVerse.setEnabled(false);
		spRepeatFor.setEnabled(false);
		spReciter.setEnabled(false);
	}



	private static CompoundButton.OnCheckedChangeListener onCheck = new CompoundButton.OnCheckedChangeListener(){

		@Override
		public void onCheckedChanged(CompoundButton p1, boolean p2)
		{
			if(cbEnableSettings.isChecked()){
				//jika checkbox di centang
				setEnable(); // mengaktifkan spinner
			}else{
				// jika checkbox tidak di centang
				setDisable(); // menonaktifkan spinner
			}
		}


	};
}