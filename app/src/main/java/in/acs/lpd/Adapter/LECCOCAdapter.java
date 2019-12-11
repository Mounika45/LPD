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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import in.acs.lpd.R;
import in.acs.lpd.leccoc.LECCOCHome;
import in.acs.lpd.leccoc.Reject;
import in.acs.lpd.leccoc.Sanction;

/**
 * Created by Abhishek on 11/19/2018.
 */

public class LECCOCAdapter extends BaseAdapter implements Filterable {
    Context context;
    int inflater_leccoc;
    JsonArray masterArray, newArray;
    Holder holder;
    String from;
    SwipeMenuListView leccoc_lv;

    public LECCOCAdapter(Context context, int inflater_leccoc, JsonArray masterArray, String from, SwipeMenuListView leccoc_lv) {
        this.context = context;
        this.inflater_leccoc = inflater_leccoc;
        this.masterArray = masterArray;
        this.newArray = masterArray;
        this.from = from;
        this.leccoc_lv = leccoc_lv;
    }

    @Override
    public int getCount() {
        return masterArray.size();
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
            row = inflater.inflate(inflater_leccoc, viewGroup, false);
            holder = new Holder();
            holder.lec_ly_inf = row.findViewById(R.id.lec_ly_inf);
            holder.coc_ly_inf = row.findViewById(R.id.coc_ly_inf);
            holder.lec_tv_inf = row.findViewById(R.id.lec_tv_inf);
            holder.coc_tv_inf = row.findViewById(R.id.coc_tv_inf);
            holder.tenant_farmer_name_tv_inf = row.findViewById(R.id.tenant_farmer_name_tv_inf);
            holder.aadhaar_inf = row.findViewById(R.id.aadhaar_inf);
            holder.survey_no_inf = row.findViewById(R.id.survey_no_inf);
            holder.land_extent_inf = row.findViewById(R.id.land_extent_inf);
            holder.leccoc_ly_inf = row.findViewById(R.id.leccoc_ly_inf);

            row.setTag(holder);
        } else {
            holder = (Holder) row.getTag();
        }

        if ((i % 2) == 0) {
            holder.leccoc_ly_inf.setBackgroundResource(R.color.divider);
        } else {
            holder.leccoc_ly_inf.setBackgroundResource(R.color.white);
        }

        JsonObject obj = masterArray.get(i).getAsJsonObject();

        if (from.equalsIgnoreCase("LEC")) {
            holder.lec_ly_inf.setVisibility(View.VISIBLE);
            holder.coc_ly_inf.setVisibility(View.GONE);

            holder.lec_tv_inf.setText("" + obj.get("cardNumber").getAsString());
        } else if (from.equalsIgnoreCase("COC")) {
            holder.lec_ly_inf.setVisibility(View.GONE);
            holder.coc_ly_inf.setVisibility(View.VISIBLE);

            holder.coc_tv_inf.setText("" + obj.get("cardNumber").getAsString());
        } else if (from.equalsIgnoreCase("BOTH")) {
            holder.lec_ly_inf.setVisibility(View.VISIBLE);
            holder.coc_ly_inf.setVisibility(View.VISIBLE);

            holder.lec_tv_inf.setText("" + obj.get("cardNumber").getAsString());
            holder.coc_tv_inf.setText("" + obj.get("cocCardNumber").getAsString());
        }

        holder.tenant_farmer_name_tv_inf.setText("" + obj.get("applicantName").getAsString());
        holder.aadhaar_inf.setText("" + obj.get("aadharNumber").getAsString());
        holder.survey_no_inf.setText("" + obj.get("surveyNumber").getAsString());
        holder.land_extent_inf.setText("" + obj.get("totalGroupLandExtent").getAsString());

        leccoc_lv.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
        leccoc_lv.setMenuCreator(new SwipeMenuCreator() {
            @SuppressLint("NewApi")
            @Override
            public void create(SwipeMenu menu) {
                //EDIT
                SwipeMenuItem editItem = new SwipeMenuItem(context);
                // set item background
                //editItem.setBackground(new ColorDrawable(context.getResources().getColor(R.color.colorAccent)));
                // set item width
                editItem.setWidth(150);
                // set a icon
                editItem.setIcon(R.drawable.approve);
                // set item title
                editItem.setTitle("Sanction");
                // set item title fontsize
                editItem.setTitleSize(18);
                // set item title font color
                editItem.setTitleColor(Color.GREEN);
                // add to menu
                menu.addMenuItem(editItem);

                //EDIT
                SwipeMenuItem rejectItem = new SwipeMenuItem(context);
                // set item background
                //rejectItem.setBackground(new ColorDrawable(context.getResources().getColor(R.color.colorPrimaryDark)));
                // set item width
                rejectItem.setWidth(150);
                // set a icon
                rejectItem.setIcon(R.drawable.reject);
                // set item title
                rejectItem.setTitle("Reject");
                // set item title fontsize
                rejectItem.setTitleSize(18);
                // set item title font color
                rejectItem.setTitleColor(Color.RED);
                // add to menu
                menu.addMenuItem(rejectItem);
            }
        });

        // step 2. listener item click event
        leccoc_lv.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                if (index == 0) {
                    JsonObject jso = masterArray.get(position).getAsJsonObject();
                    Intent i = new Intent(context, Sanction.class);
                    i.putExtra("from", from);
                    i.putExtra("SanctionObj", "" + jso);
                    context.startActivity(i);
                } else {
                    JsonObject jso = masterArray.get(position).getAsJsonObject();
                    Intent i = new Intent(context, Reject.class);
                    i.putExtra("from", from);
                    i.putExtra("RejectObj", "" + jso);
                    context.startActivity(i);
                }
                return false;
            }
        });

        notifyDataSetChanged();

        return row;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                JsonArray filteredList = new JsonArray();
                FilterResults filterResults = new FilterResults();
                Log.e("charStringgg", "" + charSequence);

                if (charString.isEmpty()) {
                    masterArray = newArray;
                } else {
                    for (JsonElement item : newArray) {
                        if (item.getAsJsonObject().get("aadharNumber").getAsString().toLowerCase().contains(charSequence)) {
                            Log.e("itemssssss", "" + item);
                            filteredList.add(item);
                        }
                    }
                    masterArray = filteredList;
                }
                filterResults.values = masterArray;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                //Toast.makeText(context, "In publsih", Toast.LENGTH_SHORT).show();
                masterArray = (JsonArray) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class Holder {
        LinearLayout lec_ly_inf, coc_ly_inf, leccoc_ly_inf;
        TextView lec_tv_inf, coc_tv_inf, tenant_farmer_name_tv_inf,
                aadhaar_inf, survey_no_inf, land_extent_inf;
    }
}
