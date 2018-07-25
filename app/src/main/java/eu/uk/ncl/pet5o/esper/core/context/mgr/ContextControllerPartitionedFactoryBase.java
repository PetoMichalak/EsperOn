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
package eu.uk.ncl.pet5o.esper.core.context.mgr;

import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.client.context.ContextPartitionIdentifier;
import eu.uk.ncl.pet5o.esper.client.context.ContextPartitionIdentifierPartitioned;
import eu.uk.ncl.pet5o.esper.collection.MultiKeyUntyped;
import eu.uk.ncl.pet5o.esper.core.context.stmt.AIRegistryAggregationMultiPerm;
import eu.uk.ncl.pet5o.esper.core.context.stmt.AIRegistryExprMultiPerm;
import eu.uk.ncl.pet5o.esper.core.context.stmt.StatementAIResourceRegistry;
import eu.uk.ncl.pet5o.esper.core.context.stmt.StatementAIResourceRegistryFactory;
import eu.uk.ncl.pet5o.esper.core.context.util.ContextDetailUtil;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprFilterSpecLookupable;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprValidationException;
import eu.uk.ncl.pet5o.esper.epl.spec.ContextDetail;
import eu.uk.ncl.pet5o.esper.epl.spec.ContextDetailConditionFilter;
import eu.uk.ncl.pet5o.esper.epl.spec.ContextDetailPartitionItem;
import eu.uk.ncl.pet5o.esper.epl.spec.ContextDetailPartitioned;
import eu.uk.ncl.pet5o.esper.epl.util.StatementSpecCompiledAnalyzer;
import eu.uk.ncl.pet5o.esper.epl.util.StatementSpecCompiledAnalyzerResult;
import eu.uk.ncl.pet5o.esper.filterspec.FilterAddendumUtil;
import eu.uk.ncl.pet5o.esper.filterspec.FilterSpecCompiled;
import eu.uk.ncl.pet5o.esper.filterspec.FilterValueSetParam;
import eu.uk.ncl.pet5o.esper.filterspec.MatchedEventMapMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public abstract class ContextControllerPartitionedFactoryBase extends ContextControllerFactoryBase implements ContextControllerFactory {

    protected final ContextDetailPartitioned segmentedSpec;
    private final List<FilterSpecCompiled> filtersSpecsNestedContexts;
    private final List<FilterSpecCompiled> filtersTerminationMayNull;

    private Map<String, Object> contextBuiltinProps;
    private MatchedEventMapMeta termConditionMatchEventMap;

    public ContextControllerPartitionedFactoryBase(ContextControllerFactoryContext factoryContext, ContextDetailPartitioned segmentedSpec, List<FilterSpecCompiled> filtersSpecsNestedContexts) {
        super(factoryContext);
        this.segmentedSpec = segmentedSpec;
        this.filtersSpecsNestedContexts = filtersSpecsNestedContexts;
        this.filtersTerminationMayNull = segmentedSpec.getOptionalTermination() == null ? null : ContextDetailUtil.getFilterSpecIfAny(segmentedSpec.getOptionalTermination());
    }

    public boolean hasFiltersSpecsNestedContexts() {
        return filtersSpecsNestedContexts != null && !filtersSpecsNestedContexts.isEmpty();
    }

    public void validateFactory() throws ExprValidationException {
        Class[] propertyTypes = ContextControllerPartitionedUtil.validateContextDesc(factoryContext.getContextName(), segmentedSpec);
        contextBuiltinProps = ContextPropertyEventType.getPartitionType(segmentedSpec, propertyTypes);

        LinkedHashSet<String> allTags = new LinkedHashSet<>();
        for (ContextDetailPartitionItem item : segmentedSpec.getItems()) {
            if (item.getAliasName() != null) {
                allTags.add(item.getAliasName());
            }
        }
        if (segmentedSpec.getOptionalInit() != null) {
            for (ContextDetailConditionFilter filter : segmentedSpec.getOptionalInit()) {
                ContextPropertyEventType.addEndpointTypes(factoryContext.getContextName(), filter, contextBuiltinProps, allTags);
            }
        }
        termConditionMatchEventMap = new MatchedEventMapMeta(allTags, false);
    }

    public ContextControllerStatementCtxCache validateStatement(ContextControllerStatementBase statement) throws ExprValidationException {
        StatementSpecCompiledAnalyzerResult streamAnalysis = StatementSpecCompiledAnalyzer.analyzeFilters(statement.getStatementSpec());
        ContextControllerPartitionedUtil.validateStatementForContext(factoryContext.getContextName(), statement, streamAnalysis, getItemEventTypes(segmentedSpec), factoryContext.getServicesContext().getNamedWindowMgmtService());
        return new ContextControllerStatementCtxCacheFilters(streamAnalysis.getFilters());
    }

    public void populateFilterAddendums(IdentityHashMap<FilterSpecCompiled, FilterValueSetParam[][]> filterAddendum, ContextControllerStatementDesc statement, Object key, int contextId) {
        ContextControllerStatementCtxCacheFilters statementInfo = (ContextControllerStatementCtxCacheFilters) statement.getCaches()[factoryContext.getNestingLevel() - 1];
        ContextControllerPartitionedUtil.populateAddendumFilters(key, statementInfo.getFilterSpecs(), segmentedSpec, statement.getStatement().getStatementSpec(), filterAddendum);
    }

    public void populateContextInternalFilterAddendums(ContextInternalFilterAddendum filterAddendum, Object key) {
        if (filtersSpecsNestedContexts == null) {
            return;
        }
        ContextControllerPartitionedUtil.populateAddendumFilters(key, filtersSpecsNestedContexts, segmentedSpec, null, filterAddendum.getFilterAddendum());
    }

    public void populateContextInternalFilterAddendumsTermination(ContextInternalFilterAddendum filterAddendum, Object key) {
        if (filtersTerminationMayNull == null) {
            return;
        }
        for (FilterSpecCompiled filtersSpec : filtersTerminationMayNull) {
            FilterValueSetParam[][] addendum = ContextControllerPartitionedUtil.getAddendumFilters(key, filtersSpec, segmentedSpec, false, null);
            if (addendum == null) {
                continue;
            }

            FilterValueSetParam[][] existing = filterAddendum.getFilterAddendum().get(filtersSpec);
            if (existing != null) {
                addendum = FilterAddendumUtil.multiplyAddendum(existing, addendum);
            }
            filterAddendum.getFilterAddendum().put(filtersSpec, addendum);
        }
    }

    public ExprFilterSpecLookupable getFilterLookupable(EventType eventType) {
        return null;
    }

    public boolean isSingleInstanceContext() {
        return false;
    }

    public StatementAIResourceRegistryFactory getStatementAIResourceRegistryFactory() {
        return new StatementAIResourceRegistryFactory() {
            public StatementAIResourceRegistry make() {
                return new StatementAIResourceRegistry(new AIRegistryAggregationMultiPerm(), new AIRegistryExprMultiPerm());
            }
        };
    }

    public List<ContextDetailPartitionItem> getContextDetailPartitionItems() {
        return segmentedSpec.getItems();
    }

    public ContextDetail getContextDetail() {
        return segmentedSpec;
    }

    public ContextDetailPartitioned getSegmentedSpec() {
        return segmentedSpec;
    }

    public MatchedEventMapMeta getTermConditionMatchEventMap() {
        return termConditionMatchEventMap;
    }

    public Map<String, Object> getContextBuiltinProps() {
        return contextBuiltinProps;
    }

    public ContextPartitionIdentifier keyPayloadToIdentifier(Object payload) {
        if (payload instanceof Object[]) {
            return new ContextPartitionIdentifierPartitioned((Object[]) payload);
        }
        if (payload instanceof MultiKeyUntyped) {
            return new ContextPartitionIdentifierPartitioned(((MultiKeyUntyped) payload).getKeys());
        }
        if (payload instanceof ContextControllerPartitionedState) {
            return new ContextPartitionIdentifierPartitioned(((ContextControllerPartitionedState) payload).getPartitionKey());
        }
        return new ContextPartitionIdentifierPartitioned(new Object[]{payload});
    }

    private Collection<EventType> getItemEventTypes(ContextDetailPartitioned segmentedSpec) {
        List<EventType> itemEventTypes = new ArrayList<EventType>();
        for (ContextDetailPartitionItem item : segmentedSpec.getItems()) {
            itemEventTypes.add(item.getFilterSpecCompiled().getFilterForEventType());
        }
        return itemEventTypes;
    }
}
