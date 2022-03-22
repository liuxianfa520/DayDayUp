package com.anxiaole.easyopen.utils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class ScriptEngineUtil {

    private ScriptEngineUtil() {
    }

    private static final ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("groovy");

    public static ScriptEngine getScriptEngine() {
        return scriptEngine;
    }
}
