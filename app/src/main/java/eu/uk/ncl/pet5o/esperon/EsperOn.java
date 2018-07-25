package eu.uk.ncl.pet5o.esperon;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.HashMap;
import java.util.Map;

import eu.uk.ncl.pet5o.esper.client.Configuration;
import eu.uk.ncl.pet5o.esper.client.EPServiceProvider;
import eu.uk.ncl.pet5o.esper.client.EPServiceProviderManager;
import eu.uk.ncl.pet5o.esper.client.EPStatement;

public class EsperOn extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esper_on);

        // init esper
        Configuration config = new Configuration();
        EPServiceProvider epService = EPServiceProviderManager.getDefaultProvider(config);
        EPStatement epStatement = epService.getEPAdministrator().createEPL("SELECT * FROM AccelEvent");
        epStatement.setSubscriber(null);

        Map<String, Object> event = new HashMap<String, Object>();
        event.put("ts", System.currentTimeMillis());
        event.put("x", 1);
        event.put("y", 2);
        event.put("z", 3);

        // send event to Esper
        epService.getEPRuntime(\).sendEvent(event, "AccelEvent");
    }
}
