package cn.linyi.music.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import cn.linyi.music.R;
import cn.linyi.music.bean.Music;
import cn.linyi.music.util.Global;

/**
 * Created by linyi on 2016/5/15.
 */
public class MusicListView extends View {
    private int viewWidth;//文本框宽度
    private int viewHeight; //文本框高度
    private boolean flag;//当前music是否被加载了
    private static final String DASH = " - ";//破折号
    private static final String ELLIPISIS = "...";//省略号
    private int textLeftX;
    private float  textMarginLeft;

    public Music getMusic() {
        return music;
    }

    public void setMusic(Music music) {
        this.music = music;
    }

    private Music music;
    private Rect mBound;//绘制时控制文本范围
    private Paint mPaint;
    private float titleSize;
    private float artistSize;
    public MusicListView(Context context) {
        super(context);
    }

    public MusicListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        viewWidth = Global.SCREEN_WIDTH;
        viewHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,50,getResources().getDisplayMetrics());
        titleSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,18,getResources().getDisplayMetrics());
        artistSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,14,getResources().getDisplayMetrics());
        textMarginLeft = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,5,getResources().getDisplayMetrics());
        setFocusable(false);
    }

    public void changData(Music m, int width,boolean flag){
        this.flag = flag;
        viewWidth = width;
        Log.i("WANG",viewWidth+"width");
        Log.i("WANG","music not null");
        this.music = m;
        if(music.getArtist() == null){
            music.setArtist("");
        } else {
            if(!music.getArtist().contains(DASH) && !music.getArtist().equals("")){
            music.setArtist(DASH + music.getArtist());
            }
        }
     super.postInvalidate();

    }
    public MusicListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setTextSize(titleSize);
        Resources rec = getResources();
        if(flag){
            mPaint.setColor(Color.RED);
            BitmapDrawable bitmapDrawable = (BitmapDrawable) rec.getDrawable(R.drawable.small_red);
            Bitmap bitmap = bitmapDrawable.getBitmap();
            //bitmap.set
            canvas.drawBitmap(bitmap,0,(viewHeight - bitmap.getHeight())/2,mPaint);
            textLeftX = (int) (bitmap.getWidth() + textMarginLeft);
            viewWidth -= textLeftX /2;
        } else {
            textLeftX = (int) textMarginLeft;
            mPaint.setColor(Color.BLACK);
        }
       // canvas.drawText("xiandakdiasdhiasdha",0,40,mPaint);
        Log.i("WANG","music not null onDraw");
        if(music != null) {
            mPaint.setTextSize(titleSize);
            float titleWidth = mPaint.measureText(music.getTitle());
            mPaint.setTextSize(artistSize);
            float artistWidth = mPaint.measureText(music.getArtist());
            Paint.FontMetrics fm = mPaint.getFontMetrics();//获取字体的度量（尺寸）
            float textHeight = (fm.descent -fm.ascent + viewHeight)/2 ;//完美居中算式哈哈哈
            float textTop =(fm.ascent-fm.descent+viewHeight)/2;
            Log.i("WANG",textHeight+"height");
            Log.i("WANG",viewWidth+"width");
           // canvas.drawText("sdadadqwdAawfawfas",0,20,mPaint);
            if((titleWidth + artistWidth) <= viewWidth) {
                Log.i("WANG",music.getTitle()+"view");
                canvas.drawText(music.getArtist(),textLeftX + titleWidth,textHeight,mPaint);
                mPaint.setTextSize(titleSize);
                canvas.drawText(music.getTitle(),textLeftX,textHeight,mPaint);
               // canvas.drawLine(0,textHeight,viewWidth,textHeight,mPaint);
                mPaint.setColor(Color.BLUE);
                Log.i("WANG",textTop+"ascent");
               // canvas.drawLine(0,textTop,viewWidth,textTop,mPaint);
            } else if(titleWidth >= viewWidth) {
                mPaint.setTextSize(titleSize);
                canvas.drawText(getAptString(viewWidth,music.getTitle()), textLeftX,textHeight,mPaint);
            } else {
                mPaint.setTextSize(titleSize);
                canvas.drawText(music.getTitle(), textLeftX ,textHeight,mPaint);
                mPaint.setTextSize(artistSize);
                canvas.drawText(getAptString(viewWidth - titleWidth,music.getArtist()), textLeftX + titleWidth,textHeight,mPaint);
            }
        }
    }
//得到适合viewWidth的字符串来显示
    private String  getAptString(float width,String str) {
        StringBuffer title = new StringBuffer( str + ELLIPISIS);
        int index = title.length()- 3;
        //对 string进行循环
        while(mPaint.measureText(title.toString()) >= width){
            title.deleteCharAt(index--);
        }

       return title.toString();
    }
}
