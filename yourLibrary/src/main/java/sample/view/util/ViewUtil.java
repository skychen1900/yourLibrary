package sample.view.util;

import javax.el.ELContext;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;

public class ViewUtil {

	public static void AddMessage(String summary, String detail) {
		AddMessageInner(FacesMessage.SEVERITY_INFO, summary, detail);
	}

	public static void AddWarningMessage(String summary, String detail) {
		AddMessageInner(FacesMessage.SEVERITY_WARN, summary, detail);
	}

	public static void AddErrorMessage(String summary, String detail) {
		AddMessageInner(FacesMessage.SEVERITY_ERROR, summary, detail);
	}

	public static void AddFatalMessage(String summary, String detail) {
		AddMessageInner(FacesMessage.SEVERITY_FATAL, summary, detail);
	}

	private static void AddMessageInner(Severity severity, String summary, String detail) {
		FacesMessage message = new FacesMessage(severity, summary, detail);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public static SessionInfo getSessionInfo() {
		ELContext elContext = FacesContext.getCurrentInstance().getELContext();
		SessionInfo sessionInfo = (SessionInfo) FacesContext.getCurrentInstance()
				.getApplication().getELResolver()
				.getValue(elContext, null, "sessionInfo");
		return sessionInfo;
	}

	public static void putToFlash(String key, Object obj) {
		Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
		flash.put(key, obj);
	}

	public static Object getFromFlash(String key) {
		Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
		return flash.get(key);
	}

}
