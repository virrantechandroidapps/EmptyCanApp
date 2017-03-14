package com.meshyog.emptycan.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.AttributeSet;

import com.android.volley.toolbox.NetworkImageView;
import com.meshyog.emptycan.R;

/**
 * Created by varadhan on 18-12-2016.
 */
public class RoundedNetworkImageView extends NetworkImageView {
    private float f20105a;
    private int f20106b;
    private Context f20107c;
    private Paint f20108d;
    private Paint f20109e;
    private boolean f20110f;
    private int f20111g;
    private int f20112h;

    public RoundedNetworkImageView(Context context) {
        this(context, null);
    }

    public RoundedNetworkImageView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public RoundedNetworkImageView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.f20107c = context;
    }

    public void setImageBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            Drawable a = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
            //a.set( (float) bitmap.getWidth());
            setImageDrawable(a);
        }
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.f20110f) {
            canvas.drawCircle((float) this.f20111g, (float) this.f20112h, (float) this.f20106b, this.f20108d);
            canvas.drawCircle((float) this.f20111g, (float) this.f20112h, ((float) this.f20106b) - this.f20105a, this.f20109e);
        }
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        this.f20111g = getWidth() >> 1;
        this.f20112h = getHeight() >> 1;
        this.f20106b = (int) (((float) (getWidth() >> 1)) - this.f20105a);
    }

    public void m27437a(float f, int i) {
        this.f20110f = true;
        this.f20105a = f;
        this.f20108d = new Paint(i);
        this.f20108d.setStrokeWidth(f);
        this.f20108d.setColor(getResources().getColor(i));
        this.f20108d.setStyle(Paint.Style.STROKE);
        this.f20108d.setFlags(1);
        this.f20109e = new Paint(R.color.readColor);
        this.f20109e.setStrokeWidth(f);
        this.f20109e.setColor(getResources().getColor(R.color.readColor));
        this.f20109e.setStyle(Paint.Style.STROKE);
        this.f20109e.setFlags(1);
    }
}