package com.example.myesv6;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.model.Score;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by 锡鑫 on 2016/1/3.
 */
public class Fg2 extends Fragment {
    ErinieShow erinieShow;
    private TextView hun,ten,num,mess;
    private TextView cid,credit,gpa,cname,rank,myscore,teacher,term,year;
    private Button pass;
    private ListView passscore;
    private APIManager apiManager;
    private static final int UPDATE= 0;
    private static final int SCORE= 0;
    private RelativeLayout relativeLayout;
    private List<HashMap<String, String>> data = new ArrayList<HashMap<String, String >>();
    private View view;
    private Thread thread;

    private Button term_button;
    private ProgressDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fg2, container,false);

        relativeLayout = (RelativeLayout)view.findViewById(R.id.contain);
        hun = (TextView)view.findViewById(R.id.textView4);
        ten = (TextView)view.findViewById(R.id.textView2);
        num = (TextView)view.findViewById(R.id.textView3);
        mess = (TextView)view.findViewById(R.id.textView);
        //pass = (Button)view.findViewById(R.id.term_button);
        apiManager = new APIManager(view.getContext());
        passscore = (ListView)view.findViewById(R.id.ls);

        term_button =(Button)view.findViewById(R.id.term_button);
        LayoutInflater spinner_inflater = LayoutInflater.from(view.getContext());
        final View layout = inflater.inflate(R.layout.spinner, null);
        dialog = new ProgressDialog(view.getContext());
        dialog.setTitle("亲～～(づ￣3￣)づ╭❤～\n正在努力为您拉成绩，请耐心等待...");
        //dialog.show();


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
                                    //apiManager.login("13331015", "04234033");
//                                    Score score = apiManager.getLatestScore("2015", "1");
                                    List<Score> scoreList = apiManager.getScoreFromCache(year_str, term_str);
                                    Message message = new Message();
                                    message.what = UPDATE;
                                    message.obj = scoreList;
                                    handler.sendMessage(message);

                                } catch (RequestErrorException e) {

                                } catch (TokenVerifyFailException e) {

                                }
                            }
                        }).start();
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog1, int which) {
                            }
                        }).create();


                        term_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alertDialog.show();
                            }
                        });
                        thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    //apiManager.login("13331015", "04234033");
                                    Score score = apiManager.getLatestScore("2015", "1");
                                    List<Score> scoreList = apiManager.getScoreFromCache("2014", "2");
                                    Message message = new Message();
                                    message.what = UPDATE;
                                    message.obj = scoreList;
                                    handler.sendMessage(message);
                                    Message mess = new Message();
                                    mess.what = SCORE;
                                    mess.obj = score;
                                    handler2.sendMessage(mess);

                                } catch (RequestErrorException e) {

                                } catch (TokenVerifyFailException e) {

                                }
                            }
                        });
                        thread.start();

                        return view;
                    }

                    public Handler handler = new Handler() {
                        public void handleMessage(Message message) {
                            switch (message.what) {
                                case UPDATE:
                                    final List<Score> scores = (List<Score>) message.obj;
//                    HashMap<String, String> ma = new HashMap<String, String>();
//                    ma.put("classname", "课程名称");
//                    ma.put("scorenum", "成绩");
//                    ma.put("range", "排名");
//                    data.add(ma);
                                    data.clear();
                                    for (Score score : scores) {
                                        HashMap<String, String> map = new HashMap<String, String>();
                                        map.put("classname", score.getName());
                                        map.put("scorenum", score.getScore());
                                        map.put("range", score.getRank());
                                        data.add(map);
                                    }
                                    String[] from = {"classname", "scorenum", "range"};
                                    int[] to = {R.id.classname, R.id.scorenum, R.id.range};
                                    final SimpleAdapter adapter = new SimpleAdapter(view.getContext(), data, R.layout.layout, from, to);
                                    passscore.setAdapter(adapter);
                                    final LinearLayout scoreif = (LinearLayout) LayoutInflater.from(view.getContext()).inflate(R.layout.scoreinfo, null);
                                    cid = (TextView) scoreif.findViewById(R.id.cid);
                                    credit = (TextView) scoreif.findViewById(R.id.credit);
                                    gpa = (TextView) scoreif.findViewById(R.id.gpa);
                                    cname = (TextView) scoreif.findViewById(R.id.cname);
                                    rank = (TextView) scoreif.findViewById(R.id.rank);
                                    myscore = (TextView) scoreif.findViewById(R.id.myscore);
                                    teacher = (TextView) scoreif.findViewById(R.id.teacher);
                                    term = (TextView) scoreif.findViewById(R.id.term);
                                    year = (TextView) scoreif.findViewById(R.id.year);
                                    final AlertDialog alertDialog = new AlertDialog.Builder(view.getContext()).setTitle("成绩信息").setView(scoreif).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    }).create();
                                    passscore.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            //if (position == 0) return;
                                            Score sc = scores.get(position);
                                            cid.setText(sc.getCourseId());
                                            credit.setText(sc.getCredit());
                                            gpa.setText(sc.getGpa());
                                            cname.setText(sc.getName());
                                            rank.setText(sc.getRank());
                                            term.setText(sc.getTerm());
                                            teacher.setText(sc.getTeacher());
                                            year.setText(sc.getYear());
                                            alertDialog.show();
                                        }
                                    });
                                    dialog.dismiss();
                                    break;
                                default:
                                    break;
                            }
                        }
                    };
                    private Handler handler2 = new Handler() {
                        public void handleMessage(Message message) {
                            switch (message.what) {
                                case SCORE:
                                    Score score = (Score) message.obj;
                                    if (score != null) {
                                        showErnie();
                                        String classname = score.getName();
                                        mess.setText("今天你的《" + classname + "》成绩出了哦,快来刮一刮吧~");
                                        String scorenum = score.getScore();
                                        if (scorenum.length() == 3) {
                                            hun.setText("1");
                                            ten.setText("0");
                                            num.setText("0");
                                        } else {
                                            hun.setText("0");
                                            ten.setText("" + scorenum.charAt(0));
                                            num.setText("" + scorenum.charAt(1));
                                        }
                                    } else {
                                        hun.setText("");
                                        ten.setText("");
                                        num.setText("");
                                    }
                                    dialog.dismiss();
                                    break;
                                default:
                                    break;
                            }
                        }
                    };

                    private void showErnie() {
//		container
                        relativeLayout.removeAllViews();

                        erinieShow = new ErinieShow(view.getContext());

                        relativeLayout.addView(erinieShow, new ViewGroup.LayoutParams(-2, -2));
                    }

                }