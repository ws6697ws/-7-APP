package com.example.myesv6;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.model.Student;

/**
 * Created by 锡鑫 on 2016/1/3.
 */
public class Fg4 extends Fragment {
    private TextView name,grade,sid,major,school,class_,sex;
    private Button About,Quit,Shake;
    private APIManager apiManager;
    private static final int UPDATE= 0;
    private AlertDialog shakeAlertDialog, shakeStartAlertDialog;
    private ShakeUtils shakeUtils;
    private int shakeTimes = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fg4, container,false);

        name = (TextView)view.findViewById(R.id.name);
        grade = (TextView)view.findViewById(R.id.grade);
        sid = (TextView)view.findViewById(R.id.id);
        major = (TextView)view.findViewById(R.id.major);
        school = (TextView)view.findViewById(R.id.school);
        sex = (TextView)view.findViewById(R.id.sex);
        class_ = (TextView)view.findViewById(R.id.class_);
        About = (Button)view.findViewById(R.id.about);
        Quit = (Button)view.findViewById(R.id.quit);
        Shake = (Button)view.findViewById(R.id.shake);
        shakeUtils = new ShakeUtils(view.getContext());
        apiManager = new APIManager(view.getContext());

        LinearLayout about = (LinearLayout) LayoutInflater.from(view.getContext()).inflate(R.layout.about,null);
        final AlertDialog alertDialog =  new AlertDialog.Builder(view.getContext()).setTitle("About Us").setView(about).setNegativeButton("哇！好屌啊...", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).create();
        shakeUtils.setOnShakeListener(new ShakeUtils.OnShakeListener() {
            @Override
            public void onShake() {
                shakeTimes += Math.random() * 5 + 1;
                if (shakeTimes > 100) shakeTimes = 100;
                shakeStartAlertDialog.setMessage("你已获得" + shakeTimes + "分！！！");
            }
        });
        shakeAlertDialog = new AlertDialog.Builder(view.getContext()).setTitle("测测你的期末成绩").setMessage("点击开始后，请用力摇晃手机，5秒钟内晃动的次数就是你的期末成绩哦~").setPositiveButton("开始", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                shakeStartAlertDialog = new AlertDialog.Builder(view.getContext()).setTitle("请用力摇晃！！！").setMessage("你已获得0分！！！").setPositiveButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        shakeUtils.onPause();
                        dialog.dismiss();
                    }
                }).create();
                shakeStartAlertDialog.setCanceledOnTouchOutside(false);
                shakeStartAlertDialog.show();
                shakeUtils.onResume();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        shakeUtils.onPause();
                        shakeStartAlertDialog.setMessage("游戏结束！\n你的期末考试将会是" + shakeTimes + "分哦~！");
                        shakeTimes = 0;
                    }
                }, 5000);
                dialog.dismiss();
            }
        }).setNegativeButton("我是学神，不需要！", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create();

        About.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.show();
            }
        });
        Quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = view.getContext().getSharedPreferences("data", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("token");
                editor.commit();
                apiManager.clearAllCache();
                startActivity(new Intent(view.getContext(), LoginActivity.class));
            }
        });
        Shake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shakeTimes = 0;
                shakeAlertDialog.show();
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //apiManager.login("13331015", "04234033");
                    Student student = apiManager.getStudentInfoFromCache();
                    Message message = new Message();
                    message.what = UPDATE;
                    message.obj = student;
                    handler.sendMessage(message);
                    //List<Score> scoreList = apiManager.getScoreFromCache("2014", "2");
                    //Score score = apiManager.getLatestScore("2014", "3");
                } catch (RequestErrorException e) {

                } catch (TokenVerifyFailException e) {

                }
            }
        }).start();

        return view;
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case UPDATE:
                    Student s = (Student)message.obj;
                    name.setText(s.getName());
                    sid.setText(s.getStudentID());
                    grade.setText(s.getGrade());
                    major.setText(s.getMajor());
                    sex.setText(s.getSex());
                    class_.setText(s.getClassName());
                    school.setText(s.getSchool());
                    break;
                default:
                    break;
            }
        }
    };

}