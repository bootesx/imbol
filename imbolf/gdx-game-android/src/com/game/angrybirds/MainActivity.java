package com.game.angrybirds;

import android.*;
import android.content.*;
import android.content.pm.*;
import android.net.*;
import android.os.*;
import android.provider.*;
import android.util.*;
import com.badlogic.gdx.backends.android.*;
import com.game.angrybirds.*;
import java.util.*;

public class MainActivity extends AndroidApplication {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        
        initialize(new MyGdxGame(), cfg);
    }
}
