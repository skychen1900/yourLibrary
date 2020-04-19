package sample.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Stereotype;
import javax.transaction.Transactional;

import sample.util.interceptor.WithLog;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Stereotype // ステレオタイプ宣言
// 以下が各クラスに付与するCDI関係のアノテーション
@Transactional
@ApplicationScoped
@WithLog
public @interface Service {
}
