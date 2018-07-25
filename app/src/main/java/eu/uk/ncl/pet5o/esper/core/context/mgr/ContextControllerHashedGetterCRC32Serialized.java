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
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprForge;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;
import eu.uk.ncl.pet5o.esper.util.Serializer;
import eu.uk.ncl.pet5o.esper.util.SerializerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.zip.CRC32;

public class ContextControllerHashedGetterCRC32Serialized implements EventPropertyGetter {
    private static final Logger log = LoggerFactory.getLogger(ContextControllerHashedGetterCRC32Serialized.class);

    private final String statementName;
    private final ExprEvaluator[] evaluators;
    private final Serializer[] serializers;
    private final int granularity;

    public ContextControllerHashedGetterCRC32Serialized(String statementName, List<ExprNode> nodes, int granularity, EngineImportService engineImportService) {
        this.statementName = statementName;
        evaluators = new ExprEvaluator[nodes.size()];
        Class[] returnTypes = new Class[nodes.size()];
        for (int i = 0; i < nodes.size(); i++) {
            ExprForge forge = nodes.get(i).getForge();
            evaluators[i] = ExprNodeCompiler.allocateEvaluator(forge, engineImportService, ContextControllerHashedGetterCRC32Serialized.class, false, statementName);
            returnTypes[i] = forge.getEvaluationType();
        }
        serializers = SerializerFactory.getSerializers(returnTypes);
        this.granularity = granularity;
    }

    public Object get(eu.uk.ncl.pet5o.esper.client.EventBean eventBean) throws PropertyAccessException {
        eu.uk.ncl.pet5o.esper.client.EventBean[] events = new eu.uk.ncl.pet5o.esper.client.EventBean[]{eventBean};

        Object[] parameters = new Object[evaluators.length];
        for (int i = 0; i < serializers.length; i++) {
            parameters[i] = evaluators[i].evaluate(events, true, null);
        }

        byte[] bytes;
        try {
            bytes = SerializerFactory.serialize(serializers, parameters);
        } catch (IOException e) {
            log.error("Exception serializing parameters for computing consistent hash for statement '" + statementName + "': " + e.getMessage(), e);
            bytes = new byte[0];
        }

        CRC32 crc = new CRC32();
        crc.update(bytes);
        long value = crc.getValue() % granularity;

        int result = (int) value;
        if (result >= 0) {
            return result;
        }
        return -result;
    }

    public boolean isExistsProperty(eu.uk.ncl.pet5o.esper.client.EventBean eventBean) {
        return false;
    }

    public Object getFragment(eu.uk.ncl.pet5o.esper.client.EventBean eventBean) throws PropertyAccessException {
        return null;
    }
}
