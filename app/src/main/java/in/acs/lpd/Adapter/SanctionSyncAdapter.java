package in.acs.lpd.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import in.acs.lpd.R;

/**
 * Created by Abhishek on 12/4/2018.
 */

public class SanctionSyncAdapter extends BaseAdapter {
    Context context;
    int inflater_sanctioned_sync;
    JsonArray sanctionedArray;
    Holder holder;

    public SanctionSyncAdapter(Context context, int inflater_sanctioned_sync, JsonArray sanctionedArray) {
        this.context = context;
        this.inflater_sanctioned_sync = inflater_sanctioned_sync;
        this.sanctionedArray = sanctionedArray;
    }

    @Override
    public int getCount() {
        return sanctionedArray.size();
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
            row = inflater.inflate(inflater_sanctioned_sync, viewGroup, false);
            holder = new Holder();
            holder.lec_ly_inf_sync = row.findViewById(R.id.lec_ly_inf_sync);
            holder.coc_ly_inf_sync = row.findViewById(R.id.coc_ly_inf_sync);

            holder.sanction_sync_inf = row.findViewById(R.id.sanction_sync_inf);

            holder.lec_tv_inf_sync = row.findViewById(R.id.lec_tv_inf_sync);
            holder.coc_tv_inf_sync = row.findViewById(R.id.coc_tv_inf_sync);

            holder.tenant_farmer_name_tv_inf_sync = row.findViewById(R.id.tenant_farmer_name_tv_inf_sync);
            holder.aadhaar_inf_sync = row.findViewById(R.id.aadhaar_inf_sync);
            holder.survey_no_inf_sync = row.findViewById(R.id.survey_no_inf_sync);
            holder.land_extent_inf_sync = row.findViewById(R.id.land_extent_inf_sync);

            row.setTag(holder);
        } else {
            holder = (Holder) row.getTag();
        }

        if ((i % 2) == 0) {
            holder.sanction_sync_inf.setBackgroundResource(R.color.divider);
        } else {
            holder.sanction_sync_inf.setBackgroundResource(R.color.white);
        }

        JsonObject obj = sanctionedArray.get(i).getAsJsonObject();

        if (obj.get("sanctionedFrom").getAsString().equalsIgnoreCase("LEC")) {
            holder.lec_ly_inf_sync.setVisibility(View.VISIBLE);
            holder.coc_ly_inf_sync.setVisibility(View.GONE);

            holder.lec_tv_inf_sync.setText("" + obj.get("cardNumber").getAsString());
        } else if (obj.get("sanctionedFrom").getAsString().equalsIgnoreCase("COC")) {
            holder.lec_ly_inf_sync.setVisibility(View.GONE);
            holder.coc_ly_inf_sync.setVisibility(View.VISIBLE);

            holder.coc_tv_inf_sync.setText("" + obj.get("cocApplicationId").getAsString());
        } else if (obj.get("sanctionedFrom").getAsString().equalsIgnoreCase("BOTH")) {
            holder.lec_ly_inf_sync.setVisibility(View.VISIBLE);
            holder.coc_ly_inf_sync.setVisibility(View.VISIBLE);

            holder.lec_tv_inf_sync.setText("" + obj.get("cardNumber").getAsString());
            holder.coc_tv_inf_sync.setText("" + obj.get("cocApplicationId").getAsString());
        }

        holder.tenant_farmer_name_tv_inf_sync.setText("" + obj.get("tenantFarmerName").getAsString());
        holder.aadhaar_inf_sync.setText("" + obj.get("tenantAadhaarNumber").getAsString());
        holder.survey_no_inf_sync.setText("" + obj.get("surveyNumber").getAsString());
        holder.land_extent_inf_sync.setText("" + obj.get("landExtent").getAsString());

        notifyDataSetChanged();
        return row;
    }

    public class Holder {
        LinearLayout lec_ly_inf_sync, coc_ly_inf_sync, sanction_sync_inf;
        TextView lec_tv_inf_sync, coc_tv_inf_sync, tenant_farmer_name_tv_inf_sync,
                aadhaar_inf_sync, survey_no_inf_sync, land_extent_inf_sync;
    }
}
