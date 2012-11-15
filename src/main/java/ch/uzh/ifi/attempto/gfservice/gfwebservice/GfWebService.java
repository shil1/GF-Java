package ch.uzh.ifi.attempto.gfservice.gfwebservice;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.simple.parser.ParseException;

import ch.uzh.ifi.attempto.gfservice.Command;
import ch.uzh.ifi.attempto.gfservice.GfService;
import ch.uzh.ifi.attempto.gfservice.GfServiceException;
import ch.uzh.ifi.attempto.gfservice.Param;

/**
 * @author Kaarel Kaljurand
 */
public class GfWebService implements GfService {

	private final URI mUri;
	private String mGrammar;

	public GfWebService(URI uri, String grammar) {
		mUri = uri;
		mGrammar = grammar;
	}


	public GfWebServiceResultGrammar grammar() throws GfServiceException {
		Params p = new Params(Command.GRAMMAR);
		String response = getResponseAsString(p.get());
		try {
			return new GfWebServiceResultGrammar(response);
		} catch (IOException e) {
			throw new GfServiceException(e);
		} catch (ParseException e) {
			throw new GfServiceException(e);
		}
	}


	public GfWebServiceResultParse parse(String cat, String input, String from) throws GfServiceException {
		return parse(cat, input, from, null);
	}


	public GfWebServiceResultParse parse(String cat, String input, String from, Integer limit) throws GfServiceException {
		Params p = new Params(Command.PARSE);
		p.add(Param.CAT, cat);
		p.add(Param.INPUT, input);
		p.add(Param.FROM, from);
		p.add(Param.LIMIT, limit);
		String response = getResponseAsString(p.get());
		try {
			return new GfWebServiceResultParse(response);
		} catch (IOException e) {
			throw new GfServiceException(e);
		} catch (ParseException e) {
			throw new GfServiceException(e);
		}
	}


	public GfWebServiceResultLinearize linearize(String tree, String to) throws GfServiceException {
		if (tree == null) {
			throw new IllegalArgumentException("Tree MUST be given");
		}
		Params p = new Params(Command.LINEARIZE);
		p.add(Param.TREE, tree);
		p.add(Param.TO, to);
		String response = getResponseAsString(p.get());
		try {
			return new GfWebServiceResultLinearize(response);
		} catch (IOException e) {
			throw new GfServiceException(e);
		} catch (ParseException e) {
			throw new GfServiceException(e);
		}
	}


	public GfWebServiceResultTranslate translate(String cat, String input, String from, String to) throws GfServiceException {
		return translate(cat, input, from, to, null);
	}


	public GfWebServiceResultTranslate translate(String cat, String input, String from, String to, Integer limit) throws GfServiceException {
		Params p = new Params(Command.TRANSLATE);
		p.add(Param.CAT, cat);
		p.add(Param.INPUT, input);
		p.add(Param.FROM, from);
		p.add(Param.TO, to);
		p.add(Param.LIMIT, limit);
		String response = getResponseAsString(p.get());
		try {
			return new GfWebServiceResultTranslate(response);
		} catch (IOException e) {
			throw new GfServiceException(e);
		} catch (ParseException e) {
			throw new GfServiceException(e);
		}
	}

	public GfWebServiceResultRandom random(String cat, Integer limit) throws GfServiceException {
		Params p = new Params(Command.RANDOM);
		p.add(Param.CAT, cat);
		p.add(Param.LIMIT, limit);
		String response = getResponseAsString(p.get());
		try {
			return new GfWebServiceResultRandom(response);
		} catch (IOException e) {
			throw new GfServiceException(e);
		} catch (ParseException e) {
			throw new GfServiceException(e);
		}
	}

	public GfWebServiceResultComplete complete(String cat, String input, String from, Integer limit) throws GfServiceException {
		Params p = new Params(Command.COMPLETE);
		p.add(Param.CAT, cat);
		p.add(Param.INPUT, input);
		p.add(Param.FROM, from);
		p.add(Param.LIMIT, limit);
		String response = getResponseAsString(p.get());
		try {
			return new GfWebServiceResultComplete(response);
		} catch (IOException e) {
			throw new GfServiceException(e);
		} catch (ParseException e) {
			throw new GfServiceException(e);
		}
	}


	public GfWebServiceResultAbstrtree abstrtree(String tree) throws GfServiceException {
		byte[] response = getDiagram(tree, Command.ABSTRTREE);
		return new GfWebServiceResultAbstrtree(response);
	}


	public GfWebServiceResultParsetree parsetree(String tree, String from) throws GfServiceException {
		if (tree == null) {
			throw new IllegalArgumentException("Tree MUST be given");
		}
		if (from == null) {
			throw new IllegalArgumentException("Source language MUST be given");
		}
		Params p = new Params(Command.PARSETREE);
		p.add(Param.TREE, tree);
		p.add(Param.FROM, from);
		return new GfWebServiceResultParsetree(getResponseAsBytes(p.get()));
	}


	public GfWebServiceResultAlignment alignment(String tree) throws GfServiceException {
		byte[] response = getDiagram(tree, Command.ALIGNMENT);
		return new GfWebServiceResultAlignment(response);
	}


	public GfWebServiceResultBrowse browse(String id) throws GfServiceException {
		Params p = new Params(Command.BROWSE);
		p.add(Param.ID, id);
		p.add(Param.FORMAT, "json");
		String response = getResponseAsString(p.get());
		try {
			return new GfWebServiceResultBrowse(response);
		} catch (IOException e) {
			throw new GfServiceException(e);
		} catch (ParseException e) {
			throw new GfServiceException(e);
		}
	}


	private String getResponseAsString(List<NameValuePair> nvps) throws GfServiceException {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpUriRequest request = getHttpUriRequest(nvps);
		return HttpUtils.getHttpEntityAsString(httpClient, request);
	}


	private byte[] getResponseAsBytes(List<NameValuePair> nvps) throws GfServiceException {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpUriRequest request = getHttpUriRequest(nvps);
		return HttpUtils.getHttpEntityAsByteArray(httpClient, request);
	}


	/**
	 * We create an HTTP GET query from the given parameters. If it turns out to be
	 * too long (which we expect to happen very infrequently) then we fall back to creating
	 * HTTP POST.
	 * 
	 * @param nvps Collection of name-value pairs
	 * @return HTTP request (either GET or POST)
	 */
	private HttpUriRequest getHttpUriRequest(List<NameValuePair> nvps) {
		String getQuery = URI.create(mUri + mGrammar) + "?" + URLEncodedUtils.format(nvps, HTTP.UTF_8);
		if (getQuery.length() > HttpUtils.MAX_HTTP_GET_LENGTH) {
			HttpPost httppost = new HttpPost(mUri); // TODO: test if it shouldn't be instead: HttpPost(mUri + mGrammar)
			try {
				httppost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
			} catch (UnsupportedEncodingException e) {
				// TODO: Assuming that this cannot happen
			}
			return httppost;
		}
		return new HttpGet(getQuery);
	}


	private byte[] getDiagram(String tree, Command command) throws GfServiceException {
		if (tree == null) {
			throw new IllegalArgumentException("Tree MUST be given");
		}
		Params p = new Params(command);
		p.add(Param.TREE, tree);
		return getResponseAsBytes(p.get());
	}
}