package com.dxh.globe3d.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;

import com.dxh.globe3d.utils.GlobeUtil;
import com.dxh.globe3d.utils.Logger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by XHD on 2024/07/17
 */
public class GlobeParentView extends ViewGroup {
    private int measuredWidth = 0;
    private int measuredHeight = 0;
    private double maxEyeDistance;//最远可视距离
    private double eyeX, eyeY, eyeZ;//视角原点坐标(默认控件中心位置)
    private double globeCenterX, globeCenterY, globeCenterZ;//球心坐标
    private double globeRadius;//球半径
    private double globeToRootGap = 50;//球到边缘间隔(默认50)

    @Override
    public String toString() {
        return "GlobeView{" +
                "measuredWidth=" + measuredWidth +
                ", measuredHeight=" + measuredHeight +
                ", maxEyeDistance=" + maxEyeDistance +
                ", eyeX=" + eyeX +
                ", eyeY=" + eyeY +
                ", eyeZ=" + eyeZ +
                ", globeCenterX=" + globeCenterX +
                ", globeCenterY=" + globeCenterY +
                ", globeCenterZ=" + globeCenterZ +
                ", globeRadius=" + globeRadius +
                ", globeToRootGap=" + globeToRootGap +
                '}';
    }

    public GlobeParentView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measuredWidth = getMeasuredWidth();
        measuredHeight = getMeasuredHeight();
        if (globeRadius == 0) {
            globeRadius = Math.min(measuredWidth, measuredHeight) / 2 - globeToRootGap;
        }
        maxEyeDistance = globeRadius * 2;
        eyeX = measuredWidth / 2;
        eyeY = measuredHeight / 2;
        eyeZ = 0;
        globeCenterX = eyeX;
        globeCenterY = eyeY;
        globeCenterZ = globeRadius;
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        Logger.e(toString());
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            int childViewMeasuredWidth = childView.getMeasuredWidth();
            int childViewMeasuredHeight = childView.getMeasuredHeight();
            if (childView instanceof GlobeChildView) {
                GlobeChildView globeChildView = (GlobeChildView) childView;
                //计算视角中心点到子View中心点距离，来决定View大小
                double d = GlobeUtil.calculateTwoPointDistance(eyeX, eyeY, eyeZ, globeChildView.x, globeChildView.y, globeChildView.z);
                //由maxEyeDistance的值，决定近大远小效果。默认面最大，越往里越小，且越透明
                float scaleVlue = Math.max((float) (1.0f - d / maxEyeDistance), 0) * 0.5f + 0.5f;//缩放范围在0.5-1之间
                float alphaVlue = Math.max((float) (1.0f - d / maxEyeDistance), 0) * 0.2f + 0.8f;//透明度范围在0.8-1之间
                Logger.e("scaleVlue=" + scaleVlue + " d=" + d);
                globeChildView.setScaleX(scaleVlue);
                globeChildView.setScaleY(scaleVlue);
                globeChildView.setAlpha(alphaVlue);
                int left = (int) (globeChildView.x - childViewMeasuredWidth / 2);
                int top = (int) (globeChildView.y - childViewMeasuredHeight / 2);
                int right = left + childViewMeasuredWidth;
                int bottom = top + childViewMeasuredHeight;
                globeChildView.layout(left, top, right, bottom);
            } else {
                Logger.e("检测的子View(" + childView + ")非Custome3dChildView类型，请使用Custome3dChildView作为子View");
            }
        }
    }

    private Handler mHandler = new Handler();
    private int mStartX = 100, mStartY = 0, mEndX = 0, mEndY = 100;

    public void recoveryAnimal() {
        startAnimal(mStartX, mStartY, mEndX, mEndY);
    }

    @SuppressLint("NewApi")
    public void startAnimal(final int startX, final int startY, final int endX, final int endY) {
        mHandler.removeCallbacksAndMessages(null);
        Logger.e(startX + " " + startY + "-" + endX + " " + endY);
        mStartX = startX;
        mStartY = startY;
        mEndX = endX;
        mEndY = endY;
        if (startX == endX && startY == endY) {
            mStartX = 100;
            mStartY = 0;
            mEndX = 0;
            mEndY = 100;
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                rotateAxis(startX, startY, endX, endY, -1);
                startAnimal(startX, startY, endX, endY);
            }
        }, 100);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void rotateAxis(int startX, int startY, int endX, int endY, int angle) {
        if (startX == endX && startY == endY) {
            return;
        }
        List<GlobeChildView> globeChildViewList = new ArrayList<>();
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            if (childAt instanceof GlobeChildView) {
                globeChildViewList.add((GlobeChildView) childAt);
            }
        }
