package com.eatgo.eatgo;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class AddFriends extends AppCompatActivity {

    // DB 제어 관련
    private DBManager dbmgr;
    private SQLiteDatabase sdb;
    private SQLiteStatement stmt;
    private Cursor cursor;
    private String id;
    private int i;

    private Button backBtn;
    private Button addBtn;

    private ArrayList<String > addFriendsList = new ArrayList<String>();
    private ArrayList<CheckBox> addFriendsCheck = new ArrayList<CheckBox>();
    private ArrayList<String > myFriends = new ArrayList<String>();
    private ListView addFriendsListView;
    private AddFriendsAdaptor addFriendsAdaptor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // 상위 클래스의 onCreate 메소드를 먼저 호출하여 먼저 실행 되게 하고, 오버라이드된 메소드를 처리
        setContentView(R.layout.activity_add_friends); // 레이아웃 설정

        Intent receivedIntent = getIntent();
        id = receivedIntent.getStringExtra("id"); // id를 설정
        addFriendsListView = (ListView) findViewById(R.id.addFriendsList); //리스트뷰 설정

        try {
            dbmgr = new DBManager(getApplicationContext());
            sdb = dbmgr.getReadableDatabase();
            cursor = sdb.rawQuery("SELECT * FROM eatgo WHERE id NOT IN ('" + id + "');", null); // 자신의 ID에 해당하는 데이터를 제외하고 모든 데이터를 불러옵니다.

            String friendsID;
            addFriendsList.clear(); // 일단 친추목록을 비웁니다.

            while(cursor.moveToNext()) {
                friendsID = cursor.getString(0);
                addFriendsList.add(friendsID); // 다음으로 움직일 수 있을 때 까지 친추목록에 친구 ID를 추가합니다.
            }

            addFriendsAdaptor = new AddFriendsAdaptor(getApplicationContext(), R.layout.add_friends_list, addFriendsList);
            // 어댑터는 위젯에 출력할 데이터 원본을 가지고 있응께 이 원본을 출력할 뷰를 생성하여 위젯에게 제공
            addFriendsListView.setAdapter(addFriendsAdaptor);

        } catch (SQLiteException e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        } finally {
            cursor.close();
            dbmgr.close();
        }

        Button backBtn = (Button) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button addBtn = (Button) findViewById(R.id.addBtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "업데이트 예정입니다! ^_^♡", Toast.LENGTH_SHORT).show();
//for문 써서 addfriendcheck에 몇번째의 체크 됐는지 받아오고, addFriendsListView 체크된 n번째에 해당하는 값들을 저장하면 됩니다
            }
        });
    }

    private class AddFriendsAdaptor extends BaseAdapter {

        Context context;
        LayoutInflater Inflater;
        ArrayList<String> arID;
        int layout;

        public AddFriendsAdaptor(Context context, int layout, ArrayList<String> arID) {
            this.context = context;
            Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.layout = layout;
            this.arID = arID;
            addFriendsCheck.clear();
        }

        @Override
        public int getCount() {
            return arID.size();
        }

        @Override
        public String getItem(int position) {
            return arID.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null)
                convertView = Inflater.inflate(layout, parent, false);

            TextView friendsID = (TextView) convertView.findViewById(R.id.friendsID); // 텍스트 뷰 설정
            friendsID.setText(arID.get(position)); //arID 리스트의 길이 = 친추목록창에 표시되는 아이디의 개수
            String str = "arID의 size는 " + arID.size() + "입니다.";
            Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();

            CheckBox friendsCheck = (CheckBox) convertView.findViewById(R.id.friendsCheck);
            addFriendsCheck.add(friendsCheck); // friendsCheck 체크박스를 addFriendsCheck에 추가가

           return convertView;
        }
    }

}
