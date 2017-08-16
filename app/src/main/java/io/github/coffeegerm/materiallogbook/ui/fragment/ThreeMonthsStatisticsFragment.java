package io.github.coffeegerm.materiallogbook.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.coffeegerm.materiallogbook.R;
import io.github.coffeegerm.materiallogbook.model.EntryItem;
import io.github.coffeegerm.materiallogbook.ui.activity.MainActivity;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by dyarz on 8/15/2017.
 * <p>
 * Three Month stats fragment to show users their data
 * for that time
 */

public class ThreeMonthsStatisticsFragment extends Fragment {
    private static final String TAG = "ThreeMonthsStatistics";
    @BindView(R.id.imgAvg)
    ImageView ivAvg;
    @BindView(R.id.imgUpArrow)
    ImageView ivUpArrow;
    @BindView(R.id.imgDownArrow)
    ImageView ivDownArrow;
    @BindView(R.id.ivA1C)
    ImageView ivA1C;
    private Realm realm;

    public static ThreeMonthsStatisticsFragment newInstance(int pageNumber, String pageTitle) {
        ThreeMonthsStatisticsFragment threeMonthsStatisticsFragment = new ThreeMonthsStatisticsFragment();
        Bundle args = new Bundle();
        args.putInt("pageNumber", pageNumber);
        args.putString("pageTitle", pageTitle);
        threeMonthsStatisticsFragment.setArguments(args);
        return threeMonthsStatisticsFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View threeMonths = inflater.inflate(R.layout.fragment_three_months_statistics, container, false);
        ButterKnife.bind(this, threeMonths);
        realm = Realm.getDefaultInstance();
        setImages();
        return threeMonths;
    }

    public Date getThreeMonthsAgo() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -90);
        return calendar.getTime();
    }

    public int getAverage(Date threeMonthsAgo) {
        int total = 0;
        RealmResults<EntryItem> entriesFromLastThreeDays = realm.where(EntryItem.class).greaterThan("date", threeMonthsAgo).greaterThan("bloodGlucose", 0).findAll();
        for (int position = 0; position < entriesFromLastThreeDays.size(); position++) {
            EntryItem currentItem = entriesFromLastThreeDays.get(position);
            total += currentItem.getBloodGlucose();
        }
        return total / entriesFromLastThreeDays.size();
    }

    public double getA1C(int average) {
        return (46.7 + average) / 28.7;
        // A1c = (46.7 + average_blood_glucose) / 28.7
    }

    private void setImages() {
        if (MainActivity.sharedPreferences.getBoolean("pref_dark_mode", false)) {
            ivAvg.setImageResource(R.drawable.ic_average_dark);
            ivUpArrow.setImageResource(R.drawable.ic_up_arrow_dark);
            ivDownArrow.setImageResource(R.drawable.ic_down_arrow_dark);
            ivA1C.setImageResource(R.drawable.ic_a1c_dark);
        }
    }

}
