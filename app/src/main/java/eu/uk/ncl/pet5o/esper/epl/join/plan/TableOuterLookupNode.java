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
package eu.uk.ncl.pet5o.esper.epl.join.plan;

import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.epl.join.exec.base.ExecNode;
import eu.uk.ncl.pet5o.esper.epl.join.exec.base.JoinExecTableLookupStrategy;
import eu.uk.ncl.pet5o.esper.epl.join.exec.base.TableOuterLookupExecNode;
import eu.uk.ncl.pet5o.esper.epl.join.exec.base.TableOuterLookupExecNodeTableLocking;
import eu.uk.ncl.pet5o.esper.epl.join.table.EventTable;
import eu.uk.ncl.pet5o.esper.epl.join.table.HistoricalStreamIndexList;
import eu.uk.ncl.pet5o.esper.epl.virtualdw.VirtualDWView;
import eu.uk.ncl.pet5o.esper.util.IndentWriter;
import eu.uk.ncl.pet5o.esper.view.Viewable;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.locks.Lock;

/**
 * Specifies exection of a table lookup with outer join using the a specified lookup plan.
 */
public class TableOuterLookupNode extends QueryPlanNode {
    private TableLookupPlan tableLookupPlan;

    /**
     * Ctor.
     *
     * @param tableLookupPlan - plan for performing lookup
     */
    public TableOuterLookupNode(TableLookupPlan tableLookupPlan) {
        this.tableLookupPlan = tableLookupPlan;
    }

    /**
     * Returns lookup plan.
     *
     * @return lookup plan
     */
    public TableLookupPlan getLookupStrategySpec() {
        return tableLookupPlan;
    }

    public void print(IndentWriter writer) {
        writer.println("TableOuterLookupNode " +
                " tableLookupPlan=" + tableLookupPlan);
    }

    public ExecNode makeExec(String statementName, int statementId, Annotation[] annotations, Map<TableLookupIndexReqKey, EventTable>[] indexesPerStream, EventType[] streamTypes, Viewable[] streamViews, HistoricalStreamIndexList[] historicalStreamIndexLists, VirtualDWView[] viewExternal, Lock[] tableSecondaryIndexLocks) {
        JoinExecTableLookupStrategy lookupStrategy = tableLookupPlan.makeStrategy(statementName, statementId, annotations, indexesPerStream, streamTypes, viewExternal);
        int indexedStream = tableLookupPlan.getIndexedStream();
        if (tableSecondaryIndexLocks[indexedStream] != null) {
            return new TableOuterLookupExecNodeTableLocking(indexedStream, lookupStrategy, tableSecondaryIndexLocks[indexedStream]);
        }
        return new TableOuterLookupExecNode(tableLookupPlan.getIndexedStream(), lookupStrategy);
    }

    public void addIndexes(HashSet<TableLookupIndexReqKey> usedIndexes) {
        usedIndexes.addAll(Arrays.asList(tableLookupPlan.getIndexNum()));
    }
}
