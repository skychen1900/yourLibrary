package sample.test;

import java.io.Serializable;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.faces.event.ValueChangeEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class NormalBean implements Serializable, ActionListener {

	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	private String buttonId;

	@Getter
	@Setter
	private ScreenDto screenDto;

	//	@PostConstruct
	public void init() {
		screenDto = new ScreenDto();

		Locale[] countries = { Locale.US, Locale.CHINA, Locale.JAPAN };
		screenDto.setCountries(countries);
	}

	public void clear() {
		screenDto.setInput("");
		screenDto.setOutput("");
	}

	private void edit(String str) {
		System.out.println(str);
		if (null == screenDto.getOutput() || "".equals(screenDto.getOutput())) {
			screenDto.setOutput(screenDto.getInput() + "+" + str);
		} else {
			screenDto.setOutput(screenDto.getInput() + screenDto.getOutput() + "+" + str);
		}

	}

	public void actionListener1(ActionEvent event) {
		//TODO actionEventって、何をできる？
		edit("actionListener1");

	}

	public void actionListener2() {
		edit("actionListener2");

	}

	public String outcome() {
		edit("outcome");
		return null;
	}

	public void countryChanged(ValueChangeEvent event) {

		for (Locale loc : screenDto.getCountries()) {
			if (loc.getCountry().equals(event.getNewValue())) {
				FacesContext.getCurrentInstance().getViewRoot().setLocale(loc);
			}
		}

		System.out.println(screenDto.getCountry());
		screenDto.setOutputCountry(screenDto.getCountry());
	}

	@Override
	public void processAction(ActionEvent event) throws AbortProcessingException {

	}
}
