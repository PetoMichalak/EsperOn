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
package eu.uk.ncl.pet5o.esper.epl.join.util;

import eu.uk.ncl.pet5o.esper.client.EPException;
import eu.uk.ncl.pet5o.esper.client.annotation.HookType;
import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineImportService;
import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineImportUtil;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprValidationException;

import java.lang.annotation.Annotation;

public class QueryPlanIndexHookUtil {

    public static QueryPlanIndexHook getHook(Annotation[] annotations, EngineImportService engineImportService) {
        try {
            return (QueryPlanIndexHook) EngineImportUtil.getAnnotationHook(annotations, HookType.INTERNAL_QUERY_PLAN, QueryPlanIndexHook.class, engineImportService);
        } catch (ExprValidationException e) {
            throw new EPException("Failed to obtain hook for " + HookType.INTERNAL_QUERY_PLAN);
        }
    }

}
