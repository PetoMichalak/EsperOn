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
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionRef;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionTypePair;
import eu.uk.ncl.pet5o.esper.collection.RefCountedSet;
import eu.uk.ncl.pet5o.esper.collection.SortedDoubleVector;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprForge;
import eu.uk.ncl.pet5o.esper.util.SimpleNumberCoercerFactory;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.newInstance;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.refCol;

/**
 * Median aggregation.
 */
public class AggregatorMedian implements AggregationMethod {
    protected SortedDoubleVector vector;

    public AggregatorMedian() {
        this.vector = new SortedDoubleVector();
    }

    public static void rowMemberCodegen(boolean distinct, int column, CodegenCtor ctor, CodegenMembersColumnized membersColumnized) {
        membersColumnized.addMember(column, SortedDoubleVector.class, "vector");
        ctor.getBlock().assignRef(refCol("vector", column), newInstance(SortedDoubleVector.class));
        if (distinct) {
            membersColumnized.addMember(column, RefCountedSet.class, "distinctSet");
            ctor.getBlock().assignRef(refCol("distinctSet", column), newInstance(RefCountedSet.class));
        }
    }

    public void enter(Object object) {
        if (object == null) {
            return;
        }
        double value = ((Number) object).doubleValue();
        vector.add(value);
    }

    public static void applyEnterCodegen(boolean distinct, boolean hasFilter, int column, CodegenMethodNode method, ExprForgeCodegenSymbol symbols, ExprForge[] forges, CodegenClassScope classScope) {
        CodegenExpressionTypePair value = AggregatorCodegenUtil.prefixWithFilterNullDistinctChecks(true, distinct, hasFilter, forges, column, method, symbols, classScope);

        CodegenExpressionRef vector = refCol("vector", column);

        method.getBlock().exprDotMethod(vector, "add", SimpleNumberCoercerFactory.SimpleNumberCoercerDouble.codegenDouble(value.getExpression(), value.getType()));
    }

    public void leave(Object object) {
        if (object == null) {
            return;
        }
        double value = ((Number) object).doubleValue();
        vector.remove(value);
    }

    public static void applyLeaveCodegen(boolean distinct, boolean hasFilter, int column, CodegenMethodNode method, ExprForgeCodegenSymbol symbols, ExprForge[] forges, CodegenClassScope classScope) {
        CodegenExpressionTypePair value = AggregatorCodegenUtil.prefixWithFilterNullDistinctChecks(false, distinct, hasFilter, forges, column, method, symbols, classScope);

        CodegenExpressionRef vector = refCol("vector", column);

        method.getBlock().exprDotMethod(vector, "remove", SimpleNumberCoercerFactory.SimpleNumberCoercerDouble.codegenDouble(value.getExpression(), value.getType()));
    }

    public void clear() {
        vector.clear();
    }

    public static void clearCodegen(boolean distinct, int column, CodegenMethodNode method) {
        method.getBlock().exprDotMethod(refCol("vector", column), "clear")
                .applyConditional(distinct, block -> block.exprDotMethod(refCol("distinctSet", column), "clear"));
    }

    public Object getValue() {
        return medianCompute(vector);
    }

    public static void getValueCodegen(int column, CodegenMethodNode method) {
        method.getBlock().methodReturn(staticMethod(AggregatorMedian.class, "medianCompute", refCol("vector", column)));
    }

    /**
     * NOTE: Code-generation-invoked method, method name and parameter order matters
     *
     * @param vector vector
     * @return value
     */
    public static Object medianCompute(SortedDoubleVector vector) {
        if (vector.size() == 0) {
            return null;
        }
        if (vector.size() == 1) {
            return vector.getValue(0);
        }

        int middle = vector.size() >> 1;
        if (vector.size() % 2 == 0) {
            return (vector.getValue(middle - 1) + vector.getValue(middle)) / 2;
        } else {
            return vector.getValue(middle);
        }
    }
}
