package ch.uzh.ifi.attempto.gfservice.gfwebservice;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import ch.uzh.ifi.attempto.gfservice.GfModule;
import ch.uzh.ifi.attempto.gfservice.GfServiceException;
import ch.uzh.ifi.attempto.gfservice.GfStorage;

/**
 * @author Kaarel Kaljurand
 */
public class GfWebStorage implements GfStorage {

	private final URI mUriNew;
	private final URI mUriCloud;

	public GfWebStorage(URI uri) {
		mUriNew = URI.create(uri + "/new");
		mUriCloud = URI.create(uri + "/cloud");
	}


	public String create() throws GfServiceException {
		return HttpUtils.getHttpEntityAsString(new DefaultHttpClient(), new HttpGet(mUriNew));
	}


	public String upload(String dirName, GfModule... modules) throws GfServiceException {
		return push(mUriCloud, "upload", dirName, modules);
	}


	public String make(String dirName, GfModule... modules) throws GfServiceException {
		return push(mUriCloud, "make", dirName, modules);
	}


	private String push(URI uri, String command, String dirName, GfModule... modules) throws GfServiceException {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		for (GfModule module : modules) {
			pairs.add(new BasicNameValuePair(module.getName(), module.getContent()));
		}
		pairs.add(new BasicNameValuePair("dir", dirName));
		pairs.add(new BasicNameValuePair("command", command));
		HttpPost post = HttpUtils.getHttpPost(uri, pairs);
		return HttpUtils.getHttpEntityAsString(new DefaultHttpClient(), post);
	}
}