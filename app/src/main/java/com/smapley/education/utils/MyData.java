package com.smapley.education.utils;

import android.text.format.Time;

import com.smapley.education.fragment.UpPicFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Smapley on 2015/4/12.
 */
public class MyData {
    public final static String BASE_URL = "http://120.25.208.188/schoolpush/";
    public final static String URL_REG = BASE_URL + "reg.php";
    public final static String URL_PROVINCE = BASE_URL + "getCityList.php";
    public final static String URL_SCHOOL = BASE_URL + "getSchoolList.php";
    public final static String URL_GETCHILD = BASE_URL + "getChildren.php";
    public final static String URL_GETSTULIST = BASE_URL + "getStuList.php";
    public final static String URL_GETBOOKLIST = BASE_URL + "getBookList.php";
    public final static String URL_GETEXAMSUBJECT = BASE_URL + "getExamSubject.php";
    public final static String URL_ADDGRADE = BASE_URL + "addExamSubject.php";
    public final static String URL_GETNEWMSG = BASE_URL + "getNewMsg.php";
    public final static String URL_GETSUBJECTLIST = BASE_URL + "getSubjectList.php";
    public final static String URL_GETSTINFO = BASE_URL + "getSTInfo.php";
    public final static String URL_UPDATESTINFO = BASE_URL + "updateSTInfo.php";
    public final static String URL_SETPROV = BASE_URL + "setProv.php";
    public final static String URL_ADDSTU = BASE_URL + "addStu.php";
    public final static String URL_GETMSGTITLELIST = BASE_URL + "getMsgTitleList.php";
    public final static String URL_GETSTUTABLE = BASE_URL + "getStuTable.php";
    public final static String URL_GETSTUTABLEEITHSTUPHONE = BASE_URL + "getStuTableWithStuPhone.php";
    public final static String URL_ADDMSG = BASE_URL + "addMsg.php";
    public final static String URL_UPDATAEXAMSUBJECT = BASE_URL + "updateExamSubject.php";
    public final static String URL_GETEXAMLIST = BASE_URL + "getExamList.php";
    public final static String URL_GETTIMETABLE = BASE_URL + "getTimetable.php";
    public final static String URL_UPDATATEATIMETABLE = BASE_URL + "updateTeaTimetable.php";
    public final static String URL_ADDEXAM = BASE_URL + "addExam.php";
    public final static String URL_ADDTEASUBJECT = BASE_URL + "addTeaSubject.php";
    public final static String URL_DELTEASUBJECTWITHTPHONE = BASE_URL + "delTeaSubjectWithTphone.php";
    public final static String URL_GETSCORELIST = BASE_URL + "getScoreList.php";
    public final static String URL_GETTEASUBJECTLIST2 = BASE_URL + "getTeaSubjectList2.php";
    public final static String URL_GETZUOYE = BASE_URL + "getZuoye.php";
    public final static String URL_GETZUOYELIST = BASE_URL + "getZuoyeList.php";
    public final static String URL_GETSTULIST2 = BASE_URL + "getStuList2.php";
    public final static String URL_BANGSTU = BASE_URL + "bangStu.php";
    public final static String URL_GETSTUTABLEROW = BASE_URL + "getStuTableRow.php";
    public final static String URL_SETSTAT = BASE_URL + "setStat.php";
    public final static String URL_SETSTUPHONE = BASE_URL + "setStuPhone.php";
    public final static String URL_ADDSCORE = BASE_URL + "addScore.php";
    public final static String URL_DELSTU = BASE_URL + "delStu.php";
    public final static String URL_UPDATESCOREBATCH = BASE_URL + "updateScoreBatch.php";
    public final static String URL_ADDTEATIMETABLE = BASE_URL + "addTeaTimetable.php";
    public final static String URL_DELMSG = BASE_URL + "delMsg.php";
    public final static String URL_DELEXAM = BASE_URL + "delExam.php";
    public final static String URL_GETEXAMPGOTO = BASE_URL + "getExamPhoto.php";
    public final static String URL_SENDPHOTO = BASE_URL + "sendPhoto.php";
    public final static String URL_SENDMSG = BASE_URL + "batchSMS.php";
    public final static String URL_LOGOUT = BASE_URL + "logout.php";

    public static final String CACHE_PIC = "/Education/ImageCache/";
    public static final String URL_FILE = "http://120.25.208.188/schoolpush/upload/";

    public final static String SP_USER = "user";

    public static List<String> GRADELIST = new ArrayList<>();
    public static String GRADENAME;
    public static int ADDCOURSETYPE;
    public static String SYLL;
    public static int SYLL_WEEK;
    public static int SYLL_NWEEK;
    public static int SYLL_DAY;
    public static int SYLL_NDAY;
    public static int SYLL_BACK;
    public static String SYLL_SELEC;

    public static int EID;
    public static String EXAMNAME;

    public static int SID;

    public static int UTYPE;
    public static Boolean UTYPECHANGED = false;
    public static int PROV;

    public static String STUNAME;
    public static int STUGRADE;
    public static Boolean FIRSTADDGRADE = false;

    public static int[] PATHVALUE;

    public static int NOWCHILDREN = 1;

    public static int SRC;
    public static String SUBJECT = "";

    public static String BOUNDPHONE;

    public static String BACKSTRING;

    public static UpPicFragment.GetPhoto getPhoto;

    public static int imagechangeed = 0;

    /**
     * 获取服务器加密码
     * key
     *
     * @return
     */
    public static int getKey() {
        int key = 0;
        key = 1 + (int) (Math.random() * 999);
        Time t = new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料。  
        t.setToNow(); // 取得系统时间。 
        int date = t.monthDay;
        return key * 789 * date;
    }
}
