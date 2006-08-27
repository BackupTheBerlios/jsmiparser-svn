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
package org.jsmiparser.phase.file;

import antlr.RecognitionException;
import antlr.Token;
import antlr.TokenStreamException;
import org.apache.log4j.Logger;
import org.jsmiparser.phase.PhaseException;
import org.jsmiparser.phase.file.antlr.SMIParser;
import org.jsmiparser.phase.lexer.LexerModule;
import org.jsmiparser.phase.oid.OidNode;
import org.jsmiparser.smi.OidComponent;
import org.jsmiparser.smi.SmiAttribute;
import org.jsmiparser.smi.SmiConstants;
import org.jsmiparser.smi.SmiImports;
import org.jsmiparser.smi.SmiMacro;
import org.jsmiparser.smi.SmiModule;
import org.jsmiparser.smi.SmiNamedNumber;
import org.jsmiparser.smi.SmiOidMacro;
import org.jsmiparser.smi.SmiOidValue;
import org.jsmiparser.smi.SmiPrimitiveType;
import org.jsmiparser.smi.SmiProtocolType;
import org.jsmiparser.smi.SmiRange;
import org.jsmiparser.smi.SmiReferencedType;
import org.jsmiparser.smi.SmiRow;
import org.jsmiparser.smi.SmiSymbol;
import org.jsmiparser.smi.SmiTable;
import org.jsmiparser.smi.SmiTextualConvention;
import org.jsmiparser.smi.SmiType;
import org.jsmiparser.smi.StatusV2;
import org.jsmiparser.util.location.Location;
import org.jsmiparser.util.symbol.IdSymbolImpl;
import org.jsmiparser.util.token.BigIntegerToken;
import org.jsmiparser.util.token.BinaryStringToken;
import org.jsmiparser.util.token.HexStringToken;
import org.jsmiparser.util.token.IdToken;
import org.jsmiparser.util.token.IntegerToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ModuleParser extends IdSymbolImpl {

    private static final Logger m_log = Logger.getLogger(ModuleParser.class);


    enum State {
        UNPARSED,
        PARSING,
        PARSED
    }

    protected FileParserPhase m_parserPhase;
    private LexerModule m_lexerModule;
    private State m_state = State.UNPARSED;
    private SmiModule m_module;

    public ModuleParser(FileParserPhase parserPhase, LexerModule lexerModule) {
        super(lexerModule.getId());
        m_parserPhase = parserPhase;
        m_lexerModule = lexerModule;
    }

    private void init(SmiModule module) {
        assert(m_module == null);
        m_module = module;
    }

    public State getState() {
        return m_state;
    }

    public FileParserPhase getParserPhase() {
        return m_parserPhase;
    }

    public LexerModule getLexerModule() {
        return m_lexerModule;
    }

    public SmiModule getModule() {
        return m_module;
    }

    public SmiModule useModule(IdToken idToken) {
        if (m_module == null) {
            init(new SmiModule(m_parserPhase.getMib(), idToken));
        }
        return m_module;
    }

    public SmiModule createModule(IdToken idToken) {
        if (m_module == null) {
            init(new SmiModule(m_parserPhase.getMib(), idToken));
        }
        return m_module;
    }

    public SmiModule parse() {
        assert(m_state == State.UNPARSED);

        try {
            m_state = State.PARSING;
            m_log.debug("Parsing :" + m_lexerModule.getSource());
            if (m_lexerModule.getSource() == null) {
                init(new SmiModule(m_parserPhase.getMib(),
                        new IdToken(new Location("nowhere"), m_lexerModule.getId())));
            } else {
                SMIParser parser = new SMIParser(m_lexerModule.createTokenStream());
                parser.init(this);

                m_module = parser.module_definition();
            }
            return m_module;
        } catch (SkipStandardException e) {
            init(m_parserPhase.getMib().findModule(e.getMessage()));
            assert(m_module != null);
            if (m_module.getId().equals("RFC1155-SMI")) {
                initRFC1155_SMI();
            }

            return m_module;
        } catch (RecognitionException e) {
            m_log.error(e.getClass().getSimpleName(), e);
            throw new PhaseException(getId(), e);
        } catch (TokenStreamException e) {
            m_log.error(e.getClass().getSimpleName(), e);
            throw new PhaseException(getId(), e);
        } finally {
            m_state = State.PARSED;
            m_lexerModule.clear(); // release memory early
            m_log.debug("Finished parsing :" + m_lexerModule.getSource());
        }
    }

    private void initRFC1155_SMI() {
        // internet      OBJECT IDENTIFIER ::= { iso org(3) dod(6) 1 }
        OidNode isoNode = resolveHardCodedOidComponent(null, "iso", -1);
        OidNode orgNode = resolveHardCodedOidComponent(isoNode, "org", 3);
        OidNode dodNode = resolveHardCodedOidComponent(orgNode, "dod", 6);
        OidNode internetNode = resolveHardCodedOidComponent(dodNode, null, 1);
        registerHardCodedOid("internet", internetNode);

        //  directory     OBJECT IDENTIFIER ::= { internet 1 }
        internetNode = resolveHardCodedOidComponent(null, "internet", -1);
        OidNode directoryNode = resolveHardCodedOidComponent(internetNode, null, 1);
        registerHardCodedOid("directory", directoryNode);

        //  mgmt          OBJECT IDENTIFIER ::= { internet 2 }
        //internetNode = resolveHardCodedOidComponent(null, "internet", -1);
        OidNode mgmtNode = resolveHardCodedOidComponent(internetNode, null, 2);
        registerHardCodedOid("mgmt", mgmtNode);

        // experimental  OBJECT IDENTIFIER ::= { internet 3 }
        //internetNode = resolveHardCodedOidComponent(null, "internet", -1);
        OidNode experimentalNode = resolveHardCodedOidComponent(internetNode, null, 3);
        registerHardCodedOid("experimental", experimentalNode);

        // private       OBJECT IDENTIFIER ::= { internet 4 }
        //internetNode = resolveHardCodedOidComponent(null, "internet", -1);
        OidNode privateNode = resolveHardCodedOidComponent(internetNode, null, 4);
        registerHardCodedOid("private", privateNode);

        // enterprises   OBJECT IDENTIFIER ::= { private 1 }
        privateNode = resolveHardCodedOidComponent(null, "private", -1);
        OidNode enterprisesNode = resolveHardCodedOidComponent(privateNode, null, 1);
        registerHardCodedOid("enterprises", enterprisesNode);

    }


    private Location makeLocation(Token token) {
        return new Location(m_lexerModule.getSource(), token.getLine(), token.getColumn());
    }

    public IdToken idt(Token idToken) {
        return new IdToken(makeLocation(idToken), idToken.getText());
    }

    public IdToken idt(Token... tokens) {
        for (Token token : tokens) {
            if (token != null) {
                return idt(token);
            }
        }
        return null;
    }

    public IntKeywordToken intkt(Token idToken, SmiPrimitiveType primitiveType) {
        return new IntKeywordToken(makeLocation(idToken), idToken.getText(), primitiveType);
    }

    public String getCStr(Token t) {
        String text = t.getText();
        if (!(text.startsWith("\"") && text.endsWith("\""))) {
            throw new IllegalArgumentException(t.getText());
        }
        return text.substring(1, text.length() - 1);
    }

    public String getOptCStr(Token t) {
        if (t != null) {
            return getCStr(t);
        }
        return null;
    }

    public IntegerToken intt(Token t) {
        int value = Integer.parseInt(t.getText());
        return new IntegerToken(makeLocation(t), value);
    }

    public BigIntegerToken bintt(Token t) {
        return new BigIntegerToken(makeLocation(t), false, t.getText());
    }

    public BigIntegerToken bintt(Token minusToken, Token t) {
        return new BigIntegerToken(makeLocation(t), minusToken != null, t.getText());
    }

    public List<IdToken> makeIdTokenList() {
        return new ArrayList<IdToken>();
    }

    public void addImports(IdToken moduleToken, List<IdToken> importedTokenList) {
        // TODO m_module.addImports(moduleToken, importedTokenList);

        // TODO check module imported twice

        Set<String> macroNames = new HashSet<String>(Arrays.asList(SmiConstants.MACRO_NAMES));

        ModuleParser importedModuleParser = getParserPhase().use(moduleToken);
        // TODO this isn't true when there are cyclic deps: assert(importedModuleParser.getState() == State.PARSED);
        SmiImports result = new SmiImports(moduleToken, importedTokenList);
        result.setModule(importedModuleParser.getModule());
/*
        for (IdToken idToken : importedTokenList) {
            //SmiSymbol symbol = importedModuleParser.use(idToken);
            SmiSymbol symbol = importedModuleParser.getModule().findSymbol(idToken.getId());
            if (symbol == null) {
                if (!macroNames.contains(idToken.getId())) {
                    System.out.println("couldn't resolve " + idToken + " from " + importedModuleParser.getModule().getId());
                }
            }
            // TODO check duplicate
            result.addSymbol(idToken, symbol);
        }
*/
        m_module.getImports().add(result);
    }

    public SmiModule makeModule(Token idToken) {
        return createModule(idt(idToken));
    }

    private OidNode resolveHardCodedOidComponent(OidNode parent, String name, int value) {
        IdToken idToken = new IdToken(null, name);
        BigIntegerToken valueToken = value >= 0 ? new BigIntegerToken(value) : null;

        return resolveOidComponent(parent, idToken, valueToken);
    }

    public OidNode resolveOidComponent(OidNode parent, Token it, Token nt) {
        IdToken idToken = (it == null ? null : idt(it));
        BigIntegerToken valueToken = (nt == null ? null : bintt(nt));
        return resolveOidComponent(parent, idToken, valueToken);
    }

    private OidNode resolveOidComponent(OidNode parent, IdToken idToken, BigIntegerToken valueToken) {
        OidNode result;
        if (parent == null) {
            result = getParserPhase().getOidMgr().resolveStart(idToken, valueToken);
        } else {
            result = parent.resolveChild(idToken, valueToken);
        }

        return result;
    }

    public OidNode registerOid(IdToken idToken, OidNode oc) {
        return getParserPhase().getOidMgr().registerNode(idToken, oc);
    }

    private void registerHardCodedOid(String id, OidNode internetNode) {
        registerOid(new IdToken(null, id), internetNode);
    }

    public void addOidComponent(List<OidComponent> ocs, Token id, Token value) {
        IdToken idToken = id != null ? idt(id) : null;
        BigIntegerToken valueToken = value != null ? bintt(value) : null;
        ocs.add(new OidComponent(idToken, valueToken));
    }

    public SmiOidValue createOidValue(IdToken idToken, List<OidComponent> oidComponents) {
        SmiOidValue result = new SmiOidValue(idToken, m_module);
        result.setOidComponents(oidComponents);
        return result;
    }

    public SmiMacro createMacro(IdToken idToken) {
        return new SmiMacro(idToken, m_module);
    }

    public SmiOidMacro createOidMacro(IdToken idToken) {
        return new SmiOidMacro(idToken, m_module);
    }

    public SmiAttribute createVariable(IdToken idToken, SmiType t) {
        final String methodWithParams = "createVariable(" + idToken.getId() + ")";
        m_log.debug(methodWithParams);

        SmiAttribute result = new SmiAttribute(idToken, m_module);
        //result = m_valueMap.create()
        result.setType(t);
        return result;
    }

    public SmiRow createRow(IdToken idToken, SmiType t) {
        final String methodWithParams = "createRow(" + idToken.getId() + ")";
        m_log.debug(methodWithParams);

        SmiRow result = new SmiRow(idToken, m_module);
        result.setType(t);
        return result;
    }

    public SmiTable createTable(IdToken idToken, SmiType t) {
        final String methodWithParams = "createTable(" + idToken.getId() + ")";
        m_log.debug(methodWithParams);

        SmiTable result = new SmiTable(idToken, m_module);
        result.setType(t);
        return result;
    }

    public SmiTextualConvention createTextualConvention(IdToken idToken, Token displayHint, StatusV2 status, Token description, Token reference, SmiType type) {
        SmiTextualConvention result = new SmiTextualConvention(idToken, m_module, getOptCStr(displayHint), status, getCStr(description), getOptCStr(reference));

        if (type.getBaseType() == null) {
            result.setBaseType(type);
        } else {
            result.setBaseType(type.getBaseType());
        }
        result.setPrimitiveType(type.getPrimitiveType());
        result.setEnumValues(type.getEnumValues());
        result.setBitFields(type.getBitFields());
        result.setRangeConstraints(type.getRangeConstraints());
        result.setSizeConstraints(type.getSizeConstraints());
        result.setPrimitiveType(type.getPrimitiveType());

        return result;
    }

    public SmiType createSequenceType(IdToken idToken) {
        return new SmiType(idToken, m_module);
    }

    public SmiType createType(IdToken idToken, SmiType baseType) {
        SmiType result;
        if (baseType == null) {
            throw new IllegalArgumentException();
        }
        if (idToken == null) {
            result = baseType;
        } else {
            result = new SmiType(idToken, m_module);
            result.setBaseType(baseType);
        }
        return result;
    }

    // TODO investigate idea: instead of using the hardcoded SmiConstants here, use SmiReferencedType for everything,
    // and resolve references to INTEGER, BITS, ... during the XRef phase
    public SmiType createIntegerType(IdToken idToken, IntKeywordToken intToken, List<SmiNamedNumber> namedNumbers, List<SmiRange> rangeConstraints) {
        if (idToken == null && intToken.getPrimitiveType() == SmiPrimitiveType.INTEGER && namedNumbers == null && rangeConstraints == null)
        {
            return SmiConstants.INTEGER_TYPE;
        } else if (idToken != null || namedNumbers != null || rangeConstraints != null) {
            SmiType type = new SmiType(idToken, m_module);
            if (idToken instanceof IntKeywordToken) {
                IntKeywordToken intKeywordToken = (IntKeywordToken) idToken;
                type.setPrimitiveType(intKeywordToken.getPrimitiveType());
            } else {
                type.setPrimitiveType(intToken.getPrimitiveType());
            }
            if (intToken.getPrimitiveType() == SmiPrimitiveType.INTEGER) {
                type.setBaseType(SmiConstants.INTEGER_TYPE);
            } else {
                type.setBaseType(new SmiReferencedType(intToken, m_module));
            }
            type.setEnumValues(namedNumbers);
            type.setRangeConstraints(rangeConstraints);
            return type;
        }
        return new SmiReferencedType(intToken, m_module);
    }

    public SmiType createBitsType(IdToken idToken, List<SmiNamedNumber> namedNumbers) {
        if (idToken != null || namedNumbers != null) {
            SmiType type = new SmiType(idToken, m_module);
            type.setPrimitiveType(SmiPrimitiveType.BITS);
            type.setBaseType(SmiConstants.BITS_TYPE);
            type.setBitFields(namedNumbers);
            return type;
        }
        return SmiConstants.BITS_TYPE;
    }

    public SmiType createOctetStringType(IdToken idToken, List<SmiRange> sizeConstraints) {
        if (idToken != null || sizeConstraints != null) {
            SmiType type = new SmiType(idToken, m_module);
            type.setPrimitiveType(SmiPrimitiveType.OCTET_STRING);
            type.setBaseType(SmiConstants.OCTET_STRING_TYPE);
            type.setSizeConstraints(sizeConstraints);
            return type;
        }
        return SmiConstants.OCTET_STRING_TYPE;

    }

    public SmiType createDefinedType(Token moduleToken, Token idToken,
                                     List<SmiNamedNumber> namedNumbers,
                                     List<SmiRange> sizeConstraints,
                                     List<SmiRange> rangeConstraints) {
        SmiReferencedType type = new SmiReferencedType(idt(idToken), m_module);
        if (moduleToken != null) {
            type.setReferencedModuleToken(idt(moduleToken));
        }
        type.setNamedNumbers(namedNumbers);
        type.setSizeConstraints(sizeConstraints);
        type.setRangeConstraints(rangeConstraints);
        return type;
    }


    public SmiProtocolType createProtocolType(IdToken idToken) {
        return new SmiProtocolType(idToken, m_module);
    }

    public void addField(SmiType sequenceType, Token col, SmiType fieldType) {
        sequenceType.addField(idt(col), fieldType);
    }

    public SmiType createSequenceOfType(Token elementTypeNameToken) {
        SmiType sequenceOfType = new SmiType(null, m_module);
        sequenceOfType.setElementTypeToken(idt(elementTypeNameToken));
        return sequenceOfType;
    }

    public void addRange(List<SmiRange> rc, org.jsmiparser.util.token.Token rv1, org.jsmiparser.util.token.Token rv2) {
        SmiRange range = null;
        if (rv2 != null) {
            range = new SmiRange(rv1, rv2);
        } else {
            range = new SmiRange(rv1);
        }
        rc.add(range);
    }

    public BinaryStringToken bst(Token bt) {
        return new BinaryStringToken(makeLocation(bt), bt.getText());
    }

    public HexStringToken hst(Token nt) {
        return new HexStringToken(makeLocation(nt), nt.getText());
    }

    public void addSymbol(SmiSymbol symbol) {
        if (symbol != null) {
            m_module.addSymbol(symbol);
        }
    }

}

