package in.acs.lpd.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;

import in.acs.lpd.R;
import in.acs.lpd.Services.utils;
import in.acs.lpd.database.DBAdapter;

public class GSFarmerAdapter extends RecyclerView.Adapter<GSFarmerAdapter.MyViewHolder> {
    Context context;
    int inflater_gsfarmer;
    JsonArray gs_farmersArray;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView gs_farmers_lv;
    DBAdapter db;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tenantfarmername_gsf_inf, aadhaar_gsf_inf, surveyno_gsf_inf, landextent_gsf_inf, apptype_gsf_inf, crop_gsf_inf;
        EditText amount_gsf_inf;

        public MyViewHolder(View row) {
            super(row);
            tenantfarmername_gsf_inf = row.findViewById(R.id.tenantfarmername_gsf_inf);
            aadhaar_gsf_inf = row.findViewById(R.id.aadhaar_gsf_inf);
            surveyno_gsf_inf = row.findViewById(R.id.surveyno_gsf_inf);
            landextent_gsf_inf = row.findViewById(R.id.landextent_gsf_inf);
            apptype_gsf_inf = row.findViewById(R.id.apptype_gsf_inf);

            crop_gsf_inf = row.findViewById(R.id.crop_gsf_inf);
            amount_gsf_inf = row.findViewById(R.id.amount_gsf_inf);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inflater_gsfarmer, parent, false);
        return new MyViewHolder(itemView);
    }

    public GSFarmerAdapter(Context context, int inflater_gsfarmer, JsonArray gs_farmersArray, RecyclerView.LayoutManager mLayoutManager, RecyclerView gs_farmers_lv) {
        this.context = context;
        this.inflater_gsfarmer = inflater_gsfarmer;
        this.gs_farmersArray = gs_farmersArray;
        this.mLayoutManager = mLayoutManager;
        this.gs_farmers_lv = gs_farmers_lv;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        try {
            db = new DBAdapter(context);
        } catch (IOException e) {
            e.printStackTrace();
        }

        holder.crop_gsf_inf.setTag(position);
        holder.apptype_gsf_inf.setTag(position);

        final JsonObject jso = gs_farmersArray.get(position).getAsJsonObject();
        holder.tenantfarmername_gsf_inf.setText("" + jso.get("applicantName").getAsString());
        holder.aadhaar_gsf_inf.setText("" + jso.get("aadharNumber").getAsString());
        holder.surveyno_gsf_inf.setText("" + jso.get("surveyNumber").getAsString());
        holder.landextent_gsf_inf.setText("" + jso.get("landExtent").getAsString());

        holder.apptype_gsf_inf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
                builderSingle.setIcon(R.drawable.lec);
                builderSingle.setTitle("Select");

                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.select_dialog_singlechoice);
                arrayAdapter.add("New");
                arrayAdapter.add("Renewal");

                builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = arrayAdapter.getItem(which);
                        holder.apptype_gsf_inf.setText("" + strName);
                    }
                });
                builderSingle.show();
            }
        });

        holder.crop_gsf_inf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final JsonArray cropArray = db.getCropDtlsGS();
                if (cropArray.size() > 0) {
                    android.app.AlertDialog selectionAlert = null;

                    final CharSequence[] nameItem = new String[cropArray.size()];
                    final CharSequence[] idItem = new String[cropArray.size()];
                    for (int i = 0; i < cropArray.size(); i++) {
                        JsonObject jso = (JsonObject) cropArray.get(i);
                        nameItem[i] = jso.get("cropName").getAsString();
                        idItem[i] = jso.get("cropNameId").getAsString();
                    }

                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
                    builder.setTitle("Select Crop");
                    builder.setIcon(R.drawable.crop);
                    builder.setCancelable(false);
                    builder.setItems(nameItem, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, final int item) {
                            JsonObject jso = (JsonObject) cropArray.get(item);
                            holder.crop_gsf_inf.setText(jso.get("cropName").getAsString());
                            holder.crop_gsf_inf.setTag(jso.get("cropNameId").getAsInt());
                        }
                    });
                    if (selectionAlert == null) {
                        selectionAlert = builder.create();
                    }
                    selectionAlert.setCancelable(true);
                    if (!selectionAlert.isShowing()) {
                        selectionAlert.show();
                    }
                } else {
                    Snackbar snackbar = Snackbar.make(view, "Crops data not avaialable!", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            }
        });

        holder.apptype_gsf_inf.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!holder.apptype_gsf_inf.getText().toString().equalsIgnoreCase("Select") &&
                        !holder.crop_gsf_inf.getText().toString().equalsIgnoreCase("Select") &&
                        !holder.amount_gsf_inf.getText().toString().equalsIgnoreCase("")) {
                    //JsonObject json = new JsonObject();
                    jso.addProperty("app_type", "" + holder.apptype_gsf_inf.getText().toString());
                    jso.addProperty("crop", "" + holder.crop_gsf_inf.getTag().toString());
                    jso.addProperty("amount", "" + holder.amount_gsf_inf.getText().toString());
                    utils.saveGSData.put(jso.get("id").getAsString(), jso);
                    Log.e("utils.saveGSData", "" + utils.saveGSData);
                }
            }
        });

        holder.amount_gsf_inf.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!holder.apptype_gsf_inf.getText().toString().equalsIgnoreCase("Select") &&
                        !holder.crop_gsf_inf.getText().toString().equalsIgnoreCase("Select") &&
                        !holder.amount_gsf_inf.getText().toString().equalsIgnoreCase("")) {
                    //JsonObject json = new JsonObject();
                    jso.addProperty("app_type", "" + holder.apptype_gsf_inf.getText().toString());
                    jso.addProperty("crop", "" + holder.crop_gsf_inf.getTag().toString());
                    jso.addProperty("amount", "" + holder.amount_gsf_inf.getText().toString());
                    utils.saveGSData.put(jso.get("id").getAsString(), jso);
                    Log.e("utils.saveGSData", "" + utils.saveGSData);
                }
            }
        });

        holder.crop_gsf_inf.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!holder.apptype_gsf_inf.getText().toString().equalsIgnoreCase("Select") &&
                        !holder.crop_gsf_inf.getText().toString().equalsIgnoreCase("Select") &&
                        !holder.amount_gsf_inf.getText().toString().equalsIgnoreCase("")) {
                    //JsonObject json = new JsonObject();
                    jso.addProperty("app_type", "" + holder.apptype_gsf_inf.getText().toString());
                    jso.addProperty("crop", "" + holder.crop_gsf_inf.getTag().toString());
                    jso.addProperty("amount", "" + holder.amount_gsf_inf.getText().toString());
                    utils.saveGSData.put(jso.get("id").getAsString(), jso);
                    Log.e("utils.saveGSData", "" + utils.saveGSData);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return gs_farmersArray.size();
    }

    public int getItemViewType(int position) {
        return position;
    }
}