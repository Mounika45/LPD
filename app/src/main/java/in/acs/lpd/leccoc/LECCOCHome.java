package in.acs.lpd.leccoc;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SearchView;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.gson.JsonArray;

import java.io.IOException;

import in.acs.lpd.Adapter.LECCOCAdapter;
import in.acs.lpd.R;
import in.acs.lpd.database.DBAdapter;

public class LECCOCHome extends AppCompatActivity {
    JsonArray masterArray;
    DBAdapter db;
    String from;
    SwipeMenuListView leccoc_lv;
    TextView tv_leccoc;
    SearchView search_et_leccochome;
    LECCOCAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leccochome);

        try {
            db = new DBAdapter(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        from = getIntent().getExtras().get("from").toString();
        masterArray = db.getLECCOCMaster(from, db.getUserDtls().get("userId").getAsString());

        leccoc_lv = findViewById(R.id.leccoc_lv);
        tv_leccoc = findViewById(R.id.tv_leccoc);
        tv_leccoc.setText("\t\t\t Swipe Left to Sanction or Reject Application \t\t\t");
        tv_leccoc.setSelected(true);
        search_et_leccochome = findViewById(R.id.search_et_leccochome);

        setAdapter();

        search_et_leccochome.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //adapter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //adapter = new LECCOCAdapter(LECCOCHome.this, masterArray);
                adapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    private void setAdapter() {
        adapter = new LECCOCAdapter(LECCOCHome.this, R.layout.inflater_leccoc, masterArray, from, leccoc_lv);
        leccoc_lv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setAdapter();
    }
}
