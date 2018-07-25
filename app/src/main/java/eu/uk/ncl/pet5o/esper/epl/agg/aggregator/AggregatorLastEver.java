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
package eu.uk.ncl.pet5o.esper.epl.agg.aggregator;

import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMembersColumnized;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.core.CodegenCtor;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprForge;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.refCol;

/**
 * Aggregator for the very last value.
 */
public class AggregatorLastEver implements AggregationMethod {
    protected Object lastValue;

    public static void rowMemberCodegen(int column, CodegenCtor ctor, CodegenMembersColumnized membersColumnized) {
        membersColumnized.addMember(column, Object.class, "lastValue");
    }

    public void enter(Object object) {
        lastValue = object;
    }

    public static void applyEnterCodegen(boolean hasFilter, int column, CodegenMethodNode method, ExprForgeCodegenSymbol symbols, ExprForge[] forges, CodegenClassScope classScope) {
        if (hasFilter) {
            AggregatorCodegenUtil.prefixWithFilterCheck(forges[forges.length - 1], method, symbols, classScope);
        }
        method.getBlock().assignRef(refCol("lastValue", column), forges[0].evaluateCodegen(Object.class, method, symbols, classScope));
    }

    public void leave(Object object) {
    }

    public void clear() {
        lastValue = null;
    }

    public static void clearCodegen(int column, CodegenMethodNode method) {
        method.getBlock().assignRef(refCol("lastValue", column), constantNull());
    }

    public Object getValue() {
        return lastValue;
    }

    public static void getValueCodegen(int column, CodegenMethodNode method) {
        method.getBlock().methodReturn(refCol("lastValue", column));
    }
}
