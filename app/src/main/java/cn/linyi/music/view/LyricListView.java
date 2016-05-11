package cn.linyi.music.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;

import cn.linyi.music.bean.Lyric;
import cn.linyi.music.util.Global;

/**
 * Created by linyi on 2016/4/24.
 */
public class LyricListView extends View {
    private List<Lyric> lyricList;
    private int currIndex = 0;
    private float startY;
    private float nowY;
    private float offSet;
    private float offsetY;
    /**
     * 绘制时控制文本绘制的范围
     */
    private Rect mBound;
    private  Paint paint;
    private int viewWidth;
    private int viewHeight;
    //每一行歌词的宽度 默认30dp
    private float linHeight;
    public LyricListView(Context context) {
        super(context);
    }

    public LyricListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //a = (Activity) context;
        paint = new Paint();
        mBound = new Rect();
        linHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,40,getResources().getDisplayMetrics());
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        //手指按到屏幕上
                        startY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        nowY = event.getY();
                        offSet = offsetY+nowY-startY;
                        Log.i("YI",offSet+"offset");
                        postInvalidate();
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.i("YI",nowY-startY+"Y-Y");
                        offSet = offsetY+ nowY-startY;
                        offsetY =offSet;
                        startY = 0;
                        nowY = 0;
                        postInvalidate();
                }
                return true;
            }
        });
    }

    public void changeData(List<Lyric> list,int index,int height) {
        //height 为LyricListView 在实际布局中的高度。
        Log.i("NUO","viewHeight:"+height+"传进来的值");
        viewHeight = height;
        Log.i("NUO","viewHeight:"+viewHeight+"传进来的值");
        currIndex = index;
        offSet = currIndex * -1*linHeight;
        offsetY =offSet;
        lyricList = list;
        super.postInvalidate();
    }

    public LyricListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(lyricList != null) {
            for(int i=0;i<lyricList.size();i++) {
                if (i == currIndex) {
                    paint.setColor(Color.WHITE);
                    //高亮 设置为20sp TypedValue..applyDimension()进行sp dp px之间的转换
                    float highLight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,20,getResources().getDisplayMetrics());
                    paint.setTextSize(highLight);
                } else {
                    //普通设置为14sp
                    float normal = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,14,getResources().getDisplayMetrics());
                    paint.setColor(Color.GRAY);
                    paint.setTextSize(normal);
                }

                String lrc = lyricList.get(i).getLrc();
                float textWidth = paint.measureText(lrc);
                Paint.FontMetrics fm = paint.getFontMetrics();//获取字体的度量（尺寸）
                float textHeight = fm.descent - fm.ascent;
                if (textWidth < Global.SCREEN_WIDTH * 9 / 10) {
                    drawText(lrc,textWidth,
                           textHeight,i,canvas);
                } else {
                    String firLrc = lrc.substring(0,lrc.length()/2);
                    String secLrc = lrc.substring(lrc.length()/2);
                    drawText(firLrc,paint.measureText(firLrc),textHeight,i,canvas);
                    offSet +=linHeight;
                    drawText(secLrc,paint.measureText(secLrc),textHeight,i,canvas);
                }
            }
        }
    }

    private void drawText(String txt,float txtWidth,float txtHeight,int index,Canvas canvas ) {
        paint.getTextBounds(txt,0,txt.length(),mBound);
       // canvas.drawRect(0,0,getMeasuredWidth(),getMeasuredHeight(),paint);
        //经试验得到canvas中的x和y均是相对坐标，相对于自己外边界的。不是屏幕上的绝对坐标。
        canvas.drawText(txt, (Global.SCREEN_WIDTH - txtWidth) / 2,
                (viewHeight - txtHeight) / 2 + index * linHeight + linHeight/2 + offSet, paint);
        if(index == currIndex) {
            Log.i("NUO","curr"+currIndex+txt+"  Y:"+ (( viewHeight - txtHeight) / 2 + index* mBound.height() + mBound.height()/2) +"  offset:"+
                    "txtHeight"+txtHeight+ "viewHeight:" + viewHeight
            +"mboundHeight:"+mBound.height()+"width:"+mBound.width());

        }
    }

    private void  qukong(List<Lyric> lyricList) {
     /*   for (Lyric l: lyricList){
            if(l.getLrc().equals(""))
                lyricList.remove(l);
        }*/
        for(int i = 0;i<lyricList.size();i++) {
            if(lyricList.get(i).getLrc().equals("")){
                lyricList.remove(i);
                i--;
            }
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
    }
}
