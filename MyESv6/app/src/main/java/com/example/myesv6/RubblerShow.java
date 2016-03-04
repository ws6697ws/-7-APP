package com.example.myesv6;

/**
 * Created by Lenovo on 2016/1/8.
 */

        import android.content.Context;
        import android.graphics.Bitmap;
        import android.graphics.Bitmap.Config;
        import android.graphics.Canvas;
        import android.graphics.Color;
        import android.graphics.Paint;
        import android.graphics.Path;
        import android.graphics.PorterDuff;
        import android.graphics.PorterDuffXfermode;
        import android.os.Handler;
        import android.util.AttributeSet;
        import android.view.MotionEvent;
        import android.view.ViewGroup.LayoutParams;
        import android.widget.TextView;

public class RubblerShow extends TextView {

    private float TOUCH_TOLERANCE; // 濉厖璺濈锛屼娇绾挎潯鏇磋嚜鐒讹紝鏌斿拰,鍊艰秺灏忥紝瓒婃煍鍜屻€?

    // private final int bgColor;

    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mPaint;
    private Path mPath;
    private float mX, mY;

    private boolean isDraw = false;

    Handler handler;
    int time=0;

    public RubblerShow(Context context, Handler handler) {
        super(context);
        this.handler=handler;
        // bgColor =
        // attrs.getAttributeIntValue("http://schemas.android.com/apk/res/android",
        // "textColor", 0xFFFFFF);
        // System.out.println(bgColor);
        // System.out.println(attrs.getAttributeValue("http://schemas.android.com/apk/res/android",
        // "layout_width"));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isDraw) {
            mCanvas.drawPath(mPath, mPaint);
            // mCanvas.drawPoint(mX, mY, mPaint);
            canvas.drawBitmap(mBitmap, 0, 0, null);
        }
    }

    /**
     * 寮€鍚闄ゅ姛鑳?
     *
     * @param bgColor
     *            瑕嗙洊鐨勮儗鏅鑹?
     * @param paintStrokeWidth
     *            瑙︾偣锛堟鐨級瀹藉害
     * @param touchTolerance
     *            濉厖璺濈,鍊艰秺灏忥紝瓒婃煍鍜屻€?
     */
    public void beginRubbler(final int bgColor, final int paintStrokeWidth,
                             float touchTolerance) {
        TOUCH_TOLERANCE = touchTolerance;
        // 璁剧疆鐢荤瑪
        mPaint = new Paint();
        // mPaint.setAlpha(0);
        // 鐢荤瑪鍒掕繃鐨勭棔杩瑰氨鍙樻垚閫忔槑鑹蹭簡
        mPaint.setColor(Color.BLACK); // 姝ゅ涓嶈兘涓洪€忔槑鑹?
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        // 鎴栬€?
        // mPaint.setAlpha(0);
        // mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND); // 鍓嶅渾瑙?
        mPaint.setStrokeCap(Paint.Cap.ROUND); // 鍚庡渾瑙?
        mPaint.setStrokeWidth(paintStrokeWidth); // 绗斿

        // 鐥曡抗
        mPath = new Path();
        // 瑕嗙洊
        LayoutParams layoutParams = getLayoutParams();
        int height = layoutParams.height;
        int width;
        if (getLayoutParams().width == LayoutParams.MATCH_PARENT) {
            width = 700;
        } else {
            width = layoutParams.width;
        }

        mBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

        mCanvas.drawColor(bgColor);
        isDraw = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isDraw) {
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // 瑙︾偣鎸変笅
                // touchDown(event.getRawX(),event.getRawY());
                touchDown(event.getX(), event.getY());
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE: // 瑙︾偣绉诲姩
                touchMove(event.getX(), event.getY());
                invalidate();
                if(time++>3&&handler!=null){
                    handler.sendEmptyMessage(200);
                }
                break;
            case MotionEvent.ACTION_UP: // 瑙︾偣寮硅捣
                touchUp(event.getX(), event.getY());
                invalidate();
                break;
            default:
                break;
        }
        return true;
    }

    private void touchDown(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }

    }

    private void touchUp(float x, float y) {
        mPath.lineTo(x, y);
        mCanvas.drawPath(mPath, mPaint);
        mPath.reset();
    }

}


