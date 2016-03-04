package com.example.myesv6;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.model.Moment;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by 锡鑫 on 2016/1/3.
 */
public class Fg3 extends Fragment {
    static CommentAdapter adapter;
    LinearLayout contentLayout;
    RelativeLayout topLayout;
    static ListView commentView;
    Button commentButton;
    RefreshableView refreshableView;
    static Context context;
    ArrayList<Moment> currentList;
    static APIManager apiManager;
    static boolean isGet;
    static Handler likeHandler = new Handler() {
        public void handleMessage(Message message) {
            switch(message.what) {
                case 0:
                    LinearLayout tem =(LinearLayout) commentView.getChildAt((Integer) message.obj);
                    ImageButton goodBtn = (ImageButton)tem.findViewById(R.id.content).findViewById(R.id.comment_bottom).findViewById(R.id.good_button);
                    goodBtn.setBackgroundResource(R.drawable.liked);
                    break;
                case 1:
                    LinearLayout tem2 =(LinearLayout) commentView.getChildAt((Integer) message.obj);
                    ImageButton badBtn = (ImageButton)tem2.findViewById(R.id.content).findViewById(R.id.comment_bottom).findViewById(R.id.good_button);
                    badBtn.setBackgroundResource(R.drawable.liked);
                    break;

            }
        }
    };
    static Handler UIHandler = new Handler() {
        public void handleMessage(Message message) {
            switch(message.what) {
                case 0:
                    ArrayList<Moment> tempList = (ArrayList<Moment>) message.obj;
                    adapter = new CommentAdapter(context, R.layout.comment_view, tempList);
                    commentView.setAdapter(adapter);
                    isGet = true;
                    break;
                default:
                    break;
            }
        }
    };;
    static Handler ToastHandler = new Handler() {
        public void handleMessage(Message message) {
            switch(message.what) {
                case 0:
                    Toast.makeText(context,"发送成功", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Toast.makeText(context,"重新登录", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(context,"网络错误", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(context,"请求成功", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fg3, container, false);
        currentList = new ArrayList<Moment>();
        context = view.getContext();
        apiManager = new APIManager(view.getContext());
        contentLayout = (LinearLayout)view.findViewById(R.id.content_layout);
        refreshableView = (RefreshableView) view.findViewById(R.id.refreshable_view);
        topLayout = (RelativeLayout)view.findViewById(R.id.top_layout);
        commentButton = (Button)view.findViewById(R.id.create_button);

        commentView = (ListView)refreshableView.findViewById(R.id.comment_view);
        Thread getData = new Thread(new RequestMoment());
        getData.start();
        //AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        refreshableView.setOnRefreshListener(new RefreshableView.PullToRefreshListener() {
            @Override
            public void onRefresh() {
                isGet = false;
                new Thread(new RequestMoment()).start();
                while (isGet == false) {
                }
                refreshableView.finishRefreshing();
            }
        }, 0);


        commentButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("发表评论");
                final LinearLayout all = (LinearLayout) LayoutInflater.from(view.getContext()).
                        inflate(R.layout.new_view, null);
                final EditText newInput = (EditText)all.findViewById(R.id.new_input);
                builder.setView(all);
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String temp = newInput.getText().toString();
                        if (temp.isEmpty()) {
                            Toast.makeText(view.getContext(), "输入不可为空", Toast.LENGTH_SHORT).show();;
                        } else {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Moment tempMoment =  apiManager.postMoment(URLEncoder.encode(newInput.getText().toString(), "UTF-8"));
                                        Message msg = new Message();
                                        msg.what = 0;
                                        ToastHandler.sendMessage(msg);
                                        ArrayList<Moment> tempList = (ArrayList<Moment>) apiManager.getMoments();
                                        Message re = new Message();
                                        re.obj = tempList;
                                        currentList = tempList;
                                        re.what = 0;
                                        UIHandler.sendMessage(re);


                                    } catch (TokenVerifyFailException e) {
                                        Message msg = new Message();
                                        msg.what = 1;
                                        ToastHandler.sendMessage(msg);
                                        e.printStackTrace();
                                    } catch (RequestErrorException e) {
                                        Message msg = new Message();
                                        msg.what = 2;
                                        ToastHandler.sendMessage(msg);
                                        e.printStackTrace();
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }
                                }

                            }).start();
                        }
                    }

                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }

                });
                builder.show();
            }

        });



        return view;
    }


    class RequestMoment implements Runnable {

        @Override
        public void run() {
            try {

                ArrayList<Moment> tempList = (ArrayList<Moment>) apiManager.getMoments();
                Message msg = new Message();
                msg.obj = tempList;

                msg.what = 0;
                UIHandler.sendMessage(msg);

                Message reMsg = new Message();
                reMsg.what = 3;
                ToastHandler.sendMessage(reMsg);
            } catch (TokenVerifyFailException e) {
                Message reMsg = new Message();
                reMsg.what = 1;
                ToastHandler.sendMessage(reMsg);
                e.printStackTrace();
            } catch (RequestErrorException e) {
                Message reMsg = new Message();
                reMsg.what = 2;
                ToastHandler.sendMessage(reMsg);
                e.printStackTrace();
            }

        }

    };

}