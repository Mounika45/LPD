package in.acs.lpd.leccoc;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;

import in.acs.lpd.R;
import in.acs.lpd.Services.utils;
import in.acs.lpd.database.DBAdapter;

public class SanctionGS extends AppCompatActivity {
    TextView bank_sanction_gs, branch_sanction_gs, ifsc_sanction_gs, loanissuedate_sanction_gs, loanduedate_sanction_gs;
    EditText accountno_sanction_gs, loanamount_sanction_gs;
    ImageView imageview_sanction_gs;
    DBAdapter db;
    String from, villageCode, imagePath;
    File destination;
    Uri filePath;
    JsonObject json;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sanction_gs);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        checkRunTimePermission();

        try {
            db = new DBAdapter(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        from = getIntent().getExtras().get("from").toString();

        initialize();

        Map.Entry<String, JsonObject> entry = utils.saveGSData.entrySet().iterator().next();
        String key = entry.getKey();
        json = entry.getValue();
        Log.e("jsonnn", "" + json);
        villageCode = json.get("villageCode").getAsString();
        Log.e("villageCode", "" + villageCode);
    }

    public void checkRunTimePermission() {
        String[] permissionArrays = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissionArrays, 11111);
        } else {
            // if already permition granted
            // PUT YOUR ACTION (Like Open cemara etc..)
        }
    }

    private void initialize() {
        bank_sanction_gs = findViewById(R.id.bank_sanction_gs);
        branch_sanction_gs = findViewById(R.id.branch_sanction_gs);
        ifsc_sanction_gs = findViewById(R.id.ifsc_sanction_gs);
        loanissuedate_sanction_gs = findViewById(R.id.loanissuedate_sanction_gs);
        loanduedate_sanction_gs = findViewById(R.id.loanduedate_sanction_gs);

        loanissuedate_sanction_gs.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                loanduedate_sanction_gs.setText("" + addYear(loanissuedate_sanction_gs.getText().toString(), 1));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        accountno_sanction_gs = findViewById(R.id.accountno_sanction_gs);
        loanamount_sanction_gs = findViewById(R.id.loanamount_sanction_gs);

        imageview_sanction_gs = findViewById(R.id.imageview_sanction_gs);
    }

    public static String addYear(String date, int i) {
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();

        try {
            cal.setTime(format.parse(date));
            cal.add(Calendar.DATE, -1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        cal.add(Calendar.YEAR, i);

        String formatted = format.format(cal.getTime());
        System.out.println(formatted);

        return formatted;
    }

    public void getBankDetailsGS(View view) {
        final JsonArray bankArray = db.getBankDtls(villageCode, from);
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
                    bank_sanction_gs.setText(jso.get("bankName").getAsString());
                    bank_sanction_gs.setTag(jso.get("bankId").getAsInt());
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

    public void setLoanIssueDateGS(View view) {
        final SimpleDateFormat dateFormatter;
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
        Calendar newCalendar = Calendar.getInstance();
        DatePickerDialog fromDatePickerDialog = new DatePickerDialog(SanctionGS.this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                loanissuedate_sanction_gs.setText(dateFormatter.format(newDate.getTime()));
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        fromDatePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        fromDatePickerDialog.getDatePicker();
        fromDatePickerDialog.show();
    }

    public void goUploadDocsGS(View view) {
        try {
            selectFiles();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void selectFiles() {
        final CharSequence[] options = {"Choose from Gallery", "Take Photo"};
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SanctionGS.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    String name = System.currentTimeMillis() + "";
                    destination = new File(Environment.getExternalStorageDirectory(), name + ".jpg");
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(destination));
                    startActivityForResult(intent, 1);
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @SuppressLint("LongLogTag")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                try {
                    FileInputStream in = new FileInputStream(destination);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 10;
                    imagePath = destination.getAbsolutePath();
                    Bitmap thumbnail = (BitmapFactory.decodeFile(imagePath));
                    imageview_sanction_gs.setVisibility(View.VISIBLE);
                    imageview_sanction_gs.setImageBitmap(thumbnail);
                    Snackbar snackbar = Snackbar.make(getWindow().getDecorView().getRootView(), "File upload success", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 2) {
                Uri selectedImage = data.getData();
                filePath = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                imagePath = c.getString(columnIndex);
                c.close();
                Log.e("filepath", "" + filePath);

                Bitmap thumbnail = (BitmapFactory.decodeFile(imagePath));
                imageview_sanction_gs.setVisibility(View.VISIBLE);
                imageview_sanction_gs.setImageBitmap(thumbnail);

                Snackbar snackbar = Snackbar.make(getWindow().getDecorView().getRootView(), "File upload success", Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        }
    }

    public void goSaveDataGS(View view) {
        if (isAllFieldsChecked()) {
            try {
                if (imagePath != null || filePath != null) {
                    int fileId = 0;
                    /*String cocCardNumber = null, filetype;

                    if (SanctionObj.has("cocCardNumber")) {
                        cocCardNumber = SanctionObj.get("cocCardNumber").getAsString();
                    }*/
                    String filetype;
                    if (imagePath != null) {
                        filetype = "image";
                        //String imageString = getStringFromFile(imagePath);
                        String imagename = getFileName(imagePath);
                        String imageextension = getFileExtension(imagePath);
                        /*if (SanctionObj.has("cocCardNumber")) {
                            cocCardNumber = SanctionObj.get("cocCardNumber").getAsString();
                        } else {
                            cocCardNumber = SanctionObj.get("cardNumber").getAsString();
                        }*/
                        fileId = db.insertFileForSanction(imagename, imageextension, db.getUserDtls().get("userId").getAsString(),
                                json.get("cardNumber").getAsString(), json.get("applicationId").getAsString(),
                                json.get("app_type").getAsString(), imagePath);
                    } else {
                        filetype = "pdf";
                        String imagename = getFileName(filePath);
                        String imageextension = "pdf";
                        fileId = db.insertFileForSanction(imagename, imageextension, db.getUserDtls().get("userId").getAsString(),
                                json.get("cardNumber").getAsString(), json.get("applicationId").getAsString(), json.get("app_type").getAsString(),
                                filePath.toString());
                    }

                    Iterator myVeryOwnIterator = utils.saveGSData.keySet().iterator();
                    while (myVeryOwnIterator.hasNext()) {
                        String id = (String) myVeryOwnIterator.next();
                        Log.e("idddddddd", "" + id);
                        JsonObject SanctionObj = utils.saveGSData.get(id).getAsJsonObject();
                        db.insertLECCOCData("" + SanctionObj.get("applicationId").getAsString(),
                                "" + SanctionObj.get("cardNumber").getAsString(),
                                from, SanctionObj.get("mobileNumber").getAsString(), SanctionObj.get("landExtent").getAsString(),
                                SanctionObj.get("surveyNumber").getAsString(), SanctionObj.get("applicationType").getAsString(),
                                SanctionObj.get("crop").getAsString(), bank_sanction_gs.getTag().toString(), branch_sanction_gs.getTag().toString(),
                                ifsc_sanction_gs.getText().toString(), accountno_sanction_gs.getText().toString(),
                                loanamount_sanction_gs.getText().toString(), loanissuedate_sanction_gs.getText().toString(),
                                loanduedate_sanction_gs.getText().toString(), SanctionObj.get("villageCode").getAsString(),
                                SanctionObj.get("applicantName").getAsString(), SanctionObj.get("aadharNumber").getAsString(),
                                db.getUserDtls().get("mandalId").getAsString(), db.getUserDtls().get("districtId").getAsString(),
                                db.getUserDtls().get("userName").getAsString(), SanctionObj.get("landId").getAsString(), imagePath,
                                SanctionObj.get("cocCardNumber").getAsString(), SanctionObj.get("app_type").getAsString(), fileId, filetype, "");
                        db.updateLECCOCData(SanctionObj.get("id").getAsString());
                    }
                    Snackbar snackbar = Snackbar.make(view, "Data saved successfully!!!", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    finish();
                } else {
                    Snackbar snackbar = Snackbar.make(view, "Please select a file Or Image!!!", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Snackbar snackbar = Snackbar.make(view, "Please fill in all fields!!!", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }

    private boolean isAllFieldsChecked() {
        if (!bank_sanction_gs.getText().toString().equalsIgnoreCase("Select") &&
                !branch_sanction_gs.getText().toString().equalsIgnoreCase("Select") &&
                !ifsc_sanction_gs.getText().toString().equalsIgnoreCase("") &&
                !accountno_sanction_gs.getText().toString().equalsIgnoreCase("") &&
                !loanamount_sanction_gs.getText().toString().equalsIgnoreCase("") &&
                !loanissuedate_sanction_gs.getText().toString().equalsIgnoreCase("") &&
                !loanduedate_sanction_gs.getText().toString().equalsIgnoreCase("")) {
            return true;
        } else {
            if (bank_sanction_gs.getText().toString().equalsIgnoreCase("Select")) {
                bank_sanction_gs.setError("");
            } else {
                bank_sanction_gs.setError(null);
            }
            if (branch_sanction_gs.getText().toString().equalsIgnoreCase("Select")) {
                branch_sanction_gs.setError("");
            } else {
                branch_sanction_gs.setError(null);
            }
            if (ifsc_sanction_gs.getText().toString().equalsIgnoreCase("")) {
                ifsc_sanction_gs.setError("");
            } else {
                ifsc_sanction_gs.setError(null);
            }
            if (accountno_sanction_gs.getText().toString().equalsIgnoreCase("")) {
                accountno_sanction_gs.setError("");
            } else {
                accountno_sanction_gs.setError(null);
            }
            if (loanamount_sanction_gs.getText().toString().equalsIgnoreCase("")) {
                loanamount_sanction_gs.setError("");
            } else {
                loanamount_sanction_gs.setError(null);
            }
            if (loanissuedate_sanction_gs.getText().toString().equalsIgnoreCase("")) {
                loanissuedate_sanction_gs.setError("");
            } else {
                loanissuedate_sanction_gs.setError(null);
            }
            if (loanduedate_sanction_gs.getText().toString().equalsIgnoreCase("")) {
                loanduedate_sanction_gs.setError("");
            } else {
                loanduedate_sanction_gs.setError(null);
            }
            return false;
        }
    }

    public void getBranchDtlsGS(View view) {
        if (!bank_sanction_gs.getText().toString().equalsIgnoreCase("Select")) {
            final JsonArray branchArray = db.getBranchDtls(bank_sanction_gs.getTag().toString(), from);
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
                        branch_sanction_gs.setText(jso.get("branchName").getAsString());
                        branch_sanction_gs.setTag(jso.get("branchId").getAsInt());
                        ifsc_sanction_gs.setText("" + jso.get("ifscCode").getAsString());
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

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private String getFileName(String path) {
        String name = path.substring(path.lastIndexOf("/") + 1);
        return name;
    }

    private String getFileExtension(String path) {
        try {
            return path.substring(path.lastIndexOf(".") + 1);
        } catch (Exception e) {
            return "";
        }
    }
}
