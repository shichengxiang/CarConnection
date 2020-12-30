package com.linruan.carconnection.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.linruan.carconnection.R;

public class PieProgressBar extends View {

    //画笔
    private Paint paint;

    //中心圆的颜色
    private int roundColor;

    //饼状进度的颜色
    private int pieColor;
    //文字验证
    private int txtColor;

    //进度，初始值为0
    private int progress = 0;

    //最大进度
    private int maxProgress;

    public PieProgressBar(Context context) {
        this(context, null);
    }

    public PieProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PieProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        paint = new Paint();

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PieProgressBar);
        roundColor = typedArray.getColor(R.styleable.PieProgressBar_roundColor,
                Color.parseColor("#66cccccc"));
        pieColor = typedArray.getColor(R.styleable.PieProgressBar_pieColor,
                Color.parseColor("#AA676363"));
        txtColor=typedArray.getColor(R.styleable.PieProgressBar_pie_textColor,
                Color.parseColor("#FFFFFF"));
        maxProgress = typedArray.getInt(R.styleable.PieProgressBar_maxProgress, 100);
        typedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //整个控件的半径
        float radius = (float)(getWidth() / 2);

        //饼状进度的半径
        float pieRadius = radius - radius / 6 ;

        //绘制中心圆
        paint.setStyle(Paint.Style.FILL);   //设置是否填充
        paint.setColor(roundColor);  //设置画笔颜色
        paint.setAntiAlias(true);  //设置是否消除锯齿
        canvas.drawCircle(radius, radius, pieRadius, paint);

        //绘制外层圆环
        paint.setColor(pieColor);
        paint.setStyle(Paint.Style.STROKE);
//        paint.setStrokeWidth(radius / 6);
        canvas.drawCircle(radius, radius, pieRadius, paint);

        //绘制扇形进度
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(pieColor);
        RectF rectF = new RectF(radius - pieRadius, radius - pieRadius,
                radius + pieRadius, radius + pieRadius);
        canvas.drawArc(rectF, -90, -360 * progress / maxProgress,
                true, paint);
        //文字
        paint.setTextSize(getResources().getDisplayMetrics().density*12);
        paint.setColor(txtColor);
        String txt=progress+"%";
        float stringWidth=paint.measureText(txt);
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float stringHeight=getHeight()/2+(Math.abs(fontMetrics.ascent)-fontMetrics.descent)/2;
        canvas.drawText(progress+"%",radius-stringWidth/2,stringHeight,paint);
    }

    public void setMaxProgress(int maxProgress) {
        if (maxProgress > 0) {
            this.maxProgress = maxProgress;
        }
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public void setProgress(int progress) {
        if (progress > maxProgress) {
            progress = maxProgress;
        }

        if (progress < 0) {
            progress = 0;
        }

        this.progress = progress;
        invalidate();
    }

    public int getProgress() {
        return progress;
    }
}