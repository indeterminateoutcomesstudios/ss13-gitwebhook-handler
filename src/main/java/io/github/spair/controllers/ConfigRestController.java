package io.github.spair.controllers;

import io.github.spair.entities.HandlerConfig;
import io.github.spair.entities.HandlerConfigStatus;
import io.github.spair.services.ConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/config/rest")
public class ConfigRestController {

    @Autowired
    private ConfigService configService;
    @Autowired
    private Environment env;

    private static final Logger logger = LoggerFactory.getLogger(ConfigRestController.class);

    @GetMapping("/current")
    public HandlerConfig getCurrentConfig() {
        return configService.getConfig();
    }

    @PutMapping("/current")
    public void saveConfig(@RequestBody HandlerConfig configuration) throws IOException {
        configService.saveNewConfig(configuration);
    }

    @GetMapping("/file")
    public FileSystemResource downloadConfigFile(HttpServletResponse response) {
        logger.info("Configuration file downloaded");

        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + ConfigService.CONFIG_NAME);

        return new FileSystemResource(configService.downloadConfigFile());
    }

    @GetMapping("/log")
    public FileSystemResource downloadLogFile(HttpServletResponse response) {
        logger.info("Log file downloaded");

        String logName = env.getProperty("logging.file");

        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + logName);

        return new FileSystemResource(new File(logName));

    }

    @PostMapping("/validation")
    public ResponseEntity validateConfig(@RequestBody HandlerConfig config) {
        HandlerConfigStatus configStatus = configService.validateConfig(config);
        HttpStatus responseStatus = configStatus.allOk ? HttpStatus.OK : HttpStatus.NOT_ACCEPTABLE;

        return new ResponseEntity<>(configStatus, responseStatus);
    }
}
