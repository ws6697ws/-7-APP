package com.example.myesv6;

import android.content.Context;
import android.content.SharedPreferences;


import com.example.model.Moment;
import com.example.model.ResponseObj;
import com.example.model.Score;
import com.example.model.SimpleData;
import com.example.model.Student;
import com.example.model.Syllabus;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orm.SugarContext;
import com.orm.SugarDb;
import com.orm.SugarRecord;
import com.orm.util.NamingHelper;
import com.orm.util.ReflectionUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Jay on 2016/1/2 0002.
 */
public class APIManager {
    private static final String HOST_NAME = "http://182.254.148.240:8080";
    private static final String LOGIN_URL = HOST_NAME + "/login";
    private static final String INFO_URL = HOST_NAME + "/info";
    private static final String SCORE_URL = HOST_NAME + "/score";
    private static final String SYLLABUS_URL = HOST_NAME + "/syllabus";
    private static final String MOMENT_URL = HOST_NAME + "/moment";
    private static final String LIKE_MOMENT_URL = HOST_NAME + "/moment/like";
    private static final String UNLIKE_MOMENT_URL = HOST_NAME + "/moment/unlike";

    private Context context;
    private HttpHelper httpHelper;
    private Gson gson;

    public APIManager(Context context) {
        this.context = context;
        this.httpHelper = new HttpHelper(context);
        this.gson = new Gson();
        SugarContext.init(context);
    }

    public boolean login(String username, String password) throws RequestErrorException {
        HashMap<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        ResponseObj response = httpHelper.post(LOGIN_URL, params);
        if (response.getCode() == -1) {
            throw new RequestErrorException();
        }
        SimpleData simpleData = gson.fromJson(response.getData(), SimpleData.class);
        if (simpleData.isStat()) {
            SharedPreferences sharedPreferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("token", simpleData.getData());
            editor.commit();
        }
        return simpleData.isStat();
    }

    public Student getStudentInfo() throws TokenVerifyFailException, RequestErrorException {
        ResponseObj response = httpHelper.get(INFO_URL, null);
        if (response.getCode() == -1) {
            throw new RequestErrorException();
        } else if (response.getCode() == 401) {
            throw new TokenVerifyFailException();
        }
        Student student = gson.fromJson(response.getData(), Student.class);
        SharedPreferences sharedPreferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("studentID", student.getStudentID());
        editor.commit();
        return student;
    }

    public List<Score> getScore(String year, String term) throws TokenVerifyFailException, RequestErrorException {
        HashMap<String, String> params = new HashMap<>();
        params.put("year", year);
        params.put("term", term);
        ResponseObj response = httpHelper.get(SCORE_URL, params);
        if (response.getCode() == -1) {
            throw new RequestErrorException();
        } else if (response.getCode() == 401) {
            throw new TokenVerifyFailException();
        }
        Type lt = new TypeToken<List<Score>>(){}.getType();
        List<Score> scoreList = gson.fromJson(response.getData(), lt);
        Score.deleteAll(Score.class, "YEAR = ? AND TERM = ?", year, term);
        for (Score score: scoreList) {
            score.save();
        }
        return scoreList;
    }

    public List<Syllabus> getSyllabusList(String year, String term) throws TokenVerifyFailException, RequestErrorException {
        HashMap<String, String> params = new HashMap<>();
        params.put("year", year);
        params.put("term", term);
        ResponseObj response = httpHelper.get(SYLLABUS_URL, params);
        if (response.getCode() == -1) {
            throw new RequestErrorException();
        } else if (response.getCode() == 401) {
            throw new TokenVerifyFailException();
        }
        Type lt = new TypeToken<List<Syllabus>>(){}.getType();
        List<Syllabus> syllabusList = gson.fromJson(response.getData(), lt);
        Syllabus.deleteAll(Syllabus.class, "YEAR = ? AND TERM = ?", year, term);
        for (Syllabus syllabus: syllabusList) {
            syllabus.save();
        }
        return syllabusList;
    }

    public String getSyllabusHtml(String year, String term) throws TokenVerifyFailException, RequestErrorException {
        HashMap<String, String> params = new HashMap<>();
        params.put("year", year);
        params.put("term", term);
        params.put("type", "html");
        ResponseObj response = httpHelper.get(SYLLABUS_URL, params);
        if (response.getCode() == -1) {
            throw new RequestErrorException();
        } else if (response.getCode() == 401) {
            throw new TokenVerifyFailException();
        }
        return response.getData();
    }

