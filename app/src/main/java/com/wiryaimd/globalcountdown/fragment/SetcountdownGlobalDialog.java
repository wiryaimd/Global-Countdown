package com.wiryaimd.globalcountdown.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.wiryaimd.globalcountdown.R;
import com.wiryaimd.globalcountdown.adapter.GlobalcAdapter;
import com.wiryaimd.globalcountdown.model.DateModel;
import com.wiryaimd.globalcountdown.model.GlobalCountdownModel;
import com.wiryaimd.globalcountdown.util.GlobalcReciver;
import com.wiryaimd.globalcountdown.util.PreferencesManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

public class SetcountdownGlobalDialog extends DialogFragment {

    private Activity activity;
    private FragmentManager fm;
    private RecyclerView recyclerView;

    private DatabaseReference dbref;
    private StorageReference storageref;

    private DateModel dateModel;

    private TextView tvimgname;
    private EditText edttitle, edtdesc;
    private Switch isNotification;
    private Button btnselectimg, btnsave;
    private ProgressBar loading;

    private String uuid, imgurl, username;
    private int globalcSize, oneClick;
    private boolean isSelectimg;

    private Random random;

    public static final int PERM_CODEG = 20;

    public SetcountdownGlobalDialog(Activity activity, RecyclerView recyclerView, FragmentManager fm, DateModel dateModel) {
        this.activity = activity;
        this.recyclerView = recyclerView;
        this.fm = fm;
        this.dateModel = dateModel;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }catch (NullPointerException e){
            System.out.println(e);
        }
        return inflater.inflate(R.layout.dialog_setcountdownglobal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        dbref = FirebaseDatabase.getInstance().getReference();
        storageref = FirebaseStorage.getInstance().getReference();
        random = new Random();
        isSelectimg = false;

        tvimgname = view.findViewById(R.id.setcountdownglobal_imgname);
        edttitle = view.findViewById(R.id.setcountdownglobal_edttitle);
        edtdesc = view.findViewById(R.id.setcountdownglobal_edtdesc);
        isNotification = view.findViewById(R.id.setcountdownglobal_notification);
        btnselectimg = view.findViewById(R.id.setcountdownglobal_selectimg);
        btnsave = view.findViewById(R.id.setcountdownglobal_btnsave);
        loading = view.findViewById(R.id.setcountdownglobal_loadingimg);
        isNotification.setChecked(true);
        loading.setVisibility(View.GONE);

        tvimgname.setText("");

        uuid = UUID.randomUUID().toString();
        username = PreferencesManager.getUsername(activity);
        imgurl = "none";

        globalcSize = 10 + random.nextInt(300 - 10 + 1);
        System.out.println("random: " + globalcSize);

        btnselectimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (permissionGalleryCek(activity)){
                    openGallery();
                }
            }
        });

        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oneClick = 0;
                if (!edttitle.getText().toString().isEmpty()) {
                    if (isSelectimg && !imgurl.equalsIgnoreCase("none") || !isSelectimg && imgurl.equalsIgnoreCase("none")) {
                        Date date = null;
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm", Locale.getDefault());

                        String mydate = dateModel.getYear() + "-" +
                                String.format(Locale.getDefault(), "%02d", Integer.parseInt(dateModel.getMonth()) + 1) + "-" +
                                dateModel.getDay() + "-" +
                                dateModel.getHours() + "-" +
                                dateModel.getMinute();
                        try {
                            date = dateFormat.parse(mydate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
                        if (date != null && alarmManager != null) {
                            if (date.getTime() > System.currentTimeMillis()) {
                                GlobalCountdownModel globalCountdownModel = new GlobalCountdownModel(
                                        username,
                                        edttitle.getText().toString(),
                                        edtdesc.getText().toString(),
                                        uuid,
                                        imgurl,
                                        dateModel,
                                        isNotification.isChecked(),
                                        new ArrayList<>(),
                                        new ArrayList<>(),
                                        date.getTime()
                                );


                                if (globalCountdownModel.isNotification()) {
                                    Intent intent = new Intent(activity, GlobalcReciver.class);
                                    intent.putExtra("CountdownTitle", globalCountdownModel.getTitle());
                                    intent.putExtra("CountdownDate", mydate);
                                    intent.putExtra("CountdownId", globalcSize);
                                    intent.putExtra("CountdownUUID", uuid);
                                    PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, globalcSize, intent, 0);

                                    long currentTime = System.currentTimeMillis();
                                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, currentTime + (globalCountdownModel.getMillisTime() - currentTime), pendingIntent);
                                }

                                dbref.child("countdown").child(uuid).setValue(globalCountdownModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        GlobalcFragment.cdcount += 1;
                                        dbref.child("countdown").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                oneClick += 1;
                                                if (oneClick == 1) {
                                                    ArrayList<GlobalCountdownModel> globalcList = new ArrayList<>();
                                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                        GlobalCountdownModel value = dataSnapshot.getValue(GlobalCountdownModel.class);
                                                        globalcList.add(value);
                                                    }

                                                    recyclerView.setLayoutManager(new LinearLayoutManager(activity));
                                                    GlobalcAdapter adapter = new GlobalcAdapter(activity, globalcList, getFragmentManager(), recyclerView);
                                                    recyclerView.setAdapter(adapter);

                                                    getDialog().dismiss();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                });

                            } else {
                                new InfoDialog("Alert", "The date is overdue").show(getFragmentManager(), "isOverdueGlobal");
                                getDialog().dismiss();
                            }
                        } else {
                            new InfoDialog("Alert", "Failed to save Countdown").show(getFragmentManager(), "isFailedGlobal");
                            getDialog().dismiss();
                        }
                    } else {
                        Toast.makeText(activity, "Please wait until image finish uploaded", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(activity, "Title required", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public boolean permissionGalleryCek(Activity activity){
        boolean cekperm = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                String[] perm = {Manifest.permission.READ_EXTERNAL_STORAGE};
                activity.requestPermissions(perm, PERM_CODEG);
            }else{
                cekperm = true;
            }
        }else{
            cekperm = true;
        }
        return cekperm;
    }

    public void openGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PERM_CODEG);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case PERM_CODEG:
                if (resultCode == Activity.RESULT_OK){
                    if (data != null) {
                        String filename = uuid + ".jpg";
                        loading.setVisibility(View.VISIBLE);
                        tvimgname.setText(filename);
                        isSelectimg = true;

                        storageref.child(uuid).putFile(data.getData()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                if (taskSnapshot.getTask().isComplete() && taskSnapshot.getMetadata() != null && taskSnapshot.getMetadata().getReference() != null){
                                    taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            imgurl = uri.toString();
                                            System.out.println("nyrottt kyaa echiii: " + imgurl);
                                        }
                                    });
                                    loading.setVisibility(View.GONE);
                                }else{
                                    new InfoDialog("Failed Upload Image", "Please check your connection and try again").show(fm, "FailedImg");
                                }
                            }
                        });

                    }
                }
                break;
        }
    }
}
