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
package eu.uk.ncl.pet5o.esper.epl.join.pollindex;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.core.service.ExprEvaluatorContextStatement;
import eu.uk.ncl.pet5o.esper.core.service.StatementContext;
import eu.uk.ncl.pet5o.esper.epl.join.table.EventTable;
import eu.uk.ncl.pet5o.esper.epl.join.table.EventTableFactoryTableIdentStmt;
import eu.uk.ncl.pet5o.esper.epl.join.table.PropertyIndexedEventTableFactory;
import eu.uk.ncl.pet5o.esper.epl.join.table.UnindexedEventTableList;

import java.util.Arrays;
import java.util.List;

/**
 * Strategy for building an index out of poll-results knowing the properties to base the index on.
 */
public class PollResultIndexingStrategyIndex implements PollResultIndexingStrategy {
    private final int streamNum;
    private final EventType eventType;
    private final String[] propertyNames;

    /**
     * Ctor.
     *
     * @param streamNum     is the stream number of the indexed stream
     * @param eventType     is the event type of the indexed stream
     * @param propertyNames is the property names to be indexed
     */
    public PollResultIndexingStrategyIndex(int streamNum, EventType eventType, String[] propertyNames) {
        this.streamNum = streamNum;
        this.eventType = eventType;
        this.propertyNames = propertyNames;
    }

    public EventTable[] index(List<EventBean> pollResult, boolean isActiveCache, StatementContext statementContext) {
        if (!isActiveCache) {
            return new EventTable[]{new UnindexedEventTableList(pollResult, streamNum)};
        }
        PropertyIndexedEventTableFactory factory = new PropertyIndexedEventTableFactory(streamNum, eventType, propertyNames, false, null);
        ExprEvaluatorContextStatement evaluatorContextStatement = new ExprEvaluatorContextStatement(statementContext, false);
        EventTable[] tables = factory.makeEventTables(new EventTableFactoryTableIdentStmt(statementContext), evaluatorContextStatement);
        for (EventTable table : tables) {
            table.add(pollResult.toArray(new EventBean[pollResult.size()]), evaluatorContextStatement);
        }
        return tables;
    }

    public String toQueryPlan() {
        return this.getClass().getSimpleName() + " properties " + Arrays.toString(propertyNames);
    }
}
