package test.elsufoundation;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;

/**
 * How to send a HTTP or HTTPS request via SOCKS proxy.
 */
public class TinyProxyClient {

    public static void main(String[] args) throws Exception {
        Registry<ConnectionSocketFactory> reg = RegistryBuilder.<ConnectionSocketFactory>create()
            .register("http", new MyHTTPConnectionSocketFactory())
            .register("https", new MyHTTPSConnectionSocketFactory(SSLContexts.createSystemDefault
                ()))
            .build();
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(reg);
        try (CloseableHttpClient httpclient = HttpClients.custom()
            .setConnectionManager(cm)
            .build()) {
            InetSocketAddress socksaddr = new InetSocketAddress("72.215.150.161", 8888);
            HttpClientContext context = HttpClientContext.create();
            context.setAttribute("socks.address", socksaddr);

            HttpHost target = new HttpHost("www.google.com/", 80, "https");
            HttpGet request = new HttpGet("/");

            System.out.println("Executing request " + request + " to " + target + " via SOCKS " +
                "proxy " + socksaddr);
            try (CloseableHttpResponse response = httpclient.execute(target, request, context)) {
                System.out.println("----------------------------------------");
                System.out.println(response.getStatusLine());
                System.out.println(EntityUtils.toString(response.getEntity(), StandardCharsets
                    .UTF_8));
            }
        }
    }

    static class MyHTTPConnectionSocketFactory extends PlainConnectionSocketFactory {
        @Override
        public Socket createSocket(final HttpContext context) throws IOException {
            InetSocketAddress socksaddr = (InetSocketAddress) context.getAttribute("socks.address");
            Proxy proxy = new Proxy(Proxy.Type.SOCKS, socksaddr);
            return new Socket(proxy);
        }
    }

    static class MyHTTPSConnectionSocketFactory extends SSLConnectionSocketFactory {
        public MyHTTPSConnectionSocketFactory(final SSLContext sslContext) {
            super(sslContext);
        }

        @Override
        public Socket createSocket(final HttpContext context) throws IOException {
            InetSocketAddress socksaddr = (InetSocketAddress) context.getAttribute("socks.address");
            Proxy proxy = new Proxy(Proxy.Type.SOCKS, socksaddr);
            return new Socket(proxy);
        }
    }
}