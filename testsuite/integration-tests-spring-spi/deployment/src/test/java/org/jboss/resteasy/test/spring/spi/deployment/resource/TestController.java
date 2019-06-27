package org.jboss.resteasy.test.spring.spi.deployment.resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.core.MediaType;

@RestController
@RequestMapping("/" + TestController.TEST_CONTROLLER_PATH)
public class TestController {

    public static final String TEST_CONTROLLER_PATH = "spring";

    @GetMapping
    public String hello(@RequestParam(name = "name") String name) {
        return "hello " + (name == null ? "world" : name);
    }

    @GetMapping(path = "/json/{message}", produces = MediaType.APPLICATION_JSON)
    public SomeClass json(@PathVariable("message") String message) {
        return new SomeClass(message);
    }

    @RequestMapping(path = "/json2/{message}", produces = MediaType.APPLICATION_JSON)
    public SomeClass jsonFromRequestMapping(@PathVariable("message") String message) {
        return new SomeClass(message);
    }

    @PostMapping(path = "/json", produces = MediaType.TEXT_PLAIN, consumes = MediaType.APPLICATION_JSON)
    public String postWithJsonBody(@RequestBody SomeClass someClass) {
        return someClass.getMessage();
    }

    @RequestMapping(path = "/json2", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN, consumes = MediaType.APPLICATION_JSON)
    public String postWithJsonBodyFromRequestMapping(@RequestBody SomeClass someClass) {
        return someClass.getMessage();
    }

    public static class SomeClass {
        private String message;

        public SomeClass() {}

        public SomeClass(final String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
