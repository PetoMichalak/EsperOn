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
package eu.uk.ncl.pet5o.esper.epl.core.engineimport;

import eu.uk.ncl.pet5o.esper.epl.expression.core.*;
import eu.uk.ncl.pet5o.esper.epl.join.plan.FilterExprAnalyzerAffector;
import eu.uk.ncl.pet5o.esper.filter.FilterSpecCompilerAdvIndexDesc;

public interface EngineImportApplicationDotMethod {

    ExprNode validate(ExprValidationContext validationContext) throws ExprValidationException;

    FilterExprAnalyzerAffector getFilterExprAnalyzerAffector();
    FilterSpecCompilerAdvIndexDesc getFilterSpecCompilerAdvIndexDesc();

    ExprForge getForge();

    String getLhsName();
    ExprNode[] getLhs();
    String getDotMethodName();
    String getRhsName();
    ExprNode[] getRhs();
}
