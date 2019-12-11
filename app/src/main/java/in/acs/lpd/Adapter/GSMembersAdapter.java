package in.acs.lpd.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import in.acs.lpd.R;
import in.acs.lpd.leccoc.GroupSanction;
import in.acs.lpd.leccoc.GroupSanctionHome;
import in.acs.lpd.leccoc.Reject;
import in.acs.lpd.leccoc.Sanction;

/**
 * Created by Abhishek on 12/20/2018.
 */

public class GSMembersAdapter extends BaseAdapter {
    Context context;
    int inflater_gsmember;
    JsonArray gsMembers_array;
    SwipeMenuListView gs_members_lv;
    Holder holder;

    public GSMembersAdapter(Context context, int inflater_gsmember, JsonArray gsMembers_array, SwipeMenuListView gs_members_lv) {
        this.context = context;
        this.inflater_gsmember = inflater_gsmember;
        this.gsMembers_array = gsMembers_array;
        this.gs_members_lv = gs_members_lv;
    }

    @Override
    public int getCount() {
        return gsMembers_array.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View row = view;
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(inflater_gsmember, viewGroup, false);
            holder = new Holder();
            holder.gsmember_ly_inf = row.findViewById(R.id.gsmember_ly_inf);
            holder.groupname_gsm_inf = row.findViewById(R.id.groupname_gsm_inf);
            holder.groupheadname_gsm_inf = row.findViewById(R.id.groupheadname_gsm_inf);
            holder.groupheadmobileno_gsm_inf = row.findViewById(R.id.groupheadmobileno_gsm_inf);
            holder.nofarmers_gsm_inf = row.findViewById(R.id.nofarmers_gsm_inf);
            holder.landextent_gsm_inf = row.findViewById(R.id.landextent_gsm_inf);

            row.setTag(holder);
        } else {
            holder = (Holder) row.getTag();
        }

        if ((i % 2) == 0) {
            holder.gsmember_ly_inf.setBackgroundResource(R.color.divider);
        } else {
            holder.gsmember_ly_inf.setBackgroundResource(R.color.white);
        }

        JsonObject obj = gsMembers_array.get(i).getAsJsonObject();

        holder.groupname_gsm_inf.setText("" + obj.get("groupName").getAsString());
        holder.groupname_gsm_inf.setTag("" + obj.get("groupId").getAsString());
        holder.groupheadname_gsm_inf.setText("" + obj.get("groupHeadName").getAsString());
        holder.groupheadmobileno_gsm_inf.setText("" + obj.get("groupHeadMobileNo").getAsString());
        holder.nofarmers_gsm_inf.setText("" + obj.get("groupMembersCount").getAsString());
        holder.landextent_gsm_inf.setText("" + obj.get("totalGroupLandExtent").getAsString());

        gs_members_lv.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
        gs_members_lv.setMenuCreator(new SwipeMenuCreator() {
            @SuppressLint("NewApi")
            @Override
            public void create(SwipeMenu menu) {
                //EDIT
                SwipeMenuItem editItem = new SwipeMenuItem(context);
                // set item background
                //editItem.setBackground(new ColorDrawable(context.getResources().getColor(R.color.colorPrimary)));
                // set item width
                editItem.setWidth(150);
                // set a icon
                editItem.setIcon(R.drawable.view);
                // set item title
                editItem.setTitle("View Data");
                // set item title fontsize
                editItem.setTitleSize(18);
                // set item title font color
                editItem.setTitleColor(context.getResources().getColor(R.color.colorPrimaryDark));
                // add to menu
                menu.addMenuItem(editItem);
            }
        });

        // step 2. listener item click event
        gs_members_lv.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                JsonObject jso = gsMembers_array.get(position).getAsJsonObject();
                Log.e("gsMemberObj", "" + jso);
                Intent i = new Intent(context, GroupSanction.class);
                i.putExtra("gsMemberObj", "" + jso);
                context.startActivity(i);
                return false;
            }
        });

        notifyDataSetChanged();
        return row;
    }

    public class Holder {
        LinearLayout gsmember_ly_inf;
        TextView groupname_gsm_inf, groupheadname_gsm_inf,
                groupheadmobileno_gsm_inf, nofarmers_gsm_inf, landextent_gsm_inf;
    }
}
