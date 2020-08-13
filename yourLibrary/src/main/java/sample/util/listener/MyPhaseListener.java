package sample.util.listener;

import java.util.Map;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sample.util.JsonObjectDump;

public class MyPhaseListener implements PhaseListener {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(MyPhaseListener.class);

    //前処理
    @Override
    public void afterPhase(PhaseEvent event) {
        logger.debug("[AFTER]" + event.getPhaseId());
    }

    //後処理
    @Override
    public void beforePhase(PhaseEvent event) {
        logger.debug("[BEFORE]" + event.getPhaseId());
        if (event.getPhaseId().equals(PhaseId.RESTORE_VIEW)) {
            Map<String, String> parameterMap = event.getFacesContext().getExternalContext()
                    .getRequestParameterMap();
            if (null == parameterMap) {
                logger.debug("null");
            } else {
                for (Map.Entry<String, String> entry : parameterMap.entrySet()) {
                    logger.debug("   <PARAMETER>" + "Key = " + entry.getKey() + ", Value = " + entry.getValue());
                    if (JsonObjectDump.getJsonString(entry.getValue()) != null) {
                        logger.debug(JsonObjectDump.getJsonString(entry.getValue()));
                    }
                }
            }
        }
    }

    //対象のフェイズIDを返す。ここでは全てのフェイズを指定。
    @Override
    public PhaseId getPhaseId() {
        return PhaseId.ANY_PHASE;
    }
}
