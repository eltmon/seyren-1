/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.seyren.core.service.checker;

import com.seyren.core.domain.Alert;
import com.seyren.core.domain.AlertType;
import com.seyren.core.domain.Check;
import com.seyren.core.domain.CheckState;
import org.joda.time.DateTime;

import javax.inject.Named;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Named
public class CumulativeSumValueChecker implements ValueChecker {
    Map<String,CheckState> checkStateMap = new HashMap<String, CheckState>();
    private static final double DELTA = 0.005;
    private static final long defaultMinCount = 100;

    @Override
    public Alert checkValue(BigDecimal value, Check check, String target, AlertType lastState) {

        CheckState checkState = checkStateMap.get(target);
        AlertType alertType =  AlertType.OK;
        double doubleValue = value.doubleValue();
        double lambda = 3*checkState.getMean();
        long count = checkState.getCount()+1;
        double mean = checkState.getMean() + ((doubleValue - checkState.getMean()) / count);
        double posSum = Math.max(0, checkState.getPosSum() + doubleValue - checkState.getMean() - DELTA);
        double negSum = Math.min(0, checkState.getNegSum() + doubleValue - checkState.getMean() - DELTA);

        if (count > checkState.getMinCount()) {

            if (posSum > lambda || negSum < (-1d) * lambda) {
                count = 0;
                mean = 0d;
                posSum = 0d;
                negSum = 0d;
                checkStateMap.replace(target, new CheckState(defaultMinCount, 0d, 0d, 0d, 0));
                alertType = AlertType.ERROR;
            }
        }
        checkStateMap.replace(target,new CheckState(defaultMinCount,mean,posSum,negSum,count));

        return createAlert(target, value, check.getWarn(), check.getError(), lastState, alertType, DateTime.now());
    }
    private Alert createAlert(String target, BigDecimal value, BigDecimal warn, BigDecimal error, AlertType from, AlertType to, DateTime now) {
        return new Alert()
                .withTarget(target)
                .withValue(value)
                .withWarn(warn)
                .withError(error)
                .withFromType(from)
                .withToType(to)
                .withTimestamp(now);
    }
}
