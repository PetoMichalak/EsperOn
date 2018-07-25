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

import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprValidationException;
import eu.uk.ncl.pet5o.esper.epl.spec.CreateExpressionDesc;
import eu.uk.ncl.pet5o.esper.epl.spec.ExpressionDeclItem;
import eu.uk.ncl.pet5o.esper.epl.spec.ExpressionScriptProvided;

import java.util.List;

public interface ExprDeclaredService {
    public ExpressionDeclItem getExpression(String name);

    public List<ExpressionScriptProvided> getScriptsByName(String expressionName);

    public String addExpressionOrScript(CreateExpressionDesc expression) throws ExprValidationException;

    public void destroyedExpression(CreateExpressionDesc expression);

    public void destroy();
}
