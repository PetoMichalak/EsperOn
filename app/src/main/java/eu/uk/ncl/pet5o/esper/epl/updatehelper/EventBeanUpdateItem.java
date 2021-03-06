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
package eu.uk.ncl.pet5o.esper.epl.updatehelper;

import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.event.EventPropertyWriter;
import eu.uk.ncl.pet5o.esper.util.TypeWidener;

public class EventBeanUpdateItem {
    private final ExprEvaluator expression;
    private final String optionalPropertyName;
    private final EventPropertyWriter optionalWriter;
    private final boolean notNullableField;
    private final TypeWidener optionalWidener;

    public EventBeanUpdateItem(ExprEvaluator expression, String optinalPropertyName, EventPropertyWriter optionalWriter, boolean notNullableField, TypeWidener optionalWidener) {
        this.expression = expression;
        this.optionalPropertyName = optinalPropertyName;
        this.optionalWriter = optionalWriter;
        this.notNullableField = notNullableField;
        this.optionalWidener = optionalWidener;
    }

    public ExprEvaluator getExpression() {
        return expression;
    }

    public String getOptionalPropertyName() {
        return optionalPropertyName;
    }

    public EventPropertyWriter getOptionalWriter() {
        return optionalWriter;
    }

    public boolean isNotNullableField() {
        return notNullableField;
    }

    public TypeWidener getOptionalWidener() {
        return optionalWidener;
    }
}
