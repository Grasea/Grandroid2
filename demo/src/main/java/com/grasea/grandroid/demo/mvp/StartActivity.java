package com.grasea.grandroid.demo.mvp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.grasea.grandroid.demo.R;
import com.grasea.grandroid.api.Callback;
import com.grasea.grandroid.api.RemoteProxy;
import com.grasea.grandroid.mvp.model.ModelProxy;

import org.json.JSONObject;

import java.util.ArrayList;

public class StartActivity extends AppCompatActivity {
    private UserModel model;
    private WeatherAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = ModelProxy.reflect(UserModel.class);
        api = RemoteProxy.reflect(WeatherAPI.class, StartActivity.class);
        setContentView(R.layout.activity_start);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btnGetBoolean:
                        Toast.makeText(getApplicationContext(), "Gender :" + model.getGender(), Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.btnPutBoolean:
                        model.saveGender(!model.getGender());
                        Toast.makeText(getApplicationContext(), "Gender :" + model.getGender(), Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.btnGetInt:
                        Toast.makeText(getApplicationContext(), "Age :" + model.getAge(), Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.btnPutInt:
                        model.saveAge(model.getAge() + 1);
                        Toast.makeText(getApplicationContext(), "Age +1 :" + model.getAge(), Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.btnGetString:
                        Toast.makeText(getApplicationContext(), "Name :" + model.getName(), Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.btnPutString:
                        model.saveName("Rovers @ " + System.currentTimeMillis());
                        Toast.makeText(getApplicationContext(), "Name :" + model.getName(), Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.btnQueryPerson:
                        Person p = model.getPerson("where _id=1");
                        if (p == null) {
                            Toast.makeText(getApplicationContext(), "Cannot found person record in database in condition: where id=1", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Found person data in database: " + p.toString(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.btnQueryAllPerson:
                        ArrayList<Person> persons = model.getAllPerson();
                        Toast.makeText(getApplicationContext(), "There are " + persons.size() + " person records in database", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.btnSavePerson:
                        Person person = new Person();
                        person.setGender(true);
                        person.setAge(model.getAge());
                        person.setName(model.getName());
                        Toast.makeText(getApplicationContext(), "Save success? " + model.saveUserData(person), Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.btnGetPersonList:
                        Toast.makeText(getApplicationContext(), "Person list size = " + model.getPersonList(), Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.btnPutPersonList:
                        ArrayList<Person> ps = model.getAllPerson();
                        Person s = model.getPerson("where _id=0");
                        Toast.makeText(getApplicationContext(), "Save success? " + model.putPersonList(ps), Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.btnCallApiJson:
                        api.getForecast();
                        break;
                    case R.id.btnCallApiObject:
                        api.getForecastObject();
                        break;
                }
            }
        };
        findViewById(R.id.btnGetBoolean).setOnClickListener(listener);
        findViewById(R.id.btnPutBoolean).setOnClickListener(listener);
        findViewById(R.id.btnGetInt).setOnClickListener(listener);
        findViewById(R.id.btnPutInt).setOnClickListener(listener);
        findViewById(R.id.btnGetString).setOnClickListener(listener);
        findViewById(R.id.btnPutString).setOnClickListener(listener);
        findViewById(R.id.btnQueryPerson).setOnClickListener(listener);
        findViewById(R.id.btnQueryAllPerson).setOnClickListener(listener);
        findViewById(R.id.btnSavePerson).setOnClickListener(listener);
        findViewById(R.id.btnPutPersonList).setOnClickListener(listener);
        findViewById(R.id.btnGetPersonList).setOnClickListener(listener);
        findViewById(R.id.btnCallApiJson).setOnClickListener(listener);
        findViewById(R.id.btnCallApiObject).setOnClickListener(listener);

    }

    @Callback(api="forecast")
    public void onGetForecast(Context context, JSONObject result) {
        Toast.makeText(context, "forecast result: " + result.toString(), Toast.LENGTH_SHORT).show();
    }

    @Callback(api="getForecastObject")
    public void onObjectResult(Context context, Forecast result) {
        Toast.makeText(context, "forecast result: " + result.result.hourly.cloudrate.toString(), Toast.LENGTH_SHORT).show();
    }
}
