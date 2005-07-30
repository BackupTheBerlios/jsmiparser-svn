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

import org.jsmiparser.parsetree.asn1.ASNModule;

import java.io.File;

public abstract class AbstractFileParser implements FileParser {

    protected FileParserPhase m_fileParserPhase;
    protected File m_file;
    protected State m_state = State.UNPARSED;
    protected ASNModule m_module;

    protected AbstractFileParser(FileParserPhase fileParserPhase, File file) {
        m_fileParserPhase = fileParserPhase;
        m_file = file;
    }

    public FileParserPhase getFileParserPhase() {
        return m_fileParserPhase;
    }

    public File getFile() {
        return m_file;
    }

    public State getState() {
        return m_state;
    }

    public ASNModule getModule() {
        return m_module;
    }
}
