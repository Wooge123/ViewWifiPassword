package com.yukang.viewwifipassword;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private WifiManager wifiManager;
    private WifiInfo wifiInfo;
    private List<WifiInfo> wifiInfos;
    private ListView lvWifiInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wifiManager = new WifiManager();
        try {
            Init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //创建首选项菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //首选项菜单选择事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.help) {
            new AlertDialog.Builder(this).setTitle("使用说明").setMessage(R.string.tell_user).setNegativeButton("了解", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            }).show();
            return true;
        }
        return false;
    }

    //初始化
    public void Init() throws Exception {
        wifiInfos = wifiManager.Read();
        lvWifiInfo = (ListView) findViewById(R.id.lvWifiInfo);
        WifiAdapter adapter = new WifiAdapter(wifiInfos, this);
        lvWifiInfo.setAdapter(adapter);
        lvWifiInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                wifiInfo = wifiInfos.get(position);
                lvWifiInfo.showContextMenu();
            }
        });
        lvWifiInfo.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(0, 0, 0, "复制热点");
                menu.add(0, 1, 0, "复制密码");
                menu.add(0, 2, 0, "分享给好友");
            }
        });
    }

    //上下文菜单选择事件
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        String ssid = wifiInfo.Ssid;
        String password = wifiInfo.Password;
        switch (item.getItemId()) {
            //复制热点
            case 0:
                cm.setText(ssid);
                Toast.makeText(this, "复制热点成功", Toast.LENGTH_SHORT).show();
                break;
            //复制密码
            case 1:
                cm.setText(password);
                Toast.makeText(this, "复制密码成功", Toast.LENGTH_SHORT).show();
                break;
            //分享给好友
            case 2:
                String tell_others = "热点:" + ssid + "\n" + "密码:" + password;
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
                intent.putExtra(Intent.EXTRA_TEXT, tell_others);
                intent.setType("text/plain");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(Intent.createChooser(intent, "分享方式"));
                break;
        }
        return super.onContextItemSelected(item);
    }

    public class WifiAdapter extends BaseAdapter {

        List<WifiInfo> wifiInfos = null;
        Context context;

        public WifiAdapter(List<WifiInfo> wifiInfos, Context con) {
            this.wifiInfos = wifiInfos;
            this.context = con;
        }

        @Override
        public int getCount() {
            return wifiInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return wifiInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_wifi, null);
            TextView tvSsid = (TextView) convertView.findViewById(R.id.tvSsid);
            TextView tvPasswd = (TextView) convertView.findViewById(R.id.tvPasswd);
            tvSsid.setText("热点 " + wifiInfos.get(position).Ssid);
            tvPasswd.setText("密码 " + wifiInfos.get(position).Password);
            return convertView;
        }

    }

}