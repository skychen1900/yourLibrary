package sample.util;

import java.util.Set;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;

public class Cdi {

	//引数のnameをもとに、ビーンを取得する
	public Object bean(String name) {
		CDI<Object> cdi = CDI.current();
		BeanManager beanManager = cdi.getBeanManager();
		Set<Bean<?>> beans = beanManager.getBeans(name);

		//セッションスコープとかだとユーザー数だけ同じ名前のビーンがあるため、ビーンを1つに特定
		Bean<?> bean = beanManager.resolve(beans);
		Class<?> beanClass = bean.getBeanClass();

		//CDI管理ビーンそのもののインスタンスを取得
		return cdi.select(beanClass).get();
	}
}
