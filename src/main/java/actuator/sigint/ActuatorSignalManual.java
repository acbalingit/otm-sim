/**
 * Copyright (c) 2018, Gabriel Gomes
 * All rights reserved.
 * This source code is licensed under the standard 3-clause BSD license found
 * in the LICENSE file in the root directory of this source tree.
 */
package actuator.sigint;

import actuator.AbstractActuator;
import control.sigint.ScheduleItem;
import dispatch.Dispatcher;
import error.OTMErrorLog;
import error.OTMException;
import runner.Scenario;

import java.util.HashMap;
import java.util.Map;

public class ActuatorSignalManual extends AbstractActuator {

    public Map<Long, SignalPhaseManual> signal_phases;
    public ScheduleItem current_schedule_item;

    ///////////////////////////////////////////////////
    // construction
    ///////////////////////////////////////////////////

    public ActuatorSignalManual(Scenario scenario, jaxb.Actuator jaxb_actuator) throws OTMException {
        super(scenario,jaxb_actuator);

        // must be on a node
        if(target==null || !(target instanceof common.Node))
            return;

        if(jaxb_actuator.getSignal()==null)
            return;

        signal_phases = new HashMap<>();
        for(jaxb.Phase jaxb_phase : jaxb_actuator.getSignal().getPhase())
            signal_phases.put(jaxb_phase.getId(), new SignalPhaseManual(scenario, this, jaxb_phase));

    }

    @Override
    public void validate(OTMErrorLog errorLog) {
        super.validate(errorLog);

        if(signal_phases==null)
            errorLog.addError("ActuatorSignalManual ID=" + id + " contains no valid phases.");
        else
            for(SignalPhaseManual p : signal_phases.values())
                p.validate(errorLog);
    }

    @Override
    public void initialize(Scenario scenario) throws OTMException {
        float now = scenario.get_current_time();
        // set all bulb colors to dark
        for(SignalPhaseManual p : signal_phases.values() )
            p.initialize(now);
    }

    public void turn_off(float now) throws OTMException {
        for(SignalPhaseManual p : signal_phases.values() )
            p.turn_off(now);
    }

    public SignalPhaseManual get_phase(long phase_id){
        return signal_phases.get(phase_id);
    }

    public void enable_phase(long phase_id, float time) throws OTMException {
        if (!signal_phases.keySet().contains(phase_id)){
            throw new OTMException(
                String.format("phase ID %s is not in actuator %s", phase_id, id));
        } else {
            signal_phases.values().forEach(p -> p.disable(time));
            signal_phases.values().stream().filter(p -> p.id == phase_id).forEach(p -> p.enable(time));
        }        
    }

    ///////////////////////////////////////////////////
    // control
    ///////////////////////////////////////////////////

    @Override
    public void process_controller_command(Object command, Dispatcher dispatcher, float timestamp) throws OTMException {

        if (command != null){
        } else {
            // null command triggers turning off the signal,
            // i.e. allow traffic to pass for all phases.
            turn_off(timestamp);
        }

     }

}
