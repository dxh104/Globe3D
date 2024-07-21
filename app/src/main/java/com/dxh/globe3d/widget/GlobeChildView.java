package com.dxh.globe3d.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.ViewParent;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.dxh.globe3d.R;
import com.dxh.globe3d.utils.GlobeUtil;
import com.dxh.globe3d.utils.Logger;

/**
 * Created by XHD on 2024/07/17
 * 子view
 */
public class GlobeChildView extends ConstraintLayout {
    public double x, y, z;//空间坐标
    public double angleA, angleB, globeRadius;//球坐标 方位角，仰角,球半径
    public double globeCenterX, globeCenterY, globeCenterZ;//球心坐标

    @Override
    public String toString() {
        return "ChildGlobeView{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", angleA=" + angleA +
                ", angleB=" + angleB +
                ", globeRadius=" + globeRadius +
                ", globeCenterX=" + globeCenterX +
                ", globeCenterY=" + globeCenterY +
                ", globeCenterZ=" + globeCenterZ +
                '}';
    }

    public GlobeChildView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public void init(Context context, @Nullable AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.GlobeChildView);
        angleA = typedArray.getFloat(R.styleable.GlobeChildView_globeChildView_angleA, 0);
        angleB = typedArray.getFloat(R.styleable.GlobeChildView_globeChildView_angleB, 0);
        typedArray.recycle();
        Logger.e(toString());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        ViewParent parent = getParent();
        if (parent instanceof GlobeParentView) {
            globeCenterX = ((GlobeParentView) parent).getGlobeCenterX();
            globeCenterY = ((GlobeParentView) parent).getGlobeCenterY();
            globeCenterZ = ((GlobeParentView) parent).getGlobeCenterZ();
            globeRadius = ((GlobeParentView) parent).getGlobeRadius();
        }
        setAngleABR(angleA,angleB);//更新空间坐标
        Logger.e(toString());
    }

    public void setXYZ(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        double[] abr = GlobeUtil.convertRoomToGlobePoint(globeCenterX, globeCenterY, globeCenterZ, x, y, z);
        this.angleA = abr[0];
        this.angleB = abr[1];
        this.globeRadius = abr[2];
    }

    /**
     *
     * @param angleA 方位角
     * @param angleB 仰角
     */
    public void setAngleABR(double angleA, double angleB) {
        this.angleA = angleA;
        this.angleB = angleB;
        double[] xyz = GlobeUtil.convertGlobeToRoomPoint(globeCenterX, globeCenterY, globeCenterZ, angleA, angleB, globeRadius);
        this.x = xyz[0];
        this.y = xyz[1];
        this.z = xyz[2];
    }

    public void updateRotateAxisNewPoint(int angle, int startX, int startY, int endX, int endY) {
        double[] calculateRotateAxisNewPoint2 = GlobeUtil.calculateRotateAxisNewPoint2(angle, x, y, z, globeCenterX, globeCenterY, globeCenterZ, startX, startY, 0, endX, endY, 0);
        setXYZ(calculateRotateAxisNewPoint2[0], calculateRotateAxisNewPoint2[1], calculateRotateAxisNewPoint2[2]);
    }

}
