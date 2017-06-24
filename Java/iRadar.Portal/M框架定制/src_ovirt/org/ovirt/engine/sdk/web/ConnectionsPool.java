package org.ovirt.engine.sdk.web;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;
import javax.net.ssl.SSLException;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;

public class ConnectionsPool
{
  private static int MAX_RETRY_REQUEST = 5;

  private DefaultHttpClient client = null;
  private CookieStore cookieStore;
  private IdleConnectionMonitorThread idleConnectionsWatchdog;
  private URL url = null;
  private Integer sessionTimeout;

  public ConnectionsPool(DefaultHttpClient client, URL url, Integer sessionTimeout, long checkTtL, long closeTtl)
  {
    this.client = client;
    this.cookieStore = this.client.getCookieStore();
    this.url = url;
    this.sessionTimeout = sessionTimeout;
    injectHttpRequestRetryHandler(this.client);
//    this.idleConnectionsWatchdog = 
//      new IdleConnectionMonitorThread(this.client
//      .getConnectionManager(), checkTtL, closeTtl);
//
//    this.idleConnectionsWatchdog.start();
  }

  public HttpResponse execute(HttpUriRequest request, HttpContext context)
    throws IOException, ClientProtocolException
  {
    return this.client.execute(request, context);
  }

  public List<Cookie> getCookies()
  {
    return this.cookieStore.getCookies();
  }

  public CookieStore getCookieStore()
  {
    return this.cookieStore;
  }

  public ClientConnectionManager getConnectionManager()
  {
    return this.client.getConnectionManager();
  }

  private void injectHttpRequestRetryHandler(DefaultHttpClient httpclient) {
    HttpRequestRetryHandler myRetryHandler = new HttpRequestRetryHandler()
    {
      public boolean retryRequest(IOException exception, int executionCount, HttpContext context)
      {
        if (executionCount >= ConnectionsPool.MAX_RETRY_REQUEST)
        {
          return false;
        }
        if ((exception instanceof InterruptedIOException))
        {
          return false;
        }
        if ((exception instanceof UnknownHostException))
        {
          return false;
        }
        if ((exception instanceof ConnectException))
        {
          return false;
        }
        if ((exception instanceof SSLException))
        {
          return false;
        }
        HttpRequest request = (HttpRequest)context.getAttribute("http.request");

        boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);

        return idempotent;
      }
    };
    httpclient.setHttpRequestRetryHandler(myRetryHandler);
  }

  public URL getUrl()
  {
    return this.url;
  }

  public Integer getSessionTimeout()
  {
    return this.sessionTimeout;
  }

  public void shutdown()
  {
//    this.idleConnectionsWatchdog.shutdown();
    getConnectionManager().shutdown();
  }
}