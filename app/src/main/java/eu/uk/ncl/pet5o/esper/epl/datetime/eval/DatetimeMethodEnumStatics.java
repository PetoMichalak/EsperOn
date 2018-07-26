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
package eu.uk.ncl.pet5o.esper.epl.datetime.eval;

import eu.uk.ncl.pet5o.esper.epl.datetime.calop.CalendarForgeFactory;
import eu.uk.ncl.pet5o.esper.epl.datetime.interval.IntervalForgeFactory;
import eu.uk.ncl.pet5o.esper.epl.datetime.reformatop.ReformatForgeFactory;

public class DatetimeMethodEnumStatics {

    public final static ForgeFactory CALENDAR_FORGE_FACTORY = new CalendarForgeFactory();
    public final static ForgeFactory REFORMAT_FORGE_FACTORY = new ReformatForgeFactory();
    public final static ForgeFactory INTERVAL_FORGE_FACTORY = new IntervalForgeFactory();
}
