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
package eu.uk.ncl.pet5o.esper.core.context.util;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.client.SafeIterator;
import eu.uk.ncl.pet5o.esper.client.context.ContextPartitionSelector;
import eu.uk.ncl.pet5o.esper.core.context.stmt.StatementAIResourceRegistryFactory;
import eu.uk.ncl.pet5o.esper.epl.spec.ContextDetail;

import java.util.Iterator;

public class ContextDescriptor {

    private final String contextName;
    private final boolean singleInstanceContext;
    private final ContextPropertyRegistry contextPropertyRegistry;
    private final StatementAIResourceRegistryFactory aiResourceRegistryFactory;
    private final ContextIteratorHandler iteratorHandler;
    private final ContextDetail contextDetail;

    public ContextDescriptor(String contextName, boolean singleInstanceContext, ContextPropertyRegistry contextPropertyRegistry, StatementAIResourceRegistryFactory aiResourceRegistryFactory, ContextIteratorHandler iteratorHandler, ContextDetail contextDetail) {
        this.contextName = contextName;
        this.singleInstanceContext = singleInstanceContext;
        this.contextPropertyRegistry = contextPropertyRegistry;
        this.aiResourceRegistryFactory = aiResourceRegistryFactory;
        this.iteratorHandler = iteratorHandler;
        this.contextDetail = contextDetail;
    }

    public String getContextName() {
        return contextName;
    }

    public boolean isSingleInstanceContext() {
        return singleInstanceContext;
    }

    public ContextPropertyRegistry getContextPropertyRegistry() {
        return contextPropertyRegistry;
    }

    public StatementAIResourceRegistryFactory getAiResourceRegistryFactory() {
        return aiResourceRegistryFactory;
    }

    public Iterator<EventBean> iterator(int statementId) {
        return iteratorHandler.iterator(statementId);
    }

    public SafeIterator<eu.uk.ncl.pet5o.esper.client.EventBean> safeIterator(int statementId) {
        return iteratorHandler.safeIterator(statementId);
    }

    public Iterator<EventBean> iterator(int statementId, ContextPartitionSelector selector) {
        return iteratorHandler.iterator(statementId, selector);
    }

    public SafeIterator<eu.uk.ncl.pet5o.esper.client.EventBean> safeIterator(int statementId, ContextPartitionSelector selector) {
        return iteratorHandler.safeIterator(statementId, selector);
    }

    public ContextDetail getContextDetail() {
        return contextDetail;
    }
}
