package ss.xml2json;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSON;
import net.sf.json.xml.XMLSerializer;

@SuppressWarnings("serial")
public class Xml2json_JSONlibServlet extends HttpServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/plain"); // actually need application/json

		String sourceURL = req.getParameter("url");
		boolean prettyJSON = Boolean.valueOf(req.getParameter("pretty-json")).booleanValue();
		Boolean useNamespaces = Boolean.valueOf(req.getParameter("use-namespaces"));
        try {
            URL url = new URL(sourceURL);
    		XMLSerializer xmlSerializer = new XMLSerializer();
    		xmlSerializer.setSkipNamespaces(!useNamespaces.booleanValue());
    		JSON json = xmlSerializer.readFromStream(url.openStream());
    		if (prettyJSON) {
    			resp.getWriter().print(json.toString(2));
    		} else {
    			resp.getWriter().print(json.toString());
    		}
        } catch (MalformedURLException e) {
        	throw e;
        } catch (IOException e) {
        	throw e;
        }
	}
}
