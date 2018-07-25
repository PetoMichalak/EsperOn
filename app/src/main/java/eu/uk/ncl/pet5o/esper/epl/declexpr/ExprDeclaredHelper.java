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

import eu.uk.ncl.pet5o.esper.core.context.util.ContextDescriptor;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;
import eu.uk.ncl.pet5o.esper.epl.script.ExprNodeScript;
import eu.uk.ncl.pet5o.esper.epl.spec.ExpressionDeclItem;
import eu.uk.ncl.pet5o.esper.epl.spec.ExpressionScriptProvided;

import java.util.Collection;
import java.util.List;

public class ExprDeclaredHelper {
    public static ExprDeclaredNodeImpl getExistsDeclaredExpr(String name, List<ExprNode> parameters, Collection<ExpressionDeclItem> expressionDeclarations, ExprDeclaredService exprDeclaredService, ContextDescriptor contextDescriptor) {
        // Find among local expressions
        if (!expressionDeclarations.isEmpty()) {
            for (ExpressionDeclItem declNode : expressionDeclarations) {
                if (declNode.getName().equals(name)) {
                    return new ExprDeclaredNodeImpl(declNode, parameters, contextDescriptor);
                }
            }
        }

        // find among global expressions
        ExpressionDeclItem found = exprDeclaredService.getExpression(name);
        if (found != null) {
            return new ExprDeclaredNodeImpl(found, parameters, contextDescriptor);
        }
        return null;
    }

    public static ExprNodeScript getExistsScript(String defaultDialect, String expressionName, List<ExprNode> parameters, Collection<ExpressionScriptProvided> scriptExpressions, ExprDeclaredService exprDeclaredService) {
        if (!scriptExpressions.isEmpty()) {
            ExpressionScriptProvided script = findScript(expressionName, parameters.size(), scriptExpressions);
            if (script != null) {
                return new ExprNodeScript(defaultDialect, script, parameters);
            }
        }

        List<ExpressionScriptProvided> globalScripts = exprDeclaredService.getScriptsByName(expressionName);
        ExpressionScriptProvided script = findScript(expressionName, parameters.size(), globalScripts);
        if (script != null) {
            return new ExprNodeScript(defaultDialect, script, parameters);
        }
        return null;
    }

    private static ExpressionScriptProvided findScript(String name, int parameterCount, Collection<ExpressionScriptProvided> scriptsByName) {
        if (scriptsByName == null || scriptsByName.isEmpty()) {
            return null;
        }
        ExpressionScriptProvided nameMatchedScript = null;
        for (ExpressionScriptProvided script : scriptsByName) {
            if (script.getName().equals(name) && script.getParameterNames().size() == parameterCount) {
                return script;
            }
            if (script.getName().equals(name)) {
                nameMatchedScript = script;
            }
        }
        return nameMatchedScript;
    }
}
