package in.acs.lpd.leccoc;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.gson.JsonArray;

import java.io.IOException;

import in.acs.lpd.Adapter.GSMembersAdapter;
import in.acs.lpd.R;
import in.acs.lpd.database.DBAdapter;

public class GroupSanctionHome extends AppCompatActivity {
    SwipeMenuListView gs_members_lv;
    DBAdapter db;
    JsonArray gsMembers_array;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_sanction_home);

        try {
            db = new DBAdapter(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        gs_members_lv = findViewById(R.id.gs_members_lv);
        gsMembers_array = db.getGSMembersData(db.getUserDtls().get("mandalId").getAsString(), db.getUserDtls().get("userId").getAsString());
        Log.e("gsMembers_array" , ""+ gsMembers_array);
        setAdapter();
    }

    private void setAdapter() {
        GSMembersAdapter adapter = new GSMembersAdapter(GroupSanctionHome.this, R.layout.inflater_gsmember, gsMembers_array, gs_members_lv);
        gs_members_lv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
