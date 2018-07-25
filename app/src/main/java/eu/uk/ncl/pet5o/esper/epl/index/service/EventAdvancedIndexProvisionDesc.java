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
package eu.uk.ncl.pet5o.esper.epl.index.service;

import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.lookup.AdvancedIndexDesc;
import eu.uk.ncl.pet5o.esper.epl.lookup.EventAdvancedIndexConfigStatement;
import eu.uk.ncl.pet5o.esper.epl.lookup.EventAdvancedIndexFactory;

public class EventAdvancedIndexProvisionDesc {
    private final AdvancedIndexDesc indexDesc;
    private final ExprEvaluator[] parameters;
    private final EventAdvancedIndexFactory factory;
    private final EventAdvancedIndexConfigStatement configStatement;

    public EventAdvancedIndexProvisionDesc(AdvancedIndexDesc indexDesc, ExprEvaluator[] parameters, EventAdvancedIndexFactory factory, EventAdvancedIndexConfigStatement configStatement) {
        this.indexDesc = indexDesc;
        this.parameters = parameters;
        this.factory = factory;
        this.configStatement = configStatement;
    }

    public AdvancedIndexDesc getIndexDesc() {
        return indexDesc;
    }

    public ExprEvaluator[] getParameters() {
        return parameters;
    }

    public EventAdvancedIndexFactory getFactory() {
        return factory;
    }

    public EventAdvancedIndexConfigStatement getConfigStatement() {
        return configStatement;
    }
}
