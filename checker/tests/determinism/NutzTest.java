// @skip-test

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.checkerframework.checker.determinism.qual.NonDet;

public class NutzTest {

    public void test_weibo_post() {
        Response resp = Http.post2("http://weibo.com/kuyunhudong", null, 10000);
        System.out.println(resp.getStatus());
        System.out.println(resp.getContent());
        System.out.println(resp.getStatus());
    }
}

class Http {
    public static @NonDet Response post2(String url, Map<String, Object> params, int timeout) {
        return Sender.create(Request.create(url, Request.METHOD.POST, params, null))
                .setTimeout(timeout)
                .send();
    }
}

class Request {
    public static enum METHOD {
        GET,
        POST,
        OPTIONS,
        PUT,
        DELETE,
        TRACE,
        CONNECT,
        HEAD
    }

    private METHOD method;
    private Map<String, Object> params;
    private String url;
    private Header header;

    public static Request create(String url, METHOD method, Map<String, Object> params) {
        return Request.create(url, method, params, Header.create());
    }

    public static Request create(
            String url, METHOD method, Map<String, Object> params, Header header) {
        return new Request().setMethod(method).setParams(params).setUrl(url).setHeader(header);
    }

    public Request setMethod(METHOD method) {
        this.method = method;
        return this;
    }

    public Request setParams(Map<String, Object> params) {
        this.params = params;
        return this;
    }

    public Request setUrl(String url) {
        if (url != null && !url.contains("://")) this.url = "http://" + url;
        else this.url = url;
        return this;
    }

    public Request setHeader(Header header) {
        if (header == null) header = new Header();
        this.header = header;
        return this;
    }

    public static Request get(String url) {
        return create(url, METHOD.GET, new HashMap<String, Object>());
    }

    public boolean isGet() {
        return METHOD.GET == method;
    }

    public boolean isPost() {
        return METHOD.POST == method;
    }

    public boolean isDelete() {
        return METHOD.DELETE == method;
    }

    public boolean isPut() {
        return METHOD.PUT == method;
    }

    public Map<String, Object> getParams() {
        return params;
    }
}

class Header {
    protected Map<String, Object> items;

    protected Header() {
        items = new LinkedHashMap<String, Object>();
    }

    public static Header create() {
        Header header = new Header();
        return header;
    }

    public String get(String key) {
        Object value = items.get(key);
        if (value == null) return null;
        if (value instanceof List) {
            if (((List) value).isEmpty()) return null;
            return (String) ((List) value).get(0);
        }
        return (String) value;
    }
}

class Strings {
    public static boolean isBlank(CharSequence cs) {
        if (null == cs) return true;
        int length = cs.length();
        for (int i = 0; i < length; i++) {
            if (!(Character.isWhitespace(cs.charAt(i)))) return false;
        }
        return true;
    }

    public static String trim(CharSequence cs) {
        if (null == cs) return null;
        int length = cs.length();
        if (length == 0) return cs.toString();
        int l = 0;
        int last = length - 1;
        int r = last;
        for (; l < length; l++) {
            if (!Character.isWhitespace(cs.charAt(l))) break;
        }
        for (; r > l; r--) {
            if (!Character.isWhitespace(cs.charAt(r))) break;
        }
        if (l > r) return "";
        else if (l == 0 && r == last) return cs.toString();
        return cs.subSequence(l, r + 1).toString();
    }
}

class Streams {
    public static String readAndClose(Reader reader) {
        return read(reader).toString();
    }

    public static StringBuilder read(Reader reader) {
        StringBuilder sb = new StringBuilder();
        return sb;
    }
}

class Encoding {
    public static String defaultEncoding() {
        return "xyz";
    }
}

class Response {
    private int status;
    private String content;
    private Header header;
    private InputStream stream;

    public int getStatus() {
        return status;
    }

    public String getContent() {
        if (Strings.isBlank(content)) {
            content = getContent(null);
        }
        return content;
    }

    public String getContent(String charsetName) {
        if (charsetName == null) return Streams.readAndClose(getReader());
        return Streams.readAndClose(getReader(charsetName));
    }

    public Reader getReader() {
        String encoding = this.getEncodeType();
        if (null == encoding) return getReader(Encoding.defaultEncoding());
        else return getReader(encoding);
    }

    public Reader getReader(String charsetName) {
        return new InputStreamReader(getStream(), Charset.forName(charsetName));
    }

    public String getEncodeType() {
        String contextType = header.get("Content-Type");
        if (null != contextType) {
            for (String tmp : contextType.split(";")) {
                if (tmp == null) continue;
                tmp = tmp.trim();
                if (tmp.startsWith("charset=")) return Strings.trim(tmp.substring(8)).trim();
            }
        }
        return null;
    }

    public InputStream getStream() {
        return new BufferedInputStream(stream);
    }
}

abstract class Sender {
    private int timeout;
    protected Request request;

    public static Sender create(Request request) {
        if (request.isGet() || request.isDelete()) return new GetSender(request);
        if ((request.isPost() || request.isPut()) && request.getParams() != null) {
            for (Object val : request.getParams().values()) {
                if (val instanceof File || val instanceof File[]) {
                    return new FilePostSender(request);
                }
            }
        }
        return new PostSender(request);
    }

    public static Sender create(String url) {
        return create(Request.get(url));
    }

    public Sender setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    protected Map<String, String> getResponseHeader() {
        Map<String, String> reHeaders = new HashMap<String, String>();
        return reHeaders;
    }

    protected Response createResponse(Map<String, String> reHeaders) {
        Response rep = null;
        return rep;
    }

    public abstract Response send();
}

class GetSender extends Sender {
    public GetSender(Request request) {}

    public Response send() {
        return createResponse(getResponseHeader());
    }
}

class PostSender extends Sender {

    public PostSender() {}

    public PostSender(Request request) {}

    public Response send() {
        return createResponse(getResponseHeader());
    }
}

class FilePostSender extends PostSender {
    public FilePostSender(Request request) {}
}
