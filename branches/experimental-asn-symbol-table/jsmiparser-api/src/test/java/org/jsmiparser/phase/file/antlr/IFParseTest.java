/*
 * Copyright 2005 Davy Verstappen.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jsmiparser.phase.file.antlr;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import junit.framework.TestCase;
import org.apache.log4j.Logger;
import org.jsmiparser.parsetree.asn1.*;
import org.jsmiparser.parsetree.smi.SMITextualConventionMacro;
import org.jsmiparser.phase.file.FileParserPhase;
import org.jsmiparser.util.problem.DefaultProblemEventHandler;
import org.jsmiparser.util.problem.DefaultProblemReporterFactory;

import java.io.BufferedInputStream;
import java.io.InputStream;

/**
 * @author davy
 */
public class IFParseTest extends TestCase {

    private static final Logger m_log = Logger.getLogger(IFParseTest.class);

    /**
     *
     */
    public IFParseTest() {
        super();
    }

    public void testModule_definition() throws RecognitionException, TokenStreamException {

        InputStream is = new BufferedInputStream(this.getClass().getResourceAsStream("/IF-MIB"));

        SMILexer lexer = new SMILexer(is);
        SMIParser parser = new SMIParser(lexer);
        FileParserPhase fileParserPhase = new FileParserPhase(new DefaultProblemReporterFactory(new DefaultProblemEventHandler()),
                AntlrFileParser.class);
        AntlrFileParser fileParser = new AntlrFileParser(fileParserPhase, null);
        parser.init("/IF-MIB", fileParser);

        ASNModule module = parser.module_definition();

        assertEquals("IF-MIB", module.getModuleName());
        assertEquals("IF-MIB", module.getName());
        assertEquals(1, module.getLocation().getLine());

//		for (ASNAssignment a : module.getAssignments())
//		{
//			System.out.println(a.getLine() + ": " + a.getName());
//		}

        ASNAssignment a = module.findAssignment("ifCompliance");
        assertNotNull(a);
        assertEquals(1741, a.getLocation().getLine());

        if (m_log.isDebugEnabled()) {
            for (ASNAssignment ta : module.getAssignments()) {
                m_log.debug(ta.getModule().getIdToken() + " " + ta.getIdToken());
            }
        }
        assertEquals(99, module.getAssignments().size());

        ASNTypeAssignment ta = module.findTypeAssignment("InterfaceIndexOrZero");
        assertNotNull(ta);
        ASNType entityType = ta.getEntityType();
        assertNotNull(entityType);
        assertEquals(ASNType.Enum.SMITEXTUALCONVENTIONMACRO, entityType.getType());

        ASNTypeAssignment iiAssignment = module.findTypeAssignment("InterfaceIndex");
        SMITextualConventionMacro iiTextualConvention = (SMITextualConventionMacro) iiAssignment.getEntityType();
        ASNDefinedType iiSyntax = (ASNDefinedType) iiTextualConvention.getSyntax();
        assertEquals("Integer32", iiSyntax.getTypeAssignment().getName());
        assertEquals("SNMPv2-SMI", iiSyntax.getTypeAssignment().getModule().getName());

//		PrintWriter out = new PrintWriter(System.out);
//		module.print(out);
//		assertFalse(out.checkError());
    }
}
