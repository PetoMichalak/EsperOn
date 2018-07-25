package eu.uk.ncl.pet5o.esperon;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import eu.uk.ncl.pet5o.esper.client.Configuration;

public class EsperOn extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esper_on);

        // init esper
        Configuration config = new Configuration();

    }
}
