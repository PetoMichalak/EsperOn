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

public class ContextManagedStatementCreateWindowDesc extends ContextControllerStatementBase {

    public ContextManagedStatementCreateWindowDesc(StatementSpecCompiled statementSpec, StatementContext statementContext, ContextMergeView mergeView, StatementAgentInstanceFactory factory) {
        super(statementSpec, statementContext, mergeView, factory);
    }
}
