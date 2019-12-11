package in.acs.lpd.leccoc;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import in.acs.lpd.Adapter.GSFarmerAdapter;
import in.acs.lpd.R;
import in.acs.lpd.RecyclerTouchListener;
import in.acs.lpd.Services.utils;
import in.acs.lpd.database.DBAdapter;

public class GroupSanction extends AppCompatActivity {
    JsonObject gsMemberObj;
    DBAdapter db;
    JsonArray gs_farmersArray;
    RecyclerView gs_farmers_lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_sanction);

        try {
            db = new DBAdapter(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        gs_farmers_lv = findViewById(R.id.gs_farmers_lv);

        JsonParser parser = new JsonParser();
        gsMemberObj = parser.parse(getIntent().getExtras().get("gsMemberObj").toString()).getAsJsonObject();
        gs_farmersArray = db.getGSFarmersData("GROUP SANCTION", db.getUserDtls().get("userId").getAsString(), gsMemberObj.get("groupId").getAsString());

        if (gs_farmersArray.size() > 0) {
            setAdapter();
        } else {
            Snackbar snackbar = Snackbar.make(getWindow().getDecorView().getRootView(), "Data not available!", Snackbar.LENGTH_LONG);
            snackbar.show();
            finish();
        }
    }

    private void setAdapter() {
        // vertical RecyclerView
        // keep movie_list_row.xml width to `match_parent`
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());

        GSFarmerAdapter adapter = new GSFarmerAdapter(GroupSanction.this, R.layout.inflater_gsfarmer, gs_farmersArray, mLayoutManager, gs_farmers_lv);
        //gs_farmers_lv.setAdapter(adapter);

        gs_farmers_lv.setHasFixedSize(true);

        // horizontal RecyclerView
        // keep movie_list_row.xml width to `wrap_content`
        // RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

        gs_farmers_lv.setLayoutManager(mLayoutManager);

        // adding inbuilt divider line
        gs_farmers_lv.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        // adding custom divider line with padding 16dp
        // recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.HORIZONTAL, 16));
        gs_farmers_lv.setItemAnimator(new DefaultItemAnimator());

        gs_farmers_lv.setAdapter(adapter);

        // row click listener
        gs_farmers_lv.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), gs_farmers_lv, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                /*String pos = gs_farmersArray.get(position).getAsString();
                Toast.makeText(getApplicationContext(), movie.getTitle() + " is selected!", Toast.LENGTH_SHORT).show();*/
                //  Toast.makeText(GroupSanction.this, "Touched", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        adapter.notifyDataSetChanged();
    }

    public void rejectGS(View view) {
        Intent i = new Intent(this, Reject.class);
        i.putExtra("from", "GROUP SANCTION");
        i.putExtra("gsArray", ""+gs_farmersArray);
        i.putExtra("gsObj", "" + gs_farmersArray.get(0).getAsJsonObject());
        startActivity(i);
    }

    public void sanctionGS(View view) {
        if (gs_farmersArray.size() == utils.saveGSData.size()) {
            if (utils.saveGSData.size() > 0) {
                Intent i = new Intent(this, SanctionGS.class);
                i.putExtra("from", "GROUP SANCTION");
                startActivity(i);
            }
        } else {
            Snackbar snackbar = Snackbar.make(view, "Please fill in all the Sanction details!", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }
}
