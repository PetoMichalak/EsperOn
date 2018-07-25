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
package eu.uk.ncl.pet5o.esper.event.util;

import eu.uk.ncl.pet5o.esper.client.util.EventPropertyRenderer;
import eu.uk.ncl.pet5o.esper.client.util.EventPropertyRendererContext;

public class EventPropertyRendererDefault implements EventPropertyRenderer {

    public static final EventPropertyRendererDefault INSTANCE = new EventPropertyRendererDefault();

    public void render(EventPropertyRendererContext context) {

    }
}
