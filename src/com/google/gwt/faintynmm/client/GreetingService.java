package com.google.gwt.faintynmm.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.server.rpc.XsrfProtect;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
@XsrfProtect
public interface GreetingService extends RemoteService {
	String greetServer(String name) throws IllegalArgumentException;
}
