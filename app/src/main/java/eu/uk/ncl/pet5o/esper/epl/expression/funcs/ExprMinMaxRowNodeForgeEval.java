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
package eu.uk.ncl.pet5o.esper.epl.expression.funcs;

import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprForge;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;
import eu.uk.ncl.pet5o.esper.epl.expression.core.MinMaxTypeEnum;
import eu.uk.ncl.pet5o.esper.metrics.instrumentation.InstrumentationHelper;
import eu.uk.ncl.pet5o.esper.util.JavaClassHelper;
import eu.uk.ncl.pet5o.esper.util.SimpleNumberBigDecimalCoercer;
import eu.uk.ncl.pet5o.esper.util.SimpleNumberBigIntegerCoercer;
import eu.uk.ncl.pet5o.esper.util.SimpleNumberCoercerFactory;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Represents the MAX(a,b) and MIN(a,b) functions is an expression tree.
 */
public class ExprMinMaxRowNodeForgeEval implements ExprEvaluator {

    private final ExprMinMaxRowNodeForge forge;
    private final MinMaxTypeEnum.Computer computer;

    public ExprMinMaxRowNodeForgeEval(ExprMinMaxRowNodeForge forge, ExprEvaluator[] evaluators, ExprForge[] forges) {
        this.forge = forge;
        if (forge.getEvaluationType() == BigInteger.class) {
            SimpleNumberBigIntegerCoercer[] convertors = new SimpleNumberBigIntegerCoercer[evaluators.length];
            for (int i = 0; i < evaluators.length; i++) {
                convertors[i] = SimpleNumberCoercerFactory.getCoercerBigInteger(forges[i].getEvaluationType());
            }
            computer = new MinMaxTypeEnum.ComputerBigIntCoerce(evaluators, convertors, forge.getForgeRenderable().getMinMaxTypeEnum() == MinMaxTypeEnum.MAX);
        } else if (forge.getEvaluationType() == BigDecimal.class) {
            SimpleNumberBigDecimalCoercer[] convertors = new SimpleNumberBigDecimalCoercer[evaluators.length];
            for (int i = 0; i < evaluators.length; i++) {
                convertors[i] = SimpleNumberCoercerFactory.getCoercerBigDecimal(forges[i].getEvaluationType());
            }
            computer = new MinMaxTypeEnum.ComputerBigDecCoerce(evaluators, convertors, forge.getForgeRenderable().getMinMaxTypeEnum() == MinMaxTypeEnum.MAX);
        } else {
            if (forge.getForgeRenderable().getMinMaxTypeEnum() == MinMaxTypeEnum.MAX) {
                computer = new MinMaxTypeEnum.MaxComputerDoubleCoerce(evaluators);
            } else {
                computer = new MinMaxTypeEnum.MinComputerDoubleCoerce(evaluators);
            }
        }
    }

    public Object evaluate(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().qExprMinMaxRow(forge.getForgeRenderable());
        }
        Number result = computer.execute(eventsPerStream, isNewData, exprEvaluatorContext);

        if (InstrumentationHelper.ENABLED) {
            Number minmax = null;
            if (result != null) {
                minmax = JavaClassHelper.coerceBoxed(result, forge.getEvaluationType());
            }
            InstrumentationHelper.get().aExprMinMaxRow(minmax);
            return minmax;
        }

        if (result == null) {
            return null;
        }
        return JavaClassHelper.coerceBoxed(result, forge.getEvaluationType());
    }

    public static CodegenExpression codegen(ExprMinMaxRowNodeForge forge, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        Class resultType = forge.getEvaluationType();
        ExprNode[] nodes = forge.getForgeRenderable().getChildNodes();

        CodegenExpression expression;
        if (resultType == BigInteger.class) {
            SimpleNumberBigIntegerCoercer[] convertors = new SimpleNumberBigIntegerCoercer[nodes.length];
            for (int i = 0; i < nodes.length; i++) {
                convertors[i] = SimpleNumberCoercerFactory.getCoercerBigInteger(nodes[i].getForge().getEvaluationType());
            }
            expression = MinMaxTypeEnum.ComputerBigIntCoerce.codegen(forge.getForgeRenderable().getMinMaxTypeEnum() == MinMaxTypeEnum.MAX, codegenMethodScope, exprSymbol, codegenClassScope, nodes, convertors);
        } else if (resultType == BigDecimal.class) {
            SimpleNumberBigDecimalCoercer[] convertors = new SimpleNumberBigDecimalCoercer[nodes.length];
            for (int i = 0; i < nodes.length; i++) {
                convertors[i] = SimpleNumberCoercerFactory.getCoercerBigDecimal(nodes[i].getForge().getEvaluationType());
            }
            expression = MinMaxTypeEnum.ComputerBigDecCoerce.codegen(forge.getForgeRenderable().getMinMaxTypeEnum() == MinMaxTypeEnum.MAX, codegenMethodScope, exprSymbol, codegenClassScope, nodes, convertors);
        } else {
            if (forge.getForgeRenderable().getMinMaxTypeEnum() == MinMaxTypeEnum.MAX) {
                expression = MinMaxTypeEnum.MaxComputerDoubleCoerce.codegen(codegenMethodScope, exprSymbol, codegenClassScope, nodes, resultType);
            } else {
                expression = MinMaxTypeEnum.MinComputerDoubleCoerce.codegen(codegenMethodScope, exprSymbol, codegenClassScope, nodes, resultType);
            }
        }
        return expression;
    }

}