//        removeAllViews();
        globeChildViewList.sort(new Comparator<GlobeChildView>() {
            @Override
            public int compare(GlobeChildView o1, GlobeChildView o2) {
                return (int) (o2.z - o1.z);
            }
        });
        for (int i = 0; i < globeChildViewList.size(); i++) {
            GlobeChildView globeChildView = globeChildViewList.get(i);
            //更新绕轴坐标
            Logger.e(angle + " " + startX + "-" + startY + ":" + endX + "-" + endY);
            globeChildView.updateRotateAxisNewPoint(angle, startX, startY, endX, endY);
            //绕轴旋转end
//            addView(globeChildView);//频繁重新添加可能导致子view点击事件不触发
        }
        requestLayout();
    }

    public void stopAnimal() {
        mHandler.removeCallbacksAndMessages(null);
    }

    @SuppressLint("NewApi")
    private void scaleGlobe(double scaleVlue) {
        globeRadius = Math.min(Math.min(measuredWidth, measuredHeight) / 2 - globeToRootGap, globeRadius + scaleVlue * 0.5);
        globeRadius = Math.max(globeRadius, Math.min(measuredWidth, measuredHeight) / 4);
        Logger.e("globeRadius=" + globeRadius + " " + scaleVlue);
        requestLayout();
        updateZAllView();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void updateZAllView() {//按z高度重新添加view
        Logger.e("updateZAllView");
        List<GlobeChildView> globeChildViewList = new ArrayList<>();
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            if (childAt instanceof GlobeChildView) {
                globeChildViewList.add((GlobeChildView) childAt);
            }
        }
        removeAllViews();
        globeChildViewList.sort(new Comparator<GlobeChildView>() {
            @Override
            public int compare(GlobeChildView o1, GlobeChildView o2) {
                return (int) (o2.z - o1.z);
            }
        });
        for (int i = 0; i < globeChildViewList.size(); i++) {
            GlobeChildView globeChildView = globeChildViewList.get(i);
            addView(globeChildView);
        }
    }

    List<Point> pointList = new ArrayList<>();

    private float dispatchTouchEventDownX, dispatchTouchEventDownY;
    private boolean isInterceptTouchEvent = false;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Logger.e(event.toString());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                dispatchTouchEventDownX = event.getX();
                dispatchTouchEventDownY = event.getY();
                isInterceptTouchEvent = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(event.getX() - dispatchTouchEventDownX) > 2 || Math.abs(event.getY() - dispatchTouchEventDownY) > 2) {
                    isInterceptTouchEvent = true;//如果是滑动事件进行拦截，交给父view处理
                }
                break;
            case MotionEvent.ACTION_UP:

                break;
        }

        return super.dispatchTouchEvent(event);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Logger.e(isInterceptTouchEvent + " " + ev.toString());
        return super.onInterceptTouchEvent(ev) || isInterceptTouchEvent;
    }

    private float pointerX1, pointerY1, pointerX2, pointerY2;
    private double pointDistance;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int pointerCount = event.getPointerCount();//触控数量
        Logger.e(pointerCount + " Action=" + event.getActionMasked() + " " + event.toString());
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                if (pointerCount == 1) {
                    pointList.clear();
                    updateZAllView();
                    stopAnimal();
                    pointList.add(new Point((int) event.getRawX(), (int) event.getRawY()));
                } else if (pointerCount == 2) {
                    pointerX1 = event.getX(0);
                    pointerY1 = event.getY(0);
                    pointerX2 = event.getX(1);
                    pointerY2 = event.getY(1);
                    pointDistance = Math.sqrt(Math.pow(pointerX1 - pointerX2, 2) + Math.pow(pointerY1 - pointerY2, 2));
                    Logger.e(pointerCount + " pointDistance=" + pointDistance);
                }
                return true;//如果未被子view消费或事件不经过子view，则自己处理
            case MotionEvent.ACTION_MOVE:
                if (pointerCount == 1) {
                    stopAnimal();
                    if (pointList.size() >= 2) {
                        pointList.remove(0);
                    }
                    pointList.add(new Point((int) event.getRawX(), (int) event.getRawY()));
                    if (pointList.size() >= 2) {//防止子View拦截了down事件，之后父View又把事件拦截交给父View处理，导致pointList只有1
                        rotateAxis(pointList.get(0).x, pointList.get(0).y, pointList.get(1).x, pointList.get(1).y, -3);
                    } else {
                        Logger.e("pointList.size 过短");
                    }
                } else if (pointerCount == 2) {
                    pointerX1 = event.getX(0);
                    pointerY1 = event.getY(0);
                    pointerX2 = event.getX(1);
                    pointerY2 = event.getY(1);
                    Logger.e(pointerX1 + "-" + pointerY1 + " " + pointerX2 + "-" + pointerY2);

                    scaleGlobe(Math.sqrt(Math.pow(pointerX1 - pointerX2, 2) + Math.pow(pointerY1 - pointerY2, 2)) - pointDistance);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                if (pointerCount == 1) {
                    if (pointList.size() >= 2) {
                        startAnimal(pointList.get(0).x, pointList.get(0).y, pointList.get(1).x, pointList.get(1).y);
                    } else {
                        recoveryAnimal();
                    }
                    pointList.clear();
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    public double getMaxEyeDistance() {
        return maxEyeDistance;
    }

    public double getEyeX() {
        return eyeX;
    }

    public double getEyeY() {
        return eyeY;
    }

    public double getEyeZ() {
        return eyeZ;
    }

    public double getGlobeCenterX() {
        return globeCenterX;
    }

    public double getGlobeCenterY() {
        return globeCenterY;
    }

    public double getGlobeCenterZ() {
        return globeCenterZ;
    }

    public double getGlobeRadius() {
        return globeRadius;
    }

    public double getGlobeToRootGap() {
        return globeToRootGap;
    }
}
