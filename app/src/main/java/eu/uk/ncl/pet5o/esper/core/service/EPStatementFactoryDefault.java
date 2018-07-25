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
package eu.uk.ncl.pet5o.esper.core.service;

import eu.uk.ncl.pet5o.esper.core.context.factory.StatementAgentInstanceFactoryResult;
import eu.uk.ncl.pet5o.esper.dispatch.DispatchService;
import eu.uk.ncl.pet5o.esper.timer.TimeSourceService;
import eu.uk.ncl.pet5o.esper.util.StopCallback;

public class EPStatementFactoryDefault implements EPStatementFactory {
    public EPStatementSPI make(String expressionNoAnnotations, boolean isPattern, DispatchService dispatchService, StatementLifecycleSvcImpl statementLifecycleSvc, long timeLastStateChange, boolean preserveDispatchOrder, boolean isSpinLocks, long blockingTimeout, TimeSourceService timeSource, StatementMetadata statementMetadata, Object statementUserObject, StatementContext statementContext, boolean isFailed, boolean nameProvided) {
        return new EPStatementImpl(expressionNoAnnotations, isPattern, dispatchService, statementLifecycleSvc, timeLastStateChange, preserveDispatchOrder, isSpinLocks, blockingTimeout, timeSource, statementMetadata, statementUserObject, statementContext, isFailed, nameProvided);
    }

    public StopCallback makeStopMethod(StatementAgentInstanceFactoryResult startResult) {
        return startResult.getStopCallback();
    }
}
