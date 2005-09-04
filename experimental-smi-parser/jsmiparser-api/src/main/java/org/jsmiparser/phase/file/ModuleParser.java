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
import org.jsmiparser.parsetree.asn1.ASNAssignment;
import org.jsmiparser.phase.PhaseException;
import org.jsmiparser.phase.file.antlr.SMIParser;
import org.jsmiparser.phase.lexer.LexerModule;
import org.jsmiparser.phase.oid.OidNode;
import org.jsmiparser.smi.*;
import org.jsmiparser.util.location.Location;
import org.jsmiparser.util.symbol.IdSymbolImpl;
import org.jsmiparser.util.token.*;

import java.util.ArrayList;
import java.util.List;

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

    private SmiSymbolMap<SmiType> m_typeMap;
    private SmiSymbolMap<SmiValue> m_valueMap;
    private SmiSymbolMap<SmiMacro> m_macroMap;

    public ModuleParser(FileParserPhase parserPhase, LexerModule lexerModule) {
        super(lexerModule.getId());
        m_parserPhase = parserPhase;
        m_lexerModule = lexerModule;
    }

    private void init(SmiModule module) {
        assert(m_module == null);
        m_module = module;
        m_typeMap = new SmiSymbolMap<SmiType>(m_parserPhase.getFileParserProblemReporter(), m_module, SmiType.class);
        m_valueMap = new SmiSymbolMap<SmiValue>(m_parserPhase.getFileParserProblemReporter(), m_module, SmiValue.class);
        m_macroMap = new SmiSymbolMap<SmiMacro>(m_parserPhase.getFileParserProblemReporter(), m_module, SmiMacro.class);
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

    public SmiSymbolMap<SmiType> getTypeMap() {
        return m_typeMap;
    }

    public SmiSymbolMap<SmiValue> getValueMap() {
        return m_valueMap;
    }

    public SmiSymbolMap<SmiMacro> getMacroMap() {
        return m_macroMap;
    }

    public SmiSymbol create(IdToken idToken) {
        SmiSymbol result = null;
        switch (ASNAssignment.determineType(idToken)) {
            case MACRODEF:
                result = m_macroMap.create(idToken);
                break;
            case TYPE:
                result = m_typeMap.create(idToken);
                break;
            case VALUE:
                result = m_valueMap.create(idToken);
                break;
        }
        return result;
    }

    /**
     * Called by other module parsers to imports symbols from this module
     *
     * @param idToken
     * @return
     */
    public SmiSymbol use(IdToken idToken) {
        if (m_typeMap == null) {
            m_log.debug(idToken + " used from " + m_module.getIdToken());
        }
        SmiSymbol result = null;
        switch (ASNAssignment.determineType(idToken)) {
            case MACRODEF:
                result = m_macroMap.use(idToken);
                break;
            case TYPE:
                result = m_typeMap.use(idToken);
                break;
            case VALUE:
                result = m_valueMap.use(idToken);
                break;
        }
        return result;
    }

    public SmiType useType(IdToken idToken) {
        return m_typeMap.use(idToken);
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
            return m_module;
        } catch (RecognitionException e) {
            m_log.error(e.getClass().getSimpleName(), e);
            throw new PhaseException(e);
        } catch (TokenStreamException e) {
            m_log.error(e.getClass().getSimpleName(), e);
            throw new PhaseException(e);
        } finally {
            m_state = State.PARSED;
        }
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

        ModuleParser importedModuleParser = getParserPhase().use(moduleToken);
        SmiImports result = new SmiImports(m_module, moduleToken, importedModuleParser.getModule());
        for (IdToken idToken : importedTokenList) {
            SmiSymbol symbol = importedModuleParser.use(idToken);

            // TODO check duplicate
            result.addSymbol(idToken, symbol);
        }
    }

    public SmiModule makeModule(Token idToken) {
        return createModule(idt(idToken));
    }

    public OidNode resolveOidComponent(OidNode parent, Token it, Token nt) {
        OidNode result = null;

        IdToken idToken = (it == null ? null : idt(it));
        BigIntegerToken valueToken = (nt == null ? null : bintt(nt));

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

    public SmiOidValue createOidValue(IdToken idToken, OidNode oc) {
        SmiOidValue result = new SmiOidValue(idToken, m_module);
        result.setOidComponent(oc);
        return result;
    }

    public SmiOidMacro createOidMacro(IdToken idToken) {
        return new SmiOidMacro(idToken, m_module);
    }

    public SmiAttribute createVariable(IdToken idToken, SmiType t) {
        SmiAttribute result = new SmiAttribute(idToken, m_module);
        result.setType(t);
        return result;
    }

    public SmiRow createRow(IdToken idToken, SmiType t) {
        SmiRow result = new SmiRow(idToken, m_module);
        result.setType(t);
        return result;
    }

    public SmiTable createTable(IdToken idToken, SmiType t) {
        SmiTable result = new SmiTable(idToken, m_module);
        result.setType(t);
        return result;
    }

    public SmiTextualConvention createTextualConvention(IdToken idToken) {
        return new SmiTextualConvention(idToken, m_module);
    }

    public SmiType createSequenceType() {
        return new SmiType(null, m_module);
    }

    public SmiType getDefinedType(Token moduleToken, Token typeToken) {
        ModuleParser mp = this;
        if (moduleToken != null) {
            mp = getParserPhase().use(idt(moduleToken));
        }
        return mp.useType(idt(typeToken));
    }

    public SmiType createType(SmiType baseType) {
        SmiType result = new SmiType(null, m_module);
        result.setBaseType(baseType);
        return result;
    }

    public SmiAttribute useColumn(Token idToken) {
        return m_valueMap.use(idt(idToken), SmiAttribute.class);
    }

    public SmiRow useRow(IdToken idToken) {
        return m_valueMap.use(idToken, SmiRow.class);    
    }

    public void addField(SmiType sequenceType, SmiAttribute col, SmiType fieldType) {
        sequenceType.addField(col, fieldType);
    }

    public SmiType createSequenceOfType(Token elementTypeNameToken) {
        SmiType elementType = m_typeMap.use(idt(elementTypeNameToken));
        SmiType sequenceOfType = new SmiType(null, m_module);
        sequenceOfType.setElementType(elementType);
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

}

