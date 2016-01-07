package com.ae.simplemusicplay.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

import com.ae.simplemusicplay.model.LrcContent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chen on 2015/12/26.
 */
public class LrcView extends TextView {
    private float width;
    private float height;
    Paint currentPaint;
    Paint noCurrentPaint;
    float textHeight;
    int index = 0;
    boolean noLrc = true;

    List<LrcContent> list = new ArrayList<>();


    public boolean isNoLrc() {
        return noLrc;
    }

    public void setNoLrc(boolean noLrc) {
        this.noLrc = noLrc;
    }

    public void setList(List<LrcContent> list) {
        this.list = list;

    }

    public LrcView(Context context) {
        this(context, null);
    }

    public LrcView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LrcView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setFocusable(true);

        currentPaint = new Paint();
        currentPaint.setAntiAlias(true);
        currentPaint.setTextAlign(Paint.Align.CENTER);

        noCurrentPaint = new Paint();
        noCurrentPaint.setAntiAlias(true);
        noCurrentPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        currentPaint.setColor(Color.parseColor("#212121"));  //播放歌词的颜色
        noCurrentPaint.setColor(Color.parseColor("#424242"));  //未播放

        currentPaint.setTextSize(35);
        currentPaint.setTypeface(Typeface.SERIF);

        noCurrentPaint.setTextSize(29);
        noCurrentPaint.setTypeface(Typeface.DEFAULT);

        setText("");
        if (noLrc) {
            setText("没有歌词....");
            this.setGravity(Gravity.CENTER);
        } else {
            if (list.size() == 0) {
                canvas.drawText("正在加载歌词....", width / 2, height / 2, currentPaint);
            } else {

                Rect bounds = new Rect();
                currentPaint.getTextBounds(list.get(0).getLrcStr(), 0, 1, bounds);
                textHeight = bounds.height();

                canvas.drawText(list.get(index).getLrcStr(), width / 2, height / 2, currentPaint);

                float tempY = height / 2;

                for (int i = index - 1; i >= 0; i--) {
                    tempY = tempY - textHeight - 50;
                    canvas.drawText(list.get(i).getLrcStr(), width / 2, tempY, noCurrentPaint);
                }

                tempY = height / 2;
                for (int i = index + 1; i < list.size(); i++) {

                    tempY = tempY + textHeight + 50;
                    canvas.drawText(list.get(i).getLrcStr(), width / 2, tempY, noCurrentPaint);
                }
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.width = w;
        this.height = h;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
