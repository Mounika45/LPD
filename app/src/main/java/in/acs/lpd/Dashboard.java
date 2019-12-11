package in.acs.lpd;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import in.acs.lpd.Services.URLs;
import in.acs.lpd.Services.utils;
import in.acs.lpd.database.DBAdapter;
import in.acs.lpd.leccoc.GroupSanctionHome;
import in.acs.lpd.leccoc.LECCOCHome;
import in.acs.lpd.sync.SyncData;
import okhttp3.internal.Util;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class Dashboard extends AppCompatActivity {
    String username;
    TextView username_db, village_tv, leccoc_tv;
    DBAdapter db;
    JsonObject json;
    utils util = new utils();
    //SwipeMenuListView lv_lec;
    private ProgressDialog progress;
    TextView logout, lec_view, coc_view, both_view, gropSanction_view, download_gs;
    LinearLayout groupSan_check_ly, groupSan_check_ly_child;
    CheckBox[] dynamicCheckBoxes;
    CheckBox checkBox;
    String roleid, role_value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        progress = new ProgressDialog(Dashboard.this);
        progress.setTitle("Contacting Server");
        progress.setMessage("Please Wait.......");
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setIndeterminate(true);
        progress.setCancelable(false);

        try {
            db = new DBAdapter(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        json = db.getUserDtls();
        username = json.get("userName").getAsString();

        roleid = json.get("userRoleId").getAsString();
        if (!roleid.equalsIgnoreCase("2")) {
            role_value = "/undefined";
        } else {
            role_value = "/" + utils.workForVillages;
        }
        //lv_lec = findViewById(R.id.lv_lec);
        username_db = findViewById(R.id.username_db);
        username_db.setText("\t \t \t \t \t \t \t \t \t \t WELCOME " + username + "\t \t \t \t \t \t \t \t \t \t");
        username_db.setSelected(true);
        village_tv = findViewById(R.id.village_tv);
        village_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectVillage();
            }
        });
        leccoc_tv = findViewById(R.id.leccoc_tv);
        leccoc_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectLECCOC();
            }
        });
        logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(Dashboard.this);
                alert.setTitle("Exit App");
                alert.setMessage("Are you sure, you want to logout?");
                alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.deleteUserDetails();
                        Intent i = new Intent(Dashboard.this, Login.class);
                        startActivity(i);
                        finish();
                    }
                });
                alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                alert.show();
            }
        });
        lec_view = findViewById(R.id.lec_view);
        coc_view = findViewById(R.id.coc_view);
        both_view = findViewById(R.id.both_view);
        gropSanction_view = findViewById(R.id.gropSanction_view);
        download_gs = findViewById(R.id.download_gs);
        groupSan_check_ly = findViewById(R.id.groupSan_check_ly);

        callGetVillageListService();
    }

    private void callServices() {
        try {
            if (!leccoc_tv.getText().toString().equalsIgnoreCase("Select")) {
                progress.show();
                callCropMasterService();
            } else {
                Snackbar snackbar = Snackbar.make(getWindow().getDecorView().getRootView(), "Please select all fields!!!", Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        } catch (Exception e) {
            progress.dismiss();
            e.printStackTrace();
        }
    }

    private void callBranchMasterService() {
        //  util.showProgressDialog(Dashboard.this);
        URLs ul = new URLs();
        /*final String url = "http://" + ul.ip + ":" + ul.port + "/" + ul.url + "/branch/getAllBranchDetailsByVillage/" +
                village_tv.getTag().toString();*/
        final String url = "http://" + ul.ip + ":" + ul.port + "/" + ul.url + "/branch/0/" + village_tv.getTag().toString() + "/nonPreferredBank";
        //http://103.211.39.43:8030/lec/rest/branch/4/726001/nonPreferredBank
        util.getBaseClassService(url, null).getBranches(new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                //progress.dismiss();
                if (response.getStatus() == 200) {
                    JsonArray branch = jsonObject.get("Branchs").getAsJsonObject().get("branch").getAsJsonArray();
                    if (branch.size() > 0) {
                        db.deleteBranchesBank(leccoc_tv.getText().toString());
                        for (int i = 0; i < branch.size(); i++) {
                            JsonObject obj = branch.get(i).getAsJsonObject();
                            try {
                                db.insertBranchesBank("" + obj.get("branchId").getAsString(),
                                        "" + obj.get("bankId").getAsString(),
                                        "" + obj.get("branchName").getAsString().replace("'", ""),
                                        "" + obj.get("ifscCode").getAsString().replace("'", ""),
                                        leccoc_tv.getText().toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().getRootView(), "Branch data not available!", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                } else {
                    Snackbar snackbar = Snackbar.make(getWindow().getDecorView().getRootView(), "Something went wrong, try again later!", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                progress.dismiss();
                Snackbar snackbar = Snackbar.make(getWindow().getDecorView().getRootView(), "Please check your internet connection!", Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        });

        callScaleOfFinanceService();
    }

    private void callScaleOfFinanceService() {
        final URLs ul = new URLs();
        final String url = "http://" + ul.ip + ":" + ul.port + "/" + ul.url + "/scaleFinanceController/getAllFinanceDetails";

        util.getBaseClassService(url, null).getScaleOfFinance(new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                //   util.dismissDialog();
                if (response.getStatus() == 200) {
                    JsonArray financeList = jsonObject.get("scaleOfFinanceList").getAsJsonObject().get("financeList").getAsJsonArray();
                    if (financeList.size() > 0) {
                        db.deleteSOFDtls(leccoc_tv.getText().toString());
                        for (int i = 0; i < financeList.size(); i++) {
                            JsonObject obj = financeList.get(i).getAsJsonObject();
                            try {
                                db.insertSOFDtls("" + obj.get("id").getAsString(),
                                        "" + obj.get("cropId").getAsString().replace("'", ""),
                                        "" + obj.get("finYearId").getAsString().replace("'", ""),
                                        "" + obj.get("amount").getAsString().replace("'", ""),
                                        "" + obj.get("type").getAsString().replace("'", ""),
                                        leccoc_tv.getText().toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().getRootView(), "Scale Of Finance data not available!", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                } else {
                    Snackbar snackbar = Snackbar.make(getWindow().getDecorView().getRootView(), "Something went wrong, try again later!", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                progress.dismiss();
                Snackbar snackbar = Snackbar.make(getWindow().getDecorView().getRootView(), "Please check your internet connection!", Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        });

        callRejectionReasonService();
    }

    private void callRejectionReasonService() {
        final URLs ul = new URLs();
        final String url = "http://" + ul.ip + ":" + ul.port + "/" + ul.url + "/loanRejectionController/getRejectionReasons";

        util.getBaseClassService(url, null).getRejectionReasons(new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                //   util.dismissDialog();
                if (response.getStatus() == 200) {
                    JsonArray rejectionDtls = jsonObject.get("loanRejectionDtls").getAsJsonObject().get("rejectionDtls").getAsJsonArray();
                    if (rejectionDtls.size() > 0) {
                        db.deleteRejectionReasons();
                        for (int i = 0; i < rejectionDtls.size(); i++) {
                            JsonObject obj = rejectionDtls.get(i).getAsJsonObject();
                            try {
                                db.insertRejectionReasons("" + obj.get("reasonId").getAsString(),
                                        "" + obj.get("reasonName").getAsString().replace("'", ""),
                                        "" + obj.get("reasonActive").getAsString().replace("'", ""),
                                        "" + obj.get("createdOn").getAsString().replace("'", ""));
                                progress.dismiss();
                            } catch (Exception e) {
                                progress.dismiss();
                                e.printStackTrace();
                            }
                        }
                    } else {
                        progress.dismiss();
                        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().getRootView(), "Scale Of Finance data not available!", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                } else {
                    progress.dismiss();
                    Snackbar snackbar = Snackbar.make(getWindow().getDecorView().getRootView(), "Something went wrong, try again later!", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                progress.dismiss();
                Snackbar snackbar = Snackbar.make(getWindow().getDecorView().getRootView(), "Please check your internet connection!", Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        });
    }

    private void callBankMasterService() {
        //util.showProgressDialog(Dashboard.this);
        final URLs ul = new URLs();
        final String url = "http://" + ul.ip + ":" + ul.port + "/" + ul.url + "/bank/getBanks/" + village_tv.getTag().toString() + "/nonPreferredBank";

        util.getBaseClassService(url, null).getBanks(new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                //   util.dismissDialog();
                if (response.getStatus() == 200) {
                    JsonArray banks = jsonObject.get("banks").getAsJsonObject().get("banks").getAsJsonArray();
                    if (banks.size() > 0) {
                        db.deleteBankDtls(leccoc_tv.getText().toString());
                        for (int i = 0; i < banks.size(); i++) {
                            JsonObject obj = banks.get(i).getAsJsonObject();
                            db.insertBankDtls("" + obj.get("bankId").getAsString(),
                                    "" + obj.get("bankName").getAsString().replace("'", ""),
                                    village_tv.getTag().toString(),
                                    leccoc_tv.getText().toString());
                        }
                    } else {
                        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().getRootView(), "Banks data not available!", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                } else {
                    Snackbar snackbar = Snackbar.make(getWindow().getDecorView().getRootView(), "Something went wrong, try again later!", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                progress.dismiss();
                Snackbar snackbar = Snackbar.make(getWindow().getDecorView().getRootView(), "Please check your internet connection!", Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        });

        callBranchMasterService();
    }

    private void callCropMasterService() {
        // util.showProgressDialog(Dashboard.this);
        URLs ul = new URLs();
        final String url = "http://" + ul.ip + ":" + ul.port + "/" + ul.url + "/cropNames";

        util.getBaseClassService(url, null).getCrops(new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                //  util.dismissDialog();
                if (response.getStatus() == 200) {
                    JsonArray cropNames = jsonObject.get("cropNames").getAsJsonObject().get("cropNames").getAsJsonArray();
                    if (cropNames.size() > 0) {
                        //      util.dismissDialog();
                        db.deleteCropDtls(leccoc_tv.getText().toString());
                        for (int i = 0; i < cropNames.size(); i++) {
                            JsonObject obj = cropNames.get(i).getAsJsonObject();
                            db.insertCropDtls("" + obj.get("cropNameId").getAsString(),
                                    "" + obj.get("cropName").getAsString().replace("'", ""),
                                    "" + obj.get("cropStatus").getAsString().replace("'", ""),
                                    "" + leccoc_tv.getText().toString());
                            //        util.dismissDialog();
                        }
                    } else {
                        //     util.dismissDialog();
                        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().getRootView(), "Crop data not available!", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                } else {
                    //   util.dismissDialog();
                    Snackbar snackbar = Snackbar.make(getWindow().getDecorView().getRootView(), "Something went wrong, try again later!", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                progress.dismiss();
                Snackbar snackbar = Snackbar.make(getWindow().getDecorView().getRootView(), "Please check your internet connection!", Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        });

        callBankMasterService();
    }

    private void selectLECCOC() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(Dashboard.this);
        builderSingle.setIcon(R.drawable.lec);
        builderSingle.setTitle("Select");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Dashboard.this, android.R.layout.select_dialog_singlechoice);
        arrayAdapter.add("LEC");
        arrayAdapter.add("COC");
        arrayAdapter.add("BOTH");
        arrayAdapter.add("GROUP SANCTION");

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);
                leccoc_tv.setText("" + strName);
            }
        });
        builderSingle.show();
    }

    private void callGetVillageListService() {
        // util.showProgressDialog(Dashboard.this);
        URLs ul = new URLs();

        final String url = "http://" + ul.ip + ":" + ul.port + "/" + ul.url + "/villages/" +
                json.get("mandalId").getAsString() + "/" + json.get("districtId").getAsString() +
                "/" + json.get("userId").getAsString() + role_value;

        util.getBaseClassService(url, null).getVillages(new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                //util.dismissDialog();
                if (response.getStatus() == 200) {
                    JsonArray villages = jsonObject.get("villages").getAsJsonObject().get("villages").getAsJsonArray();
                    if (villages.size() > 0) {
                        db.deleteVillageList();
                        for (int i = 0; i < villages.size(); i++) {
                            JsonObject obj = villages.get(i).getAsJsonObject();
                            db.insertVillageDetails("" + obj.get("villageId").getAsString(), "" + obj.get("villageMandalId").getAsString(),
                                    "" + obj.get("villageDistrictId").getAsString(), "" + obj.get("villageCode").getAsString(),
                                    "" + obj.get("villageName").getAsString().replace("'", ""),
                                    "" + obj.get("villageMandalCode").getAsString(), "" + obj.get("mandalWlCode").getAsString(),
                                    "" + obj.get("distWlCode").getAsString());
                        }
                    } else {
                        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().getRootView(), "Village data not available!", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                } else {
                    Snackbar snackbar1 = Snackbar.make(getWindow().getDecorView().getRootView(), "Something went wrong, try again later!", Snackbar.LENGTH_SHORT);
                    snackbar1.show();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                //util.dismissDialog();
                progress.dismiss();
                Snackbar snackbar = Snackbar.make(getWindow().getDecorView().getRootView(), "Please check your internet connection!", Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        });
    }

    private void selectVillage() {
        JsonArray villages = db.getVillagesList();
        if (villages.size() > 0) {
            setVillagesDropDown(villages);
        } else {
            Snackbar snackbar = Snackbar.make(getWindow().getDecorView().getRootView(), "Villages not available!", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }

    private void setVillagesDropDown(final JsonArray villages) {
        android.app.AlertDialog selectionAlert = null;

        final CharSequence[] nameItem = new String[villages.size()];
        final CharSequence[] idItem = new String[villages.size()];
        for (int i = 0; i < villages.size(); i++) {
            JsonObject jso = (JsonObject) villages.get(i);
            nameItem[i] = jso.get("villageName").getAsString();
            idItem[i] = jso.get("villageCode").getAsString();
        }

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Select Village");
        builder.setIcon(R.drawable.village);
        builder.setCancelable(false);
        builder.setItems(nameItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int item) {
                JsonObject jso = (JsonObject) villages.get(item);
                village_tv.setText(jso.get("villageName").getAsString());
                village_tv.setTag(jso.get("villageCode").getAsInt());
            }
        });
        if (selectionAlert == null) {
            selectionAlert = builder.create();
        }
        selectionAlert.setCancelable(true);
        if (!selectionAlert.isShowing()) {
            selectionAlert.show();
        }
    }

    public void downloadData(final View view) {
        if (!village_tv.getText().toString().equalsIgnoreCase("Select") &&
                !leccoc_tv.getText().toString().equalsIgnoreCase("Select")) {
            callServices();
            if (leccoc_tv.getText().toString().equalsIgnoreCase("LEC")) {
                getLECData(view);
            } else if (leccoc_tv.getText().toString().equalsIgnoreCase("COC")) {
                getCOCData(view);
            } else if (leccoc_tv.getText().toString().equalsIgnoreCase("BOTH")) {
                getBothData(view);
            } else if (leccoc_tv.getText().toString().equalsIgnoreCase("GROUP SANCTION")) {
                getGSData(view);
            }
        } else {
            Snackbar snackbar = Snackbar.make(view, "Please select all the fields !!!", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }

    private void getGSData(final View view) {
        try {
            progress.show();
            URLs ul = new URLs();
            final String url = "http://" + ul.ip + ":" + ul.port + "/" + ul.url + "/reportController/getGroupDetailsByMandal/"
                    + db.getUserDtls().get("mandalId").getAsString() + "/sanction";

            util.getBaseClassService(url, null).getGroupDetailsByMandal(new Callback<JsonObject>() {
                                                                            @Override
                                                                            public void success(JsonObject jsonObject, Response response) {
                                                                                //util.dismissDialog();
                                                                                if (response.getStatus() == 200) {
                                                                                    JsonArray loanprocess = jsonObject.get("loanprocess").getAsJsonObject().get("loanprocess").getAsJsonArray();
                                                                                    if (loanprocess.size() > 0) {
                                                                                        db.deleteGroupSanction(db.getUserDtls().get("mandalId").getAsString());
                                                                                        for (int i = 0; i < loanprocess.size(); i++) {
                                                                                            JsonObject obj = loanprocess.get(i).getAsJsonObject();
                                                                                            db.insertGroupSanction(db.getUserDtls().get("userId").getAsString(), db.getUserDtls().get("mandalId").getAsString(),
                                                                                                    "" + obj.get("groupId").getAsString(), "" + obj.get("groupName").getAsString(),
                                                                                                    "" + obj.get("groupMembersCount").getAsString().replaceAll("'", ""),
                                                                                                    "" + obj.get("totalGroupLandExtent").getAsString().replaceAll("'", ""),
                                                                                                    "" + obj.get("groupHeadName").getAsString(), "" + obj.get("groupHeadMobileNo").getAsString(),
                                                                                                    "" + obj.get("groupCreatedBy").getAsString().replaceAll("'", ""),
                                                                                                    "" + obj.get("groupRelatedTo").getAsString(), "" + obj.get("totalSanctionedAmount").getAsString());
                                                                                        }
                                                                                        progress.dismiss();
                                                                                        Snackbar snackbar = Snackbar.make(view, "Data Downloaded successfully for Group Members!!!", Snackbar.LENGTH_SHORT);
                                                                                        snackbar.show();
                                                                                        //util.dismissDialog();
                                                                                        showCheckView(loanprocess);
                                                                                        // gropSanction_view.setVisibility(View.VISIBLE);
                                                                                    } else {
                                                                                        //util.dismissDialog();
                                                                                        progress.dismiss();
                                                                                        Snackbar snackbar1 = Snackbar.make(view, "Data not available!", Snackbar.LENGTH_SHORT);
                                                                                        snackbar1.show();
                                                                                    }
                                                                                } else {
                                                                                    //util.dismissDialog();
                                                                                    progress.dismiss();
                                                                                    Snackbar snackbar1 = Snackbar.make(view, "Something went wrong, try again later!", Snackbar.LENGTH_SHORT);
                                                                                    snackbar1.show();
                                                                                }
                                                                            }

                                                                            @Override
                                                                            public void failure(RetrofitError error) {
                                                                                //util.dismissDialog();
                                                                                progress.dismiss();
                                                                                Snackbar snackbar1 = Snackbar.make(view, "Please check your Internet!", Snackbar.LENGTH_SHORT);
                                                                                snackbar1.show();
                                                                            }
                                                                        }
            );
        } catch (Exception e) {
            //util.dismissDialog();
            progress.dismiss();
            e.printStackTrace();
            Snackbar snackbar = Snackbar.make(view, "Something went wrong !!!", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }

    private void showCheckView(JsonArray loanprocess) {
        groupSan_check_ly_child = new LinearLayout(this);
        groupSan_check_ly_child.setOrientation(LinearLayout.VERTICAL);
        groupSan_check_ly.addView(groupSan_check_ly_child);
        Toast.makeText(this, "In Show view", Toast.LENGTH_SHORT).show();

        dynamicCheckBoxes = new CheckBox[loanprocess.size()];

        for (int i = 0; i < dynamicCheckBoxes.length; i++) {
            JsonObject jsoo = loanprocess.get(i).getAsJsonObject();
            Log.e("i value", "" + i + "       " + jsoo);

            checkBox = new CheckBox(this);
            checkBox.setTag(Integer.parseInt(jsoo.get("groupId").getAsString()));
            checkBox.setText(jsoo.get("groupName").getAsString() + "  Mobile No: " + jsoo.get("groupHeadMobileNo").getAsString());
            checkBox.setChecked(false);
            checkBox.setOnCheckedChangeListener(getOnClickDoSomething(checkBox, db.getUserDtls().get("mandalId").getAsString()));

            dynamicCheckBoxes[i] = checkBox;
            groupSan_check_ly_child.addView(checkBox);
        }
        download_gs.setVisibility(View.VISIBLE);
    }

    CompoundButton.OnCheckedChangeListener getOnClickDoSomething(final Button button, final String i) {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                Log.e("*************id******", "" + button.getTag());
                Log.e("and text***", button.getText().toString());
                if (isChecked) {
                    util.groupMembersData.put(button.getTag().toString(), i);
                } else {
                    util.groupMembersData.remove(button.getTag().toString());
                }
            }
        };
    }

    private void getBothData(final View view) {
        try {
            //util.showProgressDialog(Dashboard.this);
            progress.show();
            URLs ul = new URLs();
            final String url = "http://" + ul.ip + ":" + ul.port + "/" + ul.url + "/coc/getCommonVillageData/" + village_tv.getTag().toString();

            util.getBaseClassService(url, null).getBOTHDtls(new Callback<JsonObject>() {
                                                                @Override
                                                                public void success(JsonObject jsonObject, Response response) {
                                                                    //util.dismissDialog();
                                                                    if (response.getStatus() == 200) {
                                                                        JsonArray loanprocess = jsonObject.get("loanprocess").getAsJsonObject().get("loanprocess").getAsJsonArray();
                                                                        if (loanprocess.size() > 0) {
                                                                            db.deleteLECDtls("BOTH");
                                                                            for (int i = 0; i < loanprocess.size(); i++) {
                                                                                JsonObject obj = loanprocess.get(i).getAsJsonObject();
                                                                                db.insertLECDtls(db.getUserDtls().get("userId").getAsString(), village_tv.getTag().toString(), "" + obj.get("applicationId").getAsString(),
                                                                                        "" + obj.get("landExtent").getAsString(), "" + obj.get("cardNumber").getAsString().replaceAll("'", ""),
                                                                                        "" + obj.get("applicantName").getAsString().replaceAll("'", ""), "" + obj.get("aadharNumber").getAsString(),
                                                                                        "" + obj.get("issueDate").getAsString(),
                                                                                        "" + obj.get("surveyNumber").getAsString().replaceAll("'", ""), "" + obj.get("mobileNumber").getAsString(),
                                                                                        "" + obj.get("landId").getAsString(), "" + obj.get("totalGroupLandExtent").getAsString(),
                                                                                        "", "BOTH", "" + obj.get("cocCardNumber").getAsString(),
                                                                                        "" + obj.get("finYear").getAsString(), "" + obj.get("categoryName").getAsString(),
                                                                                        "" + obj.get("casteName").getAsString(),
                                                                                        "" + obj.get("totalSanctionedAmount").getAsString().replaceAll("'", ""),
                                                                                        village_tv.getText().toString(), "", "", "");
                                                                            }
                                                                            progress.dismiss();
                                                                            Snackbar snackbar = Snackbar.make(view, "Data Downloaded successfully for BOTH!!!", Snackbar.LENGTH_SHORT);
                                                                            snackbar.show();
                                                                            //util.dismissDialog();
                                                                            both_view.setVisibility(View.VISIBLE);
                                                                        } else {
                                                                            //util.dismissDialog();
                                                                            progress.dismiss();
                                                                            Snackbar snackbar1 = Snackbar.make(view, "Data not available!", Snackbar.LENGTH_SHORT);
                                                                            snackbar1.show();
                                                                        }
                                                                    } else {
                                                                        //util.dismissDialog();
                                                                        progress.dismiss();
                                                                        Snackbar snackbar1 = Snackbar.make(view, "Something went wrong, try again later!", Snackbar.LENGTH_SHORT);
                                                                        snackbar1.show();
                                                                    }
                                                                }

                                                                @Override
                                                                public void failure(RetrofitError error) {
                                                                    //util.dismissDialog();
                                                                    progress.dismiss();
                                                                    Snackbar snackbar1 = Snackbar.make(view, "Please check your Internet!", Snackbar.LENGTH_SHORT);
                                                                    snackbar1.show();
                                                                }
                                                            }
            );
        } catch (Exception e) {
            //util.dismissDialog();
            progress.dismiss();
            e.printStackTrace();
            Snackbar snackbar = Snackbar.make(view, "Something went wrong !!!", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }

    private void getCOCData(final View view) {
        try {
            //util.showProgressDialog(Dashboard.this);
            progress.show();
            URLs ul = new URLs();
            final String url = "http://" + ul.ip + ":" + ul.port + "/" + ul.url + "/coc/getVillageData/" + village_tv.getTag().toString();

            util.getBaseClassService(url, null).getCOCDtls(new Callback<JsonObject>() {
                                                               @Override
                                                               public void success(JsonObject jsonObject, Response response) {
                                                                   //util.dismissDialog();
                                                                   if (response.getStatus() == 200) {
                                                                       JsonArray loanprocess = jsonObject.get("loanprocess").getAsJsonObject().get("loanprocess").getAsJsonArray();
                                                                       if (loanprocess.size() > 0) {
                                                                           db.deleteLECDtls("COC");
                                                                           for (int i = 0; i < loanprocess.size(); i++) {
                                                                               JsonObject obj = loanprocess.get(i).getAsJsonObject();
                                                                               db.insertLECDtls(db.getUserDtls().get("userId").getAsString(), village_tv.getTag().toString(),
                                                                                       "", "" + obj.get("landExtent").getAsString(), "" + obj.get("cocCardNumber").getAsString().replaceAll("'", ""),
                                                                                       "" + obj.get("applicantName").getAsString().replaceAll("'", ""), "" + obj.get("aadharNumber").getAsString(),
                                                                                       "", "" + obj.get("surveyNumber").getAsString().replaceAll("'", ""), "" + obj.get("mobileNumber").getAsString(),
                                                                                       "", "" + obj.get("totalGroupLandExtent").getAsString(),
                                                                                       "" + obj.get("cropName").getAsString().replaceAll("'", ""), "COC",
                                                                                       "", "", "", "", "",
                                                                                       village_tv.getText().toString(), "", "", "");
                                                                           }
                                                                           Snackbar snackbar = Snackbar.make(view, "Data Downloaded successfully for LEC!!!", Snackbar.LENGTH_SHORT);
                                                                           snackbar.show();
                                                                           //util.dismissDialog();
                                                                           progress.dismiss();
                                                                           coc_view.setVisibility(View.VISIBLE);
                                                                       } else {
                                                                           //util.dismissDialog();
                                                                           progress.dismiss();
                                                                           Snackbar snackbar1 = Snackbar.make(view, "Data not available!", Snackbar.LENGTH_SHORT);
                                                                           snackbar1.show();
                                                                       }
                                                                   } else {
                                                                       //util.dismissDialog();
                                                                       progress.dismiss();
                                                                       Snackbar snackbar1 = Snackbar.make(view, "Something went wrong, try again later!", Snackbar.LENGTH_SHORT);
                                                                       snackbar1.show();
                                                                   }
                                                               }

                                                               @Override
                                                               public void failure(RetrofitError error) {
                                                                   //util.dismissDialog();
                                                                   progress.dismiss();
                                                                   Snackbar snackbar1 = Snackbar.make(view, "Please check your Internet!", Snackbar.LENGTH_SHORT);
                                                                   snackbar1.show();
                                                               }
                                                           }
            );
        } catch (Exception e) {
            //util.dismissDialog();
            progress.dismiss();
            e.printStackTrace();
            Snackbar snackbar = Snackbar.make(view, "Something went wrong !!!", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }

    private void getLECData(final View view) {
        try {
            //util.showProgressDialog(Dashboard.this);
            progress.show();
            URLs ul = new URLs();
            final String url = "http://" + ul.ip + ":" + ul.port + "/" + ul.url + "/processing/" + village_tv.getTag().toString() + "/report";

            util.getBaseClassService(url, null).getLECDtls(new Callback<JsonObject>() {
                                                               @Override
                                                               public void success(JsonObject jsonObject, Response response) {
                                                                   //util.dismissDialog();
                                                                   //progress.dismiss();
                                                                   if (response.getStatus() == 200) {
                                                                       JsonArray loanprocess = jsonObject.get("loanprocess").getAsJsonObject().get("loanprocess").getAsJsonArray();
                                                                       if (loanprocess.size() > 0) {
                                                                           db.deleteLECDtls("LEC");
                                                                           for (int i = 0; i < loanprocess.size(); i++) {
                                                                               JsonObject obj = loanprocess.get(i).getAsJsonObject();
                                                                               db.insertLECDtls(db.getUserDtls().get("userId").getAsString(), village_tv.getTag().toString(),
                                                                                       "" + obj.get("applicationId").getAsString(), "" + obj.get("landExtent").getAsString(), "" + obj.get("cardNumber").getAsString(),
                                                                                       "" + obj.get("applicantName").getAsString(), "" + obj.get("aadharNumber").getAsString(), "" + obj.get("issueDate").getAsString(),
                                                                                       "" + obj.get("surveyNumber").getAsString(), "" + obj.get("mobileNumber").getAsString(), "" + obj.get("landId").getAsString()
                                                                                       , "" + obj.get("totalGroupLandExtent").getAsString(), "", "LEC",
                                                                                       "", "", "", "", "",
                                                                                       village_tv.getText().toString(), "", "", "");
                                                                           }
                                                                           progress.dismiss();
                                                                           Snackbar snackbar = Snackbar.make(view, "Data Downloaded successfully for LEC!!!", Snackbar.LENGTH_SHORT);
                                                                           snackbar.show();
                                                                           lec_view.setVisibility(View.VISIBLE);
                                                                       } else {
                                                                           progress.dismiss();
                                                                           Snackbar snackbar1 = Snackbar.make(view, "Data not available!", Snackbar.LENGTH_SHORT);
                                                                           snackbar1.show();
                                                                       }
                                                                   } else {
                                                                       progress.dismiss();
                                                                       Snackbar snackbar1 = Snackbar.make(view, "Something went wrong, try again later!", Snackbar.LENGTH_SHORT);
                                                                       snackbar1.show();
                                                                   }
                                                               }

                                                               @Override
                                                               public void failure(RetrofitError error) {
                                                                   //util.dismissDialog();
                                                                   progress.dismiss();
                                                                   Snackbar snackbar1 = Snackbar.make(view, "Please check your Internet!", Snackbar.LENGTH_SHORT);
                                                                   snackbar1.show();
                                                               }
                                                           }
            );
        } catch (Exception e) {
            //util.dismissDialog();
            progress.dismiss();
            e.printStackTrace();
            Snackbar snackbar = Snackbar.make(view, "Something went wrong !!!", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }

    public void goLECView(View view) {
        JsonArray array = db.getLECCOCMaster("LEC", db.getUserDtls().get("userId").getAsString());
        if (array.size() > 0) {
            Intent i = new Intent(this, LECCOCHome.class);
            i.putExtra("from", "LEC");
            startActivity(i);
        } else {
            Snackbar snackbar = Snackbar.make(view, "No data available, Please download the data!!", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }

    public void syncWholeData(View view) {
        if (db.getLECCOCSanctionedData(db.getUserDtls().get("userName").getAsString()).size() != 0 ||
                db.getLECCOCRejectedData(db.getUserDtls().get("userName").getAsString()).size() != 0) {
            Intent i = new Intent(this, SyncData.class);
            startActivity(i);
        } else {
            Snackbar snackbar = Snackbar.make(getWindow().getDecorView().getRootView(), "Data not available !!!", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }

    public void goCOCView(View view) {
        JsonArray array = db.getLECCOCMaster("COC", db.getUserDtls().get("userId").getAsString());
        if (array.size() > 0) {
            Intent i = new Intent(this, LECCOCHome.class);
            i.putExtra("from", "COC");
            startActivity(i);
        } else {
            Snackbar snackbar = Snackbar.make(view, "No data available, Please download the data!!", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }

    public void goBothView(View view) {
        JsonArray array = db.getLECCOCMaster("BOTH", db.getUserDtls().get("userId").getAsString());
        if (array.size() > 0) {
            Intent i = new Intent(this, LECCOCHome.class);
            i.putExtra("from", "BOTH");
            startActivity(i);
        } else {
            Snackbar snackbar = Snackbar.make(view, "No data available, Please download the data!!", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }

    public void goGroupSanction(View view) {
        JsonArray array = db.getLECCOCMaster("GROUP SANCTION", db.getUserDtls().get("userId").getAsString());
        if (array.size() > 0) {
            Intent i = new Intent(this, GroupSanctionHome.class);
            startActivity(i);
        } else {
            Snackbar snackbar = Snackbar.make(view, "No data available, Please download the data!!", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }

    public void downloadGSMembersData(View view) {
        if (util.groupMembersData.size() > 0) {
            Iterator myVeryOwnIterator = util.groupMembersData.keySet().iterator();
            while (myVeryOwnIterator.hasNext()) {
                String groupId = (String) myVeryOwnIterator.next();
                Log.e("groupIddddddd", "" + groupId);
                callDownloadGSByGroupId(groupId, view);
            }
            groupSan_check_ly.setVisibility(View.GONE);
            download_gs.setVisibility(View.GONE);
            gropSanction_view.setVisibility(View.VISIBLE);
        } else {
            Snackbar snackbar = Snackbar.make(view, "Please check atleast one group before you download data!", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (db.getLECCOCMaster("LEC", db.getUserDtls().get("userId").getAsString()).size() > 0) {
            lec_view.setVisibility(View.VISIBLE);
        }
        if (db.getLECCOCMaster("COC", db.getUserDtls().get("userId").getAsString()).size() > 0) {
            coc_view.setVisibility(View.VISIBLE);
        }
        if (db.getLECCOCMaster("BOTH", db.getUserDtls().get("userId").getAsString()).size() > 0) {
            both_view.setVisibility(View.VISIBLE);
        }
        if (db.getLECCOCMaster("GROUP SANCTION", db.getUserDtls().get("userId").getAsString()).size() > 0) {
            gropSanction_view.setVisibility(View.VISIBLE);
        }
    }

    private void callDownloadGSByGroupId(String groupId, final View view) {
        try {
            //util.showProgressDialog(Dashboard.this);
            progress.show();
            URLs ul = new URLs();
            final String url = "http://" + ul.ip + ":" + ul.port + "/" + ul.url + "/processing/getLoanProcessingDetailsByGroupId/" + groupId;

            util.getBaseClassService(url, null).getLoanProcessingDetailsByGroupId(new Callback<JsonObject>() {
                                                                                      @Override
                                                                                      public void success(JsonObject jsonObject, Response response) {
                                                                                          //util.dismissDialog();
                                                                                          if (response.getStatus() == 200) {
                                                                                              JsonArray loanprocess = jsonObject.get("loanprocess").getAsJsonObject().get("loanprocess").getAsJsonArray();
                                                                                              if (loanprocess.size() > 0) {
                                                                                                  db.deleteLECDtls("GROUP SANCTION");
                                                                                                  for (int i = 0; i < loanprocess.size(); i++) {
                                                                                                      JsonObject obj = loanprocess.get(i).getAsJsonObject();
                                                                                                      String landId = "0";
                                                                                                      if (obj.has("landId")) {
                                                                                                          landId = obj.get("landId").getAsString();
                                                                                                      }
                                                                                                      db.insertLECDtls(db.getUserDtls().get("userId").getAsString(), "" + obj.get("villageId").getAsString(),
                                                                                                              "" + obj.get("applicationId").getAsString(), "" + obj.get("landExtent").getAsString(),
                                                                                                              "" + obj.get("cardNumber").getAsString(), "" + obj.get("applicantName").getAsString(),
                                                                                                              "" + obj.get("aadharNumber").getAsString(), "",
                                                                                                              "" + obj.get("surveyNumber").getAsString(), "" + obj.get("mobileNumber").getAsString(),
                                                                                                              "" + landId, "" + obj.get("totalGroupLandExtent").getAsString(),
                                                                                                              "", "GROUP SANCTION", "", "",
                                                                                                              "", "", "" + obj.get("totalSanctionedAmount").getAsString(),
                                                                                                              "" + obj.get("villageName").getAsString(), "" + obj.get("groupId").getAsString(),
                                                                                                              "" + obj.get("processingId").getAsString(), "" + obj.get("sanctionedFrom").getAsString());
                                                                                                  }
                                                                                                  progress.dismiss();
                                                                                                  Snackbar snackbar = Snackbar.make(view, "Data Downloaded successfully for Group Sanction!!!", Snackbar.LENGTH_SHORT);
                                                                                                  snackbar.show();
                                                                                              } else {
                                                                                                  progress.dismiss();
                                                                                                  Snackbar snackbar1 = Snackbar.make(view, "Data not available!", Snackbar.LENGTH_SHORT);
                                                                                                  snackbar1.show();
                                                                                              }
                                                                                          } else {
                                                                                              progress.dismiss();
                                                                                              Snackbar snackbar1 = Snackbar.make(view, "Something went wrong, try again later!", Snackbar.LENGTH_SHORT);
                                                                                              snackbar1.show();
                                                                                          }
                                                                                      }

                                                                                      @Override
                                                                                      public void failure(RetrofitError error) {
                                                                                          //util.dismissDialog();
                                                                                          progress.dismiss();
                                                                                          Snackbar snackbar1 = Snackbar.make(view, "Please check your Internet!", Snackbar.LENGTH_SHORT);
                                                                                          snackbar1.show();
                                                                                      }
                                                                                  }
            );
        } catch (Exception e) {
            //util.dismissDialog();
            progress.dismiss();
            e.printStackTrace();
            Snackbar snackbar = Snackbar.make(view, "Something went wrong !!!", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }
}