/*
 * Copyright 2005 Davy Verstappen.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jsmiparser.parser;

import junit.framework.TestCase;
import org.jsmiparser.phase.PhaseException;
import org.jsmiparser.phase.file.FileParserOptions;
import org.jsmiparser.smi.SmiMib;
import org.jsmiparser.smi.SmiModule;
import org.jsmiparser.smi.SmiOidValue;
import org.jsmiparser.smi.SmiSymbol;
import org.jsmiparser.util.problem.DefaultProblemEventHandler;
import org.jsmiparser.util.problem.ProblemEventHandler;

import java.io.File;
import java.net.URL;
import java.net.URISyntaxException;

public class SmiDefaultParserTest extends TestCase {

    public void testLibSmi() throws URISyntaxException {
        URL mibsURL = getClass().getClassLoader().getResource("libsmi-0.4.5/mibs");
        File mibsDir = new File(mibsURL.toURI());

        ProblemEventHandler problemEventHandler = new DefaultProblemEventHandler();
        SmiDefaultParser parser = new SmiDefaultParser(problemEventHandler);
        parser.init();
        FileParserOptions options = (FileParserOptions) parser.getLexerPhase().getOptions();
        initFileParserOptions(options, mibsDir, "iana", "ietf", "site", "tubs");

        SmiMib mib = parser.parse();
        assertNotNull(mib);
        //showOverview(mib);

        //XStream xStream = new XStream();
        //xStream.toXML(mib, System.out);

        // TODO assertEquals(216, mib.getModules().size());

        // TODO assertEquals(1522, mib.getTypes().size());

        // TODO: none of the node conversions are implemented yet
        assertEquals(0, mib.getTables().size());

        /*
        SmiType ifIndexType = mib.findType("InterfaceIndex");
        assertNotNull(ifIndexType);
        assertEquals(79, ifIndexType.getLocation().getLine());
        assertEquals(1, ifIndexType.getLocation().getColumn());
        //TODO assertEquals(SmiVarBindField.INTEGER_VALUE, ifIndexType.getVarBindField());
    */

        //SmiMib mib2 = parser.parse();
        //assertEquals(mib1, mib2);

        /*
        int errorCount = problemEventHandler.getSeverityCount(ProblemSeverity.ERROR);

        MibMerger mibMerger = new MibMerger(problemReporterFactory);
        SmiMib mib3 = mibMerger.merge(mib1, mib2);
        assertEquals(errorCount, problemEventHandler.getErrorCount());
        */

    }

    private void showOverview(SmiMib mib) {
        for (SmiModule module : mib.getModules()) {
            for (SmiSymbol symbol : module.getSymbols()) {
                String msg = module.getId() + ": " + symbol.getId() + ": " + symbol.getClass().getSimpleName();
                if (symbol instanceof SmiOidValue) {
                    SmiOidValue oidValue = (SmiOidValue) symbol;
                    msg += ": " + oidValue.getOid();
                }
                System.out.println(msg);
            }
        }
    }

    private void initFileParserOptions(FileParserOptions options, File mibsDir, String... subDirNames) {
        for (String subDirName : subDirNames) {
            File dir = new File(mibsDir, subDirName);
            assertTrue(dir.toString(), dir.exists());
            assertTrue(dir.toString(), dir.isDirectory());

            options.getUsedDirList().add(dir);
            File[] files = dir.listFiles();
            for (int i = 0; i < files.length; i++) {
                File file = files[i];

                if (file.isFile()
                        && !file.getName().equals("RFC1158-MIB") // obsoleted by RFC-1213
                        && !file.getName().contains("TOTAL")
                        && !file.getName().endsWith("tree")
                        && !file.getName().startsWith("Makefile")
                        && !file.getName().endsWith("~")) {
                    //&& !file.getName().endsWith("-orig")) { // TODO parsing -orig should give more errors!
                    options.addFile(file);
                }
            }
        }
    }

}
