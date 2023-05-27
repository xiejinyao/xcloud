package com.xjinyao.xcloud.report.controller;

import com.xjinyao.report.action.RequestHolder;
import com.xjinyao.report.action.ServletAction;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 谢进伟
 * @createDate 2023/2/23 11:07
 */
@Slf4j
@RestController
@Api(tags = "报表设计")
@RequiredArgsConstructor
@RequestMapping(XReportController.URL)
public class XReportController {

	public static final String URL = "/xreport";
	private final ApplicationContext applicationContext;
	private ConcurrentHashMap<String, ServletAction> actionMap = new ConcurrentHashMap<>();

	@PostConstruct
	public void init() {
		Collection<ServletAction> handlers = applicationContext.getBeansOfType(ServletAction.class).values();
		for (ServletAction handler : handlers) {
			String url = handler.url();
			if (actionMap.containsKey(url)) {
				throw new RuntimeException("Handler [" + url + "] already exist.");
			}
			actionMap.put(url, handler);
		}
	}

	@GetMapping("/**")
	public void get(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		service(req, resp);
	}

	@PostMapping("/**")
	public void post(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		service(req, resp);
	}

	private void service(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		String path = req.getContextPath() + URL;
		String uri = req.getRequestURI();
		log.info("request uri {}", req.getRequestURI());
		req.getParameterMap().forEach((k, vs) -> {
			log.info("{}={}", k, Arrays.deepToString(vs));
		});
		String targetUrl = uri.substring(path.length());
		if (targetUrl.length() < 1) {
			outContent(resp, "Welcome to use ureport,please specify target url.");
			return;
		}
		int slashPos = targetUrl.indexOf("/", 1);
		if (slashPos > -1) {
			targetUrl = targetUrl.substring(0, slashPos);
		}
		ServletAction targetHandler = actionMap.get(targetUrl);
		if (targetHandler == null) {
			outContent(resp, "Handler [" + targetUrl + "] not exist.");
			return;
		}
		RequestHolder.setRequest(req);
		try {
			targetHandler.execute(req, resp);
		} catch (Exception ex) {
			resp.setCharacterEncoding("UTF-8");
			PrintWriter pw = resp.getWriter();
			Throwable e = buildRootException(ex);
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			String errorMsg = e.getMessage();
			if (StringUtils.isBlank(errorMsg)) {
				errorMsg = e.getClass().getName();
			}
			pw.write(errorMsg);
			pw.close();
			throw new ServletException(ex);
		} finally {
			RequestHolder.clean();
		}
	}

	private Throwable buildRootException(Throwable throwable) {
		if (throwable.getCause() == null) {
			return throwable;
		}
		return buildRootException(throwable.getCause());
	}

	private void outContent(HttpServletResponse resp, String msg) throws IOException {
		resp.setContentType("text/html");
		PrintWriter pw = resp.getWriter();
		pw.write("<html>");
		pw.write("<header><title>UReport Console</title></header>");
		pw.write("<body>");
		pw.write(msg);
		pw.write("</body>");
		pw.write("</html>");
		pw.flush();
		pw.close();
	}
}
