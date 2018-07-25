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
package eu.uk.ncl.pet5o.esper.core.context.mgr;

import eu.uk.ncl.pet5o.esper.client.EventPropertyGetter;
import eu.uk.ncl.pet5o.esper.client.PropertyAccessException;
import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineImportService;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprNodeCompiler;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ContextControllerHashedGetterHashMultiple implements EventPropertyGetter {
    private static final Logger log = LoggerFactory.getLogger(ContextControllerHashedGetterHashMultiple.class);

    private final ExprEvaluator[] evaluators;
    private final int granularity;

    public ContextControllerHashedGetterHashMultiple(List<ExprNode> nodes, int granularity, EngineImportService engineImportService, String statementName) {
        evaluators = new ExprEvaluator[nodes.size()];
        for (int i = 0; i < nodes.size(); i++) {
            evaluators[i] = ExprNodeCompiler.allocateEvaluator(nodes.get(i).getForge(), engineImportService, ContextControllerHashedGetterHashMultiple.class, false, statementName);
        }
        this.granularity = granularity;
    }

    public Object get(eu.uk.ncl.pet5o.esper.client.EventBean eventBean) throws PropertyAccessException {
        eu.uk.ncl.pet5o.esper.client.EventBean[] events = new eu.uk.ncl.pet5o.esper.client.EventBean[]{eventBean};

        int hashCode = 0;
        for (int i = 0; i < evaluators.length; i++) {
            Object result = evaluators[i].evaluate(events, true, null);
            if (result == null) {
                continue;
            }
            if (hashCode == 0) {
                hashCode = result.hashCode();
            } else {
                hashCode = 31 * hashCode + result.hashCode();
            }
        }

        if (hashCode >= 0) {
            return hashCode % granularity;
        }
        return -hashCode % granularity;
    }

    public boolean isExistsProperty(eu.uk.ncl.pet5o.esper.client.EventBean eventBean) {
        return false;
    }

    public Object getFragment(eu.uk.ncl.pet5o.esper.client.EventBean eventBean) throws PropertyAccessException {
        return null;
    }
}