    public List<Moment> getMoments() throws TokenVerifyFailException, RequestErrorException {
        ResponseObj response = httpHelper.get(MOMENT_URL, null);
        if (response.getCode() == -1) {
            throw new RequestErrorException();
        } else if (response.getCode() == 401) {
            throw new TokenVerifyFailException();
        }
        Type lt = new TypeToken<List<Moment>>(){}.getType();
        List<Moment> momentList = gson.fromJson(response.getData(), lt);
        Moment.deleteAll(Moment.class);
        for (Moment moment: momentList) {
            moment.getStudent().save();
            moment.save();
        }
        return momentList;
    }

    public Moment postMoment(String content) throws TokenVerifyFailException, RequestErrorException {
        HashMap<String, String> params = new HashMap<>();
        params.put("content", content);
        ResponseObj response = httpHelper.post(MOMENT_URL, params);
        if (response.getCode() == -1) {
            throw new RequestErrorException();
        } else if (response.getCode() == 401) {
            throw new TokenVerifyFailException();
        }
        Moment moment = gson.fromJson(response.getData(), Moment.class);
        return moment;
    }

    public Moment likeMoment(Moment moment) throws TokenVerifyFailException, RequestErrorException {
        HashMap<String, String> params = new HashMap<>();
        params.put("moment_id", Integer.toString(moment.getMomentID()));
        ResponseObj response = httpHelper.post(LIKE_MOMENT_URL, params);
        if (response.getCode() == -1) {
            throw new RequestErrorException();
        } else if (response.getCode() == 401) {
            throw new TokenVerifyFailException();
        }
        SimpleData simpleData = gson.fromJson(response.getData(), SimpleData.class);
        moment.setIsLike(simpleData.isStat());
        return moment;
    }

    public Moment unlikeMoment(Moment moment) throws TokenVerifyFailException, RequestErrorException {
        HashMap<String, String> params = new HashMap<>();
        params.put("moment_id", Integer.toString(moment.getMomentID()));
        ResponseObj response = httpHelper.post(UNLIKE_MOMENT_URL, params);
        if (response.getCode() == -1) {
            throw new RequestErrorException();
        } else if (response.getCode() == 401) {
            throw new TokenVerifyFailException();
        }
        SimpleData simpleData = gson.fromJson(response.getData(), SimpleData.class);
        moment.setIsUnlike(simpleData.isStat());
        return moment;
    }

    public Student getStudentInfoFromCache() throws TokenVerifyFailException, RequestErrorException {
        String studentID = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("studentID", "");
        Student student;
        // 可通过NamingHelper.toSQLName(Student.class.getDeclaredField) 获得 列名
        List<Student> studentList = Student.find(Student.class, "STUDENT_ID = ?", studentID);
        if (studentList.isEmpty()) {
            student = getStudentInfo();
            student.save();
        } else {
            student = studentList.get(0);
        }
        return student;
    }

    public List<Score> getScoreFromCache(String year, String term) throws TokenVerifyFailException, RequestErrorException {
//        String a = "";
//        try {
//            a = NamingHelper.toSQLName(Score.class.getDeclaredField("year"));
//            String b = a;
//        } catch (Exception e) {
//
//        }
        List<Score> scoreList = Score.find(Score.class, "YEAR = ? AND TERM = ?", year, term);
        if (scoreList.isEmpty()) {
            scoreList = getScore(year, term);
        }
        return scoreList;
    }

    public List<Syllabus> getSyllabusListFromCache(String year, String term) throws TokenVerifyFailException, RequestErrorException {

        List<Syllabus> syllabusList = Syllabus.find(Syllabus.class, "YEAR = ? AND TERM = ?", year, term);
        if (syllabusList.isEmpty()) {
            syllabusList = getSyllabusList(year, term);
        }
        return syllabusList;
    }

    public List<Moment> getMomentsFromCache() throws TokenVerifyFailException, RequestErrorException {
        List<Moment> momentList = Moment.listAll(Moment.class);
        if (momentList.isEmpty()) {
            momentList = getMoments();
        }
        return momentList;
    }

    public Score getLatestScore(String year, String term) throws TokenVerifyFailException, RequestErrorException {
        List<Score> localScoreList = Score.find(Score.class, "YEAR = ? AND TERM = ?", year, term);
        List<Score> serverScoreList = getScore(year, term);
        for (Score score: serverScoreList) {
            if (localScoreList.indexOf(score) == -1) {
                return score;
            }
            /*
            boolean flag = true;
            for (Score locScore: localScoreList) {
                if (score.getCourseId().equals(locScore.getCourseId())) {
                    flag = false;
                }
            }
            if (flag) {
                return score;
            }
            */
        }
        return null;
    }

    public void clearAllCache() {
        Moment.deleteAll(Moment.class);
        Syllabus.deleteAll(Syllabus.class);
        Score.deleteAll(Score.class);
        Student.deleteAll(Student.class);
    }
}

class RequestErrorException extends Exception {
    public RequestErrorException() {}
}

class TokenVerifyFailException extends Exception {
    public TokenVerifyFailException() {}
}

