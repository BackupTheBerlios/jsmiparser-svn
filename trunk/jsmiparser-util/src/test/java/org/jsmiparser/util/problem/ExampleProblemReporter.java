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
package org.jsmiparser.util.problem;

import org.jsmiparser.util.problem.annotations.ProblemInterface;
import org.jsmiparser.util.problem.annotations.ProblemMethod;
import org.jsmiparser.util.problem.annotations.ProblemProperty;
import org.jsmiparser.util.location.Location;

import java.util.List;

@ProblemInterface
public interface ExampleProblemReporter {

    @ProblemMethod(message = "List size = %d")
    public void reportListSize(@ProblemProperty("size") List<?> l);

    @ProblemMethod(message = "Simple message")
    void simpleMessage();

    @ProblemMethod(message = "Simple location message")
    void simpleLocation(Location location);
}
