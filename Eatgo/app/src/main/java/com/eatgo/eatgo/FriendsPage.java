package com.eatgo.eatgo;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class FriendsPage extends AppCompatActivity {

    // DB 제어 관련
    private DBManager dbmgr;
    private SQLiteDatabase sdb;
    private SQLiteStatement stmt;
    private Cursor cursor;
    private String id;

    // 친구의 먹자GO 탭 페이지
    private EditText[] scoreET = new EditText[5];
    private EditText kor, jap, chn, wes, etc;
    private CheckBox[] exceptCB = new CheckBox[5];
    private CheckBox kore, jape, chne, wese, etce;
    private Button back;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_page);

        Intent receivedIntent = getIntent();
        id = receivedIntent.getStringExtra("id");

        title = (TextView) findViewById(R.id.title);
        title.setText(id + "님의 먹자GO");

        scoreET[0] = kor = (EditText) findViewById(R.id.kor);
        scoreET[1] = jap = (EditText) findViewById(R.id.jap);
        scoreET[2] = chn = (EditText) findViewById(R.id.chn);
        scoreET[3] = wes = (EditText) findViewById(R.id.wes);
        scoreET[4] = etc = (EditText) findViewById(R.id.etc);

        exceptCB[0] = kore = (CheckBox) findViewById(R.id.kore);
        exceptCB[1] = jape = (CheckBox) findViewById(R.id.jape);
        exceptCB[2] = chne = (CheckBox) findViewById(R.id.chne);
        exceptCB[3] = wese = (CheckBox) findViewById(R.id.wese);
        exceptCB[4] = etce = (CheckBox) findViewById(R.id.etce); // db 데이터 담을 그릇 만들어주기

        back = (Button) findViewById(R.id.back); // 돌아가기 버튼

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        try {
            dbmgr = new DBManager(getApplicationContext());
            sdb = dbmgr.getWritableDatabase(); // 쓰기 가능한 db를 불러옵니다.
            cursor = sdb.rawQuery("SELECT * FROM eatgo WHERE id='" + id + "';", null); // 해당하는 id에 해당하는 부분의 데이터를 모두 가져옵니다.
            cursor.moveToNext();

            for(int i=0; i<5; i++ ) {
                scoreET[i].setText(cursor.getString(i+2));
                if(cursor.getInt(i+7) == 1) exceptCB[i].setChecked(true);
                else exceptCB[i].setChecked(false);
            }

        } catch (SQLiteException e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        } finally {
            cursor.close();
            dbmgr.close();
        }
    }
}
