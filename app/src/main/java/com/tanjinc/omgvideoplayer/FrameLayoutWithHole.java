package com.tanjinc.omgvideoplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

public class FrameLayoutWithHole extends FrameLayout {
    private static final String TAG = "FrameLayoutWithHole";
    private Context mContext;
    private Bitmap mEraserBitmap;
    private Canvas mEraserCanvas;
    private Paint mEraser;
    private RectF mRectF;

    private int margin = 50;
    private int radius = 20;
    private int mBackGroundColor;

    public FrameLayoutWithHole(@NonNull Context context) {
        this(context, null);

    }

    public FrameLayoutWithHole(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FrameLayoutWithHole(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }


    private void init() {
        setWillNotDraw(false);  //If this view doesn't do any drawing on its own
        Point size = new Point();
        size.x = mContext.getResources().getDisplayMetrics().widthPixels;
        size.y = mContext.getResources().getDisplayMetrics().heightPixels;

        mEraserBitmap = Bitmap.createBitmap(size.x, size.y, Bitmap.Config.ARGB_8888);
        mEraserCanvas = new Canvas(mEraserBitmap);
        mEraser = new Paint();
        mEraser.setColor(0xFFFFFFFF);
        mEraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mEraser.setFlags(Paint.ANTI_ALIAS_FLAG);

        mBackGroundColor = Color.WHITE;

        Log.d(TAG, "video init: (w/h)=" + getWidth() + "/" + getHeight());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mEraserBitmap.eraseColor(Color.TRANSPARENT);
        mEraserCanvas.drawColor(mBackGroundColor);
//        mEraserCanvas.drawCircle(200, 100, 100, mEraser);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        mRectF = new RectF(margin, margin, getMeasuredWidth() - margin, getMeasuredHeight() - margin);
        mEraserCanvas.drawRoundRect(mRectF,radius, radius, mEraser);
        canvas.drawBitmap(mEraserBitmap, 0, 0, null);
    }
}
