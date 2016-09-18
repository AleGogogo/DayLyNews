package fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by LYW on 2016/9/16.
 */
public abstract class BaseFragment extends Fragment {
    protected Activity mActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup
            container, @Nullable Bundle savedInstanceState) {
         mActivity = getActivity();

        return initView(inflater, container, savedInstanceState);
    }

    protected abstract View initView(LayoutInflater inflater, ViewGroup 
            container, Bundle savedInstanceState);

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    protected  void initData(){}

    @Override
    public void onDestroy() {
        super.onDestroy();
        mActivity = null;
    }
}
