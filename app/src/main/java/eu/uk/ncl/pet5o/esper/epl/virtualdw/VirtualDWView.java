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
package eu.uk.ncl.pet5o.esper.epl.virtualdw;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.client.hook.VirtualDataWindow;
import eu.uk.ncl.pet5o.esper.collection.Pair;
import eu.uk.ncl.pet5o.esper.epl.join.exec.base.JoinExecTableLookupStrategy;
import eu.uk.ncl.pet5o.esper.epl.join.exec.base.RangeIndexLookupValue;
import eu.uk.ncl.pet5o.esper.epl.join.plan.CoercionDesc;
import eu.uk.ncl.pet5o.esper.epl.join.plan.QueryPlanIndexItem;
import eu.uk.ncl.pet5o.esper.epl.join.plan.TableLookupKeyDesc;
import eu.uk.ncl.pet5o.esper.epl.join.table.EventTable;
import eu.uk.ncl.pet5o.esper.epl.lookup.*;
import eu.uk.ncl.pet5o.esper.epl.spec.CreateIndexDesc;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface VirtualDWView {
    public Pair<IndexMultiKey, EventTable> getSubordinateQueryDesc(boolean unique, List<IndexedPropDesc> hashedProps, List<IndexedPropDesc> btreeProps);

    public SubordTableLookupStrategy getSubordinateLookupStrategy(String accessedByStatementName, int accessedByStatementId, Annotation[] accessedByStmtAnnotations, EventType[] outerStreamTypes, List<SubordPropHashKey> hashKeys, CoercionDesc hashKeyCoercionTypes, List<SubordPropRangeKey> rangeKeys, CoercionDesc rangeKeyCoercionTypes, boolean nwOnTrigger, EventTable eventTable, SubordPropPlan joinDesc, boolean forceTableScan);

    public EventTable getJoinIndexTable(QueryPlanIndexItem queryPlanIndexItem);

    public JoinExecTableLookupStrategy getJoinLookupStrategy(String accessedByStatementName, int accessedByStatementId, Annotation[] accessedByStmtAnnotations, EventTable[] eventTable, TableLookupKeyDesc keyDescriptor, int lookupStreamNum);

    public Pair<IndexMultiKey, EventTable> getFireAndForgetDesc(Set<String> keysAvailable, Set<String> rangesAvailable);

    public Collection<EventBean> getFireAndForgetData(EventTable eventTable, Object[] keyValues, RangeIndexLookupValue[] rangeValues, Annotation[] accessedByStmtAnnotations);

    public VirtualDataWindow getVirtualDataWindow();

    public void destroy();

    public void handleStartIndex(CreateIndexDesc spec);

    public void handleStopIndex(CreateIndexDesc spec);

    public void handleStopWindow();
}
