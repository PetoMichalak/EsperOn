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
import eu.uk.ncl.pet5o.esper.epl.agg.factory.AggregationMethodFactoryLeaving;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantFalse;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantTrue;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.refCol;

/**
 * For testing if a remove stream entry has been present.
 */
public class AggregatorLeaving implements AggregationMethod {

    protected boolean leaving = false;

    public static void rowMemberCodegen(int column, CodegenCtor ctor, CodegenMembersColumnized membersColumnized) {
        membersColumnized.addMember(column, boolean.class, "leaving");
    }

    public void enter(Object value) {
    }

    public void leave(Object value) {
        leaving = true;
    }

    public static void applyLeaveCodegen(AggregationMethodFactoryLeaving forge, int column, CodegenMethodNode method, ExprForgeCodegenSymbol symbols, CodegenClassScope classScope) {
        if (forge.getAggregationExpression().getPositionalParams().length > 0) {
            AggregatorCodegenUtil.prefixWithFilterCheck(forge.getAggregationExpression().getPositionalParams()[0].getForge(), method, symbols, classScope);
        }
        method.getBlock().assignRef(refCol("leaving", column), constantTrue());
    }

    public void clear() {
        leaving = false;
    }

    public static void clearCodegen(int column, CodegenMethodNode method) {
        method.getBlock().assignRef(refCol("leaving", column), constantFalse());
    }

    public Object getValue() {
        return leaving;
    }

    public static void getValueCodegen(int column, CodegenMethodNode method) {
        method.getBlock().methodReturn(refCol("leaving", column));
    }
}
