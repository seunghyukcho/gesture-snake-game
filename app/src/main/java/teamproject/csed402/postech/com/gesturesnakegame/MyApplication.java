package teamproject.csed402.postech.com.gesturesnakegame;

import android.app.Application;

public class MyApplication extends Application {
    private String someVariable = "";

    public String getSomeVariable() {
        return someVariable;
    }

    public void setSomeVariable(String someVariable) {
        this.someVariable = someVariable;
    }
}
