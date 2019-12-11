package in.acs.lpd.sync;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Base64OutputStream;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import in.acs.lpd.Adapter.RejectSyncAdapter;
import in.acs.lpd.Adapter.SanctionSyncAdapter;
import in.acs.lpd.Dashboard;
import in.acs.lpd.Login;
import in.acs.lpd.R;
import in.acs.lpd.Services.URLs;
import in.acs.lpd.Services.utils;
import in.acs.lpd.database.DBAdapter;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SyncData extends AppCompatActivity {
    ListView sanctioned_sync_lv, rejected_sync_lv;
    JsonArray sanctionedArray, rejectedArray, filesArray;
    DBAdapter db;
    ProgressDialog progress;
    utils util = new utils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_data);

        progress = new ProgressDialog(this);
        progress.setTitle("Uploading to Server");
        progress.setMessage("Please Wait.......");
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setIndeterminate(true);
        progress.setCancelable(false);

        try {
            db = new DBAdapter(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        sanctioned_sync_lv = findViewById(R.id.sanctioned_sync_lv);
        rejected_sync_lv = findViewById(R.id.rejected_sync_lv);

        sanctionedArray = db.getLECCOCSanctionedData(db.getUserDtls().get("userName").getAsString());
        rejectedArray = db.getLECCOCRejectedData(db.getUserDtls().get("userName").getAsString());

        if (sanctionedArray.size() == 0 &&
                rejectedArray.size() == 0) {
            Snackbar snackbar = Snackbar.make(getWindow().getDecorView().getRootView(), "Data not available !!!", Snackbar.LENGTH_SHORT);
            snackbar.show();
            finish();
        }

        setSanctionedAdapter();
        setRejectedAdapter();
    }

    private void setRejectedAdapter() {
        rejectedArray = db.getLECCOCRejectedData(db.getUserDtls().get("userName").getAsString());

        SanctionSyncAdapter adapter = new SanctionSyncAdapter(SyncData.this, R.layout.inflater_sanctioned_sync, sanctionedArray);
        sanctioned_sync_lv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void setSanctionedAdapter() {
        sanctionedArray = db.getLECCOCSanctionedData(db.getUserDtls().get("userName").getAsString());

        RejectSyncAdapter adapter = new RejectSyncAdapter(SyncData.this, R.layout.inflater_rejected_sync, rejectedArray);
        rejected_sync_lv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void syncDataToServer(View view) {
        progress.show();
        callSyncServices();
    }

    private void callSyncServices() {
        filesArray = db.getFiles(db.getUserDtls().get("userId").getAsString());
        try {
            if (filesArray.size() > 0) {
                callUploadFilesService();
            } else if (sanctionedArray.size() > 0) {
                callSanctionService();
            } else if (rejectedArray.size() > 0) {
                callRejectionService();
            } else {
                progress.dismiss();
                Snackbar snackbar = Snackbar.make(getWindow().getDecorView().getRootView(), "No data available to sync!!!", Snackbar.LENGTH_SHORT);
                snackbar.show();
                finish();
            }
        } catch (Exception e) {
            progress.dismiss();
            e.printStackTrace();
            Snackbar snackbar = Snackbar.make(getWindow().getDecorView().getRootView(), "Something went wrong!!!", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }

    private void callRejectionService() {
        URLs ul = new URLs();
        final String url = "http://" + ul.ip + ":" + ul.port + "/" + ul.url;
        Log.e("url", "" + url);
        JsonObject jsonObj = rejectedArray.get(0).getAsJsonObject();
        jsonObj.remove("cardNumber");
        final String id = jsonObj.get("id").getAsString();
        jsonObj.remove("id");

        JsonObject processingDtls = new JsonObject();
        processingDtls.add("processingDtls", jsonObj);

        util.getBaseClassService(url, null).saveRejectionData(processingDtls, new Callback<JsonObject>() {
                    @Override
                    public void success(JsonObject jsonObject, Response response) {
                        //progress.dismiss();
                        Log.i("KAR", "success" + jsonObject);
                        if (response.getStatus() == 200) {
                            Log.e("response_sanction", "" + response);
                            db.deleteRejectionData(id);
                            rejectedArray = db.getLECCOCRejectedData(db.getUserDtls().get("userName").getAsString());
                            if (rejectedArray.size() > 0) {
                                setRejectedAdapter();
                            }
                            callSyncServices();
                        } else {
                            Snackbar snackbar1 = Snackbar.make(getWindow().getDecorView().getRootView(), "Something went wrong, try again later!", Snackbar.LENGTH_SHORT);
                            snackbar1.show();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        progress.dismiss();
                        Snackbar snackbar1 = Snackbar.make(getWindow().getDecorView().getRootView(), "Please check your Internet Connection!", Snackbar.LENGTH_SHORT);
                        snackbar1.show();
                    }
                }
        );
    }

    private void callSanctionService() {
        URLs ul = new URLs();
        final String url = "http://" + ul.ip + ":" + ul.port + "/" + ul.url;
        Log.e("url", "" + url);
        JsonObject jsonObj = sanctionedArray.get(0).getAsJsonObject();
        Log.e("sanctionArray", "" + jsonObj);
        jsonObj.remove("cardNumber");
        final String id = jsonObj.get("id").getAsString();
        jsonObj.remove("id");

        JsonObject processingDtls = new JsonObject();
        processingDtls.add("processingDtls", jsonObj);

        util.getBaseClassService(url, null).saveSanctionData(processingDtls, new Callback<JsonObject>() {
                    @Override
                    public void success(JsonObject jsonObject, Response response) {
                        //progress.dismiss();
                        Log.i("KAR", "success" + jsonObject);
                        if (response.getStatus() == 200) {
                            Log.e("response_sanction", "" + response);
                            db.deleteSanctionData(id);
                            sanctionedArray = db.getLECCOCSanctionedData(db.getUserDtls().get("userName").getAsString());
                            if (sanctionedArray.size() > 0) {
                                setSanctionedAdapter();
                            }
                            callSyncServices();
                        } else {
                            Snackbar snackbar1 = Snackbar.make(getWindow().getDecorView().getRootView(), "Something went wrong, try again later!", Snackbar.LENGTH_SHORT);
                            snackbar1.show();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        progress.dismiss();
                        Snackbar snackbar1 = Snackbar.make(getWindow().getDecorView().getRootView(), "Please check your Internet Connection!", Snackbar.LENGTH_SHORT);
                        snackbar1.show();
                    }
                }
        );
    }

    private void callUploadFilesService() {
        URLs ul = new URLs();
        final String url = "http://" + ul.ip + ":" + ul.port + "/" + ul.url + "/loanProcessingUploadController";
        Log.e("url", "" + url);
        JsonObject jsonObj = filesArray.get(0).getAsJsonObject();
        final String id = jsonObj.get("id").getAsString();
        jsonObj.remove("id");

        if (jsonObj.get("enclosureFileType").getAsString().equalsIgnoreCase("pdf")) {
            try {
                Uri imageUri = Uri.parse(jsonObj.get("imagePath").getAsString());
                File file = new File(imageUri.getPath());//create path from uri
                final String[] split = file.getPath().split(":");

                Log.e("imagePath  ", "" + jsonObj.get("imagePath").getAsString() + "    file.getAbsolutePath()    " + split[1]);

                jsonObj.remove("imagePath");
                jsonObj.addProperty("image", getStringFromFile(split[1]));

                /*convertFileToByteArray(file);

                Log.e("imagePath  ", "" + convertFileToByteArray(file));
                jsonObj.remove("imagePath");
                jsonObj.addProperty("image", convertFileToByteArray(file));*/

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            String image = getStringFromFile(jsonObj.get("imagePath").getAsString());
            jsonObj.remove("imagePath");
            jsonObj.addProperty("image", image);
        }

        JsonObject loanProcessingUplaod = new JsonObject();
        loanProcessingUplaod.add("loanProcessingUplaod", jsonObj);

        util.getBaseClassService(url, null).mobileUploadDocument(loanProcessingUplaod, new Callback<JsonObject>() {
                    @Override
                    public void success(JsonObject jsonObject, Response response) {
                        //progress.dismiss();
                        Log.i("KAR", "success" + jsonObject);
                        if (response.getStatus() == 200) {
                            Log.e("response_files", "" + response);
                            JsonObject loanProcessingUplaod = jsonObject.get("loanProcessingUplaod").getAsJsonObject();
                            if (loanProcessingUplaod.get("docStatus").getAsString().equalsIgnoreCase("Success")) {
                                if (loanProcessingUplaod.has("enclosureId")) {
                                    db.updateLecCocSanctionData(id, loanProcessingUplaod.get("enclosureId").getAsString());
                                    db.deleteFiles(id);
                                    callSyncServices();
                                } else {
                                    Snackbar snackbar = Snackbar.make(getWindow().getDecorView().getRootView(), "Something went wrong!!!", Snackbar.LENGTH_LONG);
                                    snackbar.show();
                                    progress.dismiss();
                                }
                            } else {
                                callSyncServices();
                            }
                        } else {
                            Snackbar snackbar1 = Snackbar.make(getWindow().getDecorView().getRootView(), "Something went wrong, try again later!", Snackbar.LENGTH_SHORT);
                            snackbar1.show();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        progress.dismiss();
                        Snackbar snackbar1 = Snackbar.make(getWindow().getDecorView().getRootView(), "Please check your Internet Connection!", Snackbar.LENGTH_SHORT);
                        snackbar1.show();
                    }
                }
        );
    }

    public static String convertFileToByteArray(File f) {
        InputStream inputStream = null;
        String encodedFile = "", lastVal;
        try {
            inputStream = new FileInputStream(f.getAbsolutePath());

            byte[] buffer = new byte[10240];//specify the size to allow
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            Base64OutputStream output64 = new Base64OutputStream(output, Base64.DEFAULT);

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output64.write(buffer, 0, bytesRead);
            }
            output64.close();
            encodedFile = output.toString();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        lastVal = encodedFile;
        return lastVal;
    }

    private String getStringFromFile(String path) {
        Log.e("KAR", "getStringFromFile :: path " + path);
        //Bitmap bitmap = BitmapFactory.decodeFile(path);

        File file = new File(path);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        try {
            FileInputStream fis = new FileInputStream(file);
            for (int readNum; (readNum = fis.read(buf)) != -1; ) {
                bos.write(buf, 0, readNum); //no doubt here is 0
            }
            fis.close();
        } catch (IOException ex) {
        }
        //bitmap.compress(Bitmap.CompressFormat.JPEG,50, bos);
        byte[] bytes = bos.toByteArray();
        Log.e("bytesssssss", "" + bytes);
        byte[] ebytes = Base64.encode(bytes, Base64.DEFAULT);
        Log.e("eBytes", "" + ebytes);
        String image_str = new String(ebytes);
        Log.e("image_str", "" + image_str);
        image_str = image_str.replaceAll("\n", "");
        //Log.e("image_string", "" + image_str);
        return image_str;

        /*Bitmap bitmap = BitmapFactory.decodeFile(path);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = null;
        try {
            System.gc();
            temp = Base64.encodeToString(b, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            b = baos.toByteArray();
            temp = Base64.encodeToString(b, Base64.DEFAULT);
            Log.e("EWN", "Out of memory error catched");
        }
        return temp;*/
    }
}
