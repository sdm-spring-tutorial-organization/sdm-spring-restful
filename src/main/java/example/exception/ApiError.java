package example.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.soap.Detail;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ApiError implements Serializable {

    private static final long serialVersionUID = -8119817744873562082L;

    private String message;
    @JsonProperty("documentation_url")
    private String documentationUrl;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final List<Detail> details = new ArrayList<>();

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDocumentationUrl() {
        return documentationUrl;
    }

    public void setDocumentationUrl(String documentationUrl) {
        this.documentationUrl = documentationUrl;
    }

    public List<Detail> getDetails() {
        return details;
    }

    public void addDetail(String target, String message) {
        details.add(new Detail(target, message));
    }

    public static class Detail implements Serializable {

        private static final long serialVersionUID = -8119817744873562082L;
        private final String target;
        private final String message;

        private Detail(String target, String message) {
            this.target = target;
            this.message = message;
        }

        public String getTarget() {
            return target;
        }

        public String getMessage() {
            return message;
        }
    }
}
