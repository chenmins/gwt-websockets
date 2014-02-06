package org.realityforge.gwt.websockets.client.html5;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.realityforge.gwt.websockets.client.WebSocket;

public class Html5WebSocket
  extends WebSocket
{
  public static native boolean isSupported() /*-{
    return !!window.WebSocket;
  }-*/;

  private WebSocketImpl _webSocket;

  public static class Factory
    implements WebSocket.Factory
  {
    @Override
    public WebSocket newWebSocket()
    {
      return new Html5WebSocket( new SimpleEventBus() );
    }
  }

  public Html5WebSocket( final EventBus eventBus )
  {
    super( eventBus );
  }

  @Override
  public void close()
  {
    checkConnected();
    _webSocket.close();
    _webSocket = null;
  }

  @Override
  public void close( final short code, @Nullable final String reason )
  {
    checkConnected();
    _webSocket.close( code, reason );
    _webSocket = null;
  }

  @Override
  public void connect( @Nonnull final String server, @Nonnull final String... protocols )
  {
    if ( null != _webSocket )
    {
      throw new IllegalStateException( "WebSocket already connected" );
    }
    _webSocket = WebSocketImpl.create( this, server );
  }

  @Override
  public final int getBufferedAmount()
  {
    checkConnected();
    return _webSocket.getBufferedAmount();
  }

  @Override
  public String getProtocol()
  {
    return _webSocket.getProtocol();
  }

  @Override
  public final void send( @Nonnull String data )
  {
    checkConnected();
    _webSocket.send( data );
  }

  @Override
  public final ReadyState getReadyState()
  {
    checkConnected();
    return ReadyState.values()[_webSocket.getReadyState()];
  }

  private void checkConnected()
  {
    if ( null == _webSocket )
    {
      throw new IllegalStateException( "WebSocket not connected" );
    }
  }

  private final static class WebSocketImpl
    extends JavaScriptObject
  {
    static native WebSocketImpl create( WebSocket client, String server, String... protocols )
    /*-{
      var ws = new WebSocket( server, protocols );
      ws.onopen = $entry( function ()
                          {
                            client.@org.realityforge.gwt.websockets.client.WebSocket::onOpen()();
                          } );
      ws.onerror = $entry( function ()
                             {
                               client.@org.realityforge.gwt.websockets.client.WebSocket::onError()();
                             } );
      ws.onmessage = $entry( function ( response )
                             {
                               client.@org.realityforge.gwt.websockets.client.WebSocket::onMessage(Ljava/lang/String;)( response.data );
                             } );
      ws.onclose = $entry( function ( event )
                           {
                             client.@org.realityforge.gwt.websockets.client.WebSocket::onClose(ZILjava/lang/String;)(event.wasClean, event.code, event.reason);
                           } );
      return ws;
    }-*/;

    protected WebSocketImpl()
    {
    }

	native int getBufferedAmount() /*-{
		return this.bufferedAmount;
	}-*/;

	native int getReadyState() /*-{
		return this.readyState;
	}-*/;

    native void close() /*-{
      this.close();
    }-*/;

    native void close(int code, String reason) /*-{
        this.close(code, reason);
    }-*/;

    native void send( String data ) /*-{
      this.send( data );
    }-*/;

    native String getProtocol()  /*-{
        return this.protocol;
    }-*/;
  }
}
