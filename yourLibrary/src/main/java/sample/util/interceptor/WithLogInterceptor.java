package sample.util.interceptor;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;
import javax.enterprise.context.Dependent;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sample.util.IgnoreLogging;
import sample.util.JsonObjectDump;

@Interceptor // インターセプターの宣言
@Dependent
@WithLog // バインド用アノテーション
//@Priority(Interceptor.Priority.APPLICATION) // 優先度
public class WithLogInterceptor implements Serializable {

    private static final long serialVersionUID = -3160665196718618893L;

    // プロデューサー経由でロガー取得
    //	@Inject
    //	private Logger logger;
    private static final Logger logger = LoggerFactory.getLogger(WithLogInterceptor.class);

    private static final List<String> ignoreObjects = Arrays.asList("org.primefaces.event.SelectEvent",
            "javax.faces.event.ActionEvent");

    // アプリケーション名の取得
    @Resource(lookup = "java:app/AppName")
    String appName;

    /**
     * インターセプターのメソッド
     * @param ic 実行コンテキスト - 本来実行される処理。
     * @return 本来実行される処理の戻り値
     * @throws Exception 何らかの例外
     */
    @AroundInvoke
    public Object invoke(InvocationContext ic) throws Exception {

        // ターゲットは、CDIのクライアントプロキシなので、スーパークラスを取得。
        String classAndMethod = ic.getTarget().getClass()
                .getSuperclass().getName()
                + "#" + ic.getMethod().getName();

        Method method = ic.getMethod();
        Class<?>[] parameterTypes = method.getParameterTypes();
        Annotation[] annotations = method.getAnnotations();
        Object[] parameter = ic.getParameters();

        // ログ出力要否フラグ
        boolean flg = true;

        for (Annotation element : annotations) {
            if (element instanceof IgnoreLogging) {
                flg = false;
            }
        }

        if (flg) {

            // メソッド開始前のログ
            //      logger.info(() -> appName + ":" + classAndMethod + " start.");
            logger.debug(classAndMethod + " start>>");

            int i = 0;
            for (Class<?> parameterType : parameterTypes) {
                logger.debug("\t" + "[PARAMETER] " + parameterType.getName() + " ,[VALUE] " + parameter[i++]);
                if (!ignoreObjects.contains(parameterType.getName())
                        && JsonObjectDump.getJsonString(parameter) != null) {
                    logger.debug(JsonObjectDump.getJsonString(parameter));
                }
            }

        }

        // パラメータ出力
        //        for (Object parameter : ic.getParameters()) {
        //            logger.debug("\t" + "[PARAMETER] " + parameter);
        //            if (JsonObjectDump.getJsonString(parameter) != null) {
        //                logger.debug(JsonObjectDump.getJsonString(parameter));
        //            }
        //        }

        Object ret = null;
        try {
            // メソッドの実行   ＜次のインターセプターチェーンの実行＞
            // System.out.println("call by interceptor");
            ret = ic.proceed();
        } catch (Exception e) {
            // 例外のログを出したら、例外はそのまま再スローする。
            // トランザクションインターセプターの内部で処理されるので、
            // ここでは根本例外が出る。
            logger.error(appName, e);
            throw e;
        }

        if (flg) {

            // メソッド終了後のログ
            //      logger.info(() -> appName + ":" + classAndMethod + " end.");
            logger.debug(classAndMethod + " end<<");
        }

        return ret;
    }
}