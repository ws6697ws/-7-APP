package com.example.myesv6;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.model.Syllabus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * Created by 锡鑫 on 2016/1/3.
 */
public class Fg1 extends Fragment {

    /** 第一个无内容的格子 */
    protected TextView empty;
    /** 星期一的格子 */
    protected TextView monColum;
    /** 星期二的格子 */
    protected TextView tueColum;
    /** 星期三的格子 */
    protected TextView wedColum;
    /** 星期四的格子 */
    protected TextView thrusColum;
    /** 星期五的格子 */
    protected TextView friColum;
    /** 星期六的格子 */
    protected TextView satColum;
    /** 星期日的格子 */
    protected TextView sunColum;
    /** 课程表body部分布局 */
    protected RelativeLayout course_table_layout;
    /** 屏幕宽度 **/
    protected int screenWidth;
    /** 课程格子平均宽度 **/
    protected int aveWidth;

    private ProgressDialog dialog;

    //五种颜色的背景
    private int[] background = {R.drawable.course_info_blue, R.drawable.course_info_green,
            R.drawable.course_info_red, R.drawable.course_info_red,
            R.drawable.course_info_yellow};
    private int gridHeight;
    private View view;


    private static final int UPDATE_CONTENT = 1;
    private static final int UPDATE_CONTENT1 = 2;


    private Handler handler = new Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case UPDATE_CONTENT:
                    List<Syllabus> syllabusList = (List<Syllabus>)message.obj;
                    for (Syllabus syllabus : syllabusList) {
                        // 添加课程信息
                        TextView courseInfo = new TextView(view.getContext());
                        courseInfo.setText(syllabus.getName() + "\n@" + syllabus.getPlace());
                        //该textview的高度根据其节数的跨度来设置
                        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
                                aveWidth * 31 / 32,
                                (gridHeight - 5) * (syllabus.getToTime() - syllabus.getFromTime() + 1) );

                        //textview的位置由课程开始节数和上课的时间（day of week）确定
                        rlp.topMargin = 5 + (syllabus.getFromTime() - 1) * gridHeight;
                        rlp.leftMargin = 1;
                        // 偏移由这节课是星期几决定
                        rlp.addRule(RelativeLayout.RIGHT_OF, syllabus.getDay());
                        //字体剧中
                        courseInfo.setGravity(Gravity.CENTER);
                        // 设置一种背景
                        courseInfo.setBackgroundResource(background[syllabus.getDay() - 1]);
                        courseInfo.setTextSize(12);
                        courseInfo.setLayoutParams(rlp);
                        courseInfo.setTextColor(Color.WHITE);
                        //设置不透明度
                        courseInfo.getBackground().setAlpha(222);
                        course_table_layout.addView(courseInfo);

                    }
                    dialog.dismiss();

                    break;

                default:
                    break;
            }
            super.handleMessage(message);
        }
    };

    private Handler handler1 = new Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case UPDATE_CONTENT1:
                    View view1 = (View) message.obj;
                    course_table_layout.addView(view1);
                    break;

                default:
                    break;
            }
            super.handleMessage(message);
        }
    };



    private Button terms;
    private APIManager apiManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fg1, container,false);


        LayoutInflater spinner_inflater = LayoutInflater.from(view.getContext());
        final View layout = inflater.inflate(R.layout.spinner, null);

        final AlertDialog alertDialog = new AlertDialog.Builder(view.getContext())
                .setTitle("选择学年与学期").setView(layout).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog1, int which) {
                dialog.show();

                Spinner year = (Spinner) layout.findViewById(R.id.year_spinner);
                Spinner term = (Spinner) layout.findViewById(R.id.term_spinner);
                final String year_str = year.getSelectedItem().toString();
                final String term_str = term.getSelectedItem().toString();


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            course_table_layout.removeAllViewsInLayout();
                            List<Syllabus> syllabusList = apiManager.getSyllabusListFromCache(year_str, term_str);



                            for(int i = 1; i <= 15; i ++) {
                                for (int j = 1; j <= 8; j++) {
                                    TextView tx = new TextView(view.getContext());
                                    tx.setId((i - 1) * 8 + j);
                                    //除了最后一列，都使用course_text_view_bg背景（最后一列没有右边框）
                                    if (j < 8)
                                        tx.setBackgroundDrawable(view.getContext().
                                                getResources().getDrawable(R.drawable.course_text_view_bg));
                                    else
                                        tx.setBackgroundDrawable(view.getContext().
                                                getResources().getDrawable(R.drawable.course_table_last_colum));
                                    //相对布局参数
                                    RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(
                                            aveWidth * 33 / 32 + 1,
                                            gridHeight);
                                    //文字对齐方式
                                    tx.setGravity(Gravity.CENTER);
                                    //字体样式
                                    tx.setTextAppearance(view.getContext(), R.style.courseTableText);
                                    //如果是第一列，需要设置课的序号（1 到 15）
                                    if (j == 1) {
                                        tx.setText(String.valueOf(i));
                                        rp.width = aveWidth * 3 / 4;
                                        //设置他们的相对位置
                                        if (i == 1)
                                            rp.addRule(RelativeLayout.BELOW, empty.getId());
                                        else
                                            rp.addRule(RelativeLayout.BELOW, (i - 1) * 8);
                                    } else {
                                        rp.addRule(RelativeLayout.RIGHT_OF, (i - 1) * 8 + j - 1);
                                        rp.addRule(RelativeLayout.ALIGN_TOP, (i - 1) * 8 + j - 1);
                                        tx.setText("");
                                    }

                                    tx.setLayoutParams(rp);
//                                    course_table_layout.addView(tx);
                                    Message message = new Message();
                                    message.what = UPDATE_CONTENT1;
                                    message.obj = tx;
                                    handler1.sendMessage(message);
                                }
                            }

                            Message message = new Message();
                            message.what = UPDATE_CONTENT;
                            message.obj = syllabusList;
                            handler.sendMessage(message);
                        } catch (RequestErrorException e) {
                            Toast.makeText(view.getContext(), "亲，课表拉不下来啊~", Toast.LENGTH_SHORT);
                        } catch (TokenVerifyFailException e) {
                            startActivity(new Intent(view.getContext(), LoginActivity.class));
                        }
                    }
                }).start();
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).create();

