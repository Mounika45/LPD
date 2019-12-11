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
import in.acs.lpd.sync.SyncData;

/**
 * Created by Abhishek on 12/4/2018.
 */

public class RejectSyncAdapter extends BaseAdapter {
    Context context;
    int inflater_rejected_sync;
    JsonArray rejectedArray;
    Holder holder;

    public RejectSyncAdapter(Context context, int inflater_rejected_sync, JsonArray rejectedArray) {
        this.context = context;
        this.inflater_rejected_sync = inflater_rejected_sync;
        this.rejectedArray = rejectedArray;
    }

    @Override
    public int getCount() {
        return rejectedArray.size();
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
            row = inflater.inflate(inflater_rejected_sync, viewGroup, false);
            holder = new Holder();
            holder.lec_ly_inf_reject = row.findViewById(R.id.lec_ly_inf_reject);
            holder.coc_ly_inf_reject = row.findViewById(R.id.coc_ly_inf_reject);

            holder.sanction_reject_inf = row.findViewById(R.id.sanction_reject_inf);

            holder.lec_tv_inf_reject = row.findViewById(R.id.lec_tv_inf_reject);
            holder.coc_tv_inf_reject = row.findViewById(R.id.coc_tv_inf_reject);

            holder.tenant_farmer_name_tv_inf_reject = row.findViewById(R.id.tenant_farmer_name_tv_inf_reject);
            holder.aadhaar_inf_reject = row.findViewById(R.id.aadhaar_inf_reject);
            holder.survey_no_inf_reject = row.findViewById(R.id.survey_no_inf_reject);
            holder.land_extent_inf_reject = row.findViewById(R.id.land_extent_inf_reject);

            row.setTag(holder);
        } else {
            holder = (Holder) row.getTag();
        }

        if ((i % 2) == 0) {
            holder.sanction_reject_inf.setBackgroundResource(R.color.divider);
        } else {
            holder.sanction_reject_inf.setBackgroundResource(R.color.white);
        }

        JsonObject obj = rejectedArray.get(i).getAsJsonObject();

        if (obj.get("sanctionedFrom").getAsString().equalsIgnoreCase("LEC")) {
            holder.lec_ly_inf_reject.setVisibility(View.VISIBLE);
            holder.coc_ly_inf_reject.setVisibility(View.GONE);

            holder.lec_tv_inf_reject.setText("" + obj.get("cardNumber").getAsString());
        } else if (obj.get("sanctionedFrom").getAsString().equalsIgnoreCase("COC")) {
            holder.lec_ly_inf_reject.setVisibility(View.GONE);
            holder.coc_ly_inf_reject.setVisibility(View.VISIBLE);

            holder.coc_tv_inf_reject.setText("" + obj.get("cocApplicationId").getAsString());
        } else if (obj.get("sanctionedFrom").getAsString().equalsIgnoreCase("BOTH")) {
            holder.lec_ly_inf_reject.setVisibility(View.VISIBLE);
            holder.coc_ly_inf_reject.setVisibility(View.VISIBLE);

            holder.lec_tv_inf_reject.setText("" + obj.get("cardNumber").getAsString());
            holder.coc_tv_inf_reject.setText("" + obj.get("cocApplicationId").getAsString());
        }

        holder.tenant_farmer_name_tv_inf_reject.setText("" + obj.get("tenantFarmerName").getAsString());
        holder.aadhaar_inf_reject.setText("" + obj.get("tenantAadhaarNumber").getAsString());
        holder.survey_no_inf_reject.setText("" + obj.get("surveyNumber").getAsString());
        holder.land_extent_inf_reject.setText("" + obj.get("landExtent").getAsString());

        notifyDataSetChanged();
        return row;
    }

    public class Holder {
        LinearLayout lec_ly_inf_reject, coc_ly_inf_reject, sanction_reject_inf;
        TextView lec_tv_inf_reject, coc_tv_inf_reject, tenant_farmer_name_tv_inf_reject,
                aadhaar_inf_reject, survey_no_inf_reject, land_extent_inf_reject;
    }
}
