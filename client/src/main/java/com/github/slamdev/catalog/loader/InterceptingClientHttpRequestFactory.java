package com.github.slamdev.catalog.loader;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.*;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

public class InterceptingClientHttpRequestFactory extends AbstractClientHttpRequestFactoryWrapper {

    public InterceptingClientHttpRequestFactory(ClientHttpRequestFactory requestFactory) {
        super(requestFactory);
    }

    @Override
    protected ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod, ClientHttpRequestFactory requestFactory) {
        return new InterceptingClientHttpRequest(requestFactory, uri, httpMethod);
    }

    private static class InterceptingClientHttpRequest extends AbstractClientHttpRequest {

        private final ClientHttpRequestFactory requestFactory;

        private final HttpMethod method;

        private final URI uri;

        private ByteArrayOutputStream bufferedOutput = new ByteArrayOutputStream(1024);

        InterceptingClientHttpRequest(ClientHttpRequestFactory requestFactory, URI uri, HttpMethod method) {
            this.requestFactory = requestFactory;
            this.method = method;
            this.uri = uri;
        }

        @Override
        public HttpMethod getMethod() {
            return method;
        }

        @Override
        public URI getURI() {
            return uri;
        }

        @Override
        protected OutputStream getBodyInternal(HttpHeaders headers) throws IOException {
            return bufferedOutput;
        }

        @Override
        protected ClientHttpResponse executeInternal(HttpHeaders headers) throws IOException {
            byte[] bytes = bufferedOutput.toByteArray();
            if (headers.getContentLength() < 0) {
                headers.setContentLength(bytes.length);
            }
            ClientHttpResponse result = executeInternal(bytes);
            bufferedOutput = null;
            return result;
        }

        private ClientHttpResponse executeInternal(byte[] body) throws IOException {
            return e1xecute(getURI(), getMethod(), (uri, method) -> {
                ClientHttpRequest delegate = requestFactory.createRequest(getURI(), getMethod());
                delegate.getHeaders().putAll(getHeaders());
                if (body.length > 0) {
                    StreamUtils.copy(body, delegate.getBody());
                }
                return delegate.execute();
            });
        }

        private ClientHttpResponse e1xecute(URI uri, HttpMethod method, RequestExecutor callable) throws IOException {
            return callable.get(uri, method);
        }

        private interface RequestExecutor {

            ClientHttpResponse get(URI uri, HttpMethod method) throws IOException;
        }

    }
}
