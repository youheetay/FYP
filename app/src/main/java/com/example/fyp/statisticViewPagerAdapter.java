package com.example.fyp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;


public class statisticViewPagerAdapter extends FragmentStateAdapter {

    public statisticViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position){

            default:
                return new ReportFragment();
        }
    }

    @Override
    public int getItemCount()  {
        return 1;
    }
}
