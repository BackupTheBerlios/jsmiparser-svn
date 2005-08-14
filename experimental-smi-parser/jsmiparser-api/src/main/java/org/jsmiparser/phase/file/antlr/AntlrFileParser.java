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
package org.jsmiparser.phase.file.antlr;

import antlr.RecognitionException;
import antlr.Token;
import antlr.TokenStreamException;
import org.apache.log4j.Logger;
import org.jsmiparser.phase.PhaseException;
import org.jsmiparser.phase.file.AbstractFileParser;
import org.jsmiparser.phase.file.FileParserPhase;
import org.jsmiparser.phase.file.SkipStandardException;
import org.jsmiparser.phase.file.FileParser;
import org.jsmiparser.util.location.Location;
import org.jsmiparser.util.token.IdToken;
import org.jsmiparser.smi.SmiModule;
import org.jsmiparser.smi.SmiImports;
import org.jsmiparser.smi.SmiSymbol;

import java.io.*;
import java.util.List;
import java.util.ArrayList;

public class AntlrFileParser extends AbstractFileParser {

    private static final Logger m_log = Logger.getLogger(AntlrFileParser.class);

    public AntlrFileParser(FileParserPhase fileParserPhase, File file) {
        super(fileParserPhase, file);
    }

    public SmiModule parse() {
        assert(m_state == State.UNPARSED);

        InputStream is = null;
        try {
            m_state = State.PARSING;
            m_log.debug("Parsing :" + m_file);
            is = new BufferedInputStream(new FileInputStream(m_file));
            SMILexer lexer = new SMILexer(is);
            SMIParser parser = new SMIParser(lexer);
            parser.init(this);

            m_module = parser.module_definition();
            return m_module;
        } catch (SkipStandardException e) {
            // do nothing
            return m_module;
        } catch (FileNotFoundException e) {
            m_log.error(e.getClass().getSimpleName(), e);
            throw new PhaseException(e);
        } catch (RecognitionException e) {
            m_log.error(e.getClass().getSimpleName(), e);
            throw new PhaseException(e);
        } catch (TokenStreamException e) {
            m_log.error(e.getClass().getSimpleName(), e);
            throw new PhaseException(e);
        } catch (IOException e) {
            m_log.error(e.getClass().getSimpleName(), e);
            throw new PhaseException(e);
        } finally {
            m_state = State.PARSED;
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    throw new PhaseException(e);
                }
            }
        }
    }


    private Location makeLocation(Token token) {
        return new Location(m_file != null ? m_file.getPath() : "unknown", token.getLine(), token.getColumn());
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

    public List<IdToken> makeIdTokenList() {
        return new ArrayList<IdToken>();
    }

    public void addImports(IdToken moduleToken, List<IdToken> importedTokenList) {
        // TODO m_module.addImports(moduleToken, importedTokenList);

        // TODO check module imported twice

        FileParser importedFileParser = getFileParserPhase().use(moduleToken);
        SmiImports result = new SmiImports(m_module, moduleToken, importedFileParser.getModule());
        for (IdToken idToken : importedTokenList) {
            SmiSymbol symbol = importedFileParser.use(idToken);

            // TODO check duplicate
            result.addSymbol(idToken, symbol);
        }


    }

    public SmiModule makeModule(Token idToken) {
        return createModule(idt(idToken));
    }
}
