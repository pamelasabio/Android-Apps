package pamela.com.aircarbusiness.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;

import pamela.com.aircarbusiness.Fragments.MyCarsFragment;
import pamela.com.aircarbusiness.Fragments.NotificationFragment;
import pamela.com.aircarbusiness.Fragments.ReservationFragment;
import pamela.com.aircarbusiness.Fragments.TrackMyCarFragment;
import pamela.com.aircarbusiness.MainActivity;


/**
 * Created by pamela on 13/11/2016.
 */

public class MyAdapter extends FragmentStatePagerAdapter {

    int icons[] = {android.R.drawable.ic_menu_view,android.R.drawable.ic_input_add,android.R.drawable.ic_notification_overlay,android.R.drawable.ic_dialog_map};
    private Context context;

    public MyAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return MainActivity.getNumItems();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return MyCarsFragment.getInstance();
            case 1:
                return NotificationFragment.getInstance();
            case 2:
                return ReservationFragment.getInstance();
            case 3:
                return TrackMyCarFragment.getInstance();
            default:
                return MyCarsFragment.getInstance();
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Drawable drawable = ContextCompat.getDrawable(context, icons[position]);
        drawable.setBounds(0, 0, 120, 120);
        ImageSpan imageSpan = new ImageSpan(drawable);
        SpannableString spannableString = new SpannableString(" ");
        spannableString.setSpan(imageSpan, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }
}