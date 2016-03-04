package com.example.myesv6;

/**
 * Created by Lenovo on 2016/1/8.
 */
        import android.content.Context;
        import android.graphics.Color;
        import android.os.Handler;
        import android.os.Message;
        import android.view.View;
        import android.view.ViewGroup;
        import android.view.ViewGroup.LayoutParams;
        import android.widget.Button;
        import android.widget.FrameLayout;
        import android.widget.RelativeLayout;
        import android.widget.Toast;


public class ErinieShow extends RelativeLayout {
    Context context;
    RelativeLayout rubblerBG;
    RubblerShow rubblerShow;
    Button getReward;

    private int rubblerBGId = 10001;
    int getRewardId = 10002;


    public ErinieShow(Context context) {
        super(context);
        this.context = context;
        getElement();
        setElementLP();
        setElementStyle();
        setElement();
    }

    private void getElement() {
        rubblerBG = new RelativeLayout(context);
        rubblerShow = new RubblerShow(context,handler);

        rubblerBG.setId(0 + rubblerBGId);
        rubblerBG.addView(rubblerShow);
        addView(rubblerBG);
    }

    private void setElementLP() {
        int[] resolution = PhoneUtil.getResolution(context);
        RelativeLayout.LayoutParams rubblerBG_LP = new RelativeLayout.LayoutParams(
                resolution[0], PhoneUtil.getFitHeight(context, 125));

        rubblerBG.setLayoutParams(rubblerBG_LP);
        rubblerShow.setLayoutParams(rubblerBG_LP);

    }

    private void setElementStyle() {
//		getReward.setBackgroundResource(R.drawable.get_award);
    }

    private void setElement() {
        rubblerShow.beginRubbler(Color.parseColor("#d3d3d3"), 30, 10);
    }

    Handler handler=new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }

    };

}


