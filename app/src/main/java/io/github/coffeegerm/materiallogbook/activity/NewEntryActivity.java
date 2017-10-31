package io.github.coffeegerm.materiallogbook.activity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.coffeegerm.materiallogbook.R;
import io.github.coffeegerm.materiallogbook.model.EntryItem;
import io.github.coffeegerm.materiallogbook.utils.NotificationPublisher;
import io.realm.Realm;

import static io.github.coffeegerm.materiallogbook.activity.MainActivity.sharedPreferences;
import static io.github.coffeegerm.materiallogbook.utils.Constants.BOLUS_RATIO;
import static io.github.coffeegerm.materiallogbook.utils.Constants.NOTIFICATION;
import static io.github.coffeegerm.materiallogbook.utils.Constants.NOTIFICATION_ID;
import static io.github.coffeegerm.materiallogbook.utils.Constants.PREF_DARK_MODE;
import static io.github.coffeegerm.materiallogbook.utils.Utilities.checkTimeString;

/**
 * Created by David Yarzebinski on 6/25/2017.
 * <p>
 * Activity used to create a new Entry added the Database
 */

public class NewEntryActivity extends AppCompatActivity {

    private static final String TAG = "NewEntryActivity";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.cancelBtn)
    Button cancelBtn;
    @BindView(R.id.saveBtn)
    Button saveBtn;
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
    @BindView(R.id.new_entry_date_time_label)
    TextView dateTimeLabel;
    @BindView(R.id.new_entry_glucose_label)
    TextView glucoseLabel;
    @BindView(R.id.new_entry_carbs_label)
    TextView carbsLabel;
    @BindView(R.id.new_entry_insulin_label)
    TextView insulinLabel;
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
    @BindView(R.id.reminder_alarm)
    EditText reminder;
    @BindView(R.id.insulin_suggestion)
    LinearLayout insulinSuggestionLinearLayout;
    @BindView(R.id.insulin_suggestion_label)
    TextView insulinSuggestionLabel;
    @BindView(R.id.insulin_suggestion_value)
    TextView insulinSuggestionValue;
    @BindView(R.id.insulin_suggestion_value_label)
    TextView insulinSuggestionValueLabel;
    Handler handler;
    private Realm realm;
    private Calendar calendarToBeSaved;
    private Calendar calendar;
    private Calendar alarmCalendar;
    private int status = 0;
    private boolean wantsReminder = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: NewEntryActivity started");
        if (sharedPreferences.getBoolean(PREF_DARK_MODE, false))
            setTheme(R.style.AppTheme_Dark);
        setContentView(R.layout.activity_new_entry);
        ButterKnife.bind(this);
        realm = Realm.getDefaultInstance();
        setFonts();
        handler = new Handler();
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setTitle(R.string.create_entry);
        calendar = Calendar.getInstance();
        // Calendar for saving entered Date and Time
        calendarToBeSaved = Calendar.getInstance();

        // Set date and time to current date and time on initial create
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        month++;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        date.setText(dateFix(month, day, year));
        time.setText(checkTimeString(hour, minute));

        carbohydrates.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable carbValue) {
                if (!carbValue.toString().equals("")) {
                    insulinSuggestionLinearLayout.setVisibility(View.VISIBLE);
                    calculateInsulin(Float.parseFloat(carbValue.toString()));
                } else {
                    insulinSuggestionLinearLayout.setVisibility(View.GONE);
                }
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(NewEntryActivity.this,
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
                TimePickerDialog timePickerDialog = new TimePickerDialog(NewEntryActivity.this,
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
                statusButtonCheck(status);
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
                statusButtonCheck(status);
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
                statusButtonCheck(status);
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
                statusButtonCheck(status);
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
                statusButtonCheck(status);
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

        sweets.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                status = 6;
                Log.i(TAG, "status: " + status);
                statusButtonCheck(status);
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

        reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alarmTimePicker();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                saveEntry();
            }
        });
    }

    private void saveEntry() {
        // Checks to make sure there is a blood glucose given.
        if (bloodGlucose.getText().toString().equals(""))
            Toast.makeText(NewEntryActivity.this, R.string.no_glucose_toast, Toast.LENGTH_SHORT).show();
        else {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    // Save Entry to database
                    EntryItem entryItem = NewEntryActivity.this.realm.createObject(EntryItem.class);
                    // Creates Date object made from the DatePicker and TimePicker
                    Date date = calendarToBeSaved.getTime();
                    entryItem.setDate(date);
                    entryItem.setStatus(status);
                    entryItem.setBloodGlucose(Integer.parseInt(bloodGlucose.getText().toString()));
                    // Prevention of NullPointerException
                    if (!carbohydrates.getText().toString().equals("")) {
                        entryItem.setCarbohydrates(Integer.parseInt(carbohydrates.getText().toString()));
                    }
                    // Prevention of NullPointerException
                    if (!insulin.getText().toString().equals("")) {
                        entryItem.setInsulin(Double.parseDouble(insulin.getText().toString()));
                    }
                }
            });
            // If the user chooses to have a reminder at certain time.
            if (wantsReminder) createReminder(getNotification());
            // After save returns to MainActivity ListFragment
            finish();
        }
    }

    // Fonts used in Activity
    private void setFonts() {
        Typeface avenirRegular = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-Regular.otf");
        Typeface avenirMedium = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-Medium.otf");
        cancelBtn.setTypeface(avenirMedium);
        saveBtn.setTypeface(avenirMedium);
        if (sharedPreferences.getBoolean(PREF_DARK_MODE, false)) {
            int white = getResources().getColor(R.color.white);
            cancelBtn.setTextColor(white);
            saveBtn.setTextColor(white);
        }
        date.setTypeface(avenirRegular);
        time.setTypeface(avenirRegular);
        bloodGlucose.setTypeface(avenirRegular);
        carbohydrates.setTypeface(avenirRegular);
        insulin.setTypeface(avenirRegular);
    }

    // dateFix
    StringBuilder dateFix(int month, int day, int year) {
        return new StringBuilder().append(month).append("/").append(day).append("/").append(year);
    }

    private void statusButtonCheck(int status) {
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

    private void calculateInsulin(float carbValue) {
        float bolusRatio = (float) sharedPreferences.getInt(BOLUS_RATIO, 0);
        float suggestionInsulin = carbValue / bolusRatio;
        insulinSuggestionValue.setText(String.valueOf(suggestionInsulin));
    }

    private void alarmTimePicker() {
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        alarmCalendar = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY) + 2;
        int minute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        alarmCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        alarmCalendar.set(Calendar.MINUTE, minute);
                        reminder.setText(checkTimeString(hourOfDay, minute));
                        wantsReminder = true;
                    }
                }, hour, minute, false);
        timePickerDialog.show();
    }

    private void createReminder(Notification notification) {
        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        long delay = alarmCalendar.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    private Notification getNotification() {
        String channelId = "Reminders";
        PendingIntent newEntryActivityPendingIntent = PendingIntent.getActivity(this, 1, new Intent(this, NewEntryActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.reminder_content))
                .setTicker(getString(R.string.app_name))
                .setSmallIcon(R.drawable.notebook_notification_white)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true)
                .setContentIntent(newEntryActivityPendingIntent);
        Log.i(TAG, "notification built");
        return builder.build();
    }

    @Override
    public void onDestroy() {
        realm.close();
        super.onDestroy();
    }
}