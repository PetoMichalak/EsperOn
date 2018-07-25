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
package eu.uk.ncl.pet5o.esper.pattern.observer;

import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNamedParameterNode;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;

import java.util.List;

public class ObserverParameterUtil {
    public static void validateNoNamedParameters(String name, List<ExprNode> parameter) throws ObserverParameterException {
        for (ExprNode node : parameter) {
            if (node instanceof ExprNamedParameterNode) {
                throw new ObserverParameterException(name + " does not allow named parameters");
            }
        }
    }
}
