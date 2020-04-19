package sample.customvalidate;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * @author chenhongming
 * https://qiita.com/shotana/items/42949b88c6c670b1ed22
 */
@Documented
@Constraint(validatedBy = { MJValidator.class })
@Target({ ElementType.PARAMETER, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface MJ {
	//制約違反時のメッセージ
	String message() default "you must specify one of Michael Jackson's songs.";

	//状況に応じて制約チェックの実行の是非を判別させるための属性
	Class<?>[] groups() default {};

	//制約違反に対して重要度などの任意のカテゴリを付与する属性
	Class<? extends Payload>[] payload() default {};

	//ネストしたアノテーションで、同じ制約を異なる条件で複数定義する場合に用いる
	@Target({ ElementType.FIELD })
	@Retention(RetentionPolicy.RUNTIME)
	@Documented
	@interface List {
		MJ[] value();
	}
}
