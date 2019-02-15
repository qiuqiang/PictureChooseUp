package com.example.picture.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

/*Created by 邱强 on 2019/2/2.
 * E-Mail 2536555456@qq.com
 */
public class ImageDelete extends android.support.v7.widget.AppCompatImageView {
    private int deleteInitWidth = 32;
    private int deleteInitHeight = 32;

    public ImageDelete(Context context) {
        super(context);
        initView(context, null, 0);
    }

    public ImageDelete(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs, 0);
    }

    public ImageDelete(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr) {

    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
    }
}
