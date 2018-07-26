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
package eu.uk.ncl.pet5o.esper.epl.datetime.dtlocal;

import eu.uk.ncl.pet5o.esper.epl.datetime.interval.IntervalForge;

abstract class DTLocalForgeIntervalBase implements DTLocalForge, DTLocalForgeIntervalComp {
    protected final IntervalForge intervalForge;

    protected DTLocalForgeIntervalBase(IntervalForge intervalForge) {
        this.intervalForge = intervalForge;
    }
}