//获得列头的控件
        terms = (Button)view.findViewById(R.id.terms_button);
        empty = (TextView) view.findViewById(R.id.test_empty);
        monColum = (TextView) view.findViewById(R.id.test_monday_course);
        tueColum = (TextView) view.findViewById(R.id.test_tuesday_course);
        wedColum = (TextView) view.findViewById(R.id.test_wednesday_course);
        thrusColum = (TextView) view.findViewById(R.id.test_thursday_course);
        friColum = (TextView) view.findViewById(R.id.test_friday_course);
        satColum  = (TextView) view.findViewById(R.id.test_saturday_course);
        sunColum = (TextView) view.findViewById(R.id.test_sunday_course);
        course_table_layout = (RelativeLayout) view.findViewById(R.id.test_course_rl);
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        //屏幕宽度
        int width = dm.widthPixels;
        //平均宽度
        int aveWidth = width / 8;
        //第一个空白格子设置为25宽
        empty.setWidth(aveWidth * 3/4);
        monColum.setWidth(aveWidth * 33/32 + 1);
        tueColum.setWidth(aveWidth * 33/32 + 1);
        wedColum.setWidth(aveWidth * 33/32 + 1);
        thrusColum.setWidth(aveWidth * 33/32 + 1);
        friColum.setWidth(aveWidth * 33/32 + 1);
        satColum.setWidth(aveWidth * 33/32 + 1);
        sunColum.setWidth(aveWidth * 33/32 + 1);
        this.screenWidth = width;
        this.aveWidth = aveWidth;
        int height = dm.heightPixels;
        gridHeight = height / 12;
        //设置课表界面
        //动态生成15 * maxCourseNum个textview
        for(int i = 1; i <= 15; i ++){

            for(int j = 1; j <= 8; j ++){

                TextView tx = new TextView(view.getContext());
                tx.setId((i - 1) * 8  + j);
                //除了最后一列，都使用course_text_view_bg背景（最后一列没有右边框）
                if(j < 8)
                    tx.setBackgroundDrawable(view.getContext().
                            getResources().getDrawable(R.drawable.course_text_view_bg));
                else
                    tx.setBackgroundDrawable(view.getContext().
                            getResources().getDrawable(R.drawable.course_table_last_colum));
                //相对布局参数
                RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(
                        aveWidth * 33 / 32 + 1,
                        gridHeight);
                //文字对齐方式
                tx.setGravity(Gravity.CENTER);
                //字体样式
                tx.setTextAppearance(view.getContext(), R.style.courseTableText);
                //如果是第一列，需要设置课的序号（1 到 15）
                if(j == 1)
                {
                    tx.setText(String.valueOf(i));
                    rp.width = aveWidth * 3/4;
                    //设置他们的相对位置
                    if(i == 1)
                        rp.addRule(RelativeLayout.BELOW, empty.getId());
                    else
                        rp.addRule(RelativeLayout.BELOW, (i - 1) * 8);
                }
                else
                {
                    rp.addRule(RelativeLayout.RIGHT_OF, (i - 1) * 8  + j - 1);
                    rp.addRule(RelativeLayout.ALIGN_TOP, (i - 1) * 8  + j - 1);
                    tx.setText("");
                }
                tx.setLayoutParams(rp);
                course_table_layout.addView(tx);
            }
        }

        dialog = new ProgressDialog(view.getContext());
        dialog.setTitle("亲～～(づ￣3￣)づ╭❤～\n正在为您拉课表，请耐心等待...");
        dialog.show();

        apiManager = new APIManager(view.getContext());

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<Syllabus> syllabusList = apiManager.getSyllabusListFromCache("2015", "2");
                    Message message = new Message();
                    message.what = UPDATE_CONTENT;
                    message.obj = syllabusList;
                    handler.sendMessage(message);
                } catch (RequestErrorException e) {
                    Toast.makeText(view.getContext(), "亲，课表拉不下来啊~", Toast.LENGTH_SHORT);
                } catch (TokenVerifyFailException e) {
                    startActivity(new Intent(view.getContext(), LoginActivity.class));
                }
            }
        }).start();

        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.show();
            }
        });

        return view;
    }
}