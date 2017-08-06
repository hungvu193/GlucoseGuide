package io.github.coffeegerm.materiallogbook.ui.StatisticsDataFragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.coffeegerm.materiallogbook.R;
import io.github.coffeegerm.materiallogbook.model.EntryItem;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by David Yarzebinski on 7/28/2017.
 * <p>
 * Fragment used with Statistics ViewPager to show
 * the last seven days of statistics
 */

public class SevenDayStatisticsFragment extends Fragment {

    private static final String TAG = "SevenDaysStatistics";
    private String pageTitle;
    private int pageNumber;
    private Realm realm;
    @BindView(R.id.seven_days_average)
    TextView averageBloodGlucose;
    @BindView(R.id.seven_days_highest)
    TextView highestBloodGlucose;
    @BindView(R.id.seven_days_lowest)
    TextView lowestBloodGlucose;
    @BindView(R.id.seven_days_average_label)
    TextView averageLabel;
    @BindView(R.id.seven_days_highest_label)
    TextView highestLabel;
    @BindView(R.id.seven_days_lowest_label)
    TextView lowestLabel;

    public static SevenDayStatisticsFragment newInstance(int pageNumber, String pageTitle) {
        SevenDayStatisticsFragment sevenDayStatisticsFragment = new SevenDayStatisticsFragment();
        Bundle args = new Bundle();
        args.putInt("pageNumber", pageNumber);
        args.putString("pageTitle", pageTitle);
        sevenDayStatisticsFragment.setArguments(args);
        return sevenDayStatisticsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageTitle = getArguments().getString("pageTitle");
        pageNumber = getArguments().getInt("pageNumber");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View sevenDaysView = inflater.inflate(R.layout.fragment_seven_days_stats, container, false);
        ButterKnife.bind(this, sevenDaysView);
        setFonts();
        realm = Realm.getDefaultInstance();
        Date sevenDaysAgo = getSevenDaysAgo();
        RealmResults<EntryItem> entriesFromLastWeek = realm.where(EntryItem.class).greaterThan("date", sevenDaysAgo).greaterThan("bloodGlucose", 0).findAll();

        if (entriesFromLastWeek.size() == 0) {
            averageBloodGlucose.setText(R.string.dash);
            highestBloodGlucose.setText(R.string.dash);
            lowestBloodGlucose.setText(R.string.dash);
        } else {
            averageBloodGlucose.setText(String.valueOf(getAverageGlucose(sevenDaysAgo)));
            highestBloodGlucose.setText(String.valueOf(getHighestGlucose(sevenDaysAgo)));
            lowestBloodGlucose.setText(String.valueOf(getLowestGlucose(sevenDaysAgo)));
        }

        return sevenDaysView;
    }

    public int getAverageGlucose(Date sevenDaysAgo) {
        int total = 0;
        RealmResults<EntryItem> entriesFromLastWeek = realm.where(EntryItem.class).greaterThan("date", sevenDaysAgo).greaterThan("bloodGlucose", 0).findAll();
        for (int position = 0; position < entriesFromLastWeek.size(); position++) {
            EntryItem currentItem = entriesFromLastWeek.get(position);
            total += currentItem.getBloodGlucose();
        }
        return total / entriesFromLastWeek.size();
    }

    public int getHighestGlucose(Date sevenDaysAgo) {
        int highest = 0;
        RealmResults<EntryItem> entriesFromPastWeek = realm.where(EntryItem.class).greaterThan("date", sevenDaysAgo).greaterThan("bloodGlucose", 0).findAll();
        for (int position = 0; position < entriesFromPastWeek.size(); position++) {
            EntryItem currentItem = entriesFromPastWeek.get(position);
            if (currentItem.getBloodGlucose() > highest) {
                highest = currentItem.getBloodGlucose();
            }
        }
        return highest;
    }

    public int getLowestGlucose(Date sevenDaysAgo) {
        int lowest = 1000;
        RealmResults<EntryItem> entriesFromLastWeek = realm.where(EntryItem.class).greaterThan("date", sevenDaysAgo).greaterThan("bloodGlucose", 0).findAll();
        for (int position = 0; position < entriesFromLastWeek.size(); position++) {
            EntryItem currentItem = entriesFromLastWeek.get(position);
            if (currentItem.getBloodGlucose() < lowest) {
                lowest = currentItem.getBloodGlucose();
            }
        }
        return lowest;
    }

    public Date getSevenDaysAgo() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -7);
        return calendar.getTime();
    }

    private void setFonts() {
        Typeface avenirRegular = Typeface.createFromAsset(getContext().getAssets(), "fonts/AvenirNext-Regular.otf");
        Typeface avenirMedium = Typeface.createFromAsset(getContext().getAssets(), "fonts/AvenirNext-Medium.otf");
        averageBloodGlucose.setTypeface(avenirMedium);
        highestBloodGlucose.setTypeface(avenirMedium);
        lowestBloodGlucose.setTypeface(avenirMedium);
        averageLabel.setTypeface(avenirRegular);
        highestLabel.setTypeface(avenirRegular);
        lowestLabel.setTypeface(avenirRegular);
    }
}