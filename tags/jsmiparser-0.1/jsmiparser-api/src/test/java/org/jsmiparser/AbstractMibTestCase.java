/*
 * Copyright 2006 Davy Verstappen.
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
package org.jsmiparser;

import junit.framework.TestCase;
import org.jsmiparser.parser.SmiDefaultParser;
import org.jsmiparser.phase.file.FileParserOptions;
import org.jsmiparser.smi.SmiConstants;
import org.jsmiparser.smi.SmiMib;
import org.jsmiparser.smi.SmiModule;
import org.jsmiparser.smi.SmiOidValue;
import org.jsmiparser.smi.SmiPrimitiveType;
import org.jsmiparser.smi.SmiSymbol;
import org.jsmiparser.smi.SmiType;
import org.jsmiparser.smi.SmiVersion;
import org.jsmiparser.util.problem.DefaultProblemEventHandler;
import org.jsmiparser.util.problem.ProblemEventHandler;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

public abstract class AbstractMibTestCase extends TestCase {

    private final SmiVersion m_version;
    private final String[] m_resources;

    private SmiMib m_mib;

    private SmiType m_integer32;
    private SmiType m_counter;


    protected AbstractMibTestCase(SmiVersion version, String... resources) {
        m_version = version;
        m_resources = resources;
    }

    protected SmiMib getMib() {
        if (m_mib == null) {
            // this isn't entirely clean; but it will be better with JUnit 4 @BeforeClass
            parseMib();
        }
        return m_mib;
    }

    private void parseMib() {
        SmiDefaultParser parser = new SmiDefaultParser();
        FileParserOptions options = (FileParserOptions) parser.getFileParserPhase().getOptions();

        URL mibsURL = getClass().getClassLoader().getResource("libsmi-0.4.5/mibs/ietf");
        File mibsDir = null;
        try {
            mibsDir = new File(mibsURL.toURI());
        } catch (URISyntaxException e) {
            fail(e.getMessage());
        }

        if (m_version == null || m_version == SmiVersion.V1) {
            options.addFile(new File(mibsDir, "RFC1155-SMI"));
        }
        if (m_version == null || m_version == SmiVersion.V2) {
            options.addFile(new File(mibsDir, "SNMPv2-SMI"));
            options.addFile(new File(mibsDir, "SNMPv2-TC"));
            options.addFile(new File(mibsDir, "SNMPv2-CONF"));
            options.addFile(new File(mibsDir, "SNMPv2-MIB"));
        }

        for (String resource : getResources()) {
            URL mibURL = getClass().getClassLoader().getResource(resource);
            try {
                File mibFile = new File(mibURL.toURI());
                options.addFile(mibFile);
            } catch (URISyntaxException e) {
                fail(e.getMessage());
            }
        }
        m_mib = parser.parse();

    }

    public final String[] getResources() {
        return m_resources;
    }

    public SmiType getInteger32() {
        if (m_integer32 == null) {
            m_integer32 = getMib().findType("Integer32");
            assertSame(SmiConstants.INTEGER_TYPE, m_integer32.getBaseType());
            assertSame(SmiPrimitiveType.INTEGER_32, m_integer32.getPrimitiveType());
            assertEquals(1, m_integer32.getRangeConstraints().size());
            assertEquals(-2147483648, m_integer32.getRangeConstraints().get(0).getMinValue().intValue());
            assertEquals(2147483647, m_integer32.getRangeConstraints().get(0).getMaxValue().intValue());
            assertNull(m_integer32.getSizeConstraints());
            assertNull(m_integer32.getEnumValues());
            assertNull(m_integer32.getBitFields());
            assertNull(m_integer32.getFields());
        }
        return m_integer32;
    }

    public SmiType getCounter() {
        if (m_counter == null) {
            m_counter = getMib().findType("Counter");
            assertSame(SmiConstants.INTEGER_TYPE, m_counter.getBaseType());
            assertSame(SmiPrimitiveType.COUNTER_32, m_counter.getPrimitiveType());
            assertEquals(1, m_counter.getRangeConstraints().size());
            assertEquals(0, m_counter.getRangeConstraints().get(0).getMinValue().intValue());
            assertEquals(0xFFFFFFFFL, m_counter.getRangeConstraints().get(0).getMaxValue().longValue());
            assertNull(m_counter.getSizeConstraints());
            assertNull(m_counter.getEnumValues());
            assertNull(m_counter.getBitFields());
            assertNull(m_counter.getFields());
        }
        return m_counter;
    }

    protected void showOverview() {
        for (SmiModule module : m_mib.getModules()) {
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

    protected SmiOidValue findOidSymbol(SmiOidValue rootNode, String id) {
        for (SmiOidValue child : rootNode.getChildren()) {
            if (child.getId().equals(id)) {
                return child;
            }
            SmiOidValue result = findOidSymbol(child, id);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    protected void checkOidTree(SmiMib mib) {
        //mib.getRootNode().dumpTree(System.out, "");

        int count = 0;
        for (SmiSymbol symbol : mib.getSymbols()) {
            if (symbol instanceof SmiOidValue) {
                SmiOidValue oidValue = (SmiOidValue) symbol;
                //oidValue.dumpAncestors(System.out);
                if (oidValue != mib.getRootNode()) {
                    String msg = oidValue.getIdToken().toString();
                    assertNotNull(msg, oidValue.getParent());

                    SmiOidValue foundOidValue = oidValue.getParent().findChild(oidValue.getLastOid());
                    assertNotNull(msg, foundOidValue);
                    assertSame(msg, oidValue, foundOidValue);
                    assertTrue(msg, oidValue.getParent().contains(oidValue));
                }
//                SmiOidValue foundSymbol = findOidSymbol(mib.getRootNode(), symbol.getId());
//                assertNotNull(symbol.getId(), foundSymbol);
//                assertSame(symbol.getId(), symbol, foundSymbol);
                count++;
            }
        }

        //mib.getRootNode().dumpTree(System.out, "");
        int totalChildCount = mib.getRootNode().getTotalChildCount();
        //assertTrue(count + " < " +  totalChildCount, count < totalChildCount);
        //System.out.println("totalChildCount: " + totalChildCount);
        assertEquals(count + mib.getDummyOidNodesCount(), totalChildCount);
    }
}
