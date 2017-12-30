package com.analyzer.tool.service.analyzer.modules;

import com.analyzer.tool.service.analyzer.AnalysisAdapter;
import com.analyzer.tool.service.analyzer.ScanResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * The base class for all the modules
 */
public abstract class AbstractBaseModule {


    @Autowired
    public void registerWithAdapter(AnalysisAdapter<String> adapter) {

        adapter.registerModuleHandler(this::analyze);
    }

    abstract List<ScanResult> analyze(String string);

}
