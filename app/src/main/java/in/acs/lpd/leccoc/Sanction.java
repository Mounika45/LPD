package in.acs.lpd.leccoc;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import in.acs.lpd.MyResponse;
import in.acs.lpd.R;
import in.acs.lpd.Services.Api;
import in.acs.lpd.database.DBAdapter;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Sanction extends AppCompatActivity {
    JsonObject SanctionObj;
    TextView app_type_sanction, app_sanction, survey_no_sanction,
            crop_sanction, scale_finance_sanction,
            bank_sanction, branch_sanction, ifsc_sanction,
            loanissuedate_sanction, loanduedate_sanction;
    EditText mobile_sanction;
    CheckBox land_extent_sanction;
    EditText accountno_sanction, loanamount_sanction;
    DBAdapter db;
    String from;
    File destination;
    String imagePath;
    Uri filePath;
    LinearLayout app_sanction_ly;
    ImageView imageview_sanction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sanction);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        checkRunTimePermission();

        try {
            db = new DBAdapter(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        JsonParser parser = new JsonParser();
        SanctionObj = parser.parse(getIntent().getExtras().get("SanctionObj").toString()).getAsJsonObject();
        Log.e("SanctionObj", "" + SanctionObj);
        from = getIntent().getExtras().get("from").toString();

        initialize();

        if (from.equalsIgnoreCase("BOTH")) {
            app_sanction_ly.setVisibility(View.VISIBLE);
        } else if (from.equalsIgnoreCase("LEC")) {
            app_sanction_ly.setVisibility(View.GONE);
            app_sanction.setText("LEC");
        } else if (from.equalsIgnoreCase("COC")) {
            app_sanction.setText("COC");
        } else if (from.equalsIgnoreCase("GS")) {

        }

        putExtraValues();
    }

    public void checkRunTimePermission() {
        String[] permissionArrays = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE};

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissionArrays, 11111);
        } else {
            // if already permition granted
            // PUT YOUR ACTION (Like Open cemara etc..)
        }
    }

    private void putExtraValues() {
        bank_sanction.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                branch_sanction.setText("Select");
                branch_sanction.setTag("-1");
                ifsc_sanction.setText("");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        crop_sanction.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                scale_finance_sanction.setText("Select");
                scale_finance_sanction.setTag("-1");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        loanissuedate_sanction.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                loanduedate_sanction.setText("" + addYear(loanissuedate_sanction.getText().toString(), 1));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        try {
            land_extent_sanction.setText("Land Extent : " + SanctionObj.get("landExtent").getAsString());
            if (!SanctionObj.get("mobileNumber").getAsString().equalsIgnoreCase(null) &&
                    !SanctionObj.get("mobileNumber").getAsString().equalsIgnoreCase("null")) {
                mobile_sanction.setText("" + SanctionObj.get("mobileNumber").getAsString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initialize() {
        app_type_sanction = findViewById(R.id.app_type_sanction);
        app_sanction = findViewById(R.id.app_sanction);
        app_sanction_ly = findViewById(R.id.app_sanction_ly);
        survey_no_sanction = findViewById(R.id.survey_no_sanction);
        survey_no_sanction.setText("" + SanctionObj.get("surveyNumber").getAsString());
        crop_sanction = findViewById(R.id.crop_sanction);
        scale_finance_sanction = findViewById(R.id.scale_finance_sanction);
        bank_sanction = findViewById(R.id.bank_sanction);
        branch_sanction = findViewById(R.id.branch_sanction);
        ifsc_sanction = findViewById(R.id.ifsc_sanction);
        loanissuedate_sanction = findViewById(R.id.loanissuedate_sanction);
        loanduedate_sanction = findViewById(R.id.loanduedate_sanction);

        land_extent_sanction = findViewById(R.id.land_extent_sanction);

        accountno_sanction = findViewById(R.id.accountno_sanction);
        loanamount_sanction = findViewById(R.id.loanamount_sanction);

        mobile_sanction = findViewById(R.id.mobile_sanction);
        imageview_sanction = findViewById(R.id.imageview_sanction);
    }

    public void getApplicationType(View view) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(Sanction.this);
        builderSingle.setIcon(R.drawable.lec);
        builderSingle.setTitle("Select");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Sanction.this, android.R.layout.select_dialog_singlechoice);
        arrayAdapter.add("New");
        arrayAdapter.add("Renewal");

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
                app_type_sanction.setText("" + strName);
            }
        });
        builderSingle.show();
    }

    public void getCropData(View view) {
        final JsonArray cropArray = db.getCropDtls(survey_no_sanction.getText().toString(), from);
        if (cropArray.size() > 0) {
            android.app.AlertDialog selectionAlert = null;

            final CharSequence[] nameItem = new String[cropArray.size()];
            final CharSequence[] idItem = new String[cropArray.size()];
            for (int i = 0; i < cropArray.size(); i++) {
                JsonObject jso = (JsonObject) cropArray.get(i);
                nameItem[i] = jso.get("cropName").getAsString();
                idItem[i] = jso.get("cropNameId").getAsString();
            }

            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setTitle("Select Crop");
            builder.setIcon(R.drawable.crop);
            builder.setCancelable(false);
            builder.setItems(nameItem, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, final int item) {
                    JsonObject jso = (JsonObject) cropArray.get(item);
                    crop_sanction.setText(jso.get("cropName").getAsString());
                    crop_sanction.setTag(jso.get("cropNameId").getAsInt());
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

    public void getBankDetails(View view) {
        final JsonArray bankArray = db.getBankDtls(SanctionObj.get("villageCode").getAsString(), from);
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
                    bank_sanction.setText(jso.get("bankName").getAsString());
                    bank_sanction.setTag(jso.get("bankId").getAsInt());
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
        if (!bank_sanction.getText().toString().equalsIgnoreCase("Select")) {
            final JsonArray branchArray = db.getBranchDtls(bank_sanction.getTag().toString(), from);
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
                        branch_sanction.setText(jso.get("branchName").getAsString());
                        branch_sanction.setTag(jso.get("branchId").getAsInt());
                        ifsc_sanction.setText("" + jso.get("ifscCode").getAsString());
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

    public void getSOF(View view) {
        final JsonArray sofArray = db.getSOFData(crop_sanction.getTag().toString(), from);
        if (sofArray.size() > 0) {
            android.app.AlertDialog selectionAlert = null;

            final CharSequence[] nameItem = new String[sofArray.size()];
            final CharSequence[] idItem = new String[sofArray.size()];
            for (int i = 0; i < sofArray.size(); i++) {
                JsonObject jso = (JsonObject) sofArray.get(i);
                nameItem[i] = "Rs." + jso.get("amount").getAsString() + "/-  (" + jso.get("type").getAsString() + ")";
                idItem[i] = jso.get("id").getAsString();
            }

            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setTitle("Select Scale Of Finanace");
            builder.setIcon(R.drawable.crop);
            builder.setCancelable(false);
            builder.setItems(nameItem, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, final int item) {
                    JsonObject jso = (JsonObject) sofArray.get(item);
                    scale_finance_sanction.setText("Rs." + jso.get("amount").getAsString() + "/-  (" + jso.get("type").getAsString() + ")");
                    scale_finance_sanction.setTag(jso.get("id").getAsInt());
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
            Snackbar snackbar = Snackbar.make(view, "Scale Of Finance data not avaialable!", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }

    public void setLoanIssueDate(View view) {
        final SimpleDateFormat dateFormatter;
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
        Calendar newCalendar = Calendar.getInstance();
        DatePickerDialog fromDatePickerDialog = new DatePickerDialog(Sanction.this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                loanissuedate_sanction.setText(dateFormatter.format(newDate.getTime()));
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        fromDatePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        fromDatePickerDialog.getDatePicker();
        fromDatePickerDialog.show();
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

    public void goUploadDocs(View view) {
        try {
            selectFiles();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void selectFiles() {
        final CharSequence[] options = {"Choose from Gallery", "Take Photo"};
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Sanction.this);
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
                    imageview_sanction.setVisibility(View.VISIBLE);
                    imageview_sanction.setImageBitmap(thumbnail);
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
                imageview_sanction.setVisibility(View.VISIBLE);
                imageview_sanction.setImageBitmap(thumbnail);

                Snackbar snackbar = Snackbar.make(getWindow().getDecorView().getRootView(), "File upload success", Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        }
    }

    public void goSaveData(View view) {
        if (isAllFieldsChecked()) {
            try {
                if (imagePath != null || filePath != null) {
                    int fileId = 0;
                    String cocCardNumber = null, filetype;
                    if (SanctionObj.has("cocCardNumber")) {
                        cocCardNumber = SanctionObj.get("cocCardNumber").getAsString();
                    }
                    if (imagePath != null) {
                        filetype = "image";
                        //String imageString = getStringFromFile(imagePath);
                        String imagename = getFileName(imagePath);
                        String imageextension = getFileExtension(imagePath);
                        if (SanctionObj.has("cocCardNumber")) {
                            cocCardNumber = SanctionObj.get("cocCardNumber").getAsString();
                        } else {
                            cocCardNumber = SanctionObj.get("cardNumber").getAsString();
                        }
                        fileId = db.insertFileForSanction(imagename, imageextension, db.getUserDtls().get("userId").getAsString(),
                                cocCardNumber, SanctionObj.get("applicationId").getAsString(), app_sanction.getText().toString(),
                                imagePath);
                    } else {
                        filetype = "pdf";
                        String imagename = getFileName(filePath);
                        String imageextension = "pdf";
                        fileId = db.insertFileForSanction(imagename, imageextension, db.getUserDtls().get("userId").getAsString(),
                                cocCardNumber, SanctionObj.get("applicationId").getAsString(), app_sanction.getText().toString(),
                                filePath.toString());
                    }

                    db.insertLECCOCData("" + SanctionObj.get("applicationId").getAsString(), "" + SanctionObj.get("cardNumber").getAsString(),
                            from, mobile_sanction.getText().toString(), SanctionObj.get("landExtent").getAsString(),
                            survey_no_sanction.getText().toString().trim(), app_type_sanction.getText().toString(), crop_sanction.getTag().toString(),
                            bank_sanction.getTag().toString(), branch_sanction.getTag().toString(), ifsc_sanction.getText().toString(),
                            accountno_sanction.getText().toString(), loanamount_sanction.getText().toString(), loanissuedate_sanction.getText().toString(),
                            loanduedate_sanction.getText().toString(), SanctionObj.get("villageCode").getAsString(), SanctionObj.get("applicantName").getAsString(),
                            SanctionObj.get("aadharNumber").getAsString(), db.getUserDtls().get("mandalId").getAsString(),
                            db.getUserDtls().get("districtId").getAsString(), db.getUserDtls().get("userName").getAsString(),
                            SanctionObj.get("landId").getAsString(), imagePath, cocCardNumber, app_sanction.getText().toString(), fileId, filetype,
                            scale_finance_sanction.getTag().toString());
                    db.updateLECCOCData(SanctionObj.get("id").getAsString());
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

    private void updateProfile(String imagePath) {
        //pass it like this
        File file = new File(imagePath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
        // add another part within the multipart request
        RequestBody fullName = RequestBody.create(MediaType.parse("multipart/form-data"), file.getName());

        Log.e("body_multipart", "" + body);
        //The gson builder
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        //creating retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        //creating our api
        Api api = retrofit.create(Api.class);
        Call<MyResponse> call = api.updateProfile(body, fullName);

        /*//creating a call and calling the upload image method
        Call<MyResponse> call = api.uploadImage(requestFile, fullName);
        Log.e("MYRESPONSE", "" + call);*/

        //finally performing the call
        call.enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                Log.e("response", "" + response);
                if (!response.body().equals(null)) {
                    Toast.makeText(getApplicationContext(), "File Uploaded Successfully...", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Some error occurred...", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {
                Log.e("t.getMessage()", "" + t.getMessage());
                Toast.makeText(getApplicationContext(), t.getMessage() + "  In Failure", Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean isAllFieldsChecked() {
        if (!app_type_sanction.getText().toString().equalsIgnoreCase("Select") &&
                !app_sanction.getText().toString().equalsIgnoreCase("Select") &&
                land_extent_sanction.isChecked() &&
                !survey_no_sanction.getText().toString().equalsIgnoreCase("") &&
                !crop_sanction.getText().toString().equalsIgnoreCase("Select") &&
                !scale_finance_sanction.getText().toString().equalsIgnoreCase("Select") &&
                !bank_sanction.getText().toString().equalsIgnoreCase("Select") &&
                !branch_sanction.getText().toString().equalsIgnoreCase("Select") &&
                !ifsc_sanction.getText().toString().equalsIgnoreCase("") &&
                !accountno_sanction.getText().toString().equalsIgnoreCase("") &&
                !loanamount_sanction.getText().toString().equalsIgnoreCase("") &&
                !mobile_sanction.getText().toString().equalsIgnoreCase("") &&
                !loanissuedate_sanction.getText().toString().equalsIgnoreCase("") &&
                !loanduedate_sanction.getText().toString().equalsIgnoreCase("")) {
            return true;
        } else {
            if (app_type_sanction.getText().toString().equalsIgnoreCase("Select")) {
                app_type_sanction.setError("");
            } else {
                app_type_sanction.setError(null);
            }
            if (app_sanction.getText().toString().equalsIgnoreCase("Select")) {
                app_sanction.setError("");
            } else {
                app_sanction.setError(null);
            }
            if (!land_extent_sanction.isChecked()) {
                land_extent_sanction.setError("");
            } else {
                land_extent_sanction.setError(null);
            }
            if (survey_no_sanction.getText().toString().equalsIgnoreCase("Select")) {
                survey_no_sanction.setError("");
            } else {
                survey_no_sanction.setError(null);
            }
            if (crop_sanction.getText().toString().equalsIgnoreCase("Select")) {
                crop_sanction.setError("");
            } else {
                crop_sanction.setError(null);
            }
            if (scale_finance_sanction.getText().toString().equalsIgnoreCase("Select")) {
                scale_finance_sanction.setError("");
            } else {
                scale_finance_sanction.setError(null);
            }
            if (bank_sanction.getText().toString().equalsIgnoreCase("Select")) {
                bank_sanction.setError("");
            } else {
                bank_sanction.setError(null);
            }
            if (branch_sanction.getText().toString().equalsIgnoreCase("Select")) {
                branch_sanction.setError("");
            } else {
                branch_sanction.setError(null);
            }
            if (ifsc_sanction.getText().toString().equalsIgnoreCase("")) {
                ifsc_sanction.setError("");
            } else {
                ifsc_sanction.setError(null);
            }
            if (accountno_sanction.getText().toString().equalsIgnoreCase("")) {
                accountno_sanction.setError("");
            } else {
                accountno_sanction.setError(null);
            }
            if (loanamount_sanction.getText().toString().equalsIgnoreCase("")) {
                loanamount_sanction.setError("");
            } else {
                loanamount_sanction.setError(null);
            }
            if (mobile_sanction.getText().toString().equalsIgnoreCase("")) {
                mobile_sanction.setError("");
            } else {
                mobile_sanction.setError(null);
            }
            if (loanissuedate_sanction.getText().toString().equalsIgnoreCase("")) {
                loanissuedate_sanction.setError("");
            } else {
                loanissuedate_sanction.setError(null);
            }
            if (loanduedate_sanction.getText().toString().equalsIgnoreCase("")) {
                loanduedate_sanction.setError("");
            } else {
                loanduedate_sanction.setError(null);
            }
            return false;
        }
    }

    public void getApplication(View view) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(Sanction.this);
        builderSingle.setIcon(R.drawable.lec);
        builderSingle.setTitle("Select");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Sanction.this, android.R.layout.select_dialog_singlechoice);
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
                app_sanction.setText("" + strName);
            }
        });
        builderSingle.show();
    }
}