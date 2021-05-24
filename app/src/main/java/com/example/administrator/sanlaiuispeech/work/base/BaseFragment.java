package com.example.administrator.sanlaiuispeech.work.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;


/**
 * Fragment预加载问题的解决方案：
 * 1.可以懒加载的Fragment
 * 2.切换到其他页面时停止加载数据（可选）
 */
public abstract class BaseFragment extends Fragment {

    /**
     * 视图是否已经初初始化
     */
    protected boolean isInit = false;
    protected boolean isLoad = false;
    private View view;

    protected Activity mActivity;
    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    /**
     * 获得全局的，防止使用getActivity()为空
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = (Activity) context;
    }

    //    @Subscribe
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (view == null) {
            view = inflater.inflate(setLayoutId(), container, false);
            ButterKnife.bind(this, view);
            initViews();
        }

        isInit = true;
        /**初始化的时候去加载数据**/
        isCanLoadData();

        // 缓存的rootView需要判断是否已经被加过parent，如果有parent需要从parent删除，
        // 要不然会发生这个rootview已经有parent的错误。
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }


        return view;
    }

    /**
     * 视图是否已经对用户可见，系统的方法
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isCanLoadData();
    }

    /**
     * 是否可以加载数据
     * 可以加载数据的条件：
     * 1.视图已经初始化
     * 2.视图对用户可见
     */
    public void isCanLoadData() {
        if (!isInit) {
            return;
        }

        if (getUserVisibleHint() == !isLoad) {
            lazyLoad();
        } else {
            stopLoad();
//            if (isLoad) {
//                stopLoad();
//            }
        }
    }

    /**
     * 视图销毁的时候讲Fragment是否初始化的状态变为false
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isInit = false;
        isLoad = false;


    }

    /**
     * 设置Fragment要显示的布局
     *
     * @return 布局的layoutId
     */
    protected abstract int setLayoutId();

    /**
     * 获取设置的布局
     *
     * @return
     */
    protected View getLayoutId() {
        return view;
    }

    /**
     * 找出对应的控件
     *
     * @param id
     * @param <T>
     * @return
     */
    protected <T extends View> T findViewById(int id) {

        return (T) getLayoutId().findViewById(id);
    }

    protected abstract void initViews();

    /**
     * 当视图初始化并且对用户可见的时候去真正的加载数据
     */
    public abstract void lazyLoad();

    /**
     * 当视图已经对用户不可见并且加载过数据，如果需要在切换到其他页面时停止加载数据，可以调用此方法
     */
    protected void stopLoad() {

    }

}
