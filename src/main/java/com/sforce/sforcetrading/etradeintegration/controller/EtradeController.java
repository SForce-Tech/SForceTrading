package com.sforce.sforcetrading.etradeintegration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.bind.annotation.*;

import com.sforce.sforcetrading.etradeintegration.config.OOauthConfig;
import com.sforce.sforcetrading.etradeintegration.config.SandBoxConfig;

@RestController
@RequestMapping("/api/etrade")
public class EtradeController {

    @Autowired
    private AnnotationConfigApplicationContext ctx;

    @PostMapping("/init")
    public void initializeEtrade(@RequestParam boolean isLive) {
        if (ctx != null) {
            ctx.close();
        }
        ctx = new AnnotationConfigApplicationContext();
        if (isLive) {
            ctx.register(OOauthConfig.class);
        } else {
            ctx.register(SandBoxConfig.class);
        }
        ctx.refresh();
    }
}
