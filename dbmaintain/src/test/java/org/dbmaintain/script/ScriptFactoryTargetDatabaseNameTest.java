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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.dbmaintain.config.DbMaintainProperties.PROPERTY_SCRIPT_INDEX_REGEXP;
import static org.dbmaintain.config.DbMaintainProperties.PROPERTY_SCRIPT_QUALIFIER_REGEXP;
import static org.dbmaintain.config.DbMaintainProperties.PROPERTY_SCRIPT_TARGETDATABASE_REGEXP;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
class ScriptFactoryTargetDatabaseNameTest {

    /* Tested object */
    private ScriptFactory scriptFactory;

    @BeforeEach
    void initialize() {
        Properties configuration = new DbMaintainConfigurationLoader().loadDefaultConfiguration();
        String scriptIndexRegexp = configuration.getProperty(PROPERTY_SCRIPT_INDEX_REGEXP);
        String targetDatabaseRegexp = configuration.getProperty(PROPERTY_SCRIPT_TARGETDATABASE_REGEXP);
        String qualifierRegexp = configuration.getProperty(PROPERTY_SCRIPT_QUALIFIER_REGEXP);

        scriptFactory = new ScriptFactory(scriptIndexRegexp, targetDatabaseRegexp, qualifierRegexp, null, null, null, null, null);
    }


    @Test
    void singleTargetDatabaseName() {
        Script script = scriptFactory.createScriptWithoutContent("scripts/01_@database_my_script.sql", null, null);
        assertEquals("database", script.getTargetDatabaseName());
    }

    @Test
    void useLastWhenMultipleTargetDatabaseNames() {
        Script script = scriptFactory.createScriptWithoutContent("@db1_scripts/01_@db2_my_script.sql", null, null);
        assertEquals("db2", script.getTargetDatabaseName());
    }

    @Test
    void useLastWhenMultipleTargetDatabaseNamesInFileName() {
        Script script = scriptFactory.createScriptWithoutContent("01_@db1_@db2_my_script.sql", null, null);
        assertEquals("db2", script.getTargetDatabaseName());
    }

    @Test
    void leadingTargetDatabaseName() {
        Script script = scriptFactory.createScriptWithoutContent("@database_my_script.sql", null, null);
        assertEquals("database", script.getTargetDatabaseName());
    }

    @Test
    void onlyTargetDatabaseName() {
        Script script = scriptFactory.createScriptWithoutContent("@database.sql", null, null);
        assertEquals("database", script.getTargetDatabaseName());
    }

    @Test
    void noTargetDatabaseNames() {
        Script script = scriptFactory.createScriptWithoutContent("scripts/my_script.sql", null, null);
        assertNull(script.getTargetDatabaseName());
    }
}
