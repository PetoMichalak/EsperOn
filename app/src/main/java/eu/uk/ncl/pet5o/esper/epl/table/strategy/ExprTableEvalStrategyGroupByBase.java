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
package eu.uk.ncl.pet5o.esper.epl.table.strategy;

import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableStateInstanceGrouped;
import eu.uk.ncl.pet5o.esper.event.ObjectArrayBackedEventBean;

public abstract class ExprTableEvalStrategyGroupByBase {

    private final TableAndLockProviderGrouped provider;

    protected ExprTableEvalStrategyGroupByBase(TableAndLockProviderGrouped provider) {
        this.provider = provider;
    }

    protected ObjectArrayBackedEventBean lockTableReadAndGet(Object group, ExprEvaluatorContext context) {
        TableAndLockGrouped tableAndLockGrouped = provider.get();
        ExprTableEvalLockUtil.obtainLockUnless(tableAndLockGrouped.getLock(), context);
        return tableAndLockGrouped.getGrouped().getRowForGroupKey(group);
    }

    protected TableStateInstanceGrouped lockTableRead(ExprEvaluatorContext context) {
        TableAndLockGrouped tableAndLockGrouped = provider.get();
        ExprTableEvalLockUtil.obtainLockUnless(tableAndLockGrouped.getLock(), context);
        return tableAndLockGrouped.getGrouped();
    }
}
