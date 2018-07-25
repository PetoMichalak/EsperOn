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
package eu.uk.ncl.pet5o.esper.epl.table.mgmt;

import eu.uk.ncl.pet5o.esper.client.EPException;
import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.client.EventPropertyGetter;
import eu.uk.ncl.pet5o.esper.collection.MultiKeyUntyped;
import eu.uk.ncl.pet5o.esper.collection.Pair;
import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.epl.agg.access.AggregationServicePassThru;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprValidationException;
import eu.uk.ncl.pet5o.esper.epl.join.plan.QueryPlanIndexItem;
import eu.uk.ncl.pet5o.esper.epl.join.table.*;
import eu.uk.ncl.pet5o.esper.epl.lookup.EventTableIndexRepository;
import eu.uk.ncl.pet5o.esper.epl.lookup.EventTableIndexRepositoryEntry;
import eu.uk.ncl.pet5o.esper.epl.lookup.IndexMultiKey;
import eu.uk.ncl.pet5o.esper.event.ObjectArrayBackedEventBean;
import eu.uk.ncl.pet5o.esper.metrics.instrumentation.InstrumentationHelper;
import eu.uk.ncl.pet5o.esper.util.CollectionUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TableStateInstanceGroupedImpl extends TableStateInstance implements TableStateInstanceGrouped {

    private final Map<Object, ObjectArrayBackedEventBean> rows = new HashMap<Object, ObjectArrayBackedEventBean>();
    private final IndexMultiKey primaryIndexKey;

    public TableStateInstanceGroupedImpl(TableMetadata tableMetadata, AgentInstanceContext agentInstanceContext) {
        super(tableMetadata, agentInstanceContext);

        List<EventPropertyGetter> indexGetters = new ArrayList<EventPropertyGetter>();
        List<String> keyNames = new ArrayList<String>();
        for (Map.Entry<String, TableMetadataColumn> entry : tableMetadata.getTableColumns().entrySet()) {
            if (entry.getValue().isKey()) {
                keyNames.add(entry.getKey());
                indexGetters.add(tableMetadata.getInternalEventType().getGetter(entry.getKey()));
            }
        }

        String tableName = "primary-" + tableMetadata.getTableName();
        EventTableOrganization organization = new EventTableOrganization(tableName, true, false, 0, CollectionUtil.toArray(keyNames), EventTableOrganizationType.HASH);

        EventTable table;
        if (indexGetters.size() == 1) {
            Map<Object, EventBean> tableMap = (Map<Object, EventBean>) (Map<Object, ?>) rows;
            table = new PropertyIndexedEventTableSingleUnique(indexGetters.get(0), organization, tableMap);
        } else {
            EventPropertyGetter[] getters = indexGetters.toArray(new EventPropertyGetter[indexGetters.size()]);
            Map<MultiKeyUntyped, EventBean> tableMap = (Map<MultiKeyUntyped, EventBean>) (Map<?, ?>) rows;
            table = new PropertyIndexedEventTableUnique(getters, organization, tableMap);
        }

        Pair<int[], IndexMultiKey> pair = TableServiceUtil.getIndexMultikeyForKeys(tableMetadata.getTableColumns(), tableMetadata.getInternalEventType());
        primaryIndexKey = pair.getSecond();
        indexRepository.addIndex(primaryIndexKey, new EventTableIndexRepositoryEntry(tableName, table));
    }

    public EventTable getIndex(String indexName) {
        if (indexName.equals(tableMetadata.getTableName())) {
            return indexRepository.getIndexByDesc(primaryIndexKey);
        }
        return indexRepository.getExplicitIndexByName(indexName);
    }

    public Map<Object, ObjectArrayBackedEventBean> getRows() {
        return rows;
    }

    public void addEvent(eu.uk.ncl.pet5o.esper.client.EventBean theEvent) {
        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().qTableAddEvent(theEvent);
        }
        try {
            for (EventTable table : indexRepository.getTables()) {
                table.add(theEvent, agentInstanceContext);
            }
        } catch (EPException ex) {
            for (EventTable table : indexRepository.getTables()) {
                table.remove(theEvent, agentInstanceContext);
            }
            throw ex;
        } finally {
            if (InstrumentationHelper.ENABLED) {
                InstrumentationHelper.get().aTableAddEvent();
            }
        }
    }

    public void deleteEvent(eu.uk.ncl.pet5o.esper.client.EventBean matchingEvent) {
        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().qTableDeleteEvent(matchingEvent);
        }
        for (EventTable table : indexRepository.getTables()) {
            table.remove(matchingEvent, agentInstanceContext);
        }
        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().aTableDeleteEvent();
        }
    }

    public Iterable<EventBean> getIterableTableScan() {
        return new PrimaryIndexIterable(rows);
    }

    public void addExplicitIndex(String explicitIndexName, QueryPlanIndexItem explicitIndexDesc, boolean isRecoveringResilient, boolean allowIndexExists) throws ExprValidationException {
        indexRepository.validateAddExplicitIndex(explicitIndexName, explicitIndexDesc, tableMetadata.getInternalEventType(), new PrimaryIndexIterable(rows), getAgentInstanceContext(), isRecoveringResilient || allowIndexExists, null);
    }

    public String[] getSecondaryIndexes() {
        return indexRepository.getExplicitIndexNames();
    }

    public EventTableIndexRepository getIndexRepository() {
        return indexRepository;
    }

    public Collection<EventBean> getEventCollection() {
        return (Collection<EventBean>) (Collection<?>) rows.values();
    }

    public ObjectArrayBackedEventBean getRowForGroupKey(Object groupKey) {
        return rows.get(groupKey);
    }

    public Set<Object> getGroupKeys() {
        return rows.keySet();
    }

    public void clear() {
        clearInstance();
    }

    public void clearInstance() {
        rows.clear();
        for (EventTable table : indexRepository.getTables()) {
            table.destroy();
        }
    }

    public void destroyInstance() {
        clearInstance();
    }

    public ObjectArrayBackedEventBean getCreateRowIntoTable(Object groupByKey, ExprEvaluatorContext exprEvaluatorContext) {
        ObjectArrayBackedEventBean bean = getRows().get(groupByKey);
        if (bean != null) {
            return bean;
        }
        ObjectArrayBackedEventBean row = tableMetadata.getRowFactory().makeOA(exprEvaluatorContext.getAgentInstanceId(), groupByKey, null, getAggregationServicePassThru());
        addEvent(row);
        return row;
    }

    public int getRowCount() {
        return rows.size();
    }

    public AggregationServicePassThru getAggregationServicePassThru() {
        return null;
    }

    private static class PrimaryIndexIterable implements Iterable<EventBean> {

        private final Map<Object, ObjectArrayBackedEventBean> rows;

        private PrimaryIndexIterable(Map<Object, ObjectArrayBackedEventBean> rows) {
            this.rows = rows;
        }

        public Iterator<EventBean> iterator() {
            return (Iterator<EventBean>) (Iterator<?>) rows.values().iterator();
        }
    }
}
