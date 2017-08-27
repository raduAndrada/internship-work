package ro.axonsoft.internship172.web.controllers;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorController implements org.springframework.boot.autoconfigure.web.ErrorController {

    private static final Logger LOG = LoggerFactory.getLogger(ErrorController.class);

    private static final String PATH = "/error";
    private static final String ERROR_FORM="error";
    private static final String LOGIN="/login";
    private static final String HOME="/candidates";

    @RequestMapping(value = PATH)
    public String error(final ModelMap model, final HttpServletRequest request) {
        if (!model.containsKey("message")) {
            final int errorCode = getHttpStatusCode(request);
            LOG.debug("HTTP error status code {}", errorCode);
            final String message = "error."+ errorCode;

            final String returnURL = request.getHeader("Referer");
            String previousPage = returnURL;
            if (returnURL != null) {
                if (previousPage.contains(LOGIN)) {
                    previousPage = HOME;
                }
            }
            model.put("message", message);
            model.put("previousPage", previousPage);
            model.put("errorCode", String.valueOf(errorCode));
        }

        return ERROR_FORM;
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }

    private int getHttpStatusCode(final HttpServletRequest request) {
        if (request == null) {
            return 0;
        }
        final Object statusCd = request.getAttribute("javax.servlet.error.status_code");
        if (statusCd instanceof Number) {
            return ((Number) statusCd).intValue();
        } else {
            return 0;
        }
    }
}