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
import eu.uk.ncl.pet5o.esper.epl.spec.CreateIndexDesc;
import eu.uk.ncl.pet5o.esper.epl.spec.CreateIndexItem;
import eu.uk.ncl.pet5o.esper.epl.spec.CreateIndexType;

import org.antlr.v4.runtime.tree.Tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ASTIndexHelper {
    public static CreateIndexDesc walk(EsperEPL2GrammarParser.CreateIndexExprContext ctx, Map<Tree, ExprNode> astExprNodeMap) {
        String indexName = ctx.n.getText();
        String windowName = ctx.w.getText();

        boolean unique = false;
        if (ctx.u != null) {
            String ident = ctx.u.getText();
            if (ident.toLowerCase(Locale.ENGLISH).trim().equals("unique")) {
                unique = true;
            } else {
                throw ASTWalkException.from("Invalid keyword '" + ident + "' in create-index encountered, expected 'unique'");
            }
        }

        List<CreateIndexItem> columns = new ArrayList<>();
        List<EsperEPL2GrammarParser.CreateIndexColumnContext> cols = ctx.createIndexColumnList().createIndexColumn();
        for (EsperEPL2GrammarParser.CreateIndexColumnContext col : cols) {
            CreateIndexItem item = walk(col, astExprNodeMap);
            columns.add(item);
        }
        return new CreateIndexDesc(unique, indexName, windowName, columns);
    }

    private static CreateIndexItem walk(EsperEPL2GrammarParser.CreateIndexColumnContext col, Map<Tree, ExprNode> astExprNodeMap) {
        List<ExprNode> expressions = Collections.emptyList();
        if (col.i != null) {
            expressions = ASTExprHelper.exprCollectSubNodes(col.i, 0, astExprNodeMap);
        } else if (col.expression() != null) {
            expressions = ASTExprHelper.exprCollectSubNodes(col.expression(), 0, astExprNodeMap);
        }

        String type = CreateIndexType.HASH.getNameLower();
        if (col.t != null) {
            type = col.t.getText();
        }

        List<ExprNode> parameters = Collections.<ExprNode>emptyList();
        if (col.p != null) {
            parameters = ASTExprHelper.exprCollectSubNodes(col.p, 0, astExprNodeMap);
        }
        return new CreateIndexItem(expressions, type, parameters);
    }
}
