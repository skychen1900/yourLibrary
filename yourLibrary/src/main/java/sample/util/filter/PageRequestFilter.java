package sample.util.filter;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import sample.entity.User;
import sample.view.util.SessionInfo;

@WebFilter("/*")
public class PageRequestFilter implements Filter {

	private static final Logger logger = LoggerFactory.getLogger(PageRequestFilter.class);

	@Inject
	SessionInfo sessionInfo;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO 自動生成されたメソッド・スタブ
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		String encording = "UTF8";

		// 遷移元の文字コード指定
		request.setCharacterEncoding(encording);

		HttpServletRequest req = (HttpServletRequest) request;
		HttpSession session = req.getSession();

		String contextPath = req.getContextPath();
		String uri = req.getRequestURI();
		String method = req.getMethod();

		String uriRegex = "/\\w*.xhtml";

		if (uri.matches(contextPath + uriRegex)) {
			logger.info("アクセス： [" + method + "] " + uri);
		}

		//SLF4j出力用のユーザー・セッション取得
		User user = sessionInfo.getLoginUser();
		String userId = "";
		if (user != null) {
			userId = user.getAccount();
		}
		if (!"".equals(userId)) {
			MDC.put("userId", userId);
		} else {
			MDC.put("userId", "未設定");
		}
		if (session != null) {
			MDC.put("sessionId", session.getId());
		}

		if (uri.equals(contextPath) || uri.equals(contextPath + "/index.xhtml")) {
			// ログイン処理時はなにもしない

		} else if (session == null) {
			//TODO ユーザー認可
			// セッションが切れたらログイン画面に戻る
			//			request.setAttribute("msg", "セッションが切れました");
			//			RequestDispatcher rd = request.getRequestDispatcher("/index.xhtml");
			//			rd.forward(request, response);
			//			return;

			// ログイン情報が存在しない場合、ログイン画面に強制遷移する。
			//			String contextPath = ((HttpServletRequest) request).getContextPath();
			((HttpServletResponse) response).sendRedirect(contextPath + "/view/login/login.xhtml");
		}

		// 時間計測開始
		long start = System.currentTimeMillis();

		// サーブレットの実行
		chain.doFilter(request, response);

		// 時間計測終了
		long end = System.currentTimeMillis();

		if (uri.matches(contextPath + uriRegex)) {
			logger.info("アクセス：" + uri + " 処理時間：" + (end - start) + "ms");
		}

		MDC.remove("loginId");
		MDC.remove("sessionId");

		// 遷移先の文字コード指定
		response.setCharacterEncoding(encording);

	}

	@Override
	public void destroy() {
		// TODO 自動生成されたメソッド・スタブ

	}

}
