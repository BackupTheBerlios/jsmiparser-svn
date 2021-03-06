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
package org.jsmiparser.phase.quality;

import org.apache.log4j.Logger;
import org.jsmiparser.phase.AbstractPhase;
import org.jsmiparser.phase.PhaseException;
import org.jsmiparser.smi.SmiMib;
import org.jsmiparser.util.problem.ProblemReporterFactory;

public class MibQualityCheckerPhase extends AbstractPhase {
    private static final Logger m_log = Logger.getLogger(MibQualityCheckerPhase.class);

    public MibQualityCheckerPhase(ProblemReporterFactory problemReporterFactory) {
        super(problemReporterFactory);
    }

    public SmiMib process(Object input) throws PhaseException {
        SmiMib mib = (SmiMib) input;
        // TODO
        return mib;
    }
}
