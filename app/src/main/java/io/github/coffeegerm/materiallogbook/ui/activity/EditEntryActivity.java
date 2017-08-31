package io.github.coffeegerm.materiallogbook.ui.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.coffeegerm.materiallogbook.R;
import io.github.coffeegerm.materiallogbook.model.EntryItem;
import io.realm.Realm;
import io.realm.RealmResults;

import static io.github.coffeegerm.materiallogbook.utils.Utilities.checkTimeString;

/**
 * Created by dyarz on 8/17/2017.
 * <p>
 * Activity created to allow user to edit their entries
 * already present in database
 */

public class EditEntryActivity extends AppCompatActivity {

    private static final String TAG = "EditEntryActivity";
    static String itemId = "itemId";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.cancel)
    Button cancel;
    @BindView(R.id.update)
    Button update;
    @BindView(R.id.new_entry_date)
    EditText date;
    @BindView(R.id.new_entry_time)
    EditText time;
    @BindView(R.id.new_entry_blood_glucose_level)
    EditText bloodGlucose;
    @BindView(R.id.new_entry_carbohydrates_amount)
    EditText carbohydrates;
    @BindView(R.id.new_entry_insulin_units)
    EditText insulin;
    @BindView(R.id.breakfast_status)
    ImageButton breakfast;
    @BindView(R.id.lunch_status)
    ImageButton lunch;
    @BindView(R.id.dinner_status)
    ImageButton dinner;
    @BindView(R.id.sick_status)
    ImageButton sick;
    @BindView(R.id.exercise_status)
    ImageButton exercise;
    @BindView(R.id.sweets_status)
    ImageButton sweets;
    Date itemDate;
    Handler handler;
    int status;
    private Realm realm;
    private String itemIdString;
    private EntryItem item;
    private Calendar calendar;
    private Calendar calendarToBeSaved;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
    private SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa", Locale.US);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (MainActivity.sharedPreferences.getBoolean("pref_dark_mode", false))
            setTheme(R.style.AppTheme_Dark);
        setContentView(R.layout.activity_edit_entry);
        ButterKnife.bind(this);
        realm = Realm.getDefaultInstance();
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(R.string.edit_entry_toolbar);
        realm = Realm.getDefaultInstance();
        itemIdString = getIntent().getStringExtra(itemId);
        item = getItem();
        calendarToBeSaved = Calendar.getInstance();
        calendar = Calendar.getInstance();
        calendar.setTime(item.getDate());
        setHints();

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(EditEntryActivity.this,
                        (Build.VERSION.SDK_INT >= 21 ? android.R.style.Theme_Material_Dialog_Alert
                                : android.R.style.Theme_Holo_Light_Dialog),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                month++;
                                date.setText(dateFix(month, dayOfMonth, year));
                                month--;
                                calendarToBeSaved.set(year, month, dayOfMonth);
                            }
                        }, calendar.get(Calendar.YEAR), // year
                        calendar.get(Calendar.MONTH), // month
                        calendar.get(Calendar.DAY_OF_MONTH)); // day

                if (Build.VERSION.SDK_INT < 21)
                    if (dialog.getWindow() != null)
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

                dialog.show();
            }
        });

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(EditEntryActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                time.setText(checkTimeString(hourOfDay, minute));
                                calendarToBeSaved.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendarToBeSaved.set(Calendar.MINUTE, minute);
                            }
                        },
                        calendar.get(Calendar.HOUR_OF_DAY), // current hour
                        calendar.get(Calendar.MINUTE), // current minute
                        false); //no 24 hour view
                timePickerDialog.show();
            }
        });

        breakfast.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                status = 1;
                Log.i(TAG, "status: " + status);
                setStatus(status);
                final Toast toast = Toast.makeText(getApplicationContext(), "Breakfast", Toast.LENGTH_SHORT);
                toast.show();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toast.cancel();
                    }
                }, 700);
            }
        });

        lunch.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                status = 2;
                Log.i(TAG, "status: " + status);
                setStatus(status);
                final Toast toast = Toast.makeText(getApplicationContext(), "Lunch", Toast.LENGTH_SHORT);
                toast.show();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toast.cancel();
                    }
                }, 700);
            }
        });

        dinner.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                status = 3;
                Log.i(TAG, "status: " + status);
                setStatus(status);
                final Toast toast = Toast.makeText(getApplicationContext(), "Dinner", Toast.LENGTH_SHORT);
                toast.show();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toast.cancel();
                    }
                }, 700);
            }
        });

        sick.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                status = 4;
                Log.i(TAG, "status: " + status);
                setStatus(status);
                final Toast toast = Toast.makeText(getApplicationContext(), "Sick", Toast.LENGTH_SHORT);
                toast.show();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toast.cancel();
                    }
                }, 700);
            }
        });

        exercise.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                status = 5;
                Log.i(TAG, "status: " + status);
                setStatus(status);
                final Toast toast = Toast.makeText(getApplicationContext(), "Exercise", Toast.LENGTH_SHORT);
                toast.show();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toast.cancel();
                    }
                }, 700);
            }
        });

        sweets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                status = 6;
                Log.i(TAG, "status: " + status);
                setStatus(status);
                final Toast toast = Toast.makeText(getApplicationContext(), "Sweets", Toast.LENGTH_SHORT);
                toast.show();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toast.cancel();
                    }
                }, 700);
            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateEntry();
            }
        });
    }

    private EntryItem getItem() {
        RealmResults<EntryItem> itemRealmResults = realm.where(EntryItem.class).equalTo("id", itemIdString).findAll();
        return itemRealmResults.get(0);
    }

    private void setHints() {
        itemDate = calendar.getTime();
        String formattedDate = dateFormat.format(itemDate);
        String formattedTime = timeFormat.format(itemDate);
        date.setHint(formattedDate);
        time.setHint(formattedTime);
        bloodGlucose.setHint(String.valueOf(item.getBloodGlucose()));
        if (item.getCarbohydrates() != 0)
            carbohydrates.setHint(String.valueOf(item.getCarbohydrates()));
        else carbohydrates.setHint(R.string.dash);
        if (item.getInsulin() != 0.0)
            insulin.setHint(String.valueOf(item.getInsulin()));
        else insulin.setHint(R.string.dash);
        setStatus(item.getStatus());
    }

    private void setStatus(int status) {
        Drawable status_checked = getResources().getDrawable(R.drawable.status_checked);
        Drawable status_unchecked = getResources().getDrawable(R.drawable.status_unchecked);
        switch (status) {
            case 1:
                breakfast.setBackground(status_checked);
                lunch.setBackground(status_unchecked);
                dinner.setBackground(status_unchecked);
                sick.setBackground(status_unchecked);
                exercise.setBackground(status_unchecked);
                sweets.setBackground(status_unchecked);
                break;
            case 2:
                breakfast.setBackground(status_unchecked);
                lunch.setBackground(status_checked);
                dinner.setBackground(status_unchecked);
                sick.setBackground(status_unchecked);
                exercise.setBackground(status_unchecked);
                sweets.setBackground(status_unchecked);
                break;
            case 3:
                breakfast.setBackground(status_unchecked);
                lunch.setBackground(status_unchecked);
                dinner.setBackground(status_checked);
                sick.setBackground(status_unchecked);
                exercise.setBackground(status_unchecked);
                sweets.setBackground(status_unchecked);
                break;
            case 4:
                breakfast.setBackground(status_unchecked);
                lunch.setBackground(status_unchecked);
                dinner.setBackground(status_unchecked);
                sick.setBackground(status_checked);
                exercise.setBackground(status_unchecked);
                sweets.setBackground(status_unchecked);
                break;
            case 5:
                breakfast.setBackground(status_unchecked);
                lunch.setBackground(status_unchecked);
                dinner.setBackground(status_unchecked);
                sick.setBackground(status_unchecked);
                exercise.setBackground(status_checked);
                sweets.setBackground(status_unchecked);
                break;
            case 6:
                breakfast.setBackground(status_unchecked);
                lunch.setBackground(status_unchecked);
                dinner.setBackground(status_unchecked);
                sick.setBackground(status_unchecked);
                exercise.setBackground(status_unchecked);
                sweets.setBackground(status_checked);
                break;
            default:
                break;
        }
    }

    StringBuilder dateFix(int month, int day, int year) {
        return new StringBuilder().append(month).append("/").append(day).append("/").append(year);
    }

    private void updateEntry() {
        // TODO: 8/27/2017 Check if anything the user put is different and update as needed.
        finish();
    }

    @Override
    public void onDestroy() {
        realm.close();
        super.onDestroy();
    }
}
