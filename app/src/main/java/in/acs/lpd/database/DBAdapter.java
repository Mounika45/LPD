package in.acs.lpd.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;


public class DBAdapter {

    private Context context;
    private static SQLiteDatabase database;
    private static DataBase dbHelper;

    public DBAdapter(Context ctx) throws SQLException, IOException {
        System.out.println("control in DBAdapter");
        this.context = ctx;
    }

    public DBAdapter open() throws SQLException, IOException {

        System.out.println("control in open");

        try {
            Log.i("KAR", "DBAdapter OPEN  ");
            dbHelper = new DataBase(context);
            database = dbHelper.getWritableDatabase();
            database.disableWriteAheadLogging();
            Log.i("KAR", "DBAdapter OPEN  getWritableDatabase");
            try {
                Log.i("KAR", "DBAdapter OPEN creating ");
                dbHelper.createDataBase();
                Log.i("KAR", "DBAdapter OPEN created");
                dbHelper.openDataBase();
                Log.i("KAR", "DBAdapter OPEN opened");
            } catch (IOException e) {
                Log.i("KAR", "DBAdapter OPEN IOException");
                e.printStackTrace();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return this;
    }

    public void close() {
        System.out.println("control in close");
        dbHelper.close();
    }

    public void insertLoginDtls(String userId, String userName, String userPassword, String mandalId, String districtId, String userRoleId) {
        if (!database.isOpen()) {
            try {
                open();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String query = "INSERT INTO userdetails (userId, userName, userPassword, mandalId, districtId, userRoleId) " +
                "VALUES ('" + userId + "','" + userName + "','" + userPassword + "','" + mandalId + "','" + districtId + "', " +
                "'" + userRoleId + "')";
        Log.i("DBA", "insertion query ::" + query);
        database.execSQL(query);
    }

    public int getCheckCount() {
        int id;
        if (!database.isOpen()) {
            try {
                open();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Cursor query = database.rawQuery("SELECT COUNT (*) FROM userdetails", null);
        query.moveToFirst();
        id = query.getInt(0);
        Log.e("userid", "" + id);
        return id;
    }

    public JsonObject getUserDtls() {
        JsonObject userDtls = new JsonObject();
        Cursor query = null;
        if (!database.isOpen()) {
            try {
                open();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            query = database.rawQuery("SELECT * FROM userdetails", null);
            query.moveToFirst();
            userDtls.addProperty("userId", query.getInt(0));
            userDtls.addProperty("userName", query.getString(1));
            userDtls.addProperty("userPassword", query.getString(2));
            userDtls.addProperty("mandalId", query.getString(3));
            userDtls.addProperty("districtId", query.getString(4));
            userDtls.addProperty("userRoleId", query.getString(5));
        } finally {
            // this gets called even if there is an exception somewhere above
            if (query != null)
                query.close();
        }
        return userDtls;
    }

    public void insertVillageDetails(String villageId, String villageMandalId, String villageDistrictId,
                                     String villageCode, String villageName, String villageMandalCode,
                                     String mandalWlCode, String distWlCode) {
        if (!database.isOpen()) {
            try {
                open();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String query = "INSERT INTO m_villages (villageId, villageMandalId, villageDistrictId, villageCode, villageName, villageMandalCode, " +
                "mandalWlCode, distWlCode) " +
                "VALUES ('" + villageId + "','" + villageMandalId + "','" + villageDistrictId + "','" + villageCode + "','" + villageName + "', " +
                "'" + villageMandalCode + "', '" + mandalWlCode + "', '" + distWlCode + "')";
        Log.i("DBA", "insertion query villages ::" + query);
        database.execSQL(query);
    }

    public void deleteVillageList() {
        if (!database.isOpen()) {
            try {
                open();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String query = "DELETE FROM m_villages";
        database.execSQL(query);
        Log.e("DBADAPTERDelVillage", query);
    }

    public JsonArray getVillagesList() {
        JsonArray array = new JsonArray();
        String q;
        if (!database.isOpen()) {
            try {
                open();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        q = "SELECT * FROM m_villages";

        Cursor query = database.rawQuery(q, null);
        while (query.moveToNext()) {
            JsonObject obj = new JsonObject();
            obj.addProperty("villageId", query.getString(0));
            obj.addProperty("villageMandalId", query.getString(1));
            obj.addProperty("villageDistrictId", query.getString(2));
            obj.addProperty("villageCode", query.getString(3));
            obj.addProperty("villageName", query.getString(4));
            obj.addProperty("villageMandalCode", query.getString(5));
            obj.addProperty("mandalWlCode", query.getString(6));
            obj.addProperty("distWlCode", query.getString(7));
            array.add(obj);
        }
        Log.e("Villages_Master", "" + array + "Query::" + q);
        if (array == null) {
            array.add(new JsonObject());
        }
        return array;
    }

    public void insertLECDtls(String userId, String villageCode, String applicationId,
                              String landExtent, String cardNumber, String applicantName,
                              String aadharNumber, String issueDate, String surveyNumber,
                              String mobileNumber,
                              String landId, String totalGroupLandExtent,
                              String cropName, String applicationType,
                              String cocCardNumber, String finYear, String categoryName,
                              String casteName, String totalSanctionedAmount, String villageName,
                              String groupId, String processingId, String sanctionedFrom) {
        if (!database.isOpen()) {
            try {
                open();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String query = "INSERT INTO m_lecCOCDtls (userId, villageCode, applicationId, villageCode, landExtent," +
                "cardNumber, applicantName, aadharNumber, issueDate, surveyNumber, mobileNumber, landId, " +
                "totalGroupLandExtent, cropName, applicationType, cocCardNumber,finYear,categoryName, casteName,totalSanctionedAmount," +
                "villageName, groupId, processingId, sanctionedFrom) " +
                "VALUES ('" + userId + "','" + villageCode + "','" + applicationId + "','" + villageCode + "','" + landExtent + "', " +
                "'" + cardNumber + "', '" + applicantName + "', '" + aadharNumber + "', '" + issueDate + "', '" + surveyNumber + "'" +
                ", '" + mobileNumber + "', '" + landId + "', '" + totalGroupLandExtent + "', '" + cropName + "', '" + applicationType + "'," +
                "'" + cocCardNumber + "','" + finYear + "','" + categoryName + "','" + casteName + "','" + totalSanctionedAmount + "'" +
                ",'" + villageName + "','" + groupId + "' ,'" + processingId + "' ,'" + sanctionedFrom + "')";
        Log.i("DBA", "insertion query " + applicationType + " Master ::" + query);
        database.execSQL(query);
    }

    public void deleteLECDtls(String leccoctype) {
        if (!database.isOpen()) {
            try {
                open();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String query = "DELETE FROM m_lecCOCDtls WHERE applicationType = '" + leccoctype + "'";
        database.execSQL(query);
        Log.e("DBADel:m_lecCOCDtls", query);
    }

    public JsonArray getLECCOCMaster(String applicationType, String userid) {
        JsonArray array = new JsonArray();
        String q;
        if (!database.isOpen()) {
            try {
                open();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        q = "SELECT * FROM m_lecCOCDtls WHERE applicationType = '" + applicationType + "' AND userId = '" + userid + "' AND rejected_sanctioned IS NULL";

        Cursor query = database.rawQuery(q, null);
        while (query.moveToNext()) {
            JsonObject obj = new JsonObject();
            obj.addProperty("id", query.getString(0));
            obj.addProperty("userId", query.getString(1));
            obj.addProperty("villageCode", query.getString(2));
            obj.addProperty("applicationId", query.getString(3));
            obj.addProperty("landExtent", query.getString(4));
            obj.addProperty("cardNumber", query.getString(5));
            obj.addProperty("applicantName", query.getString(6));
            obj.addProperty("aadharNumber", query.getString(7));
            obj.addProperty("issueDate", query.getString(8));
            obj.addProperty("surveyNumber", query.getString(9));
            obj.addProperty("mobileNumber", query.getString(10));
            obj.addProperty("landId", query.getString(11));
            obj.addProperty("totalGroupLandExtent", query.getString(12));
            obj.addProperty("cropName", query.getString(13));
            obj.addProperty("applicationType", query.getString(14));
            obj.addProperty("cocCardNumber", query.getString(15));
            array.add(obj);
        }
        Log.e("LECCOC_Master", "" + array + "Query::" + q);
        if (array == null) {
            array.add(new JsonObject());
        }
        return array;
    }

    public void insertCropDtls(String cropNameId, String cropName, String cropStatus, String leccoctype) {
        if (!database.isOpen()) {
            try {
                open();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String query = "INSERT INTO m_crops (cropNameId, cropName, cropStatus, leccoctype) " +
                "VALUES ('" + cropNameId + "','" + cropName + "','" + cropStatus + "', '" + leccoctype + "')";
        Log.i("DBA", "insertion query Crops Master ::" + query);
        database.execSQL(query);
    }

    public void deleteCropDtls(String leccoctype) {
        if (!database.isOpen()) {
            try {
                open();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String query = "DELETE FROM m_crops WHERE leccoctype = '" + leccoctype + "'";
        database.execSQL(query);
        Log.e("DBADAPTERDel:m_crops", query);
    }

    public void insertBankDtls(String bankId, String bankName, String villageCode, String leccoctype) {
        if (!database.isOpen()) {
            try {
                open();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String query = "INSERT INTO m_banks (bankId, bankName, villageCode, leccoctype) " +
                "VALUES ('" + bankId + "','" + bankName + "', '" + villageCode + "', '" + leccoctype + "')";
        Log.i("DBA", "insertion query banks Master ::" + query);
        database.execSQL(query);
    }

    public void deleteBankDtls(String leccoctype) {
        if (!database.isOpen()) {
            try {
                open();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String query = "DELETE FROM m_banks WHERE leccoctype = '" + leccoctype + "'";
        database.execSQL(query);
        Log.e("DBADAPTERDel:m_banks", query);
    }

    public void insertBranchesBank(String branchId, String bankId, String branchName, String ifscCode, String leccoctype) {
        if (!database.isOpen()) {
            try {
                open();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String query = "INSERT INTO m_branches (branchId, bankId, branchName, ifscCode, leccoctype) " +
                "VALUES ('" + branchId + "','" + bankId + "','" + branchName + "','" + ifscCode + "', '" + leccoctype + "')";
        Log.i("DBA", "insertion query Crops Master ::" + query);
        database.execSQL(query);
    }

    public void deleteBranchesBank(String leccoctype) {
        if (!database.isOpen()) {
            try {
                open();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String query = "DELETE FROM m_branches WHERE leccoctype = '" + leccoctype + "'";
        database.execSQL(query);
        Log.e("DBADAPTERDel:m_branches", query);
    }

    public JsonArray getCropDtls(String survey_no, String leccoctype) {
        JsonArray array = new JsonArray();
        String q;
        if (!database.isOpen()) {
            try {
                open();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        q = "SELECT * FROM m_crops WHERE leccoctype = '" + leccoctype + "'";

        Cursor query = database.rawQuery(q, null);
        while (query.moveToNext()) {
            JsonObject obj = new JsonObject();
            obj.addProperty("cropNameId", query.getString(0));
            obj.addProperty("cropName", query.getString(1));
            obj.addProperty("cropStatus", query.getString(2));
            array.add(obj);
        }
        Log.e("Crop_Master", "" + array + "Query::" + q);
        if (array == null) {
            array.add(new JsonObject());
        }
        return array;
    }

    public JsonArray getCropDtlsGS() {
        JsonArray array = new JsonArray();
        String q;
        if (!database.isOpen()) {
            try {
                open();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        q = "SELECT * FROM m_crops";

        Cursor query = database.rawQuery(q, null);
        while (query.moveToNext()) {
            JsonObject obj = new JsonObject();
            obj.addProperty("cropNameId", query.getString(0));
            obj.addProperty("cropName", query.getString(1));
            obj.addProperty("cropStatus", query.getString(2));
            array.add(obj);
        }
        Log.e("Crop_Master", "" + array + "Query::" + q);
        if (array == null) {
            array.add(new JsonObject());
        }
        return array;
    }


    public JsonArray getBankDtls(String villagecode, String leccoctype) {
        JsonArray array = new JsonArray();
        String q;
        if (!database.isOpen()) {
            try {
                open();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        q = "SELECT * FROM m_banks WHERE villagecode = '" + villagecode + "' AND leccoctype = '" + leccoctype + "'";

        Cursor query = database.rawQuery(q, null);
        while (query.moveToNext()) {
            JsonObject obj = new JsonObject();
            obj.addProperty("bankId", query.getString(0));
            obj.addProperty("bankName", query.getString(1));
            obj.addProperty("villageCode", query.getString(2));
            array.add(obj);
        }
        Log.e("Bank_Master", "" + array + "Query::" + q);
        if (array == null) {
            array.add(new JsonObject());
        }
        return array;
    }

    public JsonArray getBranchDtls(String bankId, String from) {
        JsonArray array = new JsonArray();
        String q;
        if (!database.isOpen()) {
            try {
                open();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        q = "SELECT * FROM m_branches WHERE bankId = '" + bankId + "' AND leccoctype = '" + from + "'";

        Cursor query = database.rawQuery(q, null);
        while (query.moveToNext()) {
            JsonObject obj = new JsonObject();
            obj.addProperty("branchId", query.getString(0));
            obj.addProperty("bankId", query.getString(1));
            obj.addProperty("branchName", query.getString(2));
            obj.addProperty("ifscCode", query.getString(3));
            array.add(obj);
        }
        Log.e("Branch_Master", "" + array + "Query::" + q);
        if (array == null) {
            array.add(new JsonObject());
        }
        return array;
    }

    public void insertSOFDtls(String id, String cropId, String finYearId, String amount, String type, String leccoctype) {
        if (!database.isOpen()) {
            try {
                open();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String query = "INSERT INTO m_sof (id, cropId, finYearId, amount, type, leccoctype) " +
                "VALUES ('" + id + "','" + cropId + "', '" + finYearId + "','" + amount + "','" + type + "','" + leccoctype + "')";
        Log.i("DBA", "insertion query SOF Master ::" + query);
        database.execSQL(query);
    }

    public void deleteSOFDtls(String leccoctype) {
        if (!database.isOpen()) {
            try {
                open();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String query = "DELETE FROM m_sof WHERE leccoctype = '" + leccoctype + "'";
        database.execSQL(query);
        Log.e("DBADAPTERDel:m_sof", query);
    }

    public JsonArray getSOFData(String cropid, String leccoctype) {
        JsonArray array = new JsonArray();
        String q;
        if (!database.isOpen()) {
            try {
                open();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        q = "SELECT * FROM m_sof WHERE cropId = '" + cropid + "' AND leccoctype = '" + leccoctype + "'";

        Cursor query = database.rawQuery(q, null);
        while (query.moveToNext()) {
            JsonObject obj = new JsonObject();
            obj.addProperty("id", query.getString(0));
            obj.addProperty("cropId", query.getString(1));
            obj.addProperty("finYearId", query.getString(2));
            obj.addProperty("amount", query.getString(3));
            obj.addProperty("type", query.getString(4));
            obj.addProperty("leccoctype", query.getString(5));
            array.add(obj);
        }
        Log.e("Branch_Master", "" + array + "Query::" + q);
        if (array == null) {
            array.add(new JsonObject());
        }
        return array;
    }

    public void insertLECCOCData(String applicationId, String cardNumber,
                                 String from_lecorcoc, String mobile_no, String landExtent,
                                 String survey_no, String app_type, String cropId, String bank_id,
                                 String branchId, String ifsccode, String account_no, String loan_amount,
                                 String loan_issue_date, String loan_due_date, String villageCode,
                                 String applicantName, String aadharNumber, String mandalId,
                                 String districtId, String userName, String landId, String imagePath,
                                 String cocCardNumber, String app_sanction, int filePath, String filetype,
                                 String scaleOfFinance) {

        if (!database.isOpen()) {
            try {
                open();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String query = "INSERT INTO t_leccoc_sanction (applicationId,cardNumber,from_lecorcoc,mobile_no," +
                "landExtent,survey_no,app_type,cropId,bank_id,branchId,ifsccode,account_no,loan_amount," +
                "loan_issue_date,loan_due_date,villageCode ,applicantName, aadharNumber, mandalId," +
                "districtId,userName,landId,imagePath,cocCardNumber, app_sanction, filePath, filetype," +
                "scaleOfFinance) VALUES ('" + applicationId + "' , '" + cardNumber + "', '" + from_lecorcoc + "', '" + mobile_no + "'" +
                ", '" + landExtent + "', '" + survey_no + "', '" + app_type + "', '" + cropId + "', '" + bank_id + "', '" + branchId + "', '" + ifsccode + "', '" + account_no + "'" +
                ", '" + loan_amount + "', '" + loan_issue_date + "', '" + loan_due_date + "', '" + villageCode + "', '" + applicantName + "', '" + aadharNumber + "'" +
                ", '" + mandalId + "', '" + districtId + "', '" + userName + "', '" + landId + "', '" + imagePath + "','" + cocCardNumber + "'" +
                ", '" + app_sanction + "', '" + filePath + "','" + filetype + "', '" + scaleOfFinance + "')";
        Log.e("DBA", "insertion query t_leccoc_sanction Master ::" + query);
        database.execSQL(query);
    }

    public void deleteRejectionReasons() {
        if (!database.isOpen()) {
            try {
                open();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String query = "DELETE FROM m_rejection_reasons";
        database.execSQL(query);
        Log.e("DBADel:m_rejection_reas", query);
    }

    public void insertRejectionReasons(String reasonId, String reasonName, String reasonActive, String createdOn) {
        if (!database.isOpen()) {
            try {
                open();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String query = "INSERT INTO m_rejection_reasons (reasonId,reasonName,reasonActive,createdOn)" +
                " VALUES ('" + reasonId + "' , '" + reasonName + "', '" + reasonActive + "', '" + createdOn + "')";
        Log.e("DBA", "insertion query m_rejection_reasons ::" + query);
        database.execSQL(query);
    }

    public JsonArray getRejectionReasons() {
        JsonArray array = new JsonArray();
        String q;
        if (!database.isOpen()) {
            try {
                open();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        q = "SELECT * FROM m_rejection_reasons";

        Cursor query = database.rawQuery(q, null);
        while (query.moveToNext()) {
            JsonObject obj = new JsonObject();
            obj.addProperty("reasonId", query.getString(0));
            obj.addProperty("reasonName", query.getString(1));
            obj.addProperty("reasonActive", query.getString(2));
            obj.addProperty("createdOn", query.getString(3));
            array.add(obj);
        }
        Log.e("m_rejection_reasons", "" + array + "Query::" + q);
        if (array == null) {
            array.add(new JsonObject());
        }
        return array;
    }

    public void insertRejectData(String mobile_no, String landExtent, String surveyNumber, String from_leccoc,
                                 String bank_id, String branchId, String ifsccode, String reason_reject, String villageCode, String applicantName,
                                 String aadharNumber, String mandalId,
                                 String districtId, String userName, String cardNumber,
                                 String landId, String rejected, String applicationId, String cocCardNumber) {
        if (!database.isOpen()) {
            try {
                open();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String query = "INSERT INTO t_leccoc_rejection (mobile_no,landExtent,surveyNumber,from_leccoc," +
                "bank_id,branchId,ifsccode,reason_reject,villageCode,applicantName,aadharNumber, mandalId," +
                "districtId,userName,cardNumber,landId, rejected, applicationId, cocCardNumber) VALUES ('" + mobile_no + "' , " +
                "'" + landExtent + "', '" + surveyNumber + "', '" + from_leccoc + "'" +
                ", '" + bank_id + "', '" + branchId + "', '" + ifsccode + "', '" + reason_reject + "'," +
                " '" + villageCode + "', '" + applicantName + "'" +
                ", '" + aadharNumber + "', '" + mandalId + "', '" + districtId + "', '" + userName + "'," +
                " '" + cardNumber + "', '" + landId + "','" + rejected + "', '" + applicationId + "', '" + cocCardNumber + "')";
        Log.e("DBA", "insertion query t_leccoc_rejection ::" + query);
        database.execSQL(query);
    }

    public void updateLECCOCData(String id) {
        if (!database.isOpen()) {
            try {
                open();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String query = "UPDATE m_lecCOCDtls SET rejected_sanctioned = 'Y' WHERE id='" + id + "'";
        database.execSQL(query);
        Log.e("IN ADAPTER", "Update Successfully" + query);
    }

    public JsonArray getLECCOCSanctionedData(String userName) {
        JsonArray array = new JsonArray();
        String q;
        if (!database.isOpen()) {
            try {
                open();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        q = "SELECT * FROM t_leccoc_sanction WHERE userName = '" + userName + "'";

        Cursor query = database.rawQuery(q, null);
        while (query.moveToNext()) {
            JsonObject obj = new JsonObject();
            obj.addProperty("id", query.getString(0));
            obj.addProperty("mobileNumber", query.getString(4));
            obj.addProperty("surveyNumber", query.getString(6));
            obj.addProperty("appType", query.getString(7));
            obj.addProperty("sanctionedFrom", query.getString(25));
            obj.addProperty("landExtent", query.getString(5));
            obj.addProperty("cropNameId", query.getString(8));
            obj.addProperty("bankId", query.getString(9));
            obj.addProperty("branchId", query.getString(10));
            obj.addProperty("ifscCode", query.getString(11));
            obj.addProperty("loanAccountNumber", query.getString(12));
            obj.addProperty("loanAmount", query.getString(13));
            obj.addProperty("loanIssuedDate", query.getString(14));
            obj.addProperty("repaymentDate", query.getString(15));
            obj.addProperty("villageId", query.getString(16));
            obj.addProperty("tenantFarmerName", query.getString(17));
            obj.addProperty("tenantAadhaarNumber", query.getString(18));
            obj.addProperty("mandalId", query.getString(19));
            obj.addProperty("districtId", query.getString(20));
            obj.addProperty("createdBy", query.getString(21));
            obj.addProperty("applicationId", query.getString(1));
            obj.addProperty("landId", query.getString(22));
            obj.addProperty("cocApplicationId", query.getString(24));
            obj.addProperty("cardNumber", query.getString(2));
            obj.addProperty("docIds", query.getString(26));
            obj.addProperty("loanStatus", "Sanctioned");
            obj.addProperty("scaleOfFinance", query.getString(28));
            array.add(obj);
        }
        Log.e("t_leccoc_sanction", "" + array + "Query::" + q);
        if (array == null) {
            array.add(new JsonObject());
        }
        return array;
    }

    public JsonArray getLECCOCRejectedData(String userName) {
        JsonArray array = new JsonArray();
        String q;
        if (!database.isOpen()) {
            try {
                open();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        q = "SELECT * FROM t_leccoc_rejection WHERE userName = '" + userName + "'";

        Cursor query = database.rawQuery(q, null);
        while (query.moveToNext()) {
            JsonObject obj = new JsonObject();
            obj.addProperty("id", query.getString(0));
            obj.addProperty("mobileNumber", query.getString(1));
            obj.addProperty("surveyNumber", query.getString(3));
            obj.addProperty("sanctionedFrom", query.getString(4));
            obj.addProperty("bankId", query.getString(5));
            obj.addProperty("branchId", query.getString(6));
            obj.addProperty("ifscCode", query.getString(7));
            obj.addProperty("rejectionReasonId", query.getString(8));
            obj.addProperty("villageId", query.getString(9));
            obj.addProperty("landExtent", query.getString(2));
            obj.addProperty("tenantFarmerName", query.getString(10));
            obj.addProperty("tenantAadhaarNumber", query.getString(11));
            obj.addProperty("mandalId", query.getString(12));
            obj.addProperty("createdBy", query.getString(14));
            obj.addProperty("applicationId", query.getString(18));
            obj.addProperty("landId", query.getString(16));
            obj.addProperty("cardNumber", query.getString(15));
            obj.addProperty("loanStatus", "Rejected");
            obj.addProperty("cocApplicationId", query.getString(19));
            array.add(obj);
        }
        Log.e("t_leccoc_rejection", "" + array + "Query::" + q);
        if (array == null) {
            array.add(new JsonObject());
        }
        return array;
    }

    public void deleteUserDetails() {
        if (!database.isOpen()) {
            try {
                open();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String query = "DELETE FROM userdetails";
        database.execSQL(query);
        Log.e("DBADel:userdetails", query);
    }

    public int insertFileForSanction(String imagename, String imageextension, String userId,
                                     String cocCardNumber, String applicationId,
                                     String from_leccoc, String imagePath) {
        int i;
        if (!database.isOpen()) {
            try {
                open();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ContentValues newEntry = new ContentValues();
        newEntry.put("imagename", imagename);
        newEntry.put("imageextension", imageextension);
        newEntry.put("userId", userId);
        newEntry.put("cocCardNumber", cocCardNumber);
        newEntry.put("applicationId", applicationId);
        newEntry.put("from_leccoc", from_leccoc);
        newEntry.put("imagePath", imagePath);

        i = (int) database.insertOrThrow("t_files", null, newEntry);
        Log.e("iDB", "" + i);
        Log.e("queryDB", "" + newEntry);
        return i;
    }


    public JsonArray getFiles(String userId) {
        JsonArray array = new JsonArray();
        String q;
        if (!database.isOpen()) {
            try {
                open();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        q = "SELECT * FROM t_files WHERE userId = '" + userId + "'";

        Cursor query = database.rawQuery(q, null);
        while (query.moveToNext()) {
            JsonObject obj = new JsonObject();
            obj.addProperty("id", query.getString(0));
            obj.addProperty("enclosureFileName", query.getString(1));
            obj.addProperty("enclosureFileType", query.getString(2));
            obj.addProperty("enclosureCreatedBy", query.getString(3));
            obj.addProperty("applicationNumber", query.getString(4));
            obj.addProperty("applicationId", query.getString(5));
            obj.addProperty("enclosureFileUploadFrom", query.getString(6));
            obj.addProperty("imagePath", query.getString(7));
            array.add(obj);
        }
        Log.e("t_files", "" + array + "Query::" + q);
        if (array == null) {
            array.add(new JsonObject());
        }
        return array;
    }

    public void updateLecCocSanctionData(String id, String docids) {
        if (!database.isOpen()) {
            try {
                open();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String query = "UPDATE t_leccoc_sanction SET filePath = '" + docids + "' WHERE filePath='" + id + "'";
        database.execSQL(query);
        Log.e("IN ADAPTER", "Update Successfully" + query);
    }

    public void deleteFiles(String id) {
        if (!database.isOpen()) {
            try {
                open();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String query = "DELETE FROM t_files WHERE id = '" + id + "'";
        database.execSQL(query);
        Log.e("DBADel:t_files", query);
    }

    public void deleteSanctionData(String id) {
        if (!database.isOpen()) {
            try {
                open();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String query = "DELETE FROM t_leccoc_sanction WHERE id = '" + id + "'";
        database.execSQL(query);
        Log.e("DBADel:t_leccoc_san", query);
    }

    public void deleteRejectionData(String id) {
        if (!database.isOpen()) {
            try {
                open();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String query = "DELETE FROM t_leccoc_rejection WHERE id = '" + id + "'";
        database.execSQL(query);
        Log.e("DBADel:t_leccoc_reject", query);
    }

    public void deleteGroupSanction(String mandalId) {
        if (!database.isOpen()) {
            try {
                open();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String query = "DELETE FROM m_groupMembersData WHERE mandalId = '" + mandalId + "'";
        database.execSQL(query);
        Log.e("DBDel:m_groupMemberData", query);
    }

    public int insertGroupSanction(String userId, String mandalId, String groupId,
                                   String groupName, String groupMembersCount,
                                   String totalGroupLandExtent, String groupHeadName,
                                   String groupHeadMobileNo, String groupCreatedBy,
                                   String groupRelatedTo, String totalSanctionedAmount) {
        int i;
        if (!database.isOpen()) {
            try {
                open();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ContentValues newEntry = new ContentValues();
        newEntry.put("userId", userId);
        newEntry.put("mandalId", mandalId);
        newEntry.put("groupId", groupId);
        newEntry.put("groupName", groupName);
        newEntry.put("groupMembersCount", groupMembersCount);
        newEntry.put("totalGroupLandExtent", totalGroupLandExtent);
        newEntry.put("groupHeadName", groupHeadName);
        newEntry.put("groupHeadMobileNo", groupHeadMobileNo);
        newEntry.put("groupCreatedBy", groupCreatedBy);
        newEntry.put("groupRelatedTo", groupRelatedTo);
        newEntry.put("totalSanctionedAmount", totalSanctionedAmount);

        i = (int) database.insertOrThrow("m_groupMembersData", null, newEntry);
        Log.e("iDB", "" + i);
        Log.e("queryDB", "" + newEntry);
        return i;
    }

    public JsonArray getGSMembersData(String mandalId, String userId) {
        JsonArray array = new JsonArray();
        String q;
        if (!database.isOpen()) {
            try {
                open();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        q = "SELECT * FROM m_groupMembersData WHERE mandalId = '" + mandalId + "' AND userId = '" + userId + "'";

        Cursor query = database.rawQuery(q, null);
        while (query.moveToNext()) {
            JsonObject obj = new JsonObject();
            obj.addProperty("id", query.getString(0));
            obj.addProperty("userId", query.getString(1));
            obj.addProperty("mandalId", query.getString(2));
            obj.addProperty("groupId", query.getString(3));
            obj.addProperty("groupName", query.getString(4));
            obj.addProperty("groupMembersCount", query.getString(5));
            obj.addProperty("totalGroupLandExtent", query.getString(6));
            obj.addProperty("groupHeadName", query.getString(7));
            obj.addProperty("groupHeadMobileNo", query.getString(8));
            obj.addProperty("groupCreatedBy", query.getString(9));
            obj.addProperty("groupRelatedTo", query.getString(10));
            obj.addProperty("totalSanctionedAmount", query.getString(11));
            array.add(obj);
        }
        Log.e("m_groupMembersData", "" + array + "Query::" + q);
        if (array == null) {
            array.add(new JsonObject());
        }
        return array;
    }

    public JsonArray getGSFarmersData(String applicationType, String userid, String groupId) {
        JsonArray array = new JsonArray();
        String q;
        if (!database.isOpen()) {
            try {
                open();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        q = "SELECT * FROM m_lecCOCDtls WHERE applicationType = '" + applicationType + "' AND userId = '" + userid + "'" +
                " AND groupId = '" + groupId + "' AND rejected_sanctioned IS NULL";

        Cursor query = database.rawQuery(q, null);
        while (query.moveToNext()) {
            JsonObject obj = new JsonObject();
            obj.addProperty("id", query.getString(0));
            obj.addProperty("userId", query.getString(1));
            obj.addProperty("villageCode", query.getString(2));
            obj.addProperty("applicationId", query.getString(3));
            obj.addProperty("landExtent", query.getString(4));
            obj.addProperty("cardNumber", query.getString(5));
            obj.addProperty("applicantName", query.getString(6));
            obj.addProperty("aadharNumber", query.getString(7));
            obj.addProperty("issueDate", query.getString(8));
            obj.addProperty("surveyNumber", query.getString(9));
            obj.addProperty("mobileNumber", query.getString(10));
            obj.addProperty("landId", query.getString(11));
            obj.addProperty("totalGroupLandExtent", query.getString(12));
            obj.addProperty("cropName", query.getString(13));
            obj.addProperty("applicationType", query.getString(14));
            obj.addProperty("cocCardNumber", query.getString(15));
            obj.addProperty("finYear", query.getString(16));
            obj.addProperty("categoryName", query.getString(17));
            obj.addProperty("casteName", query.getString(18));
            obj.addProperty("totalSanctionedAmount", query.getString(19));
            obj.addProperty("rejected_sanctioned", query.getString(20));
            obj.addProperty("villageName", query.getString(21));
            obj.addProperty("groupId", query.getString(22));
            obj.addProperty("processingId", query.getString(23));
            obj.addProperty("sanctionedFrom", query.getString(24));
            array.add(obj);
        }
        Log.e("LECCOC_Master", "" + array + "Query::" + q);
        if (array == null) {
            array.add(new JsonObject());
        }
        return array;
    }
}