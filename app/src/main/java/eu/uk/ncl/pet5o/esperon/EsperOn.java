package eu.uk.ncl.pet5o.esperon;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.HashMap;
import java.util.Map;

import eu.uk.ncl.pet5o.esper.client.Configuration;
import eu.uk.ncl.pet5o.esper.client.EPServiceProvider;
import eu.uk.ncl.pet5o.esper.client.EPServiceProviderManager;
import eu.uk.ncl.pet5o.esper.client.EPStatement;

/**
 * @author Peter Michalak
 *
 * Port of the Esper CEP library (http://www.espertech.com/esper) for Android OS,
 * guided by Asper project (https://github.com/mobile-event-processing/Asper)
 * by Marcel Eggum.
 *
 * The software in this package is published under the terms of the GPL license
 *  a copy of which has been included with this distribution in the license.txt file.
 *
 *  package replace command:
 *  find . -name "*.java" -exec sed -i '' -e 's/com.espertech/eu.uk.ncl.pet5o/g' {} \;
 */
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
        epService.getEPRuntime().sendEvent(event, "AccelEvent");
    }
}
