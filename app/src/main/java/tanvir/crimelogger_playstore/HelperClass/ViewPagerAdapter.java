package tanvir.crimelogger_playstore.HelperClass;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by USER on 01-Mar-18.
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    ArrayList<Fragment> fragments = new ArrayList<>();
    ArrayList<String> tabTitles = new ArrayList<>();


    public void addFragments(Fragment fragment, String titles) {
        this.fragments.add(fragment);
        this.tabTitles.add(titles);
    }

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles.get(position);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;

    }

    public void replaceFragment(Fragment fragment) {
        fragments.remove(0);
        fragments.add(0, fragment);
        tabTitles.remove(0);
        tabTitles.add(0,"Home");
        notifyDataSetChanged();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        /*frags[position] = (Fragment) super.instantiateItem(container, position);
        return frags[position];*/

        Fragment fragment=(Fragment) super.instantiateItem(container, position);
        fragments.remove(position);
        fragments.add(position,fragment);

        return fragments.get(position);
    }
}
