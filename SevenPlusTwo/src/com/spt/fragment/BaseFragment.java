package com.spt.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseFragment extends Fragment{

	public FragmentActivity mActivity;
	  
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	mActivity = getActivity();
    	return super.onCreateView(inflater, container, savedInstanceState);
    }
  
}
