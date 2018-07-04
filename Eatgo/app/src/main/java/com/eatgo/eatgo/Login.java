package com.eatgo.eatgo;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class Login extends AppCompatActivity {

    private DBManager dbmgr;
    private Cursor cursor;

    private ImageView reset;
    private EditText editTextID;
    private EditText editTextPW;

    private Button login;
    private Button join;
    private Button forgotten;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        reset = (ImageView) findViewById(R.id.reset);
        editTextID = (EditText) findViewById(R.id.editTextID);
        editTextPW = (EditText) findViewById(R.id.editTextPW);

        login = (Button) findViewById(R.id.login);
        join = (Button) findViewById(R.id.join);
        forgotten = (Button) findViewById(R.id.forgotten);

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getApplicationContext().deleteDatabase("eatgoDB");
                Toast.makeText(getApplicationContext(), "데이터베이스를 초기화 했습니다!", Toast.LENGTH_SHORT).show();
            }
        });

        editTextID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                enableButton(login);
                enableButton(join);
            }
        });

        editTextPW.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                enableButton(login);
                enableButton(join);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String id = editTextID.getText().toString();
                String pw = editTextPW.getText().toString();

                try {
                    dbmgr = new DBManager(getApplicationContext());
                    SQLiteDatabase sdb;
                    sdb = dbmgr.getReadableDatabase();
                    cursor = sdb.rawQuery("SELECT pw FROM eatgo WHERE id='" + id + "';", null);

                    if(cursor.getCount() == 0) Toast.makeText(getApplicationContext(), "존재하지 않는 ID입니다!", Toast.LENGTH_SHORT).show();
                    else {
                        cursor.moveToNext();
                        if(!cursor.getString(0).equals(pw)) {
                            Toast.makeText(getApplicationContext(), "비밀번호를 잘못 입력하였습니다!", Toast.LENGTH_SHORT).show();
                            editTextPW.setText("");
                        } else {
                            Intent intent = new Intent(getApplicationContext(),EatGo.class);
                            intent.putExtra("id", id);
                            editTextID.setText("");
                            editTextPW.setText("");
                            startActivity(intent);
                        }
                    }
                } catch (SQLiteException e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                } finally {
                    cursor.close();
                    dbmgr.close();
                }

            }
        });

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = editTextID.getText().toString();
                String pw = editTextPW.getText().toString();

                try {
                    dbmgr = new DBManager(getApplicationContext());
                    SQLiteDatabase sdb;
                    sdb = dbmgr.getWritableDatabase();
                    cursor = sdb.rawQuery("SELECT pw FROM eatgo WHERE id='" + id + "';", null);

                    if(cursor.getCount() != 0) Toast.makeText(getApplicationContext(), "이미 존재하는 ID입니다!", Toast.LENGTH_SHORT).show();
                    else {
                        sdb.execSQL("INSERT INTO eatgo (id, pw, kor, jap, chn, wes, etc, kore, jape, chne, wese, etce) VALUES ('"+id+"', '"+pw+"', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');");
                        Toast.makeText(getApplicationContext(), id + "님의 회원가입이 완료되었습니다!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(),EatGo.class);
                        intent.putExtra("id", id);
                        editTextID.setText("");
                        editTextPW.setText("");
                        startActivity(intent);
                    }
                } catch (SQLiteException e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                } finally {
                    cursor.close();
                    dbmgr.close();
                }
            }
        });

        forgotten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String id = editTextID.getText().toString();

                try {
                    dbmgr = new DBManager(getApplicationContext());
                    SQLiteDatabase sdb;
                    sdb = dbmgr.getReadableDatabase();
                    cursor = sdb.rawQuery("SELECT pw FROM eatgo WHERE id='" + id + "';", null);

                    if(cursor.getCount() == 0) Toast.makeText(getApplicationContext(), "존재하지 않는 ID입니다!", Toast.LENGTH_SHORT).show();
                    else {
                        cursor.moveToNext();
                        String pw = cursor.getString(0);
                        Toast.makeText(getApplicationContext(), id + "님의 비밀번호는 '" + pw + "'입니다!", Toast.LENGTH_SHORT).show();
                        editTextPW.setText(pw);
                    }
                } catch (SQLiteException e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                } finally {
                    cursor.close();
                    dbmgr.close();
                }

            }
        });

    }

    protected void enableButton(Button button) {
        if((!editTextID.getText().toString().isEmpty() && !editTextPW.getText().toString().isEmpty()) && !button.isEnabled()) {
            button.setEnabled(true);
            button.setTextColor(Color.parseColor("#404040"));
            button.setPaintFlags(button.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
        } else if((editTextID.getText().toString().isEmpty() || editTextPW.getText().toString().isEmpty()) && button.isEnabled()) {
            button.setEnabled(false);
            button.setTextColor(Color.WHITE);
            button.setPaintFlags(button.getPaintFlags() & ~ Paint.FAKE_BOLD_TEXT_FLAG);
        }
    }
}
