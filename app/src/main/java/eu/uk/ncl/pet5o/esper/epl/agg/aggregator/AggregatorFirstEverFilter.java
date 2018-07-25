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
package eu.uk.ncl.pet5o.esper.epl.agg.aggregator;

import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregatorUtil;

/**
 * Aggregator for the very first value.
 */
public class AggregatorFirstEverFilter extends AggregatorFirstEver {
    @Override
    public void enter(Object parameters) {
        Object[] paramArray = (Object[]) parameters;
        if (!AggregatorUtil.checkFilter(paramArray)) {
            return;
        }
        super.enter(paramArray[0]);
    }
}
