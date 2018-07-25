/*
 ***************************************************************************************
 *  Copyright (C) 2006 EsperTech, Inc. All rights reserved.                            *
 *  http://www.espertech.com/esper                                                     *
 *  http://www.espertech.com                                                           *
 *  ---------------------------------------------------------------------------------- *
 *  The software in this package is published under the terms of the GPL license       *
 *  a copy of which has been included with this distribution in the license.txt file.  *
 ***************************************************************************************
 */
package eu.uk.ncl.pet5o.esper.core.support;

import eu.uk.ncl.pet5o.esper.client.util.ClassForNameProviderDefault;
import eu.uk.ncl.pet5o.esper.event.EventAdapterService;
import eu.uk.ncl.pet5o.esper.event.EventAdapterServiceImpl;
import eu.uk.ncl.pet5o.esper.event.EventTypeIdGeneratorImpl;
import eu.uk.ncl.pet5o.esper.event.avro.EventAdapterAvroHandler;
import eu.uk.ncl.pet5o.esper.event.avro.EventAdapterAvroHandlerUnsupported;
import eu.uk.ncl.pet5o.esper.util.JavaClassHelper;

public class SupportEventAdapterService {
    private static EventAdapterService eventAdapterService;

    static {
        eventAdapterService = allocate();
    }

    public static void reset() {
        eventAdapterService = allocate();
    }

    public static EventAdapterService getService() {
        return eventAdapterService;
    }

    private static EventAdapterService allocate() {
        EventAdapterAvroHandler avroHandler = EventAdapterAvroHandlerUnsupported.INSTANCE;
        try {
            avroHandler = (EventAdapterAvroHandler) JavaClassHelper.instantiate(EventAdapterAvroHandler.class, EventAdapterAvroHandler.HANDLER_IMPL, ClassForNameProviderDefault.INSTANCE);
        } catch (Throwable t) {
        }
        return new EventAdapterServiceImpl(new EventTypeIdGeneratorImpl(), 5, avroHandler, SupportEngineImportServiceFactory.make());
    }
}
