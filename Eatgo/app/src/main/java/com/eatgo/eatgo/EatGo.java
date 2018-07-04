package com.eatgo.eatgo;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class EatGo extends FragmentActivity {

    // DB 제어 관련
    private DBManager dbmgr;
    private SQLiteDatabase sdb;
    private SQLiteStatement stmt;
    private Cursor cursor;
    private String id;
    private String sql;

    // 홈 화면
    private TextView title;
    private LinearLayout eatgo_menubar;

    // 탭
    private int indexOfSelectedTab;
    private int indexOfClickedTab;

    private LinearLayout eatgoTab;
    private LinearLayout searchTab;
    private LinearLayout friendsTab;
    private LinearLayout mypageTab;
    private LinearLayout moreTab;

    // 탭 제어 관련
    private LinearLayout[] tabSelected = new LinearLayout[5];
    private boolean[] isTabSelected = {true, false, false, false, false}; // 초기값
    private Button[] tabButton = new Button[5];

    // 리스너 및 인플레이터
    private TabClickListener tabClickListener = new TabClickListener();
    private LayoutInflater inflater;

    // 하단바
    private Button eatgo;
    private Button search;
    private Button freinds;
    private Button mypage;
    private Button more;

    /* -------------------------------------------------------------------------------------------------------------------- */

    // 1. 먹자GO 탭
    private TextView area;
    private Button selectArea;
    private Button refresh;
    private Button filter;
    private Button addFriends;

    // 2. 검색 탭
    private EditText searchText;
    private Button searchBtn;

    // 3. 친구의 먹자GO 탭
    private ArrayList<String > friendsList = new ArrayList<String>(); // <~>는 <String>와 같음.
    private ListView friendsListView;
    ArrayAdapter<String> friendsAdaptor; // 리스트객체와 리스트뷰의 연결고리 역할을 해줄 ArrayAdapter 선언
    private AdapterView.OnItemClickListener friendsClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) { // listview에 붙일 onItemClick 메소드를 만듭니다.
            Intent intent = new Intent(getApplicationContext(), FriendsPage.class); // FriendsPage로 가는 새로운 intent를 선언.
            intent.putExtra("id", friendsList.get(position)); // 어레이리스트의 position 받아서 넘겨줌
            startActivity(intent);
        }
    };

    // 4.나의 먹자GO 탭
    private EditText[] scoreET = new EditText[5];
    private EditText kor, jap, chn, wes, etc;
    private CheckBox[] exceptCB = new CheckBox[5];
    private CheckBox kore, jape, chne, wese, etce;
    private Button myInit, myApply;

    // 5. 더 보기 탭
    private Button maker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eat_go);

        /*
         * 0. 홈 화면
         */

        title = (TextView) findViewById(R.id.title);
        eatgo_menubar = (LinearLayout) findViewById(R.id.eatgo_menubar);

        tabSelected[0] = eatgoTab = (LinearLayout) findViewById(R.id.eatgoTab);
        tabSelected[1] = searchTab = (LinearLayout) findViewById(R.id.searchTab);
        tabSelected[2] = friendsTab = (LinearLayout) findViewById(R.id.friendsTab);
        tabSelected[3] = mypageTab = (LinearLayout) findViewById(R.id.mypageTab);
        tabSelected[4] = moreTab = (LinearLayout) findViewById(R.id.moreTab); // 버튼 눌렀을 때 화면 눌림 바뀜

        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.tab_eatgo, eatgoTab, true);
        inflater.inflate(R.layout.tab_search, searchTab, true);
        inflater.inflate(R.layout.tab_friends, friendsTab, true);
        inflater.inflate(R.layout.tab_mypage, mypageTab, true);
        inflater.inflate(R.layout.tab_more, moreTab, true); // inflater 사용해 xml로 정의된 view(또는 menu 등)을 실제 객체화 시킴.

        tabButton[0] = eatgo = (Button) findViewById(R.id.eatgo);
        tabButton[1] = search = (Button) findViewById(R.id.search);
        tabButton[2] = freinds = (Button) findViewById(R.id.friends);
        tabButton[3] = mypage = (Button) findViewById(R.id.mypage);
        tabButton[4] = more = (Button) findViewById(R.id.more); // 버튼 눌렀을 때 레이아웃 바뀜

        for(int i=0; i<5; i++)
            tabButton[i].setOnClickListener(tabClickListener);

        Intent receivedIntent = getIntent();
        id = receivedIntent.getStringExtra("id");
        Toast.makeText(getApplicationContext(), id + "님 환영합니다!", Toast.LENGTH_SHORT).show();

        /* -------------------------------------------------------------------------------------------------------------------- */




        /*
         * 1. 먹자GO 탭
         */

        area = (TextView) findViewById(R.id.area);
        selectArea = (Button) findViewById(R.id.selectArea);
        refresh = (Button) findViewById(R.id.refresh);
        filter = (Button) findViewById(R.id.filter);
        addFriends = (Button) findViewById(R.id.addFriends);

        // 1-1. 지역 선택 버튼
        selectArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "업데이트 예정입니다! ^_^♡", Toast.LENGTH_SHORT).show();
            }
        });

        // 1-2. 새로고침 버튼
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "새로고침 버튼입니다!", Toast.LENGTH_SHORT).show();
            }
        });

        // 1-3. 필터 설정 버튼
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "필터 설정 버튼입니다!", Toast.LENGTH_SHORT).show();
            }
        });

        // 1-4. 친구 추가 버튼
        addFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddFriends.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });

        /* -------------------------------------------------------------------------------------------------------------------- */

        /*
         * 2. 검색 탭
         */

        /* -------------------------------------------------------------------------------------------------------------------- */

        searchText = (EditText) findViewById(R.id.searchText);
        searchBtn = (Button) findViewById(R.id.searchBtn);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "업데이트 예정입니다! ^_^♡", Toast.LENGTH_SHORT).show();
                searchText.setText("");
            }
        });

        /*
         * 3. 친구의 먹자GO 탭
         */


        friendsListView = (ListView) findViewById(R.id.friendsListView);
        friendsListView.setOnItemClickListener(friendsClickListener);

        /* -------------------------------------------------------------------------------------------------------------------- */

        /*
         * 4. 나의 먹자GO 탭
         */

        scoreET[0] = kor = (EditText) findViewById(R.id.kor);
        scoreET[1] = jap = (EditText) findViewById(R.id.jap);
        scoreET[2] = chn = (EditText) findViewById(R.id.chn);
        scoreET[3] = wes = (EditText) findViewById(R.id.wes);
        scoreET[4] = etc = (EditText) findViewById(R.id.etc); // 환산값을 넣을 EditText 선언

        exceptCB[0] = kore = (CheckBox) findViewById(R.id.kore);
        exceptCB[1] = jape = (CheckBox) findViewById(R.id.jape);
        exceptCB[2] = chne = (CheckBox) findViewById(R.id.chne);
        exceptCB[3] = wese = (CheckBox) findViewById(R.id.wese);
        exceptCB[4] = etce = (CheckBox) findViewById(R.id.etce); // 체크박스 여부를 넣을 checkBox 선언

        myInit = (Button) findViewById(R.id.myInit); // 초기화 버튼
        myApply = (Button) findViewById(R.id.myApply); // 확인 버튼

        myInit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0; i<5; i++) {
                    scoreET[i].setText("0");
                    exceptCB[i].setChecked(false);
                }
            }
        });

        myApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    dbmgr = new DBManager(getApplicationContext());
                    sdb = dbmgr.getWritableDatabase();
                    stmt = sdb.compileStatement("UPDATE eatgo SET kor=?, jap=?, chn=?, wes=?, etc=?, " +
                            "kore=?, jape=?, chne=?, wese=?, etce=? WHERE id='" + id + "';"); //SQL 문을 재컴파일 할 수있는 미리 컴파일 된 명령문 개체로 컴파일합니다.

                    for(int i=0; i<5; i++ ) {
                        stmt.bindLong(1+i, Integer.parseInt(scoreET[i].getText().toString()));
                        stmt.bindLong(6+i, exceptCB[i].isChecked() ? 1 : 0);
                        // 문을 넣을 때마다 bindString (int, String) 및 bindLong (int, long)을 사용하여 해당 값을 채울 수 있습니다. 여기선 숫자니까 long을 사용.
                    }
                    stmt.executeUpdateDelete();
                    Toast.makeText(getApplicationContext(), "나의 먹자GO가 업데이트 되었습니다!", Toast.LENGTH_SHORT).show();

                } catch (SQLiteException e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                } finally {
                    cursor.close();
                    dbmgr.close();
                }
            }
        });


        /* -------------------------------------------------------------------------------------------------------------------- */

        /*
         * 5. 더 보기 탭
         */

        /* -------------------------------------------------------------------------------------------------------------------- */

    }



    private class TabClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            for(int i=0; i<5; i++) {
                if(v == tabButton[i]) {
                    indexOfClickedTab = i;
                    break;
                }
            }

            if(indexOfSelectedTab != indexOfClickedTab) {
                tabSelected[indexOfClickedTab].setVisibility(View.VISIBLE); // 클릭된 버튼을 VISIBLE로
                tabSelected[indexOfSelectedTab].setVisibility(View.GONE); // 이전 버튼을 없앰
                tabButton[indexOfClickedTab].setBackgroundColor(Color.parseColor("#CC0000")); // 클릭된 버튼의 색깔을 바꿔줌
                tabButton[indexOfSelectedTab].setBackgroundColor(Color.parseColor("#575757")); // 이전 버튼의 색깔을 바꿔줌

                switch(indexOfClickedTab) {

                    case 2: // 친구의 먹자GO 탭이 선택된 경우
                        try {
                            dbmgr = new DBManager(getApplicationContext()); //db 불러와서
                            sdb = dbmgr.getReadableDatabase(); // 데이터 읽어들임
                            cursor = sdb.rawQuery("SELECT * FROM eatgo WHERE id NOT IN ('" + id + "');", null); // eatgo에서 전체 데이터를 찾습니다. 조건은 id가 현재 유저의 id와 같은 것을 제외.
                            //db.rawQuery("SELECT 쿼리할 컬럼명1, 쿼리할 컬럼명2 FROM 테이블명", null)

                            String friendsID;
                            friendsList.clear(); // friendslist를 초기화 시킴.

                            while(cursor.moveToNext()) { // 커서가 다음 항목으로 움직일 수 있을 때 까지 반복
                                friendsID = cursor.getString(0); // 커서의 0번째 항목에 해당하는 문자를 friendsID에 넣고
                                friendsList.add(friendsID); // friendsList에 추가
                            }

                            friendsAdaptor = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, friendsList);
                            friendsListView.setAdapter(friendsAdaptor); // adapter는 listview와 실 데이터의 중간 역할 하는 추상 인터페이스

                        } catch (SQLiteException e) {
                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show(); // 예외 발생 시 어디서 예외 발생했는지 알 수 있게 해주는 토스트 메시지
                        } finally { // finally는 해당 스택이 종료되어도 마지막에 호출 됨.
                            cursor.close();
                            dbmgr.close(); // 메모리를 아끼기 위해 커서와 dbmgr을 닫아줍시다.
                        }
                        break;

                    case 3: // 나의 먹자GO 탭이 선택된 경우
                        try {
                            dbmgr = new DBManager(getApplicationContext()); // db 불러와서
                            sdb = dbmgr.getWritableDatabase(); // 데이터 읽어들임
                            cursor = sdb.rawQuery("SELECT * FROM eatgo WHERE id='" + id + "';", null); // eatgo에서 전체 데이터를 찾습니다. 조건은 현재 유저의 id와 같은 데이터만!
                            cursor.moveToNext(); // 커서를 이동시킵니다.

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
                        break;
                }
                indexOfSelectedTab = indexOfClickedTab;
            }

        }

    }
}