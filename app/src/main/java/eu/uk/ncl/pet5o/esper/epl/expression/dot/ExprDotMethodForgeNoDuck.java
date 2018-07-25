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
package eu.uk.ncl.pet5o.esper.epl.expression.dot;

import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprForge;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNodeUtilityCore;
import eu.uk.ncl.pet5o.esper.epl.rettype.EPType;
import eu.uk.ncl.pet5o.esper.epl.rettype.EPTypeHelper;

import net.sf.cglib.reflect.FastMethod;

public class ExprDotMethodForgeNoDuck implements ExprDotForge {

    private final String statementName;
    private final FastMethod method;
    private final ExprForge[] parameters;
    private final Type type;

    public ExprDotMethodForgeNoDuck(String statementName, FastMethod method, ExprForge[] parameters, Type type) {
        this.statementName = statementName;
        this.method = method;
        this.parameters = parameters;
        this.type = type;
    }

    public EPType getTypeInfo() {
        if (type == Type.WRAPARRAY) {
            return EPTypeHelper.collectionOfSingleValue(method.getReturnType().getComponentType());
        } else {
            return EPTypeHelper.fromMethod(method.getJavaMethod());
        }
    }

    public void visit(ExprDotEvalVisitor visitor) {
        visitor.visitMethod(method.getName());
    }

    public ExprDotEval getDotEvaluator() {
        ExprEvaluator[] evaluators = ExprNodeUtilityCore.getEvaluatorsNoCompile(parameters);
        if (type == Type.WRAPARRAY) {
            return new ExprDotMethodForgeNoDuckEvalWrapArray(this, evaluators);
        } else if (type == Type.PLAIN) {
            return new ExprDotMethodForgeNoDuckEvalPlain(this, evaluators);
        } else {
            return new ExprDotMethodForgeNoDuckEvalUnderlying(this, evaluators);
        }
    }

    public CodegenExpression codegen(CodegenExpression inner, Class innerType, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        if (type == Type.WRAPARRAY) {
            return ExprDotMethodForgeNoDuckEvalWrapArray.codegenWrapArray(this, inner, innerType, codegenMethodScope, exprSymbol, codegenClassScope);
        } else if (type == Type.PLAIN) {
            return ExprDotMethodForgeNoDuckEvalPlain.codegenPlain(this, inner, innerType, codegenMethodScope, exprSymbol, codegenClassScope);
        } else {
            return ExprDotMethodForgeNoDuckEvalUnderlying.codegenUnderlying(this, inner, innerType, codegenMethodScope, exprSymbol, codegenClassScope);
        }
    }

    public String getStatementName() {
        return statementName;
    }

    public FastMethod getMethod() {
        return method;
    }

    public ExprForge[] getParameters() {
        return parameters;
    }

    public enum Type {
        WRAPARRAY,
        UNDERLYING,
        PLAIN
    }
}
