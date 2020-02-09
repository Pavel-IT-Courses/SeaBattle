package com.gmail.pavkascool.update.delegates;

import android.app.Application;
import android.os.AsyncTask;

import com.gmail.pavkascool.update.BattleApplication;
import com.gmail.pavkascool.update.database.BattleDatabase;
import com.gmail.pavkascool.update.database.Result;
import com.gmail.pavkascool.update.database.ResultDao;
import com.gmail.pavkascool.update.utils.Statistics;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class StartModel extends AndroidViewModel {

    private BattleDatabase db;
    private Statistics statistics;
    private MutableLiveData<Statistics> statData;

    public StartModel(@NonNull Application application) {
        super(application);
        BattleApplication ba = (BattleApplication)application;
        db = ba.getBattleDatabase();
        ba.setStartModel(this);

    }

    public LiveData<Statistics> getStatData() {
        if(statData == null) {
            statData = new MutableLiveData<>();
            updateResults();
        }
        return statData;
    }

    public void updateResults() {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                ResultDao dao = db.resultDao();

                List<Result> res = dao.getAll();
                int v = dao.victories();
                int d = dao.defeats();
                statistics = new Statistics(res, v, d);
                statData.postValue(statistics);
                return null;
            }

        };
        task.execute();
    }

}
