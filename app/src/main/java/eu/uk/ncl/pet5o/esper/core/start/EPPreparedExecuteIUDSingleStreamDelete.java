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
package eu.uk.ncl.pet5o.esper.core.start;

import eu.uk.ncl.pet5o.esper.core.service.EPServicesContext;
import eu.uk.ncl.pet5o.esper.core.service.StatementContext;
import eu.uk.ncl.pet5o.esper.core.service.speccompiled.StatementSpecCompiled;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprValidationException;
import eu.uk.ncl.pet5o.esper.epl.join.plan.QueryGraph;

/**
 * Starts and provides the stop method for EPL statements.
 */
public class EPPreparedExecuteIUDSingleStreamDelete extends EPPreparedExecuteIUDSingleStream {
    public EPPreparedExecuteIUDSingleStreamDelete(StatementSpecCompiled statementSpec, EPServicesContext services, StatementContext statementContext) throws ExprValidationException {
        super(statementSpec, services, statementContext);
    }

    public EPPreparedExecuteIUDSingleStreamExec getExecutor(QueryGraph queryGraph, String aliasName) {
        return new EPPreparedExecuteIUDSingleStreamExecDelete(queryGraph, statementSpec.getFilterRootNode(), statementSpec.getAnnotations(), statementSpec.getTableNodes(), services);
    }
}
