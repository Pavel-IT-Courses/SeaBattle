package com.gmail.pavkascool.update;

import android.content.Intent;

public interface ConnectionListener {
    void onSocketConnected();
    void onReceive(Intent intent);
}
