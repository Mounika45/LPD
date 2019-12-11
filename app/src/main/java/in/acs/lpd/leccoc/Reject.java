package in.acs.lpd.leccoc;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import in.acs.lpd.R;
import in.acs.lpd.database.DBAdapter;

public class Reject extends AppCompatActivity {
    JsonObject RejectObj;
    TextView app_type_reject, bank_reject, branch_reject, ifsc_reject, reason_reject;
    EditText mobile_reject;
    DBAdapter db;
    String from;
    LinearLayout app_type_reject_ly, mobile_reject_ly;
    JsonArray gsArray;
    JsonParser parser = new JsonParser();
    JsonObject gsObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reject);

        try {
            db = new DBAdapter(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        from = getIntent().getExtras().get("from").toString();

        initialize();

        if (from.equalsIgnoreCase("GROUP SANCTION")) {
            app_type_reject_ly.setVisibility(View.GONE);
            mobile_reject_ly.setVisibility(View.GONE);

            gsObj = parser.parse(getIntent().getExtras().get("gsObj").toString()).getAsJsonObject();
            gsArray = parser.parse(getIntent().getExtras().get("gsArray").toString()).getAsJsonArray();
        } else {
            RejectObj = parser.parse(getIntent().getExtras().get("RejectObj").toString()).getAsJsonObject();
            Log.e("RejectObj", "" + RejectObj);
        }
    }

    private void initialize() {
        app_type_reject = findViewById(R.id.app_type_reject);
        bank_reject = findViewById(R.id.bank_reject);
        branch_reject = findViewById(R.id.branch_reject);
        ifsc_reject = findViewById(R.id.ifsc_reject);
        reason_reject = findViewById(R.id.reason_reject);

        mobile_reject = findViewById(R.id.mobile_reject);

        if (!from.equalsIgnoreCase("GROUP SANCTION")) {
            try {
                if (!RejectObj.get("mobileNumber").getAsString().equalsIgnoreCase(null) &&
                        !RejectObj.get("mobileNumber").getAsString().equalsIgnoreCase("null")) {
                    mobile_reject.setText("" + RejectObj.get("mobileNumber").getAsString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        app_type_reject_ly = findViewById(R.id.app_type_reject_ly);
        mobile_reject_ly = findViewById(R.id.mobile_reject_ly);
    }

    public void getApplicationTypeReject(View view) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(Reject.this);
        builderSingle.setIcon(R.drawable.lec);
        builderSingle.setTitle("Select");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Reject.this, android.R.layout.select_dialog_singlechoice);
        arrayAdapter.add("LEC");
        arrayAdapter.add("COC");

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
                app_type_reject.setText("" + strName);
            }
        });
        builderSingle.show();
    }

    public void getBankDetails(View view) {
        final JsonArray bankArray;
        if (from.equalsIgnoreCase("GROUP SANCTION")) {
            bankArray = db.getBankDtls(gsObj.get("villageCode").getAsString(), from);
        } else {
            bankArray = db.getBankDtls(RejectObj.get("villageCode").getAsString(), from);
        }
        if (bankArray.size() > 0) {
            android.app.AlertDialog selectionAlert = null;

            final CharSequence[] nameItem = new String[bankArray.size()];
            final CharSequence[] idItem = new String[bankArray.size()];
            for (int i = 0; i < bankArray.size(); i++) {
                JsonObject jso = (JsonObject) bankArray.get(i);
                nameItem[i] = jso.get("bankName").getAsString();
                idItem[i] = jso.get("bankId").getAsString();
            }

            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setTitle("Select Bank");
            builder.setIcon(R.drawable.bank);
            builder.setCancelable(false);
            builder.setItems(nameItem, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, final int item) {
                    JsonObject jso = (JsonObject) bankArray.get(item);
                    bank_reject.setText(jso.get("bankName").getAsString());
                    bank_reject.setTag(jso.get("bankId").getAsInt());
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
            Snackbar snackbar = Snackbar.make(view, "Banks not avaialable!", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }

    public void getBranchDtls(View view) {
        if (!bank_reject.getText().toString().equalsIgnoreCase("Select")) {
            final JsonArray branchArray = db.getBranchDtls(bank_reject.getTag().toString(), from);
            if (branchArray.size() > 0) {
                android.app.AlertDialog selectionAlert = null;

                final CharSequence[] nameItem = new String[branchArray.size()];
                final CharSequence[] idItem = new String[branchArray.size()];
                for (int i = 0; i < branchArray.size(); i++) {
                    JsonObject jso = (JsonObject) branchArray.get(i);
                    nameItem[i] = jso.get("branchName").getAsString();
                    idItem[i] = jso.get("branchId").getAsString();
                }

                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                builder.setTitle("Select Branch");
                builder.setIcon(R.drawable.bank);
                builder.setCancelable(false);
                builder.setItems(nameItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int item) {
                        JsonObject jso = (JsonObject) branchArray.get(item);
                        branch_reject.setText(jso.get("branchName").getAsString());
                        branch_reject.setTag(jso.get("branchId").getAsInt());
                        ifsc_reject.setText("" + jso.get("ifscCode").getAsString());
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
                Snackbar snackbar = Snackbar.make(view, "Branches not avaialable!", Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        } else {
            Snackbar snackbar = Snackbar.make(view, "Please select a bank first!!!", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }

    public void getReasonDtls(View view) {
        final JsonArray reasonArray = db.getRejectionReasons();
        if (reasonArray.size() > 0) {
            android.app.AlertDialog selectionAlert = null;

            final CharSequence[] nameItem = new String[reasonArray.size()];
            final CharSequence[] idItem = new String[reasonArray.size()];
            for (int i = 0; i < reasonArray.size(); i++) {
                JsonObject jso = (JsonObject) reasonArray.get(i);
                nameItem[i] = jso.get("reasonName").getAsString();
                idItem[i] = jso.get("reasonId").getAsString();
            }

            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setTitle("Select Reason For Rejection");
            builder.setIcon(R.drawable.bank);
            builder.setCancelable(false);
            builder.setItems(nameItem, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, final int item) {
                    JsonObject jso = (JsonObject) reasonArray.get(item);
                    reason_reject.setText(jso.get("reasonName").getAsString());
                    reason_reject.setTag(jso.get("reasonId").getAsInt());
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
            Snackbar snackbar = Snackbar.make(view, "Banks not avaialable!", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }

    public void goRejectApplication(View view) {
        if (isAllFieldsChecked()) {
            if (from.equalsIgnoreCase("GROUP SANCTION")) {
                if (gsArray.size() > 0) {
                    for (int i = 0; i < gsArray.size(); i++) {
                        JsonObject obj = gsArray.get(i).getAsJsonObject();

                        db.insertRejectData("" + obj.get("mobileNumber").getAsString(), "" + obj.get("landExtent").getAsString(),
                                "" + obj.get("surveyNumber").getAsString(), "" + obj.get("applicationType").getAsString(),
                                "" + bank_reject.getTag().toString(),
                                "" + branch_reject.getTag().toString(), "" + ifsc_reject.getText().toString(), reason_reject.getTag().toString(),
                                "" + obj.get("villageCode").getAsString(), "" + obj.get("applicantName").getAsString(),
                                "" + obj.get("aadharNumber").getAsString(), "" + db.getUserDtls().get("mandalId").getAsString(),
                                db.getUserDtls().get("districtId").getAsString(), db.getUserDtls().get("userName").getAsString(),
                                "" + obj.get("cardNumber").getAsString(), "" + obj.get("landId").getAsString(), "Rejected",
                                obj.get("applicationId").getAsString(), "" + obj.get("cocCardNumber").getAsString());
                        db.updateLECCOCData(obj.get("id").getAsString());
                    }
                }
            } else {
                String cocCardNumber = null;
                if (RejectObj.has("cocCardNumber")) {
                    cocCardNumber = RejectObj.get("cocCardNumber").getAsString();
                }
                db.insertRejectData(mobile_reject.getText().toString(), "" + RejectObj.get("landExtent").getAsString(),
                        "" + RejectObj.get("surveyNumber").getAsString(), "" + app_type_reject.getText().toString(),
                        "" + bank_reject.getTag().toString(),
                        "" + branch_reject.getTag().toString(), "" + ifsc_reject.getText().toString(), reason_reject.getTag().toString(),
                        "" + RejectObj.get("villageCode").getAsString(), "" + RejectObj.get("applicantName").getAsString(),
                        "" + RejectObj.get("aadharNumber").getAsString(), "" + db.getUserDtls().get("mandalId").getAsString(),
                        db.getUserDtls().get("districtId").getAsString(), db.getUserDtls().get("userName").getAsString(),
                        "" + RejectObj.get("cardNumber").getAsString(), "" + RejectObj.get("landId").getAsString(), "Rejected",
                        RejectObj.get("applicationId").getAsString(), "" + cocCardNumber);
                db.updateLECCOCData(RejectObj.get("id").getAsString());
            }
            Snackbar snackbar = Snackbar.make(view, "Data saved successfully!!!", Snackbar.LENGTH_SHORT);
            snackbar.show();
            finish();
        }
    }

    private boolean isAllFieldsChecked() {
        if (from.equalsIgnoreCase("GROUP SANCTION")) {
            if (!bank_reject.getText().toString().equalsIgnoreCase("Select") &&
                    !branch_reject.getText().toString().equalsIgnoreCase("Select") &&
                    !ifsc_reject.getText().toString().equalsIgnoreCase("") &&
                    !reason_reject.getText().toString().equalsIgnoreCase("Select")) {
                return true;
            } else {
                if (bank_reject.getText().toString().equalsIgnoreCase("")) {
                    bank_reject.setError("");
                } else {
                    bank_reject.setError(null);
                }
                if (branch_reject.getText().toString().equalsIgnoreCase("")) {
                    branch_reject.setError("");
                } else {
                    branch_reject.setError(null);
                }
                if (ifsc_reject.getText().toString().equalsIgnoreCase("")) {
                    ifsc_reject.setError("");
                } else {
                    ifsc_reject.setError(null);
                }
                if (reason_reject.getText().toString().equalsIgnoreCase("")) {
                    reason_reject.setError("");
                } else {
                    reason_reject.setError(null);
                }
                return false;
            }
        } else {
            if (!app_type_reject.getText().toString().equalsIgnoreCase("Select") &&
                    !mobile_reject.getText().toString().equalsIgnoreCase("") &&
                    !bank_reject.getText().toString().equalsIgnoreCase("Select") &&
                    !branch_reject.getText().toString().equalsIgnoreCase("Select") &&
                    !ifsc_reject.getText().toString().equalsIgnoreCase("") &&
                    !reason_reject.getText().toString().equalsIgnoreCase("Select")) {
                return true;
            } else {
                if (app_type_reject.getText().toString().equalsIgnoreCase("Select")) {
                    app_type_reject.setError("");
                } else {
                    app_type_reject.setError(null);
                }
                if (mobile_reject.getText().toString().equalsIgnoreCase("")) {
                    mobile_reject.setError("");
                } else {
                    mobile_reject.setError(null);
                }
                if (bank_reject.getText().toString().equalsIgnoreCase("")) {
                    bank_reject.setError("");
                } else {
                    bank_reject.setError(null);
                }
                if (branch_reject.getText().toString().equalsIgnoreCase("")) {
                    branch_reject.setError("");
                } else {
                    branch_reject.setError(null);
                }
                if (ifsc_reject.getText().toString().equalsIgnoreCase("")) {
                    ifsc_reject.setError("");
                } else {
                    ifsc_reject.setError(null);
                }
                if (reason_reject.getText().toString().equalsIgnoreCase("")) {
                    reason_reject.setError("");
                } else {
                    reason_reject.setError(null);
                }
                return false;
            }
        }
    }
}
