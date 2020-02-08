package com.gmail.pavkascool.update;

import android.app.Application;
import android.os.AsyncTask;

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
        db = ((BattleApplication)application).getBattleDatabase();

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
