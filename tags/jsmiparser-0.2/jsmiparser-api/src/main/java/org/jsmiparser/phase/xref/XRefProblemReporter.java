package org.jsmiparser.phase.xref;

import org.jsmiparser.util.problem.annotations.ProblemMethod;
import org.jsmiparser.util.token.IdToken;
import org.jsmiparser.smi.SmiSymbol;

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
public interface XRefProblemReporter {

    @ProblemMethod(message = "Cannot find module %s")
    void reportCannotFindModule(IdToken moduleToken);

    @ProblemMethod(message = "Cannot find imported symbol %s in module %s")
    void reportCannotFindImportedSymbol(IdToken idToken, IdToken moduleToken);

    @ProblemMethod(message = "Cannot find symbol %s")
    void reportCannotFindSymbol(IdToken idToken);

    @ProblemMethod(message = "Found symbol %s but expected a %s instead of %s")
    void reportFoundSymbolButWrongType(IdToken idToken, Class<? extends SmiSymbol> expectedClass, Class<? extends SmiSymbol> actualClass);
}
