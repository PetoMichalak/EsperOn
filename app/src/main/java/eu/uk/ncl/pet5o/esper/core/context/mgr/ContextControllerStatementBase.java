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

import eu.uk.ncl.pet5o.esper.core.context.factory.StatementAgentInstanceFactory;
import eu.uk.ncl.pet5o.esper.core.context.util.ContextMergeView;
import eu.uk.ncl.pet5o.esper.core.service.StatementContext;
import eu.uk.ncl.pet5o.esper.core.service.speccompiled.StatementSpecCompiled;

public abstract class ContextControllerStatementBase {

    private final StatementSpecCompiled statementSpec;
    private final StatementContext statementContext;
    private final ContextMergeView mergeView;
    private final StatementAgentInstanceFactory factory;

    public ContextControllerStatementBase(StatementSpecCompiled statementSpec, StatementContext statementContext, ContextMergeView mergeView, StatementAgentInstanceFactory factory) {
        this.statementSpec = statementSpec;
        this.statementContext = statementContext;
        this.mergeView = mergeView;
        this.factory = factory;
    }

    public StatementSpecCompiled getStatementSpec() {
        return statementSpec;
    }

    public StatementContext getStatementContext() {
        return statementContext;
    }

    public ContextMergeView getMergeView() {
        return mergeView;
    }

    public StatementAgentInstanceFactory getFactory() {
        return factory;
    }
}
