package com.example.fyp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;


public class insightViewPagerAdapter extends FragmentStateAdapter {

    public insightViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position){
            case 0:
                return new FragmentStatistic();
            case 1:
                return new FragmentSavingPlan();
            default:
                return new FragmentStatistic();
        }
    }

    @Override
    public int getItemCount()  {
        return 2;
    }
}
