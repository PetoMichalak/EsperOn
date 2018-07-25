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
package eu.uk.ncl.pet5o.esper.event.bean;

import eu.uk.ncl.pet5o.esper.collection.Pair;
import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineImportService;
import eu.uk.ncl.pet5o.esper.epl.expression.core.*;

import net.sf.cglib.reflect.FastConstructor;

public class InstanceManufacturerFactoryFactory {
    public static InstanceManufacturerFactory getManufacturer(Class targetClass, EngineImportService engineImportService, ExprNode[] childNodes)
            throws ExprValidationException {
        ExprForge[] forgesUnmodified = ExprNodeUtilityCore.getForges(childNodes);
        Object[] returnTypes = new Object[forgesUnmodified.length];
        for (int i = 0; i < forgesUnmodified.length; i++) {
            returnTypes[i] = forgesUnmodified[i].getEvaluationType();
        }

        Pair<FastConstructor, ExprForge[]> ctor = InstanceManufacturerUtil.getManufacturer(targetClass, engineImportService, forgesUnmodified, returnTypes);
        return new InstanceManufacturerFactoryFastCtor(targetClass, ctor.getFirst(), ctor.getSecond());
    }
}
