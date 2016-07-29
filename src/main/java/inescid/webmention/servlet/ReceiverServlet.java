package inescid.webmention.servlet;

import java.io.IOException;
import java.nio.charset.Charset;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import inescid.webmention.UrlValidator;
import inescid.webmention.Webmention;
import inescid.webmention.WebmentionStatus;
import inescid.webmention.repository.WebmentionRepository;

public class ReceiverServlet extends HttpServlet {
	private static final Charset UTF8 = Charset.forName("UTF8");
	private static final UrlValidator validator=new UrlValidator();
	private WebmentionRepository repository;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		String repositoryFolder = config.getServletContext().getInitParameter("webmention.repository.folder");
		if (repositoryFolder.equals("${webmention.repository.folder}"))
			repositoryFolder="target";
		System.out.println("Init: "+ repositoryFolder);
		try {
			repository=new WebmentionRepository(repositoryFolder);
		} catch (IOException e) {
			throw new ServletException(e);
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			String source = req.getParameter("source");
			String target = req.getParameter("target");
			if(source==null) {
				sendResponse(resp, 400, "Parameter 'source' not found.");
				return;
			}
			if(target==null) {
				sendResponse(resp, 400, "Parameter 'target' not found.");
				return;
			}
			if(!validator.isValidSource(source)) {
				sendResponse(resp, 400, "'source' URL was malformed or is not a supported URL scheme");
				return;
			}
			if(!validator.isValidTarget(target)) {
				sendResponse(resp, 400, "Specified 'target' URL not found.");
				return;
			}
			
			Webmention mention = new Webmention(source, target);
			WebmentionStatus status = repository.getStatus(mention);	
			if(status==null) {
				repository.storeRequest(mention);
				sendResponse(resp, 202, "The Webmention is being processed");	
			} else switch(status) {
			case DELETED:
				repository.storeRequest(mention);
				sendResponse(resp, 410, "The Webmention is no longer valid");	
				break;
			case PROCESSED:
				sendResponse(resp, 200, "The Webmention is created");	
				break;
			case RECEIVED:
				sendResponse(resp, 202, "The Webmention is being processed");	
				break;
			case REJECTED:
				repository.storeRequest(mention);
				sendResponse(resp, 202, "The Webmention is being processed");	
				break;
			case VERIFICATION:
				repository.storeRequest(mention);
				sendResponse(resp, 202, "The Webmention is being processed");	
				break;
			}		
//			throw new RuntimeException("This part of the code should not ever be reached...");
		} catch (Exception e) {
			e.printStackTrace();
			sendResponse(resp, 500, "Webmention receiver internal error: "+e.getMessage());
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String source = req.getParameter("source");
		String target = req.getParameter("target");
		if(source==null && target==null)
			sendResponse(resp, 200, "Webmention endpoint is available");
		else
			sendResponse(resp, 405, null);
	}
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		sendResponse(resp, 405, null);
	}
	@Override
	protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String source = req.getParameter("source");
		String target = req.getParameter("target");
		if(source==null && target==null)
			sendResponse(resp, 200, null);
		else
			sendResponse(resp, 405, null);
	}
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		sendResponse(resp, 405, null);
	}
	protected void sendResponse(HttpServletResponse resp, int httpStatus, String body) throws IOException {
		resp.setStatus(httpStatus);
		if(body!=null && !body.isEmpty()) {
			ServletOutputStream outputStream = resp.getOutputStream();
			outputStream.write(body.getBytes(UTF8));
			resp.setContentType("text/plain; charset=utf-8");
		}
	}
}
