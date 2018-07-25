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
package eu.uk.ncl.pet5o.esper.epl.declexpr;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.client.annotation.AuditEnum;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.*;
import eu.uk.ncl.pet5o.esper.epl.spec.ExpressionDeclItem;
import eu.uk.ncl.pet5o.esper.metrics.instrumentation.InstrumentationHelper;
import eu.uk.ncl.pet5o.esper.util.AuditPath;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.enumValue;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.op;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.staticMethod;
import static eu.uk.ncl.pet5o.esper.util.AuditPath.METHOD_AUDITLOG;

public class ExprDeclaredForgeConstant implements ExprForge, ExprEvaluator {
    private final ExprDeclaredNodeImpl parent;
    private final Class returnType;
    private final ExpressionDeclItem prototype;
    private final Object value;
    private final boolean audit;
    private final String engineURI;
    private final String statementName;

    public ExprDeclaredForgeConstant(ExprDeclaredNodeImpl parent, Class returnType, ExpressionDeclItem prototype, Object value, boolean audit, String engineURI, String statementName) {
        this.parent = parent;
        this.returnType = returnType;
        this.prototype = prototype;
        this.value = value;
        this.audit = audit;
        this.engineURI = engineURI;
        this.statementName = statementName;
    }

    public Object evaluate(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().qExprDeclared(prototype);
            InstrumentationHelper.get().aExprDeclared(value);
        }
        return value;
    }

    public ExprEvaluator getExprEvaluator() {
        if (audit) {
            return (ExprEvaluator) ExprEvaluatorProxy.newInstance(engineURI, statementName, prototype.getName(), this);
        }
        return this;
    }

    public ExprForgeComplexityEnum getComplexity() {
        return ExprForgeComplexityEnum.NONE;
    }

    public CodegenExpression evaluateCodegen(Class requiredType, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        if (!audit) {
            return constant(value);
        }
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(returnType, ExprDeclaredForgeConstant.class, codegenClassScope);

        methodNode.getBlock()
                .ifCondition(staticMethod(AuditPath.class, "isInfoEnabled"))
                .staticMethod(AuditPath.class, METHOD_AUDITLOG, constant(engineURI), constant(statementName), enumValue(AuditEnum.class, "EXPRDEF"), op(constant(prototype.getName() + " result "), "+", constant(value)))
                .blockEnd()
                .methodReturn(constant(value));
        return localMethod(methodNode);
    }

    public Class getEvaluationType() {
        return returnType;
    }

    public ExprNodeRenderable getForgeRenderable() {
        return parent;
    }
}
