/*
 * Copyright 2018 Coffee and Cream Studios
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.coffeegerm.glucoseguide.ui.statistics.children;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.coffeegerm.glucoseguide.GlucoseGuide;
import io.github.coffeegerm.glucoseguide.R;
import io.github.coffeegerm.glucoseguide.data.DatabaseManager;

/**
 * Fragment used with Statistics ViewPager to show
 * the last seven days of statistics
 */

public class SevenDayStatisticsFragment extends Fragment {
  
  @Inject
  public DatabaseManager databaseManager;
  
  @BindView(R.id.seven_days_average)
  TextView average;
  @BindView(R.id.seven_days_highest)
  TextView highest;
  @BindView(R.id.seven_days_lowest)
  TextView lowest;
  
  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    GlucoseGuide.syringe.inject(this);
  }
  
  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View sevenDaysView = inflater.inflate(R.layout.fragment_seven_days_stats, container, false);
    ButterKnife.bind(this, sevenDaysView);
    setValues();
    return sevenDaysView;
  }
  
  private void setValues() {
    if (databaseManager.getAllFromDate(getSevenDaysAgo()).size() == 0) {
      average.setText(R.string.dash);
      highest.setText(R.string.dash);
      lowest.setText(R.string.dash);
    } else {
      average.setText(String.valueOf(databaseManager.getAverageGlucose(getSevenDaysAgo())));
      highest.setText(String.valueOf(databaseManager.getHighestGlucose(getSevenDaysAgo())));
      lowest.setText(String.valueOf(databaseManager.getLowestGlucose(getSevenDaysAgo())));
    }
  }
  
  public Date getSevenDaysAgo() {
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DATE, -7);
    return calendar.getTime();
  }
}
