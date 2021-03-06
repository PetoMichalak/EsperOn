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
package eu.uk.ncl.pet5o.esper.epl.core.select.eval;

import eu.uk.ncl.pet5o.esper.core.service.speccompiled.SelectClauseStreamCompiledSpec;
import eu.uk.ncl.pet5o.esper.epl.spec.SelectClauseExprCompiledSpec;

public class SelectExprStreamDesc {
    private final SelectClauseStreamCompiledSpec streamSelected;
    private final SelectClauseExprCompiledSpec expressionSelectedAsStream;

    public SelectExprStreamDesc(SelectClauseStreamCompiledSpec streamSelected) {
        this.streamSelected = streamSelected;
        this.expressionSelectedAsStream = null;
    }

    public SelectExprStreamDesc(SelectClauseExprCompiledSpec expressionSelectedAsStream) {
        this.expressionSelectedAsStream = expressionSelectedAsStream;
        this.streamSelected = null;
    }

    public SelectClauseStreamCompiledSpec getStreamSelected() {
        return streamSelected;
    }

    public SelectClauseExprCompiledSpec getExpressionSelectedAsStream() {
        return expressionSelectedAsStream;
    }
}
