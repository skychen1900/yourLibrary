package sample.util;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class StringNullConverter implements AttributeConverter<String, String> {

	@Override
	public String convertToDatabaseColumn(String attribute) {
		if (null == attribute) {
			return "";
		} else {
			return attribute;
		}
	}

	@Override
	public String convertToEntityAttribute(String dbData) {
		if (null == dbData) {
			return "";
		} else {
			return dbData;
		}
	}

}
