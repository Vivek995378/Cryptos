package bitcoin.collector.collection.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import bitcoin.collector.collection.fragment.ConverterFragment;
import bitcoin.collector.collection.fragment.PriceFragment;
import bitcoin.collector.collection.fragment.RewardsFragment;
import bitcoin.collector.collection.fragment.WithdrawFragment;


public class FragmentAdapter extends FragmentPagerAdapter {

    public FragmentAdapter(@NonNull  FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position)
        {
            case 0: return new PriceFragment();
            case 1: return new WithdrawFragment();
            case 2: return new ConverterFragment();
            case 3: return new RewardsFragment();

            default: return new PriceFragment();
        }

    }

    @Override
    public int getCount() {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;

        if(position == 0) {
            title = "Price";
        }
        else if(position == 1){
            title = "Withdraw";
        }
        else if(position == 2){
            title = "Convert";
        }
        else if(position == 3){
            title = "Rewards";
        }

        return title;
    }
}