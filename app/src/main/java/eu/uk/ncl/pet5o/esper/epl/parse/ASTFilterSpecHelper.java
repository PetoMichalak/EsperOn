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
package eu.uk.ncl.pet5o.esper.epl.parse;

import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;
import eu.uk.ncl.pet5o.esper.epl.generated.EsperEPL2GrammarParser;
import eu.uk.ncl.pet5o.esper.epl.spec.FilterSpecRaw;
import eu.uk.ncl.pet5o.esper.epl.spec.PropertyEvalSpec;

import org.antlr.v4.runtime.tree.Tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Builds a filter specification from filter AST nodes.
 */
public class ASTFilterSpecHelper {
    public static FilterSpecRaw walkFilterSpec(EsperEPL2GrammarParser.EventFilterExpressionContext ctx, PropertyEvalSpec propertyEvalSpec, Map<Tree, ExprNode> astExprNodeMap) {
        String eventName = ASTUtil.unescapeClassIdent(ctx.classIdentifier());
        List<ExprNode> exprNodes = ctx.expressionList() != null ? ASTExprHelper.exprCollectSubNodes(ctx.expressionList(), 0, astExprNodeMap) : new ArrayList<ExprNode>(1);
        return new FilterSpecRaw(eventName, exprNodes, propertyEvalSpec);
    }
}
