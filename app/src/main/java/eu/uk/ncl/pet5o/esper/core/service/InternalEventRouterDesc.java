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
package eu.uk.ncl.pet5o.esper.core.service;

import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineImportService;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprNodeCompiler;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.spec.UpdateDesc;
import eu.uk.ncl.pet5o.esper.event.EventBeanCopyMethod;
import eu.uk.ncl.pet5o.esper.util.TypeWidener;

import java.lang.annotation.Annotation;

public class InternalEventRouterDesc {
    private final UpdateDesc updateDesc;
    private final EventBeanCopyMethod copyMethod;
    private final TypeWidener[] wideners;
    private final EventType eventType;
    private final Annotation[] annotations;
    private final ExprEvaluator optionalWhereClauseEval;

    public InternalEventRouterDesc(UpdateDesc updateDesc, EventBeanCopyMethod copyMethod, TypeWidener[] wideners, EventType eventType, Annotation[] annotations, EngineImportService engineImportService, String statementName) {
        this.updateDesc = updateDesc;
        this.copyMethod = copyMethod;
        this.wideners = wideners;
        this.eventType = eventType;
        this.annotations = annotations;
        optionalWhereClauseEval = updateDesc.getOptionalWhereClause() == null ? null : ExprNodeCompiler.allocateEvaluator(updateDesc.getOptionalWhereClause().getForge(), engineImportService, this.getClass(), false, statementName);
    }

    public UpdateDesc getUpdateDesc() {
        return updateDesc;
    }

    public EventBeanCopyMethod getCopyMethod() {
        return copyMethod;
    }

    public TypeWidener[] getWideners() {
        return wideners;
    }

    public EventType getEventType() {
        return eventType;
    }

    public Annotation[] getAnnotations() {
        return annotations;
    }

    public ExprEvaluator getOptionalWhereClauseEval() {
        return optionalWhereClauseEval;
    }
}
