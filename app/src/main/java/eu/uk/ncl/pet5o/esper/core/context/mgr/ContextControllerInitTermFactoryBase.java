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
import eu.uk.ncl.pet5o.esper.client.context.ContextPartitionIdentifierInitiatedTerminated;
import eu.uk.ncl.pet5o.esper.core.context.stmt.*;
import eu.uk.ncl.pet5o.esper.core.service.EPStatementHandle;
import eu.uk.ncl.pet5o.esper.core.service.StatementContext;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprFilterSpecLookupable;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprValidationException;
import eu.uk.ncl.pet5o.esper.epl.spec.ContextDetailInitiatedTerminated;
import eu.uk.ncl.pet5o.esper.epl.spec.ContextDetailPartitionItem;
import eu.uk.ncl.pet5o.esper.filterspec.FilterSpecCompiled;
import eu.uk.ncl.pet5o.esper.filterspec.FilterValueSetParam;
import eu.uk.ncl.pet5o.esper.filterspec.MatchedEventMapMeta;
import eu.uk.ncl.pet5o.esper.schedule.SchedulingService;
import eu.uk.ncl.pet5o.esper.schedule.TimeProvider;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public abstract class ContextControllerInitTermFactoryBase extends ContextControllerFactoryBase implements ContextControllerFactory {

    private final ContextDetailInitiatedTerminated detail;
    private Map<String, Object> contextBuiltinProps;
    private MatchedEventMapMeta matchedEventMapMeta;

    public ContextControllerInitTermFactoryBase(ContextControllerFactoryContext factoryContext, ContextDetailInitiatedTerminated detail) {
        super(factoryContext);
        this.detail = detail;
    }

    public void validateFactory() throws ExprValidationException {
        contextBuiltinProps = ContextPropertyEventType.getInitiatedTerminatedType();
        LinkedHashSet<String> allTags = new LinkedHashSet<String>();
        ContextPropertyEventType.addEndpointTypes(factoryContext.getContextName(), detail.getStart(), contextBuiltinProps, allTags);
        ContextPropertyEventType.addEndpointTypes(factoryContext.getContextName(), detail.getEnd(), contextBuiltinProps, allTags);
        matchedEventMapMeta = new MatchedEventMapMeta(allTags, false);
    }

    public Map<String, Object> getContextBuiltinProps() {
        return contextBuiltinProps;
    }

    public MatchedEventMapMeta getMatchedEventMapMeta() {
        return matchedEventMapMeta;
    }

    public ContextControllerStatementCtxCache validateStatement(ContextControllerStatementBase statement) {
        return null;
    }

    public void populateFilterAddendums(IdentityHashMap<FilterSpecCompiled, FilterValueSetParam[][]> filterAddendum, ContextControllerStatementDesc statement, Object key, int contextId) {
    }

    public ExprFilterSpecLookupable getFilterLookupable(EventType eventType) {
        return null;
    }

    public ContextDetailInitiatedTerminated getContextDetail() {
        return detail;
    }

    public List<ContextDetailPartitionItem> getContextDetailPartitionItems() {
        return Collections.emptyList();
    }

    public boolean isSingleInstanceContext() {
        return !detail.isOverlapping();
    }

    public long allocateSlot() {
        return factoryContext.getAgentInstanceContextCreate().getStatementContext().getScheduleBucket().allocateSlot();
    }

    public TimeProvider getTimeProvider() {
        return factoryContext.getAgentInstanceContextCreate().getStatementContext().getTimeProvider();
    }

    public SchedulingService getSchedulingService() {
        return factoryContext.getAgentInstanceContextCreate().getStatementContext().getSchedulingService();
    }

    public EPStatementHandle getEpStatementHandle() {
        return factoryContext.getAgentInstanceContextCreate().getStatementContext().getEpStatementHandle();
    }

    public StatementContext getStatementContext() {
        return factoryContext.getAgentInstanceContextCreate().getStatementContext();
    }

    public ContextPartitionIdentifier keyPayloadToIdentifier(Object payload) {
        ContextControllerInitTermState state = (ContextControllerInitTermState) payload;
        return new ContextPartitionIdentifierInitiatedTerminated(
                state == null ? null : state.getPatternData(),
                state == null ? 0 : state.getStartTime(),
                null);
    }

    public StatementAIResourceRegistryFactory getStatementAIResourceRegistryFactory() {
        if (getContextDetail().isOverlapping()) {
            return new StatementAIResourceRegistryFactory() {
                public StatementAIResourceRegistry make() {
                    return new StatementAIResourceRegistry(new AIRegistryAggregationMultiPerm(), new AIRegistryExprMultiPerm());
                }
            };
        } else {
            return new StatementAIResourceRegistryFactory() {
                public StatementAIResourceRegistry make() {
                    return new StatementAIResourceRegistry(new AIRegistryAggregationSingle(), new AIRegistryExprSingle());
                }
            };
        }
    }
}
