package com.example.myesv6;

        import java.util.List;
        import java.util.Map;



        import android.app.AlertDialog;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.os.Handler;
        import android.os.Message;

        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.View.OnClickListener;
        import android.view.ViewGroup;
        import android.widget.ArrayAdapter;

        import android.widget.ImageButton;
        import android.widget.LinearLayout;
        import android.widget.RelativeLayout;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.example.model.Moment;
        import com.example.model.Student;

public class CommentAdapter extends ArrayAdapter<Moment> {
    private int resourceId;
    private List<Moment> list;


    public CommentAdapter(Context context, int resource, List<Moment> commentList) {
        super(context, resource, commentList);
        resourceId = resource;
        list = commentList;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final Moment comment = (Moment) getItem(position);
        LinearLayout commentView = new LinearLayout(getContext());
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(resourceId, commentView);
        ImageButton snap = (ImageButton)commentView.findViewById(R.id.snap);
        snap.setBackgroundResource(R.drawable.r);
        final Student stu = comment.getStudent();
        //
        snap.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = LayoutInflater.from(getContext());

                final LinearLayout all = (LinearLayout) inflater.inflate(R.layout.dialog_view, null);
                RelativeLayout nameLayout = (RelativeLayout) all.findViewById(R.id.name_layout);
                TextView name = (TextView) nameLayout.findViewById(R.id.name);
                name.setText(stu.getName());



                RelativeLayout majorLayout = (RelativeLayout) all.findViewById(R.id.major_layout);
                TextView major = (TextView) majorLayout.findViewById(R.id.major);
                major.setText(stu.getMajor());

                RelativeLayout sexLayout = (RelativeLayout) all.findViewById(R.id.sex_layout);
                TextView sex = (TextView) sexLayout.findViewById(R.id.sex);
                sex.setText(stu.getSex());


                RelativeLayout schoolLayout = (RelativeLayout) all.findViewById(R.id.school_layout);
                TextView school = (TextView) schoolLayout.findViewById(R.id.school);
                school.setText(stu.getSchool());

                RelativeLayout classLayout = (RelativeLayout) all.findViewById(R.id.class_layout);
                TextView class_ = (TextView) classLayout.findViewById(R.id.class_);
                class_.setText(stu.getClassName());

                RelativeLayout gradeLayout = (RelativeLayout) all.findViewById(R.id.grade_layout);
                TextView grade = (TextView) gradeLayout.findViewById(R.id.grade);
                grade.setText(stu.getGrade());

                builder.setTitle("个人信息");
                builder.setView(all);
                builder.setNeutralButton("确认", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }

                });
                builder.show();
            }

        });

        LinearLayout content = (LinearLayout)commentView.findViewById(R.id.content);
        TextView userName = (TextView)content.findViewById(R.id.user_name);
        userName.setText(stu.getName());

        TextView comment_text = (TextView)content.findViewById(R.id.comment_text);
        comment_text.setText(comment.getContent());

        RelativeLayout content_bottom = (RelativeLayout)content.findViewById(R.id.comment_bottom);

        TextView timeText = (TextView)content_bottom.findViewById(R.id.time_text);
        timeText.setText(comment.getTime());

        TextView goodText = (TextView)content_bottom.findViewById(R.id.good_text);
        goodText.setText(Integer.toString(comment.getLikeCount()));
        final ImageButton goodButton = (ImageButton)content_bottom.findViewById(R.id.good_button);
        if (comment.isLike()) {
            goodButton.setBackgroundResource(R.drawable.liked);
        } else {
            goodButton.setBackgroundResource(R.drawable.like);
        }
        goodButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!comment.isLike()) {
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                Moment tempMoment = Fg3.apiManager.likeMoment(comment);
                                list.get(position).setLikeCount(list.get(position).getLikeCount() + 1);
                                Message update = new Message();
                                update.what = 0;
                                update.obj =  position;
                                Fg3.likeHandler.sendMessage(update);
                                Message msg = new Message();
                                msg.obj = list;
                                msg.what = 0;
                                Fg3.UIHandler.sendMessage(msg);
                            } catch (TokenVerifyFailException e) {
                                Message msg = new Message();
                                msg.what = 1;
                                Fg3.ToastHandler.sendMessage(msg);
                                e.printStackTrace();
                            } catch (RequestErrorException e) {
                                Message msg = new Message();
                                msg.what = 2;
                                Fg3.ToastHandler.sendMessage(msg);
                                e.printStackTrace();
                            }
                        }

                    }).start();
                }
            }

        });

        TextView badText= (TextView)content_bottom.findViewById(R.id.bad_text);
        badText.setText(Integer.toString(comment.getUnlikeCount()));
        final ImageButton badButton = (ImageButton)content_bottom.findViewById(R.id.bad_button);
        if (comment.isUnlike()) {
            badButton.setBackgroundResource(R.drawable.disliked);
        } else {
            badButton.setBackgroundResource(R.drawable.dislike);
        }
        badButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!comment.isUnlike()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Fg3.apiManager.unlikeMoment(comment);
                                list.get(position).setUnlikeCount(list.get(position).getUnlikeCount() + 1);
                                Message update = new Message();
                                update.what = 1;
                                update.obj = position;
                                Fg3.likeHandler.sendMessage(update);
                                Message msg = new Message();
                                msg.obj = list;
                                msg.what = 0;
                                Fg3.UIHandler.sendMessage(msg);
                            } catch (TokenVerifyFailException e) {
                                Message msg = new Message();
                                msg.what = 1;
                                Fg3.ToastHandler.sendMessage(msg);
                                e.printStackTrace();
                            } catch (RequestErrorException e) {
                                Message msg = new Message();
                                msg.what = 2;
                                Fg3.ToastHandler.sendMessage(msg);
                                e.printStackTrace();
                            }
                        }

                    }).start();
                }
            }

        });
        return commentView;
    }

}
