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

public class GroupByRollupKey {
    private final com.espertech.esper.client.EventBean[] generator;
    private final AggregationGroupByRollupLevel level;
    private final Object groupKey;

    public GroupByRollupKey(com.espertech.esper.client.EventBean[] generator, AggregationGroupByRollupLevel level, Object groupKey) {
        this.generator = generator;
        this.level = level;
        this.groupKey = groupKey;
    }

    public com.espertech.esper.client.EventBean[] getGenerator() {
        return generator;
    }

    public AggregationGroupByRollupLevel getLevel() {
        return level;
    }

    public Object getGroupKey() {
        return groupKey;
    }

    public String toString() {
        return "GroupRollupKey{" +
                "level=" + level +
                ", groupKey=" + groupKey +
                '}';
    }
}