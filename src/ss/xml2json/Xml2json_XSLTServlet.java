package ss.xml2json;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

@SuppressWarnings("serial")
public class Xml2json_XSLTServlet extends HttpServlet {

	public static String prettifyJSON(String uglyJSON, boolean useJSONP) {
		// use json-lib
		String toJSON = uglyJSON;
		String prefix = "";
		String postfix = "";
		if (useJSONP) { // apply hack to trim jsonp; after apply jsonp back again
			int open_bracket = uglyJSON.indexOf('(');
			int close_bracket = uglyJSON.lastIndexOf(')');
			toJSON = uglyJSON.substring(open_bracket+1, close_bracket);
			prefix = uglyJSON.substring(0, open_bracket+1);
			postfix = ")";
		}
		StringBuilder bob = new StringBuilder();
		bob.append(prefix);
		bob.append('\n');
		bob.append(((JSONObject)JSONSerializer.toJSON(toJSON)).toString(2));
		bob.append('\n');
		bob.append(postfix);
        return bob.toString();
	};
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/plain"); // actually need application/json
		
		String outputFormat = req.getParameter("output");
		String sourceURL = req.getParameter("url");
		Boolean useNamespaces = Boolean.valueOf(req.getParameter("use-namespaces"));
		Boolean skipRoot = Boolean.valueOf(req.getParameter("skip-root"));
		String jsonp = req.getParameter("jsonp");
		boolean prettyJSON = Boolean.valueOf(req.getParameter("pretty-json")).booleanValue();

		BufferedReader source = null;
        try {
            URL url = new URL(sourceURL);
            source = new BufferedReader(new InputStreamReader(url.openStream()));
        } catch (MalformedURLException e) {
        	throw e;
        } catch (IOException e) {
        	throw e;
        }
		
		Transformer t = null;
		try {
			TransformerFactory stf = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl",null);
			InputStream xsl = getServletContext().getResourceAsStream("/generate.xsl");
			Templates templates = stf.newTemplates(new StreamSource(xsl));
			xsl.close();
			t = templates.newTransformer();
		} catch (TransformerConfigurationException e) {
			throw new RuntimeException(e);
		}
		
		StringWriter output = new StringWriter();
		try {
			t.setParameter(outputFormat, Boolean.TRUE);
			t.setParameter("use-namespaces", useNamespaces);
			t.setParameter("skip-root", skipRoot);
			t.setParameter("jsonp", jsonp);
			t.transform(new StreamSource(source), new StreamResult(output));
		} catch (TransformerException e) {
			throw new RuntimeException(e);
		}

		if (prettyJSON) {
			resp.getWriter().print(prettifyJSON(output.toString(),jsonp != null && jsonp.length() > 0));
		} else {
			resp.getWriter().print(output.toString());
		}

		source.close();
		output.close();
	}
}
