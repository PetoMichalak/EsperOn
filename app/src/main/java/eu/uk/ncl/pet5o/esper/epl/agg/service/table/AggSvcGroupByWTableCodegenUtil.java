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
package eu.uk.ncl.pet5o.esper.epl.agg.service.table;

import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionRef;
import eu.uk.ncl.pet5o.esper.epl.table.strategy.ExprTableEvalLockUtil;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethodChain;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_AGENTINSTANCECONTEXT;

public class AggSvcGroupByWTableCodegenUtil {
    protected final static CodegenExpressionRef REF_TABLESTATEINSTANCE = ref("tableStateInstance");

    public static void obtainWriteLockCodegen(CodegenMethodNode method) {
        method.getBlock().staticMethod(ExprTableEvalLockUtil.class, "obtainLockUnless", exprDotMethodChain(REF_TABLESTATEINSTANCE).add("getTableLevelRWLock").add("writeLock"), REF_AGENTINSTANCECONTEXT);
    }
}
