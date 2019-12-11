package in.acs.lpd;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import in.acs.lpd.Services.URLs;
import in.acs.lpd.Services.utils;
import in.acs.lpd.database.DBAdapter;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class Login extends AppCompatActivity {
    AutoCompleteTextView username, password;
    utils util = new utils();
    DBAdapter db;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progress = new ProgressDialog(this);
        progress.setTitle("Contacting Server");
        progress.setMessage("Please Wait.......");
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setIndeterminate(true);
        progress.setCancelable(false);

        try {
            db = new DBAdapter(this);
            db.open();
        } catch (IOException e) {
            e.printStackTrace();
        }

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);

        checkLogin();
    }

    private void checkLogin() {
        int usercount = db.getCheckCount();
        if (usercount == 0) {
            Snackbar snackbar = Snackbar.make(getWindow().getDecorView().getRootView(), "Please Login!!!", Snackbar.LENGTH_SHORT);
            snackbar.show();
        } else if (usercount > 0) {
            Intent i = new Intent(Login.this, Dashboard.class);
            startActivity(i);
            finish();
        }
    }

    public void goLogin(View view) {
        if (!username.getText().toString().equalsIgnoreCase("") &&
                !password.getText().toString().equalsIgnoreCase("")) {
            callLoginService(view);
        } else {
            Snackbar snackbar1 = Snackbar.make(view, "Please check your Username and Password!", Snackbar.LENGTH_SHORT);
            snackbar1.show();
        }
    }

    public String md5(String s) {
        String password = null;
        MessageDigest mdEnc;
        try {
            mdEnc = MessageDigest.getInstance("MD5");
            mdEnc.update(s.getBytes(), 0, s.length());
            s = new BigInteger(1, mdEnc.digest()).toString(16);
            while (s.length() < 32) {
                s = "0" + s;
            }
            password = s;
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
        return password;
    }

    public static String saltPasswordEncoder(String password) throws NoSuchAlgorithmException {
        //String password = "b";
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashInBytes = null;
        try {
            System.out.println(" password:::" + password);

            hashInBytes = md.digest(password.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            //Logger.getLogger(PasswordEncoder.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }

        // bytes to hex
        StringBuilder sb = new StringBuilder();
        for (byte b : hashInBytes) {
            sb.append(String.format("%02x", b));
        }
        String finalstat = sb.toString() + "sharan";
        // System.out.println(sb.toString());

        // 2ndtime
        byte[] hashInBytes1 = null;
        try {
            hashInBytes1 = md.digest(finalstat.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            //Logger.getLogger(PasswordEncoder.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
        StringBuilder sb1 = new StringBuilder();
        for (byte b1 : hashInBytes1) {
            sb1.append(String.format("%02x", b1));
        }
        System.out.println("encrypt code:::" + sb1.toString());
        //String finalstat =sb1.toString()+"sharan";

        return sb1.toString();
    }

    private void callLoginService(final View view) {
        progress.show();
        URLs ul = new URLs();
        final String url = "http://" + ul.ip + ":" + ul.port + "/" + ul.url + "/users";

        String password_text = password.getText().toString().trim();

        JsonObject obj = new JsonObject();
        JsonObject sobj = new JsonObject();
        sobj.addProperty("userName", username.getText().toString().trim());
        try {
            String password = "1234" + saltPasswordEncoder(password_text) + "000";
            sobj.addProperty("password", password);
            sobj.addProperty("ePantaPassword", password_text);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        obj.add("user", sobj);

        util.getBaseClassService(url, null).getTokenForLogin(obj, new Callback<JsonObject>() {
                    @Override
                    public void success(JsonObject jsonObject, Response response) {
                        progress.dismiss();
                        if (response.getStatus() == 200) {
                            // if (!jsonObject.has("session")) {
                            JsonObject session = jsonObject.get("session").getAsJsonObject();
                            String token = session.get("token").getAsString();
                            Log.e("tokennn", "" + token);
                            callLoginDetailsService(token, view);
                            //   }
                        } else {
                            Snackbar snackbar1 = Snackbar.make(view, "Something went wrong, try again later!", Snackbar.LENGTH_SHORT);
                            snackbar1.show();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        progress.dismiss();
                        Snackbar snackbar1 = Snackbar.make(view, "Please check your Username and Password!", Snackbar.LENGTH_SHORT);
                        snackbar1.show();
                    }
                }
        );
    }

    private void callLoginDetailsService(String token, final View view) {
        progress.show();
        URLs ul = new URLs();
        final String url = "http://" + ul.ip + ":" + ul.port + "/" + ul.url + "/users/getDataByToken/" + token + "/" + username.getText().toString() + "/" + password.getText().toString();
        Log.e("url", "" + url);
        util.getBaseClassService(url, null).Login(new Callback<JsonObject>() {
                                                      @Override
                                                      public void success(JsonObject jsonObject, Response response) {
                                                          progress.dismiss();
                                                          Log.i("KAR", "Login_Button success" + jsonObject);
                                                          if (response.getStatus() == 200) {
                                                              Log.e("jsonLoginDtls", "" + jsonObject);
                                                              String userId, userPassword;
                                                              JsonObject account = jsonObject.get("session").getAsJsonObject().get("account").getAsJsonObject();
                                                              Log.e("roleiddddd", "" + account.get("userRoleId").getAsString());
                                                              String userRole = account.get("userRoleId").getAsString();
                                                              if (account.get("userRoleId").getAsString().equalsIgnoreCase("2")) {
                                                                  userId = username.getText().toString().substring(0, 5);
                                                                  userPassword = password.getText().toString();

                                                                  utils.workForVillages = account.get("workForVillages").getAsString();
                                                              } else {
                                                                  userId = account.get("userId").getAsString();
                                                                  userPassword = account.get("userPassword").getAsString();
                                                              }
                                                              db.insertLoginDtls(userId, account.get("userName").getAsString(),
                                                                      userPassword, account.get("mandalId").getAsString(),
                                                                      account.get("districtId").getAsString(),
                                                                      account.get("userRoleId").getAsString());
                                                              Intent i = new Intent(Login.this, Dashboard.class);
                                                              startActivity(i);
                                                          } else {
                                                              Snackbar snackbar1 = Snackbar.make(view, "Something went wrong, try again later!", Snackbar.LENGTH_SHORT);
                                                              snackbar1.show();
                                                          }
                                                      }

                                                      @Override
                                                      public void failure(RetrofitError error) {
                                                          progress.dismiss();
                                                          Snackbar snackbar1 = Snackbar.make(view, "Please check your Username and Password!", Snackbar.LENGTH_SHORT);
                                                          snackbar1.show();
                                                      }
                                                  }
        );
    }
}
