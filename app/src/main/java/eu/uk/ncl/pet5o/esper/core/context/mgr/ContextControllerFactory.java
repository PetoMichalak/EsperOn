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
import eu.uk.ncl.pet5o.esper.core.context.stmt.StatementAIResourceRegistryFactory;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprFilterSpecLookupable;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprValidationException;
import eu.uk.ncl.pet5o.esper.epl.spec.ContextDetail;
import eu.uk.ncl.pet5o.esper.epl.spec.ContextDetailPartitionItem;
import eu.uk.ncl.pet5o.esper.filterspec.FilterSpecCompiled;
import eu.uk.ncl.pet5o.esper.filterspec.FilterValueSetParam;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public interface ContextControllerFactory {

    public ContextControllerFactoryContext getFactoryContext();

    public Map<String, Object> getContextBuiltinProps();

    public boolean isSingleInstanceContext();

    public ContextDetail getContextDetail();

    public List<ContextDetailPartitionItem> getContextDetailPartitionItems();

    public StatementAIResourceRegistryFactory getStatementAIResourceRegistryFactory();

    public void validateFactory() throws ExprValidationException;

    public ContextControllerStatementCtxCache validateStatement(ContextControllerStatementBase statement) throws ExprValidationException;

    public ContextController createNoCallback(int pathId, ContextControllerLifecycleCallback callback);

    public void populateFilterAddendums(IdentityHashMap<FilterSpecCompiled, FilterValueSetParam[][]> filterAddendum, ContextControllerStatementDesc statement, Object key, int contextId);

    public ExprFilterSpecLookupable getFilterLookupable(EventType eventType);

    public ContextPartitionIdentifier keyPayloadToIdentifier(Object payload);

    public ContextStateCache getStateCache();
}
