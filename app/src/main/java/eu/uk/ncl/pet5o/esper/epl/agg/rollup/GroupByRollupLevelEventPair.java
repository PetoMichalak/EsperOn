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
package eu.uk.ncl.pet5o.esper.epl.agg.rollup;

import com.espertech.esper.epl.agg.service.common.AggregationGroupByRollupLevel;

public class GroupByRollupLevelEventPair {
    private final AggregationGroupByRollupLevel level;
    private final com.espertech.esper.client.EventBean event;

    public GroupByRollupLevelEventPair(AggregationGroupByRollupLevel level, com.espertech.esper.client.EventBean event) {
        this.level = level;
        this.event = event;
    }

    public AggregationGroupByRollupLevel getLevel() {
        return level;
    }

    public com.espertech.esper.client.EventBean getEvent() {
        return event;
    }
}
