package com.example.mld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import io.realm.Realm;
import io.realm.RealmConfiguration;


public class Calendar extends AppCompatActivity implements View.OnClickListener{
    TextView todayDate1;
    TextView todayDate2;
    TextView today;
    MaterialCalendarView calendarView;
    LinearLayout tocontent;
    ImageView tohome;
    Button dateselect;
    Button exitSelecting;
    Button plus;
    TextView title;
    boolean inSelectionMode = false;
    String selectedDate = CalendarDay.today().getDate().toString();//전역변수 날짜. DB에 사용

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Realm.init(getApplicationContext());
        RealmConfiguration config = new RealmConfiguration.Builder().allowWritesOnUiThread(true).build();
        Realm.setDefaultConfiguration(config);
        Realm mRealm = Realm.getDefaultInstance();

        calendarView = findViewById(R.id.calendarView);
        todayDate1 = findViewById(R.id.todayDate1);
        todayDate2 = findViewById(R.id.todayDate2);
        today = findViewById(R.id.moveToday);
        plus = findViewById(R.id.plusCategory);
        tocontent = findViewById(R.id.whole_contents); //내용 표시 뷰
        tohome=findViewById(R.id.homebutton);
        dateselect = findViewById(R.id.dateSelect);
        exitSelecting = findViewById(R.id.exitSelect);
        title = findViewById(R.id.content1);


        todayDate1.setText(selectedDate);
        todayDate2.setText(selectedDate);


        DialogInterface.OnClickListener choiceListener = new DialogInterface.OnClickListener() { //날짜 선택모드. 여러개 선택과 범위 선택
            @Override
            public void onClick(DialogInterface dialog, int which) {
                calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_MULTIPLE);
                if(which != 0) //'여러 개 선택' 선택시
                    calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_RANGE);
                inSelectionMode = true;
                dateselect.setVisibility(View.GONE);
                exitSelecting.setVisibility(View.VISIBLE);
                plus.setVisibility(View.VISIBLE);
                dialog.dismiss();
            }
        };
        DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() { //날짜 선택모드 다이얼로그에서 취소버튼
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE);
            }
        };


        exitSelecting.setOnClickListener(this);
        today.setOnClickListener(this);
        tohome.setOnClickListener(this);
        tocontent.setOnClickListener(this);

        dateselect.setOnClickListener(new View.OnClickListener() { //날짜 선택버튼, 다이얼로그 표시
            @Override
            public void onClick(View view){
                AlertDialog.Builder dateSelectionMode = new AlertDialog.Builder(Calendar.this);
                dateSelectionMode.setTitle("날짜 선택모드");
                dateSelectionMode.setSingleChoiceItems(R.array.date_selection, -1, choiceListener);
                dateSelectionMode.setNegativeButton("취소", cancelListener);
                dateSelectionMode.show();
            }
        });

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() { //날짜를 클릭할 때마다(날짜가 바뀔때마다) 이벤트
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView calendarView, @NonNull CalendarDay date, boolean selected) {
                if(!inSelectionMode){
                    title.setText("");
                    selectedDate = date.getDate().toString();
                    todayDate1.setText(selectedDate);
                    todayDate2.setText(selectedDate);
                    Toast.makeText(getApplicationContext(), selectedDate, Toast.LENGTH_SHORT).show();
                    Contents_Data cd = mRealm.where(Contents_Data.class).equalTo("Date", selectedDate).findFirst();
                    try{
                        if(cd.isValid()){
                            title.setText(cd.getTitle());
                        }
                    } catch (Exception e){

                    }

                }
            }
        });
    }

    @Override
    public void onClick(View view){ //클릭 이벤트 정리.
        if(view == exitSelecting) {
            exitSelecting.setVisibility(View.GONE);
            dateselect.setVisibility(View.VISIBLE);
            plus.setVisibility(View.GONE);
            inSelectionMode = false;
            calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE);
        } else if(view == today) {
            calendarView.setSelectedDate(CalendarDay.today());
            calendarView.setCurrentDate(CalendarDay.today());
            selectedDate = CalendarDay.today().getDate().toString();
            todayDate1.setText(selectedDate);
            todayDate2.setText(selectedDate);
        } else if(view == tohome) {
            finish();
        } else if(view == tocontent) {
            Intent tocontent = new Intent(Calendar.this, Contents.class);
            tocontent.putExtra("Date", selectedDate);
            startActivity(tocontent);
        }
    }
}