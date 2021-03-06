/*
 * Copyright DbMaintain.org
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
package org.dbmaintain.script;

import org.dbmaintain.config.DbMaintainConfigurationLoader;
import org.dbmaintain.script.executedscriptinfo.ScriptIndexes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Properties;

import static java.util.Arrays.asList;
import static org.dbmaintain.config.DbMaintainProperties.PROPERTY_SCRIPT_INDEX_REGEXP;
import static org.dbmaintain.config.DbMaintainProperties.PROPERTY_SCRIPT_QUALIFIER_REGEXP;
import static org.dbmaintain.config.DbMaintainProperties.PROPERTY_SCRIPT_TARGETDATABASE_REGEXP;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
class ScriptFactoryVersionIndexTest {

    private ScriptFactory scriptFactory;

    @BeforeEach
    void initialize() {
        Properties configuration = new DbMaintainConfigurationLoader().loadDefaultConfiguration();
        String scriptIndexRegexp = configuration.getProperty(PROPERTY_SCRIPT_INDEX_REGEXP);
        String targetDatabaseRegexp = configuration.getProperty(PROPERTY_SCRIPT_TARGETDATABASE_REGEXP);
        String qualifierRegexp = configuration.getProperty(PROPERTY_SCRIPT_QUALIFIER_REGEXP);

        scriptFactory = new ScriptFactory(scriptIndexRegexp, targetDatabaseRegexp, qualifierRegexp, new HashSet<>(),
                new HashSet<>(), null, null, null);
    }


    @Test
    void singleIndex() {
        Script script = scriptFactory.createScriptWithoutContent("01_my_script.sql", null, null);
        assertScriptIndexes(script, 1L);
    }

    @Test
    void multipleIndexes() {
        Script script = scriptFactory.createScriptWithoutContent("01_scripts/2_release/003_my_script.sql", null, null);
        assertScriptIndexes(script, 1L, 2L, 3L);
    }

    @Test
    void pathWithoutIndex() {
        Script script = scriptFactory.createScriptWithoutContent("scripts/release/003_my_script.sql", null, null);
        assertScriptIndexes(script, null, null, 3L);
    }

    @Test
    void leadingIndex() {
        Script script = scriptFactory.createScriptWithoutContent("1_my_script.sql", null, null);
        assertScriptIndexes(script, 1L);
    }

    @Test
    void onlyIndex() {
        Script script = scriptFactory.createScriptWithoutContent("1.sql", null, null);
        assertScriptIndexes(script, 1L);
    }

    @Test
    void noIndexes() {
        Script script = scriptFactory.createScriptWithoutContent("scripts/my_script.sql", null, null);
        assertTrue(script.getQualifiers().isEmpty());
    }

    @Test
    void noIndexesOnlyFileName() {
        Script script = scriptFactory.createScriptWithoutContent("my_script.sql", null, null);
        assertTrue(script.getQualifiers().isEmpty());
    }

    @Test
    void invalidIndexIsIgnored() {
        Script script = scriptFactory.createScriptWithoutContent("0xxx1_script.sql", null, null);
        assertTrue(script.getQualifiers().isEmpty());
    }


    private void assertScriptIndexes(Script script, Long... indexes) {
        ScriptIndexes scriptIndexes = script.getScriptIndexes();
        assertEquals(asList(indexes), scriptIndexes.getIndexes());
    }
}
