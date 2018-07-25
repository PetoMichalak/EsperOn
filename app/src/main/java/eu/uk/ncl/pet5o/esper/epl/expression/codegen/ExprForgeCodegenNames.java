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
package eu.uk.ncl.pet5o.esper.epl.expression.codegen;

import eu.uk.ncl.pet5o.esper.codegen.core.CodegenNamedParam;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionRef;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;

import java.util.Arrays;
import java.util.List;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

public class ExprForgeCodegenNames {

    public final static String NAME_EPS = "eventsPerStream";
    public final static String NAME_ISNEWDATA = "isNewData";
    public final static String NAME_EXPREVALCONTEXT = "exprEvalCtx";

    public final static CodegenExpressionRef REF_EPS = ref(NAME_EPS);
    public final static CodegenExpressionRef REF_ISNEWDATA = ref(NAME_ISNEWDATA);
    public final static CodegenExpressionRef REF_EXPREVALCONTEXT = ref(NAME_EXPREVALCONTEXT);

    public final static CodegenNamedParam FP_EPS = new CodegenNamedParam(eu.uk.ncl.pet5o.esper.client.EventBean[].class, NAME_EPS);
    public final static CodegenNamedParam FP_ISNEWDATA = new CodegenNamedParam(boolean.class, NAME_ISNEWDATA);
    public final static CodegenNamedParam FP_EXPREVALCONTEXT = new CodegenNamedParam(ExprEvaluatorContext.class, NAME_EXPREVALCONTEXT);

    public final static List<CodegenNamedParam> PARAMS = Arrays.asList(FP_EPS, FP_ISNEWDATA, FP_EXPREVALCONTEXT);
}

