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
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenBlock;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprForge;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.arrayAtIndex;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethodBuild;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.newArrayByLength;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

public class ExprDeclaredForgeRewrite extends ExprDeclaredForgeBase {
    private final int[] streamAssignments;

    public ExprDeclaredForgeRewrite(ExprDeclaredNodeImpl parent, ExprForge innerForge, boolean isCache, int[] streamAssignments, boolean audit, String engineURI, String statementName) {
        super(parent, innerForge, isCache, audit, engineURI, statementName);
        this.streamAssignments = streamAssignments;
    }

    public EventBean[] getEventsPerStreamRewritten(EventBean[] eps) {

        // rewrite streams
        EventBean[] events = new EventBean[streamAssignments.length];
        for (int i = 0; i < streamAssignments.length; i++) {
            events[i] = eps[streamAssignments[i]];
        }

        return events;
    }

    protected CodegenExpression codegenEventsPerStreamRewritten(CodegenExpression eventsPerStream, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        CodegenBlock block = codegenMethodScope.makeChild(EventBean[].class, ExprDeclaredForgeRewrite.class, codegenClassScope).addParam(EventBean[].class, "eps").getBlock()
                .declareVar(EventBean[].class, "events", newArrayByLength(EventBean.class, constant(streamAssignments.length)));
        for (int i = 0; i < streamAssignments.length; i++) {
            block.assignArrayElement("events", constant(i), arrayAtIndex(ref("eps"), constant(streamAssignments[i])));
        }
        return localMethodBuild(block.methodReturn(ref("events"))).pass(eventsPerStream).call();
    }
}
