package com.matevz.zapisnik;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int PERMISSION_REQUEST_READ_CALL_LOG_STATE = 0;
    private ListView callLogListView;
    private LogsDataSource datasource;
    private CallLogAdapter callLogAdapter;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        callLogListView = (ListView) findViewById(R.id.callLogListView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_PHONE_STATE}, PERMISSION_REQUEST_READ_CALL_LOG_STATE);
                return;
            } else {
                setList();
            }
        } else {
            setList();
        }

        callLogListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final Log log = (Log) callLogAdapter.getItem(position);

                new AlertDialog.Builder(MainActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("DELETE COMMENT")
                        .setMessage("Are you sure you wish to delete your comment?")
                        .setNegativeButton("Dismiss", null)
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                datasource.deleteLog(log);
                                setList();
                                Toast.makeText(MainActivity.this, "Comment deleted", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();
                return true;
            }
        });

        callLogListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Dialog dialog = new Dialog(MainActivity.this);
                final Log log = (Log) callLogAdapter.getItem(position);
                dialog.setContentView(R.layout.dialog);
                TextView cancel = (TextView) dialog.findViewById(R.id.cancel);
                TextView save = (TextView) dialog.findViewById(R.id.save);

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                final EditText comment = (EditText) dialog.findViewById(R.id.comment);
                comment.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                        }
                    }
                });

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        datasource.updateLog(log, comment.getText().toString());
                        setList();
                        dialog.dismiss();
                    }
                });

                if (log.getComment().length() > 0)
                    comment.setText(log.getComment());

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialog.show();
                dialog.getWindow().setAttributes(lp);
            }
        });
    }

    public void setList() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED)
            return;

        Cursor cursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DATE + " DESC");
        cursor.moveToFirst();

        datasource = new LogsDataSource(this);
        datasource.open();
        do {
            String id = cursor.getString(cursor.getColumnIndex(CallLog.Calls._ID));
            String callType = cursor.getString(cursor.getColumnIndex(CallLog.Calls.TYPE));
            String name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
            String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_FORMATTED_NUMBER));
            String date = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DATE));
            if (number == null)
                number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
            if (name == null)
                name = "";

            try {
                datasource.createLog(id, callType, name, number, date, "");
            } catch (Exception e) {
                break;
            }
        } while (cursor.moveToNext());

        cursor.close();

        List<Log> values = datasource.getAllLogs();

        callLogAdapter = new CallLogAdapter(MainActivity.this, (ArrayList<Log>) values);
        callLogListView.setAdapter(callLogAdapter);
    }

    public void refresh(MenuItem item) {
        setList();
    }

    @Override
    protected void onResume() {
        datasource.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        datasource.close();
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_READ_CALL_LOG_STATE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setList();
            }
        }
    }
}
