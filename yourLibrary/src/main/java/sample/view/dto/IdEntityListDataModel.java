package sample.view.dto;

import java.util.List;
import java.util.Optional;

import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;

import sample.entity.IdEntity;

public class IdEntityListDataModel<T extends IdEntity> extends ListDataModel<T> implements SelectableDataModel<T> {

	//データ保存
	public IdEntityListDataModel(List<T> data) {
		super(data);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getRowData(String key) {
		List<T> entities = (List<T>) getWrappedData();
		long id = Long.parseLong(key);
		Optional<T> entity = entities.stream().filter(e -> e.getId() == id).findFirst();
		return entity.orElse(null);
		//		for (T entity : entities) {
		//			if (entity.getId() == id) {
		//				return entity;
		//			}
		//		}
		//		return null;
	}

	@Override
	public Object getRowKey(T entity) {
		return entity.getId();
	}
}
